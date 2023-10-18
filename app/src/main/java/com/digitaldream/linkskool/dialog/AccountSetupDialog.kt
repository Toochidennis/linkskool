package com.digitaldream.linkskool.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.digitaldream.linkskool.R
import org.json.JSONObject

class AccountSetupDialog(
    sContext: Context,
    private val sFrom: String,
    private val sId: String,
    private val sAccountName: String,
    private val sAccountId: String,
    private val sAccountType: String,
    private var sInputListener: OnInputListener
) : Dialog(sContext) {

    private lateinit var mAccountName: EditText
    private lateinit var mAccountId: EditText
    private lateinit var mAccountType: Spinner
    private lateinit var mBackBtn: ImageView
    private lateinit var mDoneBtn: Button

    private var mSelectedAccount: String? = null
    private var mDb: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.attributes.windowAnimations = R.style.DialogAnimation
        window!!.setGravity(Gravity.BOTTOM)

        setContentView(R.layout.dialog_setup_account)

        mAccountName = findViewById(R.id.account_name)
        mAccountId = findViewById(R.id.account_id)
        mAccountType = findViewById(R.id.spinner_account_type)
        mBackBtn = findViewById(R.id.back_btn)
        mDoneBtn = findViewById(R.id.add_account_btn)

        val sharedPreferences = context.getSharedPreferences(
            "loginDetail",
            Context.MODE_PRIVATE
        )
        mDb = sharedPreferences.getString("db", "")

        val accountTypes = context.resources.getStringArray(R.array.account_type)
        val spinnerAdapter = ArrayAdapter(
            context, android.R.layout.simple_spinner_dropdown_item, accountTypes
        )
        mAccountType.adapter = spinnerAdapter

        mAccountType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                mSelectedAccount = accountTypes[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        mBackBtn.setOnClickListener {
            dismiss()
        }

        mDoneBtn.setOnClickListener {

            if (validation()) {
                setAccount()
                sInputListener.sendInput("refresh")
                dismiss()
            } else {
                Toast.makeText(
                    context, "Empty field(s) identified", Toast
                        .LENGTH_SHORT
                ).show()
            }
        }

        try {
            if (sFrom == "edit") {
                mAccountName.setText(sAccountName)
                mAccountId.setText(sAccountId)
                // mAccountType.setSelection(sAccountType.toInt())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setAccount() {

        val url = context.getString(R.string.base_url) + "/manageAccount.php"
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, url,
            { response: String ->
                Log.d("set account", response)
                try {
                    val jsonObject = JSONObject(response)
                    val status = jsonObject.getString("status")
                    val message = jsonObject.getString("message")

                    if (status == "success") {

                        if (sFrom == "edit") {
                            Toast.makeText(
                                context, "Account edited Successfully", Toast
                                    .LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context, "Account added Successfully", Toast
                                    .LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            context, message, Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

            }, { error: VolleyError ->
                error.printStackTrace()
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                val stringMap: MutableMap<String, String> = hashMapOf()
                if (sId != "") {
                    stringMap["id"] = sId
                }
                stringMap["account_name"] = mAccountName.text.toString()
                stringMap["account_id"] = mAccountId.text.toString()

                when (mSelectedAccount) {
                    "Income" -> stringMap["account_type"] = "0"
                    "Expenditure" -> stringMap["account_type"] = "1"
                    else -> Toast.makeText(
                        context, "Select account type", Toast.LENGTH_SHORT
                    ).show()
                }
                stringMap["_db"] = mDb!!
                return stringMap
            }
        }
        val requestQueue: RequestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }

    private fun validation(): Boolean {
        if (mAccountName.text.isNullOrBlank() || mAccountId.text.isNullOrBlank())
            return false
        return true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        try {
            sInputListener = context as OnInputListener
        } catch (e: ClassCastException) {
            println(e.printStackTrace())
        }
    }

}