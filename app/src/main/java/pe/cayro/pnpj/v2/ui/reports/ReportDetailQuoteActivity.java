package pe.cayro.pnpj.v2.ui.reports;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pe.cayro.pnpj.v2.R;
import pe.cayro.pnpj.v2.api.RestClient;
import pe.cayro.pnpj.v2.model.report.MedicalSampleShare;
import pe.cayro.pnpj.v2.util.Constants;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ReportDetailQuoteActivity extends AppCompatActivity {

    String username;
    int userId;
    @Bind(R.id.report_recycler_view)
    protected RecyclerView mRecyclerView;
    @Bind(R.id.report_quote_detail_title)
    protected TextView title;

    private List<MedicalSampleShare> reportList;
    private ReportQuotedDetailListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    int institutionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail_quote);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        username  = getIntent().getStringExtra(Constants.NAME);
        userId  = getIntent().getIntExtra(Constants.USER_ID, 0);

        ButterKnife.bind(this);

        reportList = new ArrayList<>();

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ReportQuotedDetailListAdapter(reportList, R.layout.quote_detail_item);
        mRecyclerView.setAdapter(mAdapter);


        institutionId = getIntent().getIntExtra(Constants.ID, 0);
        float day = getIntent().getFloatExtra(Constants.DAY,0);
        float week = getIntent().getFloatExtra(Constants.WEEK,0);
        float month = getIntent().getFloatExtra(Constants.MONTH,0);
        String name = getIntent().getStringExtra(Constants.INSTITUTION_NAME);
        //getSupportActionBar().setSubtitle(name);

        title.setText(name);

        getData();

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(month, 0));
        entries.add(new BarEntry(week, 1));
        entries.add(new BarEntry(day, 2));


        BarDataSet dataset = new BarDataSet(entries, "% Avance de Cuota");

        ArrayList<String> labels = new ArrayList<>();
        labels.add("Mes");
        labels.add("Semana");
        labels.add("Día");


        HorizontalBarChart chart = (HorizontalBarChart) findViewById(R.id.chart1);

        //setContentView(chart);

        BarData data = new BarData(labels, dataset);
        data.setValueFormatter(new PercentFormatter());
        chart.setData(data);
        chart.setDescription("");
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setSpaceBetweenLabels(0);
        xAxis.setDrawGridLines(false);

        //chart.saveToGallery("cuota.jpg", 85); // 85 is the quality of the image
    }

    public void getData(){
        RestClient.get().getShareMedicalSample(userId,institutionId, new Callback<List<MedicalSampleShare>>() {
            @Override
            public void success(List<MedicalSampleShare> medicalSampleList, Response response) {
                mAdapter.setData(medicalSampleList);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }


    public class ReportQuotedDetailListAdapter extends RecyclerView.
            Adapter<ReportQuotedDetailListAdapter.ViewHolder> {

        private List<MedicalSampleShare> items;
        private int itemLayout;

        public ReportQuotedDetailListAdapter(List<MedicalSampleShare> items, int itemLayout) {
            this.items = items;
            this.itemLayout = itemLayout;
        }

        public void setData(List<MedicalSampleShare> items) {
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
            MedicalSampleShare item = items.get(position);
            viewHolder.name.setText(item.getName());

            viewHolder.day.setText("Día: "+ String.valueOf(item.getCantday())+"/"+String.valueOf(item.getShareday())+" "+String.valueOf( item.getPorcday())+"%");

            if( item.getPorcday() < item.getIndicator() ){
                viewHolder.dayImagen.setImageResource(R.drawable.vp2);
            }else if(item.getPorcday() < 100){
                viewHolder.dayImagen.setImageResource(R.drawable.vp1);
            }else{
                viewHolder.dayImagen.setImageResource(R.drawable.vp0);
            }

            viewHolder.week.setText("Semana: "+ String.valueOf(item.getCantweek())+"/"+String.valueOf(item.getShareweek())+" "+String.valueOf(item.getPorcweek())+"%");

            if( item.getPorcweek() < item.getIndicator() ){
                viewHolder.weekImagen.setImageResource(R.drawable.vp2);
            }else if(item.getPorcweek() < 100){
                viewHolder.weekImagen.setImageResource(R.drawable.vp1);
            }else{
                viewHolder.weekImagen.setImageResource(R.drawable.vp0);
            }


            viewHolder.month.setText("Mes: "+ String.valueOf(item.getCantmonth())+"/"+String.valueOf(item.getSharemonth())+" "+String.valueOf(item.getPorcmonth())+"%");

            if( item.getPorcmonth() < item.getIndicator() ){
                viewHolder.monthImagen.setImageResource(R.drawable.vp2);
            }else if(item.getPorcmonth() < 100){
                viewHolder.monthImagen.setImageResource(R.drawable.vp1);
            }else{
                viewHolder.monthImagen.setImageResource(R.drawable.vp0);
            }

            viewHolder.itemView.setTag(item);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView name;
            public TextView day;
            public TextView week;
            public TextView month;
            public ImageView dayImagen;
            public ImageView weekImagen;
            public ImageView monthImagen;

            public ViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.report_quote_name);
                day = (TextView) itemView.findViewById(R.id.report_quote_day);
                week = (TextView) itemView.findViewById(R.id.report_quote_week);
                month = (TextView) itemView.findViewById(R.id.report_quote_month);

                dayImagen = (ImageView) itemView.findViewById(R.id.image_day);
                weekImagen = (ImageView) itemView.findViewById(R.id.image_week);
                monthImagen = (ImageView) itemView.findViewById(R.id.image_month);
            }
        }
    }


}
