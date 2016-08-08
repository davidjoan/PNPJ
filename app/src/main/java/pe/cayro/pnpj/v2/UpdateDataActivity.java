package pe.cayro.pnpj.v2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import pe.cayro.pnpj.v2.api.RestClient;
import pe.cayro.pnpj.v2.model.Doctor;
import pe.cayro.pnpj.v2.model.DoctorType;
import pe.cayro.pnpj.v2.model.DoctorsCloseUp;
import pe.cayro.pnpj.v2.model.Pharmacy;
import pe.cayro.pnpj.v2.model.PharmacyAddress;
import pe.cayro.pnpj.v2.model.RecordPharmacy;
import pe.cayro.pnpj.v2.model.Result;
import pe.cayro.pnpj.v2.model.Specialty;
import pe.cayro.pnpj.v2.model.Ubigeo;
import pe.cayro.pnpj.v2.model.User;
import pe.cayro.pnpj.v2.serializer.DoctorSerializer;
import pe.cayro.pnpj.v2.serializer.RecordPharmacySerializer;
import pe.cayro.pnpj.v2.service.SamAlarmReceiver;
import pe.cayro.pnpj.v2.util.Constants;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UpdateDataActivity extends AppCompatActivity {
    private static String TAG = UpdateDataActivity.class.getSimpleName();
    private static final int SPLASH_DURATION = 1500;

    @Bind(R.id.logo_sam)
    protected ImageView logo;
    private Handler handler;
    private ProgressDialog progress;
    private boolean mIsBackButtonPressed;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        type = getIntent().getStringExtra(Constants.TYPE);

        ButterKnife.bind(this);

        progress = new ProgressDialog(this);

        Snackbar.make(logo, R.string.updating_app, Snackbar.LENGTH_LONG)
                .setAction(Constants.ACTION, null)
                .show();

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                progress.setCancelable(false);
                progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progress.setMax(2);
                progress.setMessage(Constants.SINCRONIZATION);
                progress.show();
                new LoginAsyncTask(getApplicationContext()).execute(Constants.EMPTY);

            }
        }, SPLASH_DURATION);
    }

    @Override
    public void onBackPressed() {
        mIsBackButtonPressed = true;
        super.onBackPressed();
    }

    public class LoginAsyncTask extends AsyncTask<String, String, Integer> {

        Context context;
        private Handler handler;

        public LoginAsyncTask(Context context) {
            super();
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            handler = new Handler();
            progress.setProgress(0);
        }

        protected void onProgressUpdate(String...a)
        {
            super.onProgressUpdate(a);

            if (a != null && a.length > 0) {
                final String msg = a[0];
                handler.post(new Runnable() {
                    public void run() {
                        progress.setProgress(progress.getProgress()+1);
                        progress.setMessage(msg);
                    }
                });
            }
        }

        @Override
        public Integer doInBackground(String... params) {
            int result = 0;

            SharedPreferences settings = context.
                    getSharedPreferences(Constants.PREFERENCES_SAM, 0);
            SharedPreferences.Editor editor = settings.edit();
            TelephonyManager telephonyManager = (TelephonyManager) context.
                    getSystemService(Context.TELEPHONY_SERVICE);


            Log.i(TAG, telephonyManager.getDeviceId());

            String imei = telephonyManager.getDeviceId();
            //String imei = Constants.IMEI_TEST;

            Realm realm = Realm.getDefaultInstance();

            try{

                Log.i(TAG, "Iniciando Sincronización");

                realm.beginTransaction();

                DoctorSerializer serializerDoctor             = new DoctorSerializer();
                RecordPharmacySerializer serializerInstitution   = new RecordPharmacySerializer();

                this.publishProgress("Enviando Datos");

                RealmResults<RecordPharmacy> pendingInstitutions = realm.where(RecordPharmacy.class).
                        equalTo(Constants.SENT, Boolean.FALSE).
                        isNotNull("ruc").
                        findAll();

                for(RecordPharmacy institution : pendingInstitutions){
                    RestClient.get().createRecordPharmacy(serializerInstitution.serialize(institution, null,null), new Callback<Result>(){

                        @Override
                        public void success(Result result, Response response) {

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

                RealmResults<Doctor> pendingDoctors = realm.where(Doctor.class).
                        equalTo(Constants.SENT, Boolean.FALSE).
                        notEqualTo("doctorTypeId", 0).
                        findAll();
                for (Doctor doctor: pendingDoctors) {

                    RestClient.get().createDoctor(serializerDoctor.
                            serialize(doctor, null, null), new Callback<Result>() {
                        @Override
                        public void success(Result result, Response response) {

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

                List<User> users = RestClient.get().getUserByImei(imei);
                User user = users.get(0);
                realm.copyToRealmOrUpdate(user);


                if(type.equals("all"))
                {
                    this.publishProgress("Descarga General");

                    this.publishProgress(Constants.LOADING_PHARMACYS);
                    List<Pharmacy> pharmacies = RestClient.get().getListPharmacy(imei);
                    realm.copyToRealmOrUpdate(pharmacies);

                    this.publishProgress(Constants.LOADING_PHARMACY_ADDRESS);
                    List<PharmacyAddress> pharmacyAddress = RestClient.get().getListPharmacyAddress(imei);
                    realm.copyToRealmOrUpdate(pharmacyAddress);

                    this.publishProgress(Constants.LOADING_DOCTORS);
                    List<Doctor> doctors = RestClient.get().getListDoctors(imei);

                    this.publishProgress(Constants.LOADING_DOCTORS_CLOSEUP);
                    List<DoctorsCloseUp> doctorsCloseup = RestClient.get().getListDoctorsCloseup(imei);

                    this.publishProgress(Constants.LOADING_UBIGEOS);
                    List<Ubigeo> ubigeos = RestClient.get().getUbigeos(imei);
                    realm.copyToRealmOrUpdate(ubigeos);

                    List<Doctor> doctorsTemp = new ArrayList<Doctor>();
                    for(Doctor temp : doctors){
                        Specialty tempEsp = realm.where(Specialty.class).equalTo(Constants.ID,
                                temp.getSpecialtyId()).findFirst();
                        temp.setSpecialty(tempEsp);
                        doctorsTemp.add(temp);
                    }

                    List<Doctor> doctorsTemp2 = new ArrayList<Doctor>();
                    for(Doctor temp : doctors){
                        DoctorType tempDoctorType = realm.where(DoctorType.class).equalTo(Constants.ID,
                                temp.getDoctorTypeId()).findFirst();
                        temp.setDoctorType(tempDoctorType);
                        doctorsTemp2.add(temp);
                    }

                    List<DoctorsCloseUp> doctorsCloseupTemp = new ArrayList<DoctorsCloseUp>();
                    for(DoctorsCloseUp temp : doctorsCloseup){
                        Specialty tempEsp = realm.where(Specialty.class).equalTo(Constants.ID,
                                temp.getSpecialtyId()).findFirst();
                        temp.setSpecialty(tempEsp);
                        doctorsCloseupTemp.add(temp);
                    }

                    realm.copyToRealmOrUpdate(doctorsTemp);
                    realm.copyToRealmOrUpdate(doctorsTemp2);
                    realm.copyToRealmOrUpdate(doctorsCloseupTemp);

                }else{
                    this.publishProgress("Forzando Actualización");

                }



                List<RecordPharmacy> recordPharmacy = RestClient.get().getListRecordPharmacy(imei);
                realm.copyToRealmOrUpdate(recordPharmacy);

                List<Specialty> specialties = RestClient.get().getListSpecialties(imei);
                realm.copyToRealmOrUpdate(specialties);

/*


*/
                realm.commitTransaction();

                editor.putString(Constants.CYCLE_LOADED, Constants.YES);

                editor.apply();
            } catch (Exception e)
            {
                Log.e(TAG, e.getMessage());
                result = 1;
            }
            finally {
                if (realm != null) {
                    realm.close();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {

            registerAlarm(getApplicationContext());

            progress.dismiss();

            switch (result) {
                case 0:
                    Snackbar.make(logo, R.string.login_success, Snackbar.LENGTH_LONG)
                            .setAction(Constants.ACTION, null)
                            .show();
                    break;
                case 1:
                    Snackbar.make(logo, R.string.error_conection, Snackbar.LENGTH_LONG)
                            .setAction(Constants.ACTION, null)
                            .show();
                    break;
            }

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!mIsBackButtonPressed) {
                        Intent intent = new Intent(UpdateDataActivity.this, MainActivity.class);
                        UpdateDataActivity.this.startActivity(intent);
                        finish();
                    }
                }
            }, SPLASH_DURATION);
        }
    }

    public static void registerAlarm(Context context) {
        Intent i = new Intent(context, SamAlarmReceiver.class);

        PendingIntent sender = PendingIntent.getBroadcast(context,1, i, 0);

        long firstTime = SystemClock.elapsedRealtime();
        firstTime += 3 * 1000;//start 3 seconds after first register.
        long range = 10 * 60 * 1000;//execute every 10 minutes.

        AlarmManager am = (AlarmManager) context
                .getSystemService(ALARM_SERVICE);

        am.cancel(sender);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime,
                range, sender);
    }
}
