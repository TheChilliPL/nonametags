package me.patrykanuszczyk.nonametags.nms.entity

import me.patrykanuszczyk.nonametags.nms.NMS
import me.patrykanuszczyk.nonametags.nms.world.WorldServerWrapper

class EntityArmorStandWrapper(world: WorldServerWrapper, x: Double, y: Double, z: Double): EntityLivingWrapper() {
    private companion object {
        private val entityArmorStandClass by lazy { NMS.getClass("EntityArmorStand") }
        private val entityArmorStandConstructor by lazy {
            entityArmorStandClass.getConstructor(
                WorldServerWrapper.worldServerClass,
                Double::class.java,
                Double::class.java,
                Double::class.java)
        }
    }

    override val instance by lazy { entityArmorStandConstructor.newInstance(world.handle, x, y, z) }
}