package nl.compra.compraapp;

/**
 * Created by Bob Desaunois on 23-7-2015.
 */
public class User {

    private int id;
    private String  firstname,
                    lastname,
                    email;

    public int    getId ()          { return id; }
    public String getFirstname ()   { return firstname; }
    public String getLastname ()    { return lastname; }
    public String getEmail ()       { return email; }

    public User (int id, String firstname, String lastname, String email)
    {

        this.id         = id;
        this.firstname  = firstname;
        this.lastname   = lastname;
        this.email      = email;

    }

}
