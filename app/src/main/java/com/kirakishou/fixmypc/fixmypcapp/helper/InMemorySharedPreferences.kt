package com.kirakishou.fixmypc.fixmypcapp.helper

import android.content.SharedPreferences
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by kirakishou on 9/17/2017.
 */
class InMemorySharedPreferences : SharedPreferences {
    private val map = mutableMapOf<String, Any>()
    private val listeners = mutableSetOf<WeakReference<SharedPreferences.OnSharedPreferenceChangeListener>>()

    @Synchronized
    override fun contains(key: String): Boolean {
        checkNotNull(key)

        return map.contains(key)
    }

    @Synchronized
    override fun getBoolean(key: String, default: Boolean): Boolean {
        checkNotNull(key)

        return map[key] as Boolean? ?: return default
    }

    @Synchronized
    override fun getInt(key: String, default: Int): Int {
        checkNotNull(key)

        return map[key] as Int? ?: return default
    }

    @Synchronized
    override fun getLong(key: String, default: Long): Long {
        checkNotNull(key)

        return map[key] as Long? ?: return default
    }

    @Synchronized
    override fun getFloat(key: String, default: Float): Float {
        checkNotNull(key)

        return map[key] as Float? ?: return default
    }

    @Synchronized
    override fun getStringSet(key: String, default: MutableSet<String>): MutableSet<String> {
        checkNotNull(key)

        return map[key] as MutableSet<String>? ?: return default
    }

    @Synchronized
    override fun getString(key: String, default: String): String {
        checkNotNull(key)

        return map[key] as String? ?: return default
    }

    @Synchronized
    override fun getAll(): MutableMap<String, *> {
        return map
    }

    @Synchronized
    override fun edit(): SharedPreferences.Editor {
        return InMemorySharedPreferencesEditor(this)
    }

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        listeners.add(WeakReference(listener))
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        listeners.remove(WeakReference(listener))
    }

    inner class InMemorySharedPreferencesEditor(val sharedPreferences: SharedPreferences) : SharedPreferences.Editor {
        private val queue = LinkedList<Operation>()

        override fun clear(): SharedPreferences.Editor {
            queue.add(Operation(OperationType.Clear, null, null))
            return this
        }

        override fun putLong(key: String, value: Long): SharedPreferences.Editor {
            checkNotNull(key)
            queue.add(Operation(OperationType.Put, key, value))
            return this
        }

        override fun putInt(key: String, value: Int): SharedPreferences.Editor {
            checkNotNull(key)
            queue.add(Operation(OperationType.Put, key, value))
            return this
        }

        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor {
            checkNotNull(key)
            queue.add(Operation(OperationType.Put, key, value))
            return this
        }

        override fun putStringSet(key: String, value: MutableSet<String>?): SharedPreferences.Editor {
            checkNotNull(key)
            queue.add(Operation(OperationType.Put, key, value))
            return this
        }

        override fun putFloat(key: String, value: Float): SharedPreferences.Editor {
            checkNotNull(key)
            queue.add(Operation(OperationType.Put, key, value))
            return this
        }

        override fun putString(key: String, value: String?): SharedPreferences.Editor {
            checkNotNull(key)
            queue.add(Operation(OperationType.Put, key, value))
            return this
        }

        override fun remove(key: String): SharedPreferences.Editor {
            checkNotNull(key)
            queue.add(Operation(OperationType.Remove, key, null))
            return this
        }

        override fun commit(): Boolean {
            processOperations()
            return true
        }

        override fun apply() {
            processOperations()
        }

        @Synchronized
        private fun processOperations() {
            while (queue.isNotEmpty()) {
                val operation = queue.poll()
                val operationType = operation.operationType
                val key = operation.key
                val value = operation.value

                when (operationType) {
                    OperationType.Clear -> map.clear()

                    OperationType.Put -> {
                        checkNotNull(key)
                        checkNotNull(value)

                        listeners.forEach { it.get()?.onSharedPreferenceChanged(sharedPreferences, key) }
                        map.put(key!!, value!!)
                    }

                    OperationType.Remove -> {
                        checkNotNull(key)

                        listeners.forEach { it.get()?.onSharedPreferenceChanged(sharedPreferences, key) }
                        map.remove(key)
                    }
                }
            }
        }
    }

    enum class OperationType {
        Clear,
        Put,
        Remove
    }

    data class Operation(val operationType: OperationType,
                         val key: String?,
                         val value: Any?)
}