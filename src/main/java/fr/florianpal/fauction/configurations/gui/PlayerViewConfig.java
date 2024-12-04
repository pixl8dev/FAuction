package fr.florianpal.fauction.configurations.gui;

import org.bukkit.configuration.Configuration;

public class PlayerViewConfig extends AbstractGuiWithAuctionsConfig {

    public void load(Configuration config) {
        super.load(config, "auction");
    }
}
