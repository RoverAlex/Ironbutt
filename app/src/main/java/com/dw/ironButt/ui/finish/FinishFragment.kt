package com.dw.ironButt.ui.finish

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import com.dw.ironButt.utils.IronButtConstant.Companion.TAG_DIALOG_INFO
import com.dw.ironButt.utils.IronUtils
import com.dw.ironButt.utils.SharedPrefsManager.Companion.STATE_PRESENTER_FINISH
import com.dw.ironButt.App
import com.dw.ironButt.R
import com.dw.ironButt.database.file.FileHelper
import com.dw.ironButt.databinding.FragmentFinishBinding
import com.dw.ironButt.ui.dialog.DialogStartOver
import com.dw.ironButt.ui.main.IOnBackPressed
import com.dw.ironButt.utils.DialogFragmentInfo
import com.dw.ironButt.utils.HelperMaps
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class FinishFragment : Fragment(), IOnBackPressed {
    val viewModel by viewModels<FinishViewModel>()
    lateinit var binding: FragmentFinishBinding
    private lateinit var fileHelper: FileHelper
    private lateinit var mMap: GoogleMap


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_finish,
            container,
            false
        )
        binding.viewModelFinish = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.sharedPrefsManager.setStateApplication(STATE_PRESENTER_FINISH)
        fileHelper = FileHelper(requireContext())
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_finish) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        trackMarker()
        listenerObserve()
    }

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = false
        mMap.uiSettings.setAllGesturesEnabled(false)
        mMap.uiSettings.isMapToolbarEnabled = false
    }

    private fun trackMarker() {
        viewModel.getListTrack { liveDataPints ->
            liveDataPints.observe(viewLifecycleOwner, { points ->
                viewModel.hideProgressBar(true)
                points.forEach {
                    HelperMaps.mapMarker(mMap, viewModel.startMarkerMap, it)
                }
            })
        }
    }

    private fun listenerObserve() {
        viewModel.newStart.observe(viewLifecycleOwner, {
            if (it) newStartDialog(requireContext())
            return@observe
        })
    }

    override fun onBackPressed(double: Boolean) {
        if (double) {
            activity?.finish()
        } else {
            IronUtils.showToast(requireContext(), getString(R.string.press_to_exit))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.way_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_info -> {
                val dialogInfo =
                    DialogFragmentInfo("Вы можете отправить свой маршрут в любое время")
                val transaction: FragmentTransaction =
                    (context as FragmentActivity)
                        .supportFragmentManager
                        .beginTransaction()
                dialogInfo.show(transaction, TAG_DIALOG_INFO)
            }
            R.id.action_start_over ->{
                returnDialogStart()
            }
        }
        return true
    }

    private fun returnDialogStart(){
        val dialog = DialogStartOver()
        val transaction: FragmentTransaction =
            (context as FragmentActivity)
                .supportFragmentManager
                .beginTransaction()
        dialog.show(transaction, "dialog_start")
    }

    private fun newStartDialog(context: Context?) {
        val dialogReturnStart = DialogFinishToStart()
        val transaction: FragmentTransaction =
            (context as FragmentActivity)
                .supportFragmentManager
                .beginTransaction()
        dialogReturnStart.show(transaction, "dialog_start")
    }

    override fun onStart() {
        super.onStart()
        viewModel.newTotalDistance()
    }

}