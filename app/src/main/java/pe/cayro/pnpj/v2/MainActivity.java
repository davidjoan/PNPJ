package pe.cayro.pnpj.v2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import pe.cayro.pnpj.v2.model.Tracking;
import pe.cayro.pnpj.v2.model.User;
import pe.cayro.pnpj.v2.service.SamAlarmReceiver;
import pe.cayro.pnpj.v2.ui.FragmentDoctor;
import pe.cayro.pnpj.v2.ui.FragmentInstitution;
import pe.cayro.pnpj.v2.ui.FragmentPatient;
import pe.cayro.pnpj.v2.ui.FragmentReport;
import pe.cayro.pnpj.v2.ui.FragmentSpecialMovement;
import pe.cayro.pnpj.v2.ui.FragmentTracking;
import pe.cayro.pnpj.v2.util.Constants;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    private static String TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.drawer_layout_main)
    protected DrawerLayout mDrawer;
    @Bind(R.id.nvViewMain)
    protected NavigationView nvDrawer;

    private User user;
    private Realm realm;
    private TextView userName;
    private TextView userCode;
    private TextView userRole;
    private ImageView avatar;
    private FragmentManager fragmentManager;
    private ActionBarDrawerToggle drawerToggle;

    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        buildGoogleApiClient();

        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setLogoDescription(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);

        realm = Realm.getDefaultInstance();
        user = realm.where(User.class).findFirst();

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_drawer);
        ab.setDisplayHomeAsUpEnabled(true);

        drawerToggle = setupDrawerToggle();
        mDrawer.setDrawerListener(drawerToggle);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent,
                FragmentInstitution.newInstance()).commit();

        setupDrawerContent(nvDrawer);
        nvDrawer.getMenu().getItem(0).setChecked(true);

        View header = nvDrawer.inflateHeaderView(R.layout.nav_header);
        userName = (TextView) header.findViewById(R.id.NavUserName);
        userName.setText(user.getName());
        userCode = (TextView) header.findViewById(R.id.NavUserCode);
        userCode.setText(user.getCode());

        avatar = (ImageView) header.findViewById(R.id.avatar);
/*
        Picasso.with(this).
                load(new StringBuilder().append(Constants.USER_PHOTO_SERVER).
                        append(user.getCode()).
                        append(Constants.DOT_JPG).toString()).
                error(R.drawable.avatar).
                into(avatar);
*/
        userRole = (TextView) header.findViewById(R.id.NavUserRole);
        userRole.setText(user.getRol());

        SharedPreferences settings = getSharedPreferences(Constants.PREFERENCES_SAM, 0);
        String session = settings.getString(Constants.SESSION, Constants.EMPTY);

        if(session.equals(Constants.YES)){

            Tracking tempTracking = Realm.getDefaultInstance().
                    where(Tracking.class).
                    equalTo(Constants.UUID,
                            settings.getString(Constants.SESSION_TRACKING, "")).
                    findFirst();

            Intent intent = new Intent(MainActivity.this, InstitutionActivity.class);
            intent.putExtra(Constants.TRACKING_CODE, tempTracking.getCode());
            intent.putExtra(Constants.INSTITUTION_NAME, tempTracking.getInstitution().getName());
            intent.putExtra(Constants.INSTITUTION_ID, tempTracking.getInstitutionId());
            intent.putExtra(Constants.UUID, tempTracking.getUuid());
            MainActivity.this.startActivity(intent);
        }



    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close){
            @Override
            public void onDrawerClosed(View view) {
                // your refresh code can be called from here
            }
        };
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(final MenuItem menuItem) {
                        // Highlight the selected item, update the title, and close the drawer
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
                    fragment = FragmentDoctor.newInstance();
                    break;
                case R.id.nav_second_fragment:
                    fragment = FragmentPatient.newInstance();
                    break;
                case R.id.nav_third_fragment:
                    fragment = FragmentInstitution.newInstance();
                    break;
                case R.id.nav_five_fragment:
                    fragment = FragmentDoctor.newInstance();
                    doExit();
                    break;

                default:
                    fragment = FragmentDoctor.newInstance();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        // Insert the fragment by replacing any existing fragment
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

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
                .between("createdAt",startTime.getTime(), finishTime.getTime())
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
            case R.id.action_close_break:
                changeSnack(false);
                break;
            case R.id.action_open_break:
                changeSnack(true);
                break;
            case R.id.action_send_data:
                registerAlarm(getApplicationContext());
                break;
            case R.id.action_receive_data:
                Intent intentReceiveData = new Intent(MainActivity.this, UpdateDataActivity.class);
                startActivity(intentReceiveData);
                finish();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * Exit the app if user select yes.
     */
    private void changeSnack(final boolean status) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getSupportActionBar().
                getThemedContext());
        alertDialog.setPositiveButton(Constants.SI, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mGoogleApiClient.connect();
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                realm.beginTransaction();
                Tracking tracking = new Tracking();
                tracking.setUuid(UUID.randomUUID().toString());
                if (status) {
                    tracking.setType(Constants.OPEN);
                } else {
                    tracking.setType(Constants.CLOSE);
                }

                tracking.setCreatedAt(new Date());
                tracking.setUserId(user.getId());
                tracking.setSent(Boolean.FALSE);

                if (mLastLocation != null) {
                    tracking.setLatitude(mLastLocation.getLatitude());
                    tracking.setLongitude(mLastLocation.getLongitude());
                }

                realm.copyToRealmOrUpdate(tracking);
                realm.commitTransaction();

                SharedPreferences settings = getApplicationContext().
                        getSharedPreferences(Constants.PREFERENCES_SAM, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(Constants.SNACK, status);
                editor.commit();
                invalidateOptionsMenu();

            }
        }).setNegativeButton(Constants.NO, null);
        alertDialog.setMessage("Quiere "+((status)?"Iniciar":"Finalizar")+" Refrigerio?");
        alertDialog.setTitle(getString(R.string.app_name));
        alertDialog.show();
    }

    /**
     * Exit the app if user select yes.
     */
    private void doExit() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setPositiveButton(Constants.SI, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog.setNegativeButton(Constants.NO, null);
        alertDialog.setMessage(Constants.EXIT);
        alertDialog.setTitle(getString(R.string.app_name));
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        doExit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK) {
            doExit();
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
            Log.i(TAG, Constants.GPS_DISABLED);
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
