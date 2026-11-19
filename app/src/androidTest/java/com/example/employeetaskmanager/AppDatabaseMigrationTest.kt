package com.example.employeetaskmanager

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AppDatabaseMigrationTest {

    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java
    )

    @Test
    @Throws(IOException::class)
    fun migrate1To4() {
        helper.createDatabase(TEST_DB, 1).apply {
            execSQL("INSERT INTO tasks (id, title, isCompleted) VALUES (1, 'Test Task V1', 0)")
            close()
        }

        val db = helper.runMigrationsAndValidate(TEST_DB, 4, true, AppDatabase.MIGRATION_1_4)

        val cursor = db.query("SELECT * FROM tasks WHERE id = 1")
        assertTrue(cursor.moveToFirst())
        assertEquals("Test Task V1", cursor.getString(cursor.getColumnIndexOrThrow("title")))
        assertEquals(0, cursor.getInt(cursor.getColumnIndexOrThrow("isCompleted")))
        assertEquals("Medium", cursor.getString(cursor.getColumnIndexOrThrow("priority")))
        cursor.close()
    }

    @Test
    @Throws(IOException::class)
    fun migrate3To4() {
        helper.createDatabase(TEST_DB, 3).apply {
            execSQL("INSERT INTO tasks (id, title, isCompleted, priority) VALUES (2, 'Test Task V3', 1, 'High')")
            close()
        }

        val db = helper.runMigrationsAndValidate(TEST_DB, 4, true, AppDatabase.MIGRATION_3_4)

        val cursor = db.query("SELECT * FROM tasks WHERE id = 2")
        assertTrue(cursor.moveToFirst())
        assertEquals("Test Task V3", cursor.getString(cursor.getColumnIndexOrThrow("title")))
        assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow("isCompleted")))
        assertEquals("High", cursor.getString(cursor.getColumnIndexOrThrow("priority")))
        cursor.close()
    }

    @Test
    fun testFreshDatabase() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        val dao = db.taskDao()
        assertNotNull(dao)
        db.close()
    }
}
