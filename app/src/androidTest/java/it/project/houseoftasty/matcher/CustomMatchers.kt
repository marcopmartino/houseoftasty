package it.project.houseoftasty.matcher

import android.view.View
import android.widget.TextView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom

// Ritorna il testo di una TextView (utilizzato negli InstrumentedTest)
fun getText(matcher: org.hamcrest.Matcher<View>): String? {
    val stringHolder = arrayOf<String?>(null)
    onView(matcher).perform(object : ViewAction {
        override fun getConstraints(): org.hamcrest.Matcher<View> {
            return isAssignableFrom(TextView::class.java)
        }

        override fun getDescription(): String {
            return "Ottenere il testo di una TextView"
        }

        override fun perform(uiController: UiController?, view: View) {
            // La conversione è sicura grazie al controllo in getConstraints()
            val textView = view as TextView
            stringHolder[0] = textView.text.toString()
        }
    })
    return stringHolder[0]
}