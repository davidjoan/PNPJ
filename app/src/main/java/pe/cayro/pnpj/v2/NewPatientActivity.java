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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import pe.cayro.pnpj.v2.adapter.UbigeoAutocompleterAdapter;
import pe.cayro.pnpj.v2.model.Patient;
import pe.cayro.pnpj.v2.model.Ubigeo;
import pe.cayro.pnpj.v2.model.User;
import pe.cayro.pnpj.v2.util.Constants;

public class NewPatientActivity extends AppCompatActivity {

    private static String TAG = NewPatientActivity.class.getSimpleName();

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.patient_save)
    protected Button patientSave;
    @Bind(R.id.patient_code)
    protected EditText patientCode;
    @Bind(R.id.patient_firstname)
    protected EditText patientFirstname;
    @Bind(R.id.patient_lastname)
    protected EditText patientLastname;
    @Bind(R.id.patient_surname)
    protected EditText patientSurname;

    @Bind(R.id.patient_mail)
    protected EditText patientEmail;
    @Bind(R.id.patient_phone)
    protected EditText patientPhone;
    @Bind(R.id.patient_address)
    protected EditText patientAddress;
    @Bind(R.id.patient_ubigeo_autocompleter)
    protected AppCompatAutoCompleteTextView patientUbigeo;

    private User user;
    private Realm realm;
    private Patient patient;
    private Ubigeo ubigeo = null;
    private UbigeoAutocompleterAdapter adapterUbigeo;

    private String uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_patient);

        uuid = getIntent().getStringExtra(Constants.UUID);

        ButterKnife.bind(this);
        toolbar.setTitle(R.string.title_activity_new_patient);

        realm = Realm.getDefaultInstance();

        if(uuid != null){
            patient = realm.where(Patient.class).equalTo(Constants.UUID, uuid).findFirst();
            ubigeo = patient.getUbigeo();
            toolbar.setTitle(R.string.edit_patient);
            toolbar.setSubtitle(patient.getLastname() + " " + patient.getSurname());

            patientCode.setText(patient.getCode());
            patientFirstname.setText(patient.getFirstname());
            patientLastname.setText(patient.getLastname());
            patientSurname.setText(patient.getSurname());
            patientAddress.setText(patient.getAddress());
            patientUbigeo.setText(ubigeo.getName());
            patientPhone.setText(patient.getPhone());
            patientEmail.setText(patient.getEmail());

        }else{
            patient = new Patient();
            patient.setUuid(UUID.randomUUID().toString());
            patient.setSent(false);
        }

        user =realm.where(User.class).findFirst();

        setSupportActionBar(toolbar);

        adapterUbigeo = new UbigeoAutocompleterAdapter(this, R.layout.ubigeo_autocomplete_item);
        patientUbigeo.setAdapter(adapterUbigeo);
        patientUbigeo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Integer temp = adapterUbigeo.getItem(position);
                ubigeo = realm.where(Ubigeo.class).equalTo(Constants.ID, temp.intValue()).findFirst();
                patientUbigeo.setText(ubigeo.getName());
            }
        });

        patientSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int countErrors = 0;

                if(patientUbigeo.getText().length() < 2){
                    countErrors++;
                    patientUbigeo.setError("El Distrito es requerido.");
                }
                if(ubigeo == null){
                    countErrors++;
                    patientUbigeo.setError("El Distrito es incorrecto.");
                }
                if(patientFirstname.getText().length() < 2){
                    countErrors++;
                    patientFirstname.setError("El Nombre es requerido.");
                }
                if(patientLastname.getText().length() < 2){
                    countErrors++;
                    patientLastname.setError("El Apellido Paterno es requerido.");
                }
                if(patientSurname.getText().length() < 2){
                    countErrors++;
                    patientSurname.setError("El Apellido Materno es requerido.");
                }

                if(patientCode.getText().length() != 8){
                    countErrors++;
                    patientCode.setError("El DNI debe tener 8 digitos");
                }

                if(!patientCode.getText().toString().equals(Constants.DNI_DEFAULT)){

                    Patient tempPatient = realm.where(Patient.class)
                            .equalTo(Constants.CODE, patientCode.getText().toString()).findFirst();

                    if(tempPatient != null){

                        if (!patient.getUuid().equals(tempPatient.getUuid())){
                            countErrors++;
                            patientCode.setError("Ya existe un paciente con este DNI");
                        }

                    }

                }



                if(countErrors == 0){

                    try {

                        realm.beginTransaction();
                        //Patient patient = realm.createObject(Patient.class);
                        patient.setCode(patientCode.getText().toString());

                        patient.setFirstname(patientFirstname.getText().toString());
                        patient.setLastname(patientLastname.getText().toString());
                        patient.setSurname(patientSurname.getText().toString());

                        patient.setAddress(patientAddress.getText().toString());

                        patient.setPhone(patientPhone.getText().toString());
                        patient.setEmail(patientEmail.getText().toString());

                        patient.setActive(Boolean.TRUE);
                        patient.setSent(Boolean.FALSE);
                        patient.setUser(user);
                        patient.setUserId(user.getId());
                        patient.setCreatedAt(new Date());

                        if(ubigeo != null){
                            patient.setUbigeoId(ubigeo.getId());
                            patient.setUbigeo(ubigeo);
                        }

                        realm.copyToRealmOrUpdate(patient);
                        realm.commitTransaction();

                        Toast.makeText(getApplicationContext(), Constants.SAVE_OK,
                                Toast.LENGTH_SHORT).show();

                        Intent data = new Intent();

                        data.putExtra(Constants.UUID, patient.getUuid());

                        if (getParent() == null) {
                            setResult(Activity.RESULT_OK, data);
                        } else {
                            getParent().setResult(Activity.RESULT_OK, data);
                        }
                        finish();

                    }catch (Exception e){
                        Log.e(TAG, e.toString());
                    }finally {
                        realm.close();
                    }
                }
            }
        });
    }
}
