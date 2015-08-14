package nl.compra.compraapp;

/**
 * Created by Bob Desaunois on 23-7-2015.
 */
public class UserManager {

    private static User currentlySignedInUser;

    public static User getCurrentlySignedInUser () { return currentlySignedInUser; }

    public static void setCurrentlySignedInUser (User currentlySignedInUser)
    {

        UserManager.currentlySignedInUser = currentlySignedInUser;

    }

    public static void logoutCurrentlySignedInUser () {

        currentlySignedInUser = null;
    }
}
