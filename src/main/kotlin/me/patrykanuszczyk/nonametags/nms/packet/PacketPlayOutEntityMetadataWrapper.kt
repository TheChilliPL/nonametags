package me.patrykanuszczyk.nonametags.nms.packet

import me.patrykanuszczyk.nonametags.nms.NMS

class PacketPlayOutEntityMetadataWrapper(id: Int, dataWatcher: Any, var1: Boolean): PacketWrapper() {
    private companion object {
        private val packetPlayOutEntityMetadataClass by lazy { NMS.getClass("PacketPlayOutEntityMetadata") }
        private val dataWatcherClass by lazy { NMS.getClass("DataWatcher") }
        private val packetPlayOutEntityMetadataConstructor by lazy {
            packetPlayOutEntityMetadataClass.getConstructor(Int::class.javaPrimitiveType, dataWatcherClass, Boolean::class.javaPrimitiveType)
        }
    }

    override val packet by lazy { packetPlayOutEntityMetadataConstructor.newInstance(id, dataWatcher, var1) }
}