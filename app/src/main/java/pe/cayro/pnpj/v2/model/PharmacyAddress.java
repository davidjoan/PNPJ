package pe.cayro.pnpj.v2.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dtataje on 27/07/2016.
 */
public class PharmacyAddress extends RealmObject {

    @PrimaryKey
    private int id;
    private int idpharmacy;
    private int idubigeo;
    private String code;
    private String name;
    private String address;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdpharmacy() {
        return idpharmacy;
    }

    public void setIdpharmacy(int idpharmacy) {
        this.idpharmacy = idpharmacy;
    }

    public int getIdubigeo() {
        return idubigeo;
    }

    public void setIdubigeo(int idubigeo) {
        this.idubigeo = idubigeo;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
