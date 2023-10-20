package it.project.houseoftasty.instrumentedTest

import android.util.Log
import android.view.Gravity
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import it.project.houseoftasty.view.RecipeFormFragment
import org.junit.Before
import org.junit.Test
import org.junit.After
import org.junit.runner.RunWith
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers
import androidx.test.espresso.matcher.ViewMatchers.isFocused
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import it.project.houseoftasty.R
import it.project.houseoftasty.testUtils.assertEmptyTextOf
import it.project.houseoftasty.testUtils.assertNullOrEmptyTextOf
import it.project.houseoftasty.testUtils.getTextOf
import it.project.houseoftasty.testUtils.getViewById
import it.project.houseoftasty.testUtils.typeEmptyString
import it.project.houseoftasty.testUtils.typeRepeatedText
import it.project.houseoftasty.validation.ValidationRule
import it.project.houseoftasty.view.MainActivity
import it.project.houseoftasty.view.RecipeFormFragmentArgs
import junit.framework.TestCase.assertEquals
import org.junit.Rule


@RunWith(AndroidJUnit4::class)
class RecipeFormInstrumentedTest {

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity>
        = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {

        // Naviga dalla Home dell'applicazione fino alla form per le ricette
        getViewById(R.id.drawer_layout)
            .check(matches(DrawerMatchers.isClosed(Gravity.LEFT)))
            .perform(DrawerActions.open())
        getViewById(R.id.nav_cookbook).perform(click())
        Thread.sleep(5000)
        getViewById(R.id.fragmentCookbookLayout).check(matches(isDisplayed()))
        getViewById(R.id.floating_action_button).perform(click())
        Thread.sleep(5000)
        getViewById(R.id.fragmentRecipeFormLayout).check(matches(isDisplayed()))
    }
    @Test
    fun testValidationSuccess() {
        getViewById(R.id.dataTitle).perform(typeText("Ricetta per un instrumented test"))
        assertEmptyTextOf(R.id.errorTitle)

        getViewById(R.id.dataIngredients).perform(scrollTo(), typeText("Questa e' una lista degli ingredienti"))
        assertEmptyTextOf(R.id.errorIngredients)

        getViewById(R.id.dataNumPeople).perform(scrollTo(), typeText("3"))
        assertEmptyTextOf(R.id.errorNumPeople)

        getViewById(R.id.dataPreparation).perform(scrollTo(), typeText("Per prima cosa... successivamente... infine..."))
        assertEmptyTextOf(R.id.errorPreparation)

        getViewById(R.id.dataPreparationTime).perform(scrollTo(), typeText("45"))
        assertEmptyTextOf(R.id.errorPreparationTime)
    }
    @Test
    fun testValidationFailure() {
        getViewById(R.id.dataTitle).perform(typeRepeatedText("TestoLungo", 6))
        assertEquals(
            ValidationRule.MaxLength(50).getErrorMessage(),
            getTextOf(R.id.errorTitle))

        getViewById(R.id.dataIngredients).perform(scrollTo(), typeText("Testo da cancellare"), clearText())
        assertEquals(
            ValidationRule.Required().getErrorMessage(),
            getTextOf(R.id.errorIngredients))

        getViewById(R.id.dataNumPeople).perform(scrollTo(), typeText("51"))
        assertEquals(
            ValidationRule.MaxValue(50).getErrorMessage(),
            getTextOf(R.id.errorNumPeople))

        getViewById(R.id.dataPreparationTime).perform(scrollTo(), typeText("0"))
        assertEquals(
            ValidationRule.MinValue(1).getErrorMessage(),
            getTextOf(R.id.errorPreparationTime))
    }
}