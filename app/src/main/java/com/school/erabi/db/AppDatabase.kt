package com.school.erabi.db

import android.content.Context
import androidx.room.*

@Database(entities = [Student::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studentDao() : StudentDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java, "studentdb.db").allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}