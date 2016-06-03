package pe.cayro.pnpj.v2.adapter;

import android.content.Context;
import android.util.Log;
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
import pe.cayro.pnpj.v2.model.Patient;
import pe.cayro.pnpj.v2.util.Constants;

/**
 * Created by David on 12/01/16.
 */
public class PatientAutocompleterAdapter extends ArrayAdapter<String> implements Filterable {

    private static String TAG = PatientAutocompleterAdapter.class.getSimpleName();

    public PatientAutocompleterAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    private class ViewHolder {
        TextView name;
        TextView code;
        TextView ubigeo;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String uuid = getItem(position);

        Log.i(TAG, uuid);

        Realm realm = Realm.getDefaultInstance();
        /* TODO: Check if exist another way to load a information of patient using Realm. */
        Patient patient = realm.where(Patient.class).equalTo(Constants.UUID, uuid).findFirst();

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.patient_autocomplete_item, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.patient_name);
            viewHolder.code = (TextView) convertView.findViewById(R.id.patient_code);
            viewHolder.ubigeo = (TextView) convertView.findViewById(R.id.patient_ubigeo);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(new StringBuilder().append(patient.getFirstname()).
                append(Constants.SPACE).append(patient.getLastname()).
                append(Constants.SPACE).append(patient.getSurname()).toString());

        if (patient.getUbigeo() != null) {
            viewHolder.ubigeo.setText(patient.getUbigeo().getName()+
                    Constants.DASH_SEPARATOR+patient.getUbigeo().getProvince());
        }

        viewHolder.code.setText(Constants.DNI_FIELD + patient.getCode());

        realm.close();

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

                RealmResults<Patient> realmResults = realm.where(Patient.class).beginGroup().
                        contains(Constants.FIRSTNAME, constraint.toString().toUpperCase()).or().
                        contains(Constants.LASTNAME, constraint.toString().toUpperCase()).or().
                        contains(Constants.SURNAME, constraint.toString().toUpperCase()).or().
                        contains(Constants.CODE, constraint.toString().toUpperCase()).
                        endGroup().findAll();

                for(Patient patient : realmResults){
                    data.add(patient.getUuid());
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
                    addAll((ArrayList<String>) results.values);
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }
}