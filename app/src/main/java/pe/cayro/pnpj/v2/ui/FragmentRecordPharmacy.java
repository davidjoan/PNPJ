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

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import pe.cayro.pnpj.v2.NewRecordPharmacyActivity;
import pe.cayro.pnpj.v2.R;
import pe.cayro.pnpj.v2.model.RecordPharmacy;
import pe.cayro.pnpj.v2.model.User;
import pe.cayro.pnpj.v2.util.Constants;

/**
 * Created by David on 8/01/16.
 */
public class FragmentRecordPharmacy extends Fragment {
    private static String TAG = FragmentRecordPharmacy.class.getSimpleName();

    static final int ADD_RECORD_PHARMACY_REQUEST = 1;

    @Bind(R.id.record_pharmacy_recycler_view)
    protected RecyclerView mRecyclerView;

    private Realm realm;
    private User user;

    private RealmResults<RecordPharmacy> result;
    private RecordPharmacyListAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    public static FragmentRecordPharmacy newInstance() {
        Bundle args = new Bundle();

        FragmentRecordPharmacy fragment = new FragmentRecordPharmacy();
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
        View view =inflater.inflate(R.layout.fragment_record_pharmacy,container,false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.pharmacy);

        ButterKnife.bind(this, view);

        realm = Realm.getDefaultInstance();

        user = realm.where(User.class).findFirst();

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecordPharmacyListAdapter(result, R.layout.record_pharmacy_item);
        mRecyclerView.setAdapter(mAdapter);

        refreshRecordUi();

        setHasOptionsMenu(true);

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
                        .setTitle("Eliminar Farmacia")
                        .setMessage("Desea eliminar esta Farmacia?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                RecordPharmacyListAdapter.ViewHolder temp =
                                        (RecordPharmacyListAdapter.ViewHolder) viewHolder;

                                realm.beginTransaction();

                                RecordPharmacy institution = realm.where(RecordPharmacy.class).
                                        equalTo(Constants.UUID, temp.uuid).findFirst();
                                //record.removeFromRealm();



                                if(institution.isSent()){

                                    Toast.makeText(getActivity(), "La Farmacia ya fue aprobado, no puede eliminarse.",
                                            Toast.LENGTH_SHORT).show();

                                }
                                else{
                                    institution.setActive(Boolean.FALSE);
                                    institution.setSent(Boolean.FALSE);
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
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_new_institution:
                Intent intent = new Intent(getActivity(), NewRecordPharmacyActivity.class);
                startActivityForResult(intent, ADD_RECORD_PHARMACY_REQUEST);
                break;
            case R.id.action_all:
                result = realm.where(RecordPharmacy.class).
                        equalTo("active", Boolean.TRUE).
                        findAll();
                break;
            case R.id.action_pending:
                result = realm.where(RecordPharmacy.class).
                        equalTo("active", Boolean.TRUE).
                        equalTo("check", 1).findAll();
                break;
            case R.id.action_aproved:
                result = realm.where(RecordPharmacy.class).
                        equalTo("active", Boolean.TRUE).
                        equalTo("check", 2).findAll();
                break;
            case R.id.action_upaproved:
                result = realm.where(RecordPharmacy.class).
                        equalTo("active", Boolean.TRUE).
                        equalTo("check", 3).findAll();
                break;
        }


        item.setChecked((item.isChecked())?false:true);
        mAdapter.setData(result);
        mAdapter.notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_institution, menu);


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
                        result = realm.where(RecordPharmacy.class).beginGroup()
                                .contains("businessname", data.toUpperCase())
                                .or()
                                .contains("ruc", data.toUpperCase())
                                .or()
                                .contains("address", data.toUpperCase())
                                .endGroup().equalTo("active", Boolean.TRUE).findAll();
                        result.sort("created_at", Sort.DESCENDING);

                        mAdapter.setData(result);
                        mAdapter.notifyDataSetChanged();
                    }
                    return false;
                }
            };
            searchView.setOnQueryTextListener(queryListener);
        }
    }




    public class RecordPharmacyListAdapter extends RecyclerView.
            Adapter<RecordPharmacyListAdapter.ViewHolder> {

        private List<RecordPharmacy> items;
        private int itemLayout;

        public RecordPharmacyListAdapter(List<RecordPharmacy> items, int itemLayout) {
            this.items = items;
            this.itemLayout = itemLayout;
        }

        public void setData(List<RecordPharmacy> items) {
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
            RecordPharmacy item = items.get(position);

            viewHolder.name.setText(item.getBusinessname());
            String tipo = "";
            switch (item.getTypeaddress())
            {
                case 1 :
                    tipo = "Av.";
                    break;
                case 2 :
                    tipo = "Calle";
                    break;
                case 3 :
                    tipo = "Jr.";
                    break;
                case 4 :
                    tipo = "Psj.";
                    break;
                case 5 :
                    tipo = "Otro";
                    break;
            }

            viewHolder.address.setText("Dirección: "+tipo+" "+item.getAddress()+" "+item.getNumberaddress());

            viewHolder.code.setText(Constants.RUC_FIELD+item.getRuc());

            viewHolder.uuid = item.getUuid();
            viewHolder.checkValue = item.getCheck();
            viewHolder.active = item.isSent();

            viewHolder.comment.setText("Comentario: "+((item.getComment() == null)?"vacío":item.getComment()));
            viewHolder.usuario.setText("Usuario: "+item.getUser());
            viewHolder.category.setText("Categoría: "+((item.getScore() == null)?"vacío":item.getScore()));



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

            public TextView name;
            public TextView address;
            public TextView code;

            public int checkValue;
            public ImageView status;
            public ImageView check;
            public TextView comment;
            public TextView usuario;
            public TextView category;
            public String uuid;
            public ImageView alert;
            public boolean active;

            public ViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.record_pharmacy_name);
                address = (TextView) itemView.findViewById(R.id.record_pharmacy_address);
                code = (TextView) itemView.findViewById(R.id.record_pharmacy_code);
                comment = (TextView) itemView.findViewById(R.id.record_pharmacy_comment);
                usuario = (TextView) itemView.findViewById(R.id.record_pharmacy_usuario);
                category = (TextView) itemView.findViewById(R.id.record_pharmacy_category);
                check = (ImageView) itemView.findViewById(R.id.record_pharmacy_check);
                status = (ImageView) itemView.findViewById(R.id.record_pharmacy_status);
                alert = (ImageView) itemView.findViewById(R.id.record_pharmacy_alert);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if(user.getRol().equals("REG")) {

                    if (!active) {
                        Intent intent = new Intent(getActivity(), NewRecordPharmacyActivity.class);
                        intent.putExtra(Constants.UUID, uuid);
                        startActivityForResult(intent, ADD_RECORD_PHARMACY_REQUEST);
                    } else {
                        Toast.makeText(getActivity(), "La Farmacia ya no puede modificarse",
                                Toast.LENGTH_SHORT).show();
                    }

                }else{

                    Intent intent = new Intent(getActivity(), NewRecordPharmacyActivity.class);
                    intent.putExtra(Constants.UUID, uuid);
                    startActivityForResult(intent, ADD_RECORD_PHARMACY_REQUEST);
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
        if (requestCode == ADD_RECORD_PHARMACY_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                refreshRecordUi();
            }
        }
    }

    public void refreshRecordUi(){

        result = realm.where(RecordPharmacy.class).
                equalTo("active", Boolean.TRUE).
                findAll();

        result.sort("created_at", Sort.DESCENDING);
        mAdapter.setData(result);
        mAdapter.notifyDataSetChanged();

        ((AppCompatActivity) getActivity()).getSupportActionBar().
                setSubtitle(Constants.QTY_FIELD+String.valueOf(result.size()));
    }
}
