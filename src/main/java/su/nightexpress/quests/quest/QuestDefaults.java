package su.nightexpress.quests.quest;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.Strings;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.wrapper.UniInt;
import su.nightexpress.quests.quest.definition.Quest;
import su.nightexpress.quests.quest.definition.QuestObjectiveTable;
import su.nightexpress.quests.registry.Registries;
import su.nightexpress.quests.reward.RewardDefaults;
import su.nightexpress.quests.task.TaskType;
import su.nightexpress.quests.task.TaskTypeId;
import su.nightexpress.quests.task.adapter.Adapter;

import java.util.List;
import java.util.function.Consumer;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;

public class QuestDefaults {

    public static void createQuests(@NotNull QuestManager manager) {
        createMiningQuests(manager);
        createKillingQuests(manager);
        createFishingQuests(manager);
        createCraftingQuests(manager);
        createSmeltingQuests(manager);
        createBrewingQuests(manager);
    }

    private static void createMiningQuests(@NotNull QuestManager manager) {
        createQuest(manager, TaskTypeId.BREAK_BLOCK,
            "Deepslate Ores",
            Lists.newList(GRAY.wrap("Mine deepslate ores to get reward!")),
            Material.DEEPSLATE_GOLD_ORE,
            quest -> {
                quest.setObjectivesAmount(UniInt.of(1, 3));
                quest.setObjectiveTable(QuestObjectiveTable.builder()
                    .addType(Material.DEEPSLATE_COAL_ORE, Adapter.VANILLA_BLOCK, 15, 30, 30, 5)
                    .addType(Material.DEEPSLATE_IRON_ORE, Adapter.VANILLA_BLOCK, 12, 24, 15, 5.5)
                    .addType(Material.DEEPSLATE_COPPER_ORE, Adapter.VANILLA_BLOCK, 12, 24, 15, 5.5)
                    .addType(Material.DEEPSLATE_GOLD_ORE, Adapter.VANILLA_BLOCK, 10, 20, 10, 6)
                    .addType(Material.DEEPSLATE_REDSTONE_ORE, Adapter.VANILLA_BLOCK, 12, 24, 10, 5.5)
                    .addType(Material.DEEPSLATE_LAPIS_ORE, Adapter.VANILLA_BLOCK, 12, 24, 10, 5.5)
                    .addType(Material.DEEPSLATE_DIAMOND_ORE, Adapter.VANILLA_BLOCK, 7, 14, 5, 10)
                    .addType(Material.DEEPSLATE_EMERALD_ORE, Adapter.VANILLA_BLOCK, 3, 9, 5, 15)
                    .build());
        });

        createQuest(manager, TaskTypeId.BLOCK_LOOT,
            "Raw Ores",
            Lists.newList(
                GRAY.wrap("Mine ore blocks to get reward!"),
                DARK_GRAY.wrap("Tip: Use the Silk Touch enchantment.")
            ),
            Material.DIAMOND_ORE,
            quest -> {
                quest.setObjectivesAmount(UniInt.of(1, 3));
                quest.setObjectiveTable(QuestObjectiveTable.builder()
                    .addType(Material.COAL_ORE, Adapter.VANILLA_BLOCK, 15, 30, 30, 7)
                    .addType(Material.IRON_ORE, Adapter.VANILLA_BLOCK, 12, 24, 15, 5.5)
                    .addType(Material.COPPER_ORE, Adapter.VANILLA_BLOCK, 12, 24, 15, 5.5)
                    .addType(Material.GOLD_ORE, Adapter.VANILLA_BLOCK, 10, 20, 10, 8)
                    .addType(Material.REDSTONE_ORE, Adapter.VANILLA_BLOCK, 12, 24, 10, 5.5)
                    .addType(Material.LAPIS_ORE, Adapter.VANILLA_BLOCK, 12, 24, 10, 5.5)
                    .addType(Material.DIAMOND_ORE, Adapter.VANILLA_BLOCK, 7, 14, 5, 10)
                    .addType(Material.EMERALD_ORE, Adapter.VANILLA_BLOCK, 3, 9, 5, 15)
                    .build());
            });

        createQuest(manager, TaskTypeId.BREAK_BLOCK,
            "Mining Stones",
            Lists.newList(GRAY.wrap("Mine those stones to get reward!")),
            Material.STONE,
            quest -> {
                quest.setObjectivesAmount(UniInt.of(1, 2));
                quest.setObjectiveTable(QuestObjectiveTable.builder()
                    .addType(Material.STONE, Adapter.VANILLA_BLOCK, 300, 500, 40, 0.3)
                    .addType(Material.GRANITE, Adapter.VANILLA_BLOCK, 200, 300, 20, 0.35)
                    .addType(Material.ANDESITE, Adapter.VANILLA_BLOCK, 200, 300, 20, 0.35)
                    .addType(Material.DIORITE, Adapter.VANILLA_BLOCK, 200, 300, 20, 0.35)
                    .build());
            });
    }

