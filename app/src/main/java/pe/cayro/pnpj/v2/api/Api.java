package pe.cayro.pnpj.v2.api;

import com.google.gson.JsonObject;

import java.util.List;

import pe.cayro.pnpj.v2.model.Agent;
import pe.cayro.pnpj.v2.model.AttentionType;
import pe.cayro.pnpj.v2.model.Doctor;
import pe.cayro.pnpj.v2.model.DoctorType;
import pe.cayro.pnpj.v2.model.Institution;
import pe.cayro.pnpj.v2.model.Patient;
import pe.cayro.pnpj.v2.model.Product;
import pe.cayro.pnpj.v2.model.Result;
import pe.cayro.pnpj.v2.model.Specialty;
import pe.cayro.pnpj.v2.model.TypeMovement;
import pe.cayro.pnpj.v2.model.Ubigeo;
import pe.cayro.pnpj.v2.model.User;
import pe.cayro.pnpj.v2.model.report.InstitutionShare;
import pe.cayro.pnpj.v2.model.report.MedicalSampleShare;
import pe.cayro.pnpj.v2.model.report.Stock;
import pe.cayro.pnpj.v2.model.report.UsersDependent;
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
    List<Institution> getListInstitutions(@Query(Constants.ID_KEY) String imei,
                                          @Query(Constants.ID_USUARIO) int idUsuario);

    @GET(Constants.API_PRODUCT)
    List<Product> getListProducts(@Query(Constants.ID_KEY) String imei);

    @GET(Constants.API_SPECIALTY)
    List<Specialty> getListSpecialties(@Query(Constants.ID_KEY) String imei);

    @GET(Constants.API_DOCTOR)
    List<Doctor> getListDoctors(@Query(Constants.ID_KEY) String imei);

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

    @POST("/doctors")
    void createDoctor(@Body JsonObject doctor, Callback<Result> result);

    @POST("/records")
    void createRecord(@Body JsonObject record, Callback<Result> result);

    @POST("/SpecialMoves")
    void createSpecialMove(@Body JsonObject specialMove, Callback<Result> result);

    @POST("/tracking")
    void createTracking(@Body JsonObject tracking, Callback<Result> result);

    @GET("/institutionsShare")
    void getShareInstitution(
            @Query(Constants.ID_USUARIO) int idUser, Callback<List<InstitutionShare>> result);

    @GET("/medicalSampleShare")
    void getShareMedicalSample(
            @Query(Constants.ID_USUARIO) int idUser,
            @Query(Constants.ID_INSTITUTION) int idInstitution,
            Callback<List<MedicalSampleShare>> result);

    @GET("/UsersDependent")
    void getUserDependent(
            @Query(Constants.ID_USUARIO) int idUser,
            Callback<List<UsersDependent>> result);

    @GET("/Stock")
    void getStockDependent(
            @Query(Constants.ID_USUARIO) int idUser,
            Callback<List<Stock>> result);
}
