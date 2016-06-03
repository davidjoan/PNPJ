package pe.cayro.pnpj.v2;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import pe.cayro.pnpj.v2.adapter.RecordDetailListAdapter;
import pe.cayro.pnpj.v2.adapter.SpecialMoveDetailListAdapter;
import pe.cayro.pnpj.v2.model.Record;
import pe.cayro.pnpj.v2.model.SpecialMove;
import pe.cayro.pnpj.v2.util.Constants;

public class ShowSpecialMoveActivity extends AppCompatActivity {

    private String uuid;
    private Realm realm;
    private SpecialMove specialMove;

    @Bind(R.id.textView)
    protected TextView type;
    @Bind(R.id.special_move_reason_autocompleter)
    protected TextView reason;
    @Bind(R.id.special_move_date)
    protected TextView date;
    @Bind(R.id.special_move_description)
    protected TextView comment;
    @Bind(R.id.special_move_detail_list)
    protected RecyclerView mRecyclerView;

    private RecyclerView.LayoutManager mLayoutManager;
    private SpecialMoveDetailListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_special_move);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        uuid = getIntent().getStringExtra(Constants.UUID);
        realm = Realm.getDefaultInstance();
        specialMove = realm.where(SpecialMove.class).equalTo(Constants.UUID, uuid).findFirst();
        type.setText(specialMove.getReasonId().equals("I")?"Ingreso":"Salida");

        reason.setText(specialMove.getTypeMovement().getName());

        SimpleDateFormat formatter = new SimpleDateFormat(Constants.FORMAT_DATE_SLASH);
        String dateFormat = formatter.format(specialMove.getRecordDate());
        date.setText(dateFormat);

        comment.setText(specialMove.getComment());

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new SpecialMoveDetailListAdapter(specialMove.getSpecialMoveDetails(),
                R.layout.record_detail_item);
        mRecyclerView.setAdapter(mAdapter);

    }

}