    private static void createKillingQuests(@NotNull QuestManager manager) {
        createQuest(manager, TaskTypeId.KILL_MOB,
            "Killing Zombies",
            Lists.newList(GRAY.wrap("Kill zombies to get reward!")),
            Material.ZOMBIE_HEAD,
            quest -> {
                quest.setObjectivesAmount(UniInt.of(1, 1));
                quest.setObjectiveTable(QuestObjectiveTable.builder()
                    .addType(EntityType.ZOMBIE, Adapter.VANILLA_MOB, 35, 50, 70, 3)
                    .addType(EntityType.ZOMBIE_VILLAGER, Adapter.VANILLA_MOB, 10, 15, 30, 10)
                    .addType(EntityType.ZOMBIFIED_PIGLIN, Adapter.VANILLA_MOB, 10, 15, 30, 10)
                    .build());
            });

        createQuest(manager, TaskTypeId.KILL_MOB,
            "Killing Skeletons",
            Lists.newList(GRAY.wrap("Kill skeletons to get reward!")),
            Material.SKELETON_SKULL,
            quest -> {
                quest.setObjectivesAmount(UniInt.of(1, 1));
                quest.setObjectiveTable(QuestObjectiveTable.builder()
                    .addType(EntityType.SKELETON, Adapter.VANILLA_MOB, 30, 45, 60, 4)
                    .addType(EntityType.STRAY, Adapter.VANILLA_MOB, 12, 20, 15, 8)
                    .addType(EntityType.BOGGED, Adapter.VANILLA_MOB, 12, 20, 15, 8)
                    .addType(EntityType.WITHER_SKELETON, Adapter.VANILLA_MOB, 5, 10, 10, 15)
                    .build());
            });
    }

    private static void createFishingQuests(@NotNull QuestManager manager) {
        createQuest(manager, TaskTypeId.FISH_ITEM,
            "Fishing Day",
            Lists.newList(GRAY.wrap("Fish fishes to get reward!")),
            Material.TROPICAL_FISH_BUCKET,
            quest -> {
                quest.setObjectivesAmount(UniInt.of(1, 1));
                quest.setObjectiveTable(QuestObjectiveTable.builder()
                    .addType(Material.COD, Adapter.VANILLA_ITEM, 30, 45, 60, 3)
                    .addType(Material.SALMON, Adapter.VANILLA_ITEM, 20, 25, 20, 6)
                    .addType(Material.PUFFERFISH, Adapter.VANILLA_ITEM, 8, 15, 15, 10)
                    .addType(Material.TROPICAL_FISH, Adapter.VANILLA_ITEM, 3,6, 5, 25)
                    .build());
            });

        createQuest(manager, TaskTypeId.FISH_ITEM,
            "Treasures of the Sea",
            Lists.newList(GRAY.wrap("Fish treasures to get reward!")),
            Material.NAUTILUS_SHELL,
            quest -> {
                quest.setObjectivesAmount(UniInt.of(1, 2));
                quest.setObjectiveTable(QuestObjectiveTable.builder()
                    .addType(Material.BOW, Adapter.VANILLA_ITEM, 3, 5, 15, 25)
                    .addType(Material.ENCHANTED_BOOK, Adapter.VANILLA_ITEM, 1, 5, 15, 35)
                    .addType(Material.FISHING_ROD, Adapter.VANILLA_ITEM, 2, 5, 15, 25)
                    .addType(Material.NAME_TAG, Adapter.VANILLA_ITEM, 2,5, 15, 25)
                    .addType(Material.NAUTILUS_SHELL, Adapter.VANILLA_ITEM, 2,5, 15, 30)
                    .addType(Material.SADDLE, Adapter.VANILLA_ITEM, 1,3, 15, 30)
                    .build());
            });
    }

