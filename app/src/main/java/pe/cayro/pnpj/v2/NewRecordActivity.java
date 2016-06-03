package pe.cayro.pnpj.v2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import pe.cayro.pnpj.v2.adapter.DoctorAutocompleterAdapter;
import pe.cayro.pnpj.v2.adapter.PatientAutocompleterAdapter;
import pe.cayro.pnpj.v2.adapter.UbigeoAutocompleterAdapter;
import pe.cayro.pnpj.v2.model.Agent;
import pe.cayro.pnpj.v2.model.AttentionType;
import pe.cayro.pnpj.v2.model.Doctor;
import pe.cayro.pnpj.v2.model.Institution;
import pe.cayro.pnpj.v2.model.Patient;
import pe.cayro.pnpj.v2.model.Record;
import pe.cayro.pnpj.v2.model.RecordDetail;
import pe.cayro.pnpj.v2.model.Tracking;
import pe.cayro.pnpj.v2.model.Ubigeo;
import pe.cayro.pnpj.v2.model.User;
import pe.cayro.pnpj.v2.util.Constants;
import pe.cayro.pnpj.v2.util.RucValidator;

public class NewRecordActivity extends AppCompatActivity {

    private static String TAG = NewRecordActivity.class.getSimpleName();

    static final int ADD_DOCTOR_REQUEST = 1;
    static final int ADD_PATIENT_REQUEST = 2;
    static final int ADD_MEDICAL_SAMPLE_REQUEST = 3;
    static final int ADD_AGENT_REQUEST = 4;

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.record_attention_type_spinner)
    protected Spinner spinner;

    @Bind(R.id.record_ruc)
    protected EditText recordRuc;
    @Bind(R.id.record_serial)
    protected EditText recordSerial;
    @Bind(R.id.record_voucher)
    protected EditText recordVoucher;
    @Bind(R.id.record_cancel_button)
    protected Button recordCancel;

    @Bind(R.id.record_code)
    protected EditText recordCode;
    @Bind(R.id.record_date)
    protected EditText editTextDate;
    @Bind(R.id.record_sale_date)
    protected EditText editTextSaleDate;
    @Bind(R.id.record_doctor_autocompleter)
    protected AppCompatAutoCompleteTextView recordDoctor;
    @Bind(R.id.record_patient_autocompleter)
    protected AppCompatAutoCompleteTextView recordPatient;
    @Bind(R.id.record_ubigeo_autocompleter)
    protected AppCompatAutoCompleteTextView recordUbigeo;

    private long code = 1;
    private User user;
    private Realm realm;
    private Record record;
    private Doctor doctor;
    private Ubigeo ubigeo;
    private Calendar start;
    private Calendar sale;
    private Patient patient;
    private Tracking tracking;
    private String trackingUuid;
    private SimpleDateFormat sdf;
    private SimpleDateFormat format;
    private AttentionType attentionType;
    private UbigeoAutocompleterAdapter adapterUbigeo;
    private DoctorAutocompleterAdapter adapterDoctor;
    private PatientAutocompleterAdapter adapterPatient;

    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_record);

        trackingUuid = getIntent().getStringExtra(Constants.TRACKING_UUID);

        format = new SimpleDateFormat(Constants.FORMAT_DATE_SLASH);
        sdf    = new SimpleDateFormat(Constants.FORMAT_DATE);

        ButterKnife.bind(this);

        settings = getSharedPreferences(Constants.PREFERENCES_SAM, 0);

        start = Calendar.getInstance();
        sale = Calendar.getInstance();
        realm = Realm.getDefaultInstance();

        tracking = realm.where(Tracking.class).equalTo(Constants.UUID, trackingUuid).findFirst();

        RealmResults<Record> results = realm.where(Record.class).findAll();
        if(results.size() > 0 ){
            code = results.max(Constants.CODE).longValue()+1;

            if((long)tracking.getCode() > code){
                code = (long)tracking.getCode();
            }
        }else{
            code = (long)tracking.getCode();
        }

        toolbar.setTitle(R.string.new_record);
        toolbar.setSubtitle(tracking.getInstitution().getName());
        setSupportActionBar(toolbar);

        //Preparing Object
        record = new Record();
        record.setUuid(UUID.randomUUID().toString());
        record.setInstitutionId(tracking.getInstitutionId());
        record.setRecordDate(new Date());
        record.setSaleDate(new Date());
        record.setCode((int) code);

        attentionType = realm.where(AttentionType.class).equalTo(Constants.ID,
                2).findFirst();

        record.setAttentionTypeId(attentionType.getId());
        record.setAttentionType(attentionType);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.attention_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setSelection(1);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "Tipo de Atención: " + String.valueOf(i));
                attentionType = realm.where(AttentionType.class).equalTo(Constants.ID,
                        i + 1).findFirst();


                switch (i) {
                    case 0:
                        findViewById(R.id.header_finish_treatment).setVisibility(View.GONE);
                        //  findViewById(R.id.body_finish_treatment_1).setVisibility(View.GONE);
                        findViewById(R.id.body_finish_treatment_2).setVisibility(View.GONE);
                        findViewById(R.id.body_finish_treatment_3).setVisibility(View.GONE);
                        findViewById(R.id.body_patient).setVisibility(View.VISIBLE);

                        break;
                    case 1:
                        findViewById(R.id.header_finish_treatment).setVisibility(View.VISIBLE);
                        //  findViewById(R.id.body_finish_treatment_1).setVisibility(View.VISIBLE);
                        findViewById(R.id.body_finish_treatment_2).setVisibility(View.VISIBLE);
                        findViewById(R.id.body_finish_treatment_3).setVisibility(View.VISIBLE);
                        findViewById(R.id.body_patient).setVisibility(View.VISIBLE);

                        break;
                    case 2:
                        findViewById(R.id.header_finish_treatment).setVisibility(View.GONE);
                        //    findViewById(R.id.body_finish_treatment_1).setVisibility(View.GONE);
                        findViewById(R.id.body_finish_treatment_2).setVisibility(View.GONE);
                        findViewById(R.id.body_finish_treatment_3).setVisibility(View.GONE);
                        findViewById(R.id.body_patient).setVisibility(View.GONE);

                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d(TAG, "Tipo de Atención: Nada Seleccionado");
            }
        });

        recordCode.setText(String.valueOf(code));
        editTextDate.setText(sdf.format(new Date()));
        editTextSaleDate.setText(sdf.format(new Date()));

        adapterDoctor = new DoctorAutocompleterAdapter(this, R.layout.doctor_autocomplete_item);
        recordDoctor.setAdapter(adapterDoctor);
        recordDoctor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String temp = adapterDoctor.getItem(position);
                doctor = realm.where(Doctor.class).equalTo(Constants.UUID, temp).findFirst();
                recordDoctor.setText(new StringBuilder().append(doctor.getFirstname()).
                        append(Constants.SPACE).append(doctor.getLastname()).
                        append(Constants.SPACE).append(doctor.getSurname()).toString());

            }
        });

        recordDoctor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (recordDoctor.getText().length() == 0) {
                    doctor = null;
                }

            }
        });

        adapterPatient = new PatientAutocompleterAdapter(this, R.layout.patient_autocomplete_item);
        recordPatient.setAdapter(adapterPatient);
        recordPatient.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String temp = adapterPatient.getItem(position);
                patient = realm.where(Patient.class).equalTo(Constants.UUID, temp).findFirst();
                recordPatient.setText(new StringBuilder().append(patient.getFirstname()).
                        append(Constants.SPACE).append(patient.getLastname()).
                        append(Constants.SPACE).append(patient.getSurname()).toString());
            }
        });

        recordPatient.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(recordPatient.getText().length() == 0){
                    patient = null;
                    record.setPatient(null);
                    record.setPatientUuid(null);
                }
            }
        });

        adapterUbigeo = new UbigeoAutocompleterAdapter(this, R.layout.ubigeo_autocomplete_item);
        recordUbigeo.setAdapter(adapterUbigeo);
        recordUbigeo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                Integer temp = adapterUbigeo.getItem(position);
                ubigeo = realm.where(Ubigeo.class).equalTo(Constants.ID,
                        temp.intValue()).findFirst();
                recordUbigeo.setText(ubigeo.getName());

            }
        });

        recordCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                realm.beginTransaction();
                RealmResults<RecordDetail> recordDetailsTemp = realm.where(RecordDetail.class).
                        equalTo("recordUuid", record.getUuid()).findAll();

                recordDetailsTemp.clear();

                Record recordTemp = realm.where(Record.class).
                        equalTo(Constants.UUID, record.getUuid()).findFirst();

                if(recordTemp != null){
                    recordTemp.removeFromRealm();
                }

                realm.commitTransaction();

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
        getMenuInflater().inflate(R.menu.menu_new_record, menu);

        if(settings.getInt(Constants.DEFAULT_AGENT_ID, 0) == 0){
            menu.findItem(R.id.action_add_agent).setIcon(R.drawable.fa_user_add);
        }else{
            menu.findItem(R.id.action_add_agent).setIcon(R.drawable.fa_user_minus);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.action_add_agent :

                Intent intent = new Intent(NewRecordActivity.this, AddAgentActivity.class);
                startActivityForResult(intent, ADD_AGENT_REQUEST);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), Constants.DATEPICKER_TAG);
    }

    public void showSaleDatePickerDialog(View v) {
        DialogFragment newFragment = new SaleDatePickerFragment();
        newFragment.show(getSupportFragmentManager(), Constants.DATEPICKER_TAG);
    }

    @SuppressLint("ValidFragment")
    public class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener
    {
        public DatePickerFragment() {
            super();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            Log.d(TAG, String.valueOf(year));
            Log.d(TAG, String.valueOf(month));
            Log.d(TAG, String.valueOf(day));

            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            DatePicker datePicker = dialog.getDatePicker();

            long time = c.getTimeInMillis();
            datePicker.setMaxDate(time+1000);

            return dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            start.set(year, month, day);
            String formatedDate = sdf.format(start.getTime());
            editTextDate.setText(formatedDate);

        }
    }

    @SuppressLint("ValidFragment")
    public class SaleDatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener
    {
        public SaleDatePickerFragment() {
            super();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            Log.d(TAG, String.valueOf(year));
            Log.d(TAG, String.valueOf(month));
            Log.d(TAG, String.valueOf(day));

            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            DatePicker datePicker = dialog.getDatePicker();

            long time = c.getTimeInMillis();
            datePicker.setMaxDate(time+1000);
            c.add(Calendar.MONTH, -2);
            long timeLow = c.getTimeInMillis();
            datePicker.setMinDate(timeLow);

            return dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            sale.set(year, month, day);
            String formatedDate = sdf.format(sale.getTime());
            editTextSaleDate.setText(formatedDate);

        }
    }

    public void openNewDoctor(View v) {
        Intent intent = new Intent(this, NewDoctorActivity.class);
        startActivityForResult(intent, ADD_DOCTOR_REQUEST);
    }

    public void openNewPatient(View v) {
        Intent intent = new Intent(this, NewPatientActivity.class);
        startActivityForResult(intent, ADD_PATIENT_REQUEST) ;
    }

    public void nextButton(View v) {

        int errors = 0;
        if(recordCode.getText().length() == 0){
            errors++;
            recordCode.setError("El Código no puede estar vacio");
        }

        if(recordCode.getText().length() > 9){
            errors++;
            recordCode.setError("El Código no puede ser demasiado largo.");
        }

        switch (attentionType.getId()){
            case 1:
                if(recordDoctor.getText().length() == 0){
                    errors++;
                    recordDoctor.setError("El Médico no puede estar vacio");
                }
                if(doctor == null){
                    errors++;
                    recordDoctor.setError("Tiene que seleccionar un Médico");
                }

                if(recordPatient.getText().length() == 0){
                    errors++;
                    recordPatient.setError("El Paciente no puede estar vacio");
                }
                break;
            case 2:
                if(recordDoctor.getText().length() == 0){
                    errors++;
                    recordDoctor.setError("El Médico no puede estar vacio");
                }
                if(doctor == null){
                    errors++;
                    recordDoctor.setError("Tiene que seleccionar un Médico");
                }
                if(recordPatient.getText().length() == 0){
                    errors++;
                    recordPatient.setError("El Paciente no puede estar vacio");
                }
                if(patient == null){
                    errors++;
                    recordPatient.setError("Tiene que seleccionar un Paciente");
                }
              /*  if(recordSerial.getText().length() == 0){
                    errors++;
                    recordSerial.setError("La serie no puede estar vacia");
                }
                if(recordVoucher.getText().length() == 0){
                    errors++;
                    recordVoucher.setError("El # de Comprobante no puede estar vacio");
                }*/

                if(recordUbigeo.getText().length() == 0){
                    errors++;
                    recordUbigeo.setError("El Distrito no puede estar vacio");
                }
                if(ubigeo == null){
                    errors++;
                    recordUbigeo.setError("Tiene que seleccionar un Distrito correcto");
                }

                if(editTextSaleDate.getText().length() == 0){
                    errors++;
                    editTextSaleDate.setError("La fecha de venta no puede estar vacia");
                }

                if(recordRuc.getText().length() != 11){
                    errors++;
                    recordRuc.setError("El RUC debe tener 11 digitos");
                }else{
                    if(!new RucValidator().validate(recordRuc.getText().toString()))
                    {
                        errors++;
                        recordRuc.setError("El RUC es incorrecto");
                    }
                }

                break;
            case 3:
                if(recordDoctor.getText().length() == 0){
                    errors++;
                    recordDoctor.setError("El Médico no puede estar vacio");
                }
                if(doctor == null){
                    errors++;
                    recordDoctor.setError("Tiene que seleccionar un Médico");
                }
                break;
        }

        if(errors == 0){

            user = realm.where(User.class).findFirst();

            realm.beginTransaction();

            record.setCode(Integer.valueOf(recordCode.getText().toString()));
            record.setAttentionTypeId(attentionType.getId());
            record.setAttentionType(attentionType);
            record.setInstitutionId(tracking.getInstitutionId());
            record.setInstitution(tracking.getInstitution());
            record.setInstitutionOriginId(tracking.getInstitutionId());
            record.setInstitutionOrigin(tracking.getInstitution());
            record.setUserId(tracking.getUserId());
            record.setUser(user);
            record.setSent(Boolean.FALSE);
            record.setActive(Boolean.TRUE);
            record.setRecordDate(start.getTime());
            record.setSaleDate(sale.getTime());

            if(ubigeo != null) {
                record.setUbigeo(ubigeo);
                record.setUbigeoId(ubigeo.getId());
            }
            record.setCreatedAt(new Date());
           // record.setUpdatedAt(new Date());

            record.setPatient(patient);
            if(patient != null){
                record.setPatientUuid(patient.getUuid());
            }

            record.setDoctor(doctor);
            record.setDoctorUuid(doctor.getUuid());
            record.setAttentionType(attentionType);
            record.setAttentionTypeId(attentionType.getId());


            int agentId = settings.getInt(Constants.DEFAULT_AGENT_ID, 0);

            if( agentId > 0){
                Agent agent  = realm.where(Agent.class).equalTo(Constants.ID, agentId).findFirst();
                record.setAgent(agent);
                record.setAgentId(agentId);
            }

            int institutionId = settings.getInt(Constants.DEFAULT_INSTITUTION_ID, 0);

            if( institutionId > 0){
                Institution institution = realm.where(Institution.class)
                        .equalTo(Constants.ID, institutionId).findFirst();
                record.setInstitutionOrigin(institution);
                record.setInstitutionOriginId(institutionId);
            }

            //Finish treatment
            if(record.getAttentionTypeId() == 2){
                //record.setVoucher(recordVoucher.getText().toString());
                record.setRuc(recordRuc.getText().toString());
                //record.setSerial(recordSerial.getText().toString());
            }

            Log.d(TAG, record.getUuid());

            realm.copyToRealmOrUpdate(record);

            realm.commitTransaction();

            Intent intent = new Intent(this, AddRecordDetailActivity.class);
            intent.putExtra(Constants.UUID, record.getUuid());
            intent.putExtra(Constants.CODE, record.getCode());
            startActivityForResult(intent, ADD_MEDICAL_SAMPLE_REQUEST);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_PATIENT_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {

                String uuid = data.getStringExtra(Constants.UUID);

                patient = realm.where(Patient.class).equalTo(Constants.UUID, uuid).findFirst();
                recordPatient.setText(new StringBuilder().append(patient.getFirstname()).
                        append(Constants.SPACE).append(patient.getLastname()).
                        append(Constants.SPACE).append(patient.getSurname()).toString());

                record.setPatient(patient);
                record.setPatientUuid(patient.getUuid());
            }
        }

        if (requestCode == ADD_DOCTOR_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                String uuid = data.getStringExtra(Constants.UUID);
                doctor = realm.where(Doctor.class).equalTo(Constants.UUID, uuid).findFirst();
                recordDoctor.setText(new StringBuilder().append(doctor.getFirstname()).
                        append(Constants.SPACE).append(doctor.getLastname()).
                        append(Constants.SPACE).append(doctor.getSurname()).toString());
                record.setDoctor(doctor);
                record.setDoctorUuid(doctor.getUuid());
            }
        }

        if (requestCode == ADD_MEDICAL_SAMPLE_REQUEST) {

            if (resultCode == Activity.RESULT_OK) {
                Intent intent = new Intent();
                if (getParent() == null) {
                    setResult(Activity.RESULT_OK, intent);
                } else {
                    getParent().setResult(Activity.RESULT_OK, intent);
                }
                finish();
            }

            if(resultCode == Activity.RESULT_CANCELED){

                String uuid = data.getStringExtra(Constants.UUID);
                record = realm.where(Record.class).equalTo(Constants.UUID, uuid).findFirst();

            }

        }

        if(requestCode == ADD_AGENT_REQUEST){

            invalidateOptionsMenu();
        }
    }

    /**
     * Exit the app if user select yes.
     */
    private void doExit() {
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
}