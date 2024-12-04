package fr.florianpal.fauction.configurations.gui;

import org.bukkit.configuration.Configuration;

public class HistoricConfig extends AbstractGuiWithAuctionsConfig {

    public void load(Configuration config) {
        super.load(config, "historic");
    }
}
