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
import pe.cayro.pnpj.v2.model.InstitutionZone;
import pe.cayro.pnpj.v2.util.Constants;

/**
 * Created by David on 12/01/16.
 */
public class ZoneAutocompleterAdapter extends ArrayAdapter<Integer> implements Filterable {
    private static String TAG = ZoneAutocompleterAdapter.class.getSimpleName();

    public ZoneAutocompleterAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    private class ViewHolder {
        TextView name;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Integer id = getItem(position);

        Realm realm = Realm.getDefaultInstance();
        InstitutionZone ubigeo = realm.where(InstitutionZone.class).equalTo(Constants.ID, id).findFirst();
        realm.close();

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.ubigeo_autocomplete_item, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.ubigeo_name);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(new StringBuilder().append(ubigeo.getName()).
                append(Constants.DASH_SEPARATOR).append(ubigeo.getProvince()).toString());

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

                if(constraint != null){

                    RealmResults<InstitutionZone> realmResults = realm.where(InstitutionZone.class).
                            contains(Constants.NAME, constraint.toString().toUpperCase()).
                            findAll();

                    int counter = 0;

                    for(InstitutionZone institutionZone : realmResults){
                        counter++;
                        data.add(Integer.valueOf(institutionZone.getId()));
                        if(counter == 10){
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