package me.patrykanuszczyk.nonametags.utils

import org.bukkit.ChatColor

/**
 * Translates alternate color codes based on the ampersand character (&).
 *
 * It's exactly the same as
 *
 *     ChatColor.translateAlternateColorCodes('&', string)
 *
 * @see ChatColor.translateAlternateColorCodes
 *
 */
fun String.color() =
    ChatColor.translateAlternateColorCodes('&', this)