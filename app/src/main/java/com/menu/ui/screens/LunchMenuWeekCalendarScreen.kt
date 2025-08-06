package com.menu.ui.screens

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.compose.weekcalendar.WeekCalendarState
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.Week
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.kizitonwose.calendar.core.yearMonth
import com.menu.R
import com.menu.model.LunchMenu
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
//TODO: Hoist state to Viewmodel
fun LunchMenuWeekCalendarScreen(
    lunchMenuLists: List<List<LunchMenu>>,
    modifier: Modifier,
    adjacentMonths: Long = 500
) {
    val currentDate = remember { LocalDate.now() }
    val currentMonth = remember(currentDate) { currentDate.yearMonth }
    val startMonth = remember(currentDate) { currentMonth.minusMonths(adjacentMonths) }
    val endMonth = remember(currentDate) { currentMonth.plusMonths(adjacentMonths) }
    val startDate = remember { currentDate.minusDays(500) }
    val endDate = remember { currentDate.plusDays(500) }
    var selection by remember { mutableStateOf(currentDate) }
    var selectedMenuIndex by remember { mutableIntStateOf(0) }
    val daysOfWeek = remember { daysOfWeek() }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .semantics { testTag = "lunchMenuWeekCalendarScreen" }, //test tag for instrumentation test,

    ) {
        val monthState = rememberCalendarState(
            startMonth = startMonth,
            endMonth = endMonth,
            firstVisibleMonth = currentMonth,
            firstDayOfWeek = daysOfWeek.first(),
        )
        val weekState = rememberWeekCalendarState(
            startDate = startDate,
            endDate = endDate,
            firstVisibleWeekDate = currentDate,
            firstDayOfWeek = daysOfWeek.first(),
        )
        CalendarTitle(
            isWeekMode = true,
            monthState = monthState,
            weekState = weekState,
        )
        CalendarHeader(daysOfWeek = daysOfWeek)
        val weekCalendarAlpha by animateFloatAsState(1f)
        var weekCalendarSize by remember { mutableStateOf(DpSize.Zero) }
        val density = LocalDensity.current
        val visibleWeek = rememberFirstVisibleWeekAfterScroll(weekState)
        var previousVisibleWeek by remember { mutableStateOf(visibleWeek) }

        LaunchedEffect(visibleWeek) {
            val currentFirstDayDate = visibleWeek.days.first().date
            val previousFirstDayDate = previousVisibleWeek.days.first().date

            when {
                currentFirstDayDate > previousFirstDayDate -> selectedMenuIndex++
                currentFirstDayDate < previousFirstDayDate -> selectedMenuIndex--
            }

            if (currentFirstDayDate != previousFirstDayDate) {
                selection = if (currentDate > currentFirstDayDate) {
                    currentDate
                } else {
                    visibleWeek.days.getOrNull(1)?.date ?: currentFirstDayDate
                }
            }

            previousVisibleWeek = visibleWeek
        }
        WeekCalendar(
            modifier = Modifier
                .wrapContentHeight()
                .onSizeChanged {
                    val size = density.run { DpSize(it.width.toDp(), it.height.toDp()) }
                    if (weekCalendarSize != size) {
                        weekCalendarSize = size
                    }
                }
                .alpha(weekCalendarAlpha)
                .zIndex(1f)
                .semantics { testTag = "weekCalendar" }, // Added test tag,
            state = weekState,
            dayContent = { day ->
                val isSelectable = !day.date.dayOfWeek.isWeekend()
                Day(
                    day.date,
                    isSelected = (isSelectable && selection == day.date),
                    isSelectable = isSelectable,
                ) { clicked ->
                    if (selection != clicked) {
                        selection = clicked
                    }
                }
            },
        )
        Text(
            text = stringResource(R.string.todays_menu),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .semantics { testTag = "todaysMenuText" }
        )
        Text(
            text = getMenuForDate(selection, lunchMenuLists, selectedMenuIndex, LocalContext.current),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .semantics { testTag = "menuForDateText" }
        )
    }
}

