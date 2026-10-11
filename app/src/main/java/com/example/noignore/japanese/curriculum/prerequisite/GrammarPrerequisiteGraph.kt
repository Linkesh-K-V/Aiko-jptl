package com.example.noignore.japanese.curriculum.prerequisite

/**
 * Authoritative N5 Grammar Prerequisite Dependency Graph.
 * Formulates strict, linguistically sound relationships across all 45 N5 grammar patterns.
 * Ensures learners are never introduced to derived forms (e.g. 〜てください) before
 * understanding their foundational forms (動詞のて形, 辞書形).
 */
object GrammarPrerequisiteGraph {

    // Direct prerequisite edges: Child grammar ID -> List of immediate Parent prerequisite IDs
    private val DIRECT_DEPENDENCIES: Map<String, List<String>> = mapOf(
        // 1. Sentence foundations & Copula
        "n5_g_01" to emptyList(), // 〜は〜です (Foundation Noun Predicate Copula)
        "n5_g_07" to listOf("n5_g_01"), // 〜も (Also, Too) replaces は
        "n5_g_08" to listOf("n5_g_01"), // 〜の (Possessive / Modifier)
        "n5_g_33" to listOf("n5_g_01", "n5_g_08"), // 〜と (並列・同伴)
        "n5_g_34" to listOf("n5_g_33"), // 〜や〜など (Non-exhaustive listing)

        // 2. Core Particles
        "n5_g_02" to listOf("n5_g_01"), // 〜を (Direct Object Marker)
        "n5_g_03" to listOf("n5_g_01"), // 〜に / 〜へ (Destination, Specific Time)
        "n5_g_04" to listOf("n5_g_02"), // 〜で (Location of Action, Means)
        "n5_g_10" to listOf("n5_g_03"), // 〜から〜まで (From... To...)
        "n5_g_09" to listOf("n5_g_01", "n5_g_03"), // 〜があります / 〜がいます (Existence)

        // 3. Verb Foundations & Polite Conjugations
        "n5_g_27" to listOf("n5_g_01"), // 動詞の過去形 (-mashita, -masendeshita)
        "n5_g_11" to listOf("n5_g_27"), // 〜ましょう / 〜ましょうか (Polite hortative)
        "n5_g_12" to listOf("n5_g_01", "n5_g_27"), // 〜たいです (Desire)
        "n5_g_32" to listOf("n5_g_03", "n5_g_27"), // 〜に行く / 来る (Purpose of motion)
        "n5_g_36" to listOf("n5_g_27"), // 〜もう〜ました (Already did)
        "n5_g_41" to listOf("n5_g_27"), // 〜方 (Way of doing)
        "n5_g_45" to listOf("n5_g_27"), // 〜ながら (Simultaneous action)

        // 4. Plain Verb Foundations (Dictionary, Nai, Te, Ta)
        "n5_g_29" to listOf("n5_g_27"), // 動詞の辞書形 (Plain dictionary form)
        "n5_g_17" to listOf("n5_g_29"), // 〜ことができる (Potential / Can do)
        "n5_g_38" to listOf("n5_g_29"), // 〜つもりです (Plan / Intention)

        // 5. Te-form & Derived Constructions
        "n5_g_28" to listOf("n5_g_29", "n5_g_27"), // 動詞のて形 (Connecting te-form)
        "n5_g_05" to listOf("n5_g_28"), // 〜てください (Polite request) - STRICT DEPENDENCY
        "n5_g_06" to listOf("n5_g_28", "n5_g_05"), // 〜てもいいですか (Asking permission)
        "n5_g_13" to listOf("n5_g_28", "n5_g_05"), // 〜てはいけません (Prohibition)
        "n5_g_14" to listOf("n5_g_28"), // 〜ている (Progressive / State)
        "n5_g_35" to listOf("n5_g_14"), // 〜まだ〜ていません (Not yet done)
        "n5_g_44" to listOf("n5_g_28"), // 〜ても (Even if)

        // 6. Nai-form & Derived Constructions
        "n5_g_30" to listOf("n5_g_29"), // 動詞のない形 (Plain negative -nai)
        "n5_g_15" to listOf("n5_g_30", "n5_g_05"), // 〜ないでください (Negative request)
        "n5_g_16" to listOf("n5_g_30"), // 〜なければなりません (Obligation)

        // 7. Ta-form & Derived Constructions
        "n5_g_31" to listOf("n5_g_28", "n5_g_29"), // 動詞のた形 (Plain past -ta)
        "n5_g_18" to listOf("n5_g_31"), // 〜たことがある (Past experience)
        "n5_g_19" to listOf("n5_g_31"), // 〜たり〜たりする (Representative actions)
        "n5_g_20" to listOf("n5_g_29", "n5_g_31"), // 〜前に / 〜後で (Before & After)

        // 8. Adjective Foundations & Comparisons
        "n5_g_24" to listOf("n5_g_01"), // 〜が好き / 嫌い (Like & Dislike with が)
        "n5_g_25" to listOf("n5_g_01"), // い形容詞の活用 (I-adjective conjugation)
        "n5_g_26" to listOf("n5_g_01"), // な形容詞の活用 (Na-adjective conjugation)
        "n5_g_21" to listOf("n5_g_01"), // 〜から / 〜ので (Reason)
        "n5_g_22" to listOf("n5_g_25", "n5_g_26"), // 〜より / 〜のほうが (Comparison)
        "n5_g_23" to listOf("n5_g_22"), // 〜の中で〜が一番 (Superlative)
        "n5_g_40" to listOf("n5_g_22"), // 〜と〜とどちらが〜ですか (Alternative comparison)
        "n5_g_37" to listOf("n5_g_25", "n5_g_27"), // 〜すぎる (Too much / Excessive)

        // 9. Conjectures, Conditions, Counters
        "n5_g_39" to listOf("n5_g_01"), // 〜でしょう (Conjecture)
        "n5_g_42" to listOf("n5_g_03", "n5_g_10"), // 期間・回数の表現 (Duration & Frequency)
        "n5_g_43" to listOf("n5_g_29", "n5_g_08"), // 〜時 (When... / At the time of)

        // 10. Phase 16 Remediated Core Foundations (Demonstratives, Social Transactions, Complex Clauses)
        "n5_g_46" to listOf("n5_g_01"), // 指示代名詞 (これ・それ・あれ・どれ)
        "n5_g_47" to listOf("n5_g_46"), // 連体詞 (この・その・あの・どの + 名詞)
        "n5_g_48" to listOf("n5_g_46"), // 場所の指示詞 (ここ・そこ・あそこ・どこ)
        "n5_g_49" to listOf("n5_g_02", "n5_g_03", "n5_g_27"), // やりもらい動詞 (あげる・くれる・もらう)
        "n5_g_50" to listOf("n5_g_29", "n5_g_30", "n5_g_31", "n5_g_33"), // 引用・思考 (〜と思います / 〜と言います)
        "n5_g_51" to listOf("n5_g_29", "n5_g_31", "n5_g_47"), // 名詞修飾 (動詞普通形 + 名詞)
        "n5_g_52" to listOf("n5_g_25", "n5_g_26", "n5_g_28"), // 形容詞のて形 (〜くて / 〜で)
        "n5_g_53" to listOf("n5_g_01") // 終助詞 (ね / よ)
    )

