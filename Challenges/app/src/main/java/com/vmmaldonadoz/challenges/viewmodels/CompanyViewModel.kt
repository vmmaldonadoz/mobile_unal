package com.vmmaldonadoz.challenges.viewmodels

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.vmmaldonadoz.challenges.App
import com.vmmaldonadoz.challenges.database.models.Company
import com.vmmaldonadoz.challenges.utils.subscribeOnIO
import io.reactivex.Completable

class CompanyViewModel : BaseViewModel() {

    val editable = ObservableBoolean(false)

    val name = ObservableField<String>("Fake name")
    val url = ObservableField<String>("http://www.url.com")
    val email = ObservableField<String>("email@fake.com")
    val phone = ObservableField<String>("+1 234 5678")
    val classification = ObservableField<String>("")

    var companyId: Int = -1

    private val database by lazy { App.instance.getDatabase() }


    override fun onResume() {
        super.onResume()
        compositeDisposable.add(
                database.companyDao()
                        .getCompanyById(companyId)
                        .subscribeOnIO()
                        .subscribe(::showCompanyInformation, Throwable::printStackTrace)
        )
    }

    private fun showCompanyInformation(companies: List<Company>) {
        companies.firstOrNull()?.let { company ->
            companyId = company.id
            name.set(company.name)
            url.set(company.url)
            email.set(company.email)
            phone.set(company.phone)
            classification.set(company.classification)
        }
    }

    fun updateCompany(): Completable {
        return Completable.fromAction {
            val company = createCompanyFromFields()
            database.companyDao().update(company)
        }
    }

    fun saveNewCompany(): Completable {
        return Completable.fromAction {
            val company = createCompanyFromFields()
            database.companyDao().insert(company)
        }
    }

    private fun createCompanyFromFields(): Company {
        val id = if (companyId == -1) 0 else companyId
        return Company(
                id,
                name.get().orEmpty(),
                url.get().orEmpty(),
                phone.get().orEmpty(),
                email.get().orEmpty(),
                classification.get().orEmpty()
        )
    }

    fun deleteCompany(): Completable {
        return Completable.fromAction {
            database.companyDao().deleteCompanyById(companyId)
        }
    }

}