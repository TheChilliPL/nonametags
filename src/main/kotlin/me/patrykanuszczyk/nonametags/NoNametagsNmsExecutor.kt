package me.patrykanuszczyk.nonametags

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.io.Closeable

abstract class NoNametagsNmsExecutor(plugin: NoNametagsPlugin)  :
    NoNametagsExecutor(plugin), Closeable
{
    private val injectedPlayers = mutableSetOf<Player>()

    init {
        for(player in plugin.server.onlinePlayers) {
            injectPlayerFromInit(player)
            injectedPlayers.add(player)
        }
    }

    override fun close() {
        for(player in injectedPlayers) {
            removePlayer(player)
        }
    }

    override fun updateHidden(m1: NametagMatrix, m2: NametagMatrix) {
        for(observed in plugin.server.onlinePlayers) {
            val hidingPackets by lazy { createHidingPackets(observed) }
            val showingPackets by lazy { createShowingPackets(observed) }

            for(observer in plugin.server.onlinePlayers) {
                val hiddenBefore = m1[observer]?.contains(observed)
                val hiddenNow = m2[observer]?.contains(observed)

                when(Pair(hiddenBefore, hiddenNow)) {
                    Pair(true, false) -> sendPackets(showingPackets, observer)
                    Pair(false, true) -> sendPackets(hidingPackets, observer)
                }
            }
        }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        plugin.logger.info("Player joined ${event.player.displayName}")
        val added = injectedPlayers.add(event.player)
        if(!added) return
        injectPlayer(event.player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val removed = injectedPlayers.remove(event.player)
        if(!removed) return
        removePlayer(event.player)
    }

    private fun injectPlayerFromInit(player: Player) = injectPlayer(player)

    abstract fun injectPlayer(player: Player)

    abstract fun removePlayer(player: Player)

    fun removeNametag(player: Player) {
        val packets = createHidingPackets(player)

        player.server.onlinePlayers.forEach {
            sendPackets(packets, it)
        }
    }

    fun removeNametag(player: Player, observer: Player) {
        val packets = createHidingPackets(player)

        sendPackets(packets, observer)
    }

    fun addNametag(player: Player) {
        val packets = createShowingPackets(player)

        player.server.onlinePlayers.forEach {
            sendPackets(packets, it)
        }
    }

    fun addNametag(player: Player, observer: Player) {
        val packets = createShowingPackets(player)

        sendPackets(packets, observer)
    }

    protected abstract fun createHidingPackets(player: Player)
        : List<Any>

    protected abstract fun createShowingPackets(player: Player)
        : List<Any>

    protected abstract fun sendPackets(packets: List<Any>, player: Player)

    companion object {
        fun new(plugin: NoNametagsPlugin): NoNametagsNmsExecutor {
            return NoNametagsNmsReflectionExecutorImpl(plugin)
        }
    }
}