    // Authoritative N4 Canonical Direct Dependencies across all 56 accepted N4 Grammar points
    val N4_DIRECT_DEPENDENCIES: Map<String, List<String>> = mapOf(
        // 1. Potential & Volitional Inflections
        "n4_g_01" to listOf("n5_g_17", "n5_g_29"), // 可能形 (Potential Form)
        "n4_g_02" to listOf("n5_g_11", "n5_g_29"), // 意向形 (Volitional Form)
        "n4_g_03" to listOf("n4_g_02", "n5_g_50"), // 〜ようと思う
        "n4_g_04" to listOf("n4_g_02", "n5_g_33"), // 〜ようとする
        "n4_g_05" to listOf("n5_g_15", "n5_g_29"), // 命令形・禁止形

        // 2. Conditionals
        "n4_g_06" to listOf("n5_g_25", "n5_g_29"), // 条件形 〜ば
        "n4_g_07" to listOf("n5_g_31"), // 条件形 〜たら
        "n4_g_08" to listOf("n5_g_01"), // 条件形 〜なら
        "n4_g_09" to listOf("n5_g_29", "n5_g_33"), // 条件形 〜と

        // 3. Voice Inflections (Passive, Causative, Causative-Passive)
        "n4_g_10" to listOf("n5_g_02", "n5_g_03", "n5_g_29"), // 受身形 (Passive)
        "n4_g_11" to listOf("n5_g_29", "n5_g_30"), // 使役形 (Causative)
        "n4_g_12" to listOf("n4_g_10", "n4_g_11"), // 使役受身形 (Causative-Passive)

        // 4. Giving and Receiving Action Auxiliaries
        "n4_g_13" to listOf("n5_g_28", "n5_g_49"), // 〜てあげる
        "n4_g_14" to listOf("n5_g_28", "n5_g_49"), // 〜てくれる
        "n4_g_15" to listOf("n5_g_28", "n5_g_49"), // 〜てもらう
        "n4_g_16" to listOf("n4_g_13", "n4_g_14", "n4_g_15"), // 敬語授受補助動詞

        // 5. Resultant State, Preparation, Completion, Transitivity
        "n4_g_17" to listOf("n5_g_09", "n5_g_28"), // 結果状態 〜てある
        "n4_g_18" to listOf("n5_g_28"), // 準備 〜ておく
        "n4_g_19" to listOf("n5_g_28"), // 完了・遺憾 〜てしまう
        "n4_g_20" to listOf("n5_g_02", "n5_g_09", "n5_g_28"), // 自動詞と他動詞の対応

        // 6. Purpose, Change, Habits & Decisions
        "n4_g_21" to listOf("n4_g_01", "n5_g_29"), // 目的 〜ように
        "n4_g_22" to listOf("n5_g_08", "n5_g_29"), // 目的 〜ために
        "n4_g_23" to listOf("n4_g_01", "n5_g_29"), // 変化 〜ようになる
        "n4_g_24" to listOf("n5_g_29", "n5_g_30"), // 習慣・努力 〜ようにする
        "n4_g_25" to listOf("n5_g_29"), // 決定 〜ことにする
        "n4_g_26" to listOf("n5_g_29"), // 決定・成り行き 〜ことになる

        // 7. Aspect & Recency
        "n4_g_27" to listOf("n5_g_28", "n5_g_29", "n5_g_31"), // 局面 〜ところだ
        "n4_g_28" to listOf("n5_g_31"), // 直後 〜ばかり

        // 8. Modality, Conjecture & Evidentiality
        "n4_g_29" to listOf("n5_g_25", "n5_g_26", "n5_g_27"), // 様態 〜そうだ
        "n4_g_30" to listOf("n5_g_29", "n5_g_50"), // 伝聞 〜そうだ
        "n4_g_31" to listOf("n5_g_01", "n5_g_29"), // 比況・推量 〜ようだ / 〜みたいだ
        "n4_g_32" to listOf("n5_g_25", "n5_g_29"), // 推量・典型 〜らしい
        "n4_g_33" to listOf("n5_g_01", "n5_g_29"), // 推量 〜かもしれない
        "n4_g_34" to listOf("n5_g_29", "n5_g_51"), // 当然・確信 〜はずだ

        // 9. Ease, Difficulty, Contrast, Advice, Exemption
        "n4_g_35" to listOf("n5_g_25", "n5_g_27"), // 容易 〜やすい
        "n4_g_36" to listOf("n5_g_25", "n5_g_27"), // 困難 〜にくい
        "n4_g_37" to listOf("n5_g_21", "n5_g_29", "n5_g_31"), // 逆接・不満 〜のに
        "n4_g_38" to listOf("n5_g_01", "n5_g_29"), // 理由列挙 〜し、〜し
        "n4_g_39" to listOf("n5_g_22", "n5_g_30", "n5_g_31"), // 助言 〜たほうがいい
        "n4_g_40" to listOf("n5_g_06", "n5_g_30"), // 不要 〜なくてもいい

        // 10. Nominalization, Embedded Questions, Emotional Signs
        "n4_g_41" to listOf("n5_g_29", "n5_g_51"), // 名詞化 〜の / 〜こと
        "n4_g_42" to listOf("n5_g_01", "n5_g_29"), // 間接疑問 〜かどうか
        "n4_g_43" to listOf("n5_g_46", "n5_g_48"), // 間接疑問 疑問詞＋〜か
        "n4_g_44" to listOf("n5_g_12", "n5_g_25"), // 感情・願望の表出 〜がる

        // 11. Complex Auxiliary Actions & Intervals
        "n4_g_45" to listOf("n5_g_28"), // 試行 〜てみる
        "n4_g_46" to listOf("n5_g_03", "n5_g_28", "n5_g_32"), // 方向・時間推移 〜ていく / 〜てくる
        "n4_g_47" to listOf("n5_g_14", "n5_g_43"), // 期間・同時 〜間に / 〜間
        "n4_g_48" to listOf("n5_g_28", "n5_g_30"), // 付帯状況 〜ないで / 〜ずに
        "n4_g_49" to listOf("n5_g_08", "n5_g_29"), // 予定 〜予定だ

        // 12. Keigo Foundations & Sentence Patterns
        "n4_g_50" to listOf("n5_g_01", "n5_g_27"), // 敬語入門: お＋動詞連用形＋になる
        "n4_g_51" to listOf("n5_g_01", "n5_g_27"), // 敬語入門: お＋動詞連用形＋する
        "n4_g_52" to listOf("n4_g_50"), // 敬語入門: 特別尊敬語動詞
        "n4_g_53" to listOf("n4_g_51"), // 敬語入門: 特別謙譲語動詞
        "n4_g_54" to listOf("n5_g_01"), // 丁寧語・美化語 (でございます / お・ご)
        "n4_g_55" to listOf("n4_g_34"), // 確信否定 〜はずがない
        "n4_g_56" to listOf("n5_g_07", "n5_g_29") // 複文・対比 〜だけでなく〜も
    )

