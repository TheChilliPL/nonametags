package me.patrykanuszczyk.nonametags.nms.network

import io.netty.channel.Channel
import me.patrykanuszczyk.nonametags.nms.NMS

internal class NetworkManagerWrapper(private val handle: Any) {
    private companion object {
        private val networkManagerClass by lazy { NMS.getClass("NetworkManager") }
        private val channelField by lazy { networkManagerClass.getField("channel") }
    }

    val channel by lazy { channelField.get(handle) as Channel }
}