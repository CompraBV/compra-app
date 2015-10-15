package nl.compra.compraapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class CartActivity extends ActionBarActivity implements PopupMenu.OnMenuItemClickListener {

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
        final Iterator<Domain> cartIterator = CartManager.getCart ().iterator ();
        while (cartIterator.hasNext ())
        {

            final Domain cartIt = cartIterator.next ();

            LayoutInflater layoutInflater = (LayoutInflater) getSystemService (Context.LAYOUT_INFLATER_SERVICE);
            View newRow = newRow = layoutInflater.inflate (R.layout.domain_row_cart, null);

            TextView dpb     = (TextView) newRow.findViewById (R.id.loadMoreExtensionsButton);
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

        Button loginActivityButton = (Button) findViewById (R.id.loginActivityButton);
        loginActivityButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {

                triggerLoginActivity (v);

            }
        });

        Button menuButton = (Button) findViewById (R.id.menuButton);
        menuButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {

                menuButtonTrigger (v);

            }
        });

        Button checkoutButton = (Button) findViewById (R.id.checkoutButton);
        checkoutButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {

                // User clicks the checkout button
                StringBuilder sb = new StringBuilder ();
                sb.append ("https://compra.nl/?c=api&m=readyToPay&id=" + UserManager.getCurrentlySignedInUser ().getId () + "&api_id=" + UserManager.getApiID ());

                CopyOnWriteArrayList<Domain> cart = (CopyOnWriteArrayList<Domain>) CartManager.getCart ();

                if (!cart.isEmpty ()) {

                    Iterator<Domain> cartIterator = cart.iterator ();
                    while (cartIterator.hasNext ()) {

                        Domain domain = cartIterator.next ();
                        sb.append ("&domains[]=" + domain.getFullDomain ());

                    }

                    String url = sb.toString ();
                    Intent browserIntent = new Intent (Intent.ACTION_VIEW, Uri.parse (url));
                    startActivity (browserIntent);

                } else {

                    toastUser ("Je winkelwagen is leeg.", Toast.LENGTH_SHORT);

                }

            }

        });

    }

    private void menuButtonTrigger (View v) {

        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener (this);
        inflater.inflate (R.menu.menu, popup.getMenu ());
        popup.show ();

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

    @Override
    public boolean onMenuItemClick (MenuItem item) {
        return false;
    }
}
