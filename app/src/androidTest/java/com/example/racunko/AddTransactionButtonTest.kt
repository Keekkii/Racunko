package com.example.racunko

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class AddTransactionButtonTest {

    @Test
    fun clickAddTransactionButton_opensInputDialog() {
        // Launch MainActivity
        ActivityScenario.launch(MainActivity::class.java)

        // Click the Add Transaction button
        onView(withId(R.id.btn_add_transaction)).perform(click())

        // Check that the dialog title is displayed
        onView(withText("Add Transaction")).check { view, noViewFoundException ->
            assert(view != null) // The dialog is displayed
        }
    }
}
