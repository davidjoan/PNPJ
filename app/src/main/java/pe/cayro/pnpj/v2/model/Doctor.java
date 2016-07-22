package pe.cayro.pnpj.v2.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import pe.cayro.pnpj.v2.util.Constants;

/**
 * Created by David on 7/01/16.
 *
 * {"uuid":"827ac554-7807-49d4-b29d-e3c06d1aedbb",
 * "type":2,"code":"46464","firstname":"HFHDHDH","lastname":"DHDJD","surname":"BDHDHDH",
 * "sex":"1","specialty_id":17,"score":null,"prehigh":"true","check":"false","active":2,
 * "zone":1,"phone":null,"cantsurgery":0,"canthospital":0,"comment":null,
 * "alert":"false","reference":"prueba","mail":null,"user_id":2,
 * "user":"SISTEMAS 1 REG","created_at":"2016-06-10 21:50:16","sent":"true"}
 */
public class Doctor extends RealmObject {

    @PrimaryKey
    private String uuid;
    @SerializedName(Constants.USER_ID)
    private int userId;
    @SerializedName(Constants.DOCTOR_TYPE_ID)
    private int doctorTypeId;
    private String code;
    private String firstname;
    private String lastname;
    private String surname;
    private int sex;
    @SerializedName(Constants.SPECIALTY_ID)
    private int specialtyId;
    private String score;
    private boolean prehigh;
    private int check;
    private boolean active;
    private int zone;
    private String phone;
    private int cantsurgery;
    private int canthospital;
    private String comment;
    private boolean alert;
    private String reference;
    private String mail;
    private String user;
    @SerializedName(Constants.CREATED_AT)
    private Date createdAt;
    private boolean sent;

    private Specialty specialty;
    private DoctorType doctorType;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getDoctorTypeId() {
        return doctorTypeId;
    }

    public void setDoctorTypeId(int doctorTypeId) {
        this.doctorTypeId = doctorTypeId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getSpecialtyId() {
        return specialtyId;
    }

    public void setSpecialtyId(int specialtyId) {
        this.specialtyId = specialtyId;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public boolean isPrehigh() {
        return prehigh;
    }

    public void setPrehigh(boolean prehigh) {
        this.prehigh = prehigh;
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getZone() {
        return zone;
    }

    public void setZone(int zone) {
        this.zone = zone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getCantsurgery() {
        return cantsurgery;
    }

    public void setCantsurgery(int cantsurgery) {
        this.cantsurgery = cantsurgery;
    }

    public int getCanthospital() {
        return canthospital;
    }

    public void setCanthospital(int canthospital) {
        this.canthospital = canthospital;
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

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }

    public DoctorType getDoctorType() {
        return doctorType;
    }

    public void setDoctorType(DoctorType doctorType) {
        this.doctorType = doctorType;
    }
}