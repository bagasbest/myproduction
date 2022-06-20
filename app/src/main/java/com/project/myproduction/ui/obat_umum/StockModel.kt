package com.project.myproduction.ui.obat_umum

data class StockModel(
    var uid : String? = null,
    var dateInMillis : Long? = 0L,
    var date : String? = null,
    var data : ArrayList<HerbsStockModel>? = null,
)