package pe.cayro.pnpj.v2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import pe.cayro.pnpj.v2.R;
import pe.cayro.pnpj.v2.model.Pharmacy;
import pe.cayro.pnpj.v2.util.Constants;

/**
 * Created by David on 12/01/16.
 */
public class PharmacyAutocompleterAdapter extends ArrayAdapter<Integer> implements Filterable {

    private static String TAG = PharmacyAutocompleterAdapter.class.getSimpleName();

    public PharmacyAutocompleterAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    private class ViewHolder {
        ImageView image;
        TextView name;
        TextView code;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Integer id = getItem(position);
        //Log.d(TAG, uuid);

        /* TODO: Check if exist another way to load a information of doctor using Realm. */
        Realm realm = Realm.getDefaultInstance();
        Pharmacy pharmacy = realm.where(Pharmacy.class).equalTo(Constants.ID, id).findFirst();
        realm.close();

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.doctor_autocomplete_item, parent, false);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.doctor_image);
            viewHolder.name  = (TextView)  convertView.findViewById(R.id.doctor_name);
            viewHolder.code  = (TextView)  convertView.findViewById(R.id.doctor_code);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(pharmacy.getName());
        viewHolder.code.setText(Constants.RUC_FIELD+pharmacy.getCode());
        viewHolder.image.setImageResource(R.drawable.fa_hospital_o);

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

                if(constraint != null) {
                    RealmResults<Pharmacy> realmResults = realm.where(Pharmacy.class).beginGroup().
                            contains(Constants.NAME, constraint.toString().toUpperCase()).or().
                            contains(Constants.CODE, constraint.toString().toUpperCase()).
                            endGroup().
                            findAll();

                    int counter = 0;

                    for (Pharmacy pharmacy : realmResults) {
                        counter++;
                        data.add(pharmacy.getId());
                        if (counter == 10) {
                            break;
                        }
                    }
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