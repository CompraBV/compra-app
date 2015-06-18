package nl.compra.compraapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // enables the activity icon as a 'home' button. required if "android:targetSdkVersion" > 14
//        getActionBar().setHomeButtonEnabled(true);

        setContentView(R.layout.activity_main);

        Context context = getApplicationContext();
        CharSequence text = "Welcome back to the Compra App!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);

        toast.show();

        //Spinners

        Spinner spinnerDomeinen = (Spinner) findViewById(R.id.domeinen);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.catagories, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerDomeinen.setAdapter(adapter);

        Spinner spinnerSorteringen = (Spinner) findViewById(R.id.sorteringen);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterrrrrrrrrrr = ArrayAdapter.createFromResource(this,
                R.array.sort, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterrrrrrrrrrr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerSorteringen.setAdapter(adapterrrrrrrrrrr);

        addDomainToTable();

    }

    public void addDomainToTable ()
    {

        TableLayout placeHolder = (TableLayout) findViewById(R.id.domainRowsTable);
        getLayoutInflater().inflate(R.layout.domain_row, placeHolder);

        try
        {

            String url = "https://www.compra.nl/?c=api&m=getExtensions";
            JSONObject jsonObject = new JSONObject (url);
            System.out.println ("JSON OPJECT WOOO");
//            System.out.println ("WASD WASD WTF I CAN'T MOVE");
//            System.out.println (jsonObject);

//        } catch (java.io.IOException e) {
//
//            e.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // This code was copied from the internet.
    // Source: http://stackoverflow.com/questions/8616781/how-to-get-a-web-pages-source-code-from-java
    private String getUrlSource(String url) throws IOException
    {

        URL custUrl = null;
        try {
            custUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        URLConnection yc = custUrl.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                yc.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuilder a = new StringBuilder();
        while ((inputLine = in.readLine()) != null)
            a.append(inputLine);
        in.close();

        return a.toString();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_login)
        {



        }


        return super.onOptionsItemSelected(item);
    }
}
