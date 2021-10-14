package com.dw.ironButt.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.denisovdw.ironbutt.database.room.UserList
import com.denisovdw.ironbutt.utils.IronUtils
import com.denisovdw.ironbutt.utils.myLog
import com.dw.ironButt.R
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.*
import java.io.IOException


class QrFragment : Fragment() {
    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var scannerView: CodeScannerView
    private lateinit var codeScanner: CodeScanner
    private lateinit var btnFile: LinearLayout

    private val gson = Gson()
    private val mJob = Job()
    private val mUiScope = CoroutineScope(Dispatchers.Main + mJob)

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->
            val image: InputImage
            try {
                image = InputImage.fromFilePath(requireContext(), imageUri)
                scanBarcodes(image)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_qr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btnFile = view.findViewById(R.id.btn_file_qr_code)
        scannerView = view.findViewById(R.id.scanner_view)
        scan()
        imagePicker()
    }

    private fun imagePicker() {
        btnFile.setOnClickListener {
            pickImage.launch("image/*")
        }
    }

    private fun scanBarcodes(image: InputImage) {
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_DATA_MATRIX
            )
            .build()

        val scanner = BarcodeScanning.getClient()

        scanner.process(image)
            .addOnSuccessListener { barcodes ->

                if (barcodes.size > 0) {
                    for (barcode in barcodes) {
                        val rawValue = barcode.rawValue
                        checkAndSave(rawValue)
                    }
                }else
                    errorScan()

            }
            .addOnFailureListener {
                // Task failed with an exception
                IronUtils.showToast(requireContext(),"Task failed with an exception ${it.message}")
             }
    }

    private fun errorScan() {
        IronUtils.showToast(requireContext(),getString(R.string.wrong_format))
    }

    private fun scan() {
        val activity = requireActivity()
        codeScanner = CodeScanner(activity, scannerView)
        codeScanner.decodeCallback = DecodeCallback { resultScan ->
            checkAndSave(resultScan.text)
        }

    }

    private fun checkAndSave(resultScan: String) {
        requireActivity().runOnUiThread {
            try {
                val resultQr = gson.fromJson(resultScan, UserList::class.java)
                myLog("id ${resultQr.id}")
                myLog("authorLk ${resultQr.authorLk}")
                myLog("name ${resultQr.fullName}")
                myLog("email ${resultQr.email}")

                authUser(resultQr)

            } catch (exception: IllegalStateException) {
                myLog("gSon fail")
                reStart()

            } catch (exception: JsonSyntaxException) {
                errorScan()
                reStart()

            }
        }
    }

    private fun authUser(resultQr: UserList) {
        viewModel.authUser(resultQr) {
            myLog("result: $it")
            if (it) {
                mUiScope.launch {
                    withContext(Dispatchers.Main) {
                        findNavController().popBackStack()
                        findNavController().navigate(R.id.loginFragment)
                    }
                }

            }
        }
    }

    private fun reStart() {
        codeScanner.startPreview()
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->{
                findNavController().popBackStack()
                findNavController().navigate(R.id.loginFragment)
            }
        }
        return true
    }


}