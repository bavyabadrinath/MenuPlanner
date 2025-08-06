package com.menu.data

interface AppContainer {
    var lunchMenuRepository: LunchMenuRepository
}

class DefaultAppContainer : AppContainer {
    override var lunchMenuRepository: LunchMenuRepository = LocalLunchMenuRepository()
}