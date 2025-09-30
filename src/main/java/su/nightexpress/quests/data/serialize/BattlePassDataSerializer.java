package su.nightexpress.quests.data.serialize;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import su.nightexpress.quests.battlepass.data.BattlePassData;
import su.nightexpress.quests.battlepass.definition.BattlePassType;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

public class BattlePassDataSerializer implements JsonSerializer<BattlePassData>, JsonDeserializer<BattlePassData> {

    @Override
    public BattlePassData deserialize(JsonElement json, Type type, JsonDeserializationContext contex) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();

        int xp = object.get("xp").getAsInt();
        int level = object.get("level").getAsInt();
        boolean premium = object.get("premium").getAsBoolean();
        long expireDate = object.get("expireDate").getAsLong();
        Map<BattlePassType, Set<Integer>> claimedRewards = contex.deserialize(object.get("claimedRewards"), new TypeToken<Map<BattlePassType, Set<Integer>>>(){}.getType());

        return new BattlePassData(xp, level, premium, claimedRewards, expireDate);
    }

    @Override
    public JsonElement serialize(BattlePassData data, Type type, JsonSerializationContext contex) {
        JsonObject object = new JsonObject();
        object.addProperty("xp", data.getXP());
        object.addProperty("level", data.getLevel());
        object.addProperty("premium", data.isPremium());
        object.addProperty("expireDate", data.getExpireDate());
        object.add("claimedRewards", contex.serialize(data.getClaimedRewards()));
        return object;
    }
}
