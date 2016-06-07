package pe.cayro.pnpj.v2.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import pe.cayro.pnpj.v2.util.Constants;

/**
 * Created by David on 7/01/16.
 *
 * {"uuid":"3458FF8F-DB8C-5FD2-E053-0401A8C06FB8",
 *  "type":1,
 *  "code":"7575",
 *  "firstname":"VIRGINIA",
 *  "lastname":"VASCONCELLOS",
 *  "surname":"BOGGIO",
 *  "sex":"M",
 *  "specialty_id":28,
 *  "score":"A3",
 *  "prehigh":"false",
 *  "check":"true",
 *  "active":"true",
 *  "zone":0,"phone":null,
 *  "cantsurgery":0,
 *  "canthospital":0,
 *  "mail":null,
 *  "sent":"true"}
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
    private String sex;
    @SerializedName(Constants.SPECIALTY_ID)
    private int specialtyId;
    private String score;
    private boolean prehigh;
    private boolean check;
    private boolean active;
    private int zone;
    private String phone;
    private int cantsurgery;
    private int canthospital;
    private String mail;
    private boolean sent;

    @SerializedName(Constants.CREATED_AT)
    private Date createdAt;


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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getDoctorTypeId() {
        return doctorTypeId;
    }

    public void setDoctorTypeId(int doctorTypeId) {
        this.doctorTypeId = doctorTypeId;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public boolean isPrehigh() {
        return prehigh;
    }

    public void setPrehigh(boolean prehigh) {
        this.prehigh = prehigh;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
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

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public DoctorType getDoctorType() {
        return doctorType;
    }

    public void setDoctorType(DoctorType doctorType) {
        this.doctorType = doctorType;
    }
}