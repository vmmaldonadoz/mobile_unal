package com.vmmaldonadoz.challenges

import android.support.multidex.MultiDexApplication
import com.vmmaldonadoz.challenges.database.CompanyDatabase

class App : MultiDexApplication() {

    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun getDatabase(): CompanyDatabase {
        return CompanyDatabase.getInstance(this)
    }

}