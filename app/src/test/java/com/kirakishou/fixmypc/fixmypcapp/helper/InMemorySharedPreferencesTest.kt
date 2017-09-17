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
            editor.apply()
        }

        assertEquals(1, sharedPreferences.getInt("test_int", 1))
    }
}