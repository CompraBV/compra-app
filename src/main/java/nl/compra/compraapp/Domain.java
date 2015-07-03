package nl.compra.compraapp;

/**
 * Created by Bob Desaunois on 3-7-2015.
 */
public class Domain
{

    private int id;
    private String tld;
    private double pricePerYear;
    private int popular;
    private int newDomain;
    private String region;
    private String restriction;

    public int getId ()                 { return id; }
    public String getTld ()             { return tld; }
    public double getPricePerYear ()    { return pricePerYear; }
    public int getPopular ()            { return popular; }
    public int getNewDomain ()          { return newDomain; }
    public String getRegion ()          { return region; }
    public String getRestriction ()     { return restriction; }

    public Domain (int id, String tld, double pricePerYear, int popular, int newDomain, String region, String restriction)
    {

        this.id = id;
        this.tld = tld;
        this.pricePerYear = pricePerYear;
        this.popular = popular;
        this.newDomain = newDomain;
        this.region = region;
        this.restriction = restriction;

    }

}
