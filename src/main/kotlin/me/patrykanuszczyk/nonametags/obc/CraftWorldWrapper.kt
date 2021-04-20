package me.patrykanuszczyk.nonametags.obc

import me.patrykanuszczyk.nonametags.nms.world.WorldServerWrapper
import org.bukkit.World

internal class CraftWorldWrapper(private val world: World) {
    private companion object {
        private val craftWorldClass by lazy { OBC.getClass("CraftWorld") }
        private val getHandleMethod by lazy { craftWorldClass.getMethod("getHandle") }
    }

    val handle by lazy { WorldServerWrapper(getHandleMethod(world)) }
}