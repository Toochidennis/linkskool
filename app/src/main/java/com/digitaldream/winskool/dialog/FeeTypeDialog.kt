package com.digitaldream.winskool.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.digitaldream.winskool.R
import com.digitaldream.winskool.activities.Login
import org.json.JSONObject

private const val TAG = "FeeTyeDialog"

class FeeTypeDialog(
    sContext: Context, private val sFrom: String, private val sFeeName: String,
    private val sMandatory: String, private val sFeeId: Int,
) : Dialog(sContext) {

    private var mInputName: EditText? = null
    private var mSwitchBtn: SwitchCompat? = null
    private var mMandatory: String = "0"
    private var db: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.attributes.windowAnimations = R.style.DialogAnimation
        window!!.setGravity(Gravity.BOTTOM)
        setContentView(R.layout.dialog_type_fee)

        mInputName = findViewById(R.id.fee_input)
        mSwitchBtn = findViewById(R.id.mandatory_btn)
        val mDoneBtn: CardView = findViewById(R.id.fee_btn)

        val sharedPreferences = context.getSharedPreferences("loginDetail",
            Context.MODE_PRIVATE)
        db = sharedPreferences.getString("db", "")


        mDoneBtn.setOnClickListener {
            inputValidation()
        }

        mSwitchBtn?.setOnClickListener {
            mMandatory = if (mSwitchBtn?.isChecked == true)
                "1" else "0"
        }


        if (sFrom == "edit") {
            mInputName?.setText(sFeeName)
            mSwitchBtn?.isChecked = sMandatory == "1"
            mMandatory = sMandatory

        }
    }

    private fun inputValidation() {
        if (mInputName!!.text.isEmpty()) {
            Toast.makeText(context, "Fee name is required", Toast.LENGTH_SHORT).show()
        } else {
            setFeeName()
            dismiss()
        }

    }

    private fun setFeeName() {

        val url = Login.urlBase + "/manageFees.php"
        val stringRequest: StringRequest = object : StringRequest(Method.POST, url,
            { response: String ->
                Log.d(TAG, response)
                val jsonObject = JSONObject(response)
                val status = jsonObject.getString("status")

                if (status == "success") {

                    if (sFrom == "edit")
                        Toast.makeText(context, "Fee name edited successfully", Toast.LENGTH_SHORT)
                            .show()
                    else
                        Toast.makeText(context, "Fee name added successfully", Toast.LENGTH_SHORT)
                            .show()
                }

            }, { error: VolleyError ->
                error.printStackTrace()
                Toast.makeText(context, error.message,
                    Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                val stringMap: MutableMap<String, String> = HashMap()
                if (sFeeId != 0) {
                    stringMap["id"] = sFeeId.toString()
                }
                stringMap["fee_name"] = mInputName!!.text.toString()
                stringMap["mandatory"] = mMandatory
                stringMap["_db"] = db!!
                return stringMap
            }
        }
        val requestQueue: RequestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }
}