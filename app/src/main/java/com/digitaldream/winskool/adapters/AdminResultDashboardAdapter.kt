package com.digitaldream.winskool.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.dialog.TermResultDialog
import com.digitaldream.winskool.models.AdminResultDashboardModel
import com.digitaldream.winskool.models.AdminResultTermModel
import com.digitaldream.winskool.utils.FunctionUtils.animateObject

class AdminResultDashboardAdapter(
    private val itemList: MutableList<AdminResultDashboardModel>,
    private val classId: String
) : RecyclerView.Adapter<AdminResultDashboardAdapter.ItemViewHolder>() {

    private lateinit var termAdapter: GenericAdapter2<AdminResultTermModel>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_admin_session_result, parent, false
        )

        return ItemViewHolder(view)

    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount() = itemList.size

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sessionTextView: TextView = itemView.findViewById(R.id.sessionTextView)
        private val termRecyclerView: RecyclerView = itemView.findViewById(R.id.termRecyclerView)


        fun bind(itemModel: AdminResultDashboardModel) {
            sessionTextView.text = itemModel.session

            setUpTermAdapter(itemModel.termList)
        }

        private fun setUpTermAdapter(termList: MutableList<AdminResultTermModel>) {
            termAdapter = GenericAdapter2(
                termList,
                R.layout.item_admin_term_result,
                bindItem = { itemView, model, _ ->
                    val termText: TextView = itemView.findViewById(R.id.termTextView)
                    val progressText: TextView = itemView.findViewById(R.id.progressTextView)
                    val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

                    termText.text = model.term
                    animateObject(progressBar, progressText, 52)

                    itemView.setOnClickListener {
                        TermResultDialog(
                            itemView.context,
                            classId,
                            null,
                            model.session,
                            model.term,
                            ""
                        ).apply {
                            show()
                        }.window?.setLayout(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    }
                }
            )

            setUpRecyclerView()
        }

        private fun setUpRecyclerView() {
            termRecyclerView.apply {
                hasFixedSize()
                layoutManager = LinearLayoutManager(itemView.context)
                adapter = termAdapter
            }
        }
    }

}