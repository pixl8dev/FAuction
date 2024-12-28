package fr.florianpal.fauction.utils;

import fr.florianpal.fauction.objects.Auction;
import fr.florianpal.fauction.objects.Category;
import fr.florianpal.fauction.objects.Historic;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListUtil {

    public static List<Auction> historicToAuction(List<Historic> historics) {
        return new ArrayList<>(historics);
    }

    public static List<Auction> getAuctionByCategory(List<Auction> auctions, Category category) {

        if (!category.containsAll()) {
            auctions = auctions.stream().filter(a -> category.getMaterials().contains(a.getItemStack().getType())).collect(Collectors.toList());
        } else if (category.containsEnchanted()) {
            auctions = auctions.stream().filter(a -> !a.getItemStack().getEnchantments().isEmpty()).collect(Collectors.toList());
        }

        return auctions;
    }
}
