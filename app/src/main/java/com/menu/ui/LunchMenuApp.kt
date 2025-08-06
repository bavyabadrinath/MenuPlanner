package com.menu.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.menu.R
import com.menu.ui.screens.HomeScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LunchMenuApp() {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { LunchMenuTopAppBar(scrollBehavior = scrollBehavior) }
    ) {
        Surface (
            modifier = Modifier.fillMaxSize()
        ) {
            val lunchMenuViewModel: LunchMenuViewModel = viewModel(factory = LunchMenuViewModel.Factory)
            HomeScreen(
                lunchMenuUiState = lunchMenuViewModel.lunchMenuUiState,
                retryAction = lunchMenuViewModel::getLunchMenu,
                modifier = Modifier.padding(it)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LunchMenuTopAppBar(scrollBehavior: TopAppBarScrollBehavior, modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
            )
        },
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun LunchMenuAppPreview() {
    LunchMenuApp()
}