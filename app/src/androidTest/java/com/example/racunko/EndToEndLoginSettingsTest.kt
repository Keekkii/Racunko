package com.example.racunko

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EndToEndLoginSettingsTest {
    @get:Rule
    var activityRule: ActivityScenarioRule<LoginActivity> = ActivityScenarioRule(
        LoginActivity::class.java
    )

    @Test
    @Throws(InterruptedException::class)
    fun login_and_navigate_to_settings() {
        // 1. Unesi korisničko ime i lozinku
        Espresso.onView(withId(R.id.usernameEditText))
            .perform(ViewActions.typeText("admin"), ViewActions.closeSoftKeyboard())
        Espresso.onView(withId(R.id.passwordEditText))
            .perform(ViewActions.typeText("admin123"), ViewActions.closeSoftKeyboard())

        // 2. Klikni login
        Espresso.onView(withId(R.id.loginButton)).perform(ViewActions.click())

        // 3. Otvori navigation drawer
        Espresso.onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())

        // 4. Klikni na Settings iz drawer menija
        Espresso.onView(withId(R.id.nav_view))
            .perform(NavigationViewActions.navigateTo(R.id.nav_settings))

        // 5. Provjeri da je SettingsActivity otvoren (npr. po naslovu toolbara ako postoji)
        Espresso.onView(withId(R.id.toolbar))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}