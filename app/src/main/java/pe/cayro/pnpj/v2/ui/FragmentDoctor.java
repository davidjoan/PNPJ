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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import pe.cayro.pnpj.v2.NewDoctorActivity;
import pe.cayro.pnpj.v2.R;
import pe.cayro.pnpj.v2.model.Doctor;
import pe.cayro.pnpj.v2.util.Constants;

/**
 * Created by David on 8/01/16.
 */
public class FragmentDoctor extends Fragment {
    private static String TAG = FragmentDoctor.class.getSimpleName();

    static final int ADD_DOCTOR_REQUEST = 1;

    @Bind(R.id.doctor_recycler_view)
    protected RecyclerView mRecyclerView;

    private Realm realm;
    private List<Doctor> doctorList;
    private DoctorListAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    public static FragmentDoctor newInstance() {
        Bundle args = new Bundle();

        FragmentDoctor fragment = new FragmentDoctor();
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
        View view =inflater.inflate(R.layout.fragment_doctor,container,false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(Constants.DOCTORS);

        ButterKnife.bind(this, view);

        realm = Realm.getDefaultInstance();

        doctorList = realm.where(Doctor.class).findAll();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(Constants.QTY_FIELD+
                String.valueOf(doctorList.size()));

        Log.d(TAG, Constants.QTY_FIELD+String.valueOf(doctorList.size()));

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new DoctorListAdapter(doctorList, R.layout.doctor_item);
        mRecyclerView.setAdapter(mAdapter);

        setHasOptionsMenu(true);
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //menu.clear();
        inflater.inflate(R.menu.menu_doctor, menu);

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
                        RealmResults<Doctor> result = realm.where(Doctor.class).findAll();
                        mAdapter.setData(result);
                        mAdapter.notifyDataSetChanged();
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextSubmit(String data) {
                    if (!TextUtils.isEmpty(data)) {
                        RealmResults<Doctor> result = realm.where(Doctor.class).beginGroup()
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
            case R.id.action_new_doctor:
                Intent intent = new Intent(getActivity(), NewDoctorActivity.class);

                startActivityForResult(intent, ADD_DOCTOR_REQUEST);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public class DoctorListAdapter extends RecyclerView.
            Adapter<DoctorListAdapter.ViewHolder> {

        private List<Doctor> items;
        private int itemLayout;

        public DoctorListAdapter(List<Doctor> items, int itemLayout) {
            this.items = items;
            this.itemLayout = itemLayout;
        }

        public void setData(List<Doctor> items) {
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
            Doctor item = items.get(position);

            viewHolder.name.setText(new StringBuilder().append(item.getFirstname()).
                    append(Constants.SPACE).append(item.getLastname()).
                    append(Constants.SPACE).append(item.getSurname()).toString());
            viewHolder.code.setText(Constants.CMP_FIELD+item.getCode());
            viewHolder.specialty.setText(Constants.SPECIALTY_FIELD+item.getSpecialty().getName());
            viewHolder.uuid = item.getUuid();
            viewHolder.active = item.isActive();

            Picasso.with(getContext()).
                    load(new StringBuilder().append(Constants.CMP_PHOTO_SERVER)
                            .append(String.format("%05d", Integer.parseInt(item.getCode())))
                            .append(Constants.DOT_JPG).toString()).
                            error(R.drawable.avatar).
                            into(viewHolder.image);

            viewHolder.itemView.setTag(item);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
                implements RecyclerView.OnClickListener {
            public ImageView image;
            public TextView name;
            public TextView specialty;
            public TextView code;
            public String uuid;
            public boolean active;

            public ViewHolder(View itemView) {
                super(itemView);
                name    = (TextView) itemView.findViewById(R.id.doctor_name);
                specialty = (TextView) itemView.findViewById(R.id.doctor_specialty);
                code = (TextView) itemView.findViewById(R.id.doctor_code);
                image = (ImageView) itemView.findViewById(R.id.doctor_image);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {

                if(!active){
                    Intent intent = new Intent(getActivity(), NewDoctorActivity.class);
                    intent.putExtra(Constants.UUID, uuid);
                    startActivityForResult(intent, ADD_DOCTOR_REQUEST);
                }else{
                    Toast.makeText(getActivity(), "El médico no puede modificarse",
                            Toast.LENGTH_SHORT).show();
                }
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
        if (requestCode == ADD_DOCTOR_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                doctorList = realm.where(Doctor.class).findAll();
                mAdapter.setData(doctorList);
                mAdapter.notifyDataSetChanged();

                ((AppCompatActivity) getActivity()).getSupportActionBar().
                        setSubtitle(Constants.QTY_FIELD + String.valueOf(doctorList.size()));
            }
        }
    }
}