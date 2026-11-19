package com.example.employeetaskmanager

import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseMigrationTest {

    private val context
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    private val testDatabaseName = "migration-test.db"
    private val freshDatabaseName = "fresh-database-test.db"

    @Before
    @After
    fun cleanTestDatabases() {
        context.deleteDatabase(testDatabaseName)
        context.deleteDatabase(freshDatabaseName)
    }

    /**
     * Builds a legacy SQLite database using the original task table structure.
     * This exercises Room's real migration path and schema validation without
     * relying on exported Room schema JSON files.
     */
    private fun createLegacyDatabase(version: Int, insertSql: String) {
        val databaseFile = context.getDatabasePath(testDatabaseName)
        databaseFile.parentFile?.mkdirs()

        SQLiteDatabase.openOrCreateDatabase(databaseFile, null).use { database ->
            database.execSQL(
                """
                CREATE TABLE tasks (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    title TEXT NOT NULL,
                    isCompleted INTEGER NOT NULL
                )
                """.trimIndent()
            )
            database.execSQL(insertSql)
            database.version = version
        }
    }

    private fun openMigratedDatabase(): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            testDatabaseName
        )
            .addMigrations(
                AppDatabase.MIGRATION_1_4,
                AppDatabase.MIGRATION_3_4
            )
            .allowMainThreadQueries()
            .build()
            .also { database ->
                // Opening the real Room database runs the migration and Room's
                // generated schema validation before returning the connection.
                database.openHelper.writableDatabase
            }
    }

    @Test
    fun migrate1To4_preservesExistingTaskAndAddsMediumPriority() {
        createLegacyDatabase(
            version = 1,
            insertSql = "INSERT INTO tasks (id, title, isCompleted) VALUES (1, 'Test Task V1', 0)"
        )

        val database = openMigratedDatabase()
        try {
            val cursor = database.openHelper.readableDatabase.query(
                "SELECT id, title, isCompleted, priority FROM tasks WHERE id = 1"
            )
            cursor.use {
                assertTrue(it.moveToFirst())
                assertEquals(1L, it.getLong(it.getColumnIndexOrThrow("id")))
                assertEquals("Test Task V1", it.getString(it.getColumnIndexOrThrow("title")))
                assertEquals(0, it.getInt(it.getColumnIndexOrThrow("isCompleted")))
                assertEquals("Medium", it.getString(it.getColumnIndexOrThrow("priority")))
            }
        } finally {
            database.close()
        }
    }

    @Test
    fun migrate3To4_preservesExistingTaskAndAddsMediumPriority() {
        // MIGRATION_3_4 adds the priority column. This fixture models the
        // pre-priority intermediate schema expected by that migration.
        createLegacyDatabase(
            version = 3,
            insertSql = "INSERT INTO tasks (id, title, isCompleted) VALUES (2, 'Test Task V3', 1)"
        )

        val database = openMigratedDatabase()
        try {
            val cursor = database.openHelper.readableDatabase.query(
                "SELECT id, title, isCompleted, priority FROM tasks WHERE id = 2"
            )
            cursor.use {
                assertTrue(it.moveToFirst())
                assertEquals(2L, it.getLong(it.getColumnIndexOrThrow("id")))
                assertEquals("Test Task V3", it.getString(it.getColumnIndexOrThrow("title")))
                assertEquals(1, it.getInt(it.getColumnIndexOrThrow("isCompleted")))
                assertEquals("Medium", it.getString(it.getColumnIndexOrThrow("priority")))
            }
        } finally {
            database.close()
        }
    }

    @Test
    fun freshDatabase_opensAndValidatesCurrentSchema() {
        val database = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            freshDatabaseName
        )
            .addMigrations(
                AppDatabase.MIGRATION_1_4,
                AppDatabase.MIGRATION_3_4
            )
            .allowMainThreadQueries()
            .build()

        try {
            assertNotNull(database.openHelper.writableDatabase)
            assertEquals(4, database.openHelper.readableDatabase.version)
        } finally {
            database.close()
        }
    }
}
