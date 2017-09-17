package com.kirakishou.fixmypc.fixmypcapp.helper

import com.kirakishou.fixmypc.fixmypcapp.helper.extension.edit
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Created by kirakishou on 9/17/2017.
 */
class InMemorySharedPreferencesTest {

    lateinit var sharedPreferences: InMemorySharedPreferences

    @Before
    fun init() {
        sharedPreferences = InMemorySharedPreferences()
    }

    @Test
    fun testPut() {
        sharedPreferences.edit { editor ->
            editor.putInt("test_int", 1)
            editor.putBoolean("test_boolean", true)
            editor.putFloat("test_float", 3.14f)
            editor.putLong("test_long", Long.MAX_VALUE)
            editor.putString("test_string", "Hello Test")
            editor.putStringSet("test_string_set", mutableSetOf("1", "2", "3", "4", "5"))
        }

        assertEquals(1, sharedPreferences.getInt("test_int", 0))
        assertEquals(true, sharedPreferences.getBoolean("test_boolean", false))
        assertEquals(3.14f, sharedPreferences.getFloat("test_float", 0.0f))
        assertEquals(Long.MAX_VALUE, sharedPreferences.getLong("test_long", 0L))
        assertEquals("Hello Test", sharedPreferences.getString("test_string", ""))
        assertEquals(mutableSetOf("1", "2", "3", "4", "5"), sharedPreferences.getStringSet("test_string_set", mutableSetOf()))
    }

    @Test
    fun testDefaultValues() {
        assertEquals(0, sharedPreferences.getInt("test_int", 0))
        assertEquals(false, sharedPreferences.getBoolean("test_boolean", false))
        assertEquals(0.0f, sharedPreferences.getFloat("test_float", 0.0f))
        assertEquals(0L, sharedPreferences.getLong("test_long", 0L))
        assertEquals("123", sharedPreferences.getString("test_string", "123"))
        assertEquals(mutableSetOf("1"), sharedPreferences.getStringSet("test_string_set", mutableSetOf("1")))
    }

    @Test
    fun testRemove() {
        sharedPreferences.edit { editor ->
            editor.putInt("test_int", 1)
            editor.putBoolean("test_boolean", true)
            editor.putFloat("test_float", 3.14f)
            editor.putLong("test_long", Long.MAX_VALUE)
            editor.putString("test_string", "Hello Test")
            editor.putStringSet("test_string_set", mutableSetOf("1", "2", "3", "4", "5"))
        }

        sharedPreferences.edit { editor ->
            editor.remove("test_int")
            editor.remove("test_boolean")
            editor.remove("test_float")
            editor.remove("test_long")
            editor.remove("test_string")
            editor.remove("test_string_set")
        }

        assertEquals(0, sharedPreferences.getInt("test_int", 0))
        assertEquals(false, sharedPreferences.getBoolean("test_boolean", false))
        assertEquals(0.0f, sharedPreferences.getFloat("test_float", 0.0f))
        assertEquals(0L, sharedPreferences.getLong("test_long", 0L))
        assertEquals("123", sharedPreferences.getString("test_string", "123"))
        assertEquals(mutableSetOf("1"), sharedPreferences.getStringSet("test_string_set", mutableSetOf("1")))
    }

    @Test
    fun testGetAll() {
        sharedPreferences.edit { editor ->
            editor.putInt("test_int", 1)
            editor.putBoolean("test_boolean", true)
            editor.putFloat("test_float", 3.14f)
            editor.putLong("test_long", Long.MAX_VALUE)
            editor.putString("test_string", "Hello Test")
            editor.putStringSet("test_string_set", mutableSetOf("1", "2", "3", "4", "5"))
        }

        val all = sharedPreferences.all

        assertEquals(6, all.size)
        assertEquals(1, all["test_int"])
    }

    @Test
    fun testListeners() {
        sharedPreferences.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
            assertEquals("test_int", key)
            assertEquals(1, sharedPreferences.getInt(key, 0))
        }

        sharedPreferences.edit { editor ->
            editor.putInt("test_int", 1)
        }
    }
}






























