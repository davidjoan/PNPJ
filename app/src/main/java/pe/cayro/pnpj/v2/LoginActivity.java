package pe.cayro.pnpj.v2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.Date;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import pe.cayro.pnpj.v2.model.Institution;
import pe.cayro.pnpj.v2.model.Record;
import pe.cayro.pnpj.v2.model.Tracking;
import pe.cayro.pnpj.v2.model.User;
import pe.cayro.pnpj.v2.util.Constants;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    private static String TAG = LoginActivity.class.getSimpleName();

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    String institutionName;
    @Bind(R.id.buttonLogin)
    protected Button loginButton;
    @Bind(R.id.text_second)
    protected TextView institutionTextView;
    @Bind(R.id.editTextAtentionCode)
    protected EditText attentionCodeEditText;

    protected Institution institution;
    protected User user;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    protected Realm realm;
    protected long max = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        buildGoogleApiClient();

        institutionName = getIntent().getStringExtra(Constants.INSTITUTION_NAME);

        ButterKnife.bind(this);

        realm = Realm.getInstance(getApplicationContext());

        institution = realm.where(Institution.class)
                .equalTo(Constants.NAME, institutionName).findFirst();

        user = realm.where(User.class).findFirst();
        RealmResults<Record> results = realm.where(Record.class).findAll();
        if(results.size() > 0 ){
            max = results.max(Constants.CODE).longValue()+1;
        }
        toolbar.setTitle(R.string.login);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        institutionTextView.setText(institutionName);
        attentionCodeEditText.setText(String.valueOf(max));
        attentionCodeEditText.setSelection(attentionCodeEditText.getText().length());


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int errors = 0;

                if(attentionCodeEditText.getText().length() == 0){
                    errors++;
                    attentionCodeEditText.setError("El código no puede ser vacío");
                    Toast.makeText(LoginActivity.this, R.string.validation_tracking_code,
                            Toast.LENGTH_SHORT).show();
                }

                if(attentionCodeEditText.getText().toString().equals("0")){
                    errors++;
                    attentionCodeEditText.setError("El código no puede ser 0");
                }

                RealmResults<Record> resultsQuery = realm.where(Record.class).equalTo("code",
                        Integer.valueOf(attentionCodeEditText.getText().toString()).intValue()).
                        findAll();

                if(resultsQuery.size() > 0){
                    errors++;
                    attentionCodeEditText.setError("El código ya existe");
                }

                if(errors == 0){

                    realm.beginTransaction();
                    Tracking tracking = new Tracking();
                    tracking.setUuid(UUID.randomUUID().toString());
                    tracking.setCode(Integer.valueOf(attentionCodeEditText.
                            getText().toString()).intValue());
                    tracking.setType(Constants.LOGIN);
                    tracking.setInstitutionId(institution.getId());
                    tracking.setCreatedAt(new Date());
                    tracking.setUserId(user.getId());
                    tracking.setInstitution(institution);
                    tracking.setSent(false);

                    if(mLastLocation != null){
                        tracking.setLatitude(mLastLocation.getLatitude());
                        tracking.setLongitude(mLastLocation.getLongitude());
                    }

                    realm.copyToRealm(tracking);
                    realm.commitTransaction();

                    SharedPreferences settings = getApplicationContext().
                            getSharedPreferences(Constants.PREFERENCES_SAM, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(Constants.SESSION,Constants.YES);
                    editor.putString(Constants.SESSION_TRACKING,tracking.getUuid());
                    editor.commit();

                    Intent intent = new Intent(LoginActivity.this, InstitutionActivity.class);
                    intent.putExtra(Constants.TRACKING_CODE, tracking.getCode());
                    intent.putExtra(Constants.INSTITUTION_NAME, institutionName);
                    intent.putExtra(Constants.INSTITUTION_ID, institution.getId());
                    intent.putExtra(Constants.UUID, tracking.getUuid());
                    LoginActivity.this.startActivity(intent);
                    finish();

                }
            }
        });
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Log.i(TAG, Constants.LATITUDE + String.valueOf(mLastLocation.getLatitude()));
            Log.i(TAG, Constants.LONGITUDE + String.valueOf(mLastLocation.getLongitude()));
        } else {
            Log.i(TAG,Constants.GPS_DISABLED);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, Constants.CONNECTION_SUSPENDED + String.valueOf(i));
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, Constants.CONNECTION_FAILED + result.getErrorCode());
    }
}
