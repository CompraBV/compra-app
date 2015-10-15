package nl.compra.compraapp;

public class UserManager {

    private static User currentlySignedInUser;
    private static String apiID;

    public static User getCurrentlySignedInUser () { return currentlySignedInUser; }
    public static String getApiID () { return apiID; }

    public static void setApiID (String apiID) { UserManager.apiID = apiID; }
    public static void setCurrentlySignedInUser (User currentlySignedInUser) { UserManager.currentlySignedInUser = currentlySignedInUser; }

    public static void logoutCurrentlySignedInUser () {

        currentlySignedInUser = null;

    }

}
