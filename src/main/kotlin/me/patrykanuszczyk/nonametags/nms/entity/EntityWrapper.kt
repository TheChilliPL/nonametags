package me.patrykanuszczyk.nonametags.nms.entity

import me.patrykanuszczyk.nonametags.nms.NMS

abstract class EntityWrapper {
    internal companion object {
        internal val entityClass by lazy { NMS.getClass("Entity") }
        private val isInvisibleMethod by lazy { entityClass.getMethod("isInvisible") }
        private val setInvisibleMethod by lazy { entityClass.getMethod("setInvisible", Boolean::class.javaPrimitiveType) }
        private val isInvulnerableMethod by lazy { entityClass.getMethod("isInvulnerable") }
        private val setInvulnerableMethod by lazy { entityClass.getMethod("setInvulnerable", Boolean::class.javaPrimitiveType) }
        private val getCustomNameVisibleMethod by lazy { entityClass.getMethod("getCustomNameVisible") }
        private val setCustomNameVisibleMethod by lazy { entityClass.getMethod("setCustomNameVisible", Boolean::class.javaPrimitiveType) }
        private val getIdMethod by lazy { entityClass.getMethod("getId") }
        private val getDataWatcherMethod by lazy { entityClass.getMethod("getDataWatcher") }
    }

    abstract val instance: Any

    var isInvisible
        get() = isInvisibleMethod(instance) as Boolean
        set(value) { setInvisibleMethod(instance, value) }

    var isInvulnerable
        get() = isInvulnerableMethod(instance) as Boolean
        set(value) { setInvulnerableMethod(instance, value) }

    var customNameVisible
        get() = getCustomNameVisibleMethod(instance) as Boolean
        set(value) { setCustomNameVisibleMethod(instance, value) }

    val id
        get() = getIdMethod(instance) as Int

    val dataWatcher: Any
        get() = getDataWatcherMethod(instance)
}