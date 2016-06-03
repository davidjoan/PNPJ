package pe.cayro.pnpj.v2.serializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

import pe.cayro.pnpj.v2.model.Record;
import pe.cayro.pnpj.v2.model.RecordDetail;
import pe.cayro.pnpj.v2.util.Constants;

/**
 * Created by David on 29/02/16.
 */
public class RecordSerializer implements JsonSerializer<Record> {

    private SimpleDateFormat sdf;

    @Override
    public JsonObject serialize(Record src, Type typeOfSrc, JsonSerializationContext context) {

        sdf  = new SimpleDateFormat(Constants.FORMAT_DATETIME_WS);

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uuid", src.getUuid());
        jsonObject.addProperty("user_id", src.getUserId());
        jsonObject.addProperty("patient_uuid", src.getPatientUuid());
        jsonObject.addProperty("doctor_uuid", src.getDoctorUuid());
        jsonObject.addProperty("institution_id", src.getInstitutionId());
        jsonObject.addProperty("agent_id", src.getAgentId());
        jsonObject.addProperty("ubigeo_id", src.getUbigeoId());
        jsonObject.addProperty("code", src.getCode());
        jsonObject.addProperty("attention_type_id", src.getAttentionTypeId());
        jsonObject.addProperty("record_date", sdf.format(src.getRecordDate()));
        jsonObject.addProperty("voucher", src.getVoucher());
        jsonObject.addProperty("ruc", src.getRuc());
        jsonObject.addProperty("sale_date", sdf.format(src.getSaleDate()));
        jsonObject.addProperty("serial", src.getSerial());
        jsonObject.addProperty("institution_origin_id", src.getInstitutionOriginId());
        jsonObject.addProperty("sent", true);
        jsonObject.addProperty("active", src.isActive());
        jsonObject.addProperty("created_at", sdf.format(src.getCreatedAt()));

        RecordDetailSerializer recordDetailSerializer = new RecordDetailSerializer();

        JsonArray recordDetails = new JsonArray();

        for(RecordDetail recordDetail : src.getRecordDetails()) {
            recordDetails.add(recordDetailSerializer.serialize(recordDetail, null, null));
        }

        jsonObject.add("record_details", recordDetails);

        return jsonObject;
    }
}
