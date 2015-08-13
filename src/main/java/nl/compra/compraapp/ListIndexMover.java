package nl.compra.compraapp;

import java.util.ArrayList;

public class ListIndexMover<T> {

    public ListIndexMover (ArrayList<T> list, int indexToMove, int indexTargetLocation)
    {

        T buffer = list.get (indexToMove);
        list.remove (indexToMove);
        list.set (indexTargetLocation, buffer);

    }

}
