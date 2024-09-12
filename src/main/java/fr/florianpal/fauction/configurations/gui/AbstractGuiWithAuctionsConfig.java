package fr.florianpal.fauction.configurations.gui;

import java.util.List;

public abstract class AbstractGuiWithAuctionsConfig extends AbstractGuiConfig {

    public abstract List<Integer> getAuctionBlocks();

    public abstract int getSize();
}
