package fr.florianpal.fauction.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import fr.florianpal.fauction.FAuction;
import fr.florianpal.fauction.configurations.GlobalConfig;
import fr.florianpal.fauction.events.AuctionAddEvent;
import fr.florianpal.fauction.gui.subGui.AuctionsGui;
import fr.florianpal.fauction.gui.subGui.ExpireGui;
import fr.florianpal.fauction.languages.MessageKeys;
import fr.florianpal.fauction.managers.SpamManager;
import fr.florianpal.fauction.managers.commandmanagers.AuctionCommandManager;
import fr.florianpal.fauction.managers.commandmanagers.CommandManager;
import fr.florianpal.fauction.managers.commandmanagers.ExpireCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import static java.lang.Math.ceil;

@CommandAlias("ah|hdv")
public class AuctionCommand extends BaseCommand {

    private final FAuction plugin;

    private final CommandManager commandManager;

    private final AuctionCommandManager auctionCommandManager;

    private final ExpireCommandManager expireCommandManager;

    private final SpamManager spamManager;

    private final GlobalConfig globalConfig;

    public AuctionCommand(FAuction plugin) {
        this.plugin = plugin;
        this.commandManager = plugin.getCommandManager();
        this.auctionCommandManager = plugin.getAuctionCommandManager();
        this.expireCommandManager = plugin.getExpireCommandManager();
        this.spamManager = plugin.getSpamManager();
        this.globalConfig = plugin.getConfigurationManager().getGlobalConfig();
    }

    @Default
    @Subcommand("list")
    @CommandPermission("fauction.list")
    @Description("{@@fauction.auction_list_help_description}")
    public void onList(Player playerSender) {

        if (spamManager.spamTest(playerSender)) {
            return;
        }

        FAuction.newChain().asyncFirst(auctionCommandManager::getAuctions).syncLast(auctions -> {
            AuctionsGui gui = new AuctionsGui(plugin, playerSender, auctions, 1, null);
            gui.initializeItems();
            CommandIssuer issuerTarget = commandManager.getCommandIssuer(playerSender);
            issuerTarget.sendInfo(MessageKeys.AUCTION_OPEN);
        }).execute();
    }

