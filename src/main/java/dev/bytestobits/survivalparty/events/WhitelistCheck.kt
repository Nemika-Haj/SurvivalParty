package dev.bytestobits.survivalparty.events

import dev.bytestobits.survivalparty.SurvivalParty
import dev.bytestobits.survivalparty.utils.Messages
import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent

class WhitelistCheck(private val plugin: SurvivalParty): Listener {

    @EventHandler()
    fun handlePlayerJoinEvent(event: AsyncPlayerPreLoginEvent) {
        if(plugin.config.getBoolean("whitelist-enabled") == true && event.name !in plugin.config.getStringList("whitelisted")) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, Component.text(Messages.coloredMessage("&7You are not &f&lwhitelisted&7.")))
        }
    }

}