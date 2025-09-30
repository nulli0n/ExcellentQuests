package su.nightexpress.quests.battlepass.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.ui.menu.MenuViewer;
import su.nightexpress.nightcore.ui.menu.data.ConfigBased;
import su.nightexpress.nightcore.ui.menu.data.Filled;
import su.nightexpress.nightcore.ui.menu.data.MenuFiller;
import su.nightexpress.nightcore.ui.menu.data.MenuLoader;
import su.nightexpress.nightcore.ui.menu.item.ItemHandler;
import su.nightexpress.nightcore.ui.menu.item.ItemOptions;
import su.nightexpress.nightcore.ui.menu.item.MenuItem;
import su.nightexpress.nightcore.ui.menu.type.LinkedMenu;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.placeholder.Replacer;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.battlepass.BattlePassManager;
import su.nightexpress.quests.battlepass.BattlePassUtils;
import su.nightexpress.quests.battlepass.data.BattlePassData;
import su.nightexpress.quests.battlepass.definition.BattlePassType;
import su.nightexpress.quests.battlepass.definition.BattlePassSeason;
import su.nightexpress.quests.config.Config;
import su.nightexpress.quests.config.Lang;
import su.nightexpress.quests.reward.Reward;
import su.nightexpress.quests.user.QuestUser;
import su.nightexpress.quests.util.MenuUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.quests.QuestsPlaceholders.*;

public class BattlePassMenu extends LinkedMenu<QuestsPlugin, BattlePassSeason> implements Filled<Integer>, ConfigBased {

    private final BattlePassManager manager;

    private NightItem lockedLevel;
    private NightItem completedLevel;
    private NightItem progressLevel;

    private NightItem lockedReward;
    private NightItem unlockedReward;
    private NightItem claimedReward;
    private NightItem premiumReward;

    private NightItem activeTimer;
    private NightItem scheduledTimer;
    private NightItem endedTimer;

    private int[] levelSlots;
    private int[] defaultSlots;
    private int[] premiumSlots;
    private int timerSlot;

    public BattlePassMenu(@NotNull QuestsPlugin plugin, @NotNull BattlePassManager manager) {
        super(plugin, MenuType.GENERIC_9X5, BLACK.wrap("Season " + SEASON_NAME + " • " + BATTLE_PASS_TYPE));
        this.manager = manager;
        this.setAutoRefreshInterval(1);
    }

    public void openAtLevel(@NotNull Player player, @NotNull BattlePassSeason season) {
        BattlePassData data = this.plugin.getUserManager().getOrFetch(player).getBattlePassData(season);
        int level = data.getLevel();

        int limit = this.levelSlots.length;
        int page = (int) Math.ceil((double) level / (double) limit);

        this.open(player, season, viewer -> viewer.setPage(page));
    }

    @Override
    @NotNull
    protected String getTitle(@NotNull MenuViewer viewer) {
        Player player = viewer.getPlayer();
        QuestUser user = plugin.getUserManager().getOrFetch(player);
        BattlePassSeason season = this.getLink(player);
        BattlePassData data = user.getBattlePassData(season);

        return Replacer.create()
            .replace(season.replacePlaceholders())
            .replace(data.replacePlaceholders())
            .replace(BATTLE_PASS_TYPE, Lang.BATTLE_PASS_MODE.getLocalized(BattlePassUtils.getPassType(player, user, season)))
            .apply(super.getTitle(viewer));
    }

