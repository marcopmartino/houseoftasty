package it.project.houseoftasty.testUtils

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import junit.framework.TestCase.assertTrue

fun getViewById(viewId: Int): ViewInteraction {
    return onView(withId(viewId))
}

fun getTextOf(viewId: Int): String? {
    return it.project.houseoftasty.matcher.getText(withId(viewId))
}

fun typeEmptyString(): ViewAction? {
    return typeText(String())
}

fun assertNullOrEmptyText(text: String?) {
    assertTrue(text.isNullOrEmpty())
}

fun assertNullOrEmptyTextOf(viewId: Int) {
    assertNullOrEmptyText(getTextOf(viewId))
}