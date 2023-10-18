package com.digitaldream.linkskool.adapters

import android.content.Context
import android.graphics.Color
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.dialog.AccountSetupDialog
import com.digitaldream.linkskool.dialog.OnInputListener
import com.digitaldream.linkskool.models.AccountSetupDataModel
import com.digitaldream.linkskool.models.ItemViewModel
import java.util.*

class AccountSetupAdapter(
    private val sContext: Context,
    private val sAccountModel: MutableList<AccountSetupDataModel>,
    private val sErrorMessage: TextView
) : RecyclerView.Adapter<AccountSetupAdapter.ViewHolder>(), OnInputListener {

    private lateinit var mItemViewModel: ItemViewModel
    private var isEnabled = false
    private var selectedItems = arrayListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout
                .fragment_setup_account_item, parent, false
        )

        mItemViewModel = ViewModelProvider(sContext as FragmentActivity)[ItemViewModel::class.java]

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mModel = sAccountModel[position]
        holder.mAccountName.text = mModel.mAccountName
        holder.mAccountId.text = mModel.mAccountId

        holder.itemView.setOnLongClickListener {
            if (!isEnabled) {
                val callback: ActionMode.Callback = object : ActionMode.Callback {
                    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {

                        ((sContext as AppCompatActivity)).supportActionBar!!.hide()
                        val menuInflater = mode.menuInflater
                        menuInflater.inflate(R.menu.pop_up_fee_menu, menu)

                        return true
                    }

                    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                        isEnabled = true

                        clickedItems(holder)

                        mItemViewModel.getText().observe(
                            (sContext as LifecycleOwner)
                        ) { sValue ->

                            when (sValue) {
                                "0" -> mode.finish()
                                "1" -> mode.title = String.format(
                                    Locale.getDefault(),
                                    "%s item selected", sValue
                                )
                                else -> mode.title = String.format(
                                    Locale.getDefault(),
                                    "%s items selected", sValue
                                )
                            }
                        }

                        return true
                    }

                    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {

                        if (item.itemId == R.id.delete) {
                            AlertDialog.Builder(sContext).apply {
                                setTitle("Warning!")
                                setMessage("Are you sure to delete?")
                                setCancelable(false)
                                setPositiveButton("Yes") { _, _ ->
                                    println("Deleted")
                                    setNegativeButton("No", null)
                                    show()
                                }
                            }
                            if (sAccountModel.size == 0) {
                                sErrorMessage.isVisible = true
                                sErrorMessage.text = sContext.getString(R.string.nothing_to_show)
                            }
                            mode.finish()

                        }
                        return true
                    }

                    override fun onDestroyActionMode(mode: ActionMode) {
                        isEnabled = false
                        selectedItems.clear()
                        ((sContext as AppCompatActivity)).supportActionBar!!.show()
                        notifyDataSetChanged()

                    }
                }

                (it.context as AppCompatActivity).startActionMode(callback)

            } else {
                clickedItems(holder)
            }

            true
        }

        holder.itemView.setOnClickListener {
            // if action mode is enabled continue selections else open edit
            if (isEnabled) {
                clickedItems(holder)
            } else {
                val id = mModel.mId
                val accountId = mModel.mAccountId
                val accountName = mModel.mAccountName
                val accountType = mModel.mAccountType

                val accountDialog = AccountSetupDialog(
                    sContext,
                    "edit",
                    id.toString(),
                    accountName!!,
                    accountId!!,
                    accountType!!,
                    this
                ).apply {
                    setCancelable(true)
                    show()
                }
                val window = accountDialog.window
                window!!.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        }
    }

    override fun getItemCount() = sAccountModel.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mAccountName: TextView = itemView.findViewById(R.id.account_name)
        val mAccountId: TextView = itemView.findViewById(R.id.account_id)
        val mCheckBtn: ImageView = itemView.findViewById(R.id.check_box)
    }

    private fun clickedItems(holder: ViewHolder) {
        // get selected items value
        val value = sAccountModel[holder.adapterPosition]

        if (!holder.mCheckBtn.isVisible) {
            holder.mCheckBtn.isVisible = true
            holder.itemView.setBackgroundColor(Color.GRAY)

            selectedItems.add(value.mId.toString())
        } else {
            holder.mCheckBtn.isVisible = false
            selectedItems.remove(value.mId.toString())
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
        }
        mItemViewModel.setText(selectedItems.size.toString())

    }

    override fun sendInput(input: String) {

    }

    override fun sendId(levelId: String) {

    }
}