package pe.cayro.pnpj.v2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.Bind;
import butterknife.ButterKnife;
import pe.cayro.pnpj.v2.util.Constants;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        ConnectionCallbacks,
        OnConnectionFailedListener {

    private static String TAG = MapsActivity.class.getSimpleName();

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;

    private GoogleMap googleMap;
    private MapView mMapView;

    private ProgressDialog progress;

    String latitude;
    String longitude;

    double latitude2;
    double longitude2;

    LatLng ubicacion;
    MarkerOptions marker;

    GoogleApiClient mGoogleApiClient;

    Location mLastLocation;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        latitude = getIntent().getStringExtra(Constants.LATITUDE);
        longitude = getIntent().getStringExtra(Constants.LONGITUDE);
        type = getIntent().getStringExtra(Constants.TYPE);

        ButterKnife.bind(this);

        toolbar.setTitle(R.string.geolocalizacion);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setMessage(getString(R.string.loading));

        try {
            MapsInitializer.initialize(this.getApplicationContext());
        } catch (Exception e) {
            Log.e(TAG ,e.getMessage());
        }

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);


        if(latitude.equals(Constants.EMPTY)){
            ubicacion  = new LatLng(-12.0978619,-77.0103938);
        }else{
            ubicacion = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        }

         marker = new MarkerOptions().position(ubicacion).
                title("Ubicación").
                snippet("Dirección");


        // Changing marker icon
        marker.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED));

        marker.draggable(true);

        googleMap.addMarker(marker);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(ubicacion).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latLng) {
                Log.d(TAG, latLng.latitude + Constants.ELLIPSIS +
                        latLng.longitude);


                googleMap.clear();

                marker = new MarkerOptions().position(latLng).
                        title("Ubicación").
                        snippet("Dirección");

                // Changing marker icon
                marker.icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_RED));

                marker.draggable(true);

                googleMap.addMarker(marker);
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                progress.show();


                latitude = String.valueOf(latLng.latitude);
                longitude = String.valueOf(latLng.longitude);

                latitude2 = latLng.latitude;
                longitude2 = latLng.longitude;

                progress.dismiss();

                Toast.makeText(getApplicationContext(), "Se Obtuvo la Ubicación Seleccionada",  Toast.LENGTH_SHORT).show();


            }
        });

        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker arg0) {
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onMarkerDragEnd(Marker arg0) {
                Log.d(TAG, arg0.getPosition().latitude + Constants.ELLIPSIS +
                        arg0.getPosition().longitude);

                googleMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));

                progress.show();


                latitude = String.valueOf(arg0.getPosition().latitude);
                 longitude = String.valueOf(arg0.getPosition().longitude);

              latitude2 = arg0.getPosition().latitude;
                longitude2 = arg0.getPosition().longitude;

                progress.dismiss();

                Toast.makeText(getApplicationContext(), "Se Obtuvo la Ubicación",  Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMarkerDrag(Marker arg0) {
            }
        });

    }

    @Override
    public void onBackPressed() {
        doExit();
    }

    public void doExit()
    {
        Intent intent = new Intent();
        intent.putExtra(Constants.LATITUDE, latitude);
        intent.putExtra(Constants.LONGITUDE, longitude);
        if (getParent() == null) {
            setResult(Activity.RESULT_OK, intent);
        } else {
            getParent().setResult(Activity.RESULT_OK, intent);
        }
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK) {
            doExit();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {

        if(type.equals("new"))
        {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {

                LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                Log.d(TAG, latLng.latitude + Constants.ELLIPSIS +
                        latLng.longitude);


                googleMap.clear();

                marker = new MarkerOptions().position(latLng).
                        title("Ubicación").
                        snippet("Dirección");

                // Changing marker icon
                marker.icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_RED));

                marker.draggable(true);

                googleMap.addMarker(marker);

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));


                progress.show();


                latitude = String.valueOf(latLng.latitude);
                longitude = String.valueOf(latLng.longitude);

                latitude2 = latLng.latitude;
                longitude2 = latLng.longitude;

                progress.dismiss();

                Toast.makeText(getApplicationContext(), "Se Obtuvo la Ubicación Actual.",  Toast.LENGTH_SHORT).show();

            }
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
