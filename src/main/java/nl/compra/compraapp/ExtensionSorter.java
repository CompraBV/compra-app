package nl.compra.compraapp;

import android.util.Log;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ExtensionSorter {

    List<Extension> extensionList;
    ExtensionSortingType sortingType;

    public ExtensionSorter (List<Extension> extensionList, ExtensionSortingType sortingType)
    {

        this.extensionList  = extensionList;
        this.sortingType     = sortingType;

    }

    public List<Extension> sort ()
    {

        Log.d ("Bob", "Cheeeeeeeeeeeeeeeese " + sortingType);

        int currentIndex = 0;
        Iterator<Extension> extensionIterator = extensionList.iterator ();
        while (extensionIterator.hasNext ())
        {

            Extension extensionIt = extensionIterator.next ();

            switch (sortingType)
            {

                case NONE:
                    // Absolutely fuck all nothing
                    break;

                case PRICE_ASCENDING:
                case PRICE_DESCENDING:

                    Collections.sort (extensionList);

                    break;

                case ALPHABETIC_ASCENDING:
                case ALPHABETIC_DESCENDING:

                    Collections.sort (extensionList, Extension.ExtensionNameComparator);

                    break;

                default:
                    Log.d ("Bob", "Big problems in ExtensionSorter, we just hit the default case. #houstonWeHaveAProblem ;___;");
                    break;

            }

//            Log.d ("Bob", "Current index in sorter: " + currentIndex);
            currentIndex++;

        }

        return extensionList;

    }

}
