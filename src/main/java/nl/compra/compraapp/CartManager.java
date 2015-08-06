package nl.compra.compraapp;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Bob Desaunois on 24-7-2015.
 */
public class CartManager {

    public static List<Domain> cart = new CopyOnWriteArrayList<Domain> ();

    public static List<Domain> getCart ()   { return cart; }
    public static Domain get (int index)    { return cart.get (index); }

    public static void remove (int index) {

        if (cart.get (index) instanceof Domain)
            cart.remove (index);
        else
            Log.d ("Bob", "Dun goofed, nigga, y'all trynna remove a domain from cart that doesn't exist.");

    }

    public static void addToCart (Domain domain)
    {

        Log.d ("Bob", "Domain was added to cart: " + domain);
        cart.add (domain);

    }

    public static boolean isInCart (String fullDomain)
    {

        Iterator<Domain> domainIterator = cart.iterator ();
        while (domainIterator.hasNext ())
        {

            Domain domainIt = domainIterator.next ();

            if (fullDomain.equals (domainIt.getFullDomain ()))
                return true;

        }

        return false;

    }

    public static void removeByFullDomain (String fullDomain)
    {

        int iteration = 0;
        Iterator<Domain> cartIterator = cart.iterator ();
        while (cartIterator.hasNext ())
        {

            Domain cartIt = cartIterator.next ();

            if (fullDomain.equals (cartIt.getFullDomain ()))
                remove (iteration);
            else
                iteration++;


        }

    }

}
