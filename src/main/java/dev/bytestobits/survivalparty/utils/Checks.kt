package dev.bytestobits.survivalparty.utils

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

object Checks {

    fun playerHasParty(config: FileConfiguration, player: Player): Pair<Boolean, Boolean> {
        if(config.get("parties.${player.uniqueId}") != null) return true to true
        val parties = config.getConfigurationSection("parties")!!
        for(party in parties.getKeys(false)) {
            if(config.get("parties.$party.member") == player.uniqueId.toString()) return true to false
        }

        return false to false
    }

    fun getParty(config: FileConfiguration, player: Player): Pair<String, ConfigurationSection>? {
        if(config.get("parties.${player.uniqueId}") != null) {
            val party = config.getConfigurationSection("parties.${player.uniqueId}")!!
            return party.getString("member")!! to party
        }

        val parties = config.getConfigurationSection("parties")!!
        for(party in parties.getKeys(false)) {
            if(config.get("parties.$party.member") == player.uniqueId.toString()) {
                val partyConfig = config.getConfigurationSection("parties.$party")!!
                return party to partyConfig
            }
        }

        return null
    }

}