package com.digitaldream.linkskool.models

class StudentPaymentModel {
    private var mId: Int = 0
    private var mStatus: String? = null
    private var mInvoiceId: String? = null
    private var mName: String? = null
    private var mLevelName: String? = null
    private var mClassName: String? = null
    private var mTerm: String? = null
    private var mYear: String? = null
    private var mDate: String? = null
    private var mSession: String? = null
    private var mAmount: String? = null
    private var mAmountT: String? = null
    private var mReferenceNumber: String? = null
    private var mJson: String? = null

    fun getId() = mId

    fun getStatus() = mStatus

    fun getInvoiceId() = mInvoiceId

    fun getName() = mName

    fun getLevelName() = mLevelName

    fun getClassName() = mClassName

    fun getTerm() = mTerm
    fun getYear() = mYear

    fun getDate() = mDate

    fun getSession() = mSession

    fun getAmount() = mAmount

    fun getAmountT() = mAmountT

    fun getReferenceNumber() = mReferenceNumber

    fun getJson() = mJson

    fun setId(sId: Int) {
        mId = sId
    }

    fun setStatus(sStatus: String) {
        mStatus = sStatus
    }

    fun setInvoiceId(sInvoiceId: String) {
        mInvoiceId = sInvoiceId
    }

    fun setName(sName: String) {
        mName = sName
    }

    fun setLevelName(sLevelName: String) {
        mLevelName = sLevelName
    }

    fun setClassName(sClassName: String) {
        mClassName = sClassName
    }

    fun setTerm(sTerm: String) {
        mTerm = sTerm
    }

    fun setYear(sYear: String) {
        mYear = sYear
    }

    fun setDate(sDate: String) {
        mDate = sDate
    }

    fun setSession(sSession: String) {
        mSession = sSession
    }

    fun setAmount(sAmount: String) {
        mAmount = sAmount
    }

    fun setAmountT(sAmountT: String) {
        mAmountT = sAmountT
    }

    fun setReferenceNumber(sReferenceNumber: String) {
        mReferenceNumber = sReferenceNumber
    }

    fun setJson(sJson: String) {
        mJson = sJson
    }

}