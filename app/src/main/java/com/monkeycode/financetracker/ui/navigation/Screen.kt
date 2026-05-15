package com.monkeycode.financetracker.ui.navigation

sealed class Screen(val route: String) {
    object Detail : Screen("detail")
    object Calendar : Screen("calendar")
    object Daily : Screen("daily")
    object Weekly : Screen("weekly")
    object Monthly : Screen("monthly")
    object Yearly : Screen("yearly")
    object Types : Screen("types")
    object CategoryStats : Screen("category_stats/{flowType}/{startDate}/{endDate}") {
        fun createRoute(flowType: Int, startDate: String, endDate: String): String {
            return "category_stats/$flowType/$startDate/$endDate"
        }
    }
}
