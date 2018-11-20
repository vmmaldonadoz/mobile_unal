package com.vmmaldonadoz.challenges.viewmodels

import com.vmmaldonadoz.challenges.App
import com.vmmaldonadoz.challenges.database.models.Company
import io.reactivex.Flowable

class SqliteViewModel : BaseViewModel() {

    private val database by lazy { App.instance.getDatabase() }

    fun getCompanies(): Flowable<List<Company>> {
        return database.companyDao().getCompanies()
    }

}