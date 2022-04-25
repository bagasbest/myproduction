package com.project.myproduction.ui.obat_racikan

import com.project.myproduction.ui.obat_racikan.material.MaterialModel

interface IFirebaseLoadDone {
    fun onFirebaseLoadSuccess(property: List<MaterialModel>)
    fun onFirebaseLoadFailed(message: String)
}