    private static void createCraftingQuests(@NotNull QuestManager manager) {
        createQuest(manager, TaskTypeId.CRAFT_ITEM,
            "Armorsmith",
            Lists.newList(GRAY.wrap("Craft armors to get reward!")),
            Material.IRON_CHESTPLATE,
            quest -> {
                quest.setObjectivesAmount(UniInt.of(1, 4));
                quest.setObjectiveTable(QuestObjectiveTable.builder()
                    .addType(Material.NETHERITE_HELMET, Adapter.VANILLA_ITEM, 1, 3, 5, 70)
                    .addType(Material.NETHERITE_CHESTPLATE, Adapter.VANILLA_ITEM, 1, 3, 5, 70)
                    .addType(Material.NETHERITE_LEGGINGS, Adapter.VANILLA_ITEM, 1, 3, 5, 70)
                    .addType(Material.NETHERITE_BOOTS, Adapter.VANILLA_ITEM, 1,3, 5, 70)

                    .addType(Material.DIAMOND_HELMET, Adapter.VANILLA_ITEM, 2, 4, 5, 55)
                    .addType(Material.DIAMOND_CHESTPLATE, Adapter.VANILLA_ITEM, 2, 4, 5, 55)
                    .addType(Material.DIAMOND_LEGGINGS, Adapter.VANILLA_ITEM, 2, 4, 5, 55)
                    .addType(Material.DIAMOND_BOOTS, Adapter.VANILLA_ITEM, 2,4, 5, 55)

                    .addType(Material.IRON_HELMET, Adapter.VANILLA_ITEM, 2, 6, 5, 35)
                    .addType(Material.IRON_CHESTPLATE, Adapter.VANILLA_ITEM, 2, 6, 5, 35)
                    .addType(Material.IRON_LEGGINGS, Adapter.VANILLA_ITEM, 2, 6, 5, 35)
                    .addType(Material.IRON_BOOTS, Adapter.VANILLA_ITEM, 2,6, 5, 35)

                    .addType(Material.LEATHER_HELMET, Adapter.VANILLA_ITEM, 3, 8, 5, 30)
                    .addType(Material.LEATHER_CHESTPLATE, Adapter.VANILLA_ITEM, 3, 8, 5, 30)
                    .addType(Material.LEATHER_LEGGINGS, Adapter.VANILLA_ITEM, 3, 8, 5, 30)
                    .addType(Material.LEATHER_BOOTS, Adapter.VANILLA_ITEM, 3,8, 5, 30)
                    .build());
            });

        createQuest(manager, TaskTypeId.CRAFT_ITEM,
            "Weaponsmith",
            Lists.newList(GRAY.wrap("Craft swords to get reward!")),
            Material.IRON_SWORD,
            quest -> {
                quest.setObjectivesAmount(UniInt.of(1, 4));
                quest.setObjectiveTable(QuestObjectiveTable.builder()
                    .addType(Material.NETHERITE_SWORD, Adapter.VANILLA_ITEM, 1, 5, 5, 60)
                    .addType(Material.DIAMOND_SWORD, Adapter.VANILLA_ITEM, 4, 8, 5, 30)
                    .addType(Material.IRON_SWORD, Adapter.VANILLA_ITEM, 7, 20, 5, 15)
                    .addType(Material.STONE_SWORD, Adapter.VANILLA_ITEM, 35, 50, 5, 4)
                    .addType(Material.WOODEN_SWORD, Adapter.VANILLA_ITEM, 50, 80, 5, 3)
                    .build());
            });

        createQuest(manager, TaskTypeId.CRAFT_ITEM,
            "Archer",
            Lists.newList(GRAY.wrap("Craft arrows to get reward!")),
            Material.ARROW,
            quest -> {
                quest.setObjectivesAmount(UniInt.of(1, 1));
                quest.setObjectiveTable(QuestObjectiveTable.builder()
                    .addType(Material.ARROW, Adapter.VANILLA_ITEM, 100, 150, 5, 1.5)
                    .addType(Material.TIPPED_ARROW, Adapter.VANILLA_ITEM, 40, 60, 5, 3)
                    .build());
            });

        createQuest(manager, TaskTypeId.CRAFT_ITEM,
            "Carpenter",
            Lists.newList(GRAY.wrap("Craft wooden planks to get reward!")),
            Material.OAK_PLANKS,
            quest -> {
                quest.setObjectivesAmount(UniInt.of(1, 3));
                quest.setObjectiveTable(QuestObjectiveTable.builder()
                    .addType(Material.OAK_PLANKS, Adapter.VANILLA_ITEM, 300, 400, 5, 0.25)
                    .addType(Material.SPRUCE_PLANKS, Adapter.VANILLA_ITEM, 300, 400, 5, 0.35)
                    .addType(Material.JUNGLE_PLANKS, Adapter.VANILLA_ITEM, 300, 400, 5, 0.45)
                    .addType(Material.ACACIA_PLANKS, Adapter.VANILLA_ITEM, 300, 400, 5, 0.45)
                    .addType(Material.DARK_OAK_PLANKS, Adapter.VANILLA_ITEM, 300, 400, 5, 0.35)
                    .addType(Material.MANGROVE_PLANKS, Adapter.VANILLA_ITEM, 300, 400, 5, 0.45)
                    .addType(Material.CHERRY_PLANKS, Adapter.VANILLA_ITEM, 300, 400, 5, 0.5)
                    .addType(Material.BAMBOO_PLANKS, Adapter.VANILLA_ITEM, 300, 400, 5, 0.5)
                    .addType(Material.PALE_OAK_PLANKS, Adapter.VANILLA_ITEM, 300, 400, 5, 0.5)
                    .addType(Material.CRIMSON_PLANKS, Adapter.VANILLA_ITEM, 300, 400, 5, 0.6)
                    .addType(Material.WARPED_PLANKS, Adapter.VANILLA_ITEM, 300, 400, 5, 0.6)
                    .build());
            });

        createQuest(manager, TaskTypeId.CRAFT_ITEM,
            "Portable Storage",
            Lists.newList(GRAY.wrap("Craft shulkers to get reward!")),
            Material.SHULKER_BOX,
            quest -> {
                quest.setObjectivesAmount(UniInt.of(1, 1));
                quest.setObjectiveTable(QuestObjectiveTable.builder()
                    .addType(Material.SHULKER_BOX, Adapter.VANILLA_ITEM, 4, 8, 5, 20)
                    .build());
            });

        createQuest(manager, TaskTypeId.CRAFT_ITEM,
            "North Star",
            Lists.newList(GRAY.wrap("Craft beacon(s) to get reward!")),
            Material.BEACON,
            quest -> {
                quest.setObjectivesAmount(UniInt.of(1, 1));
                quest.setObjectiveTable(QuestObjectiveTable.builder()
                    .addType(Material.BEACON, Adapter.VANILLA_ITEM, 1, 2, 5, 100)
                    .build());
            });

        createQuest(manager, TaskTypeId.CRAFT_ITEM,
            "A True Pirate",
            Lists.newList(GRAY.wrap("Craft spyglass to get reward!")),
            Material.SPYGLASS,
            quest -> {
                quest.setObjectivesAmount(UniInt.of(1, 1));
                quest.setObjectiveTable(QuestObjectiveTable.builder()
                    .addType(Material.SPYGLASS, Adapter.VANILLA_ITEM, 15, 20, 5, 6)
                    .build());
            });

        createQuest(manager, TaskTypeId.CRAFT_ITEM,
            "Ender Eyes",
            Lists.newList(GRAY.wrap("Craft Ender Eye to get reward!")),
            Material.ENDER_EYE,
            quest -> {
                quest.setObjectivesAmount(UniInt.of(1, 1));
                quest.setObjectiveTable(QuestObjectiveTable.builder()
                    .addType(Material.ENDER_EYE, Adapter.VANILLA_ITEM, 8, 15, 5, 15)
                    .build());
            });
    }

