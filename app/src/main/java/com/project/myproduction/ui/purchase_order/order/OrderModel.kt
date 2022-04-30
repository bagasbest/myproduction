package com.project.myproduction.ui.purchase_order.order

import android.os.Parcelable
import com.project.myproduction.ui.purchase_order.POModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderModel(
    var uid: String? = null,
    var product: ArrayList<POModel>? = null,
    var totalPrice: Long? = 0L,
    var salesName: String? = null,
    var customerName: String? = null,
    var customerPhone: String? = null,
    var customerAddress: String? = null,
    var customer2ndName: String? = null,
    var customer2ndPhone: String? = null,
    var customer2ndAddress: String? = null,
    var status: String? = null,
) : Parcelable