# Catapult - Cat Knowledge Quiz & Catalog

Welcome to **Catapult**, an Android application designed to provide users with an engaging experience of learning about various cat breeds and testing their knowledge through quizzes. This README will guide you through the project setup, structure, and functionality.

## Table of Contents
1. [Project Overview](#project-overview)
2. [Features](#features)
3. [Installation](#installation)
4. [Usage](#usage)
5. [Technologies Used](#technologies-used)
6. [API Documentation](#api-documentation)

## Project Overview

**Catapult** is a Kotlin-based Android application that offers:
- A catalog of various cat breeds.
- Detailed information about each breed.
- A photo gallery for each breed.
- A knowledge quiz about cats.
- A leaderboard to track quiz scores.

## Features

### 1. Creating Local Account
- On the first start, users must create a local account with the following details:
  - Name and surname
  - Nickname (no spaces, only letters, numbers, and underscores)
  - Valid email address
- If the account already exists, users are directed to the main screen.

### 2. Catalog of Cat Breeds
- List of breeds with a search option.
- Detailed information about each breed.
- Photo gallery of selected breeds.
- Full-screen photo viewer with swipe functionality.

### 3. Cat Knowledge Quiz
- 20 questions displayed one by one in full screen.
- Questions generated randomly from three main categories:
  - Guess the Fact
  - Guess the Cat
  - Left or Right Cat
- Scoring and time-limited gameplay.
- Option to post results to a global leaderboard.

### 4. Leaderboard
- Global leaderboard displaying the best scores.
- Sorted by category and overall performance.

### 5. Account Details
- View current account information.
- Edit account details with validation.
- View historical quiz results and best scores.

## Usage

1. **Creating Local Account**
   - On the first start, users must create a local account with the following details:
     - Name and surname
     - Nickname (no spaces, only letters, numbers, and underscores)
     - Valid email address
   - If the account already exists, users are directed to the main screen.

2. **Catalog of Cat Breeds**
   - List of breeds with a search option.
   - Detailed information about each breed.
   - Photo gallery of selected breeds.
   - Full-screen photo viewer with swipe functionality.

3. **Cat Knowledge Quiz**
   - 20 questions displayed one by one in full screen.
   - Questions generated randomly from three main categories:
     - Guess the Fact
     - Guess the Cat
     - Left or Right Cat
   - Scoring and time-limited gameplay.
   - Option to post results to a global leaderboard.

4. **Leaderboard**
   - Global leaderboard displaying the best scores.
   - Sorted by category and overall performance.

5. **Account Details**
   - View current account information.
   - Edit account details with validation.
   - View historical quiz results and best scores.

## Technologies Used

- **Programming Language:** Kotlin
- **Architecture:** MVVM (Model-View-ViewModel)
- **UI Framework:** Jetpack Compose
- **Networking:** Retrofit, OkHttp
- **Image Loading:** Coil
- **Database:** Room
- **Dependency Injection:** Hilt
- **Testing:** JUnit, Espresso

## API Documentation

The application fetches data from the following API endpoints:

1. **Cat Breeds List**
   - **Endpoint:** `/breeds`
   - **Method:** GET
   - **Description:** Fetches a list of all cat breeds.

2. **Breed Details**
   - **Endpoint:** `/breeds/{id}`
   - **Method:** GET
   - **Description:** Fetches detailed information about a specific breed.

3. **Breed Images**
   - **Endpoint:** `/breeds/{id}/images`
   - **Method:** GET
   - **Description:** Fetches images for a specific breed.

4. **Quiz Questions**
   - **Endpoint:** `/quiz/questions`
   - **Method:** GET
   - **Description:** Fetches a list of quiz questions.

5. **Submit Quiz Results**
   - **Endpoint:** `/quiz/results`
   - **Method:** POST
   - **Description:** Submits the user's quiz results.

6. **Leaderboard**
   - **Endpoint:** `/leaderboard`
   - **Method:** GET
   - **Description:** Fetches the leaderboard data.
