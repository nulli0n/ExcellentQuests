package su.nightexpress.quests.reward;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.milestone.definition.Milestone;
import su.nightexpress.quests.quest.definition.Quest;

import java.util.*;

public class RewardManager extends AbstractManager<QuestsPlugin> {

    private static final String FILE_NAME = "rewards.yml";

    private final Map<String, Reward> rewardByIdMap;

    public RewardManager(@NotNull QuestsPlugin plugin) {
        super(plugin);
        this.rewardByIdMap = new HashMap<>();
    }

    @Override
    protected void onLoad() {
        this.loadRewards();
    }

    @Override
    protected void onShutdown() {
        this.rewardByIdMap.clear();
    }

    public void loadRewards() {
        FileConfig config = FileConfig.loadOrExtract(this.plugin, FILE_NAME);
        String path = "Rewards";

        if (!config.contains(path)) {
            RewardDefaults.createRewards().forEach((id, reward) -> config.set(path + "." + id, reward));
        }

        config.getSection(path).forEach(sId -> {
            Reward reward = Reward.read(config, path + "." + sId);
            this.rewardByIdMap.put(LowerCase.INTERNAL.apply(sId), reward);
        });
        config.saveChanges();

        this.plugin.info("Loaded " + this.rewardByIdMap.size() + " rewards.");
    }

    @NotNull
    public Map<String, Reward> getRewardByIdMap() {
        return this.rewardByIdMap;
    }

    @NotNull
    public Set<Reward> getRewards() {
        return new HashSet<>(this.rewardByIdMap.values());
    }

    @Nullable
    public Reward getRewardById(@NotNull String id) {
        return this.rewardByIdMap.get(id);
    }

    @NotNull
    public List<Reward> getQuestRewards(@NotNull Quest quest) {
        return this.parseRewards(quest.getRewards());
    }

    @NotNull
    public List<Reward> getMilestoneRewards(@NotNull Milestone milestone) {
        return this.parseRewards(milestone.getRewards());
    }

    @NotNull
    public List<Reward> parseRewards(@NotNull Collection<String> rewardIds) {
        return rewardIds.stream().map(this::getRewardById).filter(Objects::nonNull).toList();
    }
}
