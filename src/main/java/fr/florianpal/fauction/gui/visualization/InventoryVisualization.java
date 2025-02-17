package fr.florianpal.fauction.gui.visualization;

import fr.florianpal.fauction.FAuction;
import fr.florianpal.fauction.gui.subGui.AuctionsGui;
import org.bukkit.Bukkit;
import org.bukkit.block.Barrel;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class InventoryVisualization implements InventoryHolder, Listener {

    private final FAuction plugin;

    private Inventory inventory;

    private final Player player;

    public InventoryVisualization(FAuction plugin, Player player, ShulkerBox shulkerBox) {
        this.player = player;
        this.plugin = plugin;
        createInventory(shulkerBox.getInventory(), InventoryType.SHULKER_BOX);
    }

    public InventoryVisualization(FAuction plugin, Player player, Barrel barrel) {
        this.player = player;
        this.plugin = plugin;
        createInventory(barrel.getInventory(), InventoryType.BARREL);
    }

    private void createInventory(Inventory oldInventory, InventoryType inventoryType) {

        inventory = Bukkit.createInventory(this, inventoryType);
        inventory.setContents(oldInventory.getContents());
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void open() {
        player.openInventory(inventory);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClickEvent(InventoryClickEvent event) {

        if (event.getInventory().getHolder() != this) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    private void onInventoryClose(InventoryCloseEvent event) {

        if (event.getInventory().getHolder() != this) {
            return;
        }

        FAuction.newChain().asyncFirst(() -> plugin.getAuctionCommandManager().getAuctions()).syncLast(auctions -> {
            AuctionsGui gui = new AuctionsGui(plugin, player, auctions, 1, null);
            gui.initialize();
        }).execute();
    }
}
