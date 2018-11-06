package com.vmmaldonadoz.triqui.utils

import android.databinding.BindingAdapter
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE

@BindingAdapter("app:goneUnless")
fun View.goneUnless(visible: Boolean) {
    visibility = if (visible) VISIBLE else GONE
}