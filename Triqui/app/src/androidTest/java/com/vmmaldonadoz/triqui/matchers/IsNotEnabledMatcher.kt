package com.vmmaldonadoz.triqui.matchers

import android.view.View
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

class IsNotEnabledMatcher : TypeSafeMatcher<View>() {

    override fun describeTo(description: Description) {
        description.appendText("is not enabled")
    }

    public override fun matchesSafely(view: View): Boolean {
        return !view.isEnabled
    }
}