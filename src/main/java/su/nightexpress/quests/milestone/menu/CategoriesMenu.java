package su.nightexpress.quests.milestone.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.ui.menu.MenuViewer;
import su.nightexpress.nightcore.ui.menu.data.ConfigBased;
import su.nightexpress.nightcore.ui.menu.data.MenuLoader;
import su.nightexpress.nightcore.ui.menu.type.NormalMenu;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.text.night.NightMessage;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.config.Lang;
import su.nightexpress.quests.milestone.MilestoneManager;
import su.nightexpress.quests.milestone.definition.Milestone;
import su.nightexpress.quests.milestone.definition.MilestoneCategory;
import su.nightexpress.quests.user.QuestUser;

import java.util.*;
import java.util.stream.IntStream;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.BLACK;
import static su.nightexpress.quests.QuestsPlaceholders.*;

public class CategoriesMenu extends NormalMenu<QuestsPlugin> implements ConfigBased {

    private final MilestoneManager manager;

    private final TreeMap<Integer, int[]> slotsByCategoryCount;

    public CategoriesMenu(@NotNull QuestsPlugin plugin, @NotNull MilestoneManager manager) {
        super(plugin, MenuType.GENERIC_9X5, BLACK.wrap("Milestone • Categories"));
        this.manager = manager;
        this.slotsByCategoryCount = new TreeMap<>();
        this.setApplyPlaceholderAPI(true);
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull InventoryView view) {
        Player player = viewer.getPlayer();
        QuestUser user = this.plugin.getUserManager().getOrFetch(player);
        List<MilestoneCategory> categories = this.manager.getCategories().stream().sorted(Comparator.comparing(category -> NightMessage.stripTags(category.getName()))).toList();

        int categoryCount = categories.size();
        int[] categorySlots = Optional.ofNullable(this.slotsByCategoryCount.ceilingEntry(categoryCount)).map(Map.Entry::getValue).orElse(new int[0]);

        for (int index = 0; index < categorySlots.length; index++) {
            int slot = categorySlots[index];
            MilestoneCategory category = categories.get(index);
            Set<Milestone> milestones = this.manager.getMilestonesByCategory(category);

            viewer.addItem(category.getIcon()
                .hideAllComponents()
                .localized(Lang.UI_MILESTONES_CATEGORY_INFO)
                .replacement(replacer -> replacer
                    .replace(category.replacePlaceholders())
                    .replace(GENERIC_TOTAL, () -> String.valueOf(milestones.size()))
                    .replace(GENERIC_COMPLETED, () -> String.valueOf(milestones.stream().filter(user::isCompleted).count()))
                    .replace(GENERIC_LEVELS, () -> String.valueOf(milestones.stream().mapToInt(user::getMilestoneCompletedLevels).sum()))
                )
                .toMenuItem()
                .setPriority(Integer.MAX_VALUE)
                .setSlots(slot)
                .setHandler((viewer1, event) -> this.runNextTick(() -> this.manager.openMilestones(viewer1.getPlayer(), category)))
                .build()
            );
        }
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Override
    public void loadConfiguration(@NotNull FileConfig config, @NotNull MenuLoader loader) {
        for (int count = 0; count < 10; count++) {
            int amount = count + 1;
            int[] defSlots = getDefaultSlots(amount);
            int[] skillSlots = ConfigValue.create("Category.SlotsByCount." + amount, defSlots).read(config);

            this.slotsByCategoryCount.put(amount, skillSlots);
        }

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
