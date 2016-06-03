package pe.cayro.pnpj.v2.serializer;

import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

import pe.cayro.pnpj.v2.model.Tracking;
import pe.cayro.pnpj.v2.util.Constants;

/**
 * Created by David on 29/02/16.
 */
public class TrackingSerializer implements JsonSerializer<Tracking> {

    private SimpleDateFormat sdf;

    @Override
    public JsonObject serialize(Tracking src, Type typeOfSrc, JsonSerializationContext context) {

        sdf  = new SimpleDateFormat(Constants.FORMAT_DATETIME_WS);

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uuid", src.getUuid());
        jsonObject.addProperty("institution_id", src.getInstitutionId());
        jsonObject.addProperty("user_id", src.getUserId());
        jsonObject.addProperty("code", src.getCode());
        jsonObject.addProperty("type", src.getType());
        jsonObject.addProperty("sent", true);
        jsonObject.addProperty("created_at", sdf.format(src.getCreatedAt()));
        jsonObject.addProperty("latitude", src.getLatitude());
        jsonObject.addProperty("longitude", src.getLongitude());

        return jsonObject;
    }
}
