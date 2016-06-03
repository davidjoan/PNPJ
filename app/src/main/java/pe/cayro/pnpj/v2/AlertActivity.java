package pe.cayro.pnpj.v2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import pe.cayro.pnpj.v2.model.RecordDetail;
import pe.cayro.pnpj.v2.util.Constants;

public class AlertActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.alert_qty)
    protected TextView textViewQty;
    @Bind(R.id.alert_date)
    protected TextView textViewDate;
    @Bind(R.id.alert_doctor)
    protected TextView textViewDoctor;
    @Bind(R.id.alert_patient)
    protected TextView textViewPatient;
    @Bind(R.id.alert_product)
    protected TextView textViewProduct;
    @Bind(R.id.alert_attention_type)
    protected TextView textViewAttentionType;
    @Bind(R.id.alert_qty_calculated)
    protected TextView textViewQtyCalculated;
    @Bind(R.id.alert_close)
    protected Button buttonClose;
    @Bind(R.id.alert_patient_linear_layout)
    protected LinearLayout linearLayoutPatient;
    @Bind(R.id.alert_qty_calculated_linear_layout)
    protected LinearLayout linearLayoutQtyCalculated;

    private String recordDetailUuid;
    private Realm realm;
    private RecordDetail recordDetail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        recordDetailUuid = getIntent().getStringExtra(Constants.UUID);

        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();

        recordDetail = realm.where(RecordDetail.class).equalTo(Constants.UUID,
                recordDetailUuid).findFirst();

        toolbar.setTitle(R.string.title_activity_alert);

        toolbar.setSubtitle(recordDetail.getProduct().getName());

        setSupportActionBar(toolbar);

        textViewProduct.setText(recordDetail.getProduct().getName());

        textViewAttentionType.setText(recordDetail.getRecord().getAttentionType().getName());

        textViewDoctor.setText(recordDetail.getRecord().getDoctor().getFirstname()
                                +" "+
                                recordDetail.getRecord().getDoctor().getLastname()
                                +" "+
                                recordDetail.getRecord().getDoctor().getSurname());

        if(recordDetail.getRecord().getAttentionTypeId() == 3){
            linearLayoutPatient.setVisibility(View.GONE);
        }else{
            linearLayoutPatient.setVisibility(View.VISIBLE);
            textViewPatient.setText(recordDetail.getRecord().getPatient().getFirstname()
                    + " " +
                    recordDetail.getRecord().getPatient().getLastname()
                    + " " +
                    recordDetail.getRecord().getPatient().getSurname());
        }

        if(recordDetail.getRecord().getAttentionTypeId() == 2){

            linearLayoutQtyCalculated.setVisibility(View.VISIBLE);
            textViewQtyCalculated.setText(Float.
                    valueOf(recordDetail.getQtyCalculated()).toString());

        }else{
            linearLayoutQtyCalculated.setVisibility(View.GONE);
        }

        SimpleDateFormat formatter = new SimpleDateFormat(Constants.FORMAT_DATETIME_SLASH);
        String dateFormat = formatter.format(recordDetail.getRecord().getRecordDate());
        textViewDate.setText(dateFormat);

        textViewQty.setText(String.valueOf(recordDetail.getQty()));

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                if (getParent() == null) {
                    setResult(Activity.RESULT_OK, intent);
                } else {
                    getParent().setResult(Activity.RESULT_OK, intent);
                }

                finish();
            }
        });
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
