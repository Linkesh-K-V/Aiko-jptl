# Aiko-JLPT 🇯🇵

**A personal adaptive Japanese learning and JLPT preparation application powered by AI.**

Aiko-JLPT is an Android application designed to help learners study Japanese systematically from **JLPT N5 to N1**. The project combines structured curriculum content, spaced repetition, knowledge relationships, adaptive study planning, practice questions, reading, listening, and an AI-based learning assistant.

The core idea is simple:

> **Don't just study more. Identify what you don't know, understand why you are struggling, and practice it intelligently.**

---

## ✨ Features

### 📚 Structured Japanese Curriculum

Aiko-JLPT contains structured learning content across:

* JLPT N5
* JLPT N4
* JLPT N3
* JLPT N2
* JLPT N1

Content categories include:

* Kanji
* Vocabulary
* Grammar
* Reading
* Listening
* JLPT-style practice questions

> **Important:** The curriculum included in this application is an internal learning dataset and should not be interpreted as an official JLPT syllabus or official JLPT content.

---

## 🧠 Adaptive Learning

Aiko-JLPT tracks the learner's performance and adapts study recommendations based on learning history.

The system can consider:

* Correct and incorrect answers
* Grammar mastery
* Weak areas
* Repeated mistakes
* Review requirements
* Learning progress
* Knowledge relationships

The goal is to avoid spending equal time on everything when the learner's weaknesses are different.

---

## 🔄 Spaced Repetition

The application includes spaced-repetition-based review mechanisms to help learners retain previously studied material.

The project includes support for:

* SRS scheduling
* Review states
* Difficulty/memory tracking
* Forgotten-item remediation
* Repeated review of weak material

The system is designed around the principle of:

**Learn → Recall → Fail → Review → Strengthen → Recall again**

---

## 🕸️ Knowledge Graph

Japanese learning content is connected through a knowledge graph.

Relationships can connect:

* Kanji → Vocabulary
* Vocabulary → Reading
* Vocabulary → Listening
* Grammar → Reading
* Grammar → Listening
* Vocabulary ↔ Grammar
* JLPT questions → Knowledge items

This allows the application to understand that Japanese knowledge is interconnected rather than a collection of isolated flashcards.

For example:

```text
Kanji
 ↓
Vocabulary
 ↓
Grammar
 ↓
Sentence
 ↓
Reading / Listening
 ↓
JLPT Question
```

---

## 🤖 AI Sensei

Aiko-JLPT includes an AI-assisted learning component designed to act as a Japanese learning companion.

The AI layer can support:

* Explanations
* Learning guidance
* Mistake analysis
* Japanese practice
* Grammar assistance
* Personalized study interactions

The AI component is intended to complement the structured learning system rather than replace the underlying curriculum.

---

## 📖 Reading & Listening

The application includes learning content for reading and listening practice.

The system can connect vocabulary and grammar knowledge to contextual learning material.

Japanese text can also be used with Android's Japanese text-to-speech capabilities where supported.

---

## 📝 JLPT Practice

Aiko-JLPT includes JLPT-style practice questions covering the curriculum.

Practice is intended to help learners move from:

```text
Knowledge
   ↓
Recognition
   ↓
Recall
   ↓
Application
   ↓
Exam Practice
```

---

## 💾 Data & Reliability

The application uses a local-first architecture so learning data can be maintained on the device.

The project includes:

* Room Database
* DataStore
* Database migrations
* JSON backup and restore
* SHA-256 backup verification
* Atomic restore/rollback handling
* Curriculum validation
* Database round-trip verification

The project also contains automated tests for important learning-system components.

---

## 🏗️ Technology Stack

### Android

* Kotlin
* Jetpack Compose
* Android SDK
* Room Database
* KSP
* DataStore

### Learning System

* Spaced Repetition
* Adaptive Learning
* Learner Model
* Knowledge Graph
* Curriculum Engine
* Mistake Tracking
* Daily Study Planning

### AI

* Gemini-based AI integration
* Structured AI responses
* Offline/fallback mechanisms

### Data

* JSON
* SHA-256
* Local database storage
* Curriculum validation

---

## 📊 Current Project Status

The current implementation contains:

| Component                         | Current Status |
| --------------------------------- | -------------- |
| Android application               | ✅ Implemented  |
| JLPT N5–N1 curriculum structure   | ✅ Implemented  |
| Curriculum database               | ✅ Implemented  |
| Kanji content                     | ✅ Implemented  |
| Vocabulary content                | ✅ Implemented  |
| Grammar content                   | ✅ Implemented  |
| Reading content                   | ⚠️ Partial     |
| Listening content                 | ⚠️ Partial     |
| JLPT practice questions           | ⚠️ Developing  |
| Spaced repetition                 | ✅ Implemented  |
| Learner model                     | ✅ Implemented  |
| Adaptive study planning           | ✅ Implemented  |
| Knowledge graph                   | ✅ Implemented  |
| Backup & restore                  | ✅ Implemented  |
| Database migration testing        | ✅ Tested       |
| Automated tests                   | ✅ Passing      |
| Source/provenance verification    | ⚠️ Ongoing     |
| Full JLPT curriculum completeness | ❌ Not claimed  |

### Current Curriculum Dataset

The latest verified curriculum expansion contains:

**649 curriculum items**

| Level     |   Items |
| --------- | ------: |
| N5        |     494 |
| N4        |      66 |
| N3        |      35 |
| N2        |      30 |
| N1        |      24 |
| **Total** | **649** |

