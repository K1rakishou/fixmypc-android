package com.kirakishou.fixmypc.fixmypcapp.helper

import android.content.SharedPreferences
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by kirakishou on 9/17/2017.
 */
class InMemorySharedPreferences : SharedPreferences {
    private val map = Collections.synchronizedMap(mutableMapOf<String, Any>())
    private val listeners = Collections.synchronizedSet(mutableSetOf<WeakReference<SharedPreferences.OnSharedPreferenceChangeListener>>())

    override fun contains(key: String): Boolean {
        checkNotNull(key)

        return map.contains(key)
    }

    override fun getBoolean(key: String, default: Boolean): Boolean {
        checkNotNull(key)

        return map[key] as Boolean? ?: return default
    }

    override fun getInt(key: String, default: Int): Int {
        checkNotNull(key)

        return map[key] as Int? ?: return default
    }

    override fun getLong(key: String, default: Long): Long {
        checkNotNull(key)

        return map[key] as Long? ?: return default
    }

    override fun getFloat(key: String, default: Float): Float {
        checkNotNull(key)

        return map[key] as Float? ?: return default
    }

    override fun getStringSet(key: String, default: MutableSet<String>): MutableSet<String> {
        checkNotNull(key)

        return map[key] as MutableSet<String>? ?: return default
    }

    override fun getString(key: String, default: String): String {
        checkNotNull(key)

        return map[key] as String? ?: return default
    }

    override fun getAll(): MutableMap<String, *> {
        return map
    }

    override fun edit(): SharedPreferences.Editor {
        return InMemorySharedPreferencesEditor(this)
    }

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        listeners.add(WeakReference(listener))
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        listeners.remove(WeakReference(listener))
    }

    inner class InMemorySharedPreferencesEditor(private val sharedPreferences: SharedPreferences) : SharedPreferences.Editor {
        private val operationsList = Collections.synchronizedList(mutableListOf<Operation>())

        override fun clear(): SharedPreferences.Editor {
            operationsList.add(Operation(OperationType.Clear, null, null))
            return this
        }

        override fun putLong(key: String, value: Long): SharedPreferences.Editor {
            checkNotNull(key)
            operationsList.add(Operation(OperationType.Put, key, value))
            return this
        }

        override fun putInt(key: String, value: Int): SharedPreferences.Editor {
            checkNotNull(key)
            operationsList.add(Operation(OperationType.Put, key, value))
            return this
        }

        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor {
            checkNotNull(key)
            operationsList.add(Operation(OperationType.Put, key, value))
            return this
        }

        override fun putStringSet(key: String, value: MutableSet<String>?): SharedPreferences.Editor {
            checkNotNull(key)
            operationsList.add(Operation(OperationType.Put, key, value))
            return this
        }

        override fun putFloat(key: String, value: Float): SharedPreferences.Editor {
            checkNotNull(key)
            operationsList.add(Operation(OperationType.Put, key, value))
            return this
        }

        override fun putString(key: String, value: String?): SharedPreferences.Editor {
            checkNotNull(key)
            operationsList.add(Operation(OperationType.Put, key, value))
            return this
        }

        override fun remove(key: String): SharedPreferences.Editor {
            checkNotNull(key)
            operationsList.add(Operation(OperationType.Remove, key, null))
            return this
        }

        override fun commit(): Boolean {
            processOperations()
            return true
        }

        override fun apply() {
            processOperations()
        }

        private fun processOperations() {
            for ((operationType, key, value) in operationsList) {
                when (operationType) {
                    OperationType.Clear -> {
                        for (mkey in map.keys) {
                            map.remove(mkey)
                            listeners.forEach { it.get()?.onSharedPreferenceChanged(sharedPreferences, key) }
                        }

                        operationsList.clear()
                        return
                    }

                    OperationType.Put -> {
                        checkNotNull(key)
                        checkNotNull(value)

                        map.put(key!!, value!!)
                        listeners.forEach { it.get()?.onSharedPreferenceChanged(sharedPreferences, key) }
                    }

                    OperationType.Remove -> {
                        checkNotNull(key)

                        map.remove(key)
                        listeners.forEach { it.get()?.onSharedPreferenceChanged(sharedPreferences, key) }
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