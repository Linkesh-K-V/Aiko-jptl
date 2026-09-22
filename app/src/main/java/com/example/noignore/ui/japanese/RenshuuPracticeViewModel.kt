package com.example.noignore.ui.japanese

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.noignore.japanese.data.JapaneseDeckRepository
import com.example.noignore.japanese.model.AnkiRating
import com.example.noignore.japanese.model.JapaneseCategory
import com.example.noignore.japanese.model.JapaneseItem
import com.example.noignore.japanese.model.SrsCardData
import com.example.noignore.japanese.model.SrsDeckSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class PracticeMode {
    FLASHCARDS,
    JLPT_STAR_QUESTIONS, // Sentence rearrangement drill (並べ替え ★)
    PARTICLE_BATTLE, // High-frequency particle cloze drill (助詞バトル)
    SURVIVAL_ROLEPLAY, // Situational dialogue modules with voice shadowing
    SPEED_QUIZ,
    KANJI_LAB,
    KANJI_EXPLORER,
    JLPT_MOCK_EXAM,
    EXAM_PAPER_STUDY, // Anki-like paper exam study with human brain retrieval mechanism
    LISTENING_LESSON,
    READING_LESSON,
    STORIES,
    CONJUGATION_DRILL,
    LEECH_REMEDY,
    DAILY_MISSION
}

data class QuizState(
    val selectedOptionIndex: Int? = null,
    val isSubmitted: Boolean = false,
    val isCorrect: Boolean = false,
    val currentStreak: Int = 0,
    val totalCorrect: Int = 0,
    val totalAnswered: Int = 0,
    val examTimeSecondsRemaining: Int = 180 // 3-minute blitz JLPT timer
)

class RenshuuPracticeViewModel(application: Application) : AndroidViewModel(application) {

    val repository = JapaneseDeckRepository(application)

    private val _cards = MutableStateFlow<List<JapaneseItem>>(emptyList())
    val cards: StateFlow<List<JapaneseItem>> = _cards.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _isFlipped = MutableStateFlow(false)
    val isFlipped: StateFlow<Boolean> = _isFlipped.asStateFlow()

    private val _practiceMode = MutableStateFlow(PracticeMode.FLASHCARDS)
    val practiceMode: StateFlow<PracticeMode> = _practiceMode.asStateFlow()

    private val _selectedCategory = MutableStateFlow<JapaneseCategory?>(null)
    val selectedCategory: StateFlow<JapaneseCategory?> = _selectedCategory.asStateFlow()

    private val _selectedJlptLevel = MutableStateFlow(repository.getSelectedExamLevel().code)
    val selectedJlptLevel: StateFlow<String> = _selectedJlptLevel.asStateFlow()

    private val _showRomajiHints = MutableStateFlow(true)
    val showRomajiHints: StateFlow<Boolean> = _showRomajiHints.asStateFlow()

    private val _quizState = MutableStateFlow(QuizState())
    val quizState: StateFlow<QuizState> = _quizState.asStateFlow()

    private val _kaoCoins = MutableStateFlow(repository.getKaoCoins())
    val kaoCoins: StateFlow<Int> = _kaoCoins.asStateFlow()

    private val _cardsStudiedToday = MutableStateFlow(repository.getCardsStudiedToday())
    val cardsStudiedToday: StateFlow<Int> = _cardsStudiedToday.asStateFlow()

    private val _sessionCompleted = MutableStateFlow(false)
    val sessionCompleted: StateFlow<Boolean> = _sessionCompleted.asStateFlow()

    private val _sessionCoinsEarned = MutableStateFlow(0)
    val sessionCoinsEarned: StateFlow<Int> = _sessionCoinsEarned.asStateFlow()

    // Anki Spaced Repetition State
    private val _srsDeckSummary = MutableStateFlow(repository.getSrsDeckSummary())
    val srsDeckSummary: StateFlow<SrsDeckSummary> = _srsDeckSummary.asStateFlow()

    private val _onlyDueCards = MutableStateFlow(false)
    val onlyDueCards: StateFlow<Boolean> = _onlyDueCards.asStateFlow()

