package su.nightexpress.quests.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.db.AbstractUserDataManager;
import su.nightexpress.nightcore.db.sql.column.Column;
import su.nightexpress.nightcore.db.sql.column.ColumnType;
import su.nightexpress.nightcore.db.sql.query.impl.DeleteQuery;
import su.nightexpress.nightcore.db.sql.query.impl.SelectQuery;
import su.nightexpress.nightcore.db.sql.query.type.ValuedQuery;
import su.nightexpress.nightcore.db.sql.util.WhereOperator;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.battlepass.definition.BattlePassSeason;
import su.nightexpress.quests.data.serialize.MilestoneDataSerializer;
import su.nightexpress.quests.data.serialize.QuestCounterSerializer;
import su.nightexpress.quests.data.serialize.QuestDataSerializer;
import su.nightexpress.quests.data.serialize.BattlePassDataSerializer;
import su.nightexpress.quests.quest.data.QuestCounter;
import su.nightexpress.quests.milestone.data.MilestoneData;
import su.nightexpress.quests.quest.data.QuestData;
import su.nightexpress.quests.battlepass.data.BattlePassData;
import su.nightexpress.quests.user.QuestUser;

import java.sql.ResultSet;
import java.util.List;
import java.util.function.Function;

public class DataHandler extends AbstractUserDataManager<QuestsPlugin, QuestUser> {

    static final Gson GSON = new GsonBuilder().setPrettyPrinting()
        .registerTypeAdapter(BattlePassData.class, new BattlePassDataSerializer())
        .registerTypeAdapter(QuestCounter.class, new QuestCounterSerializer())
        .registerTypeAdapter(QuestData.class, new QuestDataSerializer())
        .registerTypeAdapter(MilestoneData.class, new MilestoneDataSerializer())
        .create();

    static final Column COLUMN_BATTLE_PASS_DATA = Column.of("battlePassData", ColumnType.STRING);
    static final Column COLUMN_QUEST_DATA       = Column.of("questData", ColumnType.STRING);
    static final Column COLUMN_MILESTONE_DATA   = Column.of("milestoneData", ColumnType.STRING);
    static final Column COLUMN_NEW_QUESTS_DATE  = Column.of("newQuestsDate", ColumnType.LONG);

    static final Column COLUMN_BP_ID         = Column.of("seasonId", ColumnType.STRING);
    static final Column COLUMN_BP_NAME       = Column.of("name", ColumnType.STRING);
    static final Column COLUMN_BP_START_DATE = Column.of("startDate", ColumnType.LONG);
    static final Column COLUMN_BP_END_DATE   = Column.of("endDate", ColumnType.LONG);
    static final Column COLUMN_BP_EXPIRE_DATE   = Column.of("expireDate", ColumnType.LONG);
    static final Column COLUMN_BP_ACTIVE     = Column.of("active", ColumnType.BOOLEAN);

    static final String BP_TABLE = "bp_season";

    public DataHandler(@NotNull QuestsPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.createTable(BP_TABLE, Lists.newList(
            COLUMN_BP_ID,
            COLUMN_BP_NAME,
            COLUMN_BP_START_DATE,
            COLUMN_BP_END_DATE,
            COLUMN_BP_EXPIRE_DATE,
            COLUMN_BP_ACTIVE
        ));
    }

    @Override
    @NotNull
    protected Function<ResultSet, QuestUser> createUserFunction() {
        return DataQueries.USER_LOADER;
    }

    @Override
    protected void addUpsertQueryData(@NotNull ValuedQuery<?, QuestUser> query) {
        query.setValue(COLUMN_NEW_QUESTS_DATE, user -> String.valueOf(user.getNewQuestsDate()));
        query.setValue(COLUMN_BATTLE_PASS_DATA, user -> GSON.toJson(user.getBattlePassData()));
        query.setValue(COLUMN_QUEST_DATA, user -> GSON.toJson(user.getQuestData()));
        query.setValue(COLUMN_MILESTONE_DATA, user -> GSON.toJson(user.getMilestoneDataMap()));
    }

    @Override
    protected void addSelectQueryData(@NotNull SelectQuery<QuestUser> query) {
        query.column(COLUMN_NEW_QUESTS_DATE);
        query.column(COLUMN_BATTLE_PASS_DATA);
        query.column(COLUMN_QUEST_DATA);
        query.column(COLUMN_MILESTONE_DATA);
    }

    @Override
    protected void addTableColumns(@NotNull List<Column> columns) {
        columns.add(COLUMN_NEW_QUESTS_DATE);
        columns.add(COLUMN_BATTLE_PASS_DATA);
        columns.add(COLUMN_QUEST_DATA);
        columns.add(COLUMN_MILESTONE_DATA);
    }

    @NotNull
    public List<BattlePassSeason> loadBattlePassSeasons() {
        return this.select(BP_TABLE, DataQueries.SEASON_LOADER, SelectQuery::all);
    }

    public void insertBattlePassSeason(@NotNull BattlePassSeason season) {
        this.insert(BP_TABLE, DataQueries.SEASON_INSERT, season);
    }

    public void saveBattlePassSeason(@NotNull BattlePassSeason season) {
        this.update(BP_TABLE, DataQueries.SEASON_UPDATE, season);
    }

    public void removeBattlePassSeason(@NotNull BattlePassSeason season) {
        this.delete(BP_TABLE, new DeleteQuery<BattlePassSeason>().where(COLUMN_BP_ID, WhereOperator.EQUAL, passSeason -> passSeason.getId().toString()), season);
    }

    @Override
    @NotNull
    protected GsonBuilder registerAdapters(@NotNull GsonBuilder builder) {
        return builder;
    }

    @Override
    public void onSynchronize() {
        // TODO
    }
}
