package su.nightexpress.quests.data.serialize;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import su.nightexpress.quests.milestone.data.MilestoneData;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

public class MilestoneDataSerializer implements JsonSerializer<MilestoneData>, JsonDeserializer<MilestoneData> {

    @Override
    public MilestoneData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();

        String milestoneId = object.get("milestoneId").getAsString();
        Map<String, Integer> progress = context.deserialize(object.get("progress"), new TypeToken<Map<String, Integer>>(){}.getType());
        Set<Integer> completedLevels = context.deserialize(object.get("completedLevels"), new TypeToken<Set<Integer>>(){}.getType());

        //int level = object.get("level").getAsInt();
        //boolean completed = object.get("completed").getAsBoolean();

        return new MilestoneData(milestoneId, progress, completedLevels);
    }

    @Override
    public JsonElement serialize(MilestoneData data, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        object.addProperty("milestoneId", data.getMilestoneId());
        object.add("progress", context.serialize(data.getProgress()));
        object.add("completedLevels", context.serialize(data.getCompletedLevels()));
        /*object.addProperty("level", data.getLevel());
        object.addProperty("completed", data.isCompleted());*/

        return object;
    }
}
