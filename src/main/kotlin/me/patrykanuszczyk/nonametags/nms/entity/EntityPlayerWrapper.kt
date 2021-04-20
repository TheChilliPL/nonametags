package me.patrykanuszczyk.nonametags.nms.entity

import me.patrykanuszczyk.nonametags.nms.NMS
import me.patrykanuszczyk.nonametags.nms.network.PlayerConnectionWrapper

internal class EntityPlayerWrapper(handle: Any): EntityWrapper() {
    private companion object {
        private val entityPlayerClass by lazy { NMS.getClass("EntityPlayer") }
        private val playerConnectionField by lazy { entityPlayerClass.getField("playerConnection") }
    }

    override val instance = handle

    val playerConnection by lazy { PlayerConnectionWrapper(playerConnectionField.get(handle)) }
}