    // Furigana display mode: ALWAYS_SHOW, ON_TAP, NEVER_SHOW
    private val _furiganaMode = MutableStateFlow("ALWAYS_SHOW")
    val furiganaMode: StateFlow<String> = _furiganaMode.asStateFlow()

    private val _sessionRelearnQueue = mutableListOf<JapaneseItem>()
    private val _relearningCount = MutableStateFlow(0)
    val relearningCount: StateFlow<Int> = _relearningCount.asStateFlow()

    private val _currentCardSrs = MutableStateFlow<SrsCardData?>(null)
    val currentCardSrs: StateFlow<SrsCardData?> = _currentCardSrs.asStateFlow()

    init {
        loadDeck()
    }

    fun getAllKanjiList(): List<JapaneseItem> {
        return repository.getKanjiDeck(null)
    }

    fun toggleRomajiHints() {
        _showRomajiHints.value = !_showRomajiHints.value
    }

    fun cycleFuriganaMode() {
        _furiganaMode.value = when (_furiganaMode.value) {
            "ALWAYS_SHOW" -> "ON_TAP"
            "ON_TAP" -> "NEVER_SHOW"
            else -> "ALWAYS_SHOW"
        }
    }

    fun toggleOnlyDueCards() {
        _onlyDueCards.value = !_onlyDueCards.value
        loadDeck()
    }

    fun getSrsForCard(itemId: String): SrsCardData {
        return repository.getSrsCardData(itemId)
    }

    fun setItemMastery(itemId: String, mastery: Int) {
        repository.setMastery(itemId, mastery)
        refreshStats()
        loadDeck()
    }

    fun loadDeck() {
        val category = _selectedCategory.value
        val level = _selectedJlptLevel.value
        val onlyDue = _onlyDueCards.value

        val items = when (_practiceMode.value) {
            PracticeMode.KANJI_LAB -> {
                if (onlyDue) {
                    repository.getDueCards(JapaneseCategory.KANJI, level)
                } else {
                    repository.getKanjiDeck(level).shuffled()
                }
            }
            PracticeMode.JLPT_MOCK_EXAM, PracticeMode.EXAM_PAPER_STUDY -> {
                if (onlyDue) {
                    repository.getDueCards(JapaneseCategory.JLPT_EXAM, level)
                } else {
                    repository.getJlptExamDeck(level).shuffled()
                }
            }
            PracticeMode.CONJUGATION_DRILL -> {
                val verbs = repository.getAllItems().filter {
                    it.category == JapaneseCategory.VOCAB && (
                        it.japanese.endsWith("る") || it.japanese.endsWith("う") ||
                        it.japanese.endsWith("く") || it.japanese.endsWith("す") ||
                        it.japanese.endsWith("つ") || it.japanese.endsWith("む") ||
                        it.japanese.endsWith("ぶ") || it.japanese.endsWith("ぐ") ||
                        it.japanese.endsWith("ぬ") || it.japanese.endsWith("する")
                    )
                }
                verbs.ifEmpty { repository.getItemsByCategory(JapaneseCategory.VOCAB) }.shuffled()
            }
            PracticeMode.LEECH_REMEDY -> {
                val all = repository.getAllItems()
                val leeches = all.filter { repository.getSrsCardData(it.id).lapses >= 3 }
                leeches.ifEmpty { all.filter { repository.getSrsCardData(it.id).lapses > 0 } }.ifEmpty { all.take(10) }
            }
            else -> {
                if (onlyDue) {
                    repository.getDueCards(category, level)
                } else {
                    val baseItems = if (category != null) {
                        repository.getItemsByCategory(category)
                    } else {
                        repository.getAllItems()
                    }
                    if (level != "ALL") {
                        baseItems.filter { it.jlptLevel.equals(level, ignoreCase = true) }
                    } else {
                        baseItems
                    }.shuffled()
                }
            }
        }

        _cards.value = items
        _currentIndex.value = 0
        _isFlipped.value = false
        _sessionCompleted.value = false
        _quizState.value = QuizState()
        _sessionRelearnQueue.clear()
        _relearningCount.value = 0
        updateCurrentCardSrs()
        refreshStats()
    }

