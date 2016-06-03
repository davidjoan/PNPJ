package pe.cayro.pnpj.v2.serializer;

import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

import pe.cayro.pnpj.v2.model.Patient;
import pe.cayro.pnpj.v2.util.Constants;

/**
 * Created by David on 29/02/16.
 */
public class PatientSerializer implements JsonSerializer<Patient> {

    private SimpleDateFormat sdf;

    @Override
    public JsonObject serialize(Patient src, Type typeOfSrc, JsonSerializationContext context) {

        sdf    = new SimpleDateFormat(Constants.FORMAT_DATETIME_WS);

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uuid", src.getUuid());
        jsonObject.addProperty("user_id", src.getUserId());
        jsonObject.addProperty("ubigeo_id", src.getUbigeoId());
        jsonObject.addProperty("code", src.getCode());
        jsonObject.addProperty("firstname", src.getFirstname());
        jsonObject.addProperty("lastname", src.getLastname());
        jsonObject.addProperty("surname", src.getSurname());
        jsonObject.addProperty("address", src.getAddress());
        jsonObject.addProperty("phone", src.getPhone());
        jsonObject.addProperty("email", src.getEmail());
        jsonObject.addProperty("active", src.isActive());
        jsonObject.addProperty("sent", true);
        jsonObject.addProperty("created_at", sdf.format(src.getCreatedAt()));


        return jsonObject;
    }
}
