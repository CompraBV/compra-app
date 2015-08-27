package nl.compra.compraapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
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

public class MainActivity extends ActionBarActivity implements PopupMenu.OnMenuItemClickListener {

    private static final StrikethroughSpan  STRIKE_THROUGH_SPAN         = new StrikethroughSpan ();
    private static final int                AMOUNT_OF_DOMAINS_PER_BATCH = 10;
    private static final String             DEFAULT_EXTENSION           = "com";

    public  static  Domain                  domainSearchedFor;
    private static  List<Extension>        applicationExtensions;
    private static  ExtensionFilterType     domainFilter;
    private static  ExtensionSortingType    extensionSorter;
    private         String                  literalDomainSearchedFor;
    private         boolean                 domainSearchedForAvailabillity;
    private         int                     rowCount;

    public static ExtensionFilterType   getExtensionFilterType ()   { return domainFilter; }
    public static ExtensionSortingType  getExtensionSortingType ()  { return extensionSorter; }

    public MainActivity () {

        // THIS EXISTS FOR TESTING PURPOSES
        // TODO remove this when done testing
        UserManager.setCurrentlySignedInUser (new User (11285, "Nathan", "Bastiaans", "n.bastiaans@compra.nl"));

        // Default filter for all domains
        domainFilter    = ExtensionFilterType.ALL;
        extensionSorter = ExtensionSortingType.NONE;

        applicationExtensions = new ArrayList<Extension> ();

        rowCount = 0;

    }

    public void updateFilter (ExtensionFilterType filter)
    {

        domainFilter = filter;
        new UpdateExtensions ().execute ();

    }

    public void updateSorter (ExtensionSortingType sorter)
    {

        extensionSorter = sorter;
        new UpdateExtensions ().execute ();

    }

    public void initializeExtensions () {

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View newRow;

        // Now do the standard list
        if ( ! applicationExtensions.isEmpty ()) {

            // Iterates through all found domains
            Iterator<Extension> extensionListIterator = applicationExtensions.iterator ();
            while (extensionListIterator.hasNext ()) {

                // Get the next iteration
                Extension extensionIt = extensionListIterator.next ();

//                LayoutInflater layoutInflater;
//                layoutInflater = (LayoutInflater) getSystemService (Context.LAYOUT_INFLATER_SERVICE);

                // Add a new domain_row
                newRow = layoutInflater.inflate (R.layout.domain_row, null);

                // Attempt to change the text of the domain_row layout to the corresponding domain
                TextView domeinRowText = (TextView) newRow.findViewById (R.id.domeinRowText);
                domeinRowText.setText (extensionIt.getTld ());

                Button domeinPriceButton = (Button) newRow.findViewById (R.id.loadMoreExtensionsButton);
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

                appendToTable (newRow);

            }

        } else {

            Log.d ("Bob", "domainList is very empty.");

        }

    }

    public void searchByDomain (View view) {


        EditText searchField = (EditText) findViewById (R.id.searchEditText);
        String domain = searchField.getText ().toString ();

        if ( ! domain.contains ("."))
            domain += "." + DEFAULT_EXTENSION;

            clearDomains ();
            literalDomainSearchedFor = domain;

            Log.d ("Bob", "User is searching by the domain: " + literalDomainSearchedFor);
            new CheckIfDomainAvailable ().execute (domain);


    }

