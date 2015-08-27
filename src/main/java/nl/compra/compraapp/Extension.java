package nl.compra.compraapp;

import java.math.BigDecimal;
import java.util.Comparator;

/**
 * Created by Bob Desaunois on 3-7-2015.
 */
public class Extension implements Comparable<Extension>
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

    @Override
    public int compareTo (Extension another)
    {

        BigDecimal compareQuantity = new BigDecimal (((Extension) another).getPricePerYear ());

        BigDecimal moolah = new BigDecimal (pricePerYear);

        if (MainActivity.getExtensionSortingType () == ExtensionSortingType.PRICE_ASCENDING)
            return moolah.subtract (compareQuantity).intValue ();

        else if (MainActivity.getExtensionSortingType () == ExtensionSortingType.ALPHABETIC_DESCENDING)
            return compareQuantity.subtract (moolah).intValue ();

        return 0;

    }

    public static Comparator<Extension> ExtensionNameComparator = new Comparator<Extension> () {
        @Override
        public int compare (Extension lhs, Extension rhs) {

            String extensionName1 = lhs.getTld ().toUpperCase ();
            String extensionName2 = rhs.getTld ().toUpperCase ();

            if (MainActivity.getExtensionSortingType () == ExtensionSortingType.ALPHABETIC_ASCENDING)
                return extensionName1.compareTo (extensionName2);

            else if (MainActivity.getExtensionSortingType () == ExtensionSortingType.ALPHABETIC_DESCENDING)
                return extensionName2.compareTo (extensionName1);

            return 0;

        }
    };

}
