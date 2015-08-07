package nl.compra.compraapp;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Iterator;


public class CartActivity extends ActionBarActivity {

    private int iterations;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_cart);

    }

    @Override
    protected void onResume ()
    {

        super.onResume ();
        initialize ();

    }

    private void toastUser (String message, final int length)
    {

        Context context = getApplicationContext ();
        int duration = length;

        Toast toast = Toast.makeText (context, message, duration); // < Ignore this error, it's not an error.

        toast.show ();

    }

    private void initialize ()
    {

        TableLayout cartTable = (TableLayout) findViewById (R.id.winkelwagenTableLayout);
        cartTable.removeAllViews ();

        iterations = 0;
        Iterator<Domain> cartIterator = CartManager.getCart ().iterator ();
        while (cartIterator.hasNext ())
        {

            final Domain cartIt = cartIterator.next ();

            LayoutInflater layoutInflater = (LayoutInflater) getSystemService (Context.LAYOUT_INFLATER_SERVICE);
            View newRow = newRow = layoutInflater.inflate (R.layout.domain_row_cart, null);

            Button dpb       = (Button) newRow.findViewById (R.id.domeinRowOrderButton);
            ImageButton tc   = (ImageButton) newRow.findViewById (R.id.trashcanButton);
            TextView tv      = (TextView) newRow.findViewById (R.id.domeinRowText);


            tc.setOnClickListener (new View.OnClickListener () {

                @Override
                public void onClick (View v) {

                Log.d ("Bob", "Iterate value: " + iterations);
                CartManager.removeByFullDomain (cartIt.getFullDomain ());

                toastUser ("Het domein is verwijderd uit uw winkelwagen.", Toast.LENGTH_SHORT);
                initialize ();
                return;

                }
            });

            String euroSign = "\u20ac";

            dpb.setText (euroSign + " " + cartIt.getPrice () + "0");
            tv.setText (cartIt.getFullDomain ());

            cartTable.addView (newRow);
            iterations++;

        }

    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.menu_cart, menu);
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

        return super.onOptionsItemSelected (item);
    }
}
