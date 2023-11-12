package models

import java.sql.Timestamp

class ChatMessage (
    val text : String,
    val id : String,
    val fromId : String,
    val toID : String,
    val timestamp: Long
) {
    constructor() : this("", "", "", "", -1)
}