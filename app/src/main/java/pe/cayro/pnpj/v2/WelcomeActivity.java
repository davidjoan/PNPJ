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
import pe.cayro.pnpj.v2.api.RestClient;
import pe.cayro.pnpj.v2.model.Agent;
import pe.cayro.pnpj.v2.model.AttentionType;
import pe.cayro.pnpj.v2.model.Doctor;
import pe.cayro.pnpj.v2.model.DoctorType;
import pe.cayro.pnpj.v2.model.Institution;
import pe.cayro.pnpj.v2.model.Patient;
import pe.cayro.pnpj.v2.model.Product;
import pe.cayro.pnpj.v2.model.Specialty;
import pe.cayro.pnpj.v2.model.TypeMovement;
import pe.cayro.pnpj.v2.model.Ubigeo;
import pe.cayro.pnpj.v2.model.User;
import pe.cayro.pnpj.v2.service.SamAlarmReceiver;
import pe.cayro.pnpj.v2.util.Constants;

public class WelcomeActivity extends AppCompatActivity {
    private static String TAG = WelcomeActivity.class.getSimpleName();
    private static final int SPLASH_DURATION = 1500;

    @Bind(R.id.logo_sam)
    protected ImageView logo;
    private Handler handler;
    private ProgressDialog progress;
    private boolean mIsBackButtonPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ButterKnife.bind(this);

        progress = new ProgressDialog(this);

        Snackbar.make(logo, R.string.loading_app, Snackbar.LENGTH_LONG)
                .setAction(Constants.ACTION, null)
                .show();

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                    progress.setCancelable(false);
                    progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progress.setMax(11);
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

            this.publishProgress(Constants.OBTAINING_IMEI);

            //Replace for implement in production enable the following lines of code

            Log.i(TAG, telephonyManager.getDeviceId());

            //String imei = telephonyManager.getDeviceId();

            String imei = Constants.IMEI_TEST;

            Realm realm = Realm.getDefaultInstance();

            try{

                realm.beginTransaction();

                realm.clear(Specialty.class);
                realm.clear(Doctor.class);
                realm.clear(DoctorType.class);
                realm.clear(Specialty.class);
                realm.clear(Patient.class);
                realm.clear(TypeMovement.class);
               // realm.clear(Institution.class);

                this.publishProgress(Constants.LOADING_USERS);
                List<User> users = RestClient.get().getUserByImei(imei);
                User user = users.get(0);
                realm.copyToRealmOrUpdate(user);

                /*
                this.publishProgress(Constants.LOADING_INSTITUTIONS);
                List<Institution> institutions = RestClient.get().getListInstitutions(imei,
                        user.getId());
                realm.copyToRealmOrUpdate(institutions);
*/

                this.publishProgress(Constants.LOADING_SPECIALTIES);
                List<Specialty> specialties = RestClient.get().getListSpecialties(imei);
                realm.copyToRealmOrUpdate(specialties);

                this.publishProgress(Constants.LOADING_DOCTORS);
                List<Doctor> doctors = RestClient.get().getListDoctors(imei);

                this.publishProgress(Constants.LOADING_UBIGEOS);
                List<Ubigeo> ubigeos = RestClient.get().getUbigeos(imei);
                realm.copyToRealmOrUpdate(ubigeos);

                this.publishProgress(Constants.LOADING_TYPE_MOVEMENTS);
                List<TypeMovement> typeMovements = RestClient.get().getTypeMovements(user.getId());
                realm.copyToRealmOrUpdate(typeMovements);

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

                realm.copyToRealmOrUpdate(doctorsTemp2);

                this.publishProgress(Constants.LOADING_PRODUCTS);
                List<Product> products = RestClient.get().getListProducts(imei);
                realm.copyToRealmOrUpdate(products);

                this.publishProgress(Constants.LOADING_AGENTS);
                List<Agent> agents = RestClient.get().getAgents(imei);
                realm.copyToRealmOrUpdate(agents);

                this.publishProgress(Constants.LOADING_PATIENTS);
                List<Patient> patients = RestClient.get().getPatients(imei, user.getId());
                realm.copyToRealmOrUpdate(patients);

                realm.commitTransaction();


            } finally {
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
            }

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!mIsBackButtonPressed) {
                        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                        WelcomeActivity.this.startActivity(intent);
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
