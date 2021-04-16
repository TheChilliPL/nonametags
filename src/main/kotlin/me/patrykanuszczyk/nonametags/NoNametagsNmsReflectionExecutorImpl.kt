package me.patrykanuszczyk.nonametags

import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import me.patrykanuszczyk.nonametags.nms.entity.EntityArmorStandWrapper
import me.patrykanuszczyk.nonametags.nms.packet.*
import me.patrykanuszczyk.nonametags.obc.CraftPlayerWrapper
import me.patrykanuszczyk.nonametags.obc.CraftWorldWrapper
import org.bukkit.entity.Player
import java.util.*

class NoNametagsNmsReflectionExecutorImpl(plugin: NoNametagsPlugin) : NoNametagsNmsExecutor(plugin) {
    private val stands = mutableMapOf<Player, EntityArmorStandWrapper>()

    private fun getArmorStand(player: Player): EntityArmorStandWrapper {
        if(stands.containsKey(player)) return stands[player]!!

        val nmsWorld = CraftWorldWrapper(player.world).handle
        val stand = EntityArmorStandWrapper(nmsWorld, .0, .0, .0).apply {
            //TODO isInvisible = true
            isInvulnerable = true
            isMarker = true
            customNameVisible = false
        }
        stands[player] = stand
        return stand
    }

    override fun injectPlayer(player: Player) {
        val handler = object : ChannelDuplexHandler() {
            override fun write(
                ctx: ChannelHandlerContext?,
                msg: Any?,
                promise: ChannelPromise?
            ) {
                if (msg?.javaClass?.name == "PacketPlayOutNamedEntitySpawn") {
                    val uuidField = msg.javaClass.getDeclaredField("b")
                    uuidField.isAccessible = true
                    val uuid = uuidField.get(msg) as UUID
                    val spawnedPlayer = plugin.server.getPlayer(uuid) ?: return

                    plugin.logger.info(
                        spawnedPlayer.displayName
                            + " spawning for "
                            + player.displayName
                    )

                    if(shouldNametagBeHidden(spawnedPlayer, player))
                        plugin.server.scheduler.runTask(plugin) { ->
                            removeNametag(spawnedPlayer, player)
                        }
                }
                super.write(ctx, msg, promise)
            }
        }

        CraftPlayerWrapper(player).handle.playerConnection.networkManager.channel
            .pipeline().addBefore("packet_handler", player.name, handler)
    }

    override fun removePlayer(player: Player) {
        val channel = CraftPlayerWrapper(player).handle.playerConnection
            .networkManager.channel
        channel.eventLoop().submit {
            channel.pipeline().remove(player.name)
        }
    }

    override fun createHidingPackets(player: Player): List<Any> {
        val armorStand = getArmorStand(player)
        val spawnPacket = PacketPlayOutSpawnEntityLivingWrapper(armorStand)
        val metadataPacket = PacketPlayOutEntityMetadataWrapper(armorStand.id, armorStand.dataWatcher, true)
        val mountPacket = PacketPlayOutMountWrapper(CraftPlayerWrapper(player).handle)

        val newPassengerIds = IntArray(mountPacket.passengerIds.size + 1)
        mountPacket.passengerIds.copyInto(newPassengerIds)
        newPassengerIds[mountPacket.passengerIds.size] = armorStand.id
        mountPacket.passengerIds = newPassengerIds

        return listOf(spawnPacket, metadataPacket, mountPacket)
    }

    override fun createShowingPackets(player: Player): List<Any> {
        val armorStand = getArmorStand(player)
        val destroyPacket = PacketPlayOutEntityDestroyWrapper(armorStand.id)

        return listOf(destroyPacket)
    }

    override fun sendPackets(packets: List<Any>, player: Player) {
        val connection = CraftPlayerWrapper(player).handle.playerConnection

        packets.forEach { connection.sendPacket(it as PacketWrapper) }
    }
}