    private static void createSmeltingQuests(@NotNull QuestManager manager) {
        createQuest(manager, TaskTypeId.COOK_ITEM,
            "Caster",
            Lists.newList(GRAY.wrap("Smelt ores to ingots to get reward!")),
            Material.GOLD_ORE,
            quest -> {
                quest.setObjectivesAmount(UniInt.of(1, 1));
                quest.setObjectiveTable(QuestObjectiveTable.builder()
                    .addType(Material.COPPER_ORE, Adapter.VANILLA_ITEM, 150, 200, 5, 1.25)
                    .addType(Material.IRON_ORE, Adapter.VANILLA_ITEM, 130, 180, 5, 1.5)
                    .addType(Material.GOLD_ORE, Adapter.VANILLA_ITEM, 80, 120, 5, 2.5)
                    .addType(Material.ANCIENT_DEBRIS, Adapter.VANILLA_ITEM, 5, 15, 5, 17)
                    .build());
            });

        createQuest(manager, TaskTypeId.COOK_ITEM,
            "Chef",
            Lists.newList(GRAY.wrap("Cook meat to get reward!")),
            Material.COOKED_BEEF,
            quest -> {
                quest.setObjectivesAmount(UniInt.of(1, 2));
                quest.setObjectiveTable(QuestObjectiveTable.builder()
                    .addType(Material.PORKCHOP, Adapter.VANILLA_ITEM, 60, 80, 5, 2)
                    .addType(Material.MUTTON, Adapter.VANILLA_ITEM, 60, 80, 5, 2)
                    .addType(Material.CHICKEN, Adapter.VANILLA_ITEM, 60, 80, 5, 2)
                    .addType(Material.RABBIT, Adapter.VANILLA_ITEM, 30, 50, 5, 3)
                    .addType(Material.BEEF, Adapter.VANILLA_ITEM, 80, 100, 5, 1.75)
                    .addType(Material.COD, Adapter.VANILLA_ITEM, 50, 70, 5, 2)
                    .addType(Material.SALMON, Adapter.VANILLA_ITEM, 35, 50, 5, 3)
                    .build());
            });

        createQuest(manager, TaskTypeId.COOK_ITEM,
            "Drying Sponges",
            Lists.newList(GRAY.wrap("Dry sponges in furnace to get reward!")),
            Material.WET_SPONGE,
            quest -> {
                quest.setObjectivesAmount(UniInt.of(1, 1));
                quest.setObjectiveTable(QuestObjectiveTable.builder()
                    .addType(Material.WET_SPONGE, Adapter.VANILLA_ITEM, 15, 25, 5, 7)
                    .build());
            });

        createQuest(manager, TaskTypeId.COOK_ITEM,
            "Bricklayer",
            Lists.newList(GRAY.wrap("Smelt stones to get reward!")),
            Material.STONE,
            quest -> {
                quest.setObjectivesAmount(UniInt.of(1, 1));
                quest.setObjectiveTable(QuestObjectiveTable.builder()
                    .addType(Material.COBBLESTONE, Adapter.VANILLA_ITEM, 150, 200, 5, 1)
                    .addType(Material.STONE, Adapter.VANILLA_ITEM, 150, 200, 5, 1.25)
                    .build());
            });

        createQuest(manager, TaskTypeId.COOK_ITEM,
            "Making Glass",
            Lists.newList(GRAY.wrap("Smelt sand into glass to get reward!")),
            Material.GLASS,
            quest -> {
                quest.setObjectivesAmount(UniInt.of(1, 1));
                quest.setObjectiveTable(QuestObjectiveTable.builder()
                    .addType(Material.SAND, Adapter.VANILLA_ITEM, 150, 200, 5, 1)
                    .build());
            });

        createQuest(manager, TaskTypeId.COOK_ITEM,
            "Making Bricks",
            Lists.newList(GRAY.wrap("Smelt clay into bricks to get reward!")),
            Material.BRICK,
            quest -> {
                quest.setObjectivesAmount(UniInt.of(1, 1));
                quest.setObjectiveTable(QuestObjectiveTable.builder()
                    .addType(Material.CLAY_BALL, Adapter.VANILLA_ITEM, 200, 250, 5, 0.7)
                    .build());
            });
    }

