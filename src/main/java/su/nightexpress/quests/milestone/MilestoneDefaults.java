package su.nightexpress.quests.milestone;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.LangUtil;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.quests.milestone.definition.Milestone;
import su.nightexpress.quests.milestone.definition.MilestoneCategory;
import su.nightexpress.quests.milestone.definition.MilestoneObjectiveTable;
import su.nightexpress.quests.registry.Registries;
import su.nightexpress.quests.reward.RewardDefaults;
import su.nightexpress.quests.task.TaskType;
import su.nightexpress.quests.task.TaskTypeId;
import su.nightexpress.quests.task.adapter.Adapter;

import java.util.*;
import java.util.function.Consumer;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.quests.QuestsPlaceholders.*;

public class MilestoneDefaults {

    private static final String CATEGORY_MINING  = "mining";
    private static final String CATEGORY_FISHING = "fishing";
    private static final String CATEGORY_FARMING = "farming";

    private static final int LEVELS = 9;

    private static final int[] LQ_MINE_VALUES = new int[]{1000, 2000, 3000, 5000, 7500, 10000, 12500, 15000, 20000};
    private static final int[] MQ_MINE_VALUES = new int[]{500, 1250, 2000, 3500, 5000, 7500, 10000, 12500, 15000};
    private static final int[] HQ_MINE_VALUES = new int[]{100, 250, 500, 1000, 2000, 4000, 7500, 10000, 20000};
    private static final int[] UQ_MINE_VALUES = new int[]{25, 50, 100, 200, 450, 800, 1500, 3000, 5000};

    @NotNull
    public static List<MilestoneCategory> createCategories() {
        List<MilestoneCategory> categories = new ArrayList<>();

        categories.add(new MilestoneCategory(CATEGORY_MINING, "Mining", Lists.newList(
            GRAY.wrap(PLAYER_NAME + ", Destroyer of Worlds."),
            GRAY.wrap("Complete milestones by breaking specific blocks.")
        ), NightItem.fromType(Material.IRON_PICKAXE)));

        categories.add(new MilestoneCategory(CATEGORY_FISHING, "Fishing", Lists.newList(
            GRAY.wrap("It takes some patience, we know..."),
            GRAY.wrap("Complete milestones by fishing different fish.")
        ), NightItem.fromType(Material.FISHING_ROD)));

        categories.add(new MilestoneCategory(CATEGORY_FARMING, "Farming", Lists.newList(
            GRAY.wrap("Ever played Farmville?"),
            GRAY.wrap("Complete milestones by farming crops.")
        ), NightItem.fromType(Material.IRON_HOE)));

        return categories;
    }

    public static void createMilestones(@NotNull MilestoneManager manager) {
        createMiningMilestones(manager);
        createFarmingMilestones(manager);
        createFishingMilestones(manager);
    }

    private static void createMiningMilestones(@NotNull MilestoneManager manager) {
        createMiningMilestone(manager, Material.DIRT, LQ_MINE_VALUES);
        createMiningMilestone(manager, Material.STONE, LQ_MINE_VALUES);
        createMiningMilestone(manager, Material.SAND, MQ_MINE_VALUES);
        createMiningMilestone(manager, Material.GRANITE, MQ_MINE_VALUES);
        createMiningMilestone(manager, Material.ANDESITE, MQ_MINE_VALUES);
        createMiningMilestone(manager, Material.DIORITE, MQ_MINE_VALUES);
        createMiningMilestone(manager, Material.DEEPSLATE, MQ_MINE_VALUES);
        createMiningMilestone(manager, Material.DEEPSLATE_DIAMOND_ORE, HQ_MINE_VALUES);
        createMiningMilestone(manager, Material.DEEPSLATE_LAPIS_ORE, HQ_MINE_VALUES);
        createMiningMilestone(manager, Material.DEEPSLATE_GOLD_ORE, HQ_MINE_VALUES);
        createMiningMilestone(manager, Material.DEEPSLATE_IRON_ORE, HQ_MINE_VALUES);
        createMiningMilestone(manager, Material.DEEPSLATE_COAL_ORE, HQ_MINE_VALUES);
        createMiningMilestone(manager, Material.DEEPSLATE_COPPER_ORE, HQ_MINE_VALUES);
        createMiningMilestone(manager, Material.DEEPSLATE_EMERALD_ORE, HQ_MINE_VALUES);
        createMiningMilestone(manager, Material.DEEPSLATE_REDSTONE_ORE, HQ_MINE_VALUES);

        Tag.LOGS_THAT_BURN.getValues().forEach(material -> {
            if (material.name().startsWith("STRIPPED")) return;
            if (!material.name().endsWith("_LOG")) return;

            createMiningMilestone(manager, material, MQ_MINE_VALUES);
        });
    }

