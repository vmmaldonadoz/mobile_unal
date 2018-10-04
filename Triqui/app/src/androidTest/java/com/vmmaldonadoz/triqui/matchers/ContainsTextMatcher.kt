package com.vmmaldonadoz.triqui.matchers

import android.view.View
import android.widget.TextView
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

class ContainsTextMatcher(private val text: String,
                          private val ignoreCase: Boolean = true) : TypeSafeMatcher<View>() {

    override fun describeTo(description: Description) {
        description.appendText("contains text")
    }

    public override fun matchesSafely(view: View): Boolean {
        return (view as TextView).text.contains(text, ignoreCase)
    }
}