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
import android.text.InputType;
import android.text.TextUtils;
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
import io.realm.Sort;
import pe.cayro.pnpj.v2.NewDoctorActivity;
import pe.cayro.pnpj.v2.R;
import pe.cayro.pnpj.v2.model.Doctor;
import pe.cayro.pnpj.v2.model.User;
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
    private User user;

    private RealmResults<Doctor> result;
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

        user = realm.where(User.class).findFirst();


        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new DoctorListAdapter(result, R.layout.doctor_item);
        mRecyclerView.setAdapter(mAdapter);

        refreshRecordUi();

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.
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
                        .setTitle("Eliminar Médico")
                        .setMessage("Desea eliminar este médico?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {


                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {




                                DoctorListAdapter.ViewHolder temp =
                                        (DoctorListAdapter.ViewHolder) viewHolder;

                                realm.beginTransaction();

                                Doctor doctor = realm.where(Doctor.class).
                                        equalTo(Constants.UUID, temp.uuid).findFirst();
                                //record.removeFromRealm();

                                if(doctor.getCheck() > 1){

                                    Toast.makeText(getActivity(), "El Médico ya fue aprobado, no puede eliminarse.",
                                            Toast.LENGTH_SHORT).show();

                                }else{
                                    doctor.setActive(Boolean.FALSE);
                                    doctor.setSent(Boolean.FALSE);
                                }
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
                        refreshRecordUi();
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextSubmit(String data) {
                    if (!TextUtils.isEmpty(data)) {
                        result = realm.where(Doctor.class).beginGroup()
                                .contains("firstname", data.toUpperCase())
                                .or()
                                .contains("lastname", data.toUpperCase())
                                .or()
                                .contains("surname", data.toUpperCase())
                                .or()
                                .contains("code", data.toUpperCase())
                                .endGroup().equalTo("active", Boolean.TRUE).findAll();

                        result.sort("createdAt", Sort.DESCENDING);
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
                //TODO: Add Doctor Form
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
            viewHolder.active = item.getCheck();

            viewHolder.comment.setText("Comentario: "+((item.getComment() == null)?"vacío":item.getComment()));
            viewHolder.usuario.setText("Representante: "+item.getUser());
            viewHolder.category.setText("Categoría: "+((item.getScore() == null)?"vacío":item.getScore()));

            Picasso.with(getContext()).
                    load(new StringBuilder().append(Constants.CMP_PHOTO_SERVER)
                            .append(String.format("%05d", Integer.parseInt(item.getCode())))
                            .append(Constants.DOT_JPG).toString()).
                            error(R.drawable.avatar).
                            into(viewHolder.image);

            switch (item.getCheck())
            {
                case 1:
                    viewHolder.check.setImageResource(R.drawable.vp1);
                    break;
                case 2:
                    viewHolder.check.setImageResource(R.drawable.vp0);
                    break;
                case 3:
                    viewHolder.check.setImageResource(R.drawable.vp2);
                    break;
            }

            if(item.isAlert())
            {
                viewHolder.alert.setVisibility(View.VISIBLE);
            }else{
                viewHolder.alert.setVisibility(View.GONE);
            }

            if(item.isSent()){
                viewHolder.status.setImageResource(R.drawable.enviado_si);
            }else{
                viewHolder.status.setImageResource(R.drawable.enviado_no);
            }

            viewHolder.itemView.setTag(item);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
                implements RecyclerView.OnClickListener {
            public ImageView image;
            public ImageView alert;
            public TextView name;
            public TextView specialty;
            public TextView code;
            public String uuid;
            public int active;
            public ImageView status;
            public ImageView check;
            public TextView comment;
            public TextView usuario;
            public TextView category;

            public ViewHolder(View itemView) {
                super(itemView);
                name    = (TextView) itemView.findViewById(R.id.doctor_name);
                specialty = (TextView) itemView.findViewById(R.id.doctor_specialty);
                code = (TextView) itemView.findViewById(R.id.doctor_code);
                image = (ImageView) itemView.findViewById(R.id.doctor_image);

                comment  = (TextView) itemView.findViewById(R.id.doctor_comment);
                usuario  = (TextView) itemView.findViewById(R.id.doctor_usuario);
                category = (TextView) itemView.findViewById(R.id.doctor_category);

                check = (ImageView) itemView.findViewById(R.id.doctor_check);
                status = (ImageView) itemView.findViewById(R.id.doctor_status);
                alert = (ImageView) itemView.findViewById(R.id.alert);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {

                if(user.getRol().equals("REG")) {

                    if (active == 1) {
                        Intent intent = new Intent(getActivity(), NewDoctorActivity.class);
                        intent.putExtra(Constants.UUID, uuid);
                        startActivityForResult(intent, ADD_DOCTOR_REQUEST);
                    } else {
                        Toast.makeText(getActivity(), "El médico ya no puede modificarse",
                                Toast.LENGTH_SHORT).show();
                    }

                }else{

                    Intent intent = new Intent(getActivity(), NewDoctorActivity.class);
                    intent.putExtra(Constants.UUID, uuid);
                    startActivityForResult(intent, ADD_DOCTOR_REQUEST);
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
                refreshRecordUi();
            }
        }
    }


    public void refreshRecordUi(){

        result = realm.where(Doctor.class).
                equalTo("active", Boolean.TRUE).
                findAll();

        result.sort("createdAt", Sort.DESCENDING);
        mAdapter.setData(result);
        mAdapter.notifyDataSetChanged();

        ((AppCompatActivity) getActivity()).getSupportActionBar().
                setSubtitle(Constants.QTY_FIELD+String.valueOf(result.size()));
    }
}