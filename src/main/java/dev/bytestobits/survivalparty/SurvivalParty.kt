package dev.bytestobits.survivalparty

import dev.bytestobits.survivalparty.commands.PartyCore
import org.bukkit.plugin.java.JavaPlugin

class SurvivalParty : JavaPlugin() {
    override fun onEnable() {
        saveDefaultConfig()

        getCommand("party")?.setExecutor(PartyCore(this))
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}