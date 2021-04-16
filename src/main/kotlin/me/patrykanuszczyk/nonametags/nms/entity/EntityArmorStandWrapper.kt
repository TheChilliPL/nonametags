package me.patrykanuszczyk.nonametags.nms.entity

import me.patrykanuszczyk.nonametags.nms.NMS
import me.patrykanuszczyk.nonametags.nms.world.WorldServerWrapper
import me.patrykanuszczyk.nonametags.nms.world.WorldWrapper

class EntityArmorStandWrapper(world: WorldServerWrapper, x: Double, y: Double, z: Double): EntityLivingWrapper() {
    private companion object {
        private val entityArmorStandClass by lazy { NMS.getClass("EntityArmorStand") }
        private val entityArmorStandConstructor by lazy {
            entityArmorStandClass.getConstructor(
                WorldWrapper.worldClass,
                Double::class.javaPrimitiveType,
                Double::class.javaPrimitiveType,
                Double::class.javaPrimitiveType)
        }
    }

    override val instance by lazy { entityArmorStandConstructor.newInstance(world.handle, x, y, z) }
}