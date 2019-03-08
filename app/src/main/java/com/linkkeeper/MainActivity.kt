package com.linkkeeper

import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import com.linkkeeper.model.LinkItem
import com.linkkeeper.model.LinkItemContract
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.content.Intent
import android.net.Uri
import android.util.Patterns


class MainActivity : NewTabFragment.NewTabFragmentListener,
        ReadTabFragment.ReadTabFragmentListener,
        AppCompatActivity() {

    private lateinit var tabAdapter: TabFragmentPagerAdapter

    private val dbHelper = LinkKeeperDbHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tabAdapter = TabFragmentPagerAdapter(supportFragmentManager)
        viewpager.adapter = tabAdapter
        slidingTab.setupWithViewPager(viewpager)
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }

    override fun loadNewLinks() : MutableList<LinkItem> {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(BaseColumns._ID,
                LinkItemContract.LinkItem.COLUMN_NAME_LINK,
                LinkItemContract.LinkItem.COLUMN_NAME_STATUS,
                LinkItemContract.LinkItem.COLUMN_NAME_CREATED_DATE)
        val selection = "${LinkItemContract.LinkItem.COLUMN_NAME_STATUS} = ?"
        val selectionArgs = arrayOf(LinkItem.STATUS_NEW.toString())
        val sortOrder = "${LinkItemContract.LinkItem.COLUMN_NAME_CREATED_DATE} ASC"
        val cursor = db.query(
                LinkItemContract.LinkItem.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        )
        val links = mutableListOf<LinkItem>()
        with (cursor) {
            while (moveToNext()) {
                links.add(LinkItem(
                        getLong(0),
                        getString(1),
                        Date(getLong(3)),
                        getInt(2)
                ))
            }
        }
        return links
    }

    override fun addLink() {
        val url = getLinkFromClipboard()
        if (!url.equals("")) {
            val newLink = LinkItem(0, url, Date(), LinkItem.STATUS_NEW)

            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put(LinkItemContract.LinkItem.COLUMN_NAME_LINK, newLink.link)
                put(LinkItemContract.LinkItem.COLUMN_NAME_STATUS, newLink.status)
                put(LinkItemContract.LinkItem.COLUMN_NAME_CREATED_DATE, newLink.createdDate.time)
            }
            db.insert(LinkItemContract.LinkItem.TABLE_NAME, null, values)
        }
    }

    override fun deleteExpiredLinks() {
        val db = dbHelper.writableDatabase
        val selection = "CAST(? AS INTEGER) - ${LinkItemContract.LinkItem.COLUMN_NAME_READ_DATE} > CAST(? AS INTEGER)"
        val selectionArgs = arrayOf(Date().time.toString(), LinkItem.EXPIRED_IN_MILLIS.toString())
        db.delete(LinkItemContract.LinkItem.TABLE_NAME, selection, selectionArgs)
    }

    override fun loadReadLinks() : MutableList<LinkItem> {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(BaseColumns._ID,
                LinkItemContract.LinkItem.COLUMN_NAME_LINK,
                LinkItemContract.LinkItem.COLUMN_NAME_STATUS,
                LinkItemContract.LinkItem.COLUMN_NAME_CREATED_DATE,
                LinkItemContract.LinkItem.COLUMN_NAME_READ_DATE
                )
        val selection = "${LinkItemContract.LinkItem.COLUMN_NAME_STATUS} = ?"
        val selectionArgs = arrayOf(LinkItem.STATUS_READ.toString())
        val sortOrder = "${LinkItemContract.LinkItem.COLUMN_NAME_READ_DATE} ASC"
        val cursor = db.query(
                LinkItemContract.LinkItem.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        )
        val links = mutableListOf<LinkItem>()
        with (cursor) {
            while (moveToNext()) {
                links.add(LinkItem(
                        getLong(0),
                        getString(1),
                        Date(getLong(3)),
                        getInt(2),
                        getLong(4)
                ))
            }
        }
        return links
    }

    fun deleteLink(linkId: Long) {
        val db = dbHelper.writableDatabase
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(linkId.toString())
        db.delete(LinkItemContract.LinkItem.TABLE_NAME, selection, selectionArgs)
    }

    fun setLinkAsRead(linkItem: LinkItem) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(LinkItemContract.LinkItem.COLUMN_NAME_STATUS, LinkItem.STATUS_READ)
            put(LinkItemContract.LinkItem.COLUMN_NAME_READ_DATE, Date().time)
        }
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(linkItem.id.toString())
        db.update(LinkItemContract.LinkItem.TABLE_NAME, values, selection, selectionArgs)

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkItem.link))
        startActivity(intent)
    }

    private fun getLinkFromClipboard() : String {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        var url: String
        try {
            url = clipboard.primaryClip.getItemAt(0).text.toString()
            if (!Patterns.WEB_URL.matcher(url).matches()) url = ""
        } catch (ex: Exception) {
            ex.printStackTrace(System.err)
            url = ""
        }
        return url
    }

}
