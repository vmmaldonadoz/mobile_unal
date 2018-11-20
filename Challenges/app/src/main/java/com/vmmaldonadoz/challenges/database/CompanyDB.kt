package com.vmmaldonadoz.challenges.database

import android.arch.persistence.room.Room
import android.content.Context
import com.vmmaldonadoz.challenges.App


class CompanyDB {

    lateinit var database: CompanyDatabase

    companion object {

        lateinit var instance: CompanyDB
            private set
    }

    init {
        instance = this
        createDatabase(App.instance)
    }

    private fun createDatabase(applicationContext: Context) {
        database = Room.databaseBuilder(applicationContext, CompanyDatabase::class.java, "CompanyDB").build()
    }
}