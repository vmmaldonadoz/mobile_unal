package com.vmmaldonadoz.challenges.activities

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ActionMode
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import com.vmmaldonadoz.challenges.R
import com.vmmaldonadoz.challenges.constants.COMPANY_ID
import com.vmmaldonadoz.challenges.databinding.ActivityCompanyBinding
import com.vmmaldonadoz.challenges.utils.subscribeOnComputation
import com.vmmaldonadoz.challenges.viewmodels.CompanyViewModel
import io.reactivex.disposables.CompositeDisposable

class CompanyActivity : AppCompatActivity() {

    private val binding: ActivityCompanyBinding by lazy {
        DataBindingUtil.setContentView<ActivityCompanyBinding>(this,
                R.layout.activity_company)
    }

    private lateinit var viewModel: CompanyViewModel

    private var companyId = -1

    private val compositeDisposable by lazy { CompositeDisposable() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.root

        viewModel = ViewModelProviders.of(this).get(CompanyViewModel::class.java)
        binding.viewModel = viewModel

        lifecycle.addObserver(viewModel)

        readExtras()
        viewModel.companyId = companyId
        viewModel.editable.set(isNewCompany())
    }

    private fun readExtras() {
        companyId = intent?.extras?.getInt(COMPANY_ID) ?: -1
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        val inflater = menuInflater

        val menuResource = if (isNewCompany()) {
            R.menu.menu_save_company
        } else {
            R.menu.menu_edit_company
        }

        inflater.inflate(menuResource, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.item_save -> handleItemSave()
            R.id.item_edit -> handleItemEdit()
            R.id.item_delete -> handleItemDelete()
        }


        return super.onOptionsItemSelected(item)
    }

    private fun handleItemDelete() {
        compositeDisposable.add(
                viewModel.deleteCompany()
                        .onErrorComplete()
                        .subscribeOnComputation()
                        .subscribe {
                            Toast.makeText(applicationContext, getString(R.string.company_deleted), Toast.LENGTH_SHORT).show()
                            finish()
                        }
        )
    }

    private fun handleItemEdit() {
        viewModel.editable.set(true)
        startActionMode(createActionModeCallback())
    }

    private fun handleItemSave() {
        if (isNewCompany()) {
            saveCompany()
        } else {
            updateCompany()
        }
    }

    private fun updateCompany() {
        compositeDisposable.add(
                viewModel.updateCompany()
                        .onErrorComplete()
                        .subscribeOnComputation()
                        .subscribe {
                            Toast.makeText(applicationContext, getString(R.string.company_updated), Toast.LENGTH_SHORT).show()
                            finish()
                        }
        )
    }

    private fun saveCompany() {
        compositeDisposable.add(
                viewModel.saveNewCompany()
                        .onErrorComplete()
                        .subscribeOnComputation()
                        .subscribe {
                            Toast.makeText(applicationContext, getString(R.string.company_saved), Toast.LENGTH_SHORT).show()
                            finish()
                        }
        )
    }

    private fun isNewCompany(): Boolean {
        return companyId == -1
    }

    private fun createActionModeCallback(): ActionMode.Callback {
        return object : ActionMode.Callback {

            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                val inflater: MenuInflater = mode.menuInflater
                inflater.inflate(R.menu.menu_save_company, menu)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                return false
            }

            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                return when (item.itemId) {
                    R.id.item_save -> {
                        handleItemSave()
                        mode.finish()
                        true
                    }
                    else -> {
                        viewModel.editable.set(false)
                        false
                    }
                }
            }

            override fun onDestroyActionMode(mode: ActionMode) {
                viewModel.editable.set(false)
            }
        }
    }

}
