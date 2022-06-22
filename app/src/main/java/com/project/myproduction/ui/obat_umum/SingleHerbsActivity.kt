package com.project.myproduction.ui.obat_umum

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.LineSeparator
import com.itextpdf.text.pdf.draw.VerticalPositionMark
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.project.myproduction.R
import com.project.myproduction.databinding.ActivitySingleHerbsBinding
import com.project.myproduction.utils.Common
import com.project.myproduction.utils.PdfDocumentAdapter
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SingleHerbsActivity : AppCompatActivity() {

    private var binding: ActivitySingleHerbsBinding? = null
    private var adapter: HerbsAdapter? = null
    private var from: String? = null
    private var to: String? = null
    private var realFileName = ""
    private val fileName: String = "stock_single_herb.pdf"
    private var date = ""
    private var dateFirst = ""
    private var dateSecond = ""
    private var singleHerbList = ArrayList<HerbsModel>()

    override fun onResume() {
        super.onResume()
        initRecyclerView()
        initViewModel("")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingleHerbsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val role = intent.getStringExtra(ROLE)

        if (role == "admin" || role == "sales") {
            binding?.add?.visibility = View.VISIBLE
        }


        binding?.add?.setOnClickListener {
            val intent = Intent(this, HerbsAddEditActivity::class.java)
            intent.putExtra(HerbsAddEditActivity.OPTION, "add")
            startActivity(intent)
        }

        binding?.settingBtn?.setOnClickListener {
            onBackPressed()
        }

        binding?.printStock?.setOnClickListener {
            val c = Calendar.getInstance()
            val df = SimpleDateFormat("dd-MMM-yyyy, HH:mm")
            date = df.format(c.time)
            realFileName = "stock_$date"
            printStock()
        }

        binding?.search?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(query: Editable?) {
                if (query.toString().isEmpty()) {
                    initRecyclerView()
                    initViewModel("")
                } else {
                    initRecyclerView()
                    initViewModel(query.toString())
                }
            }

        })

        binding?.sort?.setOnClickListener {
            sortProduct()
        }

        binding?.update?.setOnClickListener {
            updateProduct()
        }
    }

    private fun updateProduct() {
        for(i in singleHerbList.indices) {

            val data = mapOf(
                "size" to "gram",
                "type" to "100",
                "pricePerSize" to 0L,
                "stockPerSize" to 0L,
            )

            FirebaseFirestore
                .getInstance()
                .collection("common_herbs")
                .document(singleHerbList[i].uid!!)
                .update(data)
        }
    }

    private fun printStock() {
        /// show date range picker
        val now = Calendar.getInstance()
        val datePicker: MaterialDatePicker<*> =
            MaterialDatePicker.Builder.dateRangePicker().setCalendarConstraints(CalendarConstraints.Builder().setValidator(
                DateValidatorPointBackward.now()).build())
                .setSelection(androidx.core.util.Pair.create(now.timeInMillis, now.timeInMillis)).setTitleText("Filter (dari-ke) Tanggal Cetak Stok").build()

        datePicker.show(supportFragmentManager, datePicker.toString())
        datePicker.addOnPositiveButtonClickListener {

            val prendiRange = datePicker.selection as androidx.core.util.Pair<*, *>?
            val formatFirst = prendiRange?.first.toString().toLong() - 25200000 /// decrease 7 hour
            val formatSecond = now.timeInMillis

            val sdf = SimpleDateFormat("dd")
            dateFirst = sdf.format(Date(formatFirst))
            dateSecond = sdf.format(Date(formatSecond))

            initViewModelStockByDates(formatFirst, formatSecond)


        }
    }

    private fun initViewModelStockByDates(formatFirst: Long, formatSecond: Long) {
        val viewModel = ViewModelProvider(this)[StockViewModel::class.java]

        viewModel.setListStock(formatFirst, formatSecond)
        viewModel.getStock().observe(this) { stockList ->
            Dexter.withContext(this)
                .withPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        createPDFFile(Common.getAppPath(this@SingleHerbsActivity) + fileName, stockList)
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {

                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest?,
                        token: PermissionToken?
                    ) {

                    }

                })
                .withErrorListener {
                    Log.e("Dexter", "There was an error: $it")
                }.check()
        }
    }

    private fun sortProduct() {
        val view: View = layoutInflater.inflate(R.layout.item_filter, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(view)

        val upperPriceBtn = dialog.findViewById<Button>(R.id.upperPrice)
        val lowerPriceBtn = dialog.findViewById<Button>(R.id.lowerPrice)
        val upperStockBtn = dialog.findViewById<Button>(R.id.upperStock)
        val lowerStockBtn = dialog.findViewById<Button>(R.id.lowerStock)
        val priceRangeBtn = dialog.findViewById<Button>(R.id.rangePrice)
        val fromEt = dialog.findViewById<TextInputEditText>(R.id.from)
        val toEt = dialog.findViewById<TextInputEditText>(R.id.to)

        upperPriceBtn?.setOnClickListener {
            initRecyclerView()
            initViewModel("upperPrice")
            dialog.dismiss()
        }

        lowerPriceBtn?.setOnClickListener {
            initRecyclerView()
            initViewModel("lowerPrice")
            dialog.dismiss()
        }

        upperStockBtn?.setOnClickListener {
            initRecyclerView()
            initViewModel("upperStock")
            dialog.dismiss()
        }

        lowerStockBtn?.setOnClickListener {
            initRecyclerView()
            initViewModel("lowerStock")
            dialog.dismiss()
        }

        priceRangeBtn?.setOnClickListener {
            from = fromEt?.text.toString().trim()
            to = toEt?.text.toString().trim()

            if (from == null || from!!.toLong() < 0) {
                Toast.makeText(
                    this,
                    "Maaf, untuk filter harga tidak boleh kosong, dan lebih dari 0",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (to == null || to?.toLong()!! < 0) {
                Toast.makeText(
                    this,
                    "Maaf, untuk filter harga tidak boleh kosong, dan lebih dari 0",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (from?.toLong()!! > to?.toLong()!!) {
                Toast.makeText(
                    this,
                    "Maaf, untuk filter harga rentang adalah dari bilangan ter rendah hingga bilangan tertinggi, tidak boleh terbalik",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                initRecyclerView()
                initViewModel("priceRange")
                dialog.dismiss()
            }
        }


        dialog.show()
    }

    private fun initRecyclerView() {
        binding?.rvHerbs?.layoutManager = LinearLayoutManager(this)
        adapter = HerbsAdapter()
        binding?.rvHerbs?.adapter = adapter
    }

    private fun initViewModel(query: String) {
        val viewModel = ViewModelProvider(this)[HerbsViewModel::class.java]

        binding?.progressBar?.visibility = View.VISIBLE
        when (query) {
            "" -> {
                viewModel.setListCommonHerbs()
            }
            "upperPrice" -> {
                viewModel.setListCommonHerbsOrderByPriceAscending()
            }
            "lowerPrice" -> {
                viewModel.setListCommonHerbsOrderByPriceDescending()
            }
            "upperStock" -> {
                viewModel.setListCommonHerbsOrderByStockAscending()
            }
            "lowerStock" -> {
                viewModel.setListCommonHerbsOrderByStockDescending()
            }
            "priceRange" -> {
                viewModel.setListCommonHerbsOrderByPriceRange(from?.toLong(), to?.toLong())
            }
            else -> {
                viewModel.setListCommonHerbsByQuery(query)
            }
        }
        viewModel.getHerbs().observe(this) { herbList ->
            if (herbList.size > 0) {
                adapter?.setData(herbList)
                binding?.noData?.visibility = View.GONE
                if (query == "") {
                    singleHerbList.clear()
                    singleHerbList.addAll(herbList)
                    saveStock()
                }
            } else {
                binding?.noData?.visibility = View.VISIBLE
            }
            binding!!.progressBar.visibility = View.GONE
        }
    }

    private fun saveStock() {
        val c = Calendar.getInstance()
        val df = SimpleDateFormat("dd-MM-yyyy")
        val dates = df.format(c.time)

        val listOfStock = ArrayList<HerbsStockModel>()
        listOfStock.clear()
        for (i in singleHerbList.indices) {
            val model = HerbsStockModel()
            model.name = singleHerbList[i].name
            model.stock = singleHerbList[i].stock

            listOfStock.add(model)
        }

        val data = mapOf(
            "uid" to dates,
            "dateInMillis" to c.timeInMillis,
            "date" to dates.substring(0, 2),
            "data" to listOfStock,
        )

        FirebaseFirestore
            .getInstance()
            .collection("single_herb_stocks")
            .document(dates)
            .set(data)

    }

    private fun createPDFFile(path: String, stockList: ArrayList<StockModel>) {
        if (File(path).exists()) {
            File(path).delete()
        }
        try {
            val document = Document()
            /// save
            PdfWriter.getInstance(document, FileOutputStream(path))
            /// open to write
            document.open()

            /// setting
            document.pageSize = PageSize.A5
            document.addCreationDate()
            document.addAuthor("Sales")
            document.addCreator("Sales App")

            /// font setting
            val colorAccent = BaseColor(0, 153, 204, 255)
            val headingFontSize = 12.0f
            val valueFontSize = 14.0f

            /// custom font
            val fontName =
                BaseFont.createFont("assets/fonts/regular.ttf", "UTF-8", BaseFont.EMBEDDED)
            val headingStyle = Font(fontName, headingFontSize, Font.NORMAL, colorAccent)
            val valueStyle = Font(fontName, valueFontSize, Font.NORMAL, BaseColor.BLACK)


            /// add file to document
            val titleStyle = Font(fontName, 20.0f, Font.NORMAL, BaseColor.BLACK)
            addNewItem(document, "CETAK STOK SINGLE HERB", Element.ALIGN_CENTER, titleStyle)
            addLineSpace(document)
            addLineSpace(document)
            addNewItemWithLeftAndRight(
                document,
                "PT.Primax Asia Link",
                "Tanggal: $dateFirst - $dateSecond",
                valueStyle,
                valueStyle
            )
            addNewItemWithLeftAndRight(
                document,
                "Jl.Boulevard Artha Gading\nBlok A7A No.1 Kelapa Gading,\nJakarta Utara,14240",
                "",
                valueStyle,
                valueStyle
            )

            ///loop date first into date last
            var date = ""
            for(i in stockList.indices) {
                date += "${stockList[i].date}, "
            }

            addNewItemWithLeftAndRight(
                document,
                "Tel: 021-4587-4199",
                "Tanggal: ${date.substring(0, date.length-1)}",

                valueStyle,
                valueStyle
            )


            addLineSeparator(document)


            for (i in singleHerbList.indices) {
                var stock = ""
                for(j in stockList.indices) {
                    if(j>0) {
                        stock += "   |   " + stockList[j].data!![i].stock
                    } else {
                        stock += stockList[j].data!![i].stock
                    }
                }
                addNewItemWithLeftAndRight(
                    document,
                    singleHerbList[i].name!!,
                    stock,
                    valueStyle,
                    valueStyle
                )
            }

            addLineSpace(document)
            addNewItem(document, "", Element.ALIGN_CENTER, valueStyle)
            addNewItem(document, "", Element.ALIGN_CENTER, valueStyle)

            /// close
            document.close()

            printPDF()

        } catch (e: Exception) {
            Log.e("PO Error", "" + e.message)
        }
    }

    private fun printPDF() {
        val printerManager = getSystemService(Context.PRINT_SERVICE) as PrintManager
        try {
            val printAdapter = PdfDocumentAdapter(
                this,
                Common.getAppPath(this) + fileName,
                realFileName
            )
            printerManager.print("Document", printAdapter, PrintAttributes.Builder().build())
        } catch (e: Exception) {
            Log.e("PO Print Error", "" + e.message)
        }
    }

    @Throws(DocumentException::class)
    private fun addNewItemWithLeftAndRight(
        document: Document,
        textLeft: String,
        textRight: String,
        leftStyle: Font,
        rightStyle: Font,
    ) {
        val chunkTextLeft = Chunk(textLeft, leftStyle)
        val chunkTextRight = Chunk(textRight, rightStyle)
        val p = Paragraph(chunkTextLeft)
        p.add(Chunk(VerticalPositionMark()))
        p.add(chunkTextRight)
        document.add(p)
    }

    @Throws(DocumentException::class)
    private fun addLineSeparator(document: Document) {
        val lineSeparator = LineSeparator()
        lineSeparator.lineColor = BaseColor(0, 0, 0, 68)
        addLineSpace(document)
        document.add(Chunk(lineSeparator))
        addLineSpace(document)
    }

    @Throws(DocumentException::class)
    private fun addLineSpace(document: Document) {
        document.add(Paragraph(""))
    }

    @Throws(DocumentException::class)
    private fun addNewItem(document: Document, text: String, alignCenter: Int, style: Font) {
        val chunk = Chunk(text, style)
        val p = Paragraph(chunk)
        p.alignment = alignCenter
        document.add(p)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val ROLE = "role"
    }
}