    val ALL_DEPENDENCIES: Map<String, List<String>> = DIRECT_DEPENDENCIES + N4_DIRECT_DEPENDENCIES

    fun getDirectPrerequisites(grammarId: String): List<String> {
        return ALL_DEPENDENCIES[grammarId] ?: emptyList()
    }

    fun getAllTransitivePrerequisites(grammarId: String): Set<String> {
        val visited = mutableSetOf<String>()
        val queue = ArrayDeque<String>()
        queue.addAll(getDirectPrerequisites(grammarId))

        while (queue.isNotEmpty()) {
            val curr = queue.removeFirst()
            if (visited.add(curr)) {
                queue.addAll(getDirectPrerequisites(curr))
            }
        }
        return visited
    }

    /**
     * Verifies if a grammar item can be learned given the learner's set of mastered grammar items.
     */
    fun isGrammarUnlocked(grammarId: String, masteredGrammarIds: Set<String>): Boolean {
        val directParents = getDirectPrerequisites(grammarId)
        return directParents.all { masteredGrammarIds.contains(it) }
    }

    /**
     * Authoritative N5 Topological sort guaranteeing that no item appears before its prerequisites.
     */
    fun getPedagogicalSequence(): List<String> {
        return topologicalSort(DIRECT_DEPENDENCIES)
            ?: throw IllegalStateException("Curriculum prerequisite graph contains a circular dependency")
    }

