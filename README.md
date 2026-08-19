# 📒 Address Book System

A simple Java command-line application for managing contacts with persistent file storage.

The project demonstrates fundamental Java programming concepts through a practical address-book workflow: adding contacts, searching records, deleting entries, displaying all contacts, validating input, and saving data to a text file.

## ✨ Features

- Add new contacts
- Search contacts by name
- Search contacts by phone number
- Delete contacts by name
- Delete contacts by phone number
- Display all saved contacts
- Prevent duplicate phone numbers
- Validate contact categories
- Save contacts automatically to `contacts.txt`
- Load saved contacts when the program starts

## 🛠️ Technologies

- Java
- ArrayList
- File I/O
- Scanner
- Command-Line Interface (CLI)

## 🧠 Concepts Practiced

- Classes and objects
- Methods
- ArrayList collections
- Loops and conditionals
- Switch statements
- Input validation
- CRUD operations
- Reading and writing text files
- Basic exception handling

## 📂 Project Structure

```text
Address_Book_System/
├── src/
│   └── Said_120253338.java
├── contacts.txt
└── README.md
```

## ▶️ How to Run

1. Make sure Java is installed.
2. Open the project in IntelliJ IDEA or another Java IDE.
3. Run:

```text
src/Said_120253338.java
```

You can also compile and run it from the terminal using Java commands.

## 📋 Contact Types

The application supports these categories:

- Family
- Personal
- Work
- Other

Invalid categories are automatically treated as `Other`.

## 💾 Data Storage

Contacts are stored in:

```text
contacts.txt
```

Each record is saved using a simple pipe-separated format:

```text
name|type|number
```

## 🎯 Project Purpose

This project was created to practice core Java programming and file handling through a small but complete CRUD application.

---

**Author:** Said Ahmed Abu-Fouda
