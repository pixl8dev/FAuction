package fr.florianpal.fauction.utils;

import fr.florianpal.fauction.objects.Auction;
import fr.florianpal.fauction.objects.Historic;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {

    public static List<Auction> historicToAuction(List<Historic> historics) {
        return new ArrayList<>(historics);
    }
}
