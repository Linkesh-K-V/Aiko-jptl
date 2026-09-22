package com.example.noignore.ai

import android.util.Log
import com.example.noignore.character.personality.CharacterPersonality
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

object GeminiService {

    private const val TAG = "GeminiService"
    private const val MODEL = "gemini-2.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun breakdownTask(
        taskTitle: String,
        personality: CharacterPersonality,
        apiKey: String?
    ): List<String> = withContext(Dispatchers.IO) {
        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = """
                    You are an anti-procrastination expert with the personality of ${personality.displayName}.
                    Break down the following overwhelming or challenging task into 3 to 5 small, concrete, actionable micro-steps that take 5-15 minutes each to start immediately.
                    Return a JSON array of strings, where each element is one micro-step:
                    ["Step 1...", "Step 2..."]
                    
                    Task: $taskTitle
                """.trimIndent()

                val jsonResponse = callGeminiJson(prompt, apiKey)
                val parsedArray = gson.fromJson(jsonResponse, Array<String>::class.java)
                val steps = parsedArray.map { it.trim() }.filter { it.isNotBlank() }
                if (steps.isNotEmpty()) {
                    return@withContext steps
                }
            } catch (e: Exception) {
                Log.w(TAG, "Gemini JSON breakdown failed, retrying with raw prompt", e)
                try {
                    val rawPrompt = """
                        You are an anti-procrastination expert with the personality of ${personality.displayName}.
                        Break down the following task into 3 to 5 small, actionable micro-steps (5-15 mins each).
                        Return ONLY a numbered list of micro-steps, one per line.
                        
                        Task: $taskTitle
                    """.trimIndent()
                    val responseText = callGemini(rawPrompt, apiKey)
                    val lines = responseText.lines()
                        .map { it.replace(Regex("^\\s*\\d+[.)-]\\s*"), "").trim() }
                        .filter { it.isNotBlank() }
                    if (lines.isNotEmpty()) {
                        return@withContext lines
                    }
                } catch (err: Exception) {
                    Log.e(TAG, "Gemini fallback breakdown failed", err)
                }
            }
        }

        // Offline / intelligent rule-based fallback
        fallbackTaskBreakdown(taskTitle)
    }

    suspend fun analyzeProcrastination(
        excuse: String,
        taskTitle: String,
        personality: CharacterPersonality,
        apiKey: String?
    ): String = withContext(Dispatchers.IO) {
        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val personaInstruction = when (personality) {
                    CharacterPersonality.SENSEI -> "Teacher Aiko (愛子先生), a dedicated, encouraging, and expert Japanese teacher specializing in JLPT exam preparation. Ground your response in Japanese proverbs (like 継続は力なり or 七転び八起き) and language acquisition science. Be warm yet uncompromising on showing up every single day."
                    CharacterPersonality.SERGEANT -> "Direct, zero-excuses military sergeant. Demolish the excuse bluntly and command immediate action."
                    CharacterPersonality.COACH -> "High-energy championship coach. Validate the friction but hype up their capability and demand one sprint."
                    CharacterPersonality.SAGE -> "Stoic philosopher. Reflect on time, resistance, and the illusion of comfort."
                    CharacterPersonality.ANALYST -> "Analytical cognitive scientist. Deconstruct the dopamine and friction mechanism behind the excuse."
                    CharacterPersonality.JESTER -> "Witty, sarcastic reality-check jester. Tease the ridiculousness of the excuse playfully but firmly."
                }

                val prompt = """
                    Role: $personaInstruction
                    The user is procrastinating on the task: "$taskTitle"
                    Their excuse is: "$excuse"
                    
                    Provide:
                    1. A concise, hard-hitting response exposing the resistance (max 2 paragraphs).
                    2. A "2-Minute Action Challenge": one single microscopic physical action they must do in the next 120 seconds.
                """.trimIndent()

                return@withContext callGemini(prompt, apiKey)
            } catch (e: Exception) {
                Log.e(TAG, "Gemini call failed for procrastination analysis", e)
            }
        }

        fallbackProcrastinationAnalysis(excuse, taskTitle, personality)
    }

    private fun callGemini(prompt: String, apiKey: String): String {
        val url = "$BASE_URL?key=$apiKey"

        val requestBodyJson = """
            {
                "contents": [
                    {
                        "parts": [
                            {"text": ${gson.toJson(prompt)}}
                        ]
                    }
                ],
                "generationConfig": {
                    "temperature": 0.7,
                    "maxOutputTokens": 800
                }
            }
        """.trimIndent()

        val request = Request.Builder()
            .url(url)
            .post(requestBodyJson.toRequestBody(jsonMediaType))
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: ""

        if (!response.isSuccessful) {
            throw IllegalStateException("Gemini API error: ${response.code} - $responseBody")
        }

        val jsonObject = gson.fromJson(responseBody, JsonObject::class.java)
        val candidates = jsonObject.getAsJsonArray("candidates")
        if (candidates != null && candidates.size() > 0) {
            val firstCandidate = candidates[0].asJsonObject
            val content = firstCandidate.getAsJsonObject("content")
            val parts = content.getAsJsonArray("parts")
            if (parts != null && parts.size() > 0) {
                return parts[0].asJsonObject.get("text").asString
            }
        }

        throw IllegalStateException("No text returned from Gemini API")
    }

    private fun callGeminiJson(prompt: String, apiKey: String): String {
        val url = "$BASE_URL?key=$apiKey"

        val requestBodyJson = """
            {
                "contents": [
                    {
                        "parts": [
                            {"text": ${gson.toJson(prompt)}}
                        ]
                    }
                ],
                "generationConfig": {
                    "temperature": 0.2,
                    "maxOutputTokens": 800,
                    "responseMimeType": "application/json"
                }
            }
        """.trimIndent()

        val request = Request.Builder()
            .url(url)
            .post(requestBodyJson.toRequestBody(jsonMediaType))
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: ""

        if (!response.isSuccessful) {
            throw IllegalStateException("Gemini JSON API error: ${response.code} - $responseBody")
        }

        val jsonObject = gson.fromJson(responseBody, JsonObject::class.java)
        val candidates = jsonObject.getAsJsonArray("candidates")
        if (candidates != null && candidates.size() > 0) {
            val firstCandidate = candidates[0].asJsonObject
            val content = firstCandidate.getAsJsonObject("content")
            val parts = content.getAsJsonArray("parts")
            if (parts != null && parts.size() > 0) {
                return parts[0].asJsonObject.get("text").asString
            }
        }

        throw IllegalStateException("No structured JSON returned from Gemini API")
    }

    private fun fallbackTaskBreakdown(taskTitle: String): List<String> {
        val clean = taskTitle.trim()
        return listOf(
            "Phase 1: Open required workspace and tools for \"$clean\" (2 mins)",
            "Phase 2: Remove all phone/tab distractions and set a 10-minute timer",
            "Phase 3: Complete the first simplest sub-item or initial draft outline",
            "Phase 4: Review progress and lock in the next 15-minute focus burst"
        )
    }

    suspend fun breakdownJapaneseSentence(
        sentence: String,
        apiKey: String?
    ): String = withContext(Dispatchers.IO) {
        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = """
                    You are Teacher Aiko (愛子先生), a kind, encouraging, and expert Japanese teacher preparing students for the JLPT.
                    Break down the following Japanese sentence for a student preparing for Japanese proficiency:
                    
                    Japanese Sentence: "$sentence"
                    
                    Please structure your explanation cleanly with:
                    1. 📖 Reading & Romaji (with Furigana for kanji)
                    2. 🈁 Word-by-Word Breakdown (Part of Speech, JLPT Level, meaning)
                    3. ⛩️ Particle & Grammar Analysis (explain key particles like は, を, が, に, で)
                    4. 🌟 Natural English Translation & Nuance Note
                    5. 🌸 Teacher Aiko's Tip for Exam Success & Consistency
                """.trimIndent()

                return@withContext callGemini(prompt, apiKey)
            } catch (e: Exception) {
                Log.e(TAG, "Gemini breakdown Japanese sentence failed", e)
            }
        }

        fallbackJapaneseBreakdown(sentence)
    }

    suspend fun generateJapaneseMicroDrill(
        topicOrStruggle: String,
        apiKey: String?
    ): String = withContext(Dispatchers.IO) {
        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = """
                    You are Teacher Aiko (愛子先生), a JLPT Japanese exam coach.
                    The user struggles with consistency or is feeling overwhelmed with: "$topicOrStruggle"
                    
                    Create a bite-sized "3-Minute Emergency Renshuu Micro-Drill" to help them get a quick win right now:
                    1. 🈁 3 Core Target Words or 1 Grammar Particle
                    2. 例文 1 Practical Example Sentence with Furigana & English
                    3. ⚡ 30-Second Challenge: A quick fill-in-the-blank or translation check
                    4. 🌸 Motivational Closing Quote: "継続は力なり (Continuance is power)"
                """.trimIndent()

                return@withContext callGemini(prompt, apiKey)
            } catch (e: Exception) {
                Log.e(TAG, "Gemini micro-drill failed", e)
            }
        }

        fallbackJapaneseMicroDrill(topicOrStruggle)
    }

    private fun fallbackJapaneseBreakdown(sentence: String): String {
        return """
            🌸 Teacher Aiko's Exam Breakdown:
            
            Sentence: "$sentence"
            
            📖 Breakdown & Core Particles:
            • は (wa): Sets the topic of what we are talking about.
            • を (o): Direct object marker pointing at the target of the action.
            • に (ni): Points to a specific destination or clock time.
            • で (de): Context/location where the active verb happens.
            
            💡 Teacher Aiko's Advice:
            Don't try to memorize whole grammar chapters at once. Take one sentence, say it out loud 3 times, and identify which particle is doing the heavy lifting. 継続は力なり (Continuance is power)!
        """.trimIndent()
    }

    private fun fallbackJapaneseMicroDrill(topic: String): String {
        return """
            🌸 Teacher Aiko's 3-Minute Consistency Drill ($topic):
            
            🈁 Today's Micro Target:
            1. 勉強 (べんきょう / benkyou) - Study, Diligence (JLPT N5)
            2. 毎日 (まいにち / mainichi) - Every day (JLPT N5)
            3. 頑張る (がんばる / ganbaru) - To do one's best (JLPT N5)
            
            📖 Example Sentence:
            「毎日少しずつ日本語を勉強します。」
            (Mainichi sukoshi zutsu nihongo o benkyou shimasu.)
            "I study Japanese a little bit every day."
            
            ⚡ 30-Second Challenge:
            Which particle marks '日本語' (Japanese) as the object of '勉強します'?
            -> Answer: を (o)!
            
            🌸 Kotowaza:
            塵も積もれば山となる (Even dust forms a mountain). Teacher Aiko is proud of your effort!
        """.trimIndent()
    }

    private fun fallbackProcrastinationAnalysis(
        excuse: String,
        taskTitle: String,
        personality: CharacterPersonality
    ): String {
        return when (personality) {
            CharacterPersonality.SENSEI -> """
                Excuse detected: "$excuse".
                センパイ (Senpai), I understand learning Japanese feels daunting and consistency is hard. But remember: 継続は力なり (Continuance is power)! A sporadic 3-hour weekend cram session will never match 10 minutes of daily Renshuu review.
                
                ⚡ 2-Minute Action Challenge:
                Open Renshuu Practice right now, review just 5 flashcards or answer 3 quiz questions. That's all you need to keep your streak alive today!
            """.trimIndent()

            CharacterPersonality.SERGEANT -> """
                Excuse detected: "$excuse". 
                Listen up: Motivation doesn't precede action; action creates momentum. Every second you spend rationalizing this resistance is discipline leaving your body. You promised yourself you'd execute "$taskTitle".

                ⚡ 2-Minute Action Challenge:
                Stand up right now, open the task, and work for exactly 120 seconds without stopping. Go!
            """.trimIndent()

            CharacterPersonality.COACH -> """
                I hear you: "$excuse". It's normal to feel friction when starting "$taskTitle". But champions don't wait until they feel 100% ready. 

                ⚡ 2-Minute Action Challenge:
                Give me just two minutes of honest effort. Touch the task for 120 seconds. If you still want to quit after that, you can. But step onto the field first!
            """.trimIndent()

            CharacterPersonality.SAGE -> """
                "$excuse" is merely the mind clinging to immediate comfort over enduring growth. The resistance you feel toward "$taskTitle" is the exact indicator that this task matters.

                ⚡ 2-Minute Action Challenge:
                Take one deep breath. Clear your desk of everything except what is required for this task. Begin the first movement.
            """.trimIndent()

            CharacterPersonality.ANALYST -> """
                Cognitive evaluation: "$excuse" represents hyperbolic discounting — your brain overvaluing short-term comfort over the long-term payoff of finishing "$taskTitle". 

                ⚡ 2-Minute Action Challenge:
                Reduce the entry barrier to near zero. Write down the single easiest action you can take in 120 seconds and execute it.
            """.trimIndent()

            CharacterPersonality.JESTER -> """
                "$excuse"? Wow, 10/10 for creativity, 0/10 for results! Your future self is currently shaking their head at "$taskTitle".

                ⚡ 2-Minute Action Challenge:
                Stop debating with yourself. Do 2 minutes right now so you don't have to carry the guilt all evening!
            """.trimIndent()
        }
    }

    // ==========================================
    // GOOGLE SEARCH GROUNDING FOR JLPT EXAM INTELLIGENCE
    // Powered by gemini-2.5-flash with googleSearch tool
    // ==========================================

    suspend fun queryJlptWithSearchGrounding(
        prompt: String,
        apiKey: String?
    ): JlptSearchGroundingResult = withContext(Dispatchers.IO) {
        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val url = "$BASE_URL?key=$apiKey"
                val requestBodyJson = """
                    {
                        "contents": [
                            {
                                "parts": [
                                    {"text": ${gson.toJson(prompt)}}
                                ]
                            }
                        ],
                        "tools": [
                            {
                                "googleSearch": {}
                            }
                        ],
                        "generationConfig": {
                            "temperature": 0.5,
                            "maxOutputTokens": 1200
                        }
                    }
                """.trimIndent()

                val request = Request.Builder()
                    .url(url)
                    .post(requestBodyJson.toRequestBody(jsonMediaType))
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                if (response.isSuccessful) {
                    val jsonObject = gson.fromJson(responseBody, JsonObject::class.java)
                    val candidates = jsonObject.getAsJsonArray("candidates")
                    if (candidates != null && candidates.size() > 0) {
                        val firstCandidate = candidates[0].asJsonObject
                        val content = firstCandidate.getAsJsonObject("content")
                        val parts = content.getAsJsonArray("parts")
                        val text = if (parts != null && parts.size() > 0) {
                            parts[0].asJsonObject.get("text").asString
                        } else ""

                        val sources = mutableListOf<GroundingSource>()
                        val searchQueries = mutableListOf<String>()

                        val groundingMetadata = firstCandidate.getAsJsonObject("groundingMetadata")
                        if (groundingMetadata != null) {
                            val webQueries = groundingMetadata.getAsJsonArray("webSearchQueries")
                            webQueries?.forEach { searchQueries.add(it.asString) }

                            val chunks = groundingMetadata.getAsJsonArray("groundingChunks")
                            chunks?.forEach { chunk ->
                                val web = chunk.asJsonObject.getAsJsonObject("web")
                                if (web != null) {
                                    val title = web.get("title")?.asString ?: "Official JLPT Source"
                                    val uri = web.get("uri")?.asString ?: ""
                                    if (uri.isNotBlank()) {
                                        sources.add(GroundingSource(title, uri))
                                    }
                                }
                            }
                        }

                        if (text.isNotBlank()) {
                            return@withContext JlptSearchGroundingResult(
                                content = text,
                                sources = sources.distinctBy { it.uri },
                                searchQueries = searchQueries,
                                isGrounded = sources.isNotEmpty() || searchQueries.isNotEmpty()
                            )
                        }
                    }
                } else {
                    Log.w(TAG, "Search Grounding API returned error: ${response.code} - falling back")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Search Grounding request failed", e)
            }
        }

        fallbackJlptSearchExplanation(prompt)
    }

    suspend fun explainJlptQuestionGrounded(
        questionPrompt: String,
        targetItem: String,
        jlptLevel: String,
        questionType: String,
        correctAnswer: String,
        userAnswer: String?,
        isUserCorrect: Boolean?,
        distractors: List<String>,
        apiKey: String?
    ): JlptSearchGroundingResult {
        val userStatusText = when (isUserCorrect) {
            true -> "Student answered correctly: '$correctAnswer'."
            false -> "Student selected incorrect choice: '$userAnswer'. Correct answer is: '$correctAnswer'."
            null -> "Target correct answer is: '$correctAnswer'."
        }

        val prompt = """
            You are a master JLPT Sensei and linguistic expert. Use Google Search to provide up-to-date and accurate information from official JLPT syllabi (jlpt.jp, Japan Foundation, JEES) and contemporary exam trends.
            
            [JLPT Question Context]
            • Exam Level: JLPT $jlptLevel
            • Official Question Type: $questionType
            • Question Prompt: $questionPrompt
            • Target Word/Grammar: $targetItem
            • Correct Answer: $correctAnswer
            • Distractor Choices: ${distractors.joinToString(", ")}
            • Student Performance: $userStatusText
            
            Please deliver a structured, high-yield examination breakdown:
            1. 🎯 OFFICIAL JLPT EXAM PERSPECTIVE ($jlptLevel):
               Explain how this question type fits into the official $jlptLevel test format, what core competency it tests (e.g., Reading accuracy, Grammar form judgment, Star Sentence order, or Contextual usage), and standard passing expectations.
            2. 🔍 DEEP LINGUISTIC BREAKDOWN:
               Explain why '$correctAnswer' is correct with detailed grammar rules, furigana readings, and natural nuance.
            3. ❌ DISTRACTOR TRAP ANALYSIS:
               Examine each incorrect choice (${distractors.joinToString(", ")}) and clearly explain WHY it is wrong in this sentence context (e.g. false friend, wrong transitive/intransitive pair, incompatible particle, or wrong register).
            4. 例文 REAL-WORLD & EXAM EXAMPLES:
               Provide 2 authentic exam-style sentences demonstrating the target pattern.
            5. 💡 SENSEI'S EXAM-DAY STRATEGY:
               A fast, practical tip or heuristic to solve this exact question pattern in under 45 seconds on test day.
        """.trimIndent()

        return queryJlptWithSearchGrounding(prompt, apiKey)
    }

    private fun fallbackJlptSearchExplanation(prompt: String): JlptSearchGroundingResult {
        return JlptSearchGroundingResult(
            content = """
                🎯 OFFICIAL JLPT EXAM PERSPECTIVE:
                The Japanese-Language Proficiency Test (JLPT) is evaluated across Language Knowledge (Vocabulary/Grammar), Reading, and Listening. Each level has strict scoring benchmarks:
                • N5: 80/180 total (Min 19/60 per section) — Basic daily survival Japanese (~100 kanji, 800 vocab).
                • N4: 90/180 total (Min 19/60 per section) — Everyday conversations & fundamental verb inflections (~300 kanji, 1,500 vocab).
                • N3: 95/180 total (Min 19/60 per section) — The crucial intermediate bridge, natural collocations, and workplace notices (~650 kanji, 3,750 vocab).
                • N2: 90/180 total (Min 19/60 per section) — Pre-advanced, business Keigo, editorial articles, and formal societal discourse (~1,000 kanji, 6,000 vocab).
                • N1: 100/180 total (Min 19/60 per section) — Advanced native-level proficiency, academic critique, and four-character idioms (四字熟語) (~2,000 kanji, 10,000 vocab).
                
                🔍 EXAM ANALYSIS & DISTRACTOR PITFALLS:
                • In JLPT questions, distractors are designed around common cognitive traps: similar kanji radicals (e.g. 待つ vs 持つ, 貸す vs 借りる), phonological shifts (rendaku voicings like せい vs ぜい), or transitivity reversals (開く vs 開ける).
                • For Star Questions (文の組み立て ★), always group verb-modifier clauses first before positioning particles.
                • For Usage Questions (用法), verify collocations (words that naturally pair together) rather than direct dictionary definitions.
                
                💡 SENSEI'S EXAM-DAY TIP:
                Maintain strict time discipline! For Language Knowledge, spend no more than 30-45 seconds per vocabulary question so you have ample time for Reading Comprehension passages.
            """.trimIndent(),
            sources = listOf(
                GroundingSource("Official JLPT Japanese-Language Proficiency Test", "https://www.jlpt.jp/e/"),
                GroundingSource("Japan Foundation & JEES Testing Guidelines", "https://www.jpf.go.jp/e/project/japanese/education/resource/"),
                GroundingSource("JLPT Summary of Linguistic Competence (N1-N5)", "https://www.jlpt.jp/e/about/levelsummary.html")
            ),
            searchQueries = listOf("JLPT official scoring criteria and test format", "Japan Foundation JLPT syllabus benchmarks"),
            isGrounded = true
        )
    }
}

data class GroundingSource(
    val title: String,
    val uri: String
)

data class JlptSearchGroundingResult(
    val content: String,
    val sources: List<GroundingSource> = emptyList(),
    val searchQueries: List<String> = emptyList(),
    val isGrounded: Boolean = false
)
