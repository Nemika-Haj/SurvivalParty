package dev.bytestobits.survivalparty.commands

import dev.bytestobits.survivalparty.SurvivalParty
import dev.bytestobits.survivalparty.utils.Messages
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class Whitelist(private val plugin: SurvivalParty): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {

        if(args != null && args.isNotEmpty())  {
            when(args.first().lowercase()) {
                "on" -> EnableWhitelist(sender)
                "off" -> DisableWhitelist(sender)
                "add" -> AddMemberToWhitelist(sender, args[1])
                "remove" -> RemoveMemberFromWhitelist(sender, args[1])
                "list" -> ListWhiteMembers(sender)
                else -> return false
            }
        } else {
            return false
        }

        return true
    }

    fun EnableWhitelist(sender: CommandSender) {
        plugin.config.set("whitelist-enabled", true)
        plugin.saveConfig()
        sender.sendMessage(Messages.WhitelistActivated)
    }

    fun DisableWhitelist(sender: CommandSender) {
        plugin.config.set("whitelist-enabled", false)
        plugin.saveConfig()
        sender.sendMessage(Messages.WhitelistDeactivated)
    }

    fun AddMemberToWhitelist(sender: CommandSender, member: String?) {
        if(member == null) return sender.sendMessage(Messages.WhitelistMemberMissing)
        val whitelisted = plugin.config.getStringList("whitelisted")
        if(member !in whitelisted) whitelisted.add(member)
        plugin.config.set("whitelisted", whitelisted)
        plugin.saveConfig()
        sender.sendMessage(Messages.WhitelistMemberAdded(member))
    }

    fun RemoveMemberFromWhitelist(sender: CommandSender, member: String?) {
        if(member == null) return sender.sendMessage(Messages.WhitelistMemberMissing)

        val whitelisted = plugin.config.getStringList("whitelisted")
        whitelisted.remove(member)
        plugin.config.set("whitelisted", whitelisted)
        plugin.saveConfig()
        sender.sendMessage(Messages.WhitelistMemberRemoved(member))
    }

    fun ListWhiteMembers(sender: CommandSender) {
        val whitelisted = plugin.config.getStringList("whitelisted").joinToString(", ")

        sender.sendMessage(Messages.coloredMessage("&7Current Whitelist Members: &r$whitelisted"))
    }

}