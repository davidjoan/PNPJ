package pe.cayro.pnpj.v2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.lang.reflect.Type;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import pe.cayro.pnpj.v2.R;
import pe.cayro.pnpj.v2.model.TypeMovement;
import pe.cayro.pnpj.v2.model.Ubigeo;
import pe.cayro.pnpj.v2.util.Constants;

/**
 * Created by David on 02/05/16.
 */
public class TypeMovementAutocompleterAdapter extends ArrayAdapter<Integer> implements Filterable {

    private static String TAG = TypeMovementAutocompleterAdapter.class.getSimpleName();
    private String parameter;

    public TypeMovementAutocompleterAdapter(Context context, int textViewResourceId, String parameter) {
        super(context, textViewResourceId);
        this.parameter = parameter;
    }

    private class ViewHolder {
        TextView name;
    }


    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Integer id = getItem(position);

        Realm realm = Realm.getDefaultInstance();
        TypeMovement typeMovement = realm.where(TypeMovement.class).equalTo(Constants.ID, id).findFirst();
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

        viewHolder.name.setText(new StringBuilder().append(typeMovement.getName()).toString());

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

                            RealmResults<TypeMovement> realmResults = realm.where(TypeMovement.class).
                            contains(Constants.NAME, constraint.toString().toUpperCase()).
                                    equalTo(Constants.TYPE, parameter).
                            findAll();

                    int counter = 0;

                    for(TypeMovement typeMovement : realmResults){
                        counter++;
                        data.add(Integer.valueOf(typeMovement.getId()));
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