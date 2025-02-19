package com.example.firebase

data class NoteItem(val noteTitle: String, val noteDescription: String, val noteId: String) {
    constructor() : this("", "", "")
}