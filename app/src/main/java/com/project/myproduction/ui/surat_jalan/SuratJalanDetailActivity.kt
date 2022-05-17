package com.project.myproduction.ui.surat_jalan

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.project.myproduction.databinding.ActivitySuratJalanDetailBinding
import com.project.myproduction.ui.invoice.InvoiceDetailActivity
import com.project.myproduction.ui.invoice.InvoiceModel
import com.project.myproduction.ui.purchase_order.order.OrderPOAdapter
import com.project.myproduction.ui.purchase_order.order.OrderPOAdapter2
import com.project.myproduction.utils.Common
import com.project.myproduction.utils.PdfDocumentAdapter
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.NumberFormat

class SuratJalanDetailActivity : AppCompatActivity() {

    private var binding: ActivitySuratJalanDetailBinding? = null
    private var adapter: OrderPOAdapter? = null
    private var adapter2: OrderPOAdapter2? = null
    private var model: InvoiceModel? = null
    private var totalPrice = 0L
    private val fileName: String = "surat_jalan.pdf"

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuratJalanDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        model = intent.getParcelableExtra(InvoiceDetailActivity.EXTRA_DATA)
        initRecyclerView()
        binding?.date?.text = "Tanggal: ${model?.date}"
        binding?.customerName?.text = "Kepada Yth: ${model?.customerName}"
        binding?.customerPhone?.text = "No.Handphone: ${model?.customerPhone}"
        binding?.customerAddress?.text = "Alamat: ${model?.customerAddress}"

        if(model?.category == "common") {
            binding?.rvCommon?.visibility = View.VISIBLE
            binding?.common1?.visibility = View.VISIBLE
        } else {
            binding?.rvFormulated?.visibility = View.VISIBLE
            binding?.komposisi?.visibility = View.VISIBLE
            binding?.productName?.visibility = View.VISIBLE
            binding?.qtyFormulated?.visibility = View.VISIBLE
            binding?.productName?.text = "Nama Produk Racikan: ${model?.product!![0].name}"
            binding?.qtyFormulated?.text = "Kuantitas Pemesanan: ${model?.product!![0].qty}"
        }

        if(model?.customer2ndName != "") {
            binding?.otherCustomer?.visibility = View.VISIBLE
            binding?.customerName2nd?.text = "Nama: ${model?.customer2ndName}"
            binding?.phone2nd?.text = "No.Handphone: ${model?.customer2ndPhone}"
            binding?.address2nd?.text = "Alamat: ${model?.customer2ndAddress}"
        }

        for(i in model?.product!!.indices) {
            totalPrice += model?.product!![i].price!!
        }
        val format: NumberFormat = DecimalFormat("#,###")
        binding?.totalPrice?.text = "Total Biaya: Rp.${format.format(totalPrice)}"


        Dexter.withContext(this)
            .withPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    binding?.imageButton?.setOnClickListener {
                        createPDFFile(Common.getAppPath(this@SuratJalanDetailActivity) + fileName)
                    }

                    binding?.printAddress?.setOnClickListener{
                        createPDFFileToPrintAddress(Common.getAppPath(this@SuratJalanDetailActivity) + fileName)
                    }
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

