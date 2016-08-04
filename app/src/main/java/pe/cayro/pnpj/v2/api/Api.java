package pe.cayro.pnpj.v2.api;

import com.google.gson.JsonObject;

import java.util.List;

import pe.cayro.pnpj.v2.model.Agent;
import pe.cayro.pnpj.v2.model.AttentionType;
import pe.cayro.pnpj.v2.model.Doctor;
import pe.cayro.pnpj.v2.model.DoctorType;
import pe.cayro.pnpj.v2.model.DoctorsCloseUp;
import pe.cayro.pnpj.v2.model.Institution;
import pe.cayro.pnpj.v2.model.InstitutionTypes;
import pe.cayro.pnpj.v2.model.InstitutionZone;
import pe.cayro.pnpj.v2.model.Patient;
import pe.cayro.pnpj.v2.model.Pharmacy;
import pe.cayro.pnpj.v2.model.PharmacyAddress;
import pe.cayro.pnpj.v2.model.Product;
import pe.cayro.pnpj.v2.model.RecordPharmacy;
import pe.cayro.pnpj.v2.model.Result;
import pe.cayro.pnpj.v2.model.Specialty;
import pe.cayro.pnpj.v2.model.TypeMovement;
import pe.cayro.pnpj.v2.model.Ubigeo;
import pe.cayro.pnpj.v2.model.User;
import pe.cayro.pnpj.v2.util.Constants;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by David on 8/01/16.
 */
public interface Api {

    @GET(Constants.API_INSTITUTION)
    List<Institution> getListInstitutions(@Query(Constants.ID_KEY) String imei);

    @GET(Constants.API_INSTITUTION_ZONE)
    List<InstitutionZone> getListInstitutionZones(@Query(Constants.ID_KEY) String imei);


    @GET(Constants.API_INSTITUTION_TYPE)
    List<InstitutionTypes> getListInstitutionTypes(@Query(Constants.ID_KEY) String imei);


    @GET(Constants.API_PRODUCT)
    List<Product> getListProducts(@Query(Constants.ID_KEY) String imei);

    @GET(Constants.API_SPECIALTY)
    List<Specialty> getListSpecialties(@Query(Constants.ID_KEY) String imei);

    @GET(Constants.API_DOCTOR)
    List<Doctor> getListDoctors(@Query(Constants.ID_KEY) String imei);

    @GET(Constants.API_PHARMACY)
    List<Pharmacy> getListPharmacy(@Query(Constants.ID_KEY) String imei);

    @GET(Constants.API_PHARMACY_ADDRESS)
    List<PharmacyAddress> getListPharmacyAddress(@Query(Constants.ID_KEY) String imei);

    @GET(Constants.API_RECORD_PHARMACY)
    List<RecordPharmacy> getListRecordPharmacy(@Query(Constants.ID_KEY) String imei);


    @GET(Constants.API_DOCTOR_CLOSEUP)
    List<DoctorsCloseUp> getListDoctorsCloseup(@Query(Constants.ID_KEY) String imei);


    @GET(Constants.API_DOCTOR_TYPE)
    List<DoctorType> getListDoctorType(@Query(Constants.ID_KEY) String imei);


    @GET(Constants.API_ATTENTION_TYPE)
    List<AttentionType> getAttentionTypes(@Query(Constants.ID_KEY) String imei);

    @GET("/usersIMEI")
    List<User> getUserByImei(@Query(Constants.ID_KEY) String imei);

    @GET(Constants.API_AGENT)
    List<Agent> getAgents(@Query(Constants.ID_KEY) String imei);

    @GET(Constants.API_UBIGEO)
    List<Ubigeo> getUbigeos(@Query(Constants.ID_KEY) String imei);

    @GET(Constants.API_PATIENT)
    List<Patient> getPatients(@Query(Constants.ID_KEY) String imei,
                             @Query(Constants.ID_USUARIO) int idUsuario);

    @GET("/TypeMovement")
    List<TypeMovement> getTypeMovements(@Query(Constants.ID_USUARIO) int idUsuario);

    @POST("/patients")
    void createPatient(@Body JsonObject patient, Callback<Result> result);

    @POST("/Doctors")
    void createDoctor(@Body JsonObject doctor, Callback<Result> result);

    @POST("/RecordPharmacy")
    void createRecordPharmacy(@Body JsonObject recordPharmacy, Callback<Result> result);


}
