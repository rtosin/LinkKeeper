package com.linkkeeper

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.linkkeeper.model.LinkItem
import kotlinx.android.synthetic.main.fragment_new_tab.*

class NewTabFragment : TabContentFragment, Fragment() {

    private lateinit var listener: NewTabFragmentListener
    private val links: MutableList<LinkItem> = mutableListOf()

    companion object {
        @JvmStatic
        fun newInstance() =
                NewTabFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }

    interface NewTabFragmentListener {
        fun loadNewLinks() : Collection<LinkItem>
        fun addLink()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_tab, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NewTabFragmentListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement NewTabFragmentListener")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        descLabel.text = ""
        linkList.layoutManager = LinearLayoutManager(this.context)
        linkList.adapter = LinkItemAdapter(links, this.context, this, LinkItemAdapter.CREATED_DATE)
        addButton.setOnClickListener { this.addLink() }
    }

    override fun onStart() {
        super.onStart()
        populateLinks()
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
        links.addAll(listener.loadNewLinks())
        linkList.adapter.notifyDataSetChanged()
        refreshViewsVisibility()
    }

    private fun addLink() {
        listener.addLink()
        populateLinks()
    }

}
