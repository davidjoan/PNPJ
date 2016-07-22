package pe.cayro.pnpj.v2.serializer;

import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

import pe.cayro.pnpj.v2.model.Institution;
import pe.cayro.pnpj.v2.util.Constants;

/**
 * Created by David on 29/02/16.
 *
 */

public class InstitutionSerializer implements JsonSerializer<Institution> {

    private SimpleDateFormat sdf;

    @Override
    public JsonObject serialize(Institution src, Type typeOfSrc, JsonSerializationContext context) {

        sdf    = new SimpleDateFormat(Constants.FORMAT_DATETIME_WS);
        
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uuid", src.getUuid());
        jsonObject.addProperty("type", src.getInstitutionTypeId());
        jsonObject.addProperty("user_id", src.getUserId());
        jsonObject.addProperty("code", src.getCode());
        jsonObject.addProperty("ruc", src.getRuc());
        jsonObject.addProperty("businessname", src.getBusinessname());
        jsonObject.addProperty("office", src.getOffice());
        jsonObject.addProperty("address", src.getAddress());
        jsonObject.addProperty("zone", src.getInstitutionZoneId());
        jsonObject.addProperty("score", src.getScore());
        jsonObject.addProperty("phone", src.getPhone());
        jsonObject.addProperty("web", src.getWeb());
        jsonObject.addProperty("user_id", src.getUserId());


        if(src.getCreatedAt() != null){
            jsonObject.addProperty("created_at", sdf.format(src.getCreatedAt()));
        }

        jsonObject.addProperty("active", src.isActive());
        jsonObject.addProperty("check", src.isCheck());
        jsonObject.addProperty("edit", src.isEdit());

        jsonObject.addProperty("comment", src.getComment());
        jsonObject.addProperty("user", src.getUser());

        jsonObject.addProperty("sent", true);

        return jsonObject;
    }
}
