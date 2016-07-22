package pe.cayro.pnpj.v2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import pe.cayro.pnpj.v2.adapter.RecordDetailListAdapter;
import pe.cayro.pnpj.v2.model.Record;
import pe.cayro.pnpj.v2.util.Constants;

public class ShowRecordActivity extends AppCompatActivity {

    private static String TAG = ShowRecordActivity.class.getSimpleName();

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.record_back)
    protected Button back;
    @Bind(R.id.record_code_value)
    protected TextView code;
    @Bind(R.id.record_date_value)
    protected TextView date;
    @Bind(R.id.record_doctor_value)
    protected TextView doctor;
    @Bind(R.id.record_patient_value)
    protected TextView patient;
    @Bind(R.id.record_ruc_value)
    protected TextView ruc;
    @Bind(R.id.record_sale_date_value)
    protected TextView saleDate;
    @Bind(R.id.record_ubigeo_value)
    protected TextView ubigeo;
    @Bind(R.id.record_medical_sample)
    protected RecyclerView mRecyclerView;

    @Bind(R.id.record_agent_value)
    protected TextView agent;
    @Bind(R.id.record_institution_value)
    protected TextView institution;

    private Realm realm;
    private Record record;
    private String recordUuid;

    private RecyclerView.LayoutManager mLayoutManager;
    private RecordDetailListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_record);

        ButterKnife.bind(this);



        recordUuid = getIntent().getStringExtra(Constants.UUID);
        realm = Realm.getDefaultInstance();
        record = realm.where(Record.class).equalTo(Constants.UUID, recordUuid).findFirst();

        toolbar.setTitle(R.string.title_activity_show_record);
        toolbar.setSubtitle(record.getAttentionType().getName());

        setSupportActionBar(toolbar);


        switch (record.getAttentionTypeId()) {
            case 1:
                findViewById(R.id.body_finish_treatment_2).setVisibility(View.GONE);
                findViewById(R.id.body_finish_treatment_3).setVisibility(View.GONE);
                findViewById(R.id.body_patient).setVisibility(View.VISIBLE);

                break;
            case 2:
                findViewById(R.id.body_finish_treatment_2).setVisibility(View.VISIBLE);
                findViewById(R.id.body_finish_treatment_3).setVisibility(View.VISIBLE);
                findViewById(R.id.body_patient).setVisibility(View.VISIBLE);

                break;
            case 3:
                findViewById(R.id.body_finish_treatment_2).setVisibility(View.GONE);
                findViewById(R.id.body_finish_treatment_3).setVisibility(View.GONE);
                findViewById(R.id.body_patient).setVisibility(View.GONE);
                break;
        }


            findViewById(R.id.body_finish_treatment_5).setVisibility(View.VISIBLE);
            institution.setText(record.getInstitutionOrigin().getBusinessname());

        if(record.getAgentId() > 0){
            findViewById(R.id.body_finish_treatment_4).setVisibility(View.VISIBLE);
            agent.setText(record.getAgent().getName());
        }else{
            findViewById(R.id.body_finish_treatment_4).setVisibility(View.GONE);
        }

        code.setText(String.valueOf(record.getCode()));

        SimpleDateFormat formatter = new SimpleDateFormat(Constants.FORMAT_DATE_SLASH);
        String dateFormat = formatter.format(record.getRecordDate());
        date.setText(dateFormat);

        doctor.setText(record.getDoctor().getFirstname()+" "+
                record.getDoctor().getLastname()+" "+
                record.getDoctor().getSurname());

        if(record.getPatient() != null){
            patient.setText(record.getPatient().getFirstname()+" "+
                    record.getPatient().getLastname()+" "+
                    record.getPatient().getSurname());
        }else{
            patient.setText("");
        }

        ruc.setText(record.getRuc());

        if(record.getSaleDate() != null){
            String saleDateFormat = formatter.format(record.getSaleDate());
            saleDate.setText(saleDateFormat);
        }else{
            saleDate.setText("");
        }

        if(record.getUbigeo() != null){
            ubigeo.setText(record.getUbigeo().getName());
        }else{
            ubigeo.setText("");
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                if (getParent() == null) {
                    setResult(Activity.RESULT_CANCELED, intent);
                } else {
                    getParent().setResult(Activity.RESULT_CANCELED, intent);
                }
                finish();
            }
        });

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecordDetailListAdapter(record.getRecordDetails(),
                R.layout.record_detail_item);
        mRecyclerView.setAdapter(mAdapter);
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