The knowledge graph currently contains hundreds of relationships connecting curriculum items.

These numbers describe the application's current internal dataset and **do not represent official JLPT item counts or official JLPT requirements**.

---

## 🧪 Testing

The project includes automated tests covering areas such as:

* Curriculum asset validation
* Database insertion
* Database round-trip integrity
* Knowledge graph validation
* Learner model adaptation
* SRS preservation
* Backup verification
* Restore rollback
* Database migration
* Learning-system behavior

The latest development verification reported:

```text
34 tests passed
0 tests failed
Build: SUCCESS
```

Testing is continuously expanded as new learning-system functionality is implemented.

---

## 🗂️ Project Architecture

A simplified architecture:

```text
                    ┌──────────────────┐
                    │   Jetpack        │
                    │   Compose UI     │
                    └────────┬─────────┘
                             │
                             ▼
                    ┌──────────────────┐
                    │ Learning Engine  │
                    ├──────────────────┤
                    │ SRS              │
                    │ Learner Model    │
                    │ Study Planner    │
                    │ Mistake Tracking │
                    └────────┬─────────┘
                             │
              ┌──────────────┼──────────────┐
              ▼              ▼              ▼
       ┌────────────┐ ┌─────────────┐ ┌──────────────┐
       │ Curriculum │ │ Knowledge   │ │ AI Sensei    │
       │ Engine     │ │ Graph       │ │              │
       └─────┬──────┘ └──────┬──────┘ └──────────────┘
             │               │
             └───────┬───────┘
                     ▼
              ┌──────────────┐
              │ Room Database│
              └──────────────┘
                     │
                     ▼
              ┌──────────────┐
              │ Backup/Restore│
              └──────────────┘
```

---

## 🎯 Design Philosophy

Aiko-JLPT is being developed around several principles.

### 1. Content over UI

A beautiful interface does not make a good learning system.

The actual educational content stored in the application is what determines curriculum depth.

### 2. Weakness-driven learning

The application should prioritize areas where the learner actually struggles.

### 3. Connected knowledge

Japanese is not learned as independent lists of kanji, vocabulary, and grammar.

The system attempts to connect them.

### 4. Retrieval over passive reading

The learner should repeatedly retrieve information instead of only reading explanations.

### 5. Measurable progress

Learning progress should be based on actual learner data rather than simply counting completed screens or lessons.

### 6. Honest completeness

Aiko-JLPT does **not** claim to contain complete official JLPT N5–N1 material simply because the application has N5–N1 categories.

Curriculum completeness and content quality are treated as things that must be measured and verified.

---

## 🚧 Development Roadmap

### Phase 1 — Application Foundation

* [x] Android application
* [x] Jetpack Compose UI
* [x] Room database
* [x] DataStore
* [x] Curriculum ingestion
* [x] Backup and restore

### Phase 2 — Learning Engine

* [x] Spaced repetition
* [x] Learner model
* [x] Mistake tracking
* [x] Adaptive study planning
* [x] Daily learning recommendations

### Phase 3 — Knowledge Graph

* [x] Curriculum relationships
* [x] Graph validation
* [x] Knowledge-based recommendations
* [x] Integration with study planning

### Phase 4 — Curriculum Expansion

* [x] N5 expansion
* [ ] N4 expansion
* [ ] N3 expansion
* [ ] N2 expansion
* [ ] N1 expansion
* [ ] Curriculum quality audits
* [ ] Source/provenance verification

### Phase 5 — Learning Quality

* [ ] Deeper reading curriculum
* [ ] Deeper listening curriculum
* [ ] Expanded JLPT practice
* [ ] Better grammar progression
* [ ] Vocabulary quality validation
* [ ] Knowledge graph quality audit
* [ ] End-to-end learning validation

### Phase 6 — Advanced AI Learning

* [ ] Personalized AI tutoring
* [ ] Intelligent mistake explanations
* [ ] Conversational Japanese practice
* [ ] Context-aware recommendations
* [ ] AI-generated practice with validation
* [ ] Personalized learning paths

---

## 🔐 Data & Privacy

Aiko-JLPT is designed around local learning data storage.

The project aims to minimize unnecessary external transmission of personal learning information.

AI-powered functionality may require network access depending on the configured provider and feature.

Users should review the application's implementation and configuration before using it with sensitive information.

---

## ⚠️ Project Status

**Aiko-JLPT is an actively developed personal/portfolio project.**

The application is functional, but it should not currently be considered:

* An official JLPT application
* An official JLPT preparation resource
* A replacement for official JLPT materials
* A guaranteed complete N5–N1 curriculum
* An officially affiliated JLPT product

The curriculum and learning system are continuously being audited and expanded.

---

## 📜 Disclaimer

**Aiko-JLPT is an independent project and is not affiliated with, endorsed by, or sponsored by the Japan Foundation or JEES.**

JLPT is a trademark/name associated with its respective organizations.

---

## 👨‍💻 Author

**Linkesh K V**

B.Tech — Automation & Robotics Engineering

Interested in:

* Artificial Intelligence
* Robotics
* Machine Learning
* Autonomous Systems
* Japanese Language Learning

---

## ⭐ Project Vision

The long-term goal of Aiko-JLPT is to become more than a flashcard application.

The vision is an adaptive Japanese learning system that understands:

```text
What you know
      ↓
What you don't know
      ↓
Why you are struggling
      ↓
What knowledge is connected
      ↓
What you should practice next
      ↓
Whether you actually improved
```

**Aiko-JLPT — Learn Japanese. Understand your weaknesses. Master what matters.**
