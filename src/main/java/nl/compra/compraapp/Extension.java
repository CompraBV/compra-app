package nl.compra.compraapp;

/**
 * Created by Bob Desaunois on 3-7-2015.
 */
public class Extension
{

    private int id;
    private String  tld;
    private double  pricePerYear;
    private int     popular;
    private int     newDomain;
    private String  region;
    private String  restriction;
    private double  specialPrice;
    private String  specialOfferDateBegin;
    private String  specialOfferDateEnd;
    private boolean available;

    public int      getId ()                      { return id; }
    public String   getTld ()                     { return tld; }
    public double   getPricePerYear ()            { return pricePerYear; }
    public int      getPopular ()                 { return popular; }
    public int      getNewDomain ()               { return newDomain; }
    public String   getRegion ()                  { return region; }
    public String   getRestriction ()             { return restriction; }
    public String   getSpecialOfferDateBegin ()   { return specialOfferDateBegin; }
    public String   getSpecialOfferDateEnd ()     { return specialOfferDateEnd; }
    public double   getSpecialPrice ()            { return specialPrice; }
    public boolean  isAvailable ()                { return available; }

    public void setAvailable (boolean available)  { this.available = available; }

    public Extension (int id, String tld, double pricePerYear, int popular, int newDomain, String region, String restriction, String specialOfferDateBegin, String specialOfferDateEnd, double specialPrice)
    {

        this.id                     = id;
        this.tld                    = tld;
        this.pricePerYear           = pricePerYear;
        this.popular                = popular;
        this.newDomain              = newDomain;
        this.region                 = region;
        this.restriction            = restriction;
        this.specialOfferDateBegin  = specialOfferDateBegin;
        this.specialOfferDateEnd    = specialOfferDateEnd;
        this.specialPrice           = specialPrice;

    }

}
