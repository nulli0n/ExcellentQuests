package su.nightexpress.quests.quest.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.locale.entry.IconLocale;
import su.nightexpress.nightcore.ui.menu.MenuViewer;
import su.nightexpress.nightcore.ui.menu.data.ConfigBased;
import su.nightexpress.nightcore.ui.menu.data.MenuLoader;
import su.nightexpress.nightcore.ui.menu.item.MenuItem;
import su.nightexpress.nightcore.ui.menu.type.NormalMenu;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.time.TimeFormatType;
import su.nightexpress.nightcore.util.time.TimeFormats;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.battlepass.BattlePassManager;
import su.nightexpress.quests.config.Config;
import su.nightexpress.quests.config.Lang;
import su.nightexpress.quests.quest.QuestManager;
import su.nightexpress.quests.quest.data.QuestData;
import su.nightexpress.quests.quest.definition.Quest;
import su.nightexpress.quests.quest.definition.QuestXPReward;
import su.nightexpress.quests.reward.Reward;
import su.nightexpress.quests.user.QuestUser;
import su.nightexpress.quests.util.MenuUtils;

import java.util.*;
import java.util.stream.IntStream;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.quests.QuestsPlaceholders.*;

public class QuestsMenu extends NormalMenu<QuestsPlugin> implements ConfigBased {

    private final QuestManager manager;
    private final TreeMap<Integer, int[]> slotsByQuestCount;

