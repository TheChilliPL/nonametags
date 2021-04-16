package me.patrykanuszczyk.nonametags.nms.world

import me.patrykanuszczyk.nonametags.nms.NMS

class WorldServerWrapper(internal val handle: Any) {
    internal companion object {
        internal val worldServerClass by lazy { NMS.getClass("WorldServer") }
    }
}