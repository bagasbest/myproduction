package com.project.myproduction.ui.purchase_order.option

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.myproduction.R
import com.project.myproduction.databinding.FragmentHerbBinding
import com.project.myproduction.ui.obat_umum.HerbsAdapter
import com.project.myproduction.ui.obat_umum.HerbsViewModel
import com.project.myproduction.ui.purchase_order.POAdapter
import com.project.myproduction.ui.purchase_order.POViewModel


class HerbFragment : Fragment() {

    private var binding: FragmentHerbBinding? = null
    private var adapter: POAdapter? = null

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
        val sendAnotherCb: CheckBox
        val receiverName2nd: TextInputEditText
        val receiverAddress2nd: TextInputEditText
        val confirmBtn: Button
        val address2nd: LinearLayout
        val pb: ProgressBar
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.popup_purchase_order)
        confirmBtn = dialog.findViewById(R.id.confirmBtn)
        pb = dialog.findViewById(R.id.progressBar)
        receiverName = dialog.findViewById(R.id.receiverName)
        receiverAddress = dialog.findViewById(R.id.receiverAddress)
        receiverName2nd = dialog.findViewById(R.id.receiverName2nd)
        receiverAddress2nd = dialog.findViewById(R.id.receiverAddress2nd)
        sendAnotherCb = dialog.findViewById(R.id.sendToAnotherCb)
        address2nd = dialog.findViewById(R.id.address2nd)


        sendAnotherCb.setOnClickListener {
            if(sendAnotherCb.isChecked) {
                address2nd.visibility = View.VISIBLE
            } else {
                address2nd.visibility = View.GONE
            }
        }

        confirmBtn?.setOnClickListener {
            val name = receiverName.text.toString().trim()
            val address = receiverAddress.text.toString().trim()
            val name2nd = receiverName2nd.text.toString().trim()
            val recAddress2nd = receiverAddress2nd.text.toString().trim()

            if(name.isEmpty()) {
                Toast.makeText(activity, "Maaf, Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else if (address.isEmpty()) {
                Toast.makeText(activity, "Maaf, Alamat tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else if(sendAnotherCb.isChecked && name2nd.isEmpty()) {
                Toast.makeText(activity, "Maaf, Nama penerima lain tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else if(sendAnotherCb.isChecked && recAddress2nd.isEmpty()) {
                Toast.makeText(activity, "Maaf, Alamat penerima lain tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else {
                dialog.dismiss()
            }
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun initRecyclerView() {
        binding?.rvOrderProcess?.layoutManager = LinearLayoutManager(activity)
        adapter = POAdapter()
        binding?.rvOrderProcess?.adapter = adapter
    }

    private fun initViewModel() {
        val viewModel = ViewModelProvider(this)[POViewModel::class.java]
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        binding?.progressBar?.visibility = View.VISIBLE

        viewModel.setListProductCommon(uid)
        viewModel.getProduct().observe(viewLifecycleOwner) { productList ->
            if (productList.size > 0) {
                adapter?.setData(productList)
                binding?.noData?.visibility = View.GONE
            } else {
                binding?.noData?.visibility = View.VISIBLE
            }
            binding!!.progressBar.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}