package pe.cayro.pnpj.v2.ui.reports;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import pe.cayro.pnpj.v2.R;
import pe.cayro.pnpj.v2.api.RestClient;
import pe.cayro.pnpj.v2.model.User;
import pe.cayro.pnpj.v2.model.report.UsersDependent;
import pe.cayro.pnpj.v2.util.Constants;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ReportUserActivity extends AppCompatActivity {


    User user;
    Realm realm;
    int reportId;

    @Bind(R.id.report_user_recycler_view)
    protected RecyclerView mRecyclerView;

    private List<UsersDependent> reportList;
    private ReportUserListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reportId = getIntent().getIntExtra(Constants.REPORT_ID, 0);

        realm = Realm.getDefaultInstance();

        ButterKnife.bind(this);

        user = realm.where(User.class).findFirst();

        reportList = new ArrayList<>();

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ReportUserListAdapter(reportList,R.layout.user_item);
        mRecyclerView.setAdapter(mAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getData();

    }

    public void getData(){
        RestClient.get().getUserDependent(user.getId(), new Callback<List<UsersDependent>>() {
            @Override
            public void success(List<UsersDependent> usersDependents, Response response) {
                mAdapter.setData(usersDependents);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }



    public class ReportUserListAdapter extends RecyclerView.
            Adapter<ReportUserListAdapter.ViewHolder> {

        private List<UsersDependent> items;
        private int itemLayout;

        public ReportUserListAdapter(List<UsersDependent> items, int itemLayout) {
            this.items = items;
            this.itemLayout = itemLayout;
        }

        public void setData(List<UsersDependent> items) {
            this.items = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(itemLayout,
                    parent, false);
            return new ViewHolder(itemLayoutView);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            UsersDependent item = items.get(position);
            viewHolder.name.setText(item.getName());
            viewHolder.code.setText("Código: "+item.getCode());
            viewHolder.id = item.getId();

            Picasso.with(getApplicationContext()).
                    load(new StringBuilder().append(Constants.USER_PHOTO_SERVER).
                            append(item.getCode()).
                            append(Constants.DOT_JPG).toString()).
                    error(R.drawable.avatar).
                    into(viewHolder.photo);

            viewHolder.itemView.setTag(item);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
                implements RecyclerView.OnClickListener {

            public ImageView photo;
            public TextView name;
            public TextView code;
            public int id;

            public ViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.report_user_name);
                code = (TextView) itemView.findViewById(R.id.report_user_code);
                photo = (ImageView) itemView.findViewById(R.id.report_user_image);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {

                Log.i("todo", ""+reportId);
                Log.i("id:",  ""+id);

                switch(reportId){
                    case 1:
                        Intent intent = new Intent(getApplication(), ReportQuoteActivity.class);
                        intent.putExtra(Constants.USER_ID, id);
                        intent.putExtra(Constants.REPORT_ID, reportId);
                        intent.putExtra(Constants.NAME, name.getText());
                        startActivity(intent);
                        break;
                    case 2:
                        Intent intent2 = new Intent(getApplication(), ReportStockActivity.class);
                        intent2.putExtra(Constants.USER_ID, id);
                        intent2.putExtra(Constants.REPORT_ID, reportId);
                        intent2.putExtra(Constants.NAME, name.getText());
                        startActivity(intent2);
                        break;
                }


            }
        }
    }

}
