package pe.cayro.pnpj.v2.serializer;

import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

import pe.cayro.pnpj.v2.model.RecordDetail;
import pe.cayro.pnpj.v2.model.SpecialMoveDetail;
import pe.cayro.pnpj.v2.util.Constants;

/**
 * Created by David on 29/02/16.
 */
public class SpecialMoveDetailSerializer implements JsonSerializer<SpecialMoveDetail> {

    private SimpleDateFormat sdf;

    @Override
    public JsonObject serialize(SpecialMoveDetail src, Type typeOfSrc, JsonSerializationContext context) {

        sdf  = new SimpleDateFormat(Constants.FORMAT_DATETIME_WS);

        final JsonObject jsonObject = new JsonObject();
        
        jsonObject.addProperty("uuid", src.getUuid());
        jsonObject.addProperty("record_uuid", src.getSpecialMoveUuid());
        jsonObject.addProperty("product_id", src.getProductId());
        jsonObject.addProperty("qty", src.getQty());
        jsonObject.addProperty("sent", Boolean.TRUE);
        jsonObject.addProperty("active", src.isActive());
        if(src.getCreatedAt() != null) {
            jsonObject.addProperty("created_at", sdf.format(src.getCreatedAt()));
        }


        return jsonObject;
    }
}
