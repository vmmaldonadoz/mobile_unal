package com.vmmaldonadoz.challenges.database.daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.vmmaldonadoz.challenges.database.models.Company
import io.reactivex.Flowable

@Dao
interface CompanyDao : BaseDao<Company> {

    @Query("SELECT * FROM company")
    fun getCompanies(): Flowable<List<Company>>

    @Query("SELECT * FROM company WHERE id IN (:companyId)")
    fun getCompanyById(companyId: Int): Flowable<List<Company>>

    @Query("SELECT * FROM company WHERE name LIKE :companyName")
    fun getCompaniesByName(companyName: Int): Flowable<List<Company>>

    @Query("SELECT * FROM company WHERE classification IN (:classification)")
    fun getCompaniesByClassification(classification: String): Flowable<List<Company>>

    @Query("DELETE FROM company WHERE id = :companyId")
    fun deleteCompanyById(companyId: Int)

}