package me.patrykanuszczyk.nonametags.nms.packet

import me.patrykanuszczyk.nonametags.nms.NMS

abstract class PacketWrapper {
    internal companion object {
        internal val packetClass by lazy { NMS.getClass("Packet") }
    }

    abstract val packet: Any
}