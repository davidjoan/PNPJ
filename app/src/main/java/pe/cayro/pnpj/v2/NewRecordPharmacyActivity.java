package pe.cayro.pnpj.v2;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import pe.cayro.pnpj.v2.adapter.PharmacyAutocompleterAdapter;
import pe.cayro.pnpj.v2.adapter.UbigeoAutocompleterAdapter;
import pe.cayro.pnpj.v2.model.Pharmacy;
import pe.cayro.pnpj.v2.model.PharmacyAddress;
import pe.cayro.pnpj.v2.model.RecordPharmacy;
import pe.cayro.pnpj.v2.model.Ubigeo;
import pe.cayro.pnpj.v2.model.User;
import pe.cayro.pnpj.v2.util.Constants;

public class NewRecordPharmacyActivity extends AppCompatActivity {

    private static String TAG = NewRecordPharmacyActivity.class.getSimpleName();

    static final int ADD_GEOLOCALIZATION_REQUEST = 1;

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.record_pharmacy_save)
    protected Button recordPharmacySave;
    @Bind(R.id.record_pharmacy_cancel)
    protected Button recordPharmacyCancel;
    @Bind(R.id.record_pharmacy_code_autocompleter)
    protected AutoCompleteTextView recordPharmacyCode;
    @Bind(R.id.record_pharmacy_bussinessname)
    protected EditText recordPharmacyBussinesname;
    @Bind(R.id.record_pharmacy_number_address)
    protected EditText recordPharmacyNumberAddress;
    @Bind(R.id.record_pharmacy_address)
    protected EditText recordPharmacyAddress;
    @Bind(R.id.record_pharmacy_category)
    protected EditText recordPharmacyCategory;
    @Bind(R.id.record_pharmacy_type_address)
    protected Spinner spinner;
    @Bind(R.id.record_pharmacy_zone_autocompleter)
    protected AutoCompleteTextView recordPharmacyZone;

    @Bind(R.id.record_pharmacy_details)
    protected RecyclerView mRecyclerView;
    private RealmResults<PharmacyAddress> recordDetails;
    private RecyclerView.LayoutManager mLayoutManager;

    private User user;
    private Realm realm;
    private String uuid;
    private Ubigeo ubigeo;
    private Pharmacy pharmacy;
    private RecordPharmacy recordPharmacy;
    private UbigeoAutocompleterAdapter adapterUbigeo;
    private PharmacyAutocompleterAdapter adapterPharmacy;

    private PharmacyAddressListAdapter mAdapter;

    String latitude;
    String longitude;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_record_pharmacy);

        uuid = getIntent().getStringExtra(Constants.UUID);
        type = getIntent().getStringExtra(Constants.TYPE);

        ButterKnife.bind(this);
        toolbar.setTitle("Nueva Farmacia");

        realm = Realm.getDefaultInstance();
        user = realm.where(User.class).findFirst();
