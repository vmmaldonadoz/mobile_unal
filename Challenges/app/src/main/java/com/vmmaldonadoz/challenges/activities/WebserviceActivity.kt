package com.vmmaldonadoz.challenges.activities

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.widget.Toast
import com.vmmaldonadoz.challenges.R
import com.vmmaldonadoz.challenges.databinding.ActivityWebserviceBinding
import com.vmmaldonadoz.challenges.models.Regionalism
import com.vmmaldonadoz.challenges.regionalismItem
import com.vmmaldonadoz.challenges.utils.withModels
import com.vmmaldonadoz.challenges.viewmodels.WebserviceViewModel
import io.reactivex.disposables.CompositeDisposable

class WebserviceActivity : AppCompatActivity() {

    private lateinit var viewModel: WebserviceViewModel

    private val binding: ActivityWebserviceBinding by lazy {
        DataBindingUtil.setContentView<ActivityWebserviceBinding>(this,
                R.layout.activity_webservice)
    }

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.root

        viewModel = ViewModelProviders.of(this).get(WebserviceViewModel::class.java)

        getRegionalisms()
    }

    private fun getRegionalisms(query: String = "") {
        compositeDisposable.clear()
        compositeDisposable.add(
                viewModel.getRegionalisms(query)
                        .subscribe(::showRegionalisms, Throwable::printStackTrace)
        )
    }

    private fun showRegionalisms(list: List<Regionalism>) {
        if (list.isEmpty()) {
            Toast.makeText(this, getString(R.string.empty_results), Toast.LENGTH_SHORT).show()
        } else {
            showResults(list)
        }
    }

    private fun showResults(list: List<Regionalism>) {
        binding.recyclerView.withModels {
            list.forEach { regionalism ->
                regionalismItem {
                    id(regionalism.neighbor + regionalism.similarity)
                    model(regionalism)
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_search, menu)

        (menu?.findItem(R.id.search)?.actionView as SearchView?)?.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    getRegionalisms(query.orEmpty())
                    return true
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    return true
                }
            })
        }

        return true
    }

    override fun onPause() {
        super.onPause()
        compositeDisposable.clear()
    }
}
