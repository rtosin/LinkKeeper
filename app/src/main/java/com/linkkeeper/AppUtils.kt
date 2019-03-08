package com.linkkeeper

import java.text.SimpleDateFormat
import java.util.Date

class AppUtils {

    companion object {

        val dateSdf = SimpleDateFormat("dd")
        val monthSdf = SimpleDateFormat("MMM")
        val hourSdf = SimpleDateFormat("HH")
        val minuteSdf = SimpleDateFormat("mm")

        fun getDay(date: Date): String {
            return dateSdf.format(date)
        }

        fun getMonth(date: Date): String {
            return monthSdf.format(date)
        }

        fun getHour(date: Date): String {
            return hourSdf.format(date)
        }

        fun getMinute(date: Date): String {
            return minuteSdf.format(date)
        }

    }
}