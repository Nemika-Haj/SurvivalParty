package dev.bytestobits.survivalparty

import dev.bytestobits.survivalparty.commands.PartyCore
import dev.bytestobits.survivalparty.commands.Whitelist
import dev.bytestobits.survivalparty.events.WhitelistCheck
import org.bukkit.Bukkit
import dev.bytestobits.survivalparty.commands.TradeCore
import org.bukkit.plugin.java.JavaPlugin

class SurvivalParty : JavaPlugin() {
    override fun onEnable() {
        saveDefaultConfig()

        getCommand("party")?.setExecutor(PartyCore(this))
        getCommand("whitelist")?.setExecutor(Whitelist(this))
        Bukkit.getPluginManager().registerEvents(WhitelistCheck(this), this)
        getCommand("trade")?.setExecutor(TradeCore());
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}