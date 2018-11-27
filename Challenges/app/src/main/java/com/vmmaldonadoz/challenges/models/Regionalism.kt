package com.vmmaldonadoz.challenges.models

import com.google.gson.annotations.SerializedName

data class Regionalism(
        @SerializedName("neighbor") val neighbor: String = "",
        @SerializedName("regionalism") val regionalism: String = "",
        @SerializedName("similarity") val similarity: String = "0.0"
)