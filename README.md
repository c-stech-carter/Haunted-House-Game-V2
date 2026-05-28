# Haunted House Project — Version 2.0.0 (Archival Repository)

Welcome to the historical v2.0.0 repository of the Haunted House Project. This standalone repository is preserved in its original state to showcase the evolutionary development, structural milestones, and logic design of the engine prior to the standalone optimizations introduced in later major versions.

A **gothic-industrial text adventure game** built using **JavaFX**, where players navigate a surreal nightmare realm, solve environmental lock puzzles, and manage an item inventory to secure an escape.

## 📸 Screenshots
| First 'Room' of the Game | Room Example 2 |
| :---: | :---: |
| ![First Room](src/resources/image/FrontYard.png) | ![Room Example](src/resources/image/Parlor.png) |

## 🏛️ Project Purpose & Context
This game was developed as one of my final projects for my **STECH** class to synthesize and demonstrate core Java programming paradigms, object-oriented design, dynamic event handling, and desktop GUI construction. 

---

## 🎨 Aesthetic & Assets
The visual narrative relies heavily on a grim, atmospheric aesthetic:
* **Background Environments:** The gothic, industrial pixel art rooms were engineered using generative frameworks (**DALL-E 3** and **FLUX.1-dev**).
* **Inventory Icons:** Item sprites utilize traditional 16/32-bit pixel art styles sourced via open-license creators on **itch.io**.

---

## ⚙️ Core Architecture (At a Glance)
* **State Management:** Room layout, exit routing, and item verification are handled cleanly via decoupled collections (`HashMap`, `ArrayList`, and `HashSet`).
* **JavaFX Event Pipeline:** Features a dual-input control layout. Players can trigger `Look Around` or `Search` actions using the top global menu bar or via a context-aware right-click menu.
* **Visual Transitions:** Employs synchronized, asynchronous `FadeTransition` sequences to mask background assets loading into memory during room switches.

---

## 🛠️ Archival Installation & Execution Notes

> [!IMPORTANT]
> **Pathing Constraint Note:** This historical version utilizes direct relative file paths (`file:src/resources/...`) optimized for desktop IDE runtime environments. It is not configured as a standalone JAR.

To run this specific build without asset breaking:
1. **Clone this repository:**
   ```sh
   git clone [https://github.com/your-username/HauntedHouse-v2.0.git](https://github.com/your-username/HauntedHouse-v2.0.git)


## 🚀 The Development Timeline
To see how this project evolved across different architectural stages, visit the other iterations in this progression index:
* **The Absolute Baseline:** [HauntedHouse-v1.0](https://github.com/c-stech-carter/HauntedHouse-v1.0) — The original prototype utilizing a bottom ComboBox menu system for navigation and native multimedia tracks driven straight out of Main.java.
* **The Current Milestone:** [HauntedHouse-v2.0](https://github.com/c-stech-carter/HauntedHouse-v2.0) — Introduces centralized GameWindow staging, a context-aware right-click menu system, interactive room searching, and an encapsulated inventory tracking system.
* **The Final Production Build:** [HauntedHouse-Release](https://github.com/c-stech-carter/HauntedHouse-Release) — Features complete project modernization with Gradle build automation, secure classpath resource mapping, and an optimized standalone bundle configuration.

***

### 🕒 Snapshot Date: March 2025 Milestone
