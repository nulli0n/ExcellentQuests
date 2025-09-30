package su.nightexpress.quests.milestone.menu;

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
import su.nightexpress.nightcore.ui.menu.item.MenuItem;
import su.nightexpress.nightcore.ui.menu.type.LinkedMenu;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.milestone.MilestoneManager;
import su.nightexpress.quests.milestone.data.MilestoneData;
import su.nightexpress.quests.milestone.definition.Milestone;
import su.nightexpress.quests.milestone.definition.MilestoneCategory;
import su.nightexpress.quests.reward.Reward;
import su.nightexpress.quests.user.QuestUser;
import su.nightexpress.quests.util.MenuUtils;

import java.util.List;
import java.util.stream.IntStream;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.quests.QuestsPlaceholders.*;

public class ProgressionMenu extends LinkedMenu<QuestsPlugin, Milestone> implements ConfigBased, Filled<Integer> {

    private final MilestoneManager manager;

    private NightItem levelCompleted;
    private NightItem levelInProgress;
    private NightItem levelLocked;
    private int[] levelSlots;

    public ProgressionMenu(@NotNull QuestsPlugin plugin, @NotNull MilestoneManager manager) {
        super(plugin, MenuType.GENERIC_9X3, BLACK.wrap(MILESTONE_NAME + " • Progression"));
        this.manager = manager;
    }

    @Override
    @NotNull
    protected String getTitle(@NotNull MenuViewer viewer) {
        return this.getLink(viewer).replacePlaceholders().apply(super.getTitle(viewer));
    }

    @Override
    @NotNull
    public MenuFiller<Integer> createFiller(@NotNull MenuViewer viewer) {
        Player player = viewer.getPlayer();
        Milestone milestone = this.getLink(player);
        QuestUser user = this.plugin.getUserManager().getOrFetch(player);
        MilestoneData data = user.getMilestoneData(milestone);

        return MenuFiller.builder(this)
            .setSlots(this.levelSlots)
            .setItems(IntStream.range(1, milestone.getLevels() + 1).boxed().toList())
            .setItemCreator(level -> {
                int first = data.getFirstIncompletedLevel(milestone);
                int units = data.countTotalProgress(milestone);
                //double progress = data.getTotalProgressValue(milestone);
                NightItem item = data.isLevelCompleted(level) ? this.levelCompleted : (level == first ? this.levelInProgress : this.levelLocked);
                List<Reward> rewards = this.plugin.getRewardManager().getMilestoneRewards(milestone);

                return item.copy()
                    .hideAllComponents()
                    .replacement(replacer -> replacer
                        //.replace(GENERIC_PROGRESS_BAR, MenuUtils.buildProgressBar(progress))
                        //.replace(GENERIC_PROGRESS, NumberUtil.format(progress * 100D))
                        .replace(GENERIC_LEVEL, () -> String.valueOf(level))
                        .replace(GENERIC_OBJECTIVES, MenuUtils.formatObjectives(milestone, data, level))
                        .replace(GENERIC_REWARDS, MenuUtils.formatRewards(rewards, units, level, 1D))
                    );
            })
            .build();
    }

    private void handleReturn(@NotNull MenuViewer viewer, @NotNull InventoryClickEvent event) {
        Player player = viewer.getPlayer();
        MilestoneCategory category = this.manager.getCategoryById(this.getLink(player).getCategory());
        if (category == null) {
            this.runNextTick(player::closeInventory);
            return;
        }
        this.runNextTick(() -> this.manager.openMilestones(player, category));
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull InventoryView view) {
        this.autoFill(viewer);
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Override
    public void loadConfiguration(@NotNull FileConfig config, @NotNull MenuLoader loader) {
        this.levelCompleted = ConfigValue.create("Level.Completed", NightItem.fromType(Material.LIME_STAINED_GLASS_PANE)
            .setDisplayName(GREEN.and(BOLD).wrap("Level " + GENERIC_LEVEL) + GRAY.wrap(" • ") +  WHITE.wrap("Completed"))
            .setLore(Lists.newList(
                GOLD.wrap("Objectives:"),
                //DARK_GRAY.wrap("»" + " " + GENERIC_PROGRESS_BAR + " (" + WHITE.wrap(GENERIC_PROGRESS + "%") + ")"),
                GENERIC_OBJECTIVES,
                "",
                GOLD.wrap("Rewards:"),
                GENERIC_REWARDS
            ))
            .hideAllComponents()
        ).read(config);

        this.levelInProgress = ConfigValue.create("Level.InProgress", NightItem.fromType(Material.YELLOW_STAINED_GLASS_PANE)
            .setDisplayName(YELLOW.and(BOLD).wrap("Level " + GENERIC_LEVEL) + GRAY.wrap(" • ") +  WHITE.wrap("In Progress"))
            .setLore(Lists.newList(
                GOLD.wrap("Objectives:"),
                //DARK_GRAY.wrap("»" + " " + GENERIC_PROGRESS_BAR + " (" + WHITE.wrap(GENERIC_PROGRESS + "%") + ")"),
                GENERIC_OBJECTIVES,
                "",
                GOLD.wrap("Rewards:"),
                GENERIC_REWARDS
            ))
            .hideAllComponents()
        ).read(config);

        this.levelLocked = ConfigValue.create("Level.Locked", NightItem.fromType(Material.RED_STAINED_GLASS_PANE)
            .setDisplayName(RED.and(BOLD).wrap("Level " + GENERIC_LEVEL) + GRAY.wrap(" • ") +  WHITE.wrap("Locked"))
            .setLore(Lists.newList(
                GRAY.wrap("Complete the previous levels to unlock!")
            ))
            .hideAllComponents()
        ).read(config);

        this.levelSlots = ConfigValue.create("Level.Slots", IntStream.range(9, 27).toArray()).read(config);

        loader.addDefaultItem(MenuItem.buildReturn(this, 22, this::handleReturn));

        loader.addDefaultItem(NightItem.fromType(Material.BLACK_STAINED_GLASS_PANE)
            .setHideTooltip(true)
            .toMenuItem()
            .setPriority(-1)
            .setSlots(0,1,2,3,4,5,6,7,8,18,19,20,21,22,23,24,25,26)
        );
    }
}
