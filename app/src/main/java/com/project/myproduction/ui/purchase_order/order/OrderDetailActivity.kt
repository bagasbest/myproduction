package com.project.myproduction.ui.purchase_order.order

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.project.myproduction.databinding.ActivityOrderDetailBinding
import com.project.myproduction.utils.Common
import com.project.myproduction.utils.PdfDocumentAdapter
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.NumberFormat

class OrderDetailActivity : AppCompatActivity() {

    private var binding: ActivityOrderDetailBinding? = null
    private var model: OrderModel? = null
    private var adapter: OrderPOAdapter? = null
    private var adapter2: OrderPOAdapter2? = null

    private val fileName: String = "purchase_order.pdf"

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        model = intent.getParcelableExtra(EXTRA_DATA)
        initRecyclerView()
        binding?.poId?.text = "PO ID: ${model?.uid}"
        if (model?.category == "common") {
            binding?.category?.text = "Kategori: Obat Umum"
            binding?.common1?.visibility = View.VISIBLE
            binding?.rvPoCommon?.visibility = View.VISIBLE
        } else {
            binding?.category?.text = "Kategori: Obat Racikan"
            val format: NumberFormat = DecimalFormat("#,###")
            binding?.formulated1?.visibility = View.VISIBLE
            binding?.rvPoFormulated?.visibility = View.VISIBLE
            binding?.priceFormulated?.visibility = View.VISIBLE
            binding?.qtyFormulated?.visibility = View.VISIBLE
            binding?.productName?.visibility = View.VISIBLE
            binding?.productName?.text = "Nama Produk Racikan: ${model?.product!![0].name}"
            binding?.priceFormulated?.text = "Harga: Rp.${format.format(model?.product!![0].price)}"
            binding?.qtyFormulated?.text = "Kuantitas Pemesanan: ${model?.product!![0].qty}"
        }
        binding?.customerName?.text = "Kepada Yth: ${model?.customerName}"
        binding?.customerPhone?.text = "No.Handphone: ${model?.customerPhone}"
        binding?.customerAddress?.text = "Alamat: ${model?.customerAddress}"
        binding?.salesName?.text = "Diajukan Oleh\n\n${model?.salesName}"
        binding?.tanggal?.text = "Tanggal: ${model?.date}"

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        binding?.delete?.setOnClickListener {
            showAlertDeclineDialog()
        }

        Dexter.withContext(this)
            .withPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    binding?.print?.setOnClickListener {
                        createPDFFile(Common.getAppPath(this@OrderDetailActivity) + fileName)
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

            /// add file to document
            val titleStyle = Font(fontName, 28.0f, Font.NORMAL, BaseColor.BLACK)
            addNewItem(document, "Purchase Order", Element.ALIGN_CENTER, titleStyle)


            val headingStyle = Font(fontName, headingFontSize, Font.NORMAL, colorAccent)
            addNewItem(document, "PO ID:", Element.ALIGN_LEFT, headingStyle)
            val valueStyle = Font(fontName, valueFontSize, Font.NORMAL, BaseColor.BLACK)
            addNewItem(document, model?.uid!!, Element.ALIGN_LEFT, valueStyle)

            addLineSeparator(document)

            addNewItem(document, "Tanggal:", Element.ALIGN_LEFT, headingStyle)
            addNewItem(document, model?.date!!, Element.ALIGN_LEFT, valueStyle)
            if(model?.category == "formulated") {
                addNewItem(document, "Kategori: Obat Racikan", Element.ALIGN_LEFT, valueStyle)
                addNewItem(document, "Nama Obat Racikan: ${model?.product!![0].name}", Element.ALIGN_LEFT, valueStyle)
                val format: NumberFormat = DecimalFormat("#,###")
                addNewItem(document, "Harga: Rp.${format.format(model?.product!![0].price)}", Element.ALIGN_LEFT, valueStyle)
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

            /// total
            addLineSpace(document)
            addLineSpace(document)

            addNewItemWithLeftAndRight(
                document,
                "Mengetahui",
                "Mengajukan",
                titleStyle,
                titleStyle
            )

            addLineSpace(document)

            addNewItemWithLeftAndRight(
                document,
                "Admin Sales",
                model?.salesName!!,
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

    private fun showAlertDeclineDialog() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Menolak PO")
            .setMessage("Apakah anda yakin ingin menghapus PO ini ?")
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setPositiveButton("YA") { dialogInterface, _ ->
                dialogInterface.dismiss()
                deletePO()
            }
            .setNegativeButton("TIDAK", null)
            .show()
    }

    private fun deletePO() {
        FirebaseFirestore
            .getInstance()
            .collection("order")
            .document(model?.uid!!)
            .delete()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Berhasil menghapus PO", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        this,
                        "Gagal menghapus PO, koneksi internet anda bermasalah, coba lagi nanti",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun initRecyclerView() {
        if (model?.category == "common") {
            binding?.rvPoCommon?.layoutManager = LinearLayoutManager(this)
            adapter = OrderPOAdapter()
            binding?.rvPoCommon?.adapter = adapter
            adapter!!.setData(model?.product!!)
        } else {
            binding?.rvPoFormulated?.layoutManager = LinearLayoutManager(this)
            adapter2 = OrderPOAdapter2()
            binding?.rvPoFormulated?.adapter = adapter2
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