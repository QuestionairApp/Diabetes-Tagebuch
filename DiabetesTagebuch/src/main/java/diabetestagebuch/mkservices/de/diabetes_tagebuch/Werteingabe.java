package diabetestagebuch.mkservices.de.diabetes_tagebuch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.ArrayList;
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
                String bz=sharedPref.getString(getString(R.string.werte_array),"");
                SendBzData sendBzData=new SendBzData();
                sendBzData.execute(name, username, gebtag, password, srv, bz);
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

    public class SendBzData extends AsyncTask<String, Integer, String> {

        private final String LOG_TAG=SendBzData.class.getSimpleName();

        @Override
        protected String doInBackground(String... strings) {
            String ergebnis="something went wrong";
            InputStream is=null;
            String urlStr=strings[4];
            String retVal=null;

            String pwHash=getSHA256Hash(strings[3]);
            try {
                URL url = new URL(urlStr);
                HttpURLConnection cc = (HttpURLConnection) url.openConnection();
                cc.setReadTimeout(5000);
                cc.setConnectTimeout(5000);
                cc.setRequestMethod("POST");
                cc.setDoInput(true);
                cc.connect();

                DataOutputStream dos=new DataOutputStream(cc.getOutputStream());
                StringBuilder params=new StringBuilder();
                params.append("name=");
                params.append(strings[0]);
                params.append("&username=");
                params.append(strings[1]);
                params.append("&gebtag=");
                params.append(strings[2]);
                params.append("&passwd=");
                params.append(pwHash);
                params.append("&werte=");
                params.append(strings[5]);

                dos.writeBytes(params.toString());
                dos.flush();
                dos.close();

                int response=cc.getResponseCode();
                if(response==HttpURLConnection.HTTP_OK){
                    is=cc.getInputStream();
                    InputStreamReader isr=new InputStreamReader(is);
                    BufferedReader reader=new BufferedReader(isr);
                    StringBuilder sb=new StringBuilder();

                    String line=null;
                    try{
                        while((line=reader.readLine())!=null){
                            sb.append(line);
                        }
                        ergebnis=sb.toString();
                    } catch(IOException e){

                    } catch(Exception e){

                    } finally {
                        try {
                            is.close();
                        } catch(IOException e){}
                    }
                }

            } catch(Exception e){
                Log.d(LOG_TAG, "Something went wrong");
            }
            return ergebnis;
        }

        @Override
        protected void onProgressUpdate(Integer... values){
            Toast.makeText(getApplicationContext(), values[0]+" von "+values[1]+" geladen", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(String strings){
            if(strings.equalsIgnoreCase("OK")){
                SharedPreferences sharedPrefs=getApplicationContext().getSharedPreferences(getString(R.string.PREF_KEY_FILE), Context.MODE_PRIVATE);
                SharedPreferences.Editor edit=sharedPrefs.edit();
                edit.putString(getString(R.string.werte_array),"");
                edit.apply();;
            }
            Toast.makeText(getApplicationContext(), strings, Toast.LENGTH_SHORT).show();
        }

        private String getSHA256Hash(String text){
            String hash=null;
            MessageDigest md=null;

            try{
                md=MessageDigest.getInstance("SHA-256");
                md.update(text.getBytes("UTF-8"));
                byte[] shaDig=md.digest();

                hash= Base64.encodeToString(shaDig, Base64.DEFAULT);

            } catch(NoSuchAlgorithmException ex){
                Log.d(LOG_TAG, ex.getMessage());
            } catch (UnsupportedEncodingException e){
                Log.d(LOG_TAG, e.getMessage());
            }
            return hash;
        }
    }
}
