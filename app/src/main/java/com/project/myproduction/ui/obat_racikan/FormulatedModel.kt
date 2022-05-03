package com.project.myproduction.ui.obat_racikan

import android.os.Parcelable
import com.project.myproduction.ui.obat_racikan.material.MaterialModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class FormulatedModel(

    var name: String? = null,
    var code: String? = null,
    var type: String? = null,
    var price: Long? = 0L,
    var uid: String? = null,
    var material: ArrayList<MaterialModel>? = null,

) : Parcelable