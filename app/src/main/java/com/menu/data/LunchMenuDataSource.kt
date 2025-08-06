package com.menu.data

import kotlinx.coroutines.delay

class LunchMenuDataSource {

    suspend fun getLunchMenu(): List<List<String>> {
        delay(3_000)
        return listOf(
            listOf("Alfredo Pasta", "Paneer roll", "Curry", "Pizza", "PBJ"),
            listOf("Breakfast for lunch", "Blackbean burger", "Spaghetti", "Chana masala", "Fried rice with tofu")
        )
    }
}