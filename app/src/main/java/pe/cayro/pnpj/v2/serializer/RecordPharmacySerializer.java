package pe.cayro.pnpj.v2.serializer;

import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

import pe.cayro.pnpj.v2.model.RecordPharmacy;
import pe.cayro.pnpj.v2.util.Constants;

/**
 * Created by David on 29/02/16.
 *
 *
 *
 /*
 * {
 "uuid":"12345678-1234-1234-1234-123456765",
 "type":1,
 "ruc":"12345678912",
 "businessname":"farmacia de prueba",
 "typeaddress":"Av.",
 "address":"angamos",
 "idzone":10,
 "score":"",
 "numberaddress":"132",
 "idpharmacydetal":105,
 "user_id":1,
 "created_at":"2016-06-11 15:17:59",
 "active":true,
 "check":1,
 "alert":false,
 "longitude":"-12.822727272",
 "latitude":"-75.28282828",
 "comment":"",
 "user":"nada",
 "sent":true
 }

 *
 */

public class RecordPharmacySerializer implements JsonSerializer<RecordPharmacy> {

    private SimpleDateFormat sdf;

    @Override
    public JsonObject serialize(RecordPharmacy src, Type typeOfSrc, JsonSerializationContext context) {

        sdf    = new SimpleDateFormat(Constants.FORMAT_DATETIME_WS);
        
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uuid", src.getUuid());
        jsonObject.addProperty("type", src.getType());
        jsonObject.addProperty("ruc", src.getRuc());
        jsonObject.addProperty("businessname", src.getBusinessname());
        jsonObject.addProperty("typeaddress", src.getTypeaddress());
        jsonObject.addProperty("address", src.getAddress());
        jsonObject.addProperty("idzone", src.getIdzone());
        jsonObject.addProperty("score","");
        jsonObject.addProperty("numberaddress", src.getNumberaddress());
        jsonObject.addProperty("idpharmacydetal", src.getIdpharmacydetail());
        jsonObject.addProperty("user_id", src.getUser_id());

        jsonObject.addProperty("created_at", src.getCreated_at());

        jsonObject.addProperty("active", src.isActive());
        jsonObject.addProperty("check", src.getCheck());
        jsonObject.addProperty("alert", src.isAlert());

        jsonObject.addProperty("latitude", src.getLatitude());
        jsonObject.addProperty("longitude", src.getLongitude());

        jsonObject.addProperty("comment", src.getComment());
        jsonObject.addProperty("user", src.getUser());

        jsonObject.addProperty("sent", true);



        return jsonObject;


    }
}
