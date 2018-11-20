package com.vmmaldonadoz.challenges.activities

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.vmmaldonadoz.challenges.R
import com.vmmaldonadoz.challenges.companyItem
import com.vmmaldonadoz.challenges.constants.COMPANY_ID
import com.vmmaldonadoz.challenges.database.models.Company
import com.vmmaldonadoz.challenges.databinding.ActivitySqliteChallengeBinding
import com.vmmaldonadoz.challenges.utils.subscribeOnIO
import com.vmmaldonadoz.challenges.utils.withModels
import com.vmmaldonadoz.challenges.viewmodels.SqliteViewModel
import io.reactivex.disposables.CompositeDisposable

class SqliteChallengeActivity : AppCompatActivity() {

    private lateinit var viewModel: SqliteViewModel

    private val binding: ActivitySqliteChallengeBinding by lazy {
        DataBindingUtil.setContentView<ActivitySqliteChallengeBinding>(this,
                R.layout.activity_sqlite_challenge)
    }

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.root

        viewModel = ViewModelProviders.of(this).get(SqliteViewModel::class.java)

        binding.buttonCreate.setOnClickListener { createNewCompany() }
    }

    private fun createNewCompany() {
        val intent = getCompanyIntent()
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        compositeDisposable.add(
                viewModel.getCompanies()
                        .subscribeOnIO()
                        .subscribe(::showCompanies, Throwable::printStackTrace)
        )
    }

    private fun showCompanies(companies: List<Company>) {
        binding.recyclerView.withModels {
            companies.forEach { company ->
                companyItem {
                    id(company.id)
                    model(company)
                    onClick(View.OnClickListener { showCompanyInformation(company.id) })
                }
            }
        }
    }

    private fun showCompanyInformation(id: Int) {
        val intent = getCompanyIntent(id)
        startActivity(intent)
    }

    private fun getCompanyIntent(companyId: Int? = null): Intent {
        return Intent(this, CompanyActivity::class.java).apply {
            putExtra(COMPANY_ID, companyId ?: -1)
        }
    }

    override fun onPause() {
        super.onPause()
        compositeDisposable.clear()
    }
}

