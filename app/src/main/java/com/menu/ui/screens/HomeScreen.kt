package com.menu.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.menu.R
import com.menu.model.LunchMenu
import com.menu.ui.LunchMenuUiState
import com.menu.ui.theme.MyApplicationTheme

@Composable
fun HomeScreen(
    lunchMenuUiState: LunchMenuUiState,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (lunchMenuUiState) {
        is LunchMenuUiState.Loading -> LoadingScreen(modifier = modifier)
        is LunchMenuUiState.Success -> LunchMenuWeekCalendarScreen(
            lunchMenuLists = lunchMenuUiState.lunchMenu,
            modifier = modifier
        )
        is LunchMenuUiState.Error -> ErrorScreen(retryAction, modifier = modifier)
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier.size(200.dp),
        painter = painterResource(R.drawable.loading_img),
        contentDescription = stringResource(R.string.loading)
    )
}

@Composable
fun ErrorScreen(
    retryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
        )
        Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(16.dp))
        Button(onClick = retryAction) {
            Text(stringResource(R.string.retry))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingScreenPreview() {
    MyApplicationTheme {
        LoadingScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorScreenPreview() {
    MyApplicationTheme {
        ErrorScreen({})
    }
}

@Preview(showBackground = true)
@Composable
fun LunchMenuListScreenPreview() {
    MyApplicationTheme {
        val mockData = List(2) { List(5) { LunchMenu( "Curry") } }
        LunchMenuWeekCalendarScreen(mockData, Modifier)
    }
}