    private void clearDomains () {

        Log.d ("Bob", "User attempted to clear all table rows.");

        TableLayout tl = (TableLayout) findViewById (R.id.domainRowsTable);
        tl.removeAllViews ();
        rowCount = 0;

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

    public void triggerLoginActivity (View view)
    {

        if (UserManager.getCurrentlySignedInUser () instanceof User)
        {

            Intent accountIntent = new Intent (this, AccountActivity.class);
            startActivity (accountIntent);

        } else {

            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity (loginIntent);

        }


    }

    private void killTheLastChildHidingUnderTheTable ()
    {

        rowCount--;
        TableLayout table = (TableLayout) findViewById (R.id.domainRowsTable);
        table.removeViewAt (getAmountOfChildrenHidingUnderTheTable () - 1);

    }

    private int getAmountOfChildrenHidingUnderTheTable ()
    {

        TableLayout table = (TableLayout) findViewById (R.id.domainRowsTable);
        return table.getChildCount ();

    }

    private void appendToTable (View newRow)
    {

        rowCount++;
        TableLayout table = (TableLayout) findViewById (R.id.domainRowsTable);
        table.addView (newRow);

    }

    private void menuButtonTrigger (View v) {

        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener (this);
        inflater.inflate(R.menu.menu, popup.getMenu());
        popup.show ();

    }

    private void cartButtonTrigger (View v)
    {

        Intent intent = new Intent (this, CartActivity.class);
        startActivity (intent);

    }

//    @Override
    public boolean onMenuItemClick(MenuItem item)
    {

        switch (item.getItemId())
        {

            case R.id.optionContact:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse ("http://www.compra.nl/contact"));
                startActivity(browserIntent);
                return true;

            case R.id.optionOfferte:
                Intent browserIntent2 = new Intent(Intent.ACTION_VIEW, Uri.parse ("http://www.compra.nl/#offerte"));
                startActivity(browserIntent2);
                return true;

            default:
                return false;

        }

    }

    private void toastUser (String message, final int length)
    {

        Context context = getApplicationContext ();
        int duration = length;

        Toast toast = Toast.makeText (context, message, duration); // < Ignore this error, it's not an error.

        toast.show ();

    }

