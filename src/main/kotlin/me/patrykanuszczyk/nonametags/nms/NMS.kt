package me.patrykanuszczyk.nonametags.nms

import org.bukkit.Bukkit

internal object NMS {
    private val packageName: String by lazy {
        "net.minecraft.server.$version"
    }

    private val version: String by lazy {
        Bukkit.getServer().javaClass.`package`.name.split(".")[3]
    }

    fun getClass(className: String): Class<*> {
        return Class.forName("$packageName.$className")
    }

    fun getClass(localPackageName: String, className: String): Class<*> {
        return Class.forName("$packageName.$localPackageName.$className")
    }
}