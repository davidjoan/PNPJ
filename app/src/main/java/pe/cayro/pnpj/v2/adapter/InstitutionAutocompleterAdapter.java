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
import pe.cayro.pnpj.v2.model.Institution;
import pe.cayro.pnpj.v2.util.Constants;

/**
 * Created by David on 12/01/16.
 */
public class InstitutionAutocompleterAdapter extends ArrayAdapter<Integer> implements Filterable {
    private static String TAG = InstitutionAutocompleterAdapter.class.getSimpleName();

    public InstitutionAutocompleterAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    private class ViewHolder {
        TextView name;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Integer id = getItem(position);

        Realm realm = Realm.getDefaultInstance();
        Institution institution = realm.where(Institution.class).equalTo(Constants.ID, id).findFirst();
        realm.close();

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.institution_autocomplete_item, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.institution_name);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText((institution.getBusinessname().length() > 30) ?
                institution.getBusinessname().substring(0, 30) + Constants.ELLIPSIS :
                institution.getBusinessname());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<String> data = new ArrayList<String>();
                FilterResults filterResults = new FilterResults();

                Realm  realm = Realm.getDefaultInstance();

                RealmResults<Institution> realmResults = realm.where(Institution.class).
                        contains(Constants.NAME, constraint.toString()).findAll();

                for(Institution institution : realmResults){
                    data.add(String.valueOf(institution.getUuid()));
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