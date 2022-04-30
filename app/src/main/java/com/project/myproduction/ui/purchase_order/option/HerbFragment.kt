package com.project.myproduction.ui.purchase_order.option

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.myproduction.R
import com.project.myproduction.databinding.FragmentHerbBinding
import com.project.myproduction.ui.obat_racikan.material.MaterialModel
import com.project.myproduction.ui.obat_racikan.material.MaterialViewModel
import com.project.myproduction.ui.purchase_order.POAdapter
import com.project.myproduction.ui.purchase_order.POModel
import com.project.myproduction.ui.purchase_order.POViewModel
import com.project.myproduction.ui.settings.cusotomer_data.CustomerDataModel
import com.project.myproduction.ui.settings.cusotomer_data.CustomerDataViewModel
import com.toptoche.searchablespinnerlibrary.SearchableSpinner


class HerbFragment : Fragment() {

    private var binding: FragmentHerbBinding? = null
    private var adapter: POAdapter? = null
    private var salesName: String? = null
    private var poList = ArrayList<POModel>()
    private var totalPrice: Long? = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHerbBinding.inflate(inflater, container, false)

        initRecyclerView()
        initViewModel()

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.poBtn?.setOnClickListener {
            showPopupPO()
        }
    }

    private fun showPopupPO() {
        val receiverName: TextInputEditText
        val receiverAddress: TextInputEditText
        val receiverPhone: TextInputEditText
        val sendAnotherCb: CheckBox
        val receiverName2nd: TextInputEditText
        val receiverAddress2nd: TextInputEditText
        val receiverPhone2nd: TextInputEditText
        val et1: TextInputLayout
        val et2: TextInputLayout
        val et3: TextInputLayout
        val searchableSpinner: SearchableSpinner
        val confirmBtn: Button
        val address2nd: LinearLayout
        val pb: ProgressBar
        val dialog = Dialog(requireContext())
        val customerDataList = ArrayList<CustomerDataModel>()
        dialog.setContentView(R.layout.popup_purchase_order)
        confirmBtn = dialog.findViewById(R.id.confirmBtn)
        pb = dialog.findViewById(R.id.progressBar)
        et1 = dialog.findViewById(R.id.et1)
        et2 = dialog.findViewById(R.id.et2)
        et3 = dialog.findViewById(R.id.et3)
        receiverName = dialog.findViewById(R.id.receiverName)
        receiverAddress = dialog.findViewById(R.id.receiverAddress)
        receiverPhone = dialog.findViewById(R.id.phone)
        receiverName2nd = dialog.findViewById(R.id.receiverName2nd)
        receiverAddress2nd = dialog.findViewById(R.id.receiverAddress2nd)
        receiverPhone2nd = dialog.findViewById(R.id.phone2nd)
        sendAnotherCb = dialog.findViewById(R.id.sendToAnotherCb)
        address2nd = dialog.findViewById(R.id.address2nd)
        searchableSpinner = dialog.findViewById(R.id.searchable_spinner)

        val viewModel = ViewModelProvider(this)[CustomerDataViewModel::class.java]
        viewModel.setListCustomerData()
        viewModel.getCustomerData().observe(viewLifecycleOwner) { customerData ->
            if (customerData.size > 0) {
                   customerDataList.addAll(customerData)


                val propertyName = getCustomerNameList(customerDataList)
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_expandable_list_item_1, propertyName)
                searchableSpinner.adapter = adapter

                searchableSpinner?.onItemSelectedListener =
                    (object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            val customer = customerDataList[p2]
                            val name = customer.name
                            val phone = customer.phone
                            val address = customer.address

                            receiverName.setText(name)
                            receiverPhone.setText(phone)
                            receiverAddress.setText(address)
                            et1.isEnabled = false
                            et2.isEnabled = false
                            et3.isEnabled = false

                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {

                        }
                    })
            }
        }

        sendAnotherCb.setOnClickListener {
            if (sendAnotherCb.isChecked) {
                address2nd.visibility = View.VISIBLE
            } else {
                address2nd.visibility = View.GONE
            }
        }

        confirmBtn?.setOnClickListener {
            val name = receiverName.text.toString().trim()
            val address = receiverAddress.text.toString().trim()
            val phone = receiverPhone.text.toString().trim()
            val name2nd = receiverName2nd.text.toString().trim()
            val recAddress2nd = receiverAddress2nd.text.toString().trim()
            val phone2nd = receiverPhone2nd.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(activity, "Maaf, Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else if (address.isEmpty()) {
                Toast.makeText(activity, "Maaf, Alamat tidak boleh kosong", Toast.LENGTH_SHORT)
                    .show()
            } else if (phone.isEmpty()) {
                Toast.makeText(
                    activity,
                    "Maaf, No.Handphone tidak boleh kosong",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (sendAnotherCb.isChecked && name2nd.isEmpty()) {
                Toast.makeText(
                    activity,
                    "Maaf, Nama penerima lain tidak boleh kosong",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (sendAnotherCb.isChecked && recAddress2nd.isEmpty()) {
                Toast.makeText(
                    activity,
                    "Maaf, Alamat penerima lain tidak boleh kosong",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (sendAnotherCb.isChecked && phone2nd.isEmpty()) {
                Toast.makeText(
                    activity,
                    "Maaf, No.Handphone penerima tidak boleh kosong",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                pb.visibility = View.VISIBLE
                totalPrice = 0L
                getSalesName()

                for(i in poList.indices) {
                    totalPrice = totalPrice?.plus(poList[i].price!!)
                }


                Handler().postDelayed({
                    if(et1.isEnabled) {
                        val uid = System.currentTimeMillis().toString()

                        val data = mapOf(
                            "uid" to uid,
                            "product" to poList,
                            "totalPrice" to totalPrice,
                            "salesName" to salesName,
                            "customerName" to name,
                            "customerPhone" to  phone,
                            "customerAddress" to address,
                            "customer2ndName" to "" + name2nd,
                            "customer2ndPhone" to  "" + phone2nd,
                            "customer2ndAddress" to "" + address2nd,
                            "status" to "Belum Disetujui",
                        )

                        FirebaseFirestore
                            .getInstance()
                            .collection("order")
                            .document(uid)
                            .set(data)
                            .addOnCompleteListener {
                                if(it.isSuccessful) {

                                    for(i in poList.indices) {
                                        FirebaseFirestore
                                            .getInstance()
                                            .collection("purchase_order")
                                            .document(poList[i].uid!!)
                                            .delete()
                                    }

                                    val customerData = mapOf(
                                        "uid" to uid,
                                        "name" to name,
                                        "phone" to phone,
                                        "address" to address,
                                    )

                                    FirebaseFirestore
                                        .getInstance()
                                        .collection("customer_data")
                                        .document(uid)
                                        .set(customerData)
                                        .addOnCompleteListener {
                                            binding?.rvOrderProcess?.visibility = View.GONE
                                            binding?.noData?.visibility = View.VISIBLE
                                            binding?.poBtn?.isEnabled = false
                                            pb.visibility = View.GONE
                                            dialog.dismiss()
                                            showSuccessDialog()
                                        }
                                } else {
                                    pb.visibility = View.GONE
                                    dialog.dismiss()
                                    showFailureDialog()
                                }
                            }
                    }
                }, 1500)
            }
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun getSalesName() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                salesName = "" + it.data!!["name"]
            }
    }

    private fun getCustomerNameList(customerDataList: ArrayList<CustomerDataModel>): List<String> {
        val result = ArrayList<String>()
        for (material in customerDataList) {
            result.add(material.name!!)
        }
        return result
    }

    private fun initRecyclerView() {
        binding?.rvOrderProcess?.layoutManager = LinearLayoutManager(activity)
        adapter = POAdapter(binding?.poBtn)
        binding?.rvOrderProcess?.adapter = adapter
    }

    private fun initViewModel() {
        val viewModel = ViewModelProvider(this)[POViewModel::class.java]
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        binding?.progressBar?.visibility = View.VISIBLE

        viewModel.setListProductCommon(uid)
        viewModel.getProduct().observe(viewLifecycleOwner) { productList ->
            if (productList.size > 0) {
                poList.clear()
                poList.addAll(productList)
                adapter?.setData(poList)
                binding?.noData?.visibility = View.GONE
                binding?.poBtn?.isEnabled = true

            } else {
                binding?.noData?.visibility = View.VISIBLE
            }
            binding!!.progressBar.visibility = View.GONE
        }
    }

    private fun showFailureDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Gagal Mengonfirmasi Purchase Order")
            .setMessage("Ups, koneksi internet anda bermasalah, silahkan coba lagi nanti")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Sukses Mengonfirmasi Purchase Order")
            .setMessage("Selanjutnya, Admin akan melakukan konfirmasi")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("OK") { dialogInterface, _ ->

                dialogInterface.dismiss()
            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}