package fr.florianpal.fauction.objects;

import java.util.UUID;

public class Historic extends Auction {

    private final UUID playerBuyerUUID;

    private final String playerBuyerName;

    public Historic(int id, UUID playerUUID, String playerName, UUID playerBuyerUUID, String playerBuyerName, double price, byte[] item, long date) {
        super(id, playerUUID, playerName, price, item, date);
        this.playerBuyerUUID = playerBuyerUUID;
        this.playerBuyerName = playerBuyerName;
    }

    public UUID getPlayerBuyerUUID() {
        return playerBuyerUUID;
    }

    public String getPlayerBuyerName() {
        return playerBuyerName;
    }
}