    private fun updateCurrentCardSrs() {
        val currentCards = _cards.value
        val idx = _currentIndex.value
        if (idx in currentCards.indices) {
            _currentCardSrs.value = repository.getSrsCardData(currentCards[idx].id)
        } else {
            _currentCardSrs.value = null
        }
    }

    fun setCategory(category: JapaneseCategory?) {
        _selectedCategory.value = category
        loadDeck()
    }

    fun setJlptLevel(level: String) {
        _selectedJlptLevel.value = level
        loadDeck()
    }

    fun setMode(mode: PracticeMode) {
        _practiceMode.value = mode
        _isFlipped.value = false
        _quizState.value = QuizState()
        loadDeck()
    }

    fun startJlptExam() {
        _practiceMode.value = PracticeMode.JLPT_MOCK_EXAM
        _selectedCategory.value = JapaneseCategory.JLPT_EXAM
        val examQuestions = repository.getJlptExamDeck(_selectedJlptLevel.value).shuffled()
        _cards.value = examQuestions
        _currentIndex.value = 0
        _isFlipped.value = false
        _sessionCompleted.value = false
        _quizState.value = QuizState()
        _sessionRelearnQueue.clear()
        _relearningCount.value = 0
        updateCurrentCardSrs()
    }

    fun startExamPaperStudy() {
        _practiceMode.value = PracticeMode.EXAM_PAPER_STUDY
        _selectedCategory.value = JapaneseCategory.JLPT_EXAM
        val examQuestions = repository.getJlptExamDeck(_selectedJlptLevel.value).shuffled()
        _cards.value = examQuestions
        _currentIndex.value = 0
        _isFlipped.value = false
        _sessionCompleted.value = false
        _quizState.value = QuizState()
        _sessionRelearnQueue.clear()
        _relearningCount.value = 0
        updateCurrentCardSrs()
    }

    fun startKanjiDeepStudy() {
        _practiceMode.value = PracticeMode.KANJI_LAB
        _selectedCategory.value = JapaneseCategory.KANJI
        loadDeck()
    }

    fun startTwoMinuteSprint() {
        _selectedCategory.value = null
        val items = repository.getAllItems().shuffled().take(5)
        _cards.value = items
        _currentIndex.value = 0
        _isFlipped.value = false
        _sessionCompleted.value = false
        _quizState.value = QuizState()
        _practiceMode.value = PracticeMode.SPEED_QUIZ
        _sessionRelearnQueue.clear()
        _relearningCount.value = 0
        updateCurrentCardSrs()
    }

    fun flipCard() {
        _isFlipped.value = !_isFlipped.value
    }

    /**
     * Authentic Anki SM-2 Spaced Repetition review handler.
     */
    fun ankiReviewCard(rating: AnkiRating) {
        val currentCards = _cards.value
        val index = _currentIndex.value
        if (index in currentCards.indices) {
            val item = currentCards[index]
            val updatedSrs = repository.recordAnkiReview(
                itemId = item.id,
                rating = rating,
                category = item.category.name,
                jlptLevel = item.jlptLevel
            )
            _currentCardSrs.value = updatedSrs

            // In Anki, when user marks "Again", it is placed into the immediate relearn queue!
            if (rating == AnkiRating.AGAIN) {
                if (!_sessionRelearnQueue.any { it.id == item.id }) {
                    _sessionRelearnQueue.add(item)
                    _relearningCount.value = _sessionRelearnQueue.size
                }
            }

            val coinBonus = when (rating) {
                AnkiRating.AGAIN -> 1
                AnkiRating.HARD -> 3
                AnkiRating.GOOD -> 5
                AnkiRating.EASY -> 8
            }
            _sessionCoinsEarned.value += coinBonus

            refreshStats()
            moveToNextCard()
        }
    }

    fun reviewFlashcard(isMastered: Boolean) {
        ankiReviewCard(if (isMastered) AnkiRating.GOOD else AnkiRating.HARD)
    }

