package nl.compra.compraapp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bob Desaunois on 24-7-2015.
 */
public class CartManager {

    public static List<Domain> cart = new ArrayList<Domain> ();

    public static List<Domain> getCart ()   { return cart; }
    public static Domain get (int index)    { return cart.get (index); }

    public static void addToCart (Domain domain) { cart.add (domain); }

}
