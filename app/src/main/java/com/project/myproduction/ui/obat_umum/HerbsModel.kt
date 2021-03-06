package com.project.myproduction.ui.obat_umum

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HerbsModel(
    var uid: String? = null,
    var name: String? = null,
    var code: String? = null,
    var type: String? = null,
    var size: String? = null,
    var price: Long? = 0L,
    var pricePerSize: Long? = 0L,
    var stock: Long? = 0L,
    var stockPerSize: Long? = 0L,
) : Parcelable

data class HerbsStockModel(
    var name: String? = null,
    var stock: Long? = 0L,
)