package com.project.myproduction.ui.obat_racikan.material

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MaterialModel(
    var uid: String? = null,
    var name: String? = null,
    var code: String? = null,
    var type: String? = null,
    var price: Long? = 0L,
    var stock: Long? = 0L,
    var qty: Long? = 0L,
    var collection: String? = null
) : Parcelable