    @Subcommand("sell")
    @CommandPermission("fauction.sell")
    @Description("{@@fauction.auction_add_help_description}")
    public void onAdd(Player playerSender, double price) {

        if (spamManager.spamTest(playerSender)) {
            return;
        }

        CommandIssuer issuerTarget = commandManager.getCommandIssuer(playerSender);
        ItemStack itemToSell = playerSender.getInventory().getItemInMainHand();

        if (price < 0) {
            issuerTarget.sendInfo(MessageKeys.NEGATIVE_PRICE);
            return;
        }

        if (itemToSell.getType().equals(Material.AIR)) {
            issuerTarget.sendInfo(MessageKeys.ITEM_AIR);
            return;
        }

        if (globalConfig.getBlacklistItem().contains(itemToSell.getType())) {
            issuerTarget.sendInfo(MessageKeys.ITEM_BLACKLIST);
            return;
        }

        if(!haveCorrectMinPrice(itemToSell, issuerTarget, playerSender, price)) {
            return;
        }

        if(!haveCorrectMaxPrice(itemToSell, issuerTarget, price)) {
            return;
        }

        if(Tag.SHULKER_BOXES.getValues().contains(itemToSell.getType())) {
            ItemStack item = playerSender.getInventory().getItemInMainHand();
            if (item.getItemMeta() instanceof BlockStateMeta) {

                BlockStateMeta im = (BlockStateMeta) item.getItemMeta();
                double minPrice = -1;
                double maxPrice = -1;
                if (im.getBlockState() instanceof ShulkerBox) {

                    ShulkerBox shulker = (ShulkerBox) im.getBlockState();
                    for (ItemStack itemIn : shulker.getInventory().getContents()) {
                        if (itemIn != null && (itemIn.getType() != Material.AIR)) {
                            if (plugin.getConfigurationManager().getGlobalConfig().getMinPrice().containsKey(itemIn.getType())) {
                                minPrice = minPrice + itemIn.getAmount() * globalConfig.getMinPrice().get(itemIn.getType());
                            } else if (plugin.getConfigurationManager().getGlobalConfig().isDefaultMinValueEnable()) {
                                minPrice = minPrice + itemIn.getAmount() * globalConfig.getDefaultMinValue();
                            }

                            if (plugin.getConfigurationManager().getGlobalConfig().getMaxPrice().containsKey(itemIn.getType())) {
                                maxPrice = maxPrice + itemIn.getAmount() * globalConfig.getMaxPrice().get(itemIn.getType());
                            } else if (plugin.getConfigurationManager().getGlobalConfig().isDefaultMaxValueEnable()) {
                                maxPrice = maxPrice + itemIn.getAmount() * globalConfig.getDefaultMaxValue();
                            }
                        }
                    }
                    if (minPrice != -1 && minPrice > price) {
                        issuerTarget.sendInfo(MessageKeys.MIN_PRICE, "{minPrice}", String.valueOf(ceil(minPrice)));
                        return;
                    }

                    if (maxPrice != -1 && maxPrice < price) {
                        issuerTarget.sendInfo(MessageKeys.MAX_PRICE, "{maxPrice}", String.valueOf(ceil(maxPrice)));
                        return;
                    }
                }
            }
        }

        FAuction.newChain().asyncFirst(() -> plugin.getAuctionCommandManager().getAuctions(playerSender.getUniqueId())).syncLast(auctions -> {

            int limitations;
            if (plugin.getConfigurationManager().getGlobalConfig().isLimitationsUseMetaLuckperms()) {
                limitations = plugin.getLimitationManager().getAuctionLimitationByMeta(playerSender);
            } else {
                limitations = plugin.getLimitationManager().getAuctionLimitationByConfig(playerSender);
            }


            if (limitations != -1 && limitations <= auctions.size()) {
                issuerTarget.sendInfo(MessageKeys.MAX_AUCTION);
                return;
            }

            String itemName = itemToSell.getItemMeta().getDisplayName() == null || itemToSell.getItemMeta().getDisplayName().isEmpty() ? itemToSell.getType().toString() : itemToSell.getItemMeta().getDisplayName();
            plugin.getLogger().info("Player " + playerSender.getName() + " add item to ah Item : " + itemName + ", At Price : " + price);
            auctionCommandManager.addAuction(playerSender, itemToSell, price);
            Bukkit.getPluginManager().callEvent(new AuctionAddEvent(playerSender, itemToSell, price));
            playerSender.getInventory().getItemInMainHand().setAmount(0);
            issuerTarget.sendInfo(MessageKeys.AUCTION_ADD_SUCCESS);
        }).execute();
    }

    public boolean haveCorrectMinPrice(ItemStack itemToSell, CommandIssuer issuerTarget, Player playerSender, double price) {

        if(globalConfig.getMinPrice().containsKey(itemToSell.getType())) {
            double minPrice = playerSender.getInventory().getItemInMainHand().getAmount() *  globalConfig.getMinPrice().get(itemToSell.getType());
            if(minPrice > price) {
                issuerTarget.sendInfo(MessageKeys.MIN_PRICE, "{minPrice}", String.valueOf(ceil(minPrice)));
                return false;
            }
        } else if (globalConfig.isDefaultMinValueEnable()) {
            double minPrice = itemToSell.getAmount() *  globalConfig.getDefaultMinValue();
            if(minPrice > price) {
                issuerTarget.sendInfo(MessageKeys.MIN_PRICE, "{minPrice}", String.valueOf(ceil(minPrice)));
                return false;
            }
        }

        return true;
    }

    public boolean haveCorrectMaxPrice(ItemStack itemToSell, CommandIssuer issuerTarget, double price) {

        if(globalConfig.getMaxPrice().containsKey(itemToSell.getType())) {
            double maxPrice = itemToSell.getAmount() *  globalConfig.getMaxPrice().get(itemToSell.getType());
            if(maxPrice < price) {
                issuerTarget.sendInfo(MessageKeys.MAX_PRICE, "{maxPrice}", String.valueOf(ceil(maxPrice)));
                return false;
            }
        } else if (globalConfig.isDefaultMaxValueEnable()) {
            double maxPrice = itemToSell.getAmount() *  globalConfig.getDefaultMaxValue();
            if(maxPrice < price) {
                issuerTarget.sendInfo(MessageKeys.MAX_PRICE, "{maxPrice}", String.valueOf(ceil(maxPrice)));
                return false;
            }
        }
        return true;
    }

