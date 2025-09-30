package su.nightexpress.quests.task.listener.type;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.config.Config;
import su.nightexpress.quests.task.adapter.AdapterFamily;
import su.nightexpress.quests.task.TaskManager;
import su.nightexpress.quests.task.TaskType;
import su.nightexpress.quests.task.listener.TaskListener;

import java.util.Set;

public class BlockBreakTaskListener extends TaskListener<Block, AdapterFamily<Block>> {

    private static final Set<Material> TALL_BLOCKS = Lists.newSet(Material.BAMBOO, Material.SUGAR_CANE);

    public BlockBreakTaskListener(@NotNull QuestsPlugin plugin, @NotNull TaskManager manager, @NotNull TaskType<Block, AdapterFamily<Block>> taskType) {
        super(plugin, manager, taskType);
    }

    private boolean isTallBlock(@NotNull Material material) {
        return TALL_BLOCKS.contains(material);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTaskBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!this.manager.canDoTasks(player)) return;

        Block block = event.getBlock();

        this.handleBreak(player, block);
    }

    private void handleBreak(@NotNull Player player, @NotNull Block block) {
        if (!Config.ANTI_ABUSE_COUNT_PLAYER_BLOCKS.get() && this.manager.isPlayerBlock(block)) return;

        BlockData blockData = block.getBlockData();
        boolean isTall = this.isTallBlock(block.getType());

        // Do not give XP for ungrowth plants.
        if (!isTall && blockData instanceof Ageable age) {
            if (age.getAge() < age.getMaximumAge()) {
                return;
            }
        }

        this.progressQuests(player, block);

        if (isTall) {
            Block above = block.getRelative(BlockFace.UP);
            if (above.getType() == block.getType()) {
                this.handleBreak(player, above);
            }
        }
    }
}
