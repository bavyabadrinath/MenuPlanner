package com.menu

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.menu.model.LunchMenu
import com.menu.ui.screens.LunchMenuWeekCalendarScreen
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class LunchMenuWeekCalendarScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleMenu = listOf(
        listOf(
            LunchMenu("Chicken"),
            LunchMenu("Spaghetti"),
            LunchMenu("Curry"),
            LunchMenu("Taco"),
            LunchMenu("Pizza")
        ),
        listOf(
            LunchMenu("Mac n cheese"),
            LunchMenu("Sushi"),
            LunchMenu("Taco bowl"),
            LunchMenu("Tikka masala"),
            LunchMenu("Paella")
        ),
    )

    @Test
    fun testCalendarTitleDisplaysCorrectly() {
        composeTestRule.setContent {
            LunchMenuWeekCalendarScreen(lunchMenuLists = sampleMenu, modifier = Modifier)
        }
        val currentDate = LocalDate.now()
        val month = currentDate.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        val year = currentDate.year.toString()

        composeTestRule.onNode(hasText("$month $year")).assertIsDisplayed()
    }

    @Test
    fun testDayClickChangesSelection() {
        composeTestRule.setContent {
            LunchMenuWeekCalendarScreen(lunchMenuLists = sampleMenu, modifier = Modifier)
        }
        val targetDate = LocalDate.now().with(java.time.DayOfWeek.MONDAY)
        val dayOfMonth = targetDate.dayOfMonth
        val dayNode = composeTestRule.onNodeWithTag("day_$dayOfMonth")

        // Perform a click on the day.
        dayNode.performClick()

        // Assert that the day is selected.
        val semantics = composeTestRule.onNodeWithTag("day_$dayOfMonth").fetchSemanticsNode()
        val storedDate = semantics.config.getOrNull(SemanticsProperties.Text)?.first()
        assertEquals(dayOfMonth.toString(), storedDate?.text)
    }

    @Test
    fun testMenuIsDisplayedForSelectedDay() {
        composeTestRule.setContent {
            LunchMenuWeekCalendarScreen(lunchMenuLists = sampleMenu, modifier = Modifier)
        }


        val targetDate = LocalDate.now().with(java.time.DayOfWeek.MONDAY)
        val dayOfMonth = targetDate.dayOfMonth
        composeTestRule.onNodeWithTag("day_$dayOfMonth").performClick()

        // Format the date to match how it's displayed in the UI.
        val formattedDate = targetDate.format(DateTimeFormatter.ofPattern("EEEE", Locale.getDefault()))
        val expectedMenuText = "$formattedDate - ${sampleMenu[0][0].menuItem}" // Menu for Monday

        // Assert that the menu text is displayed.
        composeTestRule.onNode(hasText(expectedMenuText)).assertIsDisplayed()
    }

    @Test
    fun testMenuComingSoonIsDisplayed() {
        composeTestRule.setContent {
            // Pass an empty list to simulate no menu.
            LunchMenuWeekCalendarScreen(lunchMenuLists = emptyList(), modifier = Modifier)
        }

        // Assert that the "Menu Coming Soon" text is displayed.
        composeTestRule.onNodeWithTag("menuForDateText").assertTextEquals("Menu coming soon")
    }

    @Test
    fun testSaturdayIsNotEnabled() {
        composeTestRule.setContent {
            LunchMenuWeekCalendarScreen(lunchMenuLists = sampleMenu, modifier = Modifier)
        }

        val today = LocalDate.now()
        val saturday = today.with(java.time.DayOfWeek.SATURDAY)

        composeTestRule.onNodeWithTag("day_${saturday.dayOfMonth}")
            .assertIsNotEnabled()
    }
}