    fun selectQuizOption(optionIndex: Int, isCorrectOverride: Boolean? = null) {
        if (_quizState.value.isSubmitted) return

        val currentCards = _cards.value
        val index = _currentIndex.value
        if (index >= currentCards.size) return

        val item = currentCards[index]
        val isCorrect = isCorrectOverride ?: (optionIndex == 0)
        val newStreak = if (isCorrect) _quizState.value.currentStreak + 1 else 0
        val newTotalCorrect = if (isCorrect) _quizState.value.totalCorrect + 1 else _quizState.value.totalCorrect

        _quizState.value = _quizState.value.copy(
            selectedOptionIndex = optionIndex,
            isSubmitted = true,
            isCorrect = isCorrect,
            currentStreak = newStreak,
            totalCorrect = newTotalCorrect,
            totalAnswered = _quizState.value.totalAnswered + 1
        )

        // Automatically calculate initial rating: Good if correct, Hard if incorrect
        val initialRating = if (isCorrect) AnkiRating.GOOD else AnkiRating.AGAIN
        val updated = repository.recordAnkiReview(
            itemId = item.id,
            rating = initialRating,
            category = item.category.name,
            jlptLevel = item.jlptLevel
        )
        _currentCardSrs.value = updated

        if (!isCorrect && !_sessionRelearnQueue.any { it.id == item.id }) {
            _sessionRelearnQueue.add(item)
            _relearningCount.value = _sessionRelearnQueue.size
        }

        // Record diagnostic mistake and learner competency in Room SQLite
        viewModelScope.launch {
            try {
                val db = com.example.noignore.data.AppDatabase.getInstance(getApplication())
                if (!isCorrect) {
                    com.example.noignore.japanese.engine.LeechInterventionEngine.recordMistake(
                        db = db,
                        item = item,
                        mistakeType = com.example.noignore.japanese.engine.MistakeCategory.READING_LAPSE
                    )
                }
                com.example.noignore.japanese.engine.LearnerModelEngine.updateCompetency(
                    db = db,
                    category = item.category.name,
                    isSuccess = isCorrect,
                    responseTimeMs = 2000L
                )
            } catch (_: Exception) {}
        }

        val coins = if (isCorrect) (5 + newStreak) else 2
        _sessionCoinsEarned.value += coins
        refreshStats()
    }

    fun nextQuizQuestion() {
        _quizState.value = _quizState.value.copy(
            selectedOptionIndex = null,
            isSubmitted = false,
            isCorrect = false
        )
        moveToNextCard()
    }

    private fun moveToNextCard() {
        val nextIdx = _currentIndex.value + 1
        if (nextIdx < _cards.value.size) {
            _currentIndex.value = nextIdx
            _isFlipped.value = false
            updateCurrentCardSrs()
        } else {
            // Check if there are items in the session relearn queue!
            if (_sessionRelearnQueue.isNotEmpty()) {
                val relearnBatch = _sessionRelearnQueue.toList()
                _sessionRelearnQueue.clear()
                _relearningCount.value = 0
                _cards.value = relearnBatch
                _currentIndex.value = 0
                _isFlipped.value = false
                updateCurrentCardSrs()
            } else {
                _sessionCompleted.value = true
            }
        }
    }

    fun refreshStats() {
        _kaoCoins.value = repository.getKaoCoins()
        _cardsStudiedToday.value = repository.getCardsStudiedToday()
        _srsDeckSummary.value = repository.getSrsDeckSummary(_selectedJlptLevel.value)
    }

    fun recordDrillSuccess(coins: Int = 5) {
        val currentCoins = repository.getKaoCoins()
        repository.setKaoCoins(currentCoins + coins)
        val today = repository.getCardsStudiedToday() + 1
        repository.setCardsStudiedToday(today)
        _sessionCoinsEarned.value += coins
        refreshStats()
    }

    fun getLeechCards(): List<Pair<JapaneseItem, SrsCardData>> {
        return repository.getLeechCards()
    }

    fun restartSession() {
        loadDeck()
        _sessionCoinsEarned.value = 0
    }
}

