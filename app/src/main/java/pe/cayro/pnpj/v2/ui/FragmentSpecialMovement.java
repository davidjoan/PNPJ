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

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import pe.cayro.pnpj.v2.NewSpecialMoveActivity;
import pe.cayro.pnpj.v2.R;
import pe.cayro.pnpj.v2.ShowSpecialMoveActivity;
import pe.cayro.pnpj.v2.model.SpecialMove;
import pe.cayro.pnpj.v2.model.SpecialMoveDetail;
import pe.cayro.pnpj.v2.model.Tracking;
import pe.cayro.pnpj.v2.model.User;
import pe.cayro.pnpj.v2.util.Constants;

/**
 * Created by David on 20/01/16.
 */
public class FragmentSpecialMovement extends Fragment {

    private static String TAG = FragmentSpecialMovement.class.getSimpleName();
    static final int ADD_SPECIALMOVE_REQUEST = 1;
    static final int SHOW_SPECIALMOVE_REQUEST = 2;

    @Bind(R.id.specialmove_recycler_view)
    protected RecyclerView mRecyclerView;

    private Realm realm;
    private User user;
    private RealmResults<SpecialMove> result;
    private SpecialMoveListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static FragmentSpecialMovement newInstance() {
        Bundle args = new Bundle();
        FragmentSpecialMovement fragment = new FragmentSpecialMovement();
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
        View view =inflater.inflate(R.layout.fragment_specialmove, container,false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.special_movements);

        ButterKnife.bind(this, view);

        realm = Realm.getDefaultInstance();

        user = realm.where(User.class).findFirst();

        result = realm.where(SpecialMove.class).
                equalTo("active", Boolean.TRUE).
                findAll();

        result.sort("recordDate", Sort.DESCENDING);

        ((AppCompatActivity) getActivity()).getSupportActionBar().
                setSubtitle(Constants.QTY_FIELD + String.valueOf(result.size()));

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new SpecialMoveListAdapter(result, R.layout.specialmove_item);
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
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {

                new AlertDialog.Builder(((AppCompatActivity) getActivity()).
                        getSupportActionBar().getThemedContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Eliminar Registro")
                        .setMessage("Desea eliminar este registro?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SpecialMoveListAdapter.ViewHolder temp =
                                        (SpecialMoveListAdapter.ViewHolder) viewHolder;

                                realm.beginTransaction();
                                RealmResults<SpecialMoveDetail> SpecialMoveDetailsTemp = realm.
                                        where(SpecialMoveDetail.class).
                                        equalTo("specialMoveUuid", temp.uuid).findAll();

                                SpecialMoveDetailsTemp.clear();

                                SpecialMove SpecialMove = realm.where(SpecialMove.class).
                                        equalTo(Constants.UUID, temp.uuid).findFirst();
                                //SpecialMove.removeFromRealm();
                                SpecialMove.setActive(Boolean.FALSE);
                                SpecialMove.setSent(Boolean.FALSE);

                                realm.commitTransaction();
                                refreshSpecialMoveUi();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                refreshSpecialMoveUi();

                            }
                        })
                        .show();

                // refreshSpecialMoveUi();
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
        inflater.inflate(R.menu.menu_specialmove, menu);

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
                        refreshSpecialMoveUi();
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextSubmit(String data) {
                    if (!TextUtils.isEmpty(data)) {
                        result = realm.where(SpecialMove.class).
                                equalTo("active", Boolean.TRUE).
                                beginGroup()
                                .contains("typeMovement.name", data.toUpperCase())
                                .or()
                                .contains("comment", data.toUpperCase())
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
            case R.id.action_new_specialmove:
                Intent intent = new Intent(getActivity(), NewSpecialMoveActivity.class);
                startActivityForResult(intent, ADD_SPECIALMOVE_REQUEST);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class SpecialMoveListAdapter extends RecyclerView.
            Adapter<SpecialMoveListAdapter.ViewHolder> {

        private List<SpecialMove> items;
        private int itemLayout;

        public SpecialMoveListAdapter(List<SpecialMove> items, int itemLayout) {
            this.items = items;
            this.itemLayout = itemLayout;
        }

        public void setData(List<SpecialMove> items) {
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
            SpecialMove item = items.get(position);

            viewHolder.reason.setText(item.getTypeMovement().getName());

            int sumMM = 0;

            for (SpecialMoveDetail temp : item.getSpecialMoveDetails()) {
                    sumMM = sumMM + temp.getQty();
            }

            if(item.getReasonId().equals(Constants.IN)){
                viewHolder.image.setImageResource(R.drawable.fa_send);
                viewHolder.typeMovement.setText("Ingreso");
            }else{
                viewHolder.image.setImageResource(R.drawable.fa_receive);
                viewHolder.typeMovement.setText("Salida");
            }

            SimpleDateFormat formatter = new SimpleDateFormat(Constants.FORMAT_DATETIME_SLASH);
            String dateFormat = formatter.format(item.getCreatedAt());
            viewHolder.date.setText(Constants.DATE_FIELD+dateFormat);



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
            public TextView reason;
            public TextView typeMovement;
            public TextView date;
            public String uuid;

            public ViewHolder(View itemView) {
                super(itemView);
                reason = (TextView) itemView.findViewById(R.id.specialmove_reason);
                typeMovement = (TextView) itemView.findViewById(R.id.specialmove_type);

                date = (TextView) itemView.findViewById(R.id.specialmove_date);
                image = (ImageView) itemView.findViewById(R.id.specialmove_image);
                sent = (ImageView) itemView.findViewById(R.id.specialmove_sent);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ShowSpecialMoveActivity.class);
                intent.putExtra(Constants.UUID, uuid);
                startActivityForResult(intent, SHOW_SPECIALMOVE_REQUEST);
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
        if (requestCode == ADD_SPECIALMOVE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                refreshSpecialMoveUi();
            }
        }
    }

    public void refreshSpecialMoveUi(){

        result = realm.where(SpecialMove.class).
                equalTo("active", Boolean.TRUE).
                findAll();
        result.sort("recordDate", Sort.DESCENDING);
        mAdapter.setData(result);
        mAdapter.notifyDataSetChanged();

        ((AppCompatActivity) getActivity()).getSupportActionBar().
                setSubtitle(Constants.QTY_FIELD+String.valueOf(result.size()));
    }
}
