package me.patrykanuszczyk.nonametags.nms.packet

import me.patrykanuszczyk.nonametags.nms.NMS

internal class PacketPlayOutEntityDestroyWrapper(vararg id: Int): PacketWrapper() {
    private companion object {
        private val packetPlayOutEntityDestroyClass by lazy { NMS.getClass("PacketPlayOutEntityDestroy") }
        private val packetPlayOutEntityDestroyConstructor by lazy {
            packetPlayOutEntityDestroyClass.getConstructor(IntArray::class.java)
        }
    }

    override val packet: Any by lazy { packetPlayOutEntityDestroyConstructor.newInstance(id) }
}