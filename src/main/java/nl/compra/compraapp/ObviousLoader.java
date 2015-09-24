package nl.compra.compraapp;

import android.app.Activity;
import android.app.ProgressDialog;

public class ObviousLoader extends Activity {

    public ProgressDialog obviousLoadingDialog;

    public void loadObviously (String message)
    {

        ProgressDialog.show (getApplicationContext (), "Laden", message);

    }

}
