package pe.cayro.pnpj.v2.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import pe.cayro.pnpj.v2.util.Constants;

/**
 * Created by David on 7/01/16.
 *
 */
public class Institution extends RealmObject {

    @PrimaryKey
    private String uuid;
    @SerializedName(Constants.INSTITUTION_TYPE)
    private int institutionTypeId;
    private String code;
    private String ruc;
    private String businessname;
    private String office;
    private String address;
    @SerializedName(Constants.INSTITUTION_ZONE)
    private int institutionZoneId;
    private String score;
    private String phone;
    private String web;
    private int userId;
    @SerializedName(Constants.CREATED_AT)
    private Date createdAt;
    private boolean active;
    private boolean check;
    private boolean edit;
    private String comment;
    private String user;
    private boolean sent;

    private InstitutionTypes institutionTypes;
    private InstitutionZone institutionZone;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getInstitutionTypeId() {
        return institutionTypeId;
    }

    public void setInstitutionTypeId(int institutionTypeId) {
        this.institutionTypeId = institutionTypeId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getInstitutionZoneId() {
        return institutionZoneId;
    }

    public void setInstitutionZoneId(int institutionZoneId) {
        this.institutionZoneId = institutionZoneId;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public InstitutionTypes getInstitutionTypes() {
        return institutionTypes;
    }

    public void setInstitutionTypes(InstitutionTypes institutionTypes) {
        this.institutionTypes = institutionTypes;
    }

    public InstitutionZone getInstitutionZone() {
        return institutionZone;
    }

    public void setInstitutionZone(InstitutionZone institutionZone) {
        this.institutionZone = institutionZone;
    }
}
