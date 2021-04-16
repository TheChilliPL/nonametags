package me.patrykanuszczyk.nonametags.nms.network

import me.patrykanuszczyk.nonametags.nms.NMS
import me.patrykanuszczyk.nonametags.nms.packet.PacketWrapper

class PlayerConnectionWrapper(private val handle: Any) {
    private companion object {
        private val playerConnectionClass by lazy { NMS.getClass("PlayerConnection") }
        private val networkManagerField by lazy { playerConnectionClass.getField("networkManager") }
        private val sendPacketMethod by lazy { playerConnectionClass.getMethod("sendPacket", PacketWrapper.packetClass) }
    }

    val networkManager by lazy { NetworkManagerWrapper(networkManagerField.get(handle)) }

    fun sendPacket(packetWrapper: PacketWrapper) {
        sendPacketMethod.invoke(handle, packetWrapper.packet)
    }
}