package dev.bytestobits.survivalparty.commands

import dev.bytestobits.survivalparty.SurvivalParty
import dev.bytestobits.survivalparty.utils.Checks
import dev.bytestobits.survivalparty.utils.Messages
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

class PartyCore(private val plugin: SurvivalParty): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if(sender !is Player){
            sender.sendMessage("You cannot do this")
            return true
        }

        if (args != null && args.isNotEmpty()) {
            when(args.first().lowercase()) {
                "info" -> PartyInfo(sender)
                "create" -> PartyCreate(sender, args.copyOfRange(1, args.size))
                "setbase" -> PartySetBase(sender)
                "base" -> PartyBaseTP(sender)
                "tp" -> PartyMemberTP(sender)
                "notes" -> PartyNotes(sender)
                "addnote" -> PartyAddNote(sender, args.copyOfRange(1, args.size))
                "delnote" -> PartyDeleteNote(sender, args[1])
                else -> sender.sendMessage(Messages.PartyMainMissingArgs)
            }

        } else {
            sender.sendMessage(Messages.PartyMainMissingArgs)
        }

        return true
    }

    private fun PartyAddNote(player: Player, note: Array<out String>) {
        val party = Checks.getParty(plugin.config, player) ?: return player.sendMessage(Messages.PartyInfoNotInParty)
        if(note.isEmpty()) return player.sendMessage(Messages.PartyNoteMissing)

        val notes = party.second.getStringList("notes")
        notes.add(note.joinToString(" "))
        party.second.set("notes", notes)
        plugin.saveConfig()

        player.sendMessage(Messages.PartyNotesAdded)
    }

    private fun PartyDeleteNote(player: Player, noteID: String?) {
        val party = Checks.getParty(plugin.config, player) ?: return player.sendMessage(Messages.PartyInfoNotInParty)
        if(noteID == null) return player.sendMessage(Messages.PartyNoteIDMissing)

        val notes = party.second.getStringList("notes").filterIndexed { index, note -> index.toString() != noteID  }
        if (notes.size == party.second.getStringList("notes").size) return player.sendMessage(Messages.PartyNoteIDMissing)

        party.second.set("notes", notes)
        plugin.saveConfig()
        player.sendMessage(Messages.PartyNoteDeleted(noteID))
    }

    private fun PartyNotes(player: Player) {
        val party = Checks.getParty(plugin.config, player) ?: return player.sendMessage(Messages.PartyInfoNotInParty)
        val notes = party.second.getStringList("notes")

        if(notes.isEmpty()) return player.sendMessage(Messages.PartyNotesEmpty)

        player.sendMessage(Messages.coloredMessage("&5&oYour Party Notes:\n") + notes.joinToString("\n") { Messages.coloredMessage("&7#${notes.indexOf(it)} - &e$it") })
    }

    private fun PartyMemberTP(player: Player) {
        val party = Checks.getParty(plugin.config, player) ?: return player.sendMessage(Messages.PartyInfoNotInParty)

        val partyMember = plugin.server.getPlayer(UUID.fromString(party.first)) ?: return player.sendMessage(Messages.PartyMemberTPNotOnline)

        player.teleport(partyMember.location)
        player.sendMessage(Messages.PartyMemberTPSuccess)
    }

    private fun PartyBaseTP(player: Player) {
        val party = Checks.getParty(plugin.config, player) ?: return player.sendMessage(Messages.PartyInfoNotInParty)

        if(party.second.getLocation("base") == null) return player.sendMessage(Messages.PartyBaseTPNotFound)
        else {
            player.teleport(party.second.getLocation("base")!!)
            player.sendMessage(Messages.PartyBaseTPSuccess)
        }
    }

    private fun PartySetBase(player: Player) {
        val party = Checks.getParty(plugin.config, player) ?: return player.sendMessage(Messages.PartyInfoNotInParty)

//        plugin.config.set("parties.${if(Checks.playerHasParty(plugin.config, player).second) player.uniqueId else party.first}.base", player.location)
        party.second.set("base", player.location)
        plugin.saveConfig()
        player.sendMessage(Messages.PartySetBaseSuccess)
    }

    private fun PartyInfo(player: Player) {
        val party = Checks.getParty(plugin.config, player) ?: return player.sendMessage(Messages.PartyInfoNotInParty)

        player.sendMessage(Messages.PartyInfoMessage(
            plugin.server.getOfflinePlayer(UUID.fromString(party.first)).name ?: "None", party.second.getLocation("base")))
    }

    private fun PartyCreate(player: Player, args: Array<out String>) {
        if(Checks.playerHasParty(plugin.config, player).first) return player.sendMessage(Messages.PartyCreateAlreadyInParty)

        if(args.isEmpty()) return player.sendMessage(Messages.PartyCreateMemberNotProvided)

        val partyMember = plugin.server.getPlayer(args.first()) ?: return player.sendMessage(Messages.PartyCreateMemberNotFound(args.first()))

        if(Checks.playerHasParty(plugin.config, partyMember).first) return player.sendMessage(Messages.PartyCreateMemberHasParty(partyMember.name))

        plugin.config.set("parties.${player.uniqueId}.member", partyMember.uniqueId.toString())
        plugin.saveConfig()

        player.sendMessage(Messages.PartyCreateSuccessful(partyMember.name))
        partyMember.sendMessage(Messages.PartyCreateSuccessful(player.name))

    }

}