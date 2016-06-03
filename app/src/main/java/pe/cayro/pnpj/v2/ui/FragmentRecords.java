package pe.cayro.pnpj.v2.ui;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelper.SimpleCallback;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import pe.cayro.pnpj.v2.NewRecordActivity;
import pe.cayro.pnpj.v2.R;
import pe.cayro.pnpj.v2.ShowRecordActivity;
import pe.cayro.pnpj.v2.model.Record;
import pe.cayro.pnpj.v2.model.RecordDetail;
import pe.cayro.pnpj.v2.model.Tracking;
import pe.cayro.pnpj.v2.util.Constants;

/**
 * Created by David on 8/01/16.
 */
public class FragmentRecords extends Fragment {

    private static String TAG = FragmentRecords.class.getSimpleName();
    static final int ADD_RECORD_REQUEST = 1;
    static final int SHOW_RECORD_REQUEST = 2;

    @Bind(R.id.record_recycler_view)
    protected RecyclerView mRecyclerView;

    private Realm realm;
    private Tracking tracking;
    private String trackingUuid;
    private RealmResults<Record> result;
    private RecordListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static FragmentRecords newInstance(String uuid) {
        Bundle args = new Bundle();
        FragmentRecords fragment = new FragmentRecords();
        args.putString(Constants.UUID, uuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trackingUuid = getArguments().getString(Constants.UUID);
        Log.i(TAG, trackingUuid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_record,container,false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.atentions);

        ButterKnife.bind(this, view);

        realm = Realm.getDefaultInstance();

        tracking = realm.where(Tracking.class).equalTo(Constants.UUID, trackingUuid).findFirst();

        result = realm.where(Record.class).
                equalTo("institutionId", tracking.getInstitutionId()).
                equalTo("active", Boolean.TRUE).
                findAll();

        result.sort("recordDate", Sort.DESCENDING);

        ((AppCompatActivity) getActivity()).getSupportActionBar().
                setSubtitle(Constants.QTY_FIELD + String.valueOf(result.size()));

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecordListAdapter(result, R.layout.record_item);
        mRecyclerView.setAdapter(mAdapter);


        SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.
                SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {

                new AlertDialog.Builder(((AppCompatActivity) getActivity()).
                        getSupportActionBar().getThemedContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Eliminar Registro")
                        .setMessage("Desea eliminar este registro?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                RecordListAdapter.ViewHolder temp =
                                        (RecordListAdapter.ViewHolder) viewHolder;

                                realm.beginTransaction();
                                RealmResults<RecordDetail> recordDetailsTemp = realm.
                                        where(RecordDetail.class).
                                        equalTo("recordUuid", temp.uuid).findAll();

                                recordDetailsTemp.clear();

                                Record record = realm.where(Record.class).
                                        equalTo(Constants.UUID, temp.uuid).findFirst();
                                //record.removeFromRealm();
                                record.setActive(Boolean.FALSE);
                                record.setSent(Boolean.FALSE);

                                realm.commitTransaction();
                                refreshRecordUi();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                refreshRecordUi();

                            }
                        })
                        .show();

               // refreshRecordUi();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //menu.clear();
        inflater.inflate(R.menu.menu_record, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getActivity().
                getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().
                    getComponentName()));
            searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
            SearchView.OnQueryTextListener queryListener = new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextChange(String data) {

                    if (TextUtils.isEmpty(data)) {
                        refreshRecordUi();
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextSubmit(String data) {
                    if (!TextUtils.isEmpty(data)) {
                        result = realm.where(Record.class).
                                equalTo("institutionId", tracking.getInstitutionId()).
                                equalTo("active", Boolean.TRUE).
                                beginGroup()
                                .contains("doctor.firstname", data.toUpperCase())
                                .or()
                                .contains("doctor.lastname", data.toUpperCase())
                                .or()
                                .contains("doctor.surname", data.toUpperCase())
                                .or()
                                .contains("patient.firstname", data.toUpperCase())
                                .or()
                                .contains("patient.lastname", data.toUpperCase())
                                .or()
                                .contains("patient.surname", data.toUpperCase())
                                .or()
                                .contains("doctor.code", data.toUpperCase())
                                .or()
                                .contains("patient.code", data.toUpperCase())
                                .endGroup().findAll();

                        result.sort("recordDate", Sort.DESCENDING);

                        mAdapter.setData(result);
                        mAdapter.notifyDataSetChanged();
                    }
                    return false;
                }
            };
            searchView.setOnQueryTextListener(queryListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_new_record:
                Intent intent = new Intent(getActivity(), NewRecordActivity.class);
                intent.putExtra(Constants.TRACKING_UUID,tracking.getUuid());
                startActivityForResult(intent, ADD_RECORD_REQUEST);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class RecordListAdapter extends RecyclerView.
            Adapter<RecordListAdapter.ViewHolder> {

        private List<Record> items;
        private int itemLayout;

        public RecordListAdapter(List<Record> items, int itemLayout) {
            this.items = items;
            this.itemLayout = itemLayout;
        }

        public void setData(List<Record> items) {
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
            Record item = items.get(position);

            viewHolder.doctor.setText(
                    new StringBuilder().
                            append(item.getDoctor().getFirstname()).
                            append(Constants.SPACE).
                            append(item.getDoctor().getLastname()).
                            append(Constants.SPACE).
                            append(item.getDoctor().getSurname()).toString());

            if(item.getAttentionTypeId() == 3){
                viewHolder.patient.setText(R.string.record_whitout_patient);

            }else{

                if(item.getPatient() == null){
                    viewHolder.patient.setText(R.string.record_whitout_patient);
                }else{
                    viewHolder.patient.setText(
                            new StringBuilder().append(Constants.PATIENT_ABR).
                                    append(item.getPatient().getFirstname()).
                                    append(Constants.SPACE).append(item.getPatient()
                                    .getLastname()).
                                    append(Constants.SPACE).append(item.getPatient()
                                    .getSurname()).toString());
                }
            }

            viewHolder.attentionType.setText(item.getAttentionType().getName());
            viewHolder.code.setText("#: "+String.valueOf(item.getCode()));

            float sumMM = 0;

            for (RecordDetail temp : item.getRecordDetails()) {
                if(item.getAttentionTypeId() == 2 ) {
                    sumMM = sumMM + temp.getQtyCalculated();
                }else{
                    sumMM = sumMM + temp.getQty();
                }
            }

            viewHolder.mm.setText("mm: "+String.valueOf(sumMM));

            Picasso.with(getContext()).
                    load(new StringBuilder().append(Constants.CMP_PHOTO_SERVER)
                            .append(String.format("%05d",
                                    Integer.parseInt(item.getDoctor().getCode())))
                            .append(Constants.DOT_JPG).toString()).
                    error(R.drawable.avatar).
                    into(viewHolder.image);

            SimpleDateFormat formatter = new SimpleDateFormat(Constants.FORMAT_DATETIME_SLASH);
            String dateFormat = formatter.format(item.getRecordDate());
            viewHolder.date.setText(Constants.DATE_FIELD+dateFormat);

            viewHolder.institution.setText(Constants.INSTITUTION_LABEL+
                    item.getInstitutionOrigin().getName());

            if(item.isSent()){
                viewHolder.sent.setImageResource(R.drawable.sync_on);
            }else{
                viewHolder.sent.setImageResource(R.drawable.sync_off);
            }

            viewHolder.uuid = item.getUuid();
            viewHolder.itemView.setTag(item);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
                implements RecyclerView.OnClickListener {

            public ImageView image;
            public ImageView sent;
            public TextView doctor;
            public TextView code;
            public TextView mm;
            public TextView patient;
            public TextView attentionType;
            public TextView date;
            public TextView institution;
            public String uuid;

            public ViewHolder(View itemView) {
                super(itemView);
                doctor = (TextView) itemView.findViewById(R.id.record_doctor);
                code = (TextView) itemView.findViewById(R.id.record_code_final);
                mm = (TextView) itemView.findViewById(R.id.record_qty_mm);
                patient = (TextView) itemView.findViewById(R.id.record_patient);
                attentionType = (TextView) itemView.findViewById(R.id.record_attention_type);
                date = (TextView) itemView.findViewById(R.id.record_date);
                image = (ImageView) itemView.findViewById(R.id.record_image);
                sent = (ImageView) itemView.findViewById(R.id.record_sent);
                institution = (TextView) itemView.findViewById(R.id.record_institution);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ShowRecordActivity.class);
                intent.putExtra(Constants.UUID, uuid);
                startActivityForResult(intent, SHOW_RECORD_REQUEST);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_RECORD_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                refreshRecordUi();
            }
        }
    }

    public void refreshRecordUi(){

        result = realm.where(Record.class).
                equalTo("institutionId", tracking.getInstitutionId()).
                equalTo("active", Boolean.TRUE).
                findAll();
        result.sort("recordDate", Sort.DESCENDING);
        mAdapter.setData(result);
        mAdapter.notifyDataSetChanged();

        ((AppCompatActivity) getActivity()).getSupportActionBar().
                setSubtitle(Constants.QTY_FIELD+String.valueOf(result.size()));
    }
}