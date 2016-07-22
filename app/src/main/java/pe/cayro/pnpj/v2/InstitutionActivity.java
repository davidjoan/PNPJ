package pe.cayro.pnpj.v2;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import pe.cayro.pnpj.v2.model.Institution;
import pe.cayro.pnpj.v2.model.Tracking;
import pe.cayro.pnpj.v2.model.User;
import pe.cayro.pnpj.v2.ui.FragmentDoctor;
import pe.cayro.pnpj.v2.ui.FragmentPatient;
import pe.cayro.pnpj.v2.ui.FragmentRecords;
import pe.cayro.pnpj.v2.ui.FragmentReport;
import pe.cayro.pnpj.v2.ui.FragmentSpecialMovement;
import pe.cayro.pnpj.v2.ui.FragmentTracking;
import pe.cayro.pnpj.v2.util.Constants;

public class InstitutionActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    private static String TAG = InstitutionActivity.class.getSimpleName();

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    protected DrawerLayout mDrawer;
    @Bind(R.id.nvView)
    protected NavigationView nvDrawer;

    Realm realm;
    private User user;
    private Tracking tracking;
    private int trackingCode;
    private String trackingUuid;
    private String institutionName;
    private int institutionId;
    private Institution institution;
    private FragmentManager fragmentManager;
    private ActionBarDrawerToggle drawerToggle;

    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_institution);

        buildGoogleApiClient();

        trackingCode    = getIntent().getIntExtra(Constants.TRACKING_CODE, 0);
        institutionName = getIntent().getStringExtra(Constants.INSTITUTION_NAME);
        institutionId   = getIntent().getIntExtra(Constants.INSTITUTION_ID, 0);
        trackingUuid    = getIntent().getStringExtra(Constants.UUID);

        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();
        tracking = realm.where(Tracking.class).equalTo(Constants.CODE, trackingCode)
                .equalTo(Constants.INSTITUTION_ID, institutionId)
                .equalTo(Constants.UUID, trackingUuid).findFirst();

        institution = realm.where(Institution.class)
                .equalTo(Constants.ID, tracking.getInstitutionId()).findFirst();

        user = realm.where(User.class).findFirst();

        toolbar.setTitle(institutionName);
        toolbar.setSubtitle(String.valueOf(trackingCode));

        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_drawer);
        ab.setDisplayHomeAsUpEnabled(true);

        drawerToggle = setupDrawerToggle();
        mDrawer.setDrawerListener(drawerToggle);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent,
                FragmentRecords.newInstance(tracking.getUuid())).commit();

        setupDrawerContent(nvDrawer);
        nvDrawer.getMenu().getItem(0).setChecked(true);

        View header = nvDrawer.inflateHeaderView(R.layout.institution_header);

        TextView institutionNameTextView = (TextView) header.findViewById(R.id.institution_name);
        institutionNameTextView.setText(institution.getBusinessname());
        TextView institutionCodeTextView = (TextView) header.findViewById(R.id.tracking_code);
        institutionCodeTextView.setText(Constants.CODE_FIELD+tracking.getCode());
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close){
            @Override
            public void onDrawerClosed(View view) {
            }
        };
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(final MenuItem menuItem) {
                        mDrawer.closeDrawers();
                        menuItem.setChecked(true);
                        setTitle(menuItem.getTitle());

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                selectDrawerItem(menuItem);
                            }
                        }, 260);

                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;

        try {
            switch(menuItem.getItemId()) {
                case R.id.nav_first_fragment:
                    fragment = FragmentRecords.newInstance(tracking.getUuid());
                    break;
                case R.id.nav_second_fragment:
                    fragment = FragmentDoctor.newInstance();
                    break;
                case R.id.nav_third_fragment:
                    fragment = FragmentPatient.newInstance();
                    break;
                case R.id.nav_fourth_fragment:
                    fragment = FragmentTracking.newInstance();
                    break;
                case R.id.nav_five_fragment:
                    fragment = FragmentReport.newInstance();
                    break;
                case R.id.nav_six_fragment:
                    fragment = FragmentSpecialMovement.newInstance();
                    break;

                default:
                    fragment = FragmentRecords.newInstance(tracking.getUuid());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_institution, menu);

        SharedPreferences settings = getApplicationContext().
                getSharedPreferences(Constants.PREFERENCES_SAM, 0);

        Calendar startTime = Calendar.getInstance();
        startTime.setTime(new Date());
        startTime.set(Calendar.HOUR_OF_DAY, 0);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.SECOND, 0);

        Calendar finishTime = Calendar.getInstance();
        finishTime.setTime(new Date());
        finishTime.set(Calendar.HOUR_OF_DAY, 24);
        finishTime.set(Calendar.MINUTE, 0);
        finishTime.set(Calendar.SECOND, 0);

        Tracking temp = realm.where(Tracking.class)
                .equalTo("type","close")
                .between("createdAt", startTime.getTime(), finishTime.getTime())
                .findFirst();

        if(temp == null){

            Boolean snack = settings.getBoolean(Constants.SNACK,false);
            if(snack.booleanValue()){
                getMenuInflater().inflate(R.menu.menu_close_break, menu);
            }else{
                getMenuInflater().inflate(R.menu.menu_open_break, menu);
            }
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {


        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){

            case R.id.action_close_break :
                break;

            case R.id.action_open_break :
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        //doExit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK) {
            //doExit();
        }
        return super.onKeyDown(keyCode, event);
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
