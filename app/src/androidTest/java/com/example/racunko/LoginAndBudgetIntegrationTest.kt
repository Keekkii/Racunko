package com.example.racunko// app/src/androidTest/java/com/example/racunko/com.example.racunko.LoginAndBudgetIntegrationTest.kt
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import org.hamcrest.Matchers.equalTo
import android.widget.EditText
import android.content.Context


@RunWith(AndroidJUnit4::class)
class LoginAndBudgetIntegrationTest {

    private lateinit var sharedPreferences: SharedPreferences


    @Before
    fun setup() {
        sharedPreferences = ApplicationProvider.getApplicationContext<Context>()
            .getSharedPreferences("finance", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply() // Resetiraj prije svakog testa
    }

    @After
    fun cleanup() {
        sharedPreferences.edit().clear().apply()
    }

    @Test
    fun loginAndSetBudget_shouldSaveAndDisplayBudget() {
        // Korak 1: Pokreni LoginActivity
        ActivityScenario.launch(LoginActivity::class.java)

        // Korak 2: Unesi podatke za login i klikni na gumb
        onView(withId(R.id.usernameEditText)).perform(typeText("admin"))
        onView(withId(R.id.passwordEditText)).perform(typeText("admin123"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.loginButton)).perform(click())

        // Korak 3: Pričekaj da se otvori MainActivity i HomeFragment
        ActivityScenario.launch(MainActivity::class.java).use {
            // Korak 4: Klikni na "Set Budget"
            onView(withId(R.id.btn_set_budget)).perform(click())

            // Korak 5: Unesi budžet u dialog i spremi
            onView(withClassName(equalTo(EditText::class.java.name)))
                .perform(replaceText("1000"))
            onView(withText("Save")).perform(click())

            // Korak 6: Provjeri prikaz na UI-u
            onView(withId(R.id.tv_budget_amount))
                .check(matches(withText("$1000.0")))

            // Korak 7: Provjeri SharedPreferences
            val savedBudget = sharedPreferences.getFloat("budget", 0f)
            assert(savedBudget == 1000f)
        }
    }
}