@Composable
fun Day(
    day: LocalDate,
    isSelected: Boolean,
    isSelectable: Boolean,
    onClick: (LocalDate) -> Unit,
) {
    val dayText = day.dayOfMonth.toString()
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(6.dp)
            .clip(CircleShape)
            .background(color = if (isSelected) colorResource(R.color.selected_date_color) else Color.Transparent)
            .clickable(
                enabled = isSelectable,
                onClick = { onClick(day) },
            )
            .semantics {
                testTag = "day_$dayText"  // Added test tag here
            },
        contentAlignment = Alignment.Center,
    ) {
        val textColor = when {
            isSelected -> Color.White
            isSelectable -> Color.Unspecified
            else -> colorResource(R.color.inactive_text_color)
        }
        Text(
            text = day.dayOfMonth.toString(),
            color = textColor,
            fontSize = 14.sp,
        )
    }
}

fun DayOfWeek.isWeekend() = this == DayOfWeek.SATURDAY || this == DayOfWeek.SUNDAY

// Get the menu for the selected date.
// If menu is not available for the selected date/week it means menu is not published yet.
// No lunch menu for weekends
fun getMenuForDate(selection: LocalDate, lunchMenuList: List<List<LunchMenu>>, selectedMenuIndex: Int, context: Context): String {
    val currentWeekMenu = if (selectedMenuIndex < 0 || selectedMenuIndex >= lunchMenuList.size) {
        emptyList()
    } else {
        lunchMenuList[selectedMenuIndex]
    }
    val dayOfWeek = selection.dayOfWeek
    val dayOrdinal = dayOfWeek.value
    return if(currentWeekMenu.isEmpty()) {
        context.getString(R.string.menu_coming_soon)
    } else {
        //TODO: Optimize for handling locale based week definition
        when(dayOrdinal) {
            1,2,3,4,5 -> "${dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())} - ${currentWeekMenu[dayOrdinal - 1].menuItem}"
            else -> context.getString(R.string.no_lunch_plan_today)
        }
    }
}

/**
 * Find first visible week in a paged week calendar **after** scrolling stops.
 */
@Composable
fun rememberFirstVisibleWeekAfterScroll(state: WeekCalendarState): Week {
    val visibleWeek = remember(state) { mutableStateOf(state.firstVisibleWeek) }
    LaunchedEffect(state) {
        snapshotFlow { state.isScrollInProgress }
            .filter { scrolling -> !scrolling }
            .collect { visibleWeek.value = state.firstVisibleWeek }
    }
    return visibleWeek.value
}

@Composable
fun CalendarTitle(
    isWeekMode: Boolean,
    monthState: CalendarState,
    weekState: WeekCalendarState,
) {
    val visibleWeek = rememberFirstVisibleWeekAfterScroll(weekState)
    MonthAndWeekCalendarTitle(
        isWeekMode = isWeekMode,
        visibleWeek = visibleWeek,
        monthState = monthState,
        weekState = weekState,
    )
}

@Composable
fun MonthAndWeekCalendarTitle(
    isWeekMode: Boolean,
    visibleWeek: Week,
    monthState: CalendarState,
    weekState: WeekCalendarState,
) {
    val coroutineScope = rememberCoroutineScope()
    SimpleCalendarTitle(
        modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
        visibleWeek = visibleWeek,
        goToPrevious = {
            coroutineScope.launch {
                if (isWeekMode) {
                    val targetDate = weekState.firstVisibleWeek.days.first().date.minusDays(1)
                    weekState.animateScrollToWeek(targetDate)
                } else {
                    val targetMonth = monthState.firstVisibleMonth.yearMonth.previousMonth
                    monthState.animateScrollToMonth(targetMonth)
                }
            }
        },
        goToNext = {
            coroutineScope.launch {
                if (isWeekMode) {
                    val targetDate = weekState.firstVisibleWeek.days.last().date.plusDays(1)
                    weekState.animateScrollToWeek(targetDate)
                } else {
                    val targetMonth = monthState.firstVisibleMonth.yearMonth.nextMonth
                    monthState.animateScrollToMonth(targetMonth)
                }
            }
        },
    )
}

@Composable
fun CalendarHeader(daysOfWeek: List<DayOfWeek>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { testTag = "calendarHeader" },
    ) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Preview
@Composable
private fun LunchMenuWeekCalendarScreenPreview() {
    LunchMenuWeekCalendarScreen(
        listOf(
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
        ),
        Modifier
    )
}
