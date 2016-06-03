package pe.cayro.pnpj.v2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import pe.cayro.pnpj.v2.adapter.AgentAutocompleterAdapter;
import pe.cayro.pnpj.v2.adapter.InstitutionAutocompleterAdapter;
import pe.cayro.pnpj.v2.model.Agent;
import pe.cayro.pnpj.v2.model.Institution;
import pe.cayro.pnpj.v2.util.Constants;

public class AddAgentActivity extends AppCompatActivity {

    @Bind(R.id.agent_add)
    protected Button agentAdd;
    @Bind(R.id.agent_cancel)
    protected Button agentCancel;
    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.agent_agent_autocompleter)
    protected AutoCompleteTextView agentAgent;
    @Bind(R.id.agent_institution_autocompleter)
    protected AutoCompleteTextView agentInstitution;

    private Realm realm;
    private Agent agent;
    private Institution institution;
    private AgentAutocompleterAdapter adapterAgent;
    private InstitutionAutocompleterAdapter adapterInstitution;

    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_agent);

        ButterKnife.bind(this);

        settings = getSharedPreferences(Constants.PREFERENCES_SAM, 0);

        realm = Realm.getDefaultInstance();

        toolbar.setTitle(R.string.add_agent);
        setSupportActionBar(toolbar);

        adapterAgent = new AgentAutocompleterAdapter(this, R.layout.agent_autocomplete_item);
        agentAgent.setAdapter(adapterAgent);
        agentAgent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                Integer temp = adapterAgent.getItem(position);
                agent = realm.where(Agent.class).equalTo(Constants.ID, temp.intValue()).findFirst();
                agentAgent.setText(agent.getName());
            }
        });

        adapterInstitution = new InstitutionAutocompleterAdapter(this,
                R.layout.institution_autocomplete_item);
        agentInstitution.setAdapter(adapterInstitution);
        agentInstitution.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                Integer temp = adapterInstitution.getItem(position);
                institution = realm.where(Institution.class).equalTo(Constants.ID,
                        temp.intValue()).findFirst();
                agentInstitution.setText(institution.getName());
            }
        });

        if(settings.getInt(Constants.DEFAULT_AGENT_ID,0) > 0){
            agent = realm.where(Agent.class)
                  .equalTo(Constants.ID, settings.getInt(Constants.DEFAULT_AGENT_ID, 0))
                  .findFirst();
            agentAgent.setText(agent.getName());

            institution = realm.where(Institution.class)
                    .equalTo(Constants.ID, settings.getInt(Constants.DEFAULT_INSTITUTION_ID, 0))
                    .findFirst();

            agentInstitution.setText(institution.getName());
        }

        agentAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int countErrors = 0;

                if(agentAgent.getText().length() < 2){
                    countErrors++;
                    agentAgent.setError("El representante es requerido");
                }
                if(agentInstitution.getText().length() < 2){
                    countErrors++;
                    agentInstitution.setError("La institución es requerida");
                }
                if(agent == null){
                    countErrors++;
                    agentAgent.setError("El representante es incorrecto");
                }
                if(institution == null){
                    countErrors++;
                    agentInstitution.setError("La institución es incorrecta");
                }

                if(countErrors == 0){

                    SharedPreferences settings = getApplicationContext().
                            getSharedPreferences(Constants.PREFERENCES_SAM, 0);
                    SharedPreferences.Editor editor = settings.edit();

                    editor.putInt(Constants.DEFAULT_AGENT_ID, agent.getId());
                    editor.putInt(Constants.DEFAULT_INSTITUTION_ID, institution.getId());

                    editor.commit();

                    Intent intent = new Intent();
                    if (getParent() == null) {
                        setResult(Activity.RESULT_OK, intent);
                    } else {
                        getParent().setResult(Activity.RESULT_OK, intent);
                    }

                    finish();
                }
            }
        });

        agentCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences settings = getApplicationContext().
                        getSharedPreferences(Constants.PREFERENCES_SAM, 0);
                SharedPreferences.Editor editor = settings.edit();

                editor.putInt(Constants.DEFAULT_AGENT_ID, 0);
                editor.putInt(Constants.DEFAULT_INSTITUTION_ID, 0);

                editor.apply();

                Intent intent = new Intent();
                if (getParent() == null) {
                    setResult(Activity.RESULT_CANCELED, intent);
                } else {
                    getParent().setResult(Activity.RESULT_CANCELED, intent);
                }
                finish();
            }
        });
    }

    /**
     * Exit the app if user select yes.
     */
    private void doExit() {
    }

    @Override
    public void onBackPressed() {
        doExit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK) {
            doExit();
        }
        return super.onKeyDown(keyCode, event);
    }
}