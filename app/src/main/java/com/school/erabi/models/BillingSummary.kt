package com.school.erabi.models

data class BillingSummary(
    var billMonth : Int,
    var billYear: Int,
    var totalBilled : Double,
    var totalPending : Double,
    var studentCount: Int
)
