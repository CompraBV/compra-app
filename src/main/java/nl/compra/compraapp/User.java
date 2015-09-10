package nl.compra.compraapp;

public class User {

    private int id;
    private String  firstname;
    private String  lastname;
    private String  email;

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
