package com.menu.data

import com.menu.model.LunchMenu
import java.io.IOException

class FakeSuccessRepository : LunchMenuRepository {
    override suspend fun getLunchMenu(): List<List<LunchMenu>>? =
        listOf(
            listOf(
                LunchMenu("Chicken and waffles"),
                LunchMenu("Tacos"),
                LunchMenu("Curry"),
                LunchMenu("Pizza"),
                LunchMenu("Sushi")
            ),
            listOf(
                LunchMenu("Breakfast for lunch"),
                LunchMenu("Hamburgers"),
                LunchMenu("Spaghetti"),
                LunchMenu("Salmon"),
                LunchMenu("Sandwiches")
            )
        )
}

class FakeErrorRepository : LunchMenuRepository {
    override suspend fun getLunchMenu(): List<List<LunchMenu>>? {
        throw IOException("Failed to fetch menu")
    }
}