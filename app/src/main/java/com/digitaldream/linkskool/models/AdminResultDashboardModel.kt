package com.digitaldream.linkskool.models

data class AdminResultDashboardModel(
    var session: String,
    var termList: MutableList<AdminResultTermModel>
)

data class AdminResultTermModel(var term: String, var session: String)
