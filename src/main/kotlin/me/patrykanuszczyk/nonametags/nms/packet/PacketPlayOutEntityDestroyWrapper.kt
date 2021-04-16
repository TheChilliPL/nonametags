package me.patrykanuszczyk.nonametags.nms.packet

import me.patrykanuszczyk.nonametags.nms.NMS

class PacketPlayOutEntityDestroyWrapper(id: Int): PacketWrapper() {
    private companion object {
        private val packetPlayOutEntityDestroyClass by lazy { NMS.getClass("PacketPlayOutEntityDestroy") }
        private val packetPlayOutEntityDestroyConstructor by lazy {
            packetPlayOutEntityDestroyClass.getConstructor(IntArray::class.javaPrimitiveType)
        }
    }

    override val packet by lazy { packetPlayOutEntityDestroyConstructor.newInstance(IntArray(id)) }
}