    private static void createBrewingQuests(@NotNull QuestManager manager) {
        createQuest(manager, TaskTypeId.BREWING,
            "Evil Alchemy",
            Lists.newList(GRAY.wrap("Brew potions of bad ingredients to get reward!")),
            Material.FERMENTED_SPIDER_EYE,
            quest -> {
                quest.setObjectivesAmount(UniInt.of(1, 1));
                quest.setObjectiveTable(QuestObjectiveTable.builder()
                    .addType(Material.SPIDER_EYE, Adapter.VANILLA_ITEM, 10, 20, 5, 10)
                    .addType(Material.FERMENTED_SPIDER_EYE, Adapter.VANILLA_ITEM, 10, 20, 5, 8)
                    .addType(Material.SLIME_BLOCK, Adapter.VANILLA_ITEM, 10, 20, 5, 8)
                    .addType(Material.COBWEB, Adapter.VANILLA_ITEM, 10, 20, 5, 8)
                    .addType(Material.STONE, Adapter.VANILLA_ITEM, 10, 20, 5, 8)
                    .addType(Material.BREEZE_ROD, Adapter.VANILLA_ITEM, 10, 20, 5, 8)
                    .build());
            });

        createQuest(manager, TaskTypeId.BREWING,
            "Good Alchemy",
            Lists.newList(GRAY.wrap("Brew potions of good ingredients to get reward!")),
            Material.GLISTERING_MELON_SLICE,
            quest -> {
                quest.setObjectivesAmount(UniInt.of(1, 1));
                quest.setObjectiveTable(QuestObjectiveTable.builder()
                    .addType(Material.GLISTERING_MELON_SLICE, Adapter.VANILLA_ITEM, 15, 30, 5, 7)
                    .addType(Material.MAGMA_CREAM, Adapter.VANILLA_ITEM, 15, 30, 5, 7)
                    .addType(Material.GHAST_TEAR, Adapter.VANILLA_ITEM, 15, 30, 5, 7)
                    .addType(Material.BLAZE_POWDER, Adapter.VANILLA_ITEM, 15, 30, 5, 7)
                    .addType(Material.SUGAR, Adapter.VANILLA_ITEM, 15, 30, 5, 7)
                    .addType(Material.GOLDEN_CARROT, Adapter.VANILLA_ITEM, 15, 30, 5, 7)
                    .addType(Material.PUFFERFISH, Adapter.VANILLA_ITEM, 15, 30, 5, 7)
                    .addType(Material.RABBIT_FOOT, Adapter.VANILLA_ITEM, 15, 30, 5, 7)
                    .addType(Material.PHANTOM_MEMBRANE, Adapter.VANILLA_ITEM, 15, 30, 5, 7)
                    .build());
            });
    }

    private static void createQuest(@NotNull QuestManager manager,
                                    @NotNull String type,
                                    @NotNull String name,
                                    @NotNull List<String> desc,
                                    @NotNull Material iconType,
                                    @NotNull Consumer<Quest> consumer) {
        TaskType<?, ?> taskType = Registries.TASK_TYPE.byKey(type);
        if (taskType == null) return;

        manager.createQuest(Strings.filterForVariable(name), quest -> {
            quest.setType(taskType);
            quest.setName(name);
            quest.setDescription(desc);
            quest.setIcon(NightItem.fromType(iconType));
            quest.addReward(RewardDefaults.QUEST_CASH_LOW);
            quest.setBattlePassXPReward(5, 1);
            quest.setCompletionTime(3600 * 6);
            consumer.accept(quest);
        });
    }
}
