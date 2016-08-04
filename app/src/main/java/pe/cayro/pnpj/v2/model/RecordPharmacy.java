package pe.cayro.pnpj.v2.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dtataje on 27/07/2016.
 *
 * [{"uuid":"12345678-1234-234-","type":1,"ruc":"12345678987","businessname":"empresa de prueba",
 * "typeaddress":1,"address":"angamos","idzone":50,"score":"A1","numberaddress":"130",
 * "idpharmacydetal":null,"user_id":"1","user":"DESARROLLO","created_at":"2016-07-27 19:51:51",
 * "active":"true","check":1,"comment":null,"alert":"false","longitude":null,"latitude":null,"sent":"true"}]
 */
public class RecordPharmacy extends RealmObject {

    @PrimaryKey
    private String uuid;
    private int type;
    private String ruc;
    private String businessname;
    private int typeaddress;
    private String address;
    private int idzone;
    private String score;
    private String numberaddress;
    private int idpharmacydetail;
    private int user_id;
    private String user;
    private String created_at;
    private boolean active;
    private int check;
    private String comment;
    private boolean alert;
    private String longitude;
    private String latitude;
    private boolean sent;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getBusinessname() {
        return businessname;
    }

    public void setBusinessname(String businessname) {
        this.businessname = businessname;
    }

    public int getTypeaddress() {
        return typeaddress;
    }

    public void setTypeaddress(int typeaddress) {
        this.typeaddress = typeaddress;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getIdzone() {
        return idzone;
    }

    public void setIdzone(int idzone) {
        this.idzone = idzone;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getNumberaddress() {
        return numberaddress;
    }

    public void setNumberaddress(String numberaddress) {
        this.numberaddress = numberaddress;
    }

    public int getIdpharmacydetail() {
        return idpharmacydetail;
    }

    public void setIdpharmacydetail(int idpharmacydetail) {
        this.idpharmacydetail = idpharmacydetail;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isAlert() {
        return alert;
    }

    public void setAlert(boolean alert) {
        this.alert = alert;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }
}
