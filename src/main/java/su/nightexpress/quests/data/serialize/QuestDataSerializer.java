package su.nightexpress.quests.data.serialize;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import su.nightexpress.quests.quest.data.QuestCounter;
import su.nightexpress.quests.quest.data.QuestData;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class QuestDataSerializer implements JsonSerializer<QuestData>, JsonDeserializer<QuestData> {

    @Override
    public QuestData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();

        UUID id = UUID.fromString(object.get("id").getAsString());
        String missionId = object.get("questId").getAsString();

        Map<String, QuestCounter> objectives = new HashMap<>();
        JsonObject objectivesRoot = object.get("objectives").getAsJsonObject();
        objectivesRoot.asMap().forEach((fullName, counterJson) -> {
            QuestCounter counter = context.deserialize(counterJson, QuestCounter.class);
            objectives.put(fullName, counter);
        });

        Set<String> rewardIds = context.deserialize(object.get("rewardIds"), new TypeToken<Set<String>>(){}.getType());

        double scale = object.get("scale").getAsDouble();
        int xpReward = object.get("xpReward").getAsInt();
        boolean active = object.get("active").getAsBoolean();
        long expireDate = object.get("expireDate").getAsLong();

        return new QuestData(id, missionId, objectives, rewardIds, scale, xpReward, active, expireDate);
    }

    @Override
    public JsonElement serialize(QuestData data, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        object.addProperty("id", data.getId().toString());
        object.addProperty("questId", data.getQuestId());

        JsonObject objectivesRoot = new JsonObject();
        data.getObjectiveCounterMap().forEach((fullName, counter) -> {
            objectivesRoot.add(fullName, context.serialize(counter, QuestCounter.class));
        });

        object.add("objectives", objectivesRoot);
        object.add("rewardIds", context.serialize(data.getRewardIds()));
        object.addProperty("scale", data.getScale());
        object.addProperty("xpReward", data.getXPReward());
        object.addProperty("active", data.isActive());
        object.addProperty("expireDate", data.getExpireDate());

        return object;
    }
}
