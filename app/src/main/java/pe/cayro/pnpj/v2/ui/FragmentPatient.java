package pe.cayro.pnpj.v2.ui;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import pe.cayro.pnpj.v2.NewPatientActivity;
import pe.cayro.pnpj.v2.R;
import pe.cayro.pnpj.v2.model.Patient;
import pe.cayro.pnpj.v2.util.Constants;

/**
 * Created by David on 8/01/16.
 */
public class FragmentPatient extends Fragment {
    private static String TAG = FragmentPatient.class.getSimpleName();

    static final int ADD_PATIENT_REQUEST = 1;

    @Bind(R.id.patient_recycler_view)
    protected RecyclerView mRecyclerView;

    private Realm realm;
    private List<Patient> patientList;
    private PatientListAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    public static FragmentPatient newInstance() {
        Bundle args = new Bundle();

        FragmentPatient fragment = new FragmentPatient();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_patient,container,false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.patients);

        ButterKnife.bind(this, view);

        realm = Realm.getDefaultInstance();
        patientList = realm.where(Patient.class).findAll();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(Constants.QTY_FIELD+
                String.valueOf(patientList.size()));

        Log.d(TAG, Constants.QTY_FIELD+String.valueOf(patientList.size()));

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new PatientListAdapter(patientList, R.layout.patient_item);
        mRecyclerView.setAdapter(mAdapter);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //menu.clear();
        inflater.inflate(R.menu.menu_patient, menu);

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
                        RealmResults<Patient> result = realm.where(Patient.class).findAll();
                        mAdapter.setData(result);
                        mAdapter.notifyDataSetChanged();
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextSubmit(String data) {
                    if (!TextUtils.isEmpty(data)) {
                        RealmResults<Patient> result = realm.where(Patient.class).beginGroup()
                                .contains("firstname", data.toUpperCase())
                                .or()
                                .contains("lastname", data.toUpperCase())
                                .or()
                                .contains("surname", data.toUpperCase())
                                .or()
                                .contains("code", data.toUpperCase())
                                .endGroup().findAll();
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
            case R.id.action_patient_new:
                Intent intent = new Intent(getActivity(), NewPatientActivity.class);
                startActivityForResult(intent, ADD_PATIENT_REQUEST);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public class PatientListAdapter extends RecyclerView.
            Adapter<PatientListAdapter.ViewHolder> {

        private List<Patient> items;
        private int itemLayout;

        public PatientListAdapter(List<Patient> items, int itemLayout) {
            this.items = items;
            this.itemLayout = itemLayout;
        }

        public void setData(List<Patient> items) {
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
            Patient item = items.get(position);

            viewHolder.name.setText(new StringBuilder().append(item.getFirstname()).
                    append(Constants.SPACE).append(item.getLastname()).
                    append(Constants.SPACE).append(item.getSurname()).toString());
            viewHolder.code.setText(Constants.DNI_FIELD+item.getCode());
            viewHolder.address.setText(Constants.PHONE_FIELD+item.getPhone());
            if(item.getUbigeo() != null){
                viewHolder.ubigeo.setText(item.getUbigeo().getName()+
                        Constants.DASH_SEPARATOR+item.getUbigeo().getProvince());
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

            public TextView name;
            public TextView address;
            public TextView ubigeo;
            public TextView code;
            public String uuid;

            public ViewHolder(View itemView) {
                super(itemView);
                name    = (TextView) itemView.findViewById(R.id.patient_name);
                address = (TextView) itemView.findViewById(R.id.patient_address);
                code = (TextView) itemView.findViewById(R.id.patient_code);
                ubigeo = (TextView) itemView.findViewById(R.id.patient_ubigeo);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewPatientActivity.class);
                intent.putExtra(Constants.UUID, uuid);
                startActivityForResult(intent, ADD_PATIENT_REQUEST);
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
        if (requestCode == ADD_PATIENT_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                patientList = realm.where(Patient.class).findAll();
                mAdapter.setData(patientList);
                mAdapter.notifyDataSetChanged();

                ((AppCompatActivity) getActivity()).getSupportActionBar().
                        setSubtitle(Constants.QTY_FIELD + String.valueOf(patientList.size()));
            }
        }
    }
}
