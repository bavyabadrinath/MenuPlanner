package com.menu.data

import com.menu.model.LunchMenu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

interface LunchMenuRepository {
    suspend fun getLunchMenu(): List<List<LunchMenu>>?
}

class LocalLunchMenuRepository : LunchMenuRepository {
    private var lunchMenuList: List<List<LunchMenu>>? = null
    override suspend fun getLunchMenu(): List<List<LunchMenu>>? {
        try {
            val stringListData = LunchMenuDataSource().getLunchMenu()
            val processedData = withContext(Dispatchers.IO) {
                stringListData.map { stringList ->
                    stringList.map { menuItemString ->
                        LunchMenu(menuItem = menuItemString)
                    }
                }
            }
            lunchMenuList = processedData
        } catch (e: IOException) {
            lunchMenuList = null
        }

        return lunchMenuList
    }
}