    private static void createFarmingMilestones(@NotNull MilestoneManager manager) {
        createFarmingMilestone(manager, Material.WHEAT, HQ_MINE_VALUES);
        createFarmingMilestone(manager, Material.CARROT, HQ_MINE_VALUES);
        createFarmingMilestone(manager, Material.POTATO, HQ_MINE_VALUES);
        createFarmingMilestone(manager, Material.BEETROOT, HQ_MINE_VALUES);
        createFarmingMilestone(manager, Material.SUGAR_CANE, MQ_MINE_VALUES);
        createFarmingMilestone(manager, Material.MELON_SLICE, MQ_MINE_VALUES);
        createFarmingMilestone(manager, Material.NETHER_WART, HQ_MINE_VALUES);
    }

    private static void createFishingMilestones(@NotNull MilestoneManager manager) {
        createFishingMilestone(manager, Material.COD, HQ_MINE_VALUES);
        createFishingMilestone(manager, Material.SALMON, UQ_MINE_VALUES);
        createFishingMilestone(manager, Material.PUFFERFISH, UQ_MINE_VALUES);
        createFishingMilestone(manager, Material.TROPICAL_FISH, UQ_MINE_VALUES);

    }

    private static void createMiningMilestone(@NotNull MilestoneManager manager, @NotNull Material material, int[] values) {
        createMilestone(manager, "mining_" + BukkitThing.getValue(material), TaskTypeId.BREAK_BLOCK, CATEGORY_MINING, milestone -> {
            milestone.setName(LangUtil.getSerializedName(material));
            milestone.setIcon(NightItem.fromType(material));
            milestone.setObjectiveTable(MilestoneObjectiveTable.builder().addType(material, Adapter.VANILLA_BLOCK, values).build());
        });
    }

    private static void createFarmingMilestone(@NotNull MilestoneManager manager, @NotNull Material itemType, int[] values) {
        createMilestone(manager, "farming_" + BukkitThing.getValue(itemType), TaskTypeId.BLOCK_LOOT, CATEGORY_FARMING, milestone -> {
            milestone.setName(LangUtil.getSerializedName(itemType));
            milestone.setIcon(NightItem.fromType(itemType));
            milestone.setObjectiveTable(MilestoneObjectiveTable.builder().addType(itemType, Adapter.VANILLA_ITEM, values).build());
        });
    }

    private static void createFishingMilestone(@NotNull MilestoneManager manager, @NotNull Material itemType, int[] values) {
        createMilestone(manager, "fishing_" + BukkitThing.getValue(itemType), TaskTypeId.FISH_ITEM, CATEGORY_FISHING, milestone -> {
            milestone.setName(LangUtil.getSerializedName(itemType));
            milestone.setIcon(NightItem.fromType(itemType));
            milestone.setObjectiveTable(MilestoneObjectiveTable.builder().addType(itemType, Adapter.VANILLA_ITEM, values).build());
        });
    }

    private static void createMilestone(@NotNull MilestoneManager manager, @NotNull String id, @NotNull String type, @NotNull String category, @NotNull Consumer<Milestone> consumer) {
        TaskType<?, ?> taskType = Registries.TASK_TYPE.byKey(type);
        if (taskType == null) return;

        manager.createMilestone(id, milestone -> {
            milestone.setLevels(LEVELS);
            milestone.setCategory(category);
            milestone.setType(taskType);
            milestone.addReward(RewardDefaults.MS_CASH_MEDIUM);
            consumer.accept(milestone);
        });
    }
}
