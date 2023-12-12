package com.digitaldream.winskool.models

class FeeTypeModel {

    private var mFeeId: Int = 0
    private var mFeeName: String? = null
    private var mMandatory: String? = null

    fun setFeeId(sId: Int) {
        mFeeId = sId
    }

    fun getFeeId(): Int = mFeeId

    fun setFeeName(sFeeName: String) {
        mFeeName = sFeeName
    }

    fun getFeeName() = mFeeName

    fun setMandatory(sMandatory: String) {
        mMandatory = sMandatory
    }

    fun isMandatory() = mMandatory

}