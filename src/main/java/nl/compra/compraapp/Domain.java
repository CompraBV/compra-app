package nl.compra.compraapp;

/**
 * Created by Bob Desaunois on 16-7-2015.
 */
public class Domain {

    private String  literalDomain;
    private String  literalExtension;
    private double  price;
    private boolean available;

    public String   getLiteralDomain ()     { return literalDomain; }
    public String   getLiteralExtension ()  { return literalExtension; }
    public String   getFullDomain ()        { return literalDomain + "." + literalExtension; }
    public double   getPrice ()             { return price; }
    public boolean  isAvailable ()          { return available; }


    public Domain (String literalDomain, String literalExtension, double price, boolean available)
    {

        this.literalDomain      = literalDomain;
        this.literalExtension   = literalExtension;
        this.price              = price;
        this.available          = available;

    }
}
