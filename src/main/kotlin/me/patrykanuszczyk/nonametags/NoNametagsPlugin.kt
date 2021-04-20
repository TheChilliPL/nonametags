package me.patrykanuszczyk.nonametags

import org.bukkit.plugin.java.JavaPlugin

class NoNametagsPlugin : JavaPlugin() {
    lateinit var executor: NoNametagsExecutor

    override fun onEnable() {
        instance = this

        logger.info("Plugin enabled.")

        executor = NoNametagsExecutor.new(this)

        server.pluginManager.registerEvents(executor, this)

        saveDefaultConfig()

        NoNametagsCommand(this).also {
            getCommand("nonametags")?.apply {
                setExecutor(it)
            }
        }

        executor.getFromConfig()
    }

    override fun onDisable() {
        executor.close()
    }

    companion object {
        var instance: NoNametagsPlugin? = null
            internal set
    }
}