    /**
     * Authoritative N4 Topological sequence (56 items) respecting both cross-level N5 and internal N4 prerequisites.
     */
    fun getN4PedagogicalSequence(): List<String> {
        val full = topologicalSort(ALL_DEPENDENCIES)
            ?: throw IllegalStateException("Combined prerequisite graph contains a circular dependency")
        return full.filter { it.startsWith("n4_g_") }
    }

    /**
     * Combined N5 + N4 Topological sequence (109 items).
     */
    fun getFullPedagogicalSequence(): List<String> {
        return topologicalSort(ALL_DEPENDENCIES)
            ?: throw IllegalStateException("Combined prerequisite graph contains a circular dependency")
    }

    data class GraphValidationReport(
        val isValid: Boolean,
        val totalNodes: Int,
        val totalEdges: Int,
        val crossLevelN4ToN5Edges: Int,
        val brokenReferences: List<String>,
        val duplicateReferences: List<String>,
        val selfReferences: List<String>,
        val illegalReverseDependencies: List<String>,
        val cycles: List<String>
    )

    /**
     * Generic validation for any dependency map (N5, N4, or combined cross-level graph).
     * Strictly enforces DAG rules, broken reference checks, and cross-level invariants.
     */
    fun validateGraphTopology(
        dependencyMap: Map<String, List<String>>,
        allowExternalParents: Boolean = false
    ): GraphValidationReport {
        val broken = mutableListOf<String>()
        val duplicates = mutableListOf<String>()
        val selfRefs = mutableListOf<String>()
        val illegalReverse = mutableListOf<String>()
        val allNodeIds = dependencyMap.keys
        var totalEdges = 0
        var n4ToN5Edges = 0

        for ((child, parents) in dependencyMap) {
            totalEdges += parents.size

            // Rule 7: Self reference
            if (parents.contains(child)) {
                selfRefs.add("Node $child references itself")
            }

            // Rule 8: Duplicate references
            val seen = mutableSetOf<String>()
            for (p in parents) {
                if (!seen.add(p)) {
                    duplicates.add("Node $child contains duplicate parent $p")
                }
            }

            for (p in parents) {
                // Rule 6: Broken references
                if (!allNodeIds.contains(p) && !allowExternalParents) {
                    broken.add("Node $child references unknown parent $p")
                }

                // Rule 1 & 2: Cross-level directionality
                // N4 can depend on N5
                if (child.startsWith("n4_") && p.startsWith("n5_")) {
                    n4ToN5Edges++
                }
                // N5 must NEVER depend on N4
                if (child.startsWith("n5_") && p.startsWith("n4_")) {
                    illegalReverse.add("Illegal reverse dependency: N5 node $child cannot depend on N4 node $p")
                }
            }
        }

        // Rule 5: Cycle detection via Kahn's algorithm
        val sorted = topologicalSort(dependencyMap)
        val cycles = mutableListOf<String>()
        if (sorted == null && dependencyMap.isNotEmpty()) {
            cycles.add("Graph contains one or more circular dependency cycles")
        }

        val isValid = broken.isEmpty() && duplicates.isEmpty() && selfRefs.isEmpty() && illegalReverse.isEmpty() && cycles.isEmpty()

        return GraphValidationReport(
            isValid = isValid,
            totalNodes = dependencyMap.size,
            totalEdges = totalEdges,
            crossLevelN4ToN5Edges = n4ToN5Edges,
            brokenReferences = broken,
            duplicateReferences = duplicates,
            selfReferences = selfRefs,
            illegalReverseDependencies = illegalReverse,
            cycles = cycles
        )
    }