    private fun createPDFFileToPrintAddress(path: String) {
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
            val headingFontSize = 14.0f
            val valueFontSize = 18.0f

            /// custom font
            val fontName =
                BaseFont.createFont("assets/fonts/regular.ttf", "UTF-8", BaseFont.EMBEDDED)
            val headingStyle = Font(fontName, headingFontSize, Font.NORMAL, colorAccent)
            val valueStyle = Font(fontName, valueFontSize, Font.NORMAL, BaseColor.BLACK)

            /// add file to document
            val titleStyle = Font(fontName, 28.0f, Font.NORMAL, BaseColor.BLACK)
            addNewItem(document, "ALAMAT TUJUAN", Element.ALIGN_CENTER, titleStyle)

            addLineSpace(document)
            addLineSpace(document)

            addNewItem(document, "Keterangan:", Element.ALIGN_LEFT, headingStyle)
            addNewItem(document, "Tanggal: ${model?.date}", Element.ALIGN_LEFT, valueStyle)
            addNewItem(document, "Kepada Yth: ${model?.customerName}", Element.ALIGN_LEFT, valueStyle)
            addNewItem(document, "No.Handphone: ${model?.customerPhone}", Element.ALIGN_LEFT, valueStyle)
            addNewItem(document, "Alamat: ${model?.customerAddress}", Element.ALIGN_LEFT, valueStyle)

            addLineSpace(document)
            addLineSpace(document)

            if(model?.customer2ndName != "") {
                addNewItem(document, "Dikirim ke alamat lain", Element.ALIGN_LEFT, headingStyle)
                addNewItem(document, "Kepada Yth: ${model?.customer2ndName}", Element.ALIGN_LEFT, valueStyle)
                addNewItem(document, "No.Handphone: ${model?.customer2ndPhone}", Element.ALIGN_LEFT, valueStyle)
                addNewItem(document, "Alamat: ${model?.customer2ndAddress}", Element.ALIGN_LEFT, valueStyle)
            }

            addLineSeparator(document)
            addLineSpace(document)
            addLineSpace(document)

            addNewItem(document, "ALAMAT TUJUAN", Element.ALIGN_CENTER, titleStyle)

            addLineSpace(document)
            addLineSpace(document)

            addNewItem(document, "Keterangan:", Element.ALIGN_LEFT, headingStyle)
            addNewItem(document, "Tanggal: ${model?.date}", Element.ALIGN_LEFT, valueStyle)
            addNewItem(document, "Kepada Yth: ${model?.customerName}", Element.ALIGN_LEFT, valueStyle)
            addNewItem(document, "No.Handphone: ${model?.customerPhone}", Element.ALIGN_LEFT, valueStyle)
            addNewItem(document, "Alamat: ${model?.customerAddress}", Element.ALIGN_LEFT, valueStyle)

            addLineSpace(document)
            addLineSpace(document)

            if(model?.customer2ndName != "") {
                addNewItem(document, "Dikirim ke alamat lain", Element.ALIGN_LEFT, headingStyle)
                addNewItem(document, "Kepada Yth: ${model?.customer2ndName}", Element.ALIGN_LEFT, valueStyle)
                addNewItem(document, "No.Handphone: ${model?.customer2ndPhone}", Element.ALIGN_LEFT, valueStyle)
                addNewItem(document, "Alamat: ${model?.customer2ndAddress}", Element.ALIGN_LEFT, valueStyle)
            }
            /// close
            document.close()

            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
            printPDF()

        } catch (e: Exception) {
            Log.e("PO Error", "" + e.message)
        }
    }

    private fun createPDFFile(path: String) {
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
            val headingFontSize = 14.0f
            val valueFontSize = 18.0f

            /// custom font
            val fontName =
                BaseFont.createFont("assets/fonts/regular.ttf", "UTF-8", BaseFont.EMBEDDED)
            val headingStyle = Font(fontName, headingFontSize, Font.NORMAL, colorAccent)
            val valueStyle = Font(fontName, valueFontSize, Font.NORMAL, BaseColor.BLACK)
            val format: NumberFormat = DecimalFormat("#,###")


            /// add file to document
            val titleStyle = Font(fontName, 28.0f, Font.NORMAL, BaseColor.BLACK)
            addNewItem(document, "SURAT JALAN", Element.ALIGN_CENTER, titleStyle)

            addLineSpace(document)
            addLineSpace(document)

            addNewItem(document, "Keterangan:", Element.ALIGN_LEFT, headingStyle)
            addNewItem(document, "Tanggal: ${model?.date}", Element.ALIGN_LEFT, valueStyle)
            addNewItem(document, "Kepada Yth: ${model?.customerName}", Element.ALIGN_LEFT, valueStyle)
            addNewItem(document, "No.Handphone: ${model?.customerPhone}", Element.ALIGN_LEFT, valueStyle)
            addNewItem(document, "Alamat: ${model?.customerAddress}", Element.ALIGN_LEFT, valueStyle)

            addLineSpace(document)
            addLineSpace(document)

            if(model?.customer2ndName != "") {
                addNewItem(document, "Dikirim ke alamat lain", Element.ALIGN_LEFT, headingStyle)
                addNewItem(document, "Kepada Yth: ${model?.customer2ndName}", Element.ALIGN_LEFT, valueStyle)
                addNewItem(document, "No.Handphone: ${model?.customer2ndPhone}", Element.ALIGN_LEFT, valueStyle)
                addNewItem(document, "Alamat: ${model?.customer2ndAddress}", Element.ALIGN_LEFT, valueStyle)
            }

            addLineSeparator(document)
            if(model?.category == "formulated") {
                addNewItem(document, "Kategori: Obat Racikan", Element.ALIGN_LEFT, valueStyle)
                addNewItem(document, "Nama Obat Racikan: ${model?.product!![0].name}", Element.ALIGN_LEFT, valueStyle)
                addLineSpace(document)
                addLineSpace(document)
                addNewItem(document, "Nama Obat Racikan: ${model?.product!![0].name}", Element.ALIGN_LEFT, valueStyle)
                addNewItem(document, "Kuantitas Pemesanan: ${model?.product!![0].qty}", Element.ALIGN_LEFT, valueStyle)
                addLineSeparator(document)
                addNewItem(document, "Komposisi Racikan", Element.ALIGN_CENTER, titleStyle)
                addLineSpace(document)
                addLineSpace(document)

                for(i in model?.product!![0].material!!.indices) {
                    addNewItemWithLeftAndRight(
                        document,
                        model?.product!![0].material!![i].name!! + " (kode:" + model?.product!![0].material!![i].code + ")",
                        "${model?.product!![0].material!![i].qty} x ${model?.product!![0].material!![i].type}",
                        valueStyle,
                        valueStyle
                    )
                }

            } else {
                addNewItem(document, "Kategori: Obat Umum", Element.ALIGN_LEFT, valueStyle)
                val format: NumberFormat = DecimalFormat("#,###")
                addNewItem(document, "Detail Order", Element.ALIGN_CENTER, titleStyle)
                addLineSpace(document)
                addLineSpace(document)

                for(i in model?.product!!.indices) {
                    addNewItemWithLeftAndRight(
                        document,
                        model?.product!![i].name!! + " (kode:" + model?.product!![i].code + ")",
                        "${model?.product!![i].qty} pcs, Rp.${format.format(model?.product!![i].price)}",
                        valueStyle,
                        valueStyle
                    )
                }
            }


            addLineSeparator(document)
            addNewItem(document, "Total Biaya: Rp.${format.format(model?.totalPrice)}", Element.ALIGN_CENTER, titleStyle)
            addLineSeparator(document)

            addLineSpace(document)
            addLineSpace(document)

            addNewItemWithLeftAndRight(
                document,
                "Approve By",
                "Receive By",
                valueStyle,
                valueStyle
            )

            addLineSpace(document)
            addNewItem(document, "", Element.ALIGN_CENTER, valueStyle)
            addNewItem(document, "", Element.ALIGN_CENTER, valueStyle)


            addNewItemWithLeftAndRight(
                document,
                "Admin Sales",
                model?.customerName!!,
                valueStyle,
                valueStyle
            )

            /// close
            document.close()

            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
            printPDF()

        } catch (e: Exception) {
            Log.e("PO Error", "" + e.message)
        }
    }

    private fun printPDF() {
        val printerManager = getSystemService(Context.PRINT_SERVICE) as PrintManager
        try {
            val printAdapter = PdfDocumentAdapter(this, Common.getAppPath(this)+fileName)
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

    private fun initRecyclerView() {
        if(model?.category == "common") {
            binding?.rvCommon?.layoutManager = LinearLayoutManager(this)
            adapter = OrderPOAdapter()
            binding?.rvCommon?.adapter = adapter
            adapter!!.setData(model?.product!!)
        } else {
            binding?.rvFormulated?.layoutManager = LinearLayoutManager(this)
            adapter2 = OrderPOAdapter2()
            binding?.rvFormulated?.adapter = adapter2
            adapter2!!.setData(model?.product!![0].material!!)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_DATA = "data"
    }
}