package com.project.myproduction.ui.purchase_order

import android.os.Parcelable
import com.project.myproduction.ui.obat_racikan.material.MaterialModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class POModel(
    var uid: String? = null,
    var name: String? = null,
    var code: String? = null,
    var type: String? = null,
    var size: String? = null,
    var price: Long? = 0L,
    var qty: Long? = 0L,
    var salesName: String? = null,
    var salesId: String? = null,
    var productId: String? = null,
    var materialId: ArrayList<String>? = null,
    var material: ArrayList<MaterialModel>? = null,
    var category: String? = null,
    var formulatedQty: ArrayList<Long>? = null,
) : Parcelable
