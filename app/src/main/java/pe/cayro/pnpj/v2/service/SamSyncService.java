package pe.cayro.pnpj.v2.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmResults;
import pe.cayro.pnpj.v2.R;
import pe.cayro.pnpj.v2.WelcomeActivity;
import pe.cayro.pnpj.v2.api.RestClient;
import pe.cayro.pnpj.v2.model.Doctor;
import pe.cayro.pnpj.v2.model.RecordPharmacy;
import pe.cayro.pnpj.v2.model.Result;
import pe.cayro.pnpj.v2.serializer.DoctorSerializer;
import pe.cayro.pnpj.v2.serializer.RecordPharmacySerializer;
import pe.cayro.pnpj.v2.util.Constants;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SamSyncService extends IntentService {

    private static String TAG = SamSyncService.class.getSimpleName();

    public static final int NOTIFICATION_ID = 1;

    private NotificationManager mNotificationManager;

    public SamSyncService() {
        super("SamSyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        Log.i(TAG, "Iniciando Sincronización");

        sendNotification("Iniciando Sincronización");

     //   PatientSerializer serializerPatient         = new PatientSerializer();
        DoctorSerializer serializerDoctor             = new DoctorSerializer();
        RecordPharmacySerializer serializerInstitution   = new RecordPharmacySerializer();
     //   TrackingSerializer serializerTracking       = new TrackingSerializer();
      //  RecordSerializer serializerRecord           = new RecordSerializer();
      //  SpecialMoveSerializer serializerSpecialMove = new SpecialMoveSerializer();


        Realm realmMain = Realm.getDefaultInstance();

      /*  RealmResults<Patient> pendingPatients = realmMain.where(Patient.class).
                        equalTo(Constants.SENT, Boolean.FALSE).findAll();

        for (Patient patient: pendingPatients) {

                    RestClient.get().createPatient(serializerPatient.
                            serialize(patient, null, null), new Callback<Result>() {
                        @Override
                        public void success(Result result, Response response) {

                            Log.i(TAG, "Patients");
                            Log.i(TAG, result.getUuid());
                            Log.i(TAG, result.getIdResult());

                            if(Constants.ONE.equals(result.getIdResult())){
                                Log.i(TAG, "Patient Updated");
                                Realm realm = Realm.getDefaultInstance();
                                realm.beginTransaction();
                                Patient temp = realm.where(Patient.class).equalTo("uuid",
                                        result.getUuid()).findFirst();
                                temp.setSent(Boolean.TRUE);
                                realm.copyToRealmOrUpdate(temp);
                                realm.commitTransaction();
                                realm.close();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {

                            Log.e(TAG, error.getMessage());

                        }
                    });

        }*/



      //  realmMain.commitTransaction();


        RealmResults<RecordPharmacy> pendingInstitutions = realmMain.where(RecordPharmacy.class).
                equalTo(Constants.SENT, Boolean.FALSE).notEqualTo("type",0).findAll();

        for(RecordPharmacy institution : pendingInstitutions){
            RestClient.get().createRecordPharmacy(serializerInstitution.serialize(institution, null,null), new Callback<Result>(){

                @Override
                public void success(Result result, Response response) {

                    Log.i(TAG, "RecordPharmacy");
                    Log.i(TAG, result.getUuid());
                    Log.i(TAG, result.getIdResult());

                    if(Constants.ONE.equals(result.getIdResult())){

                        Log.i(TAG, "RecordPharmacy Updated");
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        RecordPharmacy temp = realm.where(RecordPharmacy.class).equalTo("uuid",
                                result.getUuid()).findFirst();
                        temp.setSent(Boolean.TRUE);
                        realm.copyToRealmOrUpdate(temp);
                        realm.commitTransaction();
                        realm.close();
                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, error.getMessage());
                }
            });

        }


        RealmResults<Doctor> pendingDoctors = realmMain.where(Doctor.class).
                        equalTo(Constants.SENT, Boolean.FALSE).findAll();
        for (Doctor doctor: pendingDoctors) {

                    RestClient.get().createDoctor(serializerDoctor.
                            serialize(doctor, null, null), new Callback<Result>() {
                        @Override
                        public void success(Result result, Response response) {

                            Log.i(TAG, "Doctors");
                            Log.i(TAG, result.getUuid());
                            Log.i(TAG, result.getIdResult());

                            if(Constants.ONE.equals(result.getIdResult())){

                                Log.i(TAG, "Doctor Updated");
                                Realm realm = Realm.getDefaultInstance();
                                realm.beginTransaction();
                                Doctor temp = realm.where(Doctor.class).equalTo("uuid",
                                        result.getUuid()).findFirst();
                                temp.setSent(Boolean.TRUE);
                                realm.copyToRealmOrUpdate(temp);
                                realm.commitTransaction();
                                realm.close();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {

                            Log.e(TAG, error.getMessage());

                        }
                    });
        }

        Log.i(TAG, "Finalizando Sincronización");

        NotificationManager notifManager = (NotificationManager) this.
                    getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancelAll();

        SamAlarmReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, WelcomeActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}