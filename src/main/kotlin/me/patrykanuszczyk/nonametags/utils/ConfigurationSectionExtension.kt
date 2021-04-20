package me.patrykanuszczyk.nonametags.utils

import java.util.*
import org.bukkit.configuration.ConfigurationSection as Section

internal fun Section.getUuid(path: String): UUID? =
    getString(path)?.let { UUID.fromString(it) }

internal fun Section.getUuidList(path: String) =
    getStringList(path).map { UUID.fromString(it) }

internal fun Section.getStringMap(path: String) = getMap(path) { it to getString(it)!! }

internal fun <K, V> Section.getMutableMap(
    path: String,
    transform: Section.(String) -> Pair<K, V>
) = getMap(path, transform)?.toMutableMap()

internal fun <K, V> Section.getMap(
    path: String,
    transform: Section.(String) -> Pair<K, V>,
): Map<K, V>? {
    val mapSection = getConfigurationSection(path)
    val keys: Set<String> = mapSection?.getKeys(false)
        ?: return null

    return keys.associate { mapSection.transform(it) }
}