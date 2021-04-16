package me.patrykanuszczyk.nonametags

import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import net.minecraft.server.v1_14_R1.*
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import java.util.*

class NoNametagsNmsExecutorImpl(plugin: NoNametagsPlugin)
    : NoNametagsNmsExecutor<Packet<PacketListenerPlayOut>>(plugin)
{
    private val stands = mutableMapOf<Player, EntityArmorStand>()

    private fun getArmorStand(player: Player): EntityArmorStand {
        if(stands.containsKey(player)) return stands[player]!!

        val nmsWorld = (player.world as CraftWorld).handle
        val stand = EntityArmorStand(nmsWorld, .0, .0, .0).apply {
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
                if(msg is PacketPlayOutNamedEntitySpawn) {
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

        (player as CraftPlayer).handle.playerConnection.networkManager.channel
            .pipeline().addBefore("packet_handler", player.name, handler)
    }

    override fun removePlayer(player: Player) {
        val channel = (player as CraftPlayer).handle.playerConnection
            .networkManager.channel
        channel.eventLoop().submit {
            channel.pipeline().remove(player.name)
        }
    }

    override fun createHidingPackets(player: Player): List<Packet<PacketListenerPlayOut>> {
        val armorStand = getArmorStand(player)

        val spawnPacket = PacketPlayOutSpawnEntityLiving(armorStand)

        val metadataPacket = PacketPlayOutEntityMetadata(
            armorStand.id, armorStand.dataWatcher, true
        )

        val mountPacket = PacketPlayOutMount((player as CraftPlayer).handle)

        val field = mountPacket.javaClass.getDeclaredField("b")
        field.isAccessible = true
        val oldPassengers = field.get(mountPacket) as IntArray
        val newPassengers = IntArray(oldPassengers.size + 1)
        oldPassengers.copyInto(newPassengers)
        newPassengers[oldPassengers.size] = armorStand.id
        field.set(mountPacket, newPassengers)

        return listOf(spawnPacket, metadataPacket, mountPacket)
    }

    override fun createShowingPackets(player: Player): List<Packet<PacketListenerPlayOut>> {
        val armorStand = getArmorStand(player)

        val destroyPacket = PacketPlayOutEntityDestroy(armorStand.id)

        return listOf(destroyPacket)
    }

    override fun sendPackets(packets: List<Packet<PacketListenerPlayOut>>, player: Player) {
        val conn = (player as CraftPlayer).handle.playerConnection

        packets.forEach { conn.sendPacket(it) }
    }
}