@file:OptIn(ExperimentalCoroutinesApi::class)

package com.menu

import com.menu.data.FakeErrorRepository
import com.menu.data.FakeSuccessRepository
import com.menu.rules.TestDispatcherRule
import com.menu.ui.LunchMenuUiState
import com.menu.ui.LunchMenuViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test

class LunchMenuViewModelTest {
    @get:Rule
    val testDispatcher = TestDispatcherRule()

    @Test
    fun `getLunchMenu should update state to loading then success`() = runTest {
        val viewModel = LunchMenuViewModel(FakeSuccessRepository())
        advanceUntilIdle() // Wait for the launch block to complete
        val finalState = viewModel.lunchMenuUiState
        assertThat(finalState, instanceOf(LunchMenuUiState.Success::class.java))
        val successState = finalState as LunchMenuUiState.Success
        assertThat(successState.lunchMenu[0][0].menuItem, equalTo("Chicken and waffles"))
    }

    @Test
    fun `getLunchMenu should update state to loading then error`() = runTest {
        val viewModel = LunchMenuViewModel(FakeErrorRepository())
        advanceUntilIdle()
        val finalState = viewModel.lunchMenuUiState
        assertThat(finalState, instanceOf(LunchMenuUiState.Error::class.java))
    }

}