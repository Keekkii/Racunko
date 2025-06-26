package com.example.racunko

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingPolicy
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddTransactionEndToEndTest {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun addTransaction_shouldAppearInList() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        onView(withId(R.id.btn_add_transaction)).perform(click())

        onView(withId(R.id.et_amount)).perform(typeText("100"), closeSoftKeyboard())

        onView(withId(R.id.et_description)).perform(typeText("Test Income"), closeSoftKeyboard())

        onView(withId(R.id.spinner_category)).perform(click())

        device.wait(Until.hasObject(By.text("Food")), 3000)
        device.findObject(By.text("Food")).click()

        Thread.sleep(500)

        onView(withId(R.id.rb_income)).perform(click())

        device.wait(Until.hasObject(By.res("com.example.racunko:id/btn_confirm")), 3000)
        onView(withId(R.id.btn_confirm)).perform(click())

        onView(withText("Test Income")).check(matches(isDisplayed()))
    }
}