    /**
     * Generic topological sort for any arbitrary dependency map.
     * Returns ordered list of IDs, or null if cycle detected.
     */
    fun topologicalSort(dependencyMap: Map<String, List<String>>): List<String>? {
        val inDegree = mutableMapOf<String, Int>()
        val graph = mutableMapOf<String, MutableList<String>>()

        for (id in dependencyMap.keys) {
            inDegree[id] = 0
            graph[id] = mutableListOf()
        }

        for ((child, parents) in dependencyMap) {
            // Count in-degree only for parents present in the map
            val validParents = parents.filter { dependencyMap.containsKey(it) }
            inDegree[child] = validParents.size
            for (p in validParents) {
                graph.getOrPut(p) { mutableListOf() }.add(child)
            }
        }

        val queue = ArrayDeque<String>()
        for ((id, deg) in inDegree) {
            if (deg == 0) queue.add(id)
        }

        val ordered = mutableListOf<String>()
        while (queue.isNotEmpty()) {
            val curr = queue.removeFirst()
            ordered.add(curr)

            for (neighbor in graph[curr].orEmpty()) {
                val newDeg = (inDegree[neighbor] ?: 1) - 1
                inDegree[neighbor] = newDeg
                if (newDeg == 0) {
                    queue.add(neighbor)
                }
            }
        }

        return if (ordered.size == dependencyMap.size) ordered else null
    }
}
