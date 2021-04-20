package me.patrykanuszczyk.nonametags.nms.world

import me.patrykanuszczyk.nonametags.nms.NMS

internal open class WorldWrapper(internal val handle: Any) {
    internal companion object {
        internal val worldClass by lazy { NMS.getClass("World") }
    }
}