
package com.SBMH.SpendSprout.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Budget(
    var id: String = "",
    var name: String = "",
    var amount: Double = 0.0,
    var category: String = "",
    var startDate: Long = 0,
    var endDate: Long = 0
)
