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

        if (args[0].equals("accept")) {
            // args[1] must be trade id
            int tradeID = 0;
            if (args.length < 2) {
                commandSender.sendMessage(Messages.INSTANCE.coloredMessage("&cPlease specify the ID of the trade you want to accept"));
                commandSender.sendMessage(Messages.INSTANCE.coloredMessage("&cUse /trade list to view available trades"));
                return true;
            } else {
                tradeID = Integer.parseInt(args[1]);
            }

            if (trades.size() <= tradeID) {
                commandSender.sendMessage(ChatColor.RED + "No trade with that ID was found");
                return true;
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
                    commandSender.sendMessage(Messages.INSTANCE.coloredMessage("&cYou do not have enough items to accept this trade"));
                    return true;
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
                    commandSender.sendMessage(Messages.INSTANCE.coloredMessage("&cThe player is offline"));
                    return true;
                }

                if(amount > 0) {
                    commandSender.sendMessage(Messages.INSTANCE.coloredMessage("&eThe trader doesn't have the items to accept this trade"));
                    offeringPlayer.sendMessage(Messages.INSTANCE.coloredMessage("&c" + acceptingPlayer.getName() + " has tried to accept your trade but you don't have the offered items in your inventory"));
                    return true;
                }

                offeringPlayer.getInventory().removeItem(trades.get(tradeID).offer);
                offeringPlayer.getInventory().addItem(trades.get(tradeID).price);
                acceptingPlayer.getInventory().removeItem(trades.get(tradeID).price);
                acceptingPlayer.getInventory().addItem(trades.get(tradeID).offer);
                trades.remove(tradeID);
                commandSender.sendMessage(Messages.INSTANCE.coloredMessage("&aYou have successfully accepted the trade!"));
                offeringPlayer.sendMessage(Messages.INSTANCE.coloredMessage("&a" + acceptingPlayer.getName() + " has accepted your trade!"));
            }
            // If player found, check if requirements are met and accept the trade
            // If player is not found, send error message
        } else if (args[0].equals("list")) {
            if(trades.size() == 0) {
                commandSender.sendMessage("There are currently no trades available");
            }
            for(int i = 0; i < trades.size(); i++) {
                String offerItem = trades.get(i).offer.getType().toString();
                String priceItem = trades.get(i).price.getType().toString();
                int offerCount = trades.get(i).offer.getAmount();
                int priceCount = trades.get(i).price.getAmount();
                commandSender.sendMessage(i + ") " + offerCount + " " + offerItem + " for " + priceCount + " " + priceItem);
            }
        } else {
            // If args[0] is not accept then it must be an item
            // if args[0] is an item, get the item count in args[1]
            if (Material.getMaterial(args[1].toUpperCase()) == null) {
                commandSender.sendMessage(Messages.INSTANCE.coloredMessage("&cThe first item you entered(" + args[1] + ") does not exist"));
            } else if(Material.getMaterial(args[3].toUpperCase()) == null){
                commandSender.sendMessage(Messages.INSTANCE.coloredMessage("&cThe second item you entered(" + args[3] + ") does not exist"));
            }else{
                ItemStack offer = new ItemStack(Material.getMaterial(args[1].toUpperCase()));
                offer.setAmount(Integer.parseInt(args[0]));
                ItemStack price = new ItemStack(Material.getMaterial(args[3].toUpperCase()));
                price.setAmount(Integer.parseInt(args[2]));
                trades.add(new Trade(commandSender.getName(), offer, price));
            }

        }

        return true;
    }
}
