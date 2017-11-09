package diabetestagebuch.mkservices.de.diabetes_tagebuch;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Grunddaten extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grunddaten);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Button btn=(Button)findViewById(R.id.savePrefBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText srvEdit=(EditText)findViewById(R.id.srvPrefEdit);
                EditText fullNameEdit=(EditText)findViewById(R.id.namePrefEdit);
                EditText dateEdit=(EditText)findViewById(R.id.datePrefEdit);
                EditText userNameEdit=(EditText)findViewById(R.id.usernamePrefEdit);
                EditText passEdit=(EditText)findViewById(R.id.pwPrefEdit);
                String fullName=String.valueOf(fullNameEdit.getText()).trim();
                String userName=String.valueOf(userNameEdit.getText()).trim();
                String dob=String.valueOf(dateEdit.getText()).trim();
                String passWord=String.valueOf(passEdit.getText()).trim();
                String srv=String.valueOf(srvEdit.getText()).trim();
                Context ctx=getApplicationContext();
                SharedPreferences sharedPref=ctx.getSharedPreferences(getString(R.string.PREF_KEY_FILE), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPref.edit();
                editor.putString(getString(R.string.full_name),fullName);
                editor.putString(getString(R.string.user_name), userName);
                editor.putString(getString(R.string.date_of_birth),dob);
                editor.putString(getString(R.string.password), passWord);
                editor.putString(getString(R.string.srv_name), srv);
                editor.apply();
                Toast.makeText(ctx, "Einstellungen gespeichert", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        EditText fullNameEdit=(EditText)findViewById(R.id.namePrefEdit);
        EditText userNameEdit=(EditText)findViewById(R.id.usernamePrefEdit);
        EditText dobEdit=(EditText)findViewById(R.id.datePrefEdit);
        EditText pwEdit=(EditText)findViewById(R.id.pwPrefEdit);
        EditText srvEdit=(EditText)findViewById(R.id.srvPrefEdit);
        Context ctx=this.getApplicationContext();
        SharedPreferences sharedPref=ctx.getSharedPreferences(getString(R.string.PREF_KEY_FILE), Context.MODE_PRIVATE);
        String fullName=sharedPref.getString(getString(R.string.full_name),"");
        String userName=sharedPref.getString(getString(R.string.user_name), "");
        String dob=sharedPref.getString(getString(R.string.date_of_birth), "");
        String password=sharedPref.getString(getString(R.string.password),"");
        String srv=sharedPref.getString(getString(R.string.srv_name),"");

        fullNameEdit.setText(fullName, TextView.BufferType.EDITABLE);
        userNameEdit.setText(userName, TextView.BufferType.EDITABLE);
        dobEdit.setText(dob, TextView.BufferType.EDITABLE);
        pwEdit.setText(password, TextView.BufferType.EDITABLE);
        srvEdit.setText(srv, TextView.BufferType.EDITABLE);
    }

}
