package com.digitaldream.linkskool.models

data class TimeFrameDataModel(val getData: () -> Unit) {

    var startDate: String? = null
    var endDate: String? = null
    var grouping: String? = null
    var duration: String? = null
    var filter: String? = null
    var levelData: String? = null
    var classData: String? = null
    var vendor: String? = null
    var account: String? = null
    var term: String? = null
    var year: String? = null

}