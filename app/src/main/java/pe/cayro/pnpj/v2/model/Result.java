package pe.cayro.pnpj.v2.model;

import com.google.gson.annotations.SerializedName;

import pe.cayro.pnpj.v2.util.Constants;

/**
 * Created by David on 29/02/16.
 */
public class Result {

    private String uuid;
    @SerializedName(Constants.ID_RESULT)
    private String idResult;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getIdResult() {
        return idResult;
    }

    public void setIdResult(String idResult) {
        this.idResult = idResult;
    }
}
