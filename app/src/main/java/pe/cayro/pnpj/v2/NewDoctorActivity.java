package pe.cayro.pnpj.v2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import pe.cayro.pnpj.v2.adapter.DoctorCloseupAutocompleterAdapter;
import pe.cayro.pnpj.v2.adapter.SpecialtyAutocompleterAdapter;
import pe.cayro.pnpj.v2.model.Doctor;
import pe.cayro.pnpj.v2.model.DoctorType;
import pe.cayro.pnpj.v2.model.DoctorsCloseUp;
import pe.cayro.pnpj.v2.model.Specialty;
import pe.cayro.pnpj.v2.model.User;
import pe.cayro.pnpj.v2.util.Constants;

public class NewDoctorActivity extends AppCompatActivity {

    private static String TAG = NewDoctorActivity.class.getSimpleName();

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.doctor_save)
    protected Button doctorSave;
    @Bind(R.id.doctor_cancel)
    protected Button recordCancel;
    @Bind(R.id.doctor_code_autocompleter)
    protected AutoCompleteTextView doctorCode;
    @Bind(R.id.doctor_firstname)
    protected EditText doctorFirstname;
    @Bind(R.id.doctor_lastname)
    protected EditText doctorLastname;
    @Bind(R.id.doctor_surname)
    protected EditText doctorSurname;

    @Bind(R.id.doctor_doctor_type_spinner)
    protected Spinner spinner;

    @Bind(R.id.doctor_zone_spinner)
    protected Spinner spinnerZone;

    @Bind(R.id.doctor_sex_spinner)
    protected Spinner spinnerSex;

    @Bind(R.id.doctor_phone)
    protected EditText doctorPhone;

    @Bind(R.id.doctor_email)
    protected EditText doctorEmail;

    @Bind(R.id.doctor_reference)
    protected  EditText doctorReference;

    @Bind(R.id.doctor_category)
    protected EditText doctorCategory;

    @Bind(R.id.doctor_cantsurgery)
    protected EditText doctorCantsurgery;

    @Bind(R.id.doctor_canthospital)
    protected EditText doctorCanthospital;

    @Bind(R.id.doctor_specialty_autocompleter)
    protected AppCompatAutoCompleteTextView doctorSpecialty;

    private Realm realm;
    private String uuid;
    private User user;
    private Doctor doctor;
    private DoctorsCloseUp doctorsCloseUp;
    private Specialty specialty;
    private SpecialtyAutocompleterAdapter adapterSpecialty;
    private DoctorCloseupAutocompleterAdapter adapterCloseup;
    private boolean alert = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_doctor);

        uuid = getIntent().getStringExtra(Constants.UUID);

        ButterKnife.bind(this);
        toolbar.setTitle(R.string.title_activity_new_doctor);

        realm = Realm.getDefaultInstance();
        user = realm.where(User.class).findFirst();

        adapterCloseup = new DoctorCloseupAutocompleterAdapter(this, R.layout.doctor_autocomplete_item);
        doctorCode.setAdapter(adapterCloseup);
        doctorCode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String temp = adapterCloseup.getItem(position);
                doctorsCloseUp = realm.where(DoctorsCloseUp.class).equalTo(Constants.ID, temp).findFirst();

                if(doctorsCloseUp.isFichero()){

                    Toast.makeText(getApplicationContext(), Constants.SAVE_OK,
                            Toast.LENGTH_SHORT).show();
                    alert = false;
                }else{
                    doctorCode.setText(doctorsCloseUp.getId());
                    doctorFirstname.setText(doctorsCloseUp.getName());
                    doctorLastname.setText(doctorsCloseUp.getLastname());
                    doctorSurname.setText(doctorsCloseUp.getSurname());
                    alert = false;

                    if(doctorsCloseUp.getSpecialty() != null)
                    {
                        specialty = doctorsCloseUp.getSpecialty();
                        doctorSpecialty.setText(specialty.getName());
                    }

                }


            }
        });

        doctorCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (doctorCode.getText().length() == 0) {
                    doctorsCloseUp = null;
                }

            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.doctor_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setSelection(0);

        //Spinner Zone
        ArrayAdapter<CharSequence> adapterZone = ArrayAdapter.createFromResource(this,
                R.array.doctor_zone_array, android.R.layout.simple_spinner_item);
        adapterZone.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerZone.setAdapter(adapterZone);

        spinnerZone.setSelection(0);

        //Spinner Sex
        ArrayAdapter<CharSequence> adapterSex = ArrayAdapter.createFromResource(this,
                R.array.doctor_sex_array, android.R.layout.simple_spinner_item);
        adapterSex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSex.setAdapter(adapterSex);

        spinnerSex.setSelection(0);
        doctorCategory.setVisibility(View.GONE);

        if(user.getRol().equals("REG"))
        {
            doctorSave.setText("Guardar");

        }

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
            doctorCategory.setText(doctor.getScore());
            doctorCantsurgery.setText(String.valueOf(doctor.getCantsurgery()));
            doctorCanthospital.setText(String.valueOf(doctor.getCanthospital()));
            doctorPhone.setText(doctor.getPhone());
            doctorEmail.setText(doctor.getMail());
            doctorReference.setText(doctor.getReference());

            spinner.setSelection(doctor.getDoctorTypeId()-1);

            spinnerZone.setSelection(doctor.getZone()-1);

            spinnerSex.setSelection(doctor.getSex()-1);

            alert = doctor.isAlert();

            if(user.getRol().equals("SUP"))
            {
                recordCancel.setText("Desaprobar");
                doctorSave.setText("Aprobar");
            }

            doctorFirstname.requestFocus();


        }else{
            doctor = new Doctor();
            doctor.setUuid(UUID.randomUUID().toString());
            doctor.setSent(false);
            doctor.setCreatedAt(new Date());
            doctorCantsurgery.setText("0");
            doctorCanthospital.setText("0");
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

                int doctorTypeId = spinner.getSelectedItemPosition()+1;


                    if (doctorSpecialty.getText().length() < 2) {
                        countErrors++;
                        doctorSpecialty.setError("La Especialidad es requerida.");
                    }

                    if (specialty == null) {
                        countErrors++;
                        doctorSpecialty.setError("La Especialidad es incorrecta.");
                    }
/*
                if(!user.getRol().equals("REG")) {

                    if (doctorCategory.getText().length() < 2) {
                        countErrors++;
                        doctorCategory.setError("La Categoría es requerida.");
                    }
                }*/

                Doctor tempDoctor = realm.where(Doctor.class)
                        .equalTo(Constants.CODE, doctorCode.getText().toString()).findFirst();

                if(tempDoctor != null){
                    if(!doctor.getUuid().equalsIgnoreCase(tempDoctor.getUuid())){
                        countErrors++;
                        doctorCode.setError("Ya existe un médico con este CMP");
                    }
                }

                Pattern pattern = Pattern.compile(".+@.+\\.[a-z]+");
                Matcher matcher = pattern.matcher(doctorEmail.getText().toString());

                if(!matcher.matches())
                {
                    countErrors++;
                    doctorEmail.setError("El Correo Electrónico es incorrecto.");
                }

                if (countErrors == 0) {

                    try {

                        realm.beginTransaction();

                        DoctorType doctorType  = realm.where(DoctorType.class).equalTo(Constants.ID, doctorTypeId).findFirst();
                        doctor.setDoctorType(doctorType);
                        doctor.setDoctorTypeId(doctorTypeId);
                        doctor.setDoctorTypeId(spinner.getSelectedItemPosition()+1);
                        doctor.setCode(doctorCode.getText().toString());
                        doctor.setFirstname(doctorFirstname.getText().toString());
                        doctor.setLastname(doctorLastname.getText().toString());
                        doctor.setSurname(doctorSurname.getText().toString());
                        doctor.setScore(doctorCategory.getText().toString());
                        //doctor.setCreatedAt(new Date());
                        doctor.setSpecialty(specialty);
                        doctor.setSpecialtyId(specialty.getId());
                        doctor.setSent(Boolean.FALSE);
                        doctor.setActive(Boolean.TRUE);
                        doctor.setUserId(user.getId());
                        doctor.setPhone(doctorPhone.getText().toString());
                        doctor.setMail(doctorEmail.getText().toString());
                        doctor.setReference(doctorReference.getText().toString());
                        doctor.setCheck(1);
                        doctor.setAlert(alert);

                        if(doctorCanthospital.getText().toString().equals("")){
                            doctor.setCanthospital(0);
                        }else{
                            doctor.setCanthospital(Integer.valueOf(doctorCanthospital.getText().toString()));
                        }

                        if(doctorCantsurgery.getText().toString().equals("")){
                            doctor.setCantsurgery(0);
                        }else{
                            doctor.setCantsurgery(Integer.valueOf(doctorCantsurgery.getText().toString()));
                        }

                        doctor.setSex(spinnerSex.getSelectedItemPosition()+1);
                        doctor.setZone(spinnerZone.getSelectedItemPosition()+1);

                        if(doctor.getUser() == null){
                            doctor.setUser(user.getName());
                        }

                        if(user.getRol().equals("SUP")){

                                doctor.setCheck(2);
                            doctor.setSent(false);

                        }

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

        recordCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(user.getRol().equals("SUP")){

                    realm.beginTransaction();
                    doctor.setCheck(3);
                    doctor.setSent(false);
                    realm.copyToRealmOrUpdate(doctor);
                    realm.commitTransaction();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_pending:
                item.setChecked(item.isChecked()?false:true);
                return true;
            case R.id.action_aproved:
                item.setChecked(item.isChecked()?false:true);
                return true;
            case R.id.action_upaproved:
                item.setChecked(item.isChecked()?false:true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void clearDoctor(View v) {
        doctorCode.setText(Constants.EMPTY);
        doctorFirstname.setText(Constants.EMPTY);
        doctorLastname.setText(Constants.EMPTY);
        doctorSurname.setText(Constants.EMPTY);
        doctorSpecialty.setText(Constants.EMPTY);
    }
}