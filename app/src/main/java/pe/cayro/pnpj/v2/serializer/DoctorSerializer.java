package pe.cayro.pnpj.v2.serializer;

import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

import pe.cayro.pnpj.v2.model.Doctor;
import pe.cayro.pnpj.v2.util.Constants;

/**
 * Created by David on 29/02/16.
 */
public class DoctorSerializer implements JsonSerializer<Doctor> {

    private SimpleDateFormat sdf;

    @Override
    public JsonObject serialize(Doctor src, Type typeOfSrc, JsonSerializationContext context) {

        sdf    = new SimpleDateFormat(Constants.FORMAT_DATETIME_WS);

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uuid", src.getUuid());
        jsonObject.addProperty("type", src.getDoctorTypeId());
        jsonObject.addProperty("user_id", src.getUserId());
        jsonObject.addProperty("code", src.getCode());
        jsonObject.addProperty("firstname", src.getFirstname());
        jsonObject.addProperty("lastname", src.getLastname());
        jsonObject.addProperty("surname", src.getSurname());
        jsonObject.addProperty("specialty_id", src.getSpecialtyId());
        jsonObject.addProperty("score", src.getScore());
        jsonObject.addProperty("prehigh", true);
        jsonObject.addProperty("check", src.getCheck());
        jsonObject.addProperty("zone", src.getZone());
        jsonObject.addProperty("phone", src.getPhone());
        jsonObject.addProperty("cantsurgery", src.getCantsurgery());
        jsonObject.addProperty("canthospital", src.getCanthospital());
        jsonObject.addProperty("mail", src.getMail());
        jsonObject.addProperty("sex", src.getSex());

        jsonObject.addProperty("active", src.isActive());
        jsonObject.addProperty("sent", true);

        if(src.getCreatedAt() != null){
            jsonObject.addProperty("created_at", sdf.format(src.getCreatedAt()));
        }

        return jsonObject;
    }
}
