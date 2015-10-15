package nl.compra.compraapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class AccountActivity extends ActionBarActivity implements PopupMenu.OnMenuItemClickListener {

    @Override
    protected void onCreate (Bundle savedInstanceState) {

        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_account);

        TextView voornaamTV     = (TextView) findViewById (R.id.voornaamTextView);
        TextView achternaamTV   = (TextView) findViewById (R.id.achternaamTextView);
        TextView emailAdresTV   = (TextView) findViewById (R.id.emailAdresTextView);

        User currentlySignedInUser = UserManager.getCurrentlySignedInUser ();

        voornaamTV.setText   (currentlySignedInUser.getFirstname ());
        achternaamTV.setText (currentlySignedInUser.getLastname ());
        emailAdresTV.setText (currentlySignedInUser.getEmail ());

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

        Button logoutButton = (Button) findViewById (R.id.logoutButton);
        logoutButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {

                UserManager.logoutCurrentlySignedInUser ();
                toastUser ("U bent succesvol uitgelogd", Toast.LENGTH_SHORT);
                finish ();

            }
        });

    }

    private void menuButtonTrigger (View v) {

        PopupMenu popup = new PopupMenu (this, v);
        MenuInflater inflater = popup.getMenuInflater ();
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
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.menu_account, menu);
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
