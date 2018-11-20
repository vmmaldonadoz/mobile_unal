package com.vmmaldonadoz.challenges.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.vmmaldonadoz.challenges.database.daos.CompanyDao
import com.vmmaldonadoz.challenges.database.models.Company

@Database(entities = arrayOf(Company::class), version = 1)
abstract class CompanyDatabase : RoomDatabase() {
    abstract fun companyDao(): CompanyDao

    companion object {
        private const val DB_NAME = "Companies.db"

        @Volatile
        private var INSTANCE: CompanyDatabase? = null

        fun getInstance(context: Context): CompanyDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): CompanyDatabase {
            return Room.databaseBuilder(context.applicationContext,
                    CompanyDatabase::class.java, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
        }
    }
}