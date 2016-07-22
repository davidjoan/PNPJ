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
import pe.cayro.pnpj.v2.NewInstitutionActivity;
import pe.cayro.pnpj.v2.R;
import pe.cayro.pnpj.v2.model.Institution;
import pe.cayro.pnpj.v2.model.User;
import pe.cayro.pnpj.v2.util.Constants;

/**
 * Created by David on 8/01/16.
 */
public class FragmentInstitution extends Fragment {
    private static String TAG = FragmentInstitution.class.getSimpleName();

    static final int ADD_INSTITUTION_REQUEST = 1;

    @Bind(R.id.institution_recycler_view)
    protected RecyclerView mRecyclerView;

    private Realm realm;
    private User user;

    private RealmResults<Institution> result;
    private InstitutionListAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    public static FragmentInstitution newInstance() {
        Bundle args = new Bundle();

        FragmentInstitution fragment = new FragmentInstitution();
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
        View view =inflater.inflate(R.layout.fragment_institution,container,false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.institutions);

        ButterKnife.bind(this, view);

        realm = Realm.getDefaultInstance();

        user = realm.where(User.class).findFirst();

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new InstitutionListAdapter(result, R.layout.institution_item);
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
                        .setTitle("Eliminar Institución")
                        .setMessage("Desea eliminar esta institución?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                InstitutionListAdapter.ViewHolder temp =
                                        (InstitutionListAdapter.ViewHolder) viewHolder;

                                realm.beginTransaction();

                                Institution institution = realm.where(Institution.class).
                                        equalTo(Constants.UUID, temp.uuid).findFirst();
                                //record.removeFromRealm();

                                if(!institution.isEdit()){
                                    institution.setActive(Boolean.FALSE);

                                }else if(institution.isCheck()){

                                    Toast.makeText(getActivity(), "La institución ya fue aprobado, no puede eliminarse.",
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
                        result = realm.where(Institution.class).beginGroup()
                                .contains("businessname", data.toUpperCase())
                                .or()
                                .contains("ruc", data.toUpperCase())
                                .or()
                                .contains("address", data.toUpperCase())
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
            case R.id.action_new_institution:
                Intent intent = new Intent(getActivity(), NewInstitutionActivity.class);

                startActivityForResult(intent, ADD_INSTITUTION_REQUEST);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public class InstitutionListAdapter extends RecyclerView.
            Adapter<InstitutionListAdapter.ViewHolder> {

        private List<Institution> items;
        private int itemLayout;

        public InstitutionListAdapter(List<Institution> items, int itemLayout) {
            this.items = items;
            this.itemLayout = itemLayout;
        }

        public void setData(List<Institution> items) {
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
            Institution item = items.get(position);

            viewHolder.name.setText(item.getBusinessname());
            viewHolder.address.setText("Dirección:"+item.getAddress());

            viewHolder.code.setText(Constants.RUC_FIELD+item.getRuc());

            viewHolder.uuid = item.getUuid();
            viewHolder.active = item.isCheck();

            viewHolder.comment.setText("Comentario: "+((item.getComment() == null)?"vacío":item.getComment()));
            viewHolder.usuario.setText("Usuario: "+item.getUser());
            viewHolder.category.setText("Categoría: "+((item.getScore() == null)?"vacío":item.getScore()));
            viewHolder.office.setText("Local: "+item.getOffice());


            if(item.isCheck()){
                viewHolder.check.setImageResource(R.drawable.vp0);
            }else{
                viewHolder.check.setImageResource(R.drawable.vp2);
            }

            if(item.isSent()){
                viewHolder.status.setImageResource(R.drawable.sync_on);
            }else{
                viewHolder.status.setImageResource(R.drawable.sync_off);
            }

            if(item.isEdit())
            {
                viewHolder.edit.setVisibility(View.VISIBLE);
            }else{
                viewHolder.edit.setVisibility(View.GONE);
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

            public boolean active;
            public ImageView status;
            public ImageView check;
            public TextView comment;
            public TextView usuario;
            public TextView office;
            public TextView category;
            public String uuid;
            public ImageView edit;

            public ViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.institution_name);
                address = (TextView) itemView.findViewById(R.id.institution_address);

                code = (TextView) itemView.findViewById(R.id.institution_code);

                comment = (TextView) itemView.findViewById(R.id.institution_comment);
                usuario = (TextView) itemView.findViewById(R.id.institution_usuario);
                category = (TextView) itemView.findViewById(R.id.institution_category);
                office = (TextView) itemView.findViewById(R.id.institution_office);

                check = (ImageView) itemView.findViewById(R.id.institution_check);
                status = (ImageView) itemView.findViewById(R.id.institution_status);
                edit = (ImageView) itemView.findViewById(R.id.institution_edit);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if(user.getRol().equals("REG")) {

                    if (!active) {
                        Intent intent = new Intent(getActivity(), NewInstitutionActivity.class);
                        intent.putExtra(Constants.UUID, uuid);
                        startActivityForResult(intent, ADD_INSTITUTION_REQUEST);
                    } else {
                        Toast.makeText(getActivity(), "La institución ya no puede modificarse",
                                Toast.LENGTH_SHORT).show();
                    }

                }else{

                    Intent intent = new Intent(getActivity(), NewInstitutionActivity.class);
                    intent.putExtra(Constants.UUID, uuid);
                    startActivityForResult(intent, ADD_INSTITUTION_REQUEST);
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
        if (requestCode == ADD_INSTITUTION_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                refreshRecordUi();
            }
        }
    }

    public void refreshRecordUi(){

        result = realm.where(Institution.class).
                equalTo("active", Boolean.TRUE).
                findAll();

        result.sort("createdAt", Sort.DESCENDING);
        mAdapter.setData(result);
        mAdapter.notifyDataSetChanged();

        ((AppCompatActivity) getActivity()).getSupportActionBar().
                setSubtitle(Constants.QTY_FIELD+String.valueOf(result.size()));
    }
}
