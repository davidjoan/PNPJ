package pe.cayro.pnpj.v2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Date;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import pe.cayro.pnpj.v2.adapter.ZoneAutocompleterAdapter;
import pe.cayro.pnpj.v2.model.Institution;
import pe.cayro.pnpj.v2.model.InstitutionTypes;
import pe.cayro.pnpj.v2.model.InstitutionZone;
import pe.cayro.pnpj.v2.model.User;
import pe.cayro.pnpj.v2.util.Constants;

public class NewInstitutionActivity extends AppCompatActivity {

    private static String TAG = NewInstitutionActivity.class.getSimpleName();

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.institution_save)
    protected Button institutionSave;
    @Bind(R.id.institution_cancel)
    protected Button institutionCancel;
    @Bind(R.id.institution_code)
    protected EditText institutionCode;
    @Bind(R.id.institution_bussinessname)
    protected EditText institutionBussinesname;
    @Bind(R.id.institution_office)
    protected EditText institutionOffice;
    @Bind(R.id.institution_address)
    protected EditText institutionAddress;
    @Bind(R.id.institution_category)
    protected EditText institutionCategory;
    @Bind(R.id.institution_phone)
    protected EditText institutionPhone;
    @Bind(R.id.institution_web)
    protected EditText institutionWeb;
    @Bind(R.id.institution_institution_type_spinner)
    protected Spinner spinner;
    @Bind(R.id.institution_zone_autocompleter)
    protected AppCompatAutoCompleteTextView institutionZone;

    private Realm realm;
    private String uuid;
    private User user;
    private Institution institution;
    private InstitutionZone zone;
    private InstitutionTypes type;
    private ZoneAutocompleterAdapter adapterZone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_institution);

        uuid = getIntent().getStringExtra(Constants.UUID);

        ButterKnife.bind(this);
        toolbar.setTitle("Nueva Institución");

        realm = Realm.getDefaultInstance();
        user = realm.where(User.class).findFirst();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.institution_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setSelection(0);

        ////////////
        if(user.getRol().equals("REG"))
        {
            institutionSave.setText("Guardar");
            institutionCategory.setVisibility(View.GONE);

        }else{
            institutionSave.setText("Aprobar");
        }

        if(uuid != null){

            institution = realm.where(Institution.class).equalTo(Constants.UUID, uuid).findFirst();

            toolbar.setTitle(R.string.edit_doctor);
            toolbar.setSubtitle(institution.getBusinessname()+ " " + institution.getOffice());
            zone = institution.getInstitutionZone();
            type = institution.getInstitutionTypes();
            institutionCode.setText(institution.getRuc());
            institutionBussinesname.setText(institution.getBusinessname());
            institutionOffice.setText(institution.getOffice());
            institutionAddress.setText(institution.getAddress());
            institutionCategory.setText(institution.getScore());

            institutionZone.setText(zone.getName()+" "+zone.getProvince());
            institutionPhone.setText(institution.getPhone());
            institutionWeb.setText(institution.getWeb());

            spinner.setSelection(institution.getInstitutionTypeId()-1);

            if(user.getRol().equals("SUP"))
            {
                if(institution.isCheck()){
                    institutionSave.setText("Desaprobar");
                }else{
                    institutionSave.setText("Aprobar");
                }


            }




        }else{
            institution = new Institution();
            institution.setUuid(UUID.randomUUID().toString());
            institution.setSent(false);
            institution.setCreatedAt(new Date());
            institution.setEdit(true);
        }


        if(!institution.isEdit()){
            institutionSave.setVisibility(View.GONE);
        }

        setSupportActionBar(toolbar);

        adapterZone = new ZoneAutocompleterAdapter(this,
                R.layout.ubigeo_autocomplete_item);

        institutionZone.setAdapter(adapterZone);
        institutionZone.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Integer temp = adapterZone.getItem(position);

                zone = realm.where(InstitutionZone.class).equalTo(Constants.ID,
                        temp.intValue()).findFirst();
                institutionZone.setText(zone.getName());
            }
        });

        institutionSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int countErrors = 0;

                if(institutionCode.getText().length() != 11){
                    countErrors++;
                    institutionCode.setError("El RUC debe tener 11 digitos");
                }

                if (institutionBussinesname.getText().length() < 2) {
                    countErrors++;
                    institutionBussinesname.setError("La razón social es requerido.");
                }

                if (institutionOffice.getText().length() < 2) {
                    countErrors++;
                    institutionOffice.setError("El nombre de local es requerido.");
                }

                int institutionTypeId = spinner.getSelectedItemPosition()+1;

                    if (institutionZone.getText().length() < 2) {
                        countErrors++;
                        institutionZone.setError("La Zona es requerida.");
                    }

                    if (zone == null) {
                        countErrors++;
                        institutionZone.setError("La Zona es incorrecta.");
                    }

                if(!user.getRol().equals("REG")) {

                    if (institutionCategory.getText().length() < 2) {
                        countErrors++;
                        institutionCategory.setError("La Categoría es requerida.");
                    }
                }

                Institution tempInstitution= realm.where(Institution.class)
                        .equalTo(Constants.CODE, institutionZone.getText().toString()).findFirst();

                if(tempInstitution != null){
                    if(!institution.getUuid().equalsIgnoreCase(tempInstitution.getUuid())){
                        countErrors++;
                        institutionCode.setError("Ya existe una Institución con el mismo RUC");
                    }
                }

                if (countErrors == 0) {

                    try {

                        realm.beginTransaction();

                        InstitutionTypes institutionType  = realm.where(InstitutionTypes.class).equalTo(Constants.ID, institutionTypeId).findFirst();
                        institution.setInstitutionTypeId(institutionTypeId);
                        institution.setInstitutionTypes(institutionType);


                        institution.setRuc(institutionCode.getText().toString());
                        institution.setBusinessname(institutionBussinesname.getText().toString());
                        institution.setOffice(institutionOffice.getText().toString());
                        institution.setAddress(institutionAddress.getText().toString());
                        institution.setScore(institutionCategory.getText().toString());
                        institution.setPhone(institutionPhone.getText().toString());
                        institution.setWeb(institutionWeb.getText().toString());
                        //institution.setCreatedAt(new Date());
                        institution.setInstitutionZone(zone);
                        institution.setInstitutionZoneId(zone.getId());
                        institution.setSent(Boolean.FALSE);
                        institution.setActive(Boolean.TRUE);
                        institution.setUserId(user.getId());

                        if(institution.getUser() == null){
                            institution.setUser(user.getName());
                        }



                        if(user.getRol().equals("SUP")){
                            if(institution.isCheck()){
                                institution.setCheck(Boolean.FALSE);

                            }else{
                                institution.setCheck(Boolean.TRUE);
                            }
                        }



                        realm.copyToRealmOrUpdate(institution);

                        realm.commitTransaction();

                        Toast.makeText(getApplicationContext(), Constants.SAVE_OK,
                                Toast.LENGTH_SHORT).show();

                        Intent data = new Intent();
                        data.putExtra(Constants.UUID, institution.getUuid());

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

        institutionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
}