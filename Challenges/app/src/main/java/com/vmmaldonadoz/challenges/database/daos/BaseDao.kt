package com.vmmaldonadoz.challenges.database.daos

import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Update

interface BaseDao<in T> {

    @Insert
    fun insert(obj: T)

    @Insert
    fun insert(vararg obj: T)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(obj: T)

    @Delete
    fun delete(obj: T)
}
