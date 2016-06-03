package pe.cayro.pnpj.v2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import pe.cayro.pnpj.v2.model.Tracking;
import pe.cayro.pnpj.v2.util.Constants;

public class InstitutionMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    private Realm realm;
    private GoogleMap mMap;
    private Tracking tracking;
    private String trackingUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();

        trackingUuid = getIntent().getStringExtra(Constants.TRACKING_UUID);

        tracking = realm.where(Tracking.class).equalTo(Constants.UUID, trackingUuid).findFirst();

        if(tracking.getInstitution() != null){
            toolbar.setTitle(tracking.getInstitution().getName());
            toolbar.setSubtitle(tracking.getInstitution().getAddress());
        }else{
            toolbar.setTitle("Refrigerio");
            toolbar.setSubtitle(tracking.getType());
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        LatLng sydney = new LatLng(tracking.getLatitude(), tracking.getLongitude());

        mMap.addMarker(new MarkerOptions().position(sydney).title(getString(R.string.user)));

        if(tracking.getInstitution() != null){

            LatLng institutionLatLng = new LatLng(tracking.getInstitution().getLatitude(),
                    tracking.getInstitution().getLongitude());

            mMap.addMarker(new MarkerOptions()
                    .position(institutionLatLng)
                    .title(tracking.getInstitution().getName()));
        }

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(sydney).zoom(12).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
