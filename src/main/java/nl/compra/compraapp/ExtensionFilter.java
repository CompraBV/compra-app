package nl.compra.compraapp;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Bob Desaunois on 17-7-2015.
 */
public class ExtensionFilter {

    List<Extension> extensionList;
    ExtensionFilterType filterType;

    public ExtensionFilter (List<Extension> extensionList, ExtensionFilterType filterType)
    {

        this.extensionList  = extensionList;
        this.filterType     = filterType;

    }

    public List<Extension> filter ()
    {

        Iterator<Extension> extensionIterator = extensionList.iterator ();
        while (extensionIterator.hasNext ())
        {

            Extension extensionIt = extensionIterator.next ();

            // Filter out the searched for domain to prevent xX_DupliCateZz_Xx
            ////////////// THIS SHOULD EVENTUALLY BE TAKEN OVER BY ExtensionSorter (the sorter should sort the searched for domain to the very top) //////////////
            if (MainActivity.domainSearchedFor instanceof Domain)
            {

                if (extensionIt.getTld ().equals (MainActivity.domainSearchedFor.getLiteralExtension ()))
                {

                    extensionIterator.remove ();
                    continue;

                }

            }

            switch (filterType)
            {

                default:
                    Log.d ("Bob", "Uh oh, ExtensionFilter had it's default value triggered in a switch :(");
                    break;

                case ALL:
                    // Nothing to filter
                    break;

                case POPULAR:
                    if (extensionIt.getPopular () < 1)
                        extensionIterator.remove ();
                    break;

                case NEW:
                    if (extensionIt.getNewDomain () < 1)
                        extensionIterator.remove ();
                    break;

                case EUROPE:
                    if ( ! extensionIt.getRegion ().equals ("Europese"))
                        extensionIterator.remove ();
                    break;

                case COUNTRIES:
                    if (extensionIt.getRegion ().equals ("Generieke") /*|| ! extensionIt.getRegion ().equals ("")*/)
                        extensionIterator.remove ();
                    break;

                case COMMON:
                    if ( ! extensionIt.getRegion ().equals ("Generieke"))
                        extensionIterator.remove ();
                    break;

                case SPECIAL_OFFERS:

                    Long now = System.currentTimeMillis ();
                    Date dateOfToday = new Date (now);

                    Date domainOfferDateBegin = null;
                    Date domainOfferDateEnd = null;

                    SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd");
                    try {
                        domainOfferDateBegin = sdf.parse (extensionIt.getSpecialOfferDateBegin ());
                        domainOfferDateEnd = sdf.parse (extensionIt.getSpecialOfferDateEnd ());
                    } catch (ParseException e) {
                        e.printStackTrace ();
                    }

                    if ( ! dateOfToday.after (domainOfferDateBegin) && ! dateOfToday.before (domainOfferDateEnd)) {

                        extensionIterator.remove ();

                    }

                    break;

            }

        }

        return extensionList;

    }

}
