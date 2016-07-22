package pe.cayro.pnpj.v2.model;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by David on 12/01/16.
 */
public class Record extends RealmObject {

    @PrimaryKey
    private String uuid;
    private int userId;
    private String patientUuid;
    private String doctorUuid;
    private String institutionId;
    private int agentId;
    private int ubigeoId;
    private int code;
    private int attentionTypeId;
    private Date recordDate;
    private String voucher;
    private String ruc;
    private Date saleDate;
    private String serial;
    private String institutionOriginId;
    private boolean sent;
    private boolean active;
    private Date createdAt;

    private User user;
    private Patient patient;
    private Doctor doctor;
    private AttentionType attentionType;
    private Institution institution;
    private Agent agent;
    private Institution institutionOrigin;
    private Ubigeo ubigeo;

    private RealmList<RecordDetail> recordDetails;

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

    public String getPatientUuid() {
        return patientUuid;
    }

    public void setPatientUuid(String patientUuid) {
        this.patientUuid = patientUuid;
    }

    public String getDoctorUuid() {
        return doctorUuid;
    }

    public void setDoctorUuid(String doctorUuid) {
        this.doctorUuid = doctorUuid;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getAttentionTypeId() {
        return attentionTypeId;
    }

    public void setAttentionTypeId(int attentionTypeId) {
        this.attentionTypeId = attentionTypeId;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    public String getVoucher() {
        return voucher;
    }

    public void setVoucher(String voucher) {
        this.voucher = voucher;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public AttentionType getAttentionType() {
        return attentionType;
    }

    public void setAttentionType(AttentionType attentionType) {
        this.attentionType = attentionType;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public int getAgentId() {
        return agentId;
    }

    public void setAgentId(int agentId) {
        this.agentId = agentId;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public String getInstitutionOriginId() {
        return institutionOriginId;
    }

    public void setInstitutionOriginId(String institutionOriginId) {
        this.institutionOriginId = institutionOriginId;
    }

    public Institution getInstitutionOrigin() {
        return institutionOrigin;
    }

    public void setInstitutionOrigin(Institution institutionOrigin) {
        this.institutionOrigin = institutionOrigin;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public RealmList<RecordDetail> getRecordDetails() {
        return recordDetails;
    }

    public void setRecordDetails(RealmList<RecordDetail> recordDetails) {
        this.recordDetails = recordDetails;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getUbigeoId() {
        return ubigeoId;
    }

    public void setUbigeoId(int ubigeoId) {
        this.ubigeoId = ubigeoId;
    }

    public Ubigeo getUbigeo() {
        return ubigeo;
    }

    public void setUbigeo(Ubigeo ubigeo) {
        this.ubigeo = ubigeo;
    }
}
