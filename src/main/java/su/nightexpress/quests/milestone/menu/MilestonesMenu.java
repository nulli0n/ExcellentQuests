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
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.config.Lang;
import su.nightexpress.quests.milestone.MilestoneManager;
import su.nightexpress.quests.milestone.data.MilestoneData;
import su.nightexpress.quests.milestone.definition.Milestone;
import su.nightexpress.quests.milestone.definition.MilestoneCategory;
import su.nightexpress.quests.reward.Reward;
import su.nightexpress.quests.user.QuestUser;
import su.nightexpress.quests.util.MenuUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.BLACK;
import static su.nightexpress.quests.QuestsPlaceholders.*;

public class MilestonesMenu extends LinkedMenu<QuestsPlugin, MilestoneCategory> implements ConfigBased, Filled<Milestone> {

    private final MilestoneManager manager;

    private int[] milestoneSlots;

    public MilestonesMenu(@NotNull QuestsPlugin plugin, @NotNull MilestoneManager manager) {
        super(plugin, MenuType.GENERIC_9X5, BLACK.wrap("Milestone • " + MILESTONE_CATEGORY_NAME));
        this.manager = manager;
    }

    @Override
    @NotNull
    protected String getTitle(@NotNull MenuViewer viewer) {
        return this.getLink(viewer).replacePlaceholders().apply(super.getTitle(viewer));
    }

    @Override
    @NotNull
    public MenuFiller<Milestone> createFiller(@NotNull MenuViewer viewer) {
        Player player = viewer.getPlayer();
        MilestoneCategory category = this.getLink(player);
        QuestUser user = this.plugin.getUserManager().getOrFetch(player);

        return MenuFiller.builder(this)
            .setSlots(this.milestoneSlots)
            .setItems(this.manager.getMilestonesByCategory(category).stream().sorted(Comparator.comparing(Milestone::getName)).toList())
            .setItemCreator(milestone -> {
                MilestoneData data = user.getMilestoneData(milestone);
                int level = data.getFirstIncompletedLevel(milestone);
                int units = data.countTotalProgress(milestone);
                double progress = data.getTotalProgressValue(milestone);
                List<Reward> rewards = this.plugin.getRewardManager().getMilestoneRewards(milestone);

                return milestone.getIcon()
                    .hideAllComponents()
                    .localized(Lang.UI_MILESTONES_MILESTONE_INFO)
                    .replacement(replacer -> replacer
                        .replace(GENERIC_PROGRESS_BAR, MenuUtils.buildProgressBar(progress))
                        .replace(GENERIC_PROGRESS, NumberUtil.format(progress * 100D))
                        .replace(GENERIC_LEVEL, () -> String.valueOf(data.getFirstIncompletedLevel(milestone)))
                        .replace(GENERIC_OBJECTIVES, MenuUtils.formatObjectives(milestone, data, level))
                        .replace(GENERIC_REWARDS, MenuUtils.formatRewards(rewards, units, level, 1D))
                        .replace(milestone.replacePlaceholders())
                    );
            })
            .setItemClick(milestone -> (viewer1, event) -> {
                this.runNextTick(() -> this.manager.openProgression(player, milestone));
            })
            .build();
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull InventoryView view) {
        this.autoFill(viewer);
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    private void handleReturn(@NotNull MenuViewer viewer, @NotNull InventoryClickEvent event) {
        this.runNextTick(() -> this.manager.openCategories(viewer.getPlayer()));
    }

    @Override
    public void loadConfiguration(@NotNull FileConfig config, @NotNull MenuLoader loader) {
        this.milestoneSlots = ConfigValue.create("Milestone.Slots", IntStream.range(9, 36).toArray()).read(config);

        loader.addDefaultItem(MenuItem.buildNextPage(this, 44));
        loader.addDefaultItem(MenuItem.buildPreviousPage(this, 36));
        loader.addDefaultItem(MenuItem.buildReturn(this, 40, this::handleReturn));

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
}