    @Override
    protected void onStart ()
    {

        Spinner spinnerDomeinen = (Spinner) findViewById (R.id.domeinen);
        spinnerDomeinen.setSelection (0, false); // This prevents the spinner from firing it's listeners on start up.
        spinnerDomeinen.setOnItemSelectedListener (new AdapterView.OnItemSelectedListener () {
            @Override
            public void onItemSelected (AdapterView<?> parent, View view, int position, long id) {

                switch (position)
                {

                    default:
                        notifyUser ("Something went horribly wrong. Please contact Compra at info@compra.nl");
                        Log.d ("Bob", "DEFAULT CASE TRIGGERED IN SWITCH FOR SPINNER POSITION. SHEEEEIIIT.");
                        break;

                    case 0:
                        updateFilter (ExtensionFilterType.ALL);
                        break;

                    case 1:
                        updateFilter (ExtensionFilterType.POPULAR);
                        break;

                    case 2:
                        updateFilter (ExtensionFilterType.NEW);
                        break;

                    case 3:
                        updateFilter (ExtensionFilterType.EUROPE);
                        break;

                    case 4:
                        updateFilter (ExtensionFilterType.COUNTRIES);
                        break;

                    case 5:
                        updateFilter (ExtensionFilterType.COMMON);
                        break;

                    case 6:
                        updateFilter (ExtensionFilterType.SPECIAL_OFFERS);
                        break;

                }

            }

            @Override
            public void onNothingSelected (AdapterView<?> parent)
            {

                toastUser ("I'm a fluffy little kitty cat.", Toast.LENGTH_SHORT);

            }

        });

        Spinner spinnerSorteringen = (Spinner) findViewById (R.id.sorteringen);
        spinnerSorteringen.setSelection (0, false); // This prevents the spinner from firing it's listeners on start up.
        spinnerSorteringen.setOnItemSelectedListener (new AdapterView.OnItemSelectedListener ()
        {

            @Override
            public void onItemSelected (AdapterView<?> parent, View view, int position, long id) {

                Log.d ("Bob", "USER CHANGED THE SORTER");

                switch (position)
                {

                    case 0:
                        updateSorter (ExtensionSortingType.PRICE_ASCENDING);
                        break;

                    case 1:
                        updateSorter (ExtensionSortingType.PRICE_DESCENDING);
                        break;

                    case 2:
                        updateSorter (ExtensionSortingType.ALPHABETIC_ASCENDING);
                        break;

                    case 3:
                        updateSorter (ExtensionSortingType.ALPHABETIC_DESCENDING);
                        break;

                }

            }

            @Override
            public void onNothingSelected (AdapterView<?> parent) {

                toastUser ("I'm a fluffy little puppy doge.", Toast.LENGTH_SHORT);

            }
        });

        super.onStart ();

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

        ImageButton loginActivityButton = (ImageButton) findViewById (R.id.loginActivityButton);
        loginActivityButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {

                triggerLoginActivity (v);

            }
        });

        ImageButton menuButton = (ImageButton) findViewById (R.id.menuButton);
        menuButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {

               menuButtonTrigger (v);

            }
        });

        ImageButton cartButton = (ImageButton) findViewById (R.id.cartButton);
        cartButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {

                cartButtonTrigger (v);

            }
        });

        AwesomeBobScroller scrollView = (AwesomeBobScroller) findViewById (R.id.scrollViewRowsThing);
        // STUFF AND THINGS

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

                    clearDomains ();
                    domainSearchedFor = null;
                    literalDomainSearchedFor = null;
                    domainSearchedForAvailabillity = false;
                    new ExtensionInitializer ().execute ();

                }

            }

        });

        new ExtensionInitializer ().execute ();

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
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource (this, R.array.sort, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerSorteringen.setAdapter (adapter2);

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
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService (Context.LAYOUT_INFLATER_SERVICE);

        if (domainSearchedFor instanceof Domain) {

            applicationExtensions = new ExtensionFilter (applicationExtensions, domainFilter).filter ();

            Log.d ("Bob", "domainzzzzzzzzzz");

            // Do the searched for domain first if it exists
            // Add a new domain_row
            TableRow newRow = (TableRow) layoutInflater.inflate (R.layout.domain_row, null);

            // Attempt to change the text of the domain_row layout to the corresponding domain
            TextView domeinRowText = (TextView) newRow.findViewById (R.id.domeinRowText);
            domeinRowText.setText (domainSearchedFor.getFullDomain ());

            final Button domeinPriceButton = (Button) newRow.findViewById (R.id.loadMoreExtensionsButton);

            if (domainSearchedForAvailabillity) {

                domeinPriceButton.setOnClickListener (new View.OnClickListener () {
                    @Override
                    public void onClick (View v) {

                    Log.d ("Bob", "CLICKEDEE CLOO");
                    toastUser ("Domein is toegevoegd aan uw winkelwagen.", Toast.LENGTH_SHORT);

                    CartManager.addToCart (new Domain (domainSearchedFor.getLiteralDomain (), domainSearchedFor.getLiteralExtension (), domainSearchedFor.getPrice (), true));

                    domeinPriceButton.setText ("Toegevoegd");

                    }
                });

            }

            String euroSign = "\u20ac";

//        Long now = System.currentTimeMillis ();
//        Date dateOfToday = new Date (now);
//
//        Date domainOfferDateBegin = null;
//        Date domainOfferDateEnd = null;
//
//        SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd");
//        try {
//            domainOfferDateBegin = sdf.parse (extensionIt.getSpecialOfferDateBegin ());
//            domainOfferDateEnd = sdf.parse (extensionIt.getSpecialOfferDateEnd ());
//        } catch (ParseException e) {
//            e.printStackTrace ();
//        }
//
//        if (dateOfToday.after (domainOfferDateBegin) && dateOfToday.before (domainOfferDateEnd)) {
//
//            Log.d ("Bob", "THE DOMAIN " + extensionIt.getTld () + " IS ON SAAAAAAALE #STEAMSALE #PRAISEGABEN the new price is " + extensionIt.getSpecialPrice ());
//
//            String originalPrice = euroSign + " " + extensionIt.getPricePerYear () + "0";
//            String salePrice = euroSign + " " + extensionIt.getSpecialPrice () + "0";
//
//            domeinPriceButton.setText (salePrice);
//              domeinPriceButton.setText (originalPrice + " " + salePrice, TextView.BufferType.SPANNABLE);
//
//              Spannable spannable = (Spannable) domeinPriceButton.getText();
//              spannable.setSpan (STRIKE_THROUGH_SPAN, 0, originalPrice.length (), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//              domeinPriceButton.setPaintFlags (domeinPriceButton.getPaintFlags () | Paint.STRIKE_THRU_TEXT_FLAG); // this works but it strikes all through
//
//        } else {
//
            if (domainSearchedFor.isAvailable ())
            {

                if (CartManager.isInCart (domainSearchedFor.getFullDomain ()))
                    domeinPriceButton.setText ("Toegevoegd");
                else
                    domeinPriceButton.setText (euroSign + " " + domainSearchedFor.getPrice () + "0");

            }
            else
            {

                domeinPriceButton.setText ("BEZET");

            }
//
//        }

            TableLayout table = (TableLayout) findViewById (R.id.domainRowsTable);
            table.addView (newRow);

            Log.d ("Bob", "Hopefully the domain " + domainSearchedFor.getFullDomain () + " was added to the list.");

        }

        if (!applicationExtensions.isEmpty () && literalDomainSearchedFor != null) {

            int iteration = 0;
            // Iterates through all found domains
            Iterator<Extension> extensionListIterator = applicationExtensions.iterator ();
            while (extensionListIterator.hasNext ()) {

                if (iteration++ >= 10)
                    break;

                // Get the next iteration
                final Extension extensionIt = extensionListIterator.next ();

                layoutInflater = (LayoutInflater) getSystemService (Context.LAYOUT_INFLATER_SERVICE);

                // Add a new domain_row
                View newRow = layoutInflater.inflate (R.layout.domain_row, null);

                // Attempt to change the text of the domain_row layout to the corresponding domain
                TextView domeinRowText = (TextView) newRow.findViewById (R.id.domeinRowText);
                domeinRowText.setText ((literalDomainSearchedFor.substring (0, literalDomainSearchedFor.indexOf ("."))) + "." + extensionIt.getTld ());

                final Button domainPriceButton = (Button) newRow.findViewById (R.id.loadMoreExtensionsButton);

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

                    domainPriceButton.setOnClickListener (new View.OnClickListener () {
                        @Override
                        public void onClick (View v) {

                        Log.d ("Bob", "CLICKEDEE CLOO");
                        toastUser ("Domein is toegevoegd aan uw winkelwagen.", Toast.LENGTH_SHORT);

                        CartManager.addToCart
                        (
                            new Domain
                            (
                                domainSearchedFor.getLiteralDomain (),
                                extensionIt.getTld (),
                                extensionIt.getPricePerYear (),
                                true
                            )
                        );

                        domainPriceButton.setText ("Toegevoegd");

                        }
                    });

                    if (dateOfToday.after (domainOfferDateBegin) && dateOfToday.before (domainOfferDateEnd)) {

                        Log.d ("Bob", "THE DOMAIN " + extensionIt.getTld () + " IS ON SAAAAAAALE #STEAMSALE #PRAISEGABEN the new price is " + extensionIt.getSpecialPrice ());

                        String originalPrice = euroSign + " " + extensionIt.getPricePerYear () + "0";
                        String salePrice = euroSign + " " + extensionIt.getSpecialPrice () + "0";

//                        domainPriceButton.setText (salePrice);
                        domainPriceButton.setText (originalPrice + " " + salePrice, TextView.BufferType.SPANNABLE);

    //                    Spannable spannable = (Spannable) domainPriceButton.getText();
    //                    spannable.setSpan (STRIKE_THROUGH_SPAN, 0, originalPrice.length (), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

    //                    domainPriceButton.setPaintFlags (domainPriceButton.getPaintFlags () | Paint.STRIKE_THRU_TEXT_FLAG); // this works but it strikes all through

                    } else {

                        if (CartManager.isInCart (domainSearchedFor.getLiteralDomain () + "." + extensionIt.getTld ()))
                            domainPriceButton.setText ("Toegevoegd");
                        else
                            domainPriceButton.setText (euroSign + " " + extensionIt.getPricePerYear () + "0");

                    }

                }
                else
                {

                    domainPriceButton.setText ("Bezet");

                }

                appendToTable (newRow);

            }

            addMoreDomainsButton ();

        } else {

            Log.d ("Bob", "domainList is very empty.");

        }

    }

    private void addMoreDomainsButton ()
    {

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService (Context.LAYOUT_INFLATER_SERVICE);

        View addMoreRowsButtonRow = layoutInflater.inflate (R.layout.domain_append_row, null);
        Button loadMoreShizButton = (Button) addMoreRowsButtonRow.findViewById (R.id.loadMoreExtensionsButton);
        loadMoreShizButton.setText ("Meer extensies laden");

        loadMoreShizButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {

                // load more domains
                killTheLastChildHidingUnderTheTable ();
                new batchOfFreshDomains ().execute ();
                // do something

            }
        });

        appendToTable (addMoreRowsButtonRow);

    }

    public class batchOfFreshDomains extends AsyncTask<String, String, String>
    {

        private String url = "https://www.compra.nl/?c=api&m=checkDomain&domein=";
        private String jsonBuffer;
        private List<View> views = new ArrayList<View> ();

        @Override
        protected void onPreExecute ()
        {

            toastUser ("Bezig met het laden van meer domeinen...", Toast.LENGTH_LONG);

        }

        @Override
        protected String doInBackground (String... params) {

            for (int i = rowCount; i <= (rowCount + AMOUNT_OF_DOMAINS_PER_BATCH); i++) {

                final Extension extensionIt = applicationExtensions.get (i);
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService (Context.LAYOUT_INFLATER_SERVICE);

                try {

                    jsonBuffer = getUrlSource (url + literalDomainSearchedFor + extensionIt.getTld ());

                } catch (IOException e) {

                    Log.d ("Bob", "jsonBuffer wilt niet getUrlSource() doen.");
                    e.printStackTrace ();

                }

                JSONObject jsonObject = null;
                try {

                    jsonObject = new JSONObject (jsonBuffer);
                    String availabillityCode = jsonObject.getString ("code");

                    // Add a new domain_row
                    View newRow = layoutInflater.inflate (R.layout.domain_row, null);

                    // Attempt to change the text of the domain_row layout to the corresponding domain
                    TextView domeinRowText = (TextView) newRow.findViewById (R.id.domeinRowText);
                    domeinRowText.setText ((literalDomainSearchedFor.substring (0, literalDomainSearchedFor.indexOf ("."))) + "." + extensionIt.getTld ());

                    final Button domainPriceButton = (Button) newRow.findViewById (R.id.loadMoreExtensionsButton);

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

                        domainPriceButton.setOnClickListener (new View.OnClickListener () {
                            @Override
                            public void onClick (View v) {

                                Log.d ("Bob", "CLICKEDEE CLOO");
                                toastUser ("Domein is toegevoegd aan uw winkelwagen.", Toast.LENGTH_SHORT);

                                CartManager.addToCart
                                (
                                    new Domain
                                    (
                                            domainSearchedFor.getLiteralDomain (),
                                            extensionIt.getTld (),
                                            extensionIt.getPricePerYear (),
                                            true
                                    )
                                );

                                domainPriceButton.setText ("Toegevoegd");

                            }
                        });

                        if (dateOfToday.after (domainOfferDateBegin) && dateOfToday.before (domainOfferDateEnd)) {

                            Log.d ("Bob", "THE DOMAIN " + extensionIt.getTld () + " IS ON SAAAAAAALE #STEAMSALE #PRAISEGABEN the new price is " + extensionIt.getSpecialPrice ());

                            String originalPrice = euroSign + " " + extensionIt.getPricePerYear () + "0";
                            String salePrice = euroSign + " " + extensionIt.getSpecialPrice () + "0";

//                        domainPriceButton.setText (salePrice);
                            domainPriceButton.setText (originalPrice + " " + salePrice, TextView.BufferType.SPANNABLE);

                            //                    Spannable spannable = (Spannable) domainPriceButton.getText();
                            //                    spannable.setSpan (STRIKE_THROUGH_SPAN, 0, originalPrice.length (), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                            //                    domainPriceButton.setPaintFlags (domainPriceButton.getPaintFlags () | Paint.STRIKE_THRU_TEXT_FLAG); // this works but it strikes all through

                        } else {

                            if (CartManager.isInCart (domainSearchedFor.getLiteralDomain () + "." + extensionIt.getTld ()))
                                domainPriceButton.setText ("Toegevoegd");
                            else
                                domainPriceButton.setText (euroSign + " " + extensionIt.getPricePerYear () + "0");

                        }

                    }
                    else
                    {

                        domainPriceButton.setText ("Bezet");

                    }

                    views.add (newRow);

                } catch (JSONException e) {
                    e.printStackTrace ();
                }

            }

            return null;

        }

        @Override
        protected void onPostExecute (String string)
        {

            Iterator<View> viewIterator = views.iterator ();
            while (viewIterator.hasNext ())
            {

                View viewIt = viewIterator.next ();
                appendToTable (viewIt);

            }

            addMoreDomainsButton ();

            final ScrollView scroller = (ScrollView) findViewById (R.id.scrollViewRowsThing);
            scroller.post (new Runnable () {

                @Override
                public void run () {

                    scroller.smoothScrollTo (0, Integer.MAX_VALUE); // 2147 miljoen zal vast wel genoeg zijn

                }

            });

        }

    }

    public class ExtensionInitializer extends AsyncTask<String, String, String> {

        private final String URL = "https://www.compra.nl/?c=api&m=getExtensions";
        private String jsonShit;

        @Override
        protected void onPreExecute ()
        {

            toastUser ("Laden extensies...", Toast.LENGTH_SHORT);

        }

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
                    double specialPrice = domainObj.getDouble ("special_offer_price");

                    localExtensionList.add (new Extension (id, tld, pricePerYear, popular, newDomain, region, restriction, specialOfferDateBegin, specialOfferDateEnd, specialPrice));

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

            initializeExtensions ();

        }

    }

    public class UpdateExtensions extends AsyncTask<String, String, String> {

        private final String URL = "https://www.compra.nl/?c=api&m=getExtensions";
        private String jsonShit;

        @Override
        protected void onPreExecute ()
        {

            toastUser ("Laden extensies...", Toast.LENGTH_SHORT);

        }

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
                for (int i = 0; i < jsonArray.length (); i++) {
//                for (int i = 0; i < AMOUNT_OF_DOMAINS_PER_BATCH; i++) {

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

            Log.d ("Bob", "Extensions have been updated");

            // Apply the filter
            applicationExtensions = new ExtensionFilter (applicationExtensions, domainFilter).filter ();

            // Apply the sorter
            applicationExtensions = new ExtensionSorter (applicationExtensions, extensionSorter).sort ();

            if (domainSearchedFor instanceof Domain)
                reinitializeExtensionsWithFoundDomain ();
            else
            {

                clearDomains ();
                initializeExtensions ();

            }

        }

    }

    public class CheckIfDomainAvailable extends AsyncTask<String, String, String> {

        private String url = "https://www.compra.nl/?c=api&m=checkDomain&domein=";
        private String jsonBuffer;

        @Override
        protected void onPreExecute ()
        {

            toastUser ("Laden domeinen & extensies...", Toast.LENGTH_LONG);

        }

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
                String domainWithoutExtension   = domainWithExtension.substring (0, domainWithExtension.indexOf ("."));
                String extensionOfDomain        = domainWithExtension.substring (domainWithExtension.indexOf (".") + 1, domainWithExtension.length ());

                Log.d ("Bob", "domainWithoutExtension value:");
                Log.d ("Bob", domainWithoutExtension);

                Log.d ("Bob", "extensionOfDomain value:");
                Log.d ("Bob", extensionOfDomain);

                /*
                 * Fetch the desired extension details
                 */
                try {

                    jsonBuffer = getUrlSource ("https://www.compra.nl/?c=api&m=getExtension&extension=" + extensionOfDomain);

                } catch (IOException e) {

                    Log.d ("Bob", "jsonBuffer wilt niet getUrlSource() doen.");
                    e.printStackTrace ();

                }

                jsonObject = new JSONObject (jsonBuffer);

                domainSearchedFor = new Domain (domainWithoutExtension, extensionOfDomain, jsonObject.getDouble ("price_per_year"), domainSearchedForAvailabillity);

                int timesRan = 0;

                Iterator <Extension> extensionIterator = applicationExtensions.iterator ();
                while (extensionIterator.hasNext () && timesRan++ <= AMOUNT_OF_DOMAINS_PER_BATCH)
                {

                    Extension extensionIt = extensionIterator.next ();

                    try {

                        jsonBuffer = getUrlSource (url + domainWithoutExtension + "." + extensionIt.getTld ());
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