    @Subcommand("expire")
    @CommandPermission("fauction.expire")
    @Description("{@@fauction.expire_add_help_description}")
    public void onExpire(Player playerSender) {

        FAuction.newChain().asyncFirst(() -> expireCommandManager.getExpires(playerSender.getUniqueId())).syncLast(auctions -> {
            ExpireGui gui = new ExpireGui(plugin, playerSender, auctions, 1, null);
            gui.initializeItems();
            CommandIssuer issuerTarget = commandManager.getCommandIssuer(playerSender);
            issuerTarget.sendInfo(MessageKeys.AUCTION_OPEN);
        }).execute();
    }

    @Subcommand("admin reload")
    @CommandPermission("fauction.admin.reload")
    @Description("{@@fauction.reload_help_description}")
    public void onReload(Player playerSender) {

        CommandIssuer issuerTarget = commandManager.getCommandIssuer(playerSender);
        plugin.reloadConfiguration();
        issuerTarget.sendInfo(MessageKeys.AUCTION_RELOAD);
    }

    @Subcommand("admin purge all")
    @CommandPermission("fauction.admin.purge.all")
    @Description("{@@fauction.reload_help_description}")
    public void onPurgeAll(Player playerSender) {

        CommandIssuer issuerTarget = commandManager.getCommandIssuer(playerSender);
        plugin.purgeAllData();
        issuerTarget.sendInfo(MessageKeys.AUCTION_PURGE);
    }

    @Subcommand("admin purge historic")
    @CommandPermission("fauction.admin.purge.hictoric")
    @Description("{@@fauction.reload_help_description}")
    public void onPurgeAllHistoric(Player playerSender) {

        CommandIssuer issuerTarget = commandManager.getCommandIssuer(playerSender);
        plugin.purgeAllHistoric();
        issuerTarget.sendInfo(MessageKeys.AUCTION_PURGE);
    }

    @Subcommand("admin purge expire")
    @CommandPermission("fauction.admin.purge.expire")
    @Description("{@@fauction.reload_help_description}")
    public void onPurgeAllExpire(Player playerSender) {

        CommandIssuer issuerTarget = commandManager.getCommandIssuer(playerSender);
        plugin.purgeAllExpire();
        issuerTarget.sendInfo(MessageKeys.AUCTION_PURGE);
    }

    @Subcommand("admin purge auction")
    @CommandPermission("fauction.admin.purge.auction")
    @Description("{@@fauction.reload_help_description}")
    public void onPurgeAllAucton(Player playerSender) {

        CommandIssuer issuerTarget = commandManager.getCommandIssuer(playerSender);
        plugin.purgeAllAuction();
        issuerTarget.sendInfo(MessageKeys.AUCTION_PURGE);
    }

    @Subcommand("admin transfertToPaper")
    @CommandPermission("fauction.admin.transfertBddToPaper")
    @Description("{@@fauction.transfert_bdd_help_description}")
    public void onTransferBddPaper(Player playerSender) {

        CommandIssuer issuerTarget = commandManager.getCommandIssuer(playerSender);
        plugin.getTransfertManager().transfertBDD(true);
        issuerTarget.sendInfo(MessageKeys.TRANSFERT_BDD);
    }

    @Subcommand("admin transfertToBukkit")
    @CommandPermission("fauction.admin.transfertBddToPaper")
    @Description("{@@fauction.transfert_bdd_help_description}")
    public void onTransferBddSpigot(Player playerSender) {

        CommandIssuer issuerTarget = commandManager.getCommandIssuer(playerSender);
        plugin.getTransfertManager().transfertBDD(false);
        issuerTarget.sendInfo(MessageKeys.TRANSFERT_BDD);
    }

    @HelpCommand
    @Description("{@@fauction.help_description}")
    public void doHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}