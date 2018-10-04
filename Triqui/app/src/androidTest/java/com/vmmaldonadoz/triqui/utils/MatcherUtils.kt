package com.vmmaldonadoz.triqui.utils

import android.view.View
import com.vmmaldonadoz.triqui.matchers.ContainsTextMatcher
import com.vmmaldonadoz.triqui.matchers.IsNotEnabledMatcher
import com.vmmaldonadoz.triqui.matchers.BlankTextMatcher
import org.hamcrest.TypeSafeMatcher

fun isNotEnabled(): TypeSafeMatcher<View> {
    return IsNotEnabledMatcher()
}

fun containsText(text: String): TypeSafeMatcher<View> {
    return ContainsTextMatcher(text)
}

fun isTextBlank(): TypeSafeMatcher<View> {
    return BlankTextMatcher()
}