package me.patrykanuszczyk.nonametags.nms.packet

import me.patrykanuszczyk.nonametags.nms.NMS
import me.patrykanuszczyk.nonametags.nms.entity.EntityLivingWrapper

internal class PacketPlayOutSpawnEntityLivingWrapper(entity: EntityLivingWrapper): PacketWrapper() {
    private companion object {
        private val packetPlayOutSpawnEntityLivingClass by lazy {
            NMS.getClass("PacketPlayOutSpawnEntityLiving")
        }
        private val packetPlayOutSpawnEntityLivingConstructor by lazy {
            packetPlayOutSpawnEntityLivingClass.getConstructor(EntityLivingWrapper.entityLivingClass)
        }
    }

    override val packet by lazy { packetPlayOutSpawnEntityLivingConstructor.newInstance(entity.instance) }
}