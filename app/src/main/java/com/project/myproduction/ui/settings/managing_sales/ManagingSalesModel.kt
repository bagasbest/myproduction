package com.project.myproduction.ui.settings.managing_sales

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ManagingSalesModel(
    var uid: String? = null,
    var name: String? = null,
    var username: String? = null,
    var email: String? = null,
    var phone: String? = null,
    var work: String? = null,
    var status: String? = null
) : Parcelable
