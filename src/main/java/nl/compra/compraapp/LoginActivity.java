package nl.compra.compraapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;

public class LoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate (Bundle savedInstanceState) {

        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_login);

        TextView tvNa = (TextView) findViewById (R.id.newAccountText);
        tvNa.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {

                newAccount (v);

            }
        });

        TextView tvFp = (TextView) findViewById (R.id.forgotPasswordText);
        tvFp.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {

                forgotPassword (v);

            }
        });

        Button loginButton = (Button) findViewById (R.id.loginButton);
        loginButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {

                login (v);

            }
        });

    }

    private void notifyUser (String message)
    {

        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show ();

    }

    public void login (View view)
    {

        Log.d ("Bob", "LOGIN BOOTAAAN");

        EditText emailEditText      = (EditText) findViewById (R.id.emailEditText);
        EditText passwordEditText   = (EditText) findViewById (R.id.passwordEditText);

        String emailInput       = emailEditText.getText ().toString ();
        String passwordInput    = passwordEditText.getText ().toString ();

        new LoginTask ().execute (emailInput, passwordInput);

    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.menu_login, menu);
        return true;
    }

    public void newAccount (View view)
    {

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse ("https://www.compra.nl/nieuw-account"));
        startActivity (browserIntent);

    }

    public void forgotPassword (View view)
    {

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse ("https://www.compra.nl/wachtwoord-vergeten"));
        startActivity (browserIntent);

    }

    private void toastUser (String message, final int length)
    {

        Context context = getApplicationContext ();
        int duration = length;

        Toast toast = Toast.makeText (context, message, duration); // < Ignore this error, it's not an error.

        toast.show ();

    }

    private void loginAftermath () {

        if (UserManager.getCurrentlySignedInUser () == null)
        {

            notifyUser ("Foute inlog gegevens");

        }
        else
        {

            toastUser ("U bent successvol ingelogd", Toast.LENGTH_SHORT);
            this.finish ();

        }

    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId ();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected (item);
    }

    private String getUrlSource (String url) throws IOException {

        // This code was brutallity ripped from the intarwurbz

        URL custUrl = null;
        try {
            custUrl = new URL (url);
        } catch (MalformedURLException e) {
            e.printStackTrace ();
        }
        URLConnection yc = custUrl.openConnection ();
        BufferedReader in = new BufferedReader (new InputStreamReader (yc.getInputStream (), "UTF-8"));
        String inputLine;
        StringBuilder a = new StringBuilder ();
        while ((inputLine = in.readLine ()) != null)
            a.append (inputLine);
        in.close ();

        return a.toString ();

    }

    public class LoginTask extends AsyncTask<String, String, String> {

        private String url;
        private String jsonBuffer;

        @Override
        protected String doInBackground (String... params) {

            url = "https://www.compra.nl/?c=api&m=login&email=" + params[0] + "&password=" + params[1];

            Log.d ("Bob", "Attempted login");

            try {

                jsonBuffer = getUrlSource (url);

                if (jsonBuffer.toString ().equals ("false"))
                {

                    Log.d ("Bob", "False login");

                }
                else
                {

                    JSONObject jsonObject = null;
                    try {

                        jsonObject = new JSONObject (jsonBuffer);

                        int id = jsonObject.getInt ("id");
                        String firstname = jsonObject.getString ("firstname");
                        String lastname = jsonObject.getString ("lastname");
                        String email = jsonObject.getString ("email");

                        User user = new User (id, firstname, lastname, email);

                        UserManager.setCurrentlySignedInUser (user);

                        Log.d ("Bob", "True login");

                    } catch (JSONException e) {

                        Log.d ("Bob", "Shit has indefinitely hit the fan but that doesn't matter because we USE the error :D.");

                        e.printStackTrace ();
                    }

                }


            } catch (IOException e) {

                Log.d ("Bob", "jsonBuffer wilt niet getUrlSource() doen.");
                e.printStackTrace ();

            }



            return null;

        }

        @Override
        protected void onPostExecute (String string)
        {

            loginAftermath ();

        }

    }


}
