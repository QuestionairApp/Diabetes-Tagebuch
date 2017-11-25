package diabetestagebuch.mkservices.de.diabetes_tagebuch;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BzListe extends AppCompatActivity {

    private Spinner spinner;
    private ArrayList<bzData> bzListe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bz_liste);
        Toolbar toolbar = (Toolbar) findViewById(R.id.bzliste_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        spinner = (Spinner)findViewById(R.id.bzliste_spinner);
        bzListe= new ArrayList<>();
        Button delBtn=(Button)findViewById(R.id.detail_delete_btn);
        delBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Spinner spn=(Spinner)findViewById(R.id.bzliste_spinner);
                int pos=spn.getSelectedItemPosition();
                final Context ctx=getApplicationContext();
                SharedPreferences sharedPref=ctx.getSharedPreferences(getString(R.string.PREF_KEY_FILE), Context.MODE_PRIVATE);
                String bzJsonArr=sharedPref.getString(getString(R.string.werte_array),"");
                JSONArray jArrNew=new JSONArray();
                try{
                    JSONArray jArr=new JSONArray(bzJsonArr);
                    for(int i=0; i<jArr.length(); i++){
                        if(i!=pos) {
                            jArrNew.put(jArr.get(i));
                        }
                    }
                    SharedPreferences.Editor edit=sharedPref.edit();
                    edit.putString(getString(R.string.werte_array), jArrNew.toString());
                    edit.apply();
                    bzListe.clear();
                    getBzPrefs();
                    BzListeSpinnerAdapter adapter=(BzListeSpinnerAdapter)spn.getAdapter();
                    adapter.notifyDataSetChanged();

                } catch(JSONException e){}
                Toast.makeText(getApplicationContext(), "Entry removed", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        getBzPrefs();
        BzListeSpinnerAdapter adapter=new BzListeSpinnerAdapter(this, R.layout.detail_item, bzListe);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bzData tmp= bzListe.get(position);
                TextView bz=(TextView)findViewById(R.id.detail_bz_txt);
                TextView ke=(TextView)findViewById(R.id.detail_ke_txt);
                TextView ie=(TextView)findViewById(R.id.detail_ie_txt);
                TextView lantus=(TextView)findViewById(R.id.detail_lantus_txt);
                TextView syst=(TextView)findViewById(R.id.detail_syst_txt);
                TextView diast=(TextView)findViewById(R.id.detail_diast_txt);
                TextView puls=(TextView)findViewById(R.id.detail_puls_txt);
                bz.setText(tmp.getBz());
                ke.setText(tmp.getKe());
                ie.setText(tmp.getIe());
                lantus.setText(tmp.getLantus());
                syst.setText(tmp.getSyst());
                diast.setText(tmp.getDiast());
                puls.setText(tmp.getPuls());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getBzPrefs(){
        final Context ctx=getApplicationContext();
        SharedPreferences sharedPref=ctx.getSharedPreferences(getString(R.string.PREF_KEY_FILE), Context.MODE_PRIVATE);
        String bzJsonArr=sharedPref.getString(getString(R.string.werte_array),"");
        JSONArray jArr=new JSONArray();
        JSONObject jObj=new JSONObject();
        try {
            jArr = new JSONArray(bzJsonArr);
            for(int jl=0;jl<jArr.length();jl++){
                JSONObject tmp=jArr.getJSONObject(jl);
                bzData bz=new bzData();
                bz.setDatum(tmp.getString("datum"));
                bz.setZeit(tmp.getString("zeit"));
                bz.setBz(tmp.getString("bz"));
                bz.setKe(tmp.getString("ke"));
                bz.setIe(tmp.getString("ie"));
                bz.setLantus(tmp.getString("lantus"));
                bz.setSyst(tmp.getString("syst"));
                bz.setDiast(tmp.getString("diast"));
                bz.setPuls(tmp.getString("puls"));
                bzListe.add(bz);
            }
        } catch(JSONException e){}

    }

}
