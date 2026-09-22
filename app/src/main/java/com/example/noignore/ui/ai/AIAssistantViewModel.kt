package com.example.noignore.ui.ai

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.noignore.ai.GeminiService
import com.example.noignore.character.personality.CharacterPersonality
import com.example.noignore.data.AppDatabase
import com.example.noignore.data.Task
import com.example.noignore.model.RepeatMode
import com.example.noignore.reminder.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AIAssistantViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)

    private val _breakdownSteps = MutableStateFlow<List<String>>(emptyList())
    val breakdownSteps: StateFlow<List<String>> = _breakdownSteps.asStateFlow()

    private val _analysisResult = MutableStateFlow<String?>(null)
    val analysisResult: StateFlow<String?> = _analysisResult.asStateFlow()

    private val _japaneseSentenceResult = MutableStateFlow<String?>(null)
    val japaneseSentenceResult: StateFlow<String?> = _japaneseSentenceResult.asStateFlow()

    private val _jlptGroundingResult = MutableStateFlow<com.example.noignore.ai.JlptSearchGroundingResult?>(null)
    val jlptGroundingResult: StateFlow<com.example.noignore.ai.JlptSearchGroundingResult?> = _jlptGroundingResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private fun getApiKey(): String? {
        return try {
            com.example.BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            null
        }
    }

    fun requestTaskBreakdown(taskTitle: String, personality: CharacterPersonality) {
        if (taskTitle.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val steps = GeminiService.breakdownTask(taskTitle, personality, getApiKey())
                _breakdownSteps.value = steps
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun requestProcrastinationAnalysis(excuse: String, taskTitle: String, personality: CharacterPersonality) {
        if (excuse.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val analysis = GeminiService.analyzeProcrastination(excuse, taskTitle, personality, getApiKey())
                _analysisResult.value = analysis
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun requestJapaneseBreakdown(sentence: String) {
        if (sentence.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = GeminiService.breakdownJapaneseSentence(sentence, getApiKey())
                _japaneseSentenceResult.value = result
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun requestJapaneseMicroDrill(topicOrStruggle: String) {
        if (topicOrStruggle.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = GeminiService.generateJapaneseMicroDrill(topicOrStruggle, getApiKey())
                _japaneseSentenceResult.value = result
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun requestJlptQuestionGroundedExplanation(
        questionPrompt: String,
        targetItem: String,
        jlptLevel: String,
        questionType: String,
        correctAnswer: String,
        userAnswer: String?,
        isUserCorrect: Boolean?,
        distractors: List<String>
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = GeminiService.explainJlptQuestionGrounded(
                    questionPrompt = questionPrompt,
                    targetItem = targetItem,
                    jlptLevel = jlptLevel,
                    questionType = questionType,
                    correctAnswer = correctAnswer,
                    userAnswer = userAnswer,
                    isUserCorrect = isUserCorrect,
                    distractors = distractors,
                    apiKey = getApiKey()
                )
                _jlptGroundingResult.value = result
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun requestJlptSearch(query: String, level: String? = null) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val levelContext = if (level != null && level != "ALL") "for JLPT $level level" else "across JLPT N5 to N1"
                val prompt = """
                    You are an authoritative JLPT exam coordinator and Japanese language expert. Use Google Search to provide up-to-date and accurate information from official JLPT sources (jlpt.jp, Japan Foundation, JEES).
                    
                    Student Question / Topic: $query ($levelContext)
                    
                    Please structure your answer clearly:
                    1. 🎯 OFFICIAL JLPT SPECIFICATIONS & DATES:
                       Provide official guidelines, schedule, registration windows, or level criteria as applicable.
                    2. 📊 BENCHMARK & SCORING SYSTEM:
                       Explain the required points (total pass score + minimum 19 points per section) and standard question count.
                    3. 📚 CORE COMPETENCIES & TRAPS:
                       Detail the key vocabulary, kanji, and grammar scope, plus common student misconceptions.
                    4. 💡 EXAM-DAY RECOMMENDATIONS:
                       Give 2-3 strategic, actionable study and time-management tips.
                """.trimIndent()

                val result = GeminiService.queryJlptWithSearchGrounding(prompt, getApiKey())
                _jlptGroundingResult.value = result
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearJlptGrounding() {
        _jlptGroundingResult.value = null
    }

    fun addStepAsTask(title: String, delayMinutes: Long = 10) {
        viewModelScope.launch {
            val triggerTime = System.currentTimeMillis() + (delayMinutes * 60 * 1000)
            val task = Task(
                title = title,
                timeMillis = triggerTime,
                confirmDelayMinutes = 5,
                repeatMode = RepeatMode.ONCE
            )
            val newId = db.taskDao().insert(task)
            ReminderScheduler.schedule(
                context = getApplication(),
                taskId = newId.toInt(),
                triggerAtMillis = triggerTime,
                confirmDelayMinutes = 5,
                ringtoneUri = null
            )
        }
    }

    fun clear() {
        _breakdownSteps.value = emptyList()
        _analysisResult.value = null
        _japaneseSentenceResult.value = null
        _error.value = null
    }
}
