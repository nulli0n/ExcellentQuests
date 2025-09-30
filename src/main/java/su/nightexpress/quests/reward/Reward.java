package su.nightexpress.quests.reward;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.placeholder.Replacer;
import su.nightexpress.quests.QuestsPlaceholders;

import java.util.*;
import java.util.function.Function;

public class Reward implements Writeable {

    private final String                name;
    private final List<String>          commands;
    private final Map<String, Variable> variables;

    public Reward(@NotNull String name, @NotNull List<String> commands, @NotNull Map<String, Variable> variables) {
        this.name = name;
        this.commands = commands;
        this.variables = variables;
    }

    @NotNull
    public static Builder builder(@NotNull String name) {
        return new Builder(name);
    }

    @NotNull
    public static Reward read(@NotNull FileConfig config, @NotNull String path) {
        Map<String, Variable> variables = new HashMap<>();
        config.getSection(path + ".Variables").forEach(sId -> {
            Variable variable = Variable.read(config, path + ".Variables." + sId);
            variables.put(LowerCase.INTERNAL.apply(sId), variable);
        });

        String name = ConfigValue.create(path + ".Name", "null").read(config);
        List<String> commands = ConfigValue.create(path + ".Commands", Collections.emptyList()).read(config);

        return new Reward(name, commands, variables);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.remove(path + ".Variables");
        this.variables.forEach((name, variable) -> {
            config.set(path + ".Variables." + name, variable);
        });
        config.set(path + ".Name", this.name);
        config.set(path + ".Commands", this.commands);
    }

    @NotNull
    public Replacer variableReplacer(int units, int level, double scale, @NotNull Function<Double, String> formatter) {
        Replacer replacer = Replacer.create();
        this.variables.forEach((name, variable) -> {
            replacer.replace(QuestsPlaceholders.VARIABLE.apply(name), () -> formatter.apply(variable.getValue(units, level, scale)));
        });
        return replacer;
    }

    @NotNull
    public String getName(int units, int level, double scale) {
        return this.variableReplacer(units, level, scale, NumberUtil::format).apply(this.name);
    }

    public void runCommands(@NotNull Player player, int units, int level, double scale) {
        Players.dispatchCommands(player, this.variableReplacer(units, level, scale, String::valueOf).apply(this.commands));
    }

    public record Variable(double base, double unitBonus, double[] levelBonus) implements Writeable {

        @NotNull
        public static Variable of(double base, double unitBonus, double... levelBonus) {
            return new Variable(base, unitBonus, levelBonus);
        }

        @NotNull
        public static Variable read(@NotNull FileConfig config, @NotNull String path) {
            config.setInlineComments(path + ".Base", "Base value.");
            config.setInlineComments(path + ".UnitBonus", "Bonus value per objective unit. (Quests and Milestones only)");
            config.setInlineComments(path + ".LevelBonus", "Bonus value for specific level(s). [LV1, LV2, LV3, ..., LV99] (Milestones and Battle Pass only)");

            double base = ConfigValue.create(path + ".Base", 0).read(config);
            double unitBonus = ConfigValue.create(path + ".UnitBonus", 0).read(config);
            String levelsRaw = ConfigValue.create(path + ".LevelBonus", "[]").read(config);

            double[] levelBonus = Arrays.stream(levelsRaw.substring(1, levelsRaw.length() -1).split(","))
                .map(String::trim)
                .mapToDouble(NumberUtil::getDoubleAbs)
                .toArray();

            return new Variable(base, unitBonus, levelBonus);
        }

        @Override
        public void write(@NotNull FileConfig config, @NotNull String path) {
            config.set(path + ".Base", this.base);
            config.set(path + ".UnitBonus", this.unitBonus);
            config.set(path + ".LevelBonus", Arrays.toString(this.levelBonus));
        }

        public double getValue(int units, int level, double scale) {
            int levelIndex = level - 1;
            double levelBonus = levelIndex >= 0 && levelIndex < this.levelBonus.length ? this.levelBonus[levelIndex] : 0;

            return (this.base + levelBonus + (this.unitBonus * units)) * scale;
        }
    }

    public static class Builder {

        private final String                name;
        private final List<String>          commands;
        private final Map<String, Variable> variables;

        public Builder(@NotNull String name) {
            this.name = name;
            this.commands = new ArrayList<>();
            this.variables = new HashMap<>();
        }

        @NotNull
        public Reward build() {
            return new Reward(this.name, this.commands, this.variables);
        }

        @NotNull
        public Builder commands(@NotNull String... commands) {
            this.commands.addAll(Arrays.asList(commands));
            return this;
        }

        @NotNull
        public Builder variable(@NotNull String name, double base, double unitBonus, double... levelBonus) {
            this.variables.put(LowerCase.INTERNAL.apply(name), Variable.of(base, unitBonus, levelBonus));
            return this;
        }
    }
}
