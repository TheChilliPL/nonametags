package me.patrykanuszczyk.nonametags

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class NoNametagsCommand(val plugin: NoNametagsPlugin)
    : CommandExecutor, TabCompleter
{
    private infix fun String.equalsIgnoreCase(other: String)
        = this.equals(other, true)

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        // Command called without arguments
        if(args.isEmpty()) return false
        // If it's called with the first argument being “?”
        if(args[0] == "?") return false
        // or “help”
        if(args[0] equalsIgnoreCase "help") return false

        fun parseBoolean(string: String) = when(string) {
            "show" -> false
            "hide" -> true
            else -> null
        }

        fun Boolean?.toStateText() = when(this) {
            false -> "&aSHOWN&r"
            true -> "&cHIDDEN&r"
            null -> "&bDEFAULT&r"
        }

        when(args[0].toLowerCase()) {
            "default" -> {
                if(args.size != 2) return false

                val state = parseBoolean(args[1]) ?: return false
                val stateText = state.toStateText()

                plugin.executor.hideNametagsByDefault = state

                sender.sendMessage("Default state set to $stateText.")

                return true
            }

            "player" -> {
                if(args.size !in 2..3) return false

                val player = when {
                    args.size == 3 -> plugin.server.getPlayer(args[1])
                    sender is Player -> sender
                    else -> null
                } ?: return false

                val state = parseBoolean(args.last())
                val stateText = state.toStateText()

                plugin.executor.setPlayerNametagHidden(player, state)

                sender.sendMessage("Player's nametag is now $stateText.")
            }

            "seeall" -> {
                if(args.size !in 2..3) return false

                val player = when {
                    args.size == 3 -> plugin.server.getPlayer(args[1])
                    sender is Player -> sender
                    else -> null
                } ?: return false

                val state = parseBoolean(args.last())
                val stateText = state.toStateText()

                plugin.executor.setPlayerDoesntSeeNametags(player, state)

                sender.sendMessage(
                    "For the player, nametags are now $stateText."
                )
            }

            "override" -> {
                if(args.size !in 3..4) return false

                val observer = when {
                    args.size == 4 -> plugin.server.getPlayer(args[1])
                    sender is Player -> sender
                    else -> null
                } ?: return false

                val target = plugin.server.getPlayer(args[args.size - 2])
                    ?: return false

                val state = parseBoolean(args.last())
                val stateText = state.toStateText()

                plugin.executor.setNametagOverride(target, observer, state)

                sender.sendMessage(
                    "For the observer, target's nametag is now $stateText."
                )
            }
        }

        return false
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String>? {
        fun getOnlinePlayerNames(): MutableList<String> {
            return plugin.server.onlinePlayers.map {
                it.name
            }.toMutableList()
        }

        val showHide = listOf("show", "hide")
        val showHideDefault = listOf("show", "hide", "default")

        when(args.size) {
            in 0..1 -> return listOf("help")
            2 -> when(args[0].toLowerCase()) {
                "default" -> return showHide
                in setOf("player", "seeall", "override") ->
                    return getOnlinePlayerNames()
            }
            3 -> when(args[0].toLowerCase()) {
                in setOf("player", "seeall") ->
                    return showHideDefault
                "override" ->
                    return showHideDefault + getOnlinePlayerNames()
            }
            4 -> if(args[0] equalsIgnoreCase "override")
                return getOnlinePlayerNames()
        }

        return null
    }
}