    @Override
    protected void onItemPrepare(@NotNull MenuViewer viewer, @NotNull MenuItem menuItem, @NotNull NightItem item) {
        super.onItemPrepare(viewer, menuItem, item);

        Player player = viewer.getPlayer();
        QuestUser user = plugin.getUserManager().getOrFetch(player);
        BattlePassSeason season = this.getLink(player);
        BattlePassData data = user.getBattlePassData(season);

        item.replacement(replacer -> replacer
            .replace(data.replacePlaceholders())
            .replace(season.replacePlaceholders())
            .replace(BATTLE_PASS_TYPE, Lang.BATTLE_PASS_MODE.getLocalized(BattlePassUtils.getPassType(player, user, season)))
        );
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull InventoryView view) {
        this.autoFill(viewer);
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Override
    @NotNull
    public MenuFiller<Integer> createFiller(@NotNull MenuViewer viewer) {
        Player player = viewer.getPlayer();
        QuestUser user = plugin.getUserManager().getOrFetch(player);
        BattlePassSeason season = this.getLink(player);
        BattlePassData data = user.getBattlePassData(season);

        int passLevel = data.getLevel();
        boolean isPremium = BattlePassUtils.hasPremium(player, user, season);

        AtomicInteger indexer = new AtomicInteger(0);

        NightItem timerItem;
        if (season.isRunning()) {
            timerItem = this.activeTimer;
        }
        else if (season.isScheduled()) {
            timerItem = this.scheduledTimer;
        }
        else {
            timerItem = this.endedTimer;
        }

        viewer.addItem(timerItem.copy()
            .toMenuItem()
            .setPriority(Integer.MAX_VALUE)
            .setSlots(this.timerSlot)
            .build()
        );

        return MenuFiller.builder(this)
            .setSlots(this.levelSlots)
            .setItems(IntStream.range(1, data.getMaxLevel() + 1).boxed().toList())
            .setItemCreator(level -> {
                NightItem levelItem;
                if (passLevel >= level) {
                    levelItem = this.completedLevel;
                }
                else if (passLevel == level - 1) {
                    levelItem = this.progressLevel;
                }
                else {
                    levelItem = this.lockedLevel;
                }

                int rewardIndex = indexer.get();
                for (BattlePassType type : BattlePassType.values()) {
                    List<Reward> rewards = this.manager.getLevelRewards(level, type);
                    if (rewards.isEmpty()) continue;

                    int[] slots = type == BattlePassType.FREE ? this.defaultSlots : this.premiumSlots;
                    if (rewardIndex >= slots.length) continue;

                    boolean claimed = data.isRewardClaimed(type, level);

                    NightItem rewardItem;
                    if (type == BattlePassType.PREMIUM && !isPremium) {
                        rewardItem = this.premiumReward;
                    }
                    else if (passLevel >= level) {
                        rewardItem = claimed ? this.claimedReward : this.unlockedReward;
                    }
                    else {
                        rewardItem = this.lockedReward;
                    }

                    int slot = slots[rewardIndex];

                    viewer.addItem(rewardItem
                        .copy()
                        .replacement(replacer -> replacer
                            .replace(GENERIC_LEVEL, String.valueOf(level))
                            .replace(GENERIC_REWARDS, MenuUtils.formatRewards(rewards, 0, level, 1D))
                        )
                        .toMenuItem()
                        .setSlots(slot)
                        .setPriority(Integer.MAX_VALUE)
                        .setHandler((viewer1, event) -> {
                            if (claimed || passLevel < level) return;

                            rewards.forEach(reward -> {
                                reward.runCommands(player, 0, level, 1D);
                                data.setRewardClaimed(type, level);
                            });
                            this.plugin.getUserManager().save(user);

                            this.runNextTick(() -> this.flush(viewer));
                        })
                        .build()
                    );
                }

                indexer.incrementAndGet();

                return levelItem
                    .copy()
                    .hideAllComponents()
                    .replacement(replacer -> replacer
                        .replace(GENERIC_LEVEL, NumberUtil.format(level))
                    );
            })
            .setItemClick(level -> (viewer1, event) -> {})
            .build();
    }

    private void handleQuests(@NotNull MenuViewer viewer, @NotNull InventoryClickEvent event) {
        this.runNextTick(() -> this.plugin.questManager().ifPresent(questManager -> questManager.openQuests(viewer.getPlayer())));
    }

    @Override
    public void loadConfiguration(@NotNull FileConfig config, @NotNull MenuLoader loader) {
        this.lockedLevel = ConfigValue.create("Level.Locked", NightItem.fromType(Material.RED_STAINED_GLASS_PANE)
            .setDisplayName(RED.wrap(BOLD.wrap("Level " + GENERIC_LEVEL)) + GRAY.wrap(" • ") + WHITE.wrap("Locked"))
            .setLore(Lists.newList())
        ).read(config);

        this.completedLevel = ConfigValue.create("Level.Claimed", NightItem.fromType(Material.LIME_STAINED_GLASS_PANE)
            .setDisplayName(GREEN.wrap(BOLD.wrap("Level " + GENERIC_LEVEL)) + GRAY.wrap(" • ") + WHITE.wrap("Completed"))
            .setLore(Lists.newList())
        ).read(config);

        this.progressLevel = ConfigValue.create("Level.Upcoming", NightItem.fromType(Material.YELLOW_STAINED_GLASS_PANE)
            .setDisplayName(YELLOW.wrap(BOLD.wrap("Level " + GENERIC_LEVEL)) + GRAY.wrap(" • ") + WHITE.wrap("In Progress"))
            .setLore(Lists.newList(
                "",
                DARK_GRAY.wrap("┃") + GRAY.wrap(" Level: " + WHITE.wrap(BATTLE_PASS_LEVEL) + "/" + GOLD.wrap(BATTLE_PASS_MAX_LEVEL)),
                DARK_GRAY.wrap("┃") + GRAY.wrap(" XP: " + WHITE.wrap(BATTLE_PASS_XP) + "/" + GOLD.wrap(BATTLE_PASS_XP_MAX))
            ))
        ).read(config);

        this.lockedReward = ConfigValue.create("Reward.Locked", NightItem.fromType(Material.STRUCTURE_VOID)
            .setDisplayName(DARK_AQUA.and(BOLD).wrap("Locked Reward"))
            .setLore(Lists.newList(
                GENERIC_REWARDS,
                "",
                GRAY.wrap(DARK_AQUA.wrap("➥") + " Complete the " + DARK_AQUA.wrap(GENERIC_LEVEL) + " level to unlock!")
            ))
        ).read(config);

        this.premiumReward = ConfigValue.create("Reward.Premium", NightItem.fromType(Material.BARRIER)
            .setDisplayName(RED.and(BOLD).wrap("Premium Reward"))
            .setLore(Lists.newList(
                GENERIC_REWARDS,
                "",
                RED.wrap("➥ Upgrade to " + UNDERLINED.wrap("Premium") + " to unlock!")
            ))
        ).read(config);

        this.unlockedReward = ConfigValue.create("Reward.Unlocked", NightItem.fromType(Material.CHEST_MINECART)
            .setDisplayName(GOLD.and(BOLD).wrap("Reward Available"))
            .setLore(Lists.newList(
                GENERIC_REWARDS,
                "",
                GOLD.wrap("→ " + UNDERLINED.wrap("Click to claim!"))
            ))
        ).read(config);

        this.claimedReward = ConfigValue.create("Reward.Claimed", NightItem.fromType(Material.MINECART)
            .setDisplayName(GOLD.and(BOLD).wrap("Claimed Reward"))
            .setLore(Lists.newList(
                GENERIC_REWARDS,
                "",
                GREEN.wrap("✔") + " " + DARK_GRAY.wrap("You have claimed this reward.")
            ))
        ).read(config);

        this.activeTimer = ConfigValue.create("Timer.Active", NightItem.fromType(Material.CLOCK)
            .setDisplayName(YELLOW.and(BOLD).wrap("Season: ") + WHITE.wrap(SEASON_NAME) + " • " + GREEN.wrap("Active"))
            .setLore(Lists.newList(
                GRAY.wrap("Season is running!"),
                "",
                YELLOW.wrap("➥ Launched: ") + " " + WHITE.wrap(SEASON_START_DATE),
                YELLOW.wrap("⏳ Timeleft:") + " " + WHITE.wrap(SEASON_TIME_LEFT)
            ))
        ).read(config);

        this.endedTimer = ConfigValue.create("Timer.Ended", NightItem.fromType(Material.CLOCK)
            .setDisplayName(YELLOW.and(BOLD).wrap("Season: ") + WHITE.wrap(SEASON_NAME) + " • " + RED.wrap("Ended"))
            .setLore(Lists.newList(
                GRAY.wrap("The season is over! Follow us on"),
                GRAY.wrap("discord for further event news!"),
                "",
                YELLOW.wrap("➥ Launched: ") + " " + WHITE.wrap(SEASON_START_DATE),
                YELLOW.wrap("⏳ Finished:") + " " + WHITE.wrap(SEASON_END_DATE)
            ))
        ).read(config);

        this.scheduledTimer = ConfigValue.create("Timer.Scheduled", NightItem.fromType(Material.CLOCK)
            .setDisplayName(YELLOW.and(BOLD).wrap("Season: ") + WHITE.wrap(SEASON_NAME) + " • " + YELLOW.wrap("Scheduled"))
            .setLore(Lists.newList(
                GRAY.wrap("The new season is coming!"),
                "",
                YELLOW.wrap("➥ Launch Date:") + " " + WHITE.wrap(SEASON_START_DATE)
            ))
        ).read(config);

        this.levelSlots = ConfigValue.create("Slots.Levels", new int[]{19,20,21,22,23,24,25}).read(config);
        this.defaultSlots = ConfigValue.create("Slots.DefaultRewards", new int[]{10,11,12,13,14,15,16}).read(config);
        this.premiumSlots = ConfigValue.create("Slots.PremiumRewards", new int[]{28,29,30,31,32,33,34}).read(config);
        this.timerSlot = ConfigValue.create("Timer.Slot", 43).read(config);

        loader.addDefaultItem(NightItem.fromType(Material.ANVIL)
            .setDisplayName(BLUE.wrap("Battle Pass"))
            .setLore(Lists.newList(
                GRAY.wrap("Complete " + BLUE.wrap("Daily Quests") + " to progress"),
                GRAY.wrap("in the " + BLUE.wrap("Battle Pass") + "!"),
                " ",
                DARK_GRAY.wrap("[Stats]"),
                DARK_GRAY.wrap("• " + GRAY.wrap("Type: ") + GOLD.wrap(BATTLE_PASS_TYPE)),
                DARK_GRAY.wrap("• " + GRAY.wrap("Level: ") + GOLD.wrap(BATTLE_PASS_LEVEL)),
                DARK_GRAY.wrap("• " + GRAY.wrap("Progress: ") + GOLD.wrap(BATTLE_PASS_XP) + "/" + GOLD.wrap(BATTLE_PASS_XP_MAX))
            ))
            .hideAllComponents()
            .toMenuItem()
            .setSlots(37)
            .setPriority(10)
        );

        loader.addDefaultItem(NightItem.fromType(Material.KNOWLEDGE_BOOK)
            .setDisplayName(GREEN.wrap("Daily Quests"))
            .setLore(Lists.newList(
                GREEN.wrap("→ " + UNDERLINED.wrap("Click to open!"))
             ))
            .toMenuItem()
            .setSlots(40)
            .setPriority(10)
            .setHandler(new ItemHandler("quests", this::handleQuests, ItemOptions.builder().setVisibilityPolicy(viewer -> Config.isQuestsEnabled()).build()))
        );

        loader.addDefaultItem(NightItem.fromType(Material.GRAY_STAINED_GLASS_PANE)
            .setHideTooltip(true)
            .toMenuItem()
            .setPriority(-1)
            .setSlots(IntStream.range(9, 36).toArray())
        );

        loader.addDefaultItem(NightItem.fromType(Material.BLACK_STAINED_GLASS_PANE)
            .setHideTooltip(true)
            .toMenuItem()
            .setPriority(-1)
            .setSlots(0,1,2,3,4,5,6,7,8,36,37,38,39,40,41,42,43,44)
        );

        loader.addDefaultItem(MenuItem.buildNextPage(this, 26).setPriority(10));
        loader.addDefaultItem(MenuItem.buildPreviousPage(this, 18).setPriority(10));
    }
}
