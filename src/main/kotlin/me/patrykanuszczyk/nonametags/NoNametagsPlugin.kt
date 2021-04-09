package me.patrykanuszczyk.nonametags

import org.bukkit.plugin.java.JavaPlugin

class NoNametagsPlugin : JavaPlugin() {
    lateinit var executor: NoNametagsExecutor

    override fun onEnable() {
        logger.info("Plugin enabled.")

        executor = NoNametagsNmsExecutor.new(this)

        server.pluginManager.registerEvents(executor, this)

        saveDefaultConfig()

        NoNametagsCommand(this).also {
            getCommand("nonametags")?.apply {
                setExecutor(it)
            }/* ?: throw NullPointerException(
                "Didn't find the /nonametags command."
            ) shouldn't happen with the current plugin.yml*/
        }

        executor.getFromConfig()
    }

    override fun onDisable() {
        executor.close()
    }
}