package com.monkeycode.financetracker.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.monkeycode.financetracker.ui.components.BottomNavigationBar
import com.monkeycode.financetracker.ui.screens.calendar.CalendarScreen
import com.monkeycode.financetracker.ui.screens.detail.FinanceDetailScreen
import com.monkeycode.financetracker.ui.screens.stats.CategoryStatsDetailScreen
import com.monkeycode.financetracker.ui.screens.stats.DailyStatsScreen
import com.monkeycode.financetracker.ui.screens.stats.MonthlyStatsScreen
import com.monkeycode.financetracker.ui.screens.stats.WeeklyStatsScreen
import com.monkeycode.financetracker.ui.screens.stats.YearlyStatsScreen
import com.monkeycode.financetracker.ui.screens.types.TransactionTypeScreen
import java.time.LocalDate

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    
    val showBottomBar = navBackStackEntry?.destination?.route in listOf(
        Screen.Detail.route,
        Screen.Calendar.route,
        Screen.Daily.route,
        Screen.Weekly.route,
        Screen.Monthly.route,
        Screen.Yearly.route,
        Screen.Types.route
    )
    
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Detail.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Detail.route) {
                FinanceDetailScreen(navController = navController)
            }
            composable(Screen.Calendar.route) {
                CalendarScreen(navController = navController)
            }
            composable(Screen.Daily.route) {
                DailyStatsScreen(navController = navController)
            }
            composable(Screen.Weekly.route) {
                WeeklyStatsScreen(navController = navController)
            }
            composable(Screen.Monthly.route) {
                MonthlyStatsScreen(navController = navController)
            }
            composable(Screen.Yearly.route) {
                YearlyStatsScreen(navController = navController)
            }
            composable(Screen.Types.route) {
                TransactionTypeScreen(navController = navController)
            }
            composable(
                route = Screen.CategoryStats.route,
                arguments = listOf(
                    navArgument("flowType") { type = NavType.IntType },
                    navArgument("startDate") { type = NavType.StringType },
                    navArgument("endDate") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val flowTypeValue = backStackEntry.arguments?.getInt("flowType") ?: 0
                val startDateArg = backStackEntry.arguments?.getString("startDate") ?: LocalDate.now().toString()
                val endDateArg = backStackEntry.arguments?.getString("endDate") ?: LocalDate.now().toString()
                CategoryStatsDetailScreen(
                    navController = navController,
                    flowTypeValue = flowTypeValue,
                    startDateStr = startDateArg,
                    endDateStr = endDateArg
                )
            }
        }
    }
}
