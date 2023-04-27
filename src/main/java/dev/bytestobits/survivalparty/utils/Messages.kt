package dev.bytestobits.survivalparty.utils

import org.bukkit.ChatColor
import org.bukkit.Location

object Messages {

    fun coloredMessage(message: String) = message.replace("&", "§")

    val PartyMainMissingArgs = listOf(
        "&a&lParty Manager",
        "&7&m----------------------",
        "&e/party info &7- Check your party's info",
        "&e/party base &7- Teleport to your party's base",
        "&e/party setbase &7- Set your party's base",
        "&e/party tp &7- Teleport to your party member's location",
        "&e/party notes &7- Check your party's notes",
        "&e/party addnote <note> &7- Add a note to your party",
        "&e/party delnote <note_id> &7- Delete a party note"
    ).joinToString("\n") { coloredMessage(it) }

    val PartyCreateAlreadyInParty = coloredMessage("&cYou already belong in a party!")
    val PartyCreateMemberNotProvided = coloredMessage("&cProvide a &eParty Member &cto create a party with.")
    fun PartyCreateMemberNotFound(member: String) = coloredMessage("&cCould not find member &e$member&c.")
    fun PartyCreateMemberHasParty(member: String) = coloredMessage("&e$member &calready belongs in a party.")
    fun PartyCreateSuccessful(member: String) = coloredMessage("&aYou now are in a party with &e$member&a!")

    val PartyInfoNotInParty = coloredMessage("&cYou do not belong in a party!")
    fun PartyInfoMessage(member: String, base: Location?) = listOf(
        "&e&lParty Info",
        "&7&m----------------------",
        "&7You are in a party with &e$member",
        "&7Your base is ${if(base == null) "&cnot set&7. &eUse /party setbase" else "at &e${base.blockX}, ${base.blockY}, ${base.blockZ}&7!"}"
    ).joinToString("\n") { coloredMessage(it) }

    val PartySetBaseSuccess = coloredMessage("&aYou successfully set your Party's base location.")

    val PartyBaseTPNotFound = coloredMessage("&cYour party does not have a base yet. &eUse /party setbase")
    val PartyBaseTPSuccess = coloredMessage("&aYou teleported to your party's base!")

    val PartyMemberTPNotOnline = coloredMessage("&cYour party member is not currently online.")
    val PartyMemberTPSuccess = coloredMessage("&aYou teleported to your party member!")

    val PartyNotesEmpty = coloredMessage("&cYou do not have any party notes. &eUse /party addnote <note>")
    val PartyNoteMissing = coloredMessage("&cProvide a valid &eNote &cto add.")
    val PartyNotesAdded = coloredMessage("&aYou added a note to your party!")
    val PartyNoteIDMissing = coloredMessage("&cProvide a valid &eNote ID &cto remove.")
    fun PartyNoteDeleted(noteID: String) = coloredMessage("&cYou deleted &eNote #$noteID&c.")

}