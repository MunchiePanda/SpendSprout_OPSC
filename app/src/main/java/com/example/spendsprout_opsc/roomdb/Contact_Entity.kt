package com.example.spendsprout_opsc.roomdb

data class Contact_Entity(
    var id: Int = 0,
    var contactName: String = "",
    var contactId: Long? = null,
    var contactBalance: Double = 0.0,
    var contactNotes: String? = null,
)
