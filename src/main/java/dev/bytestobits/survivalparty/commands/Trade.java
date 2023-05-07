package dev.bytestobits.survivalparty.commands;

import org.bukkit.inventory.ItemStack;

public class Trade {

    public String traderName;
    public ItemStack offer;
    public ItemStack price;

    public Trade(String traderName, ItemStack offer, ItemStack price) {
        this.traderName = traderName;
        this.offer = offer;
        this.price = price;
    }

}
