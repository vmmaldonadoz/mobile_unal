package com.vmmaldonadoz.triqui

import android.support.multidex.MultiDexApplication
import com.google.firebase.database.FirebaseDatabase

class TriquiApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

}