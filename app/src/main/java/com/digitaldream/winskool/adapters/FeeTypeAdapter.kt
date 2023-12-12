package com.digitaldream.winskool.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.digitaldream.winskool.R
import com.digitaldream.winskool.dialog.FeeTypeDialog
import com.digitaldream.winskool.models.FeeTypeModel
import com.digitaldream.winskool.models.ItemViewModel
import java.util.*

class FeeTypeAdapter(
    private val sContext: Context,
    private val sFeeList: MutableList<FeeTypeModel>,
    private val sErrorMessage: TextView
) : RecyclerView.Adapter<FeeTypeAdapter.ViewHolder>() {

    private lateinit var mItemViewModel: ItemViewModel
    private var isEnabled = false
    private var selectedItems = arrayListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(sContext).inflate(
            R.layout.fragment_fee_setup_item, parent, false
        )

        mItemViewModel = ViewModelProvider(sContext as FragmentActivity)[ItemViewModel::class.java]

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val feeTypeModel = sFeeList[position]
        holder.feeName.text = feeTypeModel.getFeeName()

        if (feeTypeModel.isMandatory().equals("1"))
            holder.mandatory.text = sContext.getString(R.string.mandatory)
        else holder.mandatory.text = sContext.getString(R.string.not_mandatory)


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
                                    for (itemId in selectedItems)
                                        deleteFeeName(itemId)
                                }
                                setNegativeButton("No", null)
                                show()
                            }
                        }
                        if (sFeeList.size == 0) {
                            sErrorMessage.isVisible = true
                            sErrorMessage.text = sContext.getString(R.string.nothing_to_show)
                        }
                        mode.finish()

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
            // if action mode is enabled continue show selections else open edit
            if (isEnabled) {
                clickedItems(holder)
            } else {
                val feeName = feeTypeModel.getFeeName().toString()
                val mandatory = feeTypeModel.isMandatory().toString()
                val feeId = feeTypeModel.getFeeId()

                val feeTypeDialog =
                    FeeTypeDialog(sContext, "edit", feeName, mandatory, feeId)
                feeTypeDialog.setCancelable(true)
                feeTypeDialog.show()
                val window = feeTypeDialog.window
                window!!.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        }

    }

    override fun getItemCount() = sFeeList.size


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val feeName: TextView = itemView.findViewById(R.id.fee_name)
        val mandatory: TextView = itemView.findViewById(R.id.mandatory_text)
        val checkBox: ImageView = itemView.findViewById(R.id.check_box)

    }

    private fun clickedItems(holder: ViewHolder) {

        // get selected items value
        val value = sFeeList[holder.adapterPosition]

        if (!holder.checkBox.isVisible) {
            holder.checkBox.isVisible = true
            holder.itemView.setBackgroundColor(Color.GRAY)

            selectedItems.add(value.getFeeId().toString())
        } else {
            holder.checkBox.isVisible = false
            selectedItems.remove(value.getFeeId().toString())
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
        }

        mItemViewModel.setText(selectedItems.size.toString())

    }

    private fun deleteFeeName(sId: String) {

        val sharedPreferences = sContext.getSharedPreferences(
            "loginDetail", Context.MODE_PRIVATE
        )
        val db = sharedPreferences.getString("db", "")

        val url = sContext.getString(R.string.base_url) + "/manageFees.php"
        val stringRequest: StringRequest = object : StringRequest(Method.POST, url,
            { response: String ->
                Log.d("TAG", response)
                //  val jsonObject = JSONObject(response)
                // val status = jsonObject.getString("status")

            }, { error: VolleyError ->
                error.printStackTrace()
                Toast.makeText(sContext, error.message, Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                val stringMap: MutableMap<String, String> = HashMap()
                println(sId)
                stringMap["delete"] = sId
                stringMap["_db"] = db!!
                return stringMap
            }
        }
        val requestQueue: RequestQueue = Volley.newRequestQueue(sContext)
        requestQueue.add(stringRequest)
    }

}