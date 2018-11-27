package com.vmmaldonadoz.challenges.viewmodels

import com.vmmaldonadoz.challenges.models.Regionalism
import com.vmmaldonadoz.challenges.network.NetworkHelper
import com.vmmaldonadoz.challenges.network.services.RegionalismService
import com.vmmaldonadoz.challenges.utils.subscribeOnIO
import io.reactivex.Single

class WebserviceViewModel : BaseViewModel() {

    private val baseUrl = "https://www.datos.gov.co/resource/"

    private val service by lazy { NetworkHelper.getClient(baseUrl, RegionalismService::class.java) }

    fun getRegionalisms(query: String = ""): Single<List<Regionalism>> {
        return if (query.isBlank()) {
            service.getRegionalisms()
        } else {
            service.getFilteredRegionalisms(query)
        }.subscribeOnIO()
    }

}

