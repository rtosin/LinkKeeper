package com.linkkeeper.model

import java.util.Date

data class LinkItem(val id: Long = 0, val link: String, val createdDate: Date, val status: Int, val readDate: Long = 0) {

    companion object {

        const val STATUS_NEW = 1
        const val STATUS_READ = 2
        const val STATUS_ARCHIVE = 3

        const val EXPIRED_IN_MILLIS = 172800000;// 2 days

    }

}