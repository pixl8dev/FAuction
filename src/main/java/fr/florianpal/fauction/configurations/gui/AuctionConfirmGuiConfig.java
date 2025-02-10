package fr.florianpal.fauction.configurations.gui;

import dev.dejvokep.boostedyaml.YamlDocument;
import fr.florianpal.fauction.FAuction;
import fr.florianpal.fauction.objects.Confirm;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.florianpal.fauction.enums.BlockType.CONFIRM;

public class AuctionConfirmGuiConfig extends AbstractGuiWithAuctionsConfig {

    private String titleTrue = "";

    private String titleFalse = "";

    private String auctionTitle = "";

    private List<String> auctionDescription = new ArrayList<>();

    public void load(FAuction plugin, YamlDocument config) {

        super.load(plugin, config, "auction");

        titleTrue = config.getString("gui.title-true");
        titleFalse = config.getString("gui.title-false");

        auctionTitle = config.getString("gui.auction.title");
        replaceTitle = config.getBoolean("gui.auction.replaceTitle");
        auctionDescription = config.getStringList("gui.auction.description");
    }

    public List<Confirm> getConfirmBlocks() {
        return confirmBlocks;
    }

    public String getTitleTrue() {
        return titleTrue;
    }

    public String getTitleFalse() {
        return titleFalse;
    }

    public String getAuctionTitle() {
        return auctionTitle;
    }

    public List<String> getAuctionDescription() {
        return auctionDescription;
    }
}
