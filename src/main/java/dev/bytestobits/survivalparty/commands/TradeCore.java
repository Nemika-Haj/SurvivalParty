package dev.bytestobits.survivalparty.commands;

import dev.bytestobits.survivalparty.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedList;

enum TradeStatus {
    MISSING_TRADE_ID,
    TRADE_NOT_FOUND,
    ACCEPTER_MISSING_ITEMS,
    TRADER_MISSING_ITEMS,
    TRADER_OFFLINE,
    ACCEPTED
}

public class TradeCore implements CommandExecutor {

    private final LinkedList<Trade> trades = new LinkedList<>();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage("You cannot use this command");
            return true;
        }

        if(args.length < 1) {
            commandSender.sendMessage(Messages.INSTANCE.coloredMessage("&cImproper use of the /trade command, see the manual with /trade help"));
            return true;
        }

        switch(args[0]){
            case "accept":
                acceptTrade(commandSender, args);
                break;
            case "list":
                listTrades(commandSender);
                break;
            case "help":
                displayHelpMessage(commandSender);
                break;
            default:
                addTrade(commandSender, args);
                break;
        }
        return true;
    }

    private void displayHelpMessage(CommandSender commandSender) {
        commandSender.sendMessage(Messages.INSTANCE.coloredMessage("&6/trade list : shows the list of available trades and their IDs"));
        commandSender.sendMessage(Messages.INSTANCE.coloredMessage("&6/trade accept <tradeID>: accepts a trade with the specified trade ID"));
        commandSender.sendMessage(Messages.INSTANCE.coloredMessage("&6When accepting a trade, the first item specified is the item you want, and the second item is the price, i.e what you will give in exchange for that item"));
        commandSender.sendMessage(Messages.INSTANCE.coloredMessage("&6/trade <count> <item name> <count> <item name> : adds a trade to the list of available trades"));
        commandSender.sendMessage(Messages.INSTANCE.coloredMessage("&6When adding a trade, the first item you specify is the item you(the trader) are giving out, and the second item is the price, i.e what you want in exchange for that item"));
    }

    private void addTrade(CommandSender commandSender, String[] args) {
        if (Material.getMaterial(args[1].toUpperCase()) == null) {
            commandSender.sendMessage(Messages.INSTANCE.coloredMessage("&cThe first item you entered(" + args[1] + ") does not exist"));
            return;
        } else if(Material.getMaterial(args[3].toUpperCase()) == null){
            commandSender.sendMessage(Messages.INSTANCE.coloredMessage("&cThe second item you entered(" + args[3] + ") does not exist"));
            return;
        }

        ItemStack offer = new ItemStack(Material.getMaterial(args[1].toUpperCase()));
        offer.setAmount(Integer.parseInt(args[0]));
        ItemStack price = new ItemStack(Material.getMaterial(args[3].toUpperCase()));
        price.setAmount(Integer.parseInt(args[2]));
        trades.add(new Trade(commandSender.getName(), offer, price));
    }

    private void listTrades(CommandSender commandSender) {
        if(trades.size() == 0) {
            commandSender.sendMessage(Messages.INSTANCE.coloredMessage("&eThere are currently no trades available"));
        }else{
            commandSender.sendMessage(Messages.INSTANCE.coloredMessage("&6Trades List:"));
            for(int i = 0; i < trades.size(); i++) {
                String offerItem = trades.get(i).offer.getType().toString();
                String priceItem = trades.get(i).price.getType().toString();
                int offerCount = trades.get(i).offer.getAmount();
                int priceCount = trades.get(i).price.getAmount();
                commandSender.sendMessage(Messages.INSTANCE.coloredMessage("&b" + i + ")   &6" + offerCount + " " + offerItem + " for " + priceCount + " " + priceItem));
            }
        }
    }

    private void displayStatusMessage(Player offeringPlayer, Player acceptingPlayer, TradeStatus status) {
        switch(status) {
            case MISSING_TRADE_ID:
                acceptingPlayer.sendMessage(Messages.INSTANCE.coloredMessage("&cPlease specify the ID of the trade you want to accept"));
                acceptingPlayer.sendMessage(Messages.INSTANCE.coloredMessage("&cUse /trade list to view available trades"));
                break;
            case TRADE_NOT_FOUND:
                acceptingPlayer.sendMessage(Messages.INSTANCE.coloredMessage("&cNo trade with that ID was found"));
                break;
            case ACCEPTER_MISSING_ITEMS:
                acceptingPlayer.sendMessage(Messages.INSTANCE.coloredMessage("&cYou do not have enough items to accept this trade"));
                break;
            case TRADER_MISSING_ITEMS:
                acceptingPlayer.sendMessage(Messages.INSTANCE.coloredMessage("&eThe trader doesn't have the items to accept this trade"));
                offeringPlayer.sendMessage(Messages.INSTANCE.coloredMessage("&cSomeone has tried to accept your trade but you don't have the offered items in your inventory"));
                break;
            case TRADER_OFFLINE:
                acceptingPlayer.sendMessage(Messages.INSTANCE.coloredMessage("&cThe player is offline"));
                break;
            case ACCEPTED:
                acceptingPlayer.sendMessage(Messages.INSTANCE.coloredMessage("&aYou have successfully accepted the trade!"));
                offeringPlayer.sendMessage(Messages.INSTANCE.coloredMessage("&a" + acceptingPlayer.getName() + " has accepted your trade!"));
                break;
        }
    }

    private void acceptTrade(CommandSender commandSender, String[] args) {
        int tradeID = 0;
        TradeStatus status = TradeStatus.ACCEPTED;
        if (args.length < 2) {
            status = TradeStatus.MISSING_TRADE_ID;
        } else {
            tradeID = Integer.parseInt(args[1]);
        }

        if (trades.size() <= tradeID) {
            status = TradeStatus.TRADE_NOT_FOUND;
        }else{
            // The actual trading code
            Player acceptingPlayer = (Player)commandSender;

            int amount = trades.get(tradeID).price.getAmount();

            // Check if the accepting player has the items to accept the trade
            for(int i = 0; i < acceptingPlayer.getInventory().getSize(); i++) {
                ItemStack item = acceptingPlayer.getInventory().getItem(i);
                if(item != null) {
                    if(item.getType() == trades.get(tradeID).price.getType()) {
                        amount -= item.getAmount();
                    }
                }
            }

            if(amount > 0) {

                status = TradeStatus.ACCEPTER_MISSING_ITEMS;
            }

            // Check if the offering player has the items to accept the trade
            Player offeringPlayer = Bukkit.getServer().getPlayerExact(trades.get(tradeID).traderName);

            amount = trades.get(tradeID).offer.getAmount();

            if(offeringPlayer != null) {
                for(ItemStack item : offeringPlayer.getInventory().getContents()) {
                    if(item != null) {
                        if (item.getType() == trades.get(tradeID).offer.getType()) {
                            amount -= item.getAmount();
                        }
                    }
                }
            }else{
                status = TradeStatus.TRADER_OFFLINE;
            }

            if(amount > 0) {
                status = TradeStatus.TRADER_MISSING_ITEMS;
            }

            offeringPlayer.getInventory().removeItem(trades.get(tradeID).offer);
            offeringPlayer.getInventory().addItem(trades.get(tradeID).price);
            acceptingPlayer.getInventory().removeItem(trades.get(tradeID).price);
            acceptingPlayer.getInventory().addItem(trades.get(tradeID).offer);
            trades.remove(tradeID);
            displayStatusMessage(offeringPlayer, acceptingPlayer, status);
        }
    }
}
