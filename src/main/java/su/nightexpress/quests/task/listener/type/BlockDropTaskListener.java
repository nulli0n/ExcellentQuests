package su.nightexpress.quests.task.listener.type;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.config.Config;
import su.nightexpress.quests.task.adapter.AdapterFamily;
import su.nightexpress.quests.task.TaskManager;
import su.nightexpress.quests.task.TaskType;
import su.nightexpress.quests.task.listener.TaskListener;

public class BlockDropTaskListener extends TaskListener<ItemStack, AdapterFamily<ItemStack>> {

    public BlockDropTaskListener(@NotNull QuestsPlugin plugin, @NotNull TaskManager manager, @NotNull TaskType<ItemStack, AdapterFamily<ItemStack>> taskType) {
        super(plugin, manager, taskType);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTaskBlockHarvest(PlayerHarvestBlockEvent event) {
        Player player = event.getPlayer();
        if (!this.manager.canDoTasks(player)) return;

        event.getItemsHarvested().forEach(itemStack -> {
            this.progressQuests(player, itemStack, itemStack.getAmount());
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTaskBlockDrop(BlockDropItemEvent event) {
        Player player = event.getPlayer();
        if (!this.manager.canDoTasks(player)) return;
        if (event.getBlockState() instanceof Container) return; // Do not handle container's drops.

        Block block = event.getBlock();
        if (!Config.ANTI_ABUSE_COUNT_PLAYER_BLOCKS.get() && this.manager.isPlayerBlock(block)) return;

        event.getItems().forEach(item -> {
            ItemStack itemStack = item.getItemStack();
            this.progressQuests(player, itemStack, itemStack.getAmount());
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTaskHoneyCollect(PlayerInteractEvent event) {
        if (event.useItemInHand() == Event.Result.DENY) return;
        if (event.useInteractedBlock() == Event.Result.DENY) return;

        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.BEEHIVE) return;

        EquipmentSlot slot = event.getHand();
        if (slot == null) return;

        Player player = event.getPlayer();
        if (!this.manager.canDoTasks(player)) return;

        ItemStack itemStack = player.getInventory().getItem(slot);
        if (itemStack == null || itemStack.getType() != Material.GLASS_BOTTLE) return;

        this.plugin.runTask(task -> {
            ItemStack honey = player.getInventory().getItem(slot);
            if (honey == null || honey.getType() != Material.HONEY_BOTTLE) return;

            //this.giveXP(player, (job, table) -> table.getBlockResourceXP(honey));
        });
    }
}
