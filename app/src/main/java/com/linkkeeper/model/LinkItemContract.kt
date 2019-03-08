package com.linkkeeper.model

import android.provider.BaseColumns

object LinkItemContract {

    object LinkItem : BaseColumns {
        const val TABLE_NAME = "link_item"
        const val COLUMN_NAME_LINK = "link"
        const val COLUMN_NAME_STATUS = "status"
        const val COLUMN_NAME_CREATED_DATE = "created_date"
        const val COLUMN_NAME_READ_DATE = "read_date"
    }

    const val SQL_CREATE_TABLES =
            "CREATE TABLE ${LinkItem.TABLE_NAME} (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
                    "${LinkItem.COLUMN_NAME_LINK} TEXT, " +
                    "${LinkItem.COLUMN_NAME_STATUS} INT, " +
                    "${LinkItem.COLUMN_NAME_CREATED_DATE} INT, " +
                    "${LinkItem.COLUMN_NAME_READ_DATE} INT)"

    const val SQL_DROP_TABLES =
            "DROP TABLE IF EXISTS ${LinkItem.TABLE_NAME}"

}