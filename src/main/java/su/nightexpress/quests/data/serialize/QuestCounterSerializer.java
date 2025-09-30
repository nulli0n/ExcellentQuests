package su.nightexpress.quests.data.serialize;

import com.google.gson.*;
import su.nightexpress.quests.quest.data.QuestCounter;

import java.lang.reflect.Type;

public class QuestCounterSerializer implements JsonSerializer<QuestCounter>, JsonDeserializer<QuestCounter> {

    @Override
    public QuestCounter deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();

        int required = object.get("required").getAsInt();
        int completed = object.get("completed").getAsInt();
        double unitWorth = object.get("unitWorth").getAsDouble();

        return new QuestCounter(required, completed, unitWorth);
    }

    @Override
    public JsonElement serialize(QuestCounter counter, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        object.addProperty("required", counter.getRequired());
        object.addProperty("completed", counter.getCompleted());
        object.addProperty("unitWorth", counter.getUnitWorth());

        return object;
    }
}
