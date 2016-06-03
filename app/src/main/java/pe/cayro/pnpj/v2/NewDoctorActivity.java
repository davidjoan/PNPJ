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
import pe.cayro.pnpj.v2.adapter.SpecialtyAutocompleterAdapter;
import pe.cayro.pnpj.v2.model.Doctor;
import pe.cayro.pnpj.v2.model.Specialty;
import pe.cayro.pnpj.v2.util.Constants;

public class NewDoctorActivity extends AppCompatActivity {

    private static String TAG = NewDoctorActivity.class.getSimpleName();

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.doctor_save)
    protected Button doctorSave;
    @Bind(R.id.doctor_code)
    protected EditText doctorCode;
    @Bind(R.id.doctor_firstname)
    protected EditText doctorFirstname;
    @Bind(R.id.doctor_lastname)
    protected EditText doctorLastname;
    @Bind(R.id.doctor_surname)
    protected EditText doctorSurname;

    @Bind(R.id.doctor_specialty_autocompleter)
    protected AppCompatAutoCompleteTextView doctorSpecialty;

    private Realm realm;
    private String uuid;
    private Doctor doctor;
    private Specialty specialty;
    private SpecialtyAutocompleterAdapter adapterSpecialty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_doctor);

        uuid = getIntent().getStringExtra(Constants.UUID);

        ButterKnife.bind(this);
        toolbar.setTitle(R.string.title_activity_new_doctor);

        realm = Realm.getDefaultInstance();

        if(uuid != null){
            doctor = realm.where(Doctor.class).equalTo(Constants.UUID, uuid).findFirst();

            toolbar.setTitle(R.string.edit_doctor);
            toolbar.setSubtitle(doctor.getLastname() + " " + doctor.getSurname());
            specialty = doctor.getSpecialty();
            doctorCode.setText(doctor.getCode());
            doctorFirstname.setText(doctor.getFirstname());
            doctorLastname.setText(doctor.getLastname());
            doctorSurname.setText(doctor.getSurname());
            doctorSpecialty.setText(specialty.getName());

        }else{
            doctor = new Doctor();
            doctor.setUuid(UUID.randomUUID().toString());
            doctor.setSent(false);
            doctor.setCreatedAt(new Date());
        }

        setSupportActionBar(toolbar);



        adapterSpecialty = new SpecialtyAutocompleterAdapter(this,
                R.layout.specialty_autocomplete_item);
        doctorSpecialty.setAdapter(adapterSpecialty);
        doctorSpecialty.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Integer temp = adapterSpecialty.getItem(position);

                specialty = realm.where(Specialty.class).equalTo(Constants.ID,
                        temp.intValue()).findFirst();
                doctorSpecialty.setText(specialty.getName());
            }
        });

        doctorSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int countErrors = 0;

                if (doctorCode.getText().length() < 2) {
                    countErrors++;
                    doctorCode.setError("El Código CMP es requerido.");
                }
                if(doctorCode.getText().length() > 7){
                    countErrors++;
                    doctorCode.setError("El Código CMP es demasiado largo.");
                }
                if (doctorFirstname.getText().length() < 2) {
                    countErrors++;
                    doctorFirstname.setError("El Nombre es requerido.");
                }

                if (doctorLastname.getText().length() < 2) {
                    countErrors++;
                    doctorLastname.setError("El Apellido Paterno es requerido.");
                }

                if (doctorSurname.getText().length() < 2) {
                    countErrors++;
                    doctorSurname.setError("El Apellido Materno es requerido.");
                }

                if (doctorSpecialty.getText().length() < 2) {
                    countErrors++;
                    doctorSpecialty.setError("La Especialidad es requerida.");
                }

                if (specialty == null) {
                    countErrors++;
                    doctorSpecialty.setError("La Especialidad es incorrecta.");
                }

                Doctor tempDoctor = realm.where(Doctor.class)
                        .equalTo(Constants.CODE, doctorCode.getText().toString()).findFirst();

                if(tempDoctor != null){
                    if(!doctor.getUuid().equalsIgnoreCase(tempDoctor.getUuid())){
                        countErrors++;
                        doctorCode.setError("Ya existe un médico con este CMP");
                    }
                }

                if (countErrors == 0) {

                    try {

                        realm.beginTransaction();

                        doctor.setCode(doctorCode.getText().toString());
                        doctor.setFirstname(doctorFirstname.getText().toString());
                        doctor.setLastname(doctorLastname.getText().toString());
                        doctor.setSurname(doctorSurname.getText().toString());
                        doctor.setScore("X");
                        doctor.setCreatedAt(new Date());
                        doctor.setSpecialty(specialty);
                        doctor.setSpecialtyId(specialty.getId());
                        doctor.setSent(Boolean.FALSE);
                        doctor.setActive(Boolean.TRUE);

                        realm.copyToRealmOrUpdate(doctor);
                        realm.commitTransaction();

                        Toast.makeText(getApplicationContext(), Constants.SAVE_OK,
                                Toast.LENGTH_SHORT).show();

                        Intent data = new Intent();
                        data.putExtra(Constants.UUID, doctor.getUuid());

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
    }
}
