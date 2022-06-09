package com.project.myproduction.ui.item_history

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemHistoryModel(
    var uid: String? = null,
    var status: String? = null,
    var date: String? = null,
    var stock: Long? = 0L,
    var customerName: String? = null,
    var dateInMillis : Long? = 0L,
    var productName : String? = null,
    var productId : String? = null,
    var productCode : String? = null,
    var productType : String? = null,
) : Parcelable