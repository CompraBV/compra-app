package nl.compra.compraapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends ActionBarActivity
{

    public Map <String, Double> applicationDomainList;

    public MainActivity ()
    {

        applicationDomainList = new HashMap <String, Double> ();

    }

    public void initializeDomains ()
    {

        Log.d ("Bob", "domainzzzzzzzzzz");

        if ( ! applicationDomainList.isEmpty ()) {

            // Iterates through all found domains
            Iterator<Map.Entry<String, Double>> domainListIterator = applicationDomainList.entrySet ().iterator ();
            while (domainListIterator.hasNext ()) {

                // Get the next entry
                Map.Entry domainListIt = domainListIterator.next ();

                LayoutInflater layoutInflater;
                layoutInflater = (LayoutInflater) getSystemService (Context.LAYOUT_INFLATER_SERVICE);

                // Add a new domain_row
                View newRow = layoutInflater.inflate (R.layout.domain_row, null);

                // Attempt to change the text of the domain_row layout to the corresponding domain
                TextView domeinRowText = (TextView) newRow.findViewById (R.id.domeinRowText);
                domeinRowText.setText (domainListIt.getKey ().toString ());

                Button domeinPriceButton = (Button) newRow.findViewById (R.id.domeinRowOrderButton);
                String euroSign = "\u20ac";
                domeinPriceButton.setText (euroSign + " " + domainListIt.getValue ().toString ());

                TableLayout table = (TableLayout) findViewById (R.id.domainRowsTable);
                table.addView (newRow);

            }

        } else {

            Log.d ("Bob", "domainList is very empty.");

        }

    }

    public void searchByDomain (View view)
    {

        Log.d ("Bob", "User is searching by domain.");

        clearDomains ();

        EditText searchField = (EditText) findViewById (R.id.searchEditText);
        String domain = searchField.getText ().toString ();

        new ExtensionSearchOnDomain ().execute (domain);

    }

    private void clearDomains ()
    {

        Log.d ("Bob", "User attempted to clear all table rows.");

        TableLayout tl = (TableLayout) findViewById (R.id.domainRowsTable);
        tl.removeAllViews ();

    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {

        super.onCreate (savedInstanceState);

        setContentView (R.layout.activity_main);


        //////////////////////////////////////////////////////////////
        // Set listeners here that the layout can't for some reason //
        //////////////////////////////////////////////////////////////

        Button b = (Button) findViewById (R.id.domainSearchButton);
        b.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {

                searchByDomain (v);

            }
        });

        final EditText searchEditText = (EditText) findViewById(R.id.searchEditText);

        searchEditText.addTextChangedListener(new TextWatcher () {

            @Override
            public void beforeTextChanged (CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged (CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged (Editable s) {

                if (searchEditText.getText ().toString ().equals (""))
                {

                    new ExtensionInitializer ().execute ();

                }

            }

        });

            // Start CompraApiAdapter thread to load all domains
            new ExtensionInitializer ().execute ();

            Context context = getApplicationContext ();
            CharSequence text = "Welcome back to the Compra App!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText (context, text, duration);

            toast.show();

            // Creates the spinners

            Spinner spinnerDomeinen = (Spinner) findViewById (R.id.domeinen);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource (this, R.array.catagories, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinnerDomeinen.setAdapter(adapter);

            Spinner spinnerSorteringen = (Spinner) findViewById (R.id.sorteringen);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapterrrrrrrrrrr = ArrayAdapter.createFromResource (this, R.array.sort, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapterrrrrrrrrrr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinnerSorteringen.setAdapter(adapterrrrrrrrrrr);

        }

        @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.menu_main, menu);
        return true;
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

        if (id == R.id.action_login) {


        }


        return super.onOptionsItemSelected (item);
    }

    // This code was copied from the internet.
    // Source: http://stackoverflow.com/questions/8616781/how-to-get-a-web-pages-source-code-from-java
    private String getUrlSource (String url) throws IOException {

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

    public class ExtensionInitializer extends AsyncTask<String, String, String> {

        private final String URL = "https://www.compra.nl/?c=api&m=getExtensions";
        private String jsonShit;

        @Override
        protected String doInBackground (String... params) {

            Log.d ("Bob", "Hello this is the ExtensionInitializer.");

            try {

                jsonShit = getUrlSource (URL);

            } catch (IOException e) {

                Log.d ("Bob", "jsonShit wilt niet getUrlSource() doen.");
                e.printStackTrace ();

            }

            JSONObject jsonObject = null;
            try {

                jsonObject = new JSONObject (jsonShit);
                JSONArray jsonArray = jsonObject.getJSONArray ("items");

                Map<String, Double> localDomainList;
                localDomainList = new HashMap<String, Double> ();
                for (int i = 0; i < jsonArray.length (); i++) {

                    // Create the JSON data object
                    JSONObject domainObj = jsonArray.getJSONObject (i);
                    String domain = domainObj.getString ("tld");
                    double price = domainObj.getDouble ("price_per_year");

                    localDomainList.put (domain, price);

                }

                Log.d ("Bob", "ExtensionInitializer successfully completed the domainList Map.");
                applicationDomainList = localDomainList;

            } catch (JSONException e) {

                Log.d ("Bob", "COMPRA API ADAPTER FAILED");
                e.printStackTrace ();

            }

            return "Executed";

        }

        @Override
        protected void onPostExecute (String string) {

            Log.d ("Bob", "Domains have been initialized");

            initializeDomains ();

        }

    }

    public class ExtensionSearchOnDomain extends AsyncTask<String, String, String>
    {

        private String url;
        private String jsonShit;

        @Override
        protected String doInBackground (String... params) {

            try {

                String actualDomein = params[0];

                url = "https://www.compra.nl/?c=api&m=checkDomain&domein=" + actualDomein;
                jsonShit = getUrlSource (url);


            } catch (IOException e) {

                e.printStackTrace ();

            }

            JSONObject jsonObject = null;
            try {

                jsonObject = new JSONObject (jsonShit);
                JSONArray jsonArray = jsonObject.getJSONArray ("items");

                Map<String, Double> localDomainList;
                localDomainList = new HashMap<String, Double> ();
                for (int i = 0; i < jsonArray.length (); i++) {

                    // Create the JSON data object
                    JSONObject domainObj = jsonArray.getJSONObject (i);
                    String domain = domainObj.getString ("tld");
                    double price = domainObj.getDouble ("price_per_year");

                    localDomainList.put (domain, price);

                }

                Log.d ("Bob", "ExtensionSearchOnDomain successfully executed");
                applicationDomainList = localDomainList;

            } catch (JSONException e) {

                Log.d ("Bob", "ExtensionSearchOnDomain FAILED");
                e.printStackTrace ();

            }

            return null;

        }

        @Override
        protected void onPostExecute (String string) {

            Log.d ("Bob", "Doe ik iets of ben ik meuilijk lui?");



        }

    }

}
