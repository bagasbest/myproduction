package com.project.myproduction.ui.obat_umum

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HerbsModel(
    var uid: String? = null,
    var name: String? = null,
    var code: String? = null,
    var type: String? = null,
    var price: Long? = 0L,
    var stock: Long? = 0L,
) : Parcelable