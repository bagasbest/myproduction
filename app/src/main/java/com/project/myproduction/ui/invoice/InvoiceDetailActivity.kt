package com.project.myproduction.ui.invoice

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.project.myproduction.databinding.ActivityInvoiceDetailBinding
import com.project.myproduction.ui.purchase_order.order.OrderPOAdapter
import com.project.myproduction.ui.purchase_order.order.OrderPOAdapter2
import com.project.myproduction.utils.Common
import com.project.myproduction.utils.PdfDocumentAdapter
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class InvoiceDetailActivity : AppCompatActivity() {

    private var binding: ActivityInvoiceDetailBinding? = null
    private var adapter: OrderPOAdapter? = null
    private var adapter2: OrderPOAdapter2? = null
    private var model: InvoiceModel? = null
    private var totalPrice = 0L
    private var finalPriceWithDiscountAndPpn = 0L
    private val fileName: String = "invoice.pdf"
    private var ppn: Double = 0.0
    private var discount: Double = 0.0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvoiceDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        model = intent.getParcelableExtra(EXTRA_DATA)
        initRecyclerView()
        binding?.date?.text = "Tanggal: ${model?.date}"
        binding?.customerName?.text = "Kepada Yth: ${model?.customerName}"
        binding?.customerPhone?.text = "No.Handphone: ${model?.customerPhone}"
        binding?.customerAddress?.text = "Alamat: ${model?.customerAddress}"
        val position = intent.getIntExtra(POSITION, 0)
        if(position+1 < 10) {
            binding?.invoiceId?.text = "INVOICE ID: ${model?.dateInvoiceId}${String.format("%03d", position+1)}"
        } else if (position+1 < 100) {
            binding?.invoiceId?.text = "INVOICE ID: ${model?.dateInvoiceId}${String.format("%02d", position+1)}"
        } else {
            binding?.invoiceId?.text = "INVOICE ID: ${model?.dateInvoiceId}${position+1}"
        }
        binding?.salesName?.text = "Nama Sales: ${model?.salesName}"
        binding?.pembayaranAtasNama?.text = "Pembayaran atas nama: ${model?.customerName}"
        discount = model?.discount?.toDouble()!!
        ppn = model?.ppn?.toDouble()!!
        binding?.discount?.setText(discount.toInt().toString())
        binding?.ppn?.setText(ppn.toInt().toString())

        if(model?.category == "common") {
            binding?.category?.text = "Kategori: Obat Umum"
            binding?.rvCommon?.visibility = View.VISIBLE
            binding?.common1?.visibility = View.VISIBLE
        } else {
            binding?.category?.text = "Kategori: Obat Racikan"
            binding?.rvFormulated?.visibility = View.VISIBLE
            binding?.komposisi?.visibility = View.VISIBLE
            binding?.qtyFormulated?.visibility = View.VISIBLE
            binding?.productName?.visibility = View.VISIBLE
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
        val discPrice = (discount / 100) * totalPrice
        val ppnPrice = (ppn / 100) * totalPrice
        val format: NumberFormat = DecimalFormat("#,###")

        binding?.discPrice?.text = "Diskon: - Rp.${format.format(discPrice)}"
        binding?.ppnPrice?.text ="PPN: + Rp.${format.format(ppnPrice)}"
        finalPriceWithDiscountAndPpn = totalPrice - discPrice.toLong() + ppnPrice.toLong()
        binding?.totalPrice?.text = "Total Biaya: Rp.${format.format(finalPriceWithDiscountAndPpn)}"


        Dexter.withContext(this)
            .withPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    binding?.imageButton?.setOnClickListener {
                        createPDFFile(Common.getAppPath(this@InvoiceDetailActivity) + fileName)
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


        binding?.paymentBtn?.setOnClickListener {
            paymentPopup()
        }

        binding?.saveBtn?.setOnClickListener {
            changePrice()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun changePrice() {
        val dsc = binding?.discount?.text.toString()
        val pp = binding?.ppn?.text.toString()

        if(dsc.isEmpty()) {
            Toast.makeText(this, "Diskon tidak boleh kosong", Toast.LENGTH_SHORT).show()
        } else if (pp.isEmpty()) {
            Toast.makeText(this, "PPN tidak boleh kosong", Toast.LENGTH_SHORT).show()
        } else {
            discount = dsc.toDouble()
            ppn = pp.toDouble()
            val discPrice = (discount / 100) * totalPrice
            val ppnPrice = (ppn / 100) * totalPrice
            val format: NumberFormat = DecimalFormat("#,###")

            val data = mapOf(
                "discount" to discount.toInt(),
                "ppn" to ppn.toInt()
            )

            FirebaseFirestore
                .getInstance()
                .collection("invoice")
                .document(model?.uid!!)
                .update(data)
                .addOnCompleteListener {
                    if(it.isSuccessful) {
                        binding?.discPrice?.text = "Diskon: - Rp.${format.format(discPrice)}"
                        binding?.ppnPrice?.text ="PPN: + Rp.${format.format(ppnPrice)}"
                        finalPriceWithDiscountAndPpn = totalPrice - discPrice.toLong() + ppnPrice.toLong()
                        binding?.totalPrice?.text = "Total Biaya: Rp.${format.format(finalPriceWithDiscountAndPpn)}"
                    }
                }
        }
    }

    private fun paymentPopup() {
        val confirmBtn: Button
        val cashBtn: Button
        val tfBtn: Button
        val dateBtn: Button
        val yesBtn: Button
        val noBtn: Button
        val pb: ProgressBar
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.popup_payment)
        cashBtn = dialog.findViewById(R.id.cashBtn)
        tfBtn = dialog.findViewById(R.id.tfBtn)
        dateBtn = dialog.findViewById(R.id.dateBtn)
        yesBtn = dialog.findViewById(R.id.yesBtn)
        noBtn = dialog.findViewById(R.id.noBtn)
        confirmBtn = dialog.findViewById(R.id.confirmBtn)
        pb = dialog.findViewById(R.id.progressBar)

        var datePayment = ""
        var paymentType = ""
        var paymentStatus = ""

        dateBtn.setOnClickListener {
            val datePicker: MaterialDatePicker<*> =
                MaterialDatePicker.Builder.datePicker().setTitleText("Pilih Tanggal Pembayaran").build()
            datePicker.show(supportFragmentManager, datePicker.toString())
            datePicker.addOnPositiveButtonClickListener { selection: Any ->
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val format = sdf.format(Date(selection.toString().toLong()))
                dateBtn.text = format
                datePayment = dateBtn.text.toString().trim()
            }
        }

        cashBtn.setOnClickListener {
            paymentType = "Cash"
            cashBtn.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.holo_orange_dark)
            tfBtn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.blue)
        }

        tfBtn.setOnClickListener {
            paymentType = "Transfer"
            tfBtn.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.holo_orange_dark)
            cashBtn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.blue)
        }

        yesBtn.setOnClickListener {
            paymentStatus = "Pembayaran Full"
            yesBtn.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.holo_orange_dark)
            noBtn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.blue)
        }

        noBtn.setOnClickListener {
            paymentStatus = "Pembayaran Sebagian"
            noBtn.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.holo_orange_dark)
            yesBtn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.blue)
        }

        confirmBtn?.setOnClickListener {

            if(paymentType == "") {
                Toast.makeText(this, "Maaf, anda harus memilih salah satu cara pembayaran", Toast.LENGTH_SHORT).show()
            } else if (datePayment == "") {
                Toast.makeText(this, "Maaf, anda harus memilih tanggal pembayaran", Toast.LENGTH_SHORT).show()
            } else if (paymentStatus == "") {
                Toast.makeText(this, "Maaf, anda harus memilih status pembayaran", Toast.LENGTH_SHORT).show()
            }
            else {
                pb.visibility = View.VISIBLE

                    val data = mapOf(
                        "paymentType" to paymentType,
                        "paymentDate" to datePayment,
                        "paymentStatus" to paymentStatus,
                    )

                    FirebaseFirestore
                        .getInstance()
                        .collection("invoice")
                        .document(model?.uid!!)
                        .update(data)
                        .addOnCompleteListener {
                            if(it.isSuccessful) {
                                dialog.dismiss()
                                pb.visibility = View.GONE
                                Toast.makeText(this, "Sukses memvalidasi pembayaran", Toast.LENGTH_SHORT).show()
                            } else {
                                dialog.dismiss()
                                pb.visibility = View.GONE
                                Toast.makeText(this, "Gagal memvalidasi pembayaran, silahkan periksa koneksi internet anda dan coba lagi nanti", Toast.LENGTH_SHORT).show()
                            }
                        }
            }
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
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
            val headingFontSize = 12.0f
            val valueFontSize = 14.0f

            /// custom font
            val fontName =
                BaseFont.createFont("assets/fonts/regular.ttf", "UTF-8", BaseFont.EMBEDDED)
            val headingStyle = Font(fontName, headingFontSize, Font.NORMAL, colorAccent)
            val valueStyle = Font(fontName, valueFontSize, Font.NORMAL, BaseColor.BLACK)
            val format: NumberFormat = DecimalFormat("#,###")


            /// add file to document
            val titleStyle = Font(fontName, 20.0f, Font.NORMAL, BaseColor.BLACK)
            addNewItem(document, "INVOICE", Element.ALIGN_CENTER, titleStyle)
            addLineSpace(document)
            addLineSpace(document)
            addNewItemWithLeftAndRight(
                document,
                "PT.Primax Asia Link",
                "Tanggal: ${model?.date}",
                valueStyle,
                valueStyle
            )
            addNewItemWithLeftAndRight(
                document,
                "Jl.Boulevard Artha Gading\nBlok A7A No.1 Kelapa Gading,\nJakarta Utara,14240",
                "Kepada Yth: ${model?.customerName}",
                valueStyle,
                valueStyle
            )
            addNewItemWithLeftAndRight(
                document,
                "Tel: 021-4587-4199",
                "No.Handphone: ${model?.customerPhone}",
                valueStyle,
                valueStyle
            )
            addNewItemWithLeftAndRight(
                document,
                "",
                "Alamat: ${model?.customerAddress}",
                valueStyle,
                valueStyle
            )

            addNewItem(document, "INVOICE ID:", Element.ALIGN_LEFT, headingStyle)
            val position = intent.getIntExtra(POSITION, 0)
            if(position+1 < 10) {
                addNewItem(document, "${model?.dateInvoiceId}${String.format("%03d", position+1)}", Element.ALIGN_LEFT, valueStyle)
            } else if (position+1 < 100) {
                addNewItem(document, "${model?.dateInvoiceId}${String.format("%02d", position+1)}", Element.ALIGN_LEFT, valueStyle)
            } else {
                addNewItem(document, "${model?.dateInvoiceId}${position+1}", Element.ALIGN_LEFT, valueStyle)
            }

            addLineSeparator(document)
            if(model?.category == "formulated") {
                addNewItem(document, "Kategori: Obat Racikan", Element.ALIGN_LEFT, valueStyle)
                addNewItem(document, "Status: ${model?.paymentStatus}", Element.ALIGN_LEFT, valueStyle)
                addNewItem(document, "Nama Obat Racikan: ${model?.product!![0].name}", Element.ALIGN_LEFT, valueStyle)
                addNewItem(document, "Nama Sales: ${model?.salesName}", Element.ALIGN_LEFT, valueStyle)
                addLineSpace(document)
                addLineSpace(document)
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
                addNewItem(document, "Status: ${model?.paymentStatus}", Element.ALIGN_LEFT, valueStyle)
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
            addNewItem(document, "${binding?.discPrice?.text}", Element.ALIGN_CENTER, valueStyle)
            addNewItem(document, "${binding?.ppnPrice?.text}", Element.ALIGN_CENTER, valueStyle)
            addNewItem(document, "Total Biaya: Rp.${format.format(finalPriceWithDiscountAndPpn)}", Element.ALIGN_CENTER, valueStyle)

            addLineSeparator(document)
            addLineSpace(document)
            addNewItem(document, "MEMO", Element.ALIGN_LEFT, headingStyle)
            addNewItem(document, "Pembayaran:", Element.ALIGN_LEFT, valueStyle)
            addNewItem(document, "Bank BCA\nNo.Rekening 635-013-638-8\nA/n = PT PRIMAX ASIA LINK", Element.ALIGN_LEFT, valueStyle)

            addLineSpace(document)
            addLineSpace(document)

            if(model?.customer2ndName != "") {
                addNewItem(document, "Dikirim ke alamat lain", Element.ALIGN_LEFT, headingStyle)
                addNewItem(document, "Kepada Yth: ${model?.customer2ndName}", Element.ALIGN_LEFT, valueStyle)
                addNewItem(document, "No.Handphone: ${model?.customer2ndPhone}", Element.ALIGN_LEFT, valueStyle)
                addNewItem(document, "Alamat: ${model?.customer2ndAddress}", Element.ALIGN_LEFT, valueStyle)
            }

            /// total
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
        const val POSITION = "position"
    }
}