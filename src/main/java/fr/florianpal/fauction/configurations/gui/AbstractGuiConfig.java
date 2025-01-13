package fr.florianpal.fauction.configurations.gui;

import org.bukkit.event.inventory.InventoryType;

public abstract class AbstractGuiConfig {

    protected InventoryType inventoryType;

    public InventoryType getType() {
        return inventoryType;
    }
}
