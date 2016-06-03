package pe.cayro.pnpj.v2.serializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

import pe.cayro.pnpj.v2.model.Record;
import pe.cayro.pnpj.v2.model.RecordDetail;
import pe.cayro.pnpj.v2.model.SpecialMove;
import pe.cayro.pnpj.v2.model.SpecialMoveDetail;
import pe.cayro.pnpj.v2.util.Constants;

/**
 * Created by David on 29/02/16.
 */
public class SpecialMoveSerializer implements JsonSerializer<SpecialMove> {

    private SimpleDateFormat sdf;

    @Override
    public JsonObject serialize(SpecialMove src, Type typeOfSrc, JsonSerializationContext context) {

        sdf  = new SimpleDateFormat(Constants.FORMAT_DATETIME_WS);

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uuid", src.getUuid());
        jsonObject.addProperty("mov_type_id", src.getTypeMovement().getId());
        jsonObject.addProperty("record_date", sdf.format(src.getRecordDate()));
        jsonObject.addProperty("reason_id", src.getTypeMovement().getType());

        if(src.getComment() != null) {
            jsonObject.addProperty("comment", src.getComment());
        }
        jsonObject.addProperty("user_id", src.getUserId());
        jsonObject.addProperty("created_at", sdf.format(src.getCreatedAt()));
        jsonObject.addProperty("active", src.isActive());
        jsonObject.addProperty("sent", true);


        SpecialMoveDetailSerializer specialMoveDetailSerializer = new SpecialMoveDetailSerializer();

        JsonArray specialMoveDetails = new JsonArray();

        for(SpecialMoveDetail specialMoveDetail : src.getSpecialMoveDetails()) {
            specialMoveDetails.add(specialMoveDetailSerializer.serialize(specialMoveDetail, null, null));
        }

        jsonObject.add("record_details", specialMoveDetails);

        return jsonObject;
    }
}
