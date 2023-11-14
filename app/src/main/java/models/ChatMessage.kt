package models

class ChatMessage (
    val text : String,
    val id : String,
    val fromId : String,
    val toId : String,
    val timestamp: Long
) {
    constructor() : this("", "", "", "", -1)
}