package com.linkkeeper

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.linkkeeper.model.LinkItem
import kotlinx.android.synthetic.main.fragment_read_tab.*


class ReadTabFragment : TabContentFragment, Fragment() {

    private lateinit var listener: ReadTabFragmentListener
    private val links: MutableList<LinkItem> = mutableListOf()

    companion object {
        @JvmStatic
        fun newInstance() =
            ReadTabFragment().apply {
                arguments = Bundle().apply {
            }
        }
    }

    interface ReadTabFragmentListener {
        fun deleteExpiredLinks()
        fun loadReadLinks() : Collection<LinkItem>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_read_tab, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ReadTabFragmentListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement ReadTabFragmentListener")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        descLabel.text = "To be deleted in (hour/minute)"
        linkList.layoutManager = LinearLayoutManager(this.context)
        linkList.adapter = LinkItemAdapter(links, this.context, this, LinkItemAdapter.EXPIRED_DATE)
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible && isResumed) {
            listener.deleteExpiredLinks()
            populateLinks()
        }
    }

    override fun refreshViewsVisibility() {
        val linkSize = links.size
        if (linkSize <= 0 && linkList.visibility == View.VISIBLE) {
            linkList.visibility = View.GONE
            topPane.visibility = View.GONE
            emptyLinkLabel.visibility = View.VISIBLE
            emptyLinkImg.visibility = View.VISIBLE
        } else if (linkSize > 0 && linkList.visibility == View.GONE) {
            emptyLinkLabel.visibility = View.GONE
            emptyLinkImg.visibility = View.GONE
            linkList.visibility = View.VISIBLE
            topPane.visibility = View.VISIBLE
        }
        linkCountLabel.text = linkSize.toString() + " link" + (if (linkSize > 1) "s" else "")
    }

    private fun populateLinks() {
        links.clear()
        links.addAll(listener.loadReadLinks())
        linkList.adapter.notifyDataSetChanged()
        refreshViewsVisibility()
    }

}
