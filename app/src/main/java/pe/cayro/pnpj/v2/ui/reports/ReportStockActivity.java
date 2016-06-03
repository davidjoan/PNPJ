package pe.cayro.pnpj.v2.ui.reports;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pe.cayro.pnpj.v2.R;
import pe.cayro.pnpj.v2.api.RestClient;
import pe.cayro.pnpj.v2.model.report.Stock;
import pe.cayro.pnpj.v2.util.Constants;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ReportStockActivity extends AppCompatActivity {

    private int reportId;
    private int userId;
    private String name;

    @Bind(R.id.report_stock_recycler_view)
    protected RecyclerView mRecyclerView;

    private List<Stock> reportList;
    private ReportStockListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_stock);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        reportId = getIntent().getIntExtra(Constants.REPORT_ID, 0);
        userId = getIntent().getIntExtra(Constants.USER_ID, 0);
        name  = getIntent().getStringExtra(Constants.NAME);

        toolbar.setSubtitle(name);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        reportList = new ArrayList<>();

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ReportStockListAdapter(reportList,R.layout.stock_item);
        mRecyclerView.setAdapter(mAdapter);
        getData();
    }


    public void getData(){
        RestClient.get().getStockDependent(userId, new Callback<List<Stock>>() {
            @Override
            public void success(List<Stock> usersDependents, Response response) {
                mAdapter.setData(usersDependents);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public class ReportStockListAdapter extends RecyclerView.
            Adapter<ReportStockListAdapter.ViewHolder> {

        private List<Stock> items;
        private int itemLayout;

        public ReportStockListAdapter(List<Stock> items, int itemLayout) {
            this.items = items;
            this.itemLayout = itemLayout;
        }

        public void setData(List<Stock> items) {
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
            Stock item = items.get(position);
            viewHolder.name.setText(item.getName());
            viewHolder.stock.setText("Stock: "+item.getStock());
            viewHolder.itemView.setTag(item);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView name;
            public TextView stock;
            public ViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.stock_name);
                stock = (TextView) itemView.findViewById(R.id.stock_stock);
            }
        }
    }
}
