package su.nightexpress.quests.milestone.definition;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.Strings;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.quests.QuestsPlaceholders;

import java.util.Collections;
import java.util.List;
import java.util.function.UnaryOperator;

public class MilestoneCategory implements Writeable {

    private final String id;
    private final String name;
    private final List<String> description;
    private final NightItem icon;

    public MilestoneCategory(@NotNull String id, @NotNull String name, @NotNull List<String> description, @NotNull NightItem icon) {
        this.id = Strings.filterForVariable(id);
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    @NotNull
    public static MilestoneCategory read(@NotNull FileConfig config, @NotNull String path, @NotNull String id) {
        String name = ConfigValue.create(path + ".Name", StringUtil.capitalizeUnderscored(id)).read(config);
        List<String> desc = ConfigValue.create(path + ".Description", Collections.emptyList()).read(config);
        NightItem icon = ConfigValue.create(path + ".Icon", NightItem.fromType(Material.COMPASS)).read(config);

        return new MilestoneCategory(id, name, desc, icon);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Name", this.name);
        config.set(path + ".Description", this.description);
        config.set(path + ".Icon", this.icon);
    }

    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return QuestsPlaceholders.MILESTONE_CATEGORY.replacer(this);
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    @NotNull
    public List<String> getDescription() {
        return this.description;
    }

    @NotNull
    public NightItem getIcon() {
        return this.icon.copy();
    }
}
