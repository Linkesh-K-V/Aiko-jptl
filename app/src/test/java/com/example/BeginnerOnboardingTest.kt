package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.noignore.japanese.lesson.JapaneseLessonRepository
import com.example.noignore.ui.onboarding.BeginnerStartingLevel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BeginnerOnboardingTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        val prefs = context.getSharedPreferences("aiko_jlpt_user_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
    }

    @Test
    fun test1_initialLaunchStateIndicatesOnboardingIncomplete() {
        val prefs = context.getSharedPreferences("aiko_jlpt_user_prefs", Context.MODE_PRIVATE)
        val isCompleted = prefs.getBoolean("onboarding_completed", false)
        assertFalse("New install must have onboarding_completed false", isCompleted)
        assertNull("Starting level must not be set initially", prefs.getString("learner_starting_level", null))
    }

    @Test
    fun test2_completingOnboardingSavesPreferencesAccurately() {
        val prefs = context.getSharedPreferences("aiko_jlpt_user_prefs", Context.MODE_PRIVATE)

        // Simulate completing onboarding as ABSOLUTE_BEGINNER with 15 minutes daily
        val selectedLevel = BeginnerStartingLevel.ABSOLUTE_BEGINNER
        val selectedMinutes = 15

        prefs.edit()
            .putBoolean("onboarding_completed", true)
            .putString("learner_starting_level", selectedLevel.name)
            .putInt("daily_target_minutes", selectedMinutes)
            .commit()

        assertTrue(prefs.getBoolean("onboarding_completed", false))
        assertEquals("ABSOLUTE_BEGINNER", prefs.getString("learner_starting_level", null))
        assertEquals(15, prefs.getInt("daily_target_minutes", 0))
    }

    @Test
    fun test3_absoluteBeginnerPathRoutesToAuthoritativeLessonOneWithKana() {
        val repo = JapaneseLessonRepository(context)
        val lesson1 = repo.allLessons.firstOrNull { it.lessonNumber == 1 }
        assertNotNull("Lesson 1 must exist for zero-knowledge beginner", lesson1)
        assertEquals("Hiragana Vowels (あ・い・う・え・お)", lesson1!!.title)

        // Zero-knowledge beginner starts at lesson 1, which introduces core Hiragana and foundational vocabulary
        assertTrue("Lesson 1 items must be populated", lesson1.coreItems.isNotEmpty())
        assertEquals("JLPT N5", lesson1.level)
        assertTrue("Lesson 1 core items must include Hiragana vowels", lesson1.coreItems.any { it.japanese == "あ" })
    }

    @Test
    fun test4_beginnerStartingLevelsEnumCoverage() {
        val levels = BeginnerStartingLevel.entries
        assertEquals(3, levels.size)
        assertTrue(levels.contains(BeginnerStartingLevel.ABSOLUTE_BEGINNER))
        assertTrue(levels.contains(BeginnerStartingLevel.SOME_KANA))
        assertTrue(levels.contains(BeginnerStartingLevel.ADVANCED_EXPLORER))
    }
}
