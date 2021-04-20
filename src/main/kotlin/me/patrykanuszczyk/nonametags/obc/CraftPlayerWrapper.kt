package me.patrykanuszczyk.nonametags.obc

import me.patrykanuszczyk.nonametags.nms.entity.EntityPlayerWrapper
import org.bukkit.entity.Player

internal class CraftPlayerWrapper(private val player: Player) {
    private companion object {
        private val craftPlayerClass by lazy { OBC.getClass("entity", "CraftPlayer") }
        private val getHandleMethod by lazy { craftPlayerClass.getMethod("getHandle") }
    }

    val handle by lazy {
        EntityPlayerWrapper(getHandleMethod(player))
    }
}