    public QuestsMenu(@NotNull QuestsPlugin plugin, @NotNull QuestManager manager) {
        super(plugin, MenuType.GENERIC_9X5, BLACK.wrap("Daily Quests"));
        this.manager = manager;
        this.slotsByQuestCount = new TreeMap<>();
        this.setAutoRefreshInterval(1);
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull InventoryView view) {
        Player player = viewer.getPlayer();
        QuestUser user = this.plugin.getUserManager().getOrFetch(player);
        List<QuestData> questDatas = user.getQuestDatas().stream().sorted(Comparator.comparing(QuestData::getQuestId)).toList();

        int questCount = questDatas.size();
        int[] questSlots = Optional.ofNullable(this.slotsByQuestCount.ceilingEntry(questCount)).map(Map.Entry::getValue).orElse(new int[0]);

        for (int index = 0; index < questSlots.length; index++) {
            int slot = questSlots[index];
            QuestData questData = questDatas.get(index);

            Quest quest = this.manager.getQuestById(questData.getQuestId());
            if (quest == null) {
                this.plugin.error("Invalid quest data: '" + questData.getId() + "'!");
                continue;
            }

            double progress = questData.getProgressValue();

            IconLocale locale;
            if (questData.isCompleted()) {
                locale = Lang.UI_QUEST_COMPLETED;
            }
            else if (questData.isExpired()) {
                locale = Lang.UI_QUEST_FAILED;
            }
            else if (questData.isActive()) {
                locale = Lang.UI_QUEST_IN_PROGRESS;
            }
            else locale = Lang.UI_QUEST_AVAILABLE;

            List<Reward> rewards = this.plugin.getRewardManager().getQuestRewards(quest);
            QuestXPReward xpReward = quest.getBattlePassXPReward();

            double scale = questData.getScale();
            int unitsWorth = questData.countUnitsWorth();
            boolean hasBattleSeason = this.plugin.battlePassManager().map(BattlePassManager::isSeasonActive).orElse(false);
            boolean autoCompletionTime = Config.QUESTS_AUTO_COMPLETION_TIME.get();

            viewer.addItem(quest.getIcon()
                .hideAllComponents()
                .localized(locale)
                .replacement(replacer -> replacer
                    .replace(GENERIC_TIMELEFT, () -> TimeFormats.formatDuration(questData.getExpireDate(), TimeFormatType.LITERAL))
                    .replace(GENERIC_PROGRESS_BAR, () -> MenuUtils.buildProgressBar(progress))
                    .replace(GENERIC_PROGRESS, () -> NumberUtil.format(progress * 100D))
                    .replace(GENERIC_OBJECTIVES, MenuUtils.formatObjectives(quest, questData))
                    .replace(GENERIC_REWARDS, MenuUtils.formatRewards(rewards, unitsWorth, 0, scale))
                    .replace(GENERIC_BATTLE_PASS_REWARDS, hasBattleSeason ? MenuUtils.formatBattlePassRewards(xpReward, unitsWorth) : Collections.emptyList())
                    .replace(GENERIC_XP, NumberUtil.format(questData.getXPReward()))
                    .replace(quest.replacePlaceholders())
                )
                .toMenuItem()
                .setPriority(Integer.MAX_VALUE)
                .setSlots(slot)
                .setHandler((viewer1, event) -> {
                    if (questData.isActive()) return;
                    if (questData.isExpired()) return;

                    questData.setActive(true);
                    questData.setExpireDate(autoCompletionTime ? user.getNewQuestsDate() : TimeUtil.createFutureTimestamp(quest.getCompletionTime()));

                    this.runNextTick(() -> this.flush(viewer));
                    Lang.QUESTS_QUEST_ACCEPTED.message().send(player, replacer -> replacer.replace(quest.replacePlaceholders()));
                })
                .build());
        }
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Override
    protected void onItemPrepare(@NotNull MenuViewer viewer, @NotNull MenuItem menuItem, @NotNull NightItem item) {
        super.onItemPrepare(viewer, menuItem, item);

        if (viewer.hasItem(menuItem)) return;

        Player player = viewer.getPlayer();
        QuestUser user = this.plugin.getUserManager().getOrFetch(player);

        item.replacement(replacer -> replacer.replace(GENERIC_REFRESH_TIME, () -> TimeFormats.formatDuration(user.getNewQuestsDate(), TimeFormatType.LITERAL)));
    }

    @Override
    public void loadConfiguration(@NotNull FileConfig config, @NotNull MenuLoader loader) {
        for (int count = 0; count < 10; count++) {
            int amount = count + 1;
            int[] defSlots = getDefaultSlots(amount);
            int[] skillSlots = ConfigValue.create("Quest.SlotsByCount." + amount, defSlots).read(config);

            this.slotsByQuestCount.put(amount, skillSlots);
        }

        loader.addDefaultItem(NightItem.fromType(Material.CLOCK)
            .setDisplayName(YELLOW.and(BOLD).wrap("Quest Timer"))
            .setLore(Lists.newList(
                GRAY.wrap("New quests in: " + WHITE.wrap(GENERIC_REFRESH_TIME))
            ))
            .hideAllComponents()
            .toMenuItem()
            .setPriority(10)
            .setSlots(4)
        );

        loader.addDefaultItem(NightItem.fromType(Material.BLACK_STAINED_GLASS_PANE)
            .setHideTooltip(true)
            .toMenuItem()
            .setPriority(-1)
            .setSlots(0,1,2,3,4,5,6,7,8,36,37,38,39,40,41,42,43,44)
        );

        loader.addDefaultItem(NightItem.fromType(Material.GRAY_STAINED_GLASS_PANE)
            .setHideTooltip(true)
            .toMenuItem()
            .setPriority(-1)
            .setSlots(IntStream.range(9, 36).toArray())
        );
    }

    private static int[] getDefaultSlots(int count) {
        return switch (count) {
            case 1 -> new int[]{22};
            case 2 -> new int[]{21, 23};
            case 3 -> new int[]{21, 22, 23};
            case 4 -> new int[]{21, 22, 24, 25};
            case 5 -> new int[]{20, 21, 22, 23, 24};
            case 6 -> new int[]{20, 21, 22, 23, 24, 31};
            case 7 -> new int[]{20, 21, 22, 23, 24, 30, 32};
            case 8 -> new int[]{20, 21, 22, 23, 24, 30, 31, 32};
            case 9 -> new int[]{20, 21, 22, 23, 24, 29, 30, 32, 33};
            case 10 -> new int[]{20, 21, 22, 23, 24, 29, 30, 31, 32, 33};
            default -> new int[]{};
        };
    }
}
