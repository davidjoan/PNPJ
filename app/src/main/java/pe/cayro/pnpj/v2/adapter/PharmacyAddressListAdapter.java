package pe.cayro.pnpj.v2.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pe.cayro.pnpj.v2.R;
import pe.cayro.pnpj.v2.model.PharmacyAddress;

/**
 * Created by David on 3/02/16.
 */
public class PharmacyAddressListAdapter extends RecyclerView.
        Adapter<PharmacyAddressListAdapter.ViewHolder> {

    private List<PharmacyAddress> items;
    private int itemLayout;

    public PharmacyAddressListAdapter(List<PharmacyAddress> items, int itemLayout) {
        this.items = items;
        this.itemLayout = itemLayout;
    }

    public void setData(List<PharmacyAddress> items) {
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
        PharmacyAddress item = items.get(position);
        viewHolder.name.setText(item.getName());
        viewHolder.qty.setText(item.getAddress());
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
        public TextView qty;
        public int id;

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