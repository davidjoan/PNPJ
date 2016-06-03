package pe.cayro.pnpj.v2.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pe.cayro.pnpj.v2.R;
import pe.cayro.pnpj.v2.model.RecordDetail;
import pe.cayro.pnpj.v2.util.Constants;

/**
 * Created by David on 3/02/16.
 */
public class RecordDetailListAdapter extends RecyclerView.
        Adapter<RecordDetailListAdapter.ViewHolder> {

    private List<RecordDetail> items;
    private int itemLayout;

    public RecordDetailListAdapter(List<RecordDetail> items, int itemLayout) {
        this.items = items;
        this.itemLayout = itemLayout;
    }

    public void setData(List<RecordDetail> items) {
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
        RecordDetail item = items.get(position);
        viewHolder.name.setText(item.getProduct().getName());
        if(item.getRecord().getAttentionTypeId() == 2) {
            viewHolder.qty.setText(Constants.QTY_FIELD + String.valueOf(item.getQty())+
                    ", C Bonificada: "+String.valueOf(item.getQtyCalculated()));
        }else{
            viewHolder.qty.setText(Constants.QTY_FIELD + String.valueOf(item.getQty()));
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

        public TextView name;
        public TextView qty;
        public String uuid;

        public ViewHolder(View itemView) {
            super(itemView);
            name    = (TextView) itemView.findViewById(R.id.record_detail_name);
            qty = (TextView) itemView.findViewById(R.id.record_detail_qty);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
                /* TODO: Implement Intent to edit the patient information */
        }
    }
}