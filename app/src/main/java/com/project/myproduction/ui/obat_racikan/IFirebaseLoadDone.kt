package com.project.myproduction.ui.obat_racikan

import com.project.myproduction.ui.obat_racikan.material.MaterialModel
import com.project.myproduction.ui.obat_umum.HerbsModel

interface IFirebaseLoadDone {
    fun onFirebaseLoadSuccess(property: List<MaterialModel>)
    fun onFirebaseLoadSuccessCommon(property: List<HerbsModel>)
    fun onFirebaseLoadFailed(message: String)
}