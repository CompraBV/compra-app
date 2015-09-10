package nl.compra.compraapp;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AccountActivity extends ActionBarActivity {

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
