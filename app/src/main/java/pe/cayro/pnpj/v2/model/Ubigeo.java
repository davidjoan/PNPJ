package pe.cayro.pnpj.v2.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by David on 21/01/16.
 *
 * {"id":501,"code":90113,"name":"NUEVO OCCORO","province":"HUANCAVELICA"}
 */
public class Ubigeo extends RealmObject {

    @PrimaryKey
    private int id;
    private String code;
    private String name;
    private String province;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}
