package nl.compra.compraapp;

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
