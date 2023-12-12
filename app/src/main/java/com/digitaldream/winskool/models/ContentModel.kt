package com.digitaldream.winskool.models

data class ContentModel(
    val id: String,
    val title: String,
    val description: String,
    val courseId: String,
    val levelId: String,
    val authorId: String,
    val authorName: String,
    val date: String,
    val term: String,
    val type: String,
    val viewType: String,
    val category: String = ""
)

//class ContentComparator() : Comparator<ContentModel> {
//    override fun compare(o1: ContentModel?, o2: ContentModel?): Int {
//
//        return when {
//            o1?.category.isNullOrEmpty() && o2?.category.isNullOrEmpty() -> 0
//            o1?.category.isNullOrEmpty() -> -1
//            o2?.category.isNullOrEmpty() -> 1
//            else -> {
//                val topicComparison = o1?.category?.compareTo(o2?.category!!)
//                if (topicComparison != 0) {
//                    topicComparison.takeIf {  }
//                } else {
//                    o1.category.compareTo(o2?.category!!)
//                }
//            }
//        }
//
//    }
//}
