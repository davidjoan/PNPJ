package pe.cayro.pnpj.v2.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pe.cayro.pnpj.v2.R;
import pe.cayro.pnpj.v2.model.report.Report;
import pe.cayro.pnpj.v2.ui.reports.ReportUserActivity;
import pe.cayro.pnpj.v2.util.Constants;

/**
 * Created by David on 20/01/16.
 */
public class FragmentReport extends Fragment {

    private static String TAG = FragmentTracking.class.getSimpleName();


    @Bind(R.id.report_recycler_view)
    protected RecyclerView mRecyclerView;

    private List<Report> reportList;
    private ReportListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static FragmentReport newInstance() {
        Bundle args = new Bundle();

        FragmentReport fragment = new FragmentReport();
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
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        ButterKnife.bind(this, view);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.reports);

        reportList = new ArrayList<Report>();

        reportList.add(new Report(1,"Reporte Cuotas"));
        reportList.add(new Report(2,"Reporte Stock"));

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ReportListAdapter(reportList, R.layout.report_item);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    public class ReportListAdapter extends RecyclerView.
            Adapter<ReportListAdapter.ViewHolder> {

        private List<Report> items;
        private int itemLayout;

        public ReportListAdapter(List<Report> items, int itemLayout) {
            this.items = items;
            this.itemLayout = itemLayout;
        }

        public void setData(List<Report> items) {
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
            Report item = items.get(position);
            viewHolder.name.setText(item.getName());
            viewHolder.id = item.getId();
            viewHolder.itemView.setTag(item);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
                implements RecyclerView.OnClickListener {

            public TextView name;
            public int id;

            public ViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.report_name);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), ReportUserActivity.class);
                    intent.putExtra(Constants.REPORT_ID, id); //REPORT ID
                    startActivity(intent);
            }
        }
    }
}
