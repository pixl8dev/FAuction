package fr.florianpal.fauction.objects;

import fr.florianpal.fauction.enums.CurrencyType;

import java.util.UUID;

public class CurrencyPending {

    private final int id;

    private final UUID playerUUID;

    private final CurrencyType currencyType;

    private final double amount;

    public CurrencyPending(int id, UUID playerUUID, CurrencyType currencyType, double amount) {
        this.id = id;
        this.playerUUID = playerUUID;
        this.currencyType = currencyType;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public CurrencyType getCurrencyType() {
        return currencyType;
    }

    public double getAmount() {
        return amount;
    }
}
