package nl.compra.compraapp;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ExtensionSorter {

    List<Extension> extensionList;
    ExtensionSortingType sortingType;

    public ExtensionSorter (List<Extension> extensionList, ExtensionSortingType sortingType)
    {

        this.extensionList  = (CopyOnWriteArrayList) extensionList;
        this.sortingType     = sortingType;

    }

    public List<Extension> sort ()
    {

        int currentIndex = 0;
        Iterator<Extension> extensionIterator = extensionList.iterator ();
        while (extensionIterator.hasNext ())
        {

            Extension extensionIt = extensionIterator.next ();
            currentIndex++;


            switch (sortingType)
            {

                default:
                    Log.d ("Bob", "Big problems in ExtensionSorter, we just hit the default case. #houstonWeHaveAProblem");
                    break;

                case PRICE_ASCENDING:
                    break;

                case PRICE_DESCENDING:
                    break;

                case ALPHABETIC_ASCENDING:
                    break;

                case ALPHABETIC_DESCENDING:
                    break;

            }

        }

        return extensionList;

    }

}
