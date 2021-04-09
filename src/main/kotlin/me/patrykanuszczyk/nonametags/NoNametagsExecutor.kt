package me.patrykanuszczyk.nonametags

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import java.io.Closeable
import java.util.*

typealias NametagMatrix = Map<Player, Set<Player>>

abstract class NoNametagsExecutor(val plugin: NoNametagsPlugin)
    : Listener, Closeable
{
    private var config =
        plugin.config.getConfigurationSection("nonametags")
            ?: plugin.config.createSection("nonametags")
    private var _defaultHideNametags = false
    private var _playerNametagHidden = mutableMapOf<UUID, Boolean>()
    private var _playerDoesntSeeNametags = mutableMapOf<UUID, Boolean>()
    private var _nametagOverrides
        = mutableMapOf<UUID, MutableMap<UUID, Boolean>>()

    var hideNametagsByDefault: Boolean
        get() = _defaultHideNametags
        set(value) {
            val m1 = getNametagMatrix()
            _defaultHideNametags = value
            val m2 = getNametagMatrix()

            config["default"] = value
            plugin.saveConfig()

            updateHidden(m1, m2)
        }

    fun getPlayerNametagHidden(uuid: UUID) = _playerNametagHidden[uuid]
    fun getPlayerNametagHidden(player: Player)
        = getPlayerNametagHidden(player.uniqueId)

    fun setPlayerNametagHidden(uuid: UUID, hidden: Boolean?) {
        val m1 = getNametagMatrix()
        when(hidden) {
            null -> _playerNametagHidden.remove(uuid)
            else -> _playerNametagHidden[uuid] = hidden
        }
        val m2 = getNametagMatrix()

        config["player"] = _playerNametagHidden.mapKeys { it.key.toString() }
        plugin.saveConfig()

        updateHidden(m1, m2)
    }
    fun setPlayerNametagHidden(player: Player, hidden: Boolean?)
        = setPlayerNametagHidden(player.uniqueId, hidden)

    fun getPlayerDoesntSeeNametags(uuid: UUID) = _playerDoesntSeeNametags[uuid]
    fun getPlayerDoesntSeeNametags(player: Player)
        = getPlayerDoesntSeeNametags(player.uniqueId)

    fun setPlayerDoesntSeeNametags(uuid: UUID, seesNametags: Boolean?) {
        val m1 = getNametagMatrix()
        when(seesNametags) {
            null -> _playerDoesntSeeNametags.remove(uuid)
            else -> _playerDoesntSeeNametags[uuid] = seesNametags
        }
        val m2 = getNametagMatrix()

        config["seeall"] = _playerDoesntSeeNametags.mapKeys { it.key.toString() }
        plugin.saveConfig()

        updateHidden(m1, m2)
    }
    fun setPlayerDoesntSeeNametags(player: Player, seesNametags: Boolean?)
        = setPlayerDoesntSeeNametags(player.uniqueId, seesNametags)

    fun getNametagOverride(observed: UUID, observer: UUID)
        = _nametagOverrides[observed]?.get(observer)
    fun getNametagOverride(observed: Player, observer: Player)
        = getNametagOverride(observed.uniqueId, observer.uniqueId)

    fun setNametagOverride(
        observed: UUID,
        observer: UUID,
        override: Boolean?
    ) {
        val m1 = getNametagMatrix()

        _nametagOverrides.computeIfAbsent(observed) {
            mutableMapOf()
        }

        when(override) {
            null -> _nametagOverrides[observed]!!.remove(observer)
            else -> _nametagOverrides[observed]!![observer] = override
        }

        config["override"] = _nametagOverrides.map { it ->
            it.key.toString() to it.value.mapKeys { it.key.toString() }
        }.toMap()
        plugin.saveConfig()

        val m2 = getNametagMatrix()

        updateHidden(m1, m2)
    }
    fun setNametagOverride(
        observed: Player,
        observer: Player,
        override: Boolean?
    ) = setNametagOverride(observed.uniqueId, observer.uniqueId, override)

    fun shouldNametagBeHidden(observed: UUID, observer: UUID): Boolean {
        return _nametagOverrides[observed]?.get(observer)
            ?: _playerDoesntSeeNametags[observer]
            ?: _playerNametagHidden[observed]
            ?: _defaultHideNametags
    }
    fun shouldNametagBeHidden(observed: Player, observer: Player)
        = shouldNametagBeHidden(observed.uniqueId, observer.uniqueId)

    private fun getNametagMatrix(): NametagMatrix {
        val matrix = mutableMapOf<Player, Set<Player>>()
        for(observer in plugin.server.onlinePlayers) {
            val set = mutableSetOf<Player>()
            for(observed in plugin.server.onlinePlayers) {
                val hidden = shouldNametagBeHidden(
                    observed.uniqueId,
                    observer.uniqueId
                )
                if(hidden) set.add(observed)
            }
            matrix[observer] = set
        }
        return matrix
    }

    fun getFromConfig() {
        fun ConfigurationSection.getUuid(path: String)
            = UUID.fromString(getString(path))
        fun ConfigurationSection.getUuidList(path: String)
            = getStringList(path).map { UUID.fromString(it)!! }
        fun ConfigurationSection.getUuidMap(path: String) =
            (get(path) as Map<*, *>).map {
                UUID.fromString(it.key as String)!! to (it.value as Boolean)
            }.toMap()

        _defaultHideNametags = config.getBoolean("default", true)

        _playerNametagHidden = config.getUuidMap("player").toMutableMap()

        _playerDoesntSeeNametags = config.getUuidMap("seeall").toMutableMap()

        _nametagOverrides = (config.get("override") as Map<*, *>)
            .map { a ->
                UUID.fromString(a.key as String)!! to (a.value as Map<*, *>)
                    .map { b ->
                        UUID.fromString(b.key as String) to (b.value as Boolean)
                    }
                    .toMap().toMutableMap()
            }
            .toMap().toMutableMap()

        updateHidden(emptyMap(), getNametagMatrix())
    }

    abstract fun updateHidden(m1: NametagMatrix, m2: NametagMatrix)

    override fun close() {}
}