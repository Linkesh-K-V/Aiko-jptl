package com.example.noignore.japanese.curriculum.resource

enum class StudyResourceType(val displayName: String) {
    GRAMMAR_EXPLANATION("Grammar Explanation"),
    EXAMPLE_SENTENCES("Example Sentences"),
    VOCABULARY_REFERENCE("Vocabulary Reference"),
    KANJI_REFERENCE("Kanji Reference"),
    READING_PRACTICE("Reading Practice"),
    LISTENING_PRACTICE("Listening Practice"),
    EXERCISES_AND_QUIZZES("Exercises & Quizzes"),
    GENERAL_REVIEW("General Review")
}

enum class ResourceVerificationStatus {
    VERIFIED,
    UNVERIFIED,
    STALE,
    FAILED
}

enum class CurriculumCoverageState(val label: String, val description: String) {
    AVAILABLE_IN_AIKO("Available in Aiko", "Core concept entity exists in the native curriculum"),
    INSTRUCTIONAL_CONTENT_AVAILABLE("Instructional Content Available", "Reference definition, readings, and example sentences present"),
    TAUGHT_BY_LESSON("Taught in Lesson", "Explicitly included in a structured syllabus lesson"),
    PRACTICED("Practiced by Learner", "Learner has engaged in flashcard or practice flow"),
    ASSESSED("Formally Assessed", "Assessed via targeted quiz or exam item"),
    DEMONSTRATED_MASTERY("Mastery Evidenced", "Demonstrated high retention in spaced repetition"),
    RECOMMENDED_FURTHER_STUDY("Further Study Recommended", "Verified external deep dive resource recommended"),
    NOT_YET_COVERED("Curriculum Gap", "Advanced or non-core nuance not yet covered in Aiko")
}

data class CurriculumResource(
    val id: String,
    val title: String,
    val provider: String,
    val url: String,
    val associatedConceptIds: List<String>,
    val jlptLevel: String,
    val resourceType: StudyResourceType,
    val language: String,
    val description: String,
    val reasonForRecommendation: String,
    val verificationStatus: ResourceVerificationStatus,
    val lastVerifiedDate: String,
    val provenance: String,
    val contentNotes: String = ""
)

data class ConceptCoverageProfile(
    val conceptId: String,
    val conceptTitle: String,
    val category: String,
    val jlptLevel: String,
    val coverageStates: Set<CurriculumCoverageState>,
    val whatAikoCovers: String,
    val whatLearnerCanPractice: String,
    val knownGaps: String,
    val furtherStudyResources: List<CurriculumResource>
)
