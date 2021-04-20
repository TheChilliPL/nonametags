package me.patrykanuszczyk.nonametags.nms.packet

import me.patrykanuszczyk.nonametags.nms.NMS
import me.patrykanuszczyk.nonametags.nms.entity.EntityWrapper

internal class PacketPlayOutMountWrapper(entity: EntityWrapper): PacketWrapper() {
    private companion object {
        private val packetPlayOutMountClass by lazy { NMS.getClass("PacketPlayOutMount") }
        private val packetPlayOutMountConstructor by lazy {
            packetPlayOutMountClass.getConstructor(EntityWrapper.entityClass)
        }
        private val bField by lazy {
            packetPlayOutMountClass.getDeclaredField("b").apply {
                isAccessible = true
            }
        }
    }

    override val packet by lazy {
        packetPlayOutMountConstructor.newInstance(entity.instance)
    }

    var passengerIds
        get() = bField.get(packet) as IntArray
        set(value) = bField.set(packet, value)
}