package com.vmmaldonadoz.triqui.matchers

import android.view.View
import android.widget.TextView
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

class BlankTextMatcher : TypeSafeMatcher<View>() {

    override fun describeTo(description: Description) {
        description.appendText("has blank text")
    }

    public override fun matchesSafely(view: View): Boolean {
        return (view as TextView).text.toString().isBlank()
    }
}