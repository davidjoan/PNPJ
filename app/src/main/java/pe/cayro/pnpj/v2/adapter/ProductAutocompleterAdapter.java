package pe.cayro.pnpj.v2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import pe.cayro.pnpj.v2.R;
import pe.cayro.pnpj.v2.model.Product;
import pe.cayro.pnpj.v2.util.Constants;

/**
 * Created by David on 12/01/16.
 */
public class ProductAutocompleterAdapter extends ArrayAdapter<Integer> implements Filterable {
    private static String TAG = ProductAutocompleterAdapter.class.getSimpleName();

    public ProductAutocompleterAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    private class ViewHolder {
        TextView name;
        TextView code;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Integer id = getItem(position);

        Realm realm = Realm.getDefaultInstance();
        Product product = realm.where(Product.class).equalTo(Constants.ID, id).findFirst();
        realm.close();

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.product_autocomplete_item, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.product_name);
            viewHolder.code = (TextView) convertView.findViewById(R.id.product_code);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        /*viewHolder.name.setText((product.getName().length() > 25) ?
                product.getName().substring(0, 25) + Constants.ELLIPSIS :
                product.getName());*/
        viewHolder.name.setText(product.getName());
        viewHolder.code.setText(product.getCode());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<Integer> data = new ArrayList<Integer>();
                FilterResults filterResults = new FilterResults();

                Realm  realm = Realm.getDefaultInstance();

                RealmResults<Product> realmResults = realm.where(Product.class).beginGroup().
                        contains(Constants.NAME, constraint.toString().toUpperCase()).or().
                        contains(Constants.CODE, constraint.toString().toUpperCase()).
                        endGroup().findAll();

                for(Product product : realmResults){
                    data.add(Integer.valueOf(product.getId()));
                }

                filterResults.values = data;
                filterResults.count = data.size();
                realm.close();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    clear();
                    addAll((ArrayList<Integer>) results.values);
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }
}