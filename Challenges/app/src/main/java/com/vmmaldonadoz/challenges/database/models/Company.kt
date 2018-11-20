package com.vmmaldonadoz.challenges.database.models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Company(
        @PrimaryKey(autoGenerate = true) var id: Int = 0,
        @ColumnInfo(name = "name") var name: String,
        @ColumnInfo(name = "url") var url: String,
        @ColumnInfo(name = "phone") var phone: String,
        @ColumnInfo(name = "email") var email: String,
        @ColumnInfo(name = "classification") var classification: String
)