/*
        RealmResults<Ubigeo>  ubigeos = realm.where(Ubigeo.class).findAll();

        for(Ubigeo temp : ubigeos)
        {
            Log.d(TAG, "Ubigeo: "+temp.getId()+" , "+temp.getCode());
        }

        RealmResults<PharmacyAddress>  detalles = realm.where(PharmacyAddress.class).findAll();

        for(PharmacyAddress temp : detalles)
        {
            Log.d(TAG, "PharmacyAddress: "+temp.getId()+" , "+temp.getIdpharmacy()+" , "+temp.getIdubigeo());
        }
*/
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.address_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        recordPharmacyCategory.setVisibility(View.GONE);

        if(user.getRol().equals("REG"))
        {
            recordPharmacySave.setText("Guardar");
        }else{
            recordPharmacySave.setText("Aprobar");
        }

        recordDetails = realm.where(PharmacyAddress.class).equalTo("id",0).findAll();

        if(uuid != null){

            recordPharmacy = realm.where(RecordPharmacy.class).equalTo(Constants.UUID, uuid).findFirst();

            recordDetails = realm.where(PharmacyAddress.class).
                    equalTo("code",recordPharmacy.getRuc()).
                    equalTo("idubigeo",recordPharmacy.getIdzone()).
                    findAll();



            ubigeo = realm.where(Ubigeo.class).equalTo(Constants.ID, recordPharmacy.getIdzone()).findFirst();

            toolbar.setTitle(R.string.edit_doctor);
            toolbar.setSubtitle(recordPharmacy.getBusinessname());
            recordPharmacyCode.setText(recordPharmacy.getRuc());
            recordPharmacyBussinesname.setText(recordPharmacy.getBusinessname());
            recordPharmacyAddress.setText(recordPharmacy.getAddress());
            recordPharmacyCategory.setText(recordPharmacy.getScore());
            if(ubigeo != null){
                recordPharmacyZone.setText(ubigeo.getName());
            }
            spinner.setSelection(recordPharmacy.getTypeaddress()-1);
            recordPharmacyNumberAddress.setText(recordPharmacy.getNumberaddress());
            latitude = recordPharmacy.getLatitude();
            longitude = recordPharmacy.getLongitude();

            if(user.getRol().equals("SUP"))
            {
                recordPharmacyCancel.setText("Desaprobar");
                recordPharmacySave.setText("Aprobar");
            }
            
        }else{

            recordPharmacy = new RecordPharmacy();
            recordPharmacy.setType(1);
            recordPharmacy.setCheck(1);
            recordPharmacy.setUuid(UUID.randomUUID().toString());
            Date date = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            recordPharmacy.setCreated_at( df.format(date));
            recordPharmacy.setSent(false);
            recordPharmacy.setUser_id(user.getId());
            recordPharmacy.setUser(user.getName());
            recordPharmacy.setLongitude("");
            recordPharmacy.setLatitude("");
            latitude = "";
            longitude = "";
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(recordPharmacy);
            realm.commitTransaction();
        }

       // recordDetails = realm.where(PharmacyAddress.class).equalTo("idpharmacy", pharmacy.getId()).findAll();


        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new PharmacyAddressListAdapter(recordDetails, R.layout.record_detail_item);
        mRecyclerView.setAdapter(mAdapter);
        if(user.getRol().equals("REG")) {
            if (recordPharmacy.isSent()) {
                recordPharmacySave.setVisibility(View.GONE);
            }
        }

        setSupportActionBar(toolbar);

        adapterUbigeo = new UbigeoAutocompleterAdapter(this, R.layout.ubigeo_autocomplete_item);

        adapterPharmacy = new PharmacyAutocompleterAdapter(this, R.layout.doctor_autocomplete_item);

        recordPharmacyCode.setAdapter(adapterPharmacy);

        recordPharmacyCode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Integer temp = adapterPharmacy.getItem(position);

                pharmacy = realm.where(Pharmacy.class).equalTo(Constants.ID,
                        temp.intValue()).findFirst();
                recordPharmacyCode.setText(pharmacy.getCode());
                recordPharmacyBussinesname.setText(pharmacy.getName());

                if(ubigeo != null){

                    Log.d(TAG, "parametros completos ubigeo");
                    Log.d(TAG, String.valueOf(pharmacy.getId()));
                    Log.d(TAG, String.valueOf(ubigeo.getId()));
                    recordDetails = realm.where(PharmacyAddress.class)
                            .equalTo("idpharmacy", pharmacy.getId())
                            .equalTo("idubigeo", ubigeo.getId())
                            .findAll();
                    mAdapter.setData(recordDetails);
                    mAdapter.notifyDataSetChanged();
                }

                recordPharmacyAddress.requestFocus();

            }
        });

        recordPharmacyZone.setAdapter(adapterUbigeo);
        recordPharmacyZone.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Integer temp = adapterUbigeo.getItem(position);

                ubigeo = realm.where(Ubigeo.class).equalTo(Constants.ID,
                        temp.intValue()).findFirst();
                recordPharmacyZone.setText(ubigeo.getName());

                recordPharmacyCode.requestFocus();


                if(pharmacy != null){

                    Log.d(TAG, "parametros completos pharmacy");
                    Log.d(TAG, String.valueOf(pharmacy.getId()));
                    Log.d(TAG, String.valueOf(ubigeo.getId()));
                    recordDetails = realm.where(PharmacyAddress.class)
                            .equalTo("idpharmacy", pharmacy.getId())
                            .equalTo("idubigeo", ubigeo.getId())
                            .findAll();
                    mAdapter.setData(recordDetails);
                    mAdapter.notifyDataSetChanged();
                }

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(recordPharmacyCode, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        recordPharmacySave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int countErrors = 0;

                if(recordPharmacyCode.getText().length() != 11){
                    countErrors++;
                    recordPharmacyCode.setError("El RUC debe tener 11 digitos");
                }

                if (recordPharmacyBussinesname.getText().length() < 2) {
                    countErrors++;
                    recordPharmacyBussinesname.setError("La razón social es requerido.");
                }

                if (recordPharmacyBussinesname.getText().length() < 2) {
                    countErrors++;
                    recordPharmacyBussinesname.setError("La razón social es requerido.");
                }

                int recordPharmacyTypeId = spinner.getSelectedItemPosition()+1;

                    if (recordPharmacyZone.getText().length() < 2) {
                        countErrors++;
                        recordPharmacyZone.setError("La Zona es requerida.");
                    }

                    if (ubigeo == null) {
                        countErrors++;
                        recordPharmacyZone.setError("La Zona es incorrecta.");
                    }


             /*   RecordPharmacy tempRecordPharmacy= realm.where(RecordPharmacy.class)
                        .equalTo(Constants.CODE, recordPharmacyZone.getText().toString()).findFirst();

                if(tempRecordPharmacy != null){
                    if(!recordPharmacy.getUuid().equalsIgnoreCase(tempRecordPharmacy.getUuid())){
                        countErrors++;
                        recordPharmacyCode.setError("Ya existe una Institución con el mismo RUC");
                    }
                }
*/
                if (countErrors == 0) {

                    try {

                        realm.beginTransaction();

                        recordPharmacy.setRuc(recordPharmacyCode.getText().toString());
                        recordPharmacy.setBusinessname(recordPharmacyBussinesname.getText().toString());
                        recordPharmacy.setAddress(recordPharmacyAddress.getText().toString());
                        recordPharmacy.setScore(recordPharmacyCategory.getText().toString());
                        //recordPharmacy.setCreatedAt(new Date());
                        recordPharmacy.setIdzone(ubigeo.getId());
                        recordPharmacy.setSent(Boolean.FALSE);
                        recordPharmacy.setActive(Boolean.TRUE);
                        recordPharmacy.setUser_id(user.getId());
                        recordPharmacy.setNumberaddress(recordPharmacyNumberAddress.getText().toString());
                        recordPharmacy.setLatitude(latitude);
                        recordPharmacy.setLongitude(longitude);
                        recordPharmacy.setTypeaddress(spinner.getSelectedItemPosition()+1);
                        recordPharmacy.setAlert(false);

                        if(recordPharmacy.getUser() == null){
                            recordPharmacy.setUser(user.getName());
                        }

                        if(user.getRol().equals("SUP"))
                        {
                            recordPharmacy.setCheck(2);
                            recordPharmacy.setSent(false);
                        }

                        realm.copyToRealmOrUpdate(recordPharmacy);

                        realm.commitTransaction();

                        Toast.makeText(getApplicationContext(), Constants.SAVE_OK,
                                Toast.LENGTH_SHORT).show();

                        Intent data = new Intent();
                        data.putExtra(Constants.UUID, recordPharmacy.getUuid());

                        if (getParent() == null) {
                            setResult(Activity.RESULT_OK, data);
                        } else {
                            getParent().setResult(Activity.RESULT_OK, data);
                        }
                        finish();

                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    } finally {
                        realm.close();
                    }
                }
            }
        });

        recordPharmacyCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(user.getRol().equals("SUP")){

                    realm.beginTransaction();
                    recordPharmacy.setCheck(3);
                    recordPharmacy.setSent(false);
                    realm.copyToRealmOrUpdate(recordPharmacy);

                    realm.commitTransaction();

                }else{

                    if(!recordPharmacy.isSent()){

                        realm.beginTransaction();

                        RecordPharmacy recordTemp = realm.where(RecordPharmacy.class).
                                equalTo(Constants.UUID, recordPharmacy.getUuid()).findFirst();

                        if(recordTemp != null){
                            recordTemp.removeFromRealm();
                        }

                        realm.commitTransaction();
                    }


                }



                Intent intent = new Intent();
                if (getParent() == null) {
                    setResult(Activity.RESULT_OK, intent);
                } else {
                    getParent().setResult(Activity.RESULT_OK, intent);
                }
                realm.close();

                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_record_pharmacy, menu);
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
            case R.id.action_geolocalization:
                Intent intentGeo = new Intent(this, MapsActivity.class);
                intentGeo.putExtra(Constants.LATITUDE, latitude);
                intentGeo.putExtra(Constants.LONGITUDE, longitude);
                intentGeo.putExtra(Constants.TYPE,type);
                startActivityForResult(intentGeo, ADD_GEOLOCALIZATION_REQUEST);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_GEOLOCALIZATION_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {

                Log.d(TAG, "Geoloc OK");
                String lat = data.getStringExtra(Constants.LATITUDE);
                String lon = data.getStringExtra(Constants.LONGITUDE);

                Log.d(TAG, "Latitude: "+lat);
                Log.d(TAG, "Longitude: "+lon);

                latitude = lat;
                longitude = lon;
            }
        }

    }

    private void doExit() {
    }

    @Override
    public void onBackPressed() {
        if(type.equals("edit")){
        if(user.getRol().equals("SUP")){
            super.onBackPressed();
        }}
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK) {
            doExit();
        }
        return super.onKeyDown(keyCode, event);
    }

    public class PharmacyAddressListAdapter extends RecyclerView.
            Adapter<PharmacyAddressListAdapter.ViewHolder> {

        private List<PharmacyAddress> items;
        private int itemLayout;

        public PharmacyAddressListAdapter(List<PharmacyAddress> items, int itemLayout) {
            this.items = items;
            this.itemLayout = itemLayout;
        }

        public void setData(List<PharmacyAddress> items) {
            this.items = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(itemLayout,
                    parent, false);
            ViewHolder viewHolder = new ViewHolder(itemLayoutView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            PharmacyAddress item = items.get(position);
            viewHolder.name.setText(item.getName());
            viewHolder.qty.setText(item.getAddress());
            viewHolder.id = item.getId();
            viewHolder.itemView.setTag(item);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
                implements RecyclerView.OnClickListener {

            public TextView name;
            public TextView qty;
            public int id;

            public ViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.record_detail_name);
                qty  = (TextView) itemView.findViewById(R.id.record_detail_qty);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {

                if(user.getRol().equals("SUP")) {

                    new AlertDialog.Builder(getSupportActionBar().getThemedContext())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Asignación de Dirección")
                            .setMessage("¿Está seguro que desea asignar la dirección?")
                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    realm.beginTransaction();
                                    recordPharmacy.setIdpharmacydetail(id);
                                    recordPharmacy.setCheck(2);

                                    recordPharmacy.setRuc(recordPharmacyCode.getText().toString());
                                    recordPharmacy.setBusinessname(recordPharmacyBussinesname.getText().toString());
                                    recordPharmacy.setAddress(recordPharmacyAddress.getText().toString());
                                    recordPharmacy.setScore(recordPharmacyCategory.getText().toString());
                                    //recordPharmacy.setCreatedAt(new Date());
                                    recordPharmacy.setIdzone(ubigeo.getId());
                                    recordPharmacy.setSent(Boolean.FALSE);
                                    recordPharmacy.setActive(Boolean.TRUE);
                                    recordPharmacy.setUser_id(user.getId());
                                    recordPharmacy.setNumberaddress(recordPharmacyNumberAddress.getText().toString());
                                    recordPharmacy.setLatitude(latitude);
                                    recordPharmacy.setLongitude(longitude);
                                    recordPharmacy.setTypeaddress(spinner.getSelectedItemPosition()+1);
                                    recordPharmacy.setAlert(false);

                                    realm.copyToRealmOrUpdate(recordPharmacy);

                                    realm.commitTransaction();

                                    Toast.makeText(getApplicationContext(), "Se asignó esta dirección al registro.",
                                            Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent();
                                    if (getParent() == null) {
                                        setResult(Activity.RESULT_OK, intent);
                                    } else {
                                        getParent().setResult(Activity.RESULT_OK, intent);
                                    }
                                    realm.close();

                                    finish();

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                            .show();

                }else{
                    Toast.makeText(getApplicationContext(), "El supervisor asignará la dirección.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}