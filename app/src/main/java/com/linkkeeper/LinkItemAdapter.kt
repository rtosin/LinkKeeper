package com.linkkeeper

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.linkkeeper.model.LinkItem
import kotlinx.android.synthetic.main.link_list_row.view.*
import java.util.*

class LinkItemAdapter(private val items: MutableList<LinkItem>,
                      private val context: Context,
                      private val tabContentFragment: TabContentFragment,
                      private val dayDisplayMode: Int) : RecyclerView.Adapter<ViewHolder>() {

    companion object {
        const val CREATED_DATE = 1
        const val EXPIRED_DATE = 2
    }

    private var mainActivity = context as MainActivity

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.link_list_row, parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.deleteButton.visibility = View.GONE

        val linkItem = items.get(position)
        holder.linkLabel.text = linkItem.link

        if (dayDisplayMode == CREATED_DATE) {
            holder.dateLabel.text = AppUtils.getDay(linkItem.createdDate)
            holder.monthLabel.text = AppUtils.getMonth(linkItem.createdDate)
        } else if (dayDisplayMode == EXPIRED_DATE) {
            val diff = (linkItem.readDate + LinkItem.EXPIRED_IN_MILLIS) - Date().time
            if (diff > 0) {
                val hour = (diff / 3600000).toBigDecimal();
                val min = ((diff % 3600000) / 60000).toBigDecimal()
                holder.dateLabel.text = String.format("%02d", hour.toInt())
                holder.monthLabel.text = String.format("%02d", min.toInt())
            } else {
                holder.dateLabel.text = ""
                holder.monthLabel.text = ""
            }
        }
        holder.listRow.setOnLongClickListener { toggleDeleteButtonVisibility(holder.deleteButton, true) }
        holder.listRow.setOnClickListener { onLinkClick(items.indexOf(linkItem), holder.deleteButton) }
        holder.deleteButton.setOnClickListener{ deleteLink(items.indexOf(linkItem)) }
    }

    private fun deleteLink(position: Int) {
        if (position > -1 && position < items.size) {
            mainActivity.deleteLink(items.get(position).id)
            items.removeAt(position)
            this.notifyItemRemoved(position)
            tabContentFragment.refreshViewsVisibility()
        }
    }

    private fun toggleDeleteButtonVisibility(deleteButton: View, show: Boolean) : Boolean {
        if (show && deleteButton.visibility == View.GONE) {
            deleteButton.visibility = View.VISIBLE
        } else if (!show && deleteButton.visibility == View.VISIBLE) {
            deleteButton.visibility = View.GONE
        }
        return true
    }

    private fun onLinkClick(position: Int, deleteButton: View) {
        if (deleteButton.visibility == View.VISIBLE) {
            toggleDeleteButtonVisibility(deleteButton, false)
        } else {
            if (position > -1 && position < items.size) {
                mainActivity.setLinkAsRead(items.get(position))
                items.removeAt(position)
                this.notifyItemRemoved(position)
                tabContentFragment.refreshViewsVisibility()
            }
        }
    }

}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val linkLabel: TextView = view.linkURLLabel
    val dateLabel: TextView = view.dateLabel
    val monthLabel: TextView = view.monthLabel
    val deleteButton: View = view.deleteButton
    var listRow: View = view.listRow
}