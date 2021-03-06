package me.patrykanuszczyk.nonametags.nms.entity

import me.patrykanuszczyk.nonametags.nms.NMS

abstract class EntityLivingWrapper: EntityWrapper() {
    internal companion object {
        internal val entityLivingClass by lazy { NMS.getClass("EntityLiving") }
    }
}