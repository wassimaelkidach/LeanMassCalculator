package com.wassima.leanmass.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wassima.leanmass.data.local.dao.LBMRecordDao
import com.wassima.leanmass.data.local.entity.LBMRecordEntity

@Database(
    entities = [LBMRecordEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun lbmRecordDao(): LBMRecordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "leanmass_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}