package com.example

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.noignore.character.CharacterViewModel
import com.example.noignore.ui.achievements.AchievementsScreen
import com.example.noignore.ui.achievements.AchievementsViewModel
import com.example.noignore.ui.ai.AIAssistantScreen
import com.example.noignore.ui.ai.AIAssistantViewModel
import com.example.noignore.ui.history.HistoryScreen
import com.example.noignore.ui.history.HistoryViewModel
import com.example.noignore.ui.japanese.PracticeMode
import com.example.noignore.ui.japanese.RenshuuPracticeScreen
import com.example.noignore.ui.japanese.RenshuuPracticeViewModel
import com.example.noignore.ui.jlpt.JlptProgressScreen
import com.example.noignore.ui.jlpt.JlptProgressViewModel
import com.example.noignore.ui.reminder.StudyReminderViewModel
import com.example.noignore.ui.streak.StreakViewModel
import com.example.noignore.ui.task.TaskAddEditScreen
import com.example.noignore.ui.task.TaskListScreen
import com.example.noignore.ui.lesson.LessonStudyFlowScreen
import com.example.noignore.ui.jlpt.JlptLevelTopicsScreen
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.noignore.ui.task.TaskViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        com.example.noignore.reminder.TeacherAikoReminderManager.createNotificationChannel(this)
        com.example.noignore.reminder.CacheMaintenanceScheduler.schedulePeriodicMaintenance(this)
        requestPermissionsIfNeeded()

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()

                val taskViewModel: TaskViewModel = viewModel()
                val streakViewModel: StreakViewModel = viewModel()
                val characterViewModel: CharacterViewModel = viewModel()
                val achievementsViewModel: AchievementsViewModel = viewModel()
                val historyViewModel: HistoryViewModel = viewModel()
                val aiViewModel: AIAssistantViewModel = viewModel()
                val renshuuViewModel: RenshuuPracticeViewModel = viewModel()
                val jlptProgressViewModel: JlptProgressViewModel = viewModel()
                val studyReminderViewModel: StudyReminderViewModel = viewModel()

                val selectedPersonality by characterViewModel.selectedPersonality.collectAsState()

                // Navigate directly to study screen if opened from Teacher Aiko's reminder notification
                LaunchedEffect(Unit) {
                    val target = intent?.getStringExtra(com.example.noignore.reminder.TeacherAikoReminderManager.EXTRA_TARGET_SCREEN)
                    if (target == "lesson_study") {
                        navController.navigate("lesson_study")
                    } else if (target == "renshuu") {
                        renshuuViewModel.setMode(PracticeMode.FLASHCARDS)
                        navController.navigate("renshuu")
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = "tasks",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("tasks") {
                        TaskListScreen(
                            taskViewModel = taskViewModel,
                            streakViewModel = streakViewModel,
                            characterViewModel = characterViewModel,
                            achievementsViewModel = achievementsViewModel,
                            studyReminderViewModel = studyReminderViewModel,
                            onNavigateToCreate = { navController.navigate("create_task") },
                            onNavigateToEdit = { taskId -> navController.navigate("edit_task/$taskId") },
                            onNavigateToHistory = { navController.navigate("history") },
                            onNavigateToAchievements = { navController.navigate("achievements") },
                            onNavigateToAI = { navController.navigate("ai") },
                            onNavigateToRenshuu = { 
                                renshuuViewModel.setMode(PracticeMode.FLASHCARDS)
                                navController.navigate("renshuu") 
                            },
                            onNavigateToJlptProgress = { navController.navigate("jlpt_progress") },
                            onNavigateToListening = {
                                renshuuViewModel.setMode(PracticeMode.LISTENING_LESSON)
                                navController.navigate("renshuu")
                            },
                            onNavigateToReading = {
                                renshuuViewModel.setMode(PracticeMode.READING_LESSON)
                                navController.navigate("renshuu")
                            },
                            onNavigateToStories = {
                                renshuuViewModel.setMode(PracticeMode.STORIES)
                                navController.navigate("renshuu")
                            },
                            onNavigateToKanjiStudy = {
                                renshuuViewModel.setMode(PracticeMode.KANJI_EXPLORER)
                                navController.navigate("renshuu")
                            },
                            onNavigateToLessonStudy = { _ ->
                                navController.navigate("lesson_study")
                            },
                            onNavigateToCategoryPractice = { category ->
                                renshuuViewModel.setCategory(category)
                                renshuuViewModel.setMode(PracticeMode.FLASHCARDS)
                                navController.navigate("renshuu")
                            },
                            onNavigateToMode = { mode ->
                                renshuuViewModel.setMode(mode)
                                navController.navigate("renshuu")
                            },
                            onNavigateToJlptTopics = { levelCode ->
                                navController.navigate("jlpt_topics/$levelCode")
                            },
                            onNavigateToBackup = {
                                navController.navigate("backup")
                            }
                        )
                    }

                    composable(
                        route = "jlpt_topics/{level}",
                        arguments = listOf(
                            navArgument("level") {
                                type = NavType.StringType
                                defaultValue = "N5"
                            }
                        )
                    ) { backStackEntry ->
                        val levelCode = backStackEntry.arguments?.getString("level") ?: "N5"
                        JlptLevelTopicsScreen(
                            initialLevelCode = levelCode,
                            onBack = { navController.popBackStack() },
                            onPracticeTopic = { topicUnit ->
                                renshuuViewModel.setMode(PracticeMode.FLASHCARDS)
                                navController.navigate("renshuu")
                            }
                        )
                    }

                    composable("jlpt_topics") {
                        JlptLevelTopicsScreen(
                            initialLevelCode = "N5",
                            onBack = { navController.popBackStack() },
                            onPracticeTopic = { topicUnit ->
                                renshuuViewModel.setMode(PracticeMode.FLASHCARDS)
                                navController.navigate("renshuu")
                            }
                        )
                    }

                    composable("lesson_study") {
                        LessonStudyFlowScreen(
                            onBack = { navController.popBackStack() },
                            onLessonCompleted = {
                                lifecycleScope.launch {
                                    val updatedStreak = streakViewModel.refreshNow()
                                    characterViewModel.refresh(updatedStreak.currentStreak)
                                    achievementsViewModel.checkAndUnlock(updatedStreak.currentStreak, taskViewModel.tasks.value.count { it.completed })
                                    achievementsViewModel.refresh()
                                }
                                navController.popBackStack()
                            }
                        )
                    }

                    composable("jlpt_progress") {
                        JlptProgressScreen(
                            viewModel = jlptProgressViewModel,
                            studyReminderViewModel = studyReminderViewModel,
                            onBack = { navController.popBackStack() },
                            onNavigateToStudy = { navController.navigate("renshuu") },
                            onNavigateToSources = { navController.navigate("sources_and_licenses") }
                        )
                    }

                    composable("renshuu") {
                        RenshuuPracticeScreen(
                            viewModel = renshuuViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("create_task") {
                        TaskAddEditScreen(
                            taskId = null,
                            viewModel = taskViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(
                        route = "edit_task/{taskId}",
                        arguments = listOf(
                            navArgument("taskId") { type = NavType.LongType }
                        )
                    ) { backStackEntry ->
                        val taskId = backStackEntry.arguments?.getLong("taskId")
                        TaskAddEditScreen(
                            taskId = taskId,
                            viewModel = taskViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("history") {
                        HistoryScreen(
                            viewModel = historyViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("achievements") {
                        val streakData by streakViewModel.streakData.collectAsState()
                        AchievementsScreen(
                            viewModel = achievementsViewModel,
                            currentStreak = streakData.currentStreak,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("ai") {
                        AIAssistantScreen(
                            viewModel = aiViewModel,
                            activePersonality = selectedPersonality,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("backup") {
                        com.example.noignore.ui.backup.BackupRestoreScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("sources_and_licenses") {
                        com.example.noignore.ui.sources.SourcesAndLicensesScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun requestPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                try {
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    startActivity(intent)
                } catch (e: Exception) {
                    // Ignore if intent is not supported on specific devices/emulators
                }
            }
        }
    }
}
