package me.patrykanuszczyk.nonametags.obc

import org.bukkit.Bukkit

internal object OBC {
    private val packageName: String by lazy {
        "org.bukkit.craftbukkit.$version"
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