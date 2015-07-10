package nl.compra.compraapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.StrikethroughSpan;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    private static final StrikethroughSpan STRIKE_THROUGH_SPAN = new StrikethroughSpan ();
    private static final int MAX_AMOUNT_OF_DOMAINS = 20;

    private List<Extension> applicationExtensions;
    private String domainSearchedFor;
    private boolean domainSearchedForAvailabillity;

    public MainActivity () {

        applicationExtensions = new ArrayList<Extension> ();

    }

    public void intializeExtensions () {

        Log.d ("Bob", "domainzzzzzzzzzz");

        if (!applicationExtensions.isEmpty ()) {

            // Iterates through all found domains
            Iterator<Extension> extensionListIterator = applicationExtensions.iterator ();
            while (extensionListIterator.hasNext ()) {

                // Get the next iteration
                Extension extensionIt = extensionListIterator.next ();

                LayoutInflater layoutInflater;
                layoutInflater = (LayoutInflater) getSystemService (Context.LAYOUT_INFLATER_SERVICE);

                // Add a new domain_row
                View newRow = layoutInflater.inflate (R.layout.domain_row, null);

                // Attempt to change the text of the domain_row layout to the corresponding domain
                TextView domeinRowText = (TextView) newRow.findViewById (R.id.domeinRowText);
                domeinRowText.setText (extensionIt.getTld ());

                Button domeinPriceButton = (Button) newRow.findViewById (R.id.domeinRowOrderButton);
                String euroSign = "\u20ac";

                Long now = System.currentTimeMillis ();
                Date dateOfToday = new Date (now);

                Date domainOfferDateBegin = null;
                Date domainOfferDateEnd = null;

                SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd");
                try {
                    domainOfferDateBegin = sdf.parse (extensionIt.getSpecialOfferDateBegin ());
                    domainOfferDateEnd = sdf.parse (extensionIt.getSpecialOfferDateEnd ());
                } catch (ParseException e) {
                    e.printStackTrace ();
                }

                if (dateOfToday.after (domainOfferDateBegin) && dateOfToday.before (domainOfferDateEnd)) {

                    Log.d ("Bob", "THE DOMAIN " + extensionIt.getTld () + " IS ON SAAAAAAALE #STEAMSALE #PRAISEGABEN the new price is " + extensionIt.getSpecialPrice ());

                    String originalPrice = euroSign + " " + extensionIt.getPricePerYear () + "0";
                    String salePrice = euroSign + " " + extensionIt.getSpecialPrice () + "0";

                    domeinPriceButton.setText (salePrice);
//                    domeinPriceButton.setText (originalPrice + " " + salePrice, TextView.BufferType.SPANNABLE);

//                    Spannable spannable = (Spannable) domeinPriceButton.getText();
//                    spannable.setSpan (STRIKE_THROUGH_SPAN, 0, originalPrice.length (), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

//                    domeinPriceButton.setPaintFlags (domeinPriceButton.getPaintFlags () | Paint.STRIKE_THRU_TEXT_FLAG); // this works but it strikes all through

                } else {

                    domeinPriceButton.setText (euroSign + " " + extensionIt.getPricePerYear () + "0");

                }

                TableLayout table = (TableLayout) findViewById (R.id.domainRowsTable);
                table.addView (newRow);

            }

        } else {

            Log.d ("Bob", "domainList is very empty.");

        }

    }

    public void searchByDomain (View view) {


        clearDomains ();

        EditText searchField = (EditText) findViewById (R.id.searchEditText);
        String domain = searchField.getText ().toString ();

        domainSearchedFor = domain;

        Log.d ("Bob", "User is searching by the domain: " + domainSearchedFor);
        new CheckIfDomainAvailable ().execute (domain);

    }

    private void clearDomains () {

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

        final EditText searchEditText = (EditText) findViewById (R.id.searchEditText);

        searchEditText.addTextChangedListener (new TextWatcher () {

            @Override
            public void beforeTextChanged (CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged (CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged (Editable s) {

                if (searchEditText.getText ().toString ().equals ("")) {

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

        toast.show ();

        // Creates the spinners

        Spinner spinnerDomeinen = (Spinner) findViewById (R.id.domeinen);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource (this, R.array.catagories, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerDomeinen.setAdapter (adapter);

        Spinner spinnerSorteringen = (Spinner) findViewById (R.id.sorteringen);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterrrrrrrrrrr = ArrayAdapter.createFromResource (this, R.array.sort, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterrrrrrrrrrr.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerSorteringen.setAdapter (adapterrrrrrrrrrr);

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

    private void reinitializeExtensionsWithFoundDomain ()
    {

        clearDomains ();

        if (!applicationExtensions.isEmpty () && domainSearchedFor != null) {

            // Iterates through all found domains
            Iterator<Extension> extensionListIterator = applicationExtensions.iterator ();
            while (extensionListIterator.hasNext ()) {

                // Get the next iteration
                Extension extensionIt = extensionListIterator.next ();

                LayoutInflater layoutInflater;
                layoutInflater = (LayoutInflater) getSystemService (Context.LAYOUT_INFLATER_SERVICE);

                // Add a new domain_row
                View newRow = layoutInflater.inflate (R.layout.domain_row, null);

                // Attempt to change the text of the domain_row layout to the corresponding domain
                TextView domeinRowText = (TextView) newRow.findViewById (R.id.domeinRowText);
                domeinRowText.setText ((domainSearchedFor.substring (0, domainSearchedFor.indexOf ("."))) + "." + extensionIt.getTld ());


                Button domainPriceButton = (Button) newRow.findViewById (R.id.domeinRowOrderButton);
                String euroSign = "\u20ac";

                Long now = System.currentTimeMillis ();
                Date dateOfToday = new Date (now);

                Date domainOfferDateBegin = null;
                Date domainOfferDateEnd = null;

                SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd");
                try {
                    domainOfferDateBegin = sdf.parse (extensionIt.getSpecialOfferDateBegin ());
                    domainOfferDateEnd = sdf.parse (extensionIt.getSpecialOfferDateEnd ());
                } catch (ParseException e) {
                    e.printStackTrace ();
                }

                if (extensionIt.isAvailable ())
                {

                    if (dateOfToday.after (domainOfferDateBegin) && dateOfToday.before (domainOfferDateEnd)) {

                        Log.d ("Bob", "THE DOMAIN " + extensionIt.getTld () + " IS ON SAAAAAAALE #STEAMSALE #PRAISEGABEN the new price is " + extensionIt.getSpecialPrice ());

                        String originalPrice = euroSign + " " + extensionIt.getPricePerYear () + "0";
                        String salePrice = euroSign + " " + extensionIt.getSpecialPrice () + "0";

                        domainPriceButton.setText (salePrice);
    //                    domainPriceButton.setText (originalPrice + " " + salePrice, TextView.BufferType.SPANNABLE);

    //                    Spannable spannable = (Spannable) domainPriceButton.getText();
    //                    spannable.setSpan (STRIKE_THROUGH_SPAN, 0, originalPrice.length (), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

    //                    domainPriceButton.setPaintFlags (domainPriceButton.getPaintFlags () | Paint.STRIKE_THRU_TEXT_FLAG); // this works but it strikes all through

                    } else {

                        domainPriceButton.setText (euroSign + " " + extensionIt.getPricePerYear () + "0");

                    }

                }
                else
                {

                    domainPriceButton.setText ("Bezet");

                }


                TableLayout table = (TableLayout) findViewById (R.id.domainRowsTable);
                table.addView (newRow);

            }

        } else {

            Log.d ("Bob", "domainList is very empty.");

        }

        domainSearchedFor = null;

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

                Log.d ("Bob", "jsonBuffer wilt niet getUrlSource() doen.");
                e.printStackTrace ();

            }

            JSONObject jsonObject = null;
            try {

                jsonObject = new JSONObject (jsonShit);
                JSONArray jsonArray = jsonObject.getJSONArray ("items");


                // Exists for debugging purposes
//                Log.d ("Bob", "Ik ga nu gezellig de hele jsonarray af");
//                for (int i = 0; i < jsonArray.length (); i++)
//                {
//
//                    Log.d ("Bob", jsonArray.get (i).toString ());
//
//                }

                List<Extension> localExtensionList;
                localExtensionList = new ArrayList<Extension> ();
//                for (int i = 0; i < jsonArray.length (); i++) {
                for (int i = 0; i < MAX_AMOUNT_OF_DOMAINS; i++) {

                    // Create the JSON data object
                    JSONObject domainObj = jsonArray.getJSONObject (i);

                    int id = domainObj.getInt ("id");
                    String tld = domainObj.getString ("tld");
                    double pricePerYear = domainObj.getDouble ("price_per_year");
                    int popular = domainObj.getInt ("popular");
                    int newDomain = domainObj.getInt ("new");
                    String region = domainObj.getString ("region");
                    String restriction = domainObj.getString ("restriction");
                    String specialOfferDateBegin = domainObj.getString ("special_offer_begin");
                    String specialOfferDateEnd = domainObj.getString ("special_offer_end");
                    double specialPrice = domainObj.getDouble ("special_offer_price");

                    localExtensionList.add (new Extension (id, tld, pricePerYear, popular, newDomain, region, restriction, specialOfferDateBegin, specialOfferDateEnd, specialPrice));

//                    localExtensionList.put (domain, price);

                }

                Log.d ("Bob", "ExtensionInitializer successfully completed the domainList Map.");
                applicationExtensions = localExtensionList;

            } catch (JSONException e) {

                Log.d ("Bob", "COMPRA API ADAPTER FAILED");
                e.printStackTrace ();

            }

            return "Executed";

        }

        @Override
        protected void onPostExecute (String string) {

            Log.d ("Bob", "Extensions have been initialized");

            intializeExtensions ();

        }

    }

    /*
        This is unused and I don't really know if CheckIfDomainAvailable replaced it.
        But to be safe I kinda just left it in.
     */
    public class ExtensionSearchOnDomain extends AsyncTask<String, String, String> {

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

                List<Extension> localExtensionList;
                localExtensionList = new ArrayList<Extension> ();
                for (int i = 0; i < jsonArray.length (); i++) {

                    // Create the JSON data object
                    JSONObject domainObj = jsonArray.getJSONObject (i);

                    int id = domainObj.getInt ("id");
                    String tld = domainObj.getString ("tld");
                    double pricePerYear = domainObj.getDouble ("price_per_year");
                    int popular = domainObj.getInt ("popular");
                    int newDomain = domainObj.getInt ("new");
                    String region = domainObj.getString ("region");
                    String restriction = domainObj.getString ("restriction");
                    String specialOfferDateBegin = domainObj.getString ("special_offer_begin");
                    String specialOfferDateEnd = domainObj.getString ("special_offer_end");
                    int specialPrice = domainObj.getInt ("special_offer_price");

                    localExtensionList.add (new Extension (id, tld, pricePerYear, popular, newDomain, region, restriction, specialOfferDateBegin, specialOfferDateEnd, specialPrice));


                }

                Log.d ("Bob", "ExtensionSearchOnDomain successfully executed");
                applicationExtensions = localExtensionList;

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

    public class CheckIfDomainAvailable extends AsyncTask<String, String, String> {

        private String url = "https://www.compra.nl/?c=api&m=checkDomain&domein=";
        private String jsonBuffer;

        @Override
        protected String doInBackground (String... params) {

            try {

                jsonBuffer = getUrlSource (url + params[0]);

            } catch (IOException e) {

                Log.d ("Bob", "jsonBuffer wilt niet getUrlSource() doen.");
                e.printStackTrace ();

            }

            JSONObject jsonObject = null;
            try {

                jsonObject = new JSONObject (jsonBuffer);

                String availabillityCode = jsonObject.getString ("code");
                domainSearchedForAvailabillity = availabillityCode.equals ("210") ? true : false;

                String domainWithExtension = params[0];

                // Remove the extension
                String domainWithoutExtension = domainWithExtension.substring (0, domainWithExtension.indexOf ("."));

                Log.d ("Bob", "domainWithoutExtension value:");
                Log.d ("Bob", domainWithoutExtension);

                int timesRan = 0;

                Iterator <Extension> extensionIterator = applicationExtensions.iterator ();
                while (extensionIterator.hasNext () && timesRan <= MAX_AMOUNT_OF_DOMAINS)
                {

                    timesRan++;

                    Extension extensionIt = extensionIterator.next ();

                    try {

                        jsonBuffer = getUrlSource (url + domainWithoutExtension + extensionIt.getTld ());
                        jsonObject = new JSONObject (jsonBuffer);
                        boolean availabillity = jsonObject.getString ("code").equals ("210") ? true : false;
                        extensionIt.setAvailable (availabillity);

                        Log.d ("Bob", "the domain " + domainWithoutExtension + " with extension " + extensionIt.getTld () + " has code " + jsonObject.getString ("code") + "!");

                    } catch (IOException e) {

                        Log.d ("Bob", "jsonBuffer wilt niet getUrlSource() doen.");
                        e.printStackTrace ();

                    }

                }

            } catch (JSONException e) {

                Log.d ("Bob", "COMPRA API ADAPTER FAILED");
                e.printStackTrace ();

            }

            return null;

        }

        @Override
        protected void onPostExecute (String string)
        {

            reinitializeExtensionsWithFoundDomain ();

        }

    }

}
