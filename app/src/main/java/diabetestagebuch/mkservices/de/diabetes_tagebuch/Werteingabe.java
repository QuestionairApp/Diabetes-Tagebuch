package diabetestagebuch.mkservices.de.diabetes_tagebuch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Date;


public class Werteingabe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_werteingabe);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Button btn=(Button)findViewById(R.id.saveValuesBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText datumEdit=(EditText)findViewById(R.id.dateEdit);
                EditText zeitEdit=(EditText)findViewById(R.id.timeEdit);
                EditText bzEdit=(EditText)findViewById(R.id.bzEdit);
                EditText keEdit=(EditText)findViewById(R.id.keEdit);
                EditText ieEdit=(EditText)findViewById(R.id.ieEdit);
                EditText lantusEdit=(EditText)findViewById(R.id.lantusEdit);
                EditText systEdit=(EditText)findViewById(R.id.systEdit);
                EditText diastEdit=(EditText)findViewById(R.id.diastEdit);
                EditText pulsEdit=(EditText)findViewById(R.id.pulsEdit);

                String datum = String.valueOf(datumEdit.getText()).trim();
                String zeit= String.valueOf(zeitEdit.getText()).trim();
                String bz= String.valueOf(bzEdit.getText()).trim();
                String ke= String.valueOf(keEdit.getText()).trim();
                String ie= String.valueOf(ieEdit.getText()).trim();
                String lantus=String.valueOf(lantusEdit.getText()).trim();
                String syst= String.valueOf(systEdit.getText()).trim();
                String diast= String.valueOf(diastEdit.getText()).trim();
                String puls= String.valueOf(pulsEdit.getText()).trim();

                String currentTimeString = DateFormat.getTimeInstance().format(new Date());
                zeitEdit.setText(currentTimeString, TextView.BufferType.EDITABLE);
                bzEdit.setText("", TextView.BufferType.EDITABLE);
                keEdit.setText("", TextView.BufferType.EDITABLE);
                ieEdit.setText("", TextView.BufferType.EDITABLE);
                lantusEdit.setText("", TextView.BufferType.EDITABLE);
                systEdit.setText("", TextView.BufferType.EDITABLE);
                diastEdit.setText("", TextView.BufferType.EDITABLE);
                pulsEdit.setText("", TextView.BufferType.EDITABLE);

                JSONObject data = new JSONObject();

                try {
                    data.put("datum", datum);
                    data.put("zeit", zeit);
                    data.put("bz", bz);
                    data.put("ke", ke);
                    data.put("ie", ie);
                    data.put("lantus", lantus);
                    data.put("syst", syst);
                    data.put("diast", diast);
                    data.put("puls", puls);
                } catch(JSONException je){

                }
                Context ctx=getApplicationContext();
                SharedPreferences sharedPref=ctx.getSharedPreferences(getString(R.string.PREF_KEY_FILE), Context.MODE_PRIVATE);
                String str=sharedPref.getString(getString(R.string.werte_array),"");
                JSONArray werte=new JSONArray();
                if(str.length()>0){
                    try {
                        werte = new JSONArray(str);
                    } catch(JSONException e){

                    }
                } else {
                    werte=new JSONArray();
                }
                werte=werte.put(data);
                SharedPreferences.Editor editor=sharedPref.edit();
                editor.putString(getString(R.string.werte_array), werte.toString());
                editor.apply();
                Toast.makeText(v.getContext(), "Saved", Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public void onResume(){
        super.onResume();
        String currentDateString = DateFormat.getDateInstance().format(new Date());
        String currentTimeString = DateFormat.getTimeInstance().format(new Date());
        EditText datum=(EditText)findViewById(R.id.dateEdit);
        datum.setText(currentDateString, TextView.BufferType.EDITABLE);
        EditText zeit=(EditText)findViewById(R.id.timeEdit);
        zeit.setText(currentTimeString, TextView.BufferType.EDITABLE);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_grunddaten:
                // User chose the "Settings" item, show the app settings UI...
                //Toast.makeText(this, "Grunddaten", Toast.LENGTH_LONG).show();
                Intent intent=new Intent(this, Grunddaten.class);
                startActivity(intent);
                return true;

            case R.id.action_sync:
                SharedPreferences sharedPref=getApplicationContext().getSharedPreferences(getString(R.string.PREF_KEY_FILE), Context.MODE_PRIVATE);
                String name=sharedPref.getString(getString(R.string.full_name),"");
                String username=sharedPref.getString(getString(R.string.user_name),"");
                String gebtag=sharedPref.getString(getString(R.string.date_of_birth), "");
                String password=sharedPref.getString(getString(R.string.password),"");
                String srv=sharedPref.getString(getString(R.string.srv_name),"");
                SendBzData sendBzData=new SendBzData();
                sendBzData.execute(name, username, gebtag, password, srv);
                return true;
            case R.id.action_bzliste:
                Intent bzIntent=new Intent(this, BzListe.class);
                startActivity(bzIntent);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public class SendBzData extends AsyncTask<String, Integer, String[]> {

        private final String LOG_TAG=SendBzData.class.getSimpleName();

        @Override
        protected String[] doInBackground(String... strings) {

            Toast.makeText(getApplicationContext(), strings[0]+":"+strings[1]+":"+strings[2]+":"+strings[3]+":"+strings[4], Toast.LENGTH_LONG).show();
            String[] ergebnisArray=new String[20];
            for(int i=0;i<ergebnisArray.length; i++){
                ergebnisArray[i]=strings[0]+"_"+(i+1);
                if(i%5==4){
                    publishProgress(i+1, 20);
                }
                try{
                    Thread.sleep(600);
                } catch(Exception e){ Log.e(LOG_TAG, "Error ", e);}
            }
            return ergebnisArray;
        }

        @Override
        protected void onProgressUpdate(Integer... values){
            Toast.makeText(getApplicationContext(), values[0]+" von "+values[1]+" geladen", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(String[] strings){
            Toast.makeText(getApplicationContext(), "Daten gesendet", Toast.LENGTH_SHORT).show();
        }
    }
}
