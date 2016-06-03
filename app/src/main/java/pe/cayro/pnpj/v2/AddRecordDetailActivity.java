package pe.cayro.pnpj.v2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import pe.cayro.pnpj.v2.adapter.ProductAutocompleterAdapter;
import pe.cayro.pnpj.v2.adapter.RecordDetailListAdapter;
import pe.cayro.pnpj.v2.model.Product;
import pe.cayro.pnpj.v2.model.Record;
import pe.cayro.pnpj.v2.model.RecordDetail;
import pe.cayro.pnpj.v2.util.Constants;

public class AddRecordDetailActivity extends AppCompatActivity {
    private static String TAG = AddRecordDetailActivity.class.getSimpleName();
    private static final int SHOW_ALERT_REQUEST = 1;

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.record_detail_add_record_detail)
    protected Button buttonSave;
    @Bind(R.id.record_detail_cancel_record_detail)
    protected Button buttonCancel;
    @Bind(R.id.record_detail_back_record_detail)
    protected Button buttonBack;
    @Bind(R.id.record_detail_qty)
    protected EditText recordDetailQty;
    @Bind(R.id.record_detail_list)
    protected RecyclerView mRecyclerView;
    @Bind(R.id.record_detail_qty_calculated)
    protected EditText recordDetailQtyCalculated;
    @Bind(R.id.record_detail_product_autocompleter)
    protected AppCompatAutoCompleteTextView recordDetailProduct;
    @Bind(R.id.record_detail_alert_button)
    protected ImageButton alertButton;

    private static final DecimalFormat oneDecimal = new DecimalFormat("#");

    private Realm realm;
    private Record record;
    private String recordUuid;
    private int recordCode;
    private Product product;
    private List<RecordDetail> recordDetails;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecordDetailListAdapter mAdapter;
    private ProductAutocompleterAdapter adapterProduct;

    private RecordDetail oldRecordDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record_detail);

        recordUuid = getIntent().getStringExtra(Constants.UUID);
        recordCode = getIntent().getIntExtra(Constants.CODE, 0);

        ButterKnife.bind(this);

        oneDecimal.setRoundingMode(RoundingMode.DOWN);

        toolbar.setTitle(R.string.medical_sample);

        toolbar.setSubtitle(Constants.CODE_FIELD + String.valueOf(recordCode));

        setSupportActionBar(toolbar);

        recordDetailQty.setEnabled(false);

        realm = Realm.getDefaultInstance();

        record = realm.where(Record.class).equalTo(Constants.UUID, recordUuid).findFirst();

        recordDetails = realm.where(RecordDetail.class).equalTo("recordUuid", recordUuid).findAll();

        if(record.getAttentionTypeId() == 2){
            recordDetailQtyCalculated.setVisibility(View.VISIBLE);
        }else{
            recordDetailQtyCalculated.setVisibility(View.GONE);
        }

        recordDetailProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                alertButton.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(recordDetailProduct.getText().length() == 0){
                    recordDetailQty.setText("");
                    recordDetailQtyCalculated.setText("");
                }

            }
        });

        recordDetailQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(recordDetailQty.getText().length() > 0){

                    Integer temp = Integer.valueOf(recordDetailQty.getText().toString());

                    switch(record.getAttentionTypeId()){
                        case 1 :  //Inicio de Tratamiento
                            if(temp > product.getQtyMax()){
                                String value = String.valueOf(product.getQtyMax());
                                Toast.makeText(getApplicationContext(),
                                        "La cantidad maxima para este producto es "+
                                                value, Toast.LENGTH_SHORT).show();
                                recordDetailQty.setText(value);
                            }

                            break;
                        case 2 ://Fin de Tratamiento"
                            if(temp > product.getQtyMaxA()){
                                String value = String.valueOf(product.getQtyMaxA());
                                Toast.makeText(getApplicationContext(),
                                        "La cantidad maxima para este producto es "+
                                                value, Toast.LENGTH_SHORT).show();
                                recordDetailQty.setText(value);
                            }
                            break;
                        case 3 ://Uso Propio
                            if(temp > product.getQtyMaxB()){
                                String value = String.valueOf(product.getQtyMaxB());
                                Toast.makeText(getApplicationContext(),
                                        "La cantidad maxima para este producto es "+
                                                value, Toast.LENGTH_SHORT).show();
                                recordDetailQty.setText(value);
                            }
                            break;
                    }

                    Float value =  Float.valueOf(recordDetailQty.getText().toString());

                    recordDetailQtyCalculated.setText(String.valueOf(oneDecimal.
                            format(value.floatValue()/product.getBonus())));
                }else{
                    recordDetailQtyCalculated.setText("");
                }
            }
        });

        adapterProduct = new ProductAutocompleterAdapter(this, R.layout.product_autocomplete_item);
        recordDetailProduct.setAdapter(adapterProduct);
        recordDetailProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Integer temp = adapterProduct.getItem(position);

                product = realm.where(Product.class).equalTo(Constants.ID,
                        temp.intValue()).findFirst();
                recordDetailProduct.setText(product.getName());

                //Check If Exist Old Data
                if(record.getAttentionTypeId() == 3){
                    oldRecordDetail = realm.where(RecordDetail.class)
                            .equalTo("productId", product.getId())
                            .equalTo("record.doctorUuid", record.getDoctorUuid())
                            .equalTo("record.attentionTypeId", record.getAttentionTypeId())
                            .findFirst();
                }else{
                    oldRecordDetail = realm.where(RecordDetail.class)
                            .equalTo("productId", product.getId())
                            .equalTo("record.patientUuid", record.getPatientUuid())
                            .findFirst();
                }

                if(oldRecordDetail == null){
                    alertButton.setVisibility(View.GONE);
                }else{
                    alertButton.setVisibility(View.VISIBLE);
                }

                recordDetailQty.setEnabled(true);
                recordDetailQty.setFocusableInTouchMode(true);
                recordDetailQty.requestFocus();

                if(record.getAttentionTypeId() == 1) {
                    recordDetailQty.setText(String.valueOf(product.getQtyMin()));
                }
            }
        });

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecordDetailListAdapter(recordDetails, R.layout.record_detail_item);
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.
                SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                RecordDetailListAdapter.ViewHolder temp = (RecordDetailListAdapter.ViewHolder)
                        viewHolder;

                realm.beginTransaction();
                RecordDetail recordDetailTemp = realm.
                        where(RecordDetail.class).
                        equalTo(Constants.UUID, temp.uuid).findFirst();

                recordDetailTemp.removeFromRealm();

                realm.commitTransaction();

                recordDetails = realm.where(RecordDetail.class)
                        .equalTo("recordUuid", recordUuid).findAll();
                mAdapter.setData(recordDetails);
                mAdapter.notifyDataSetChanged();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);

        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        alertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AlertActivity.class);
                intent.putExtra(Constants.UUID, oldRecordDetail.getUuid());
                startActivityForResult(intent, SHOW_ALERT_REQUEST);
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordDetails = realm.where(RecordDetail.class)
                        .equalTo("recordUuid", recordUuid).findAll();
                mAdapter.setData(recordDetails);
                mAdapter.notifyDataSetChanged();

                if(recordDetails.size() > 0){

                    Intent intent = new Intent();
                    if (getParent() == null) {
                        setResult(Activity.RESULT_OK, intent);
                    } else {
                        getParent().setResult(Activity.RESULT_OK, intent);
                    }

                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Debe Ingresar al menos 1 muestra medica", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* recordDetails = realm.where(RecordDetail.class)
                        .equalTo("recordUuid", recordUuid).findAll();
                mAdapter.setData(recordDetails);
                mAdapter.notifyDataSetChanged();

                if(recordDetails.size() > 0){*/

                    Intent intent = new Intent();
                    intent.putExtra(Constants.UUID, record.getUuid());

                    if (getParent() == null) {
                        setResult(Activity.RESULT_CANCELED, intent);
                    } else {
                        getParent().setResult(Activity.RESULT_CANCELED, intent);
                    }

                    finish();
                    /*
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Debe Ingresar al menos 1 muestra medica", Toast.LENGTH_SHORT).show();
                }*/
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int errors = 0;
                if (recordDetailQty.getText().length() == 0) {
                    errors++;
                    recordDetailQty.setError("La cantidad no puede estar en blanco");
                }
                if (recordDetailProduct.getText().length() == 0) {
                    errors++;
                    recordDetailProduct.setError("La Muestra Médica no puede estar en blanco");
                }
                if(product == null){
                    errors++;
                    recordDetailProduct.setError("La Muestra Médica no es correcta");
                }

                RecordDetail tempDetailDuplicated = realm.where(RecordDetail.class)
                        .equalTo("recordUuid", recordUuid)
                        .equalTo("productId", product.getId())
                        .findFirst();

                if(tempDetailDuplicated != null){
                    errors++;
                    Toast.makeText(getApplicationContext(),
                            "Esta Muestra Médica ya fue ingresada.", Toast.LENGTH_SHORT).show();
                }

                if (errors == 0) {

                    realm.beginTransaction();

                    RecordDetail recordDetail = realm.createObject(RecordDetail.class);
                    recordDetail.setRecord(record);
                    recordDetail.setProduct(product);
                    recordDetail.setProductId(product.getId());
                    recordDetail.setRecordUuid(record.getUuid());
                    recordDetail.setUuid(UUID.randomUUID().toString());
                    recordDetail.setQty(Integer.valueOf(recordDetailQty.getText().toString()).intValue());
                    recordDetail.setQtyCalculated(Float.valueOf(recordDetailQtyCalculated.getText().toString()).floatValue());
                    recordDetail.setSent(true);
                    recordDetail.setActive(true);

                    realm.copyToRealmOrUpdate(recordDetail);
                    record.getRecordDetails().add(recordDetail);
                    realm.copyToRealmOrUpdate(record);
                    realm.commitTransaction();

                    recordDetailProduct.setText("");
                    recordDetailQty.setText("");
                    recordDetailQtyCalculated.setText("");
                    alertButton.setVisibility(View.GONE);
                    recordDetailProduct.setFocusableInTouchMode(true);
                    recordDetailProduct.requestFocus();

                    recordDetails = realm.where(RecordDetail.class)
                            .equalTo("recordUuid", recordUuid).findAll();
                    mAdapter.setData(recordDetails);
                    mAdapter.notifyDataSetChanged();
                }
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
