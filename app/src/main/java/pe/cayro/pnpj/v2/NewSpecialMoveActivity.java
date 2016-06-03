package pe.cayro.pnpj.v2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;

import pe.cayro.pnpj.v2.adapter.ProductAutocompleterAdapter;
import pe.cayro.pnpj.v2.adapter.SpecialMoveDetailListAdapter;
import pe.cayro.pnpj.v2.adapter.TypeMovementAutocompleterAdapter;
import pe.cayro.pnpj.v2.model.Product;

import pe.cayro.pnpj.v2.model.SpecialMove;
import pe.cayro.pnpj.v2.model.SpecialMoveDetail;
import pe.cayro.pnpj.v2.model.TypeMovement;
import pe.cayro.pnpj.v2.model.User;
import pe.cayro.pnpj.v2.util.Constants;

public class NewSpecialMoveActivity extends AppCompatActivity {

    private static String TAG = NewSpecialMoveActivity.class.getSimpleName();

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.special_move_type_spinner)
    protected Spinner spinner;
    @Bind(R.id.special_move_detail_add_record_detail)
    protected Button buttonSave;
    @Bind(R.id.special_move_detail_cancel_record_detail)
    protected Button buttonCancel;
    @Bind(R.id.special_move_detail_back_record_detail)
    protected Button buttonBack;

    @Bind(R.id.special_move_qty)
    protected EditText specialMoveQty;

    @Bind(R.id.special_move_description)
    protected EditText specialMoveDescription;

    @Bind(R.id.special_move_reason_autocompleter)
    protected AppCompatAutoCompleteTextView specialMoveReason;

    @Bind(R.id.special_move_product_autocompleter)
    protected AppCompatAutoCompleteTextView specialMoveProduct;

    @Bind(R.id.special_move_date)
    protected EditText editTextDate;

    @Bind(R.id.special_move_detail_list)
    protected RecyclerView mRecyclerView;

    private int counter = 0;

    private User user;
    private Realm realm;
    private SpecialMove specialMove;
    private TypeMovement typeMovement;
    private Product product;
    private Calendar start;
    private SimpleDateFormat sdf;
    private SimpleDateFormat format;
    private TypeMovementAutocompleterAdapter adapterTypeMovement;
    private ProductAutocompleterAdapter adapterProduct;

    private static final DecimalFormat oneDecimal = new DecimalFormat("#");

    private List<SpecialMoveDetail> specialMoveDetails;
    private RecyclerView.LayoutManager mLayoutManager;
    private SpecialMoveDetailListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_special_move);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        format = new SimpleDateFormat(Constants.FORMAT_DATE_SLASH);
        sdf    = new SimpleDateFormat(Constants.FORMAT_DATE);

        ButterKnife.bind(this);


        oneDecimal.setRoundingMode(RoundingMode.DOWN);

        start = Calendar.getInstance();
        realm = Realm.getDefaultInstance();

        user = realm.where(User.class).findFirst();

        specialMove = new SpecialMove();
        specialMove.setUserId(user.getId());
        specialMove.setActive(true);
        specialMove.setSent(false);
        specialMove.setUuid(UUID.randomUUID().toString());
        specialMove.setRecordDate(new Date());
        specialMove.setCreatedAt(new Date());



        typeMovement = realm.where(TypeMovement.class).findFirst();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.special_move_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setSelection(1);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                typeMovement = null;
                specialMove.setTypeMovement(null);
                specialMoveReason.setText("");

                switch (position){
                    case 0: //In
                        adapterTypeMovement.setParameter(Constants.IN);
                        break;
                    case 1:  //Out
                        adapterTypeMovement.setParameter(Constants.OUT);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "Tipo de Movimiento: Nada Seleccionado");
            }
        });

        adapterTypeMovement = new TypeMovementAutocompleterAdapter(this, R.layout.ubigeo_autocomplete_item,typeMovement.getType());
        specialMoveReason.setAdapter(adapterTypeMovement);

        specialMoveReason.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Integer temp = adapterTypeMovement.getItem(position);
                typeMovement = realm.where(TypeMovement.class).equalTo(Constants.ID, temp.intValue()).findFirst();
                specialMoveReason.setText(typeMovement.getName());
            }
        });

        adapterProduct = new ProductAutocompleterAdapter(this, R.layout.product_autocomplete_item);

        specialMoveProduct.setAdapter(adapterProduct);

        specialMoveProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Integer temp = adapterProduct.getItem(position);

                product = realm.where(Product.class).equalTo(Constants.ID, temp.intValue()).findFirst();

                specialMoveProduct.setText(product.getName());
            }
        });

        editTextDate.setText(sdf.format(new Date()));

        // Parte 2
        buttonCancel.setOnClickListener(new View.OnClickListener() {
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

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                SpecialMoveDetail recordDetailTemp = realm.
                        where(SpecialMoveDetail.class).
                        equalTo(Constants.UUID, specialMove.getUuid()).findFirst();

                if(recordDetailTemp != null){
                    realm.beginTransaction();
                    recordDetailTemp.removeFromRealm();
                    realm.commitTransaction();
                }




                Intent intent = new Intent();
                //intent.putExtra(Constants.UUID, record.getUuid());

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

                if (specialMoveQty.getText().length() == 0) {
                    errors++;
                    specialMoveQty.setError("La cantidad no puede estar en blanco");
                }
                if (specialMoveProduct.getText().length() == 0) {
                    errors++;
                    specialMoveProduct.setError("La Muestra Médica no puede estar en blanco");
                }
                if(specialMoveReason.getText().length() == 0){
                    errors++;
                    specialMoveReason.setError("La Motivo no puede estar en blanco");
                }

                if (errors == 0) {

                    counter++;





                    //specialMove.setTypeMovement(typeMovement);
                    // specialMove.setTypeMovementId(typeMovement.getId());

                    //specialMove.setReasonId(typeMovement.getType());
                    if(counter == 1){

                        realm.beginTransaction();

                        specialMove.setComment(specialMoveDescription.getText().toString());
                        specialMove.setReasonId(typeMovement.getType());
                        specialMove.setTypeMovement(typeMovement);
                        specialMove.setTypeMovementId(typeMovement.getId());

                        realm.copyToRealmOrUpdate(specialMove);
                        realm.commitTransaction();

                    }

                    realm.beginTransaction();

                    SpecialMoveDetail specialMoveDetail = realm.createObject(SpecialMoveDetail.class);

                    specialMoveDetail.setUuid(UUID.randomUUID().toString());

                    specialMoveDetail.setQty( Integer.valueOf(specialMoveQty.getText().toString()).intValue());
                    specialMoveDetail.setProduct(product);
                    specialMoveDetail.setProductId(product.getId());
                    specialMoveDetail.setSent(true);
                    specialMoveDetail.setActive(true);
                    specialMoveDetail.setCreatedAt(new Date());

                    SpecialMove temp = realm.where(SpecialMove.class).equalTo(Constants.UUID, specialMove.getUuid()).findFirst();

                    specialMoveDetail.setSpecialMoveUuid(temp.getUuid());
                    specialMoveDetail.setSpecialMove(temp);

                    //realm.copyToRealm(specialMoveDetail);

//                    specialMove.getSpecialMoveDetails().add(specialMoveDetail);



                    temp.getSpecialMoveDetails().add(specialMoveDetail);

                    realm.copyToRealmOrUpdate(temp);



                    realm.commitTransaction();

                    specialMoveProduct.setText("");
                    specialMoveQty.setText("");

                    specialMoveProduct.setFocusableInTouchMode(true);
                    specialMoveProduct.requestFocus();

                    specialMoveDetails = realm.where(SpecialMoveDetail.class)
                            .equalTo("specialMoveUuid", specialMove.getUuid()).findAll();
                    mAdapter.setData(specialMoveDetails);
                    mAdapter.notifyDataSetChanged();

                }
            }
        });

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        specialMoveDetails = new ArrayList<>();
        mAdapter = new SpecialMoveDetailListAdapter(specialMoveDetails, R.layout.record_detail_item);
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

                SpecialMoveDetailListAdapter.ViewHolder temp = (SpecialMoveDetailListAdapter.ViewHolder)
                        viewHolder;

                realm.beginTransaction();
                SpecialMoveDetail recordDetailTemp = realm.
                        where(SpecialMoveDetail.class).
                        equalTo(Constants.UUID, temp.uuid).findFirst();

                recordDetailTemp.removeFromRealm();

                realm.commitTransaction();

                specialMoveDetails = realm.where(SpecialMoveDetail.class)
                        .equalTo("specialMoveUuid", specialMove.getUuid()).findAll();
                mAdapter.setData(specialMoveDetails);
                mAdapter.notifyDataSetChanged();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);

        itemTouchHelper.attachToRecyclerView(mRecyclerView);


    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
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
