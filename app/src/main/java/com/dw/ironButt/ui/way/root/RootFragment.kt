package com.dw.ironButt.ui.way.root

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.denisovdw.ironbutt.utils.IronButtConstant.Companion.TAG_DIALOG_INFO
import com.denisovdw.ironbutt.utils.IronUtils
import com.denisovdw.ironbutt.utils.SharedPrefsManager.Companion.STATE_PRESENTER_ROOT
import com.dw.ironButt.App
import com.dw.ironButt.R
import com.dw.ironButt.database.file.FileHelper
import com.dw.ironButt.databinding.FragmentRootBinding
import com.dw.ironButt.ui.dialog.DialogFinish
import com.dw.ironButt.ui.dialog.DialogStartOver
import com.dw.ironButt.ui.main.IOnBackPressed
import com.dw.ironButt.utils.DialogFragmentInfo
import com.dw.ironButt.utils.HelperMaps
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class RootFragment : Fragment(), IOnBackPressed {
    private lateinit var binding: FragmentRootBinding
    private val viewModel by viewModels<RootViewModel>()
    private lateinit var mMap: GoogleMap
    private lateinit var fileHelper: FileHelper
    private val pref = App.sharedPrefsManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_root,
            container,
            false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref.setStateApplication(STATE_PRESENTER_ROOT)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_start) as SupportMapFragment?
        mapFragment?.getMapAsync(callbackMap)

        fileHelper = FileHelper(requireContext())
        listenerObserve()

    }

    private fun listenerObserve() {
        viewModel.error.observe(viewLifecycleOwner, {
            IronUtils.showToast(requireContext(), it)
        })
        viewModel.takePhoto.observe(viewLifecycleOwner, {
            if (it) {
                takeImage()
            }

        })
        viewModel.finishFragment.observe(viewLifecycleOwner, {
            if (it) {
                findNavController().navigate(R.id.finishFragment)
            }
        })
        viewModel.finish.observe(viewLifecycleOwner, {
            if (it) {
                val dialog = DialogFinish(viewModel)
                val transaction: FragmentTransaction =
                    (context as FragmentActivity)
                        .supportFragmentManager
                        .beginTransaction()
                dialog.show(transaction, "dialog_finish")
            }
        })
    }

    private fun takeImage() {
        lifecycleScope.launchWhenStarted {
            fileHelper.getTmpFileUri().let { uri ->
                takeImageResult.launch(uri)
            }
        }
    }

    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                fileHelper.saveImage(getString(R.string.point)) { resSaveFile ->
                    if (resSaveFile) {
                        viewModel.takePhoto(false)
                        viewModel.saveDate()
                    }
                }
            }
        }

    private val callbackMap = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = false
        mMap.uiSettings.setAllGesturesEnabled(false)
        mMap.uiSettings.isMapToolbarEnabled = false
        trackMarker()
    }

    private fun trackMarker() {
        viewModel.getListTrack { liveDataPints ->
            liveDataPints.observe(viewLifecycleOwner, { points ->
                //viewModel.startMarkerMap.clear()
                points.forEach { pointLocation ->
                    //mapMarkerUser(it)
                    HelperMaps.mapMarker(mMap, viewModel.startMarkerMap, pointLocation)
                    viewModel.flagPoint = true
                }
            })
        }

        App.currentLocation.observe(viewLifecycleOwner, { loc ->
            viewModel.latestLocationData = loc

            // if (viewModel.checkTime()){
            viewModel.newPointLocation.latitude = loc.latitude
            viewModel.newPointLocation.longitude = loc.longitude
            viewModel.newPointLocation.time = loc.time
            viewModel.newPointLocation.infoTrack = getString(R.string.are_you_here)
            //mapMarkerUser(viewModel.newPointLocation, false)
            viewModel.hideProgressBar(true)

            HelperMaps.mapMarker(
                mMap, viewModel.startMarkerMap,
                viewModel.newPointLocation,
                false
            )
            // }

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
            R.id.action_info -> infoDialog()
            R.id.action_settings -> findNavController().navigate(R.id.settingsFragment)
            R.id.action_start_over -> cancelDialog(requireContext())
        }
        return true
    }


    private fun infoDialog() {
        val dialogInfo = DialogFragmentInfo(getString(R.string.receipt_information))
        val transaction: FragmentTransaction =
            (context as FragmentActivity)
                .supportFragmentManager
                .beginTransaction()
        dialogInfo.show(transaction, TAG_DIALOG_INFO)
    }

    private fun cancelDialog(context: Context?) {
        viewModel.startMarkerMap.forEach {
            if (it.key != "0") it.value.remove()
        }
        val removeDialogFragment = DialogStartOver()
        val transaction: FragmentTransaction =
            (context as FragmentActivity)
                .supportFragmentManager
                .beginTransaction()
        removeDialogFragment.show(transaction, TAG_DIALOG_INFO)
    }

    override fun onResume() {
        super.onResume()
        viewModel.createTimer()
        App.startStopLocation.value = true
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopTimer()
        App.startStopLocation.value = false
    }

//    override fun onStart() {
//        super.onStart()
//        viewModel.createTimer()
//        App.startStopLocation.value = true
//    }
//
//    override fun onStop() {
//        super.onStop()
//        viewModel.stopTimer()
//        App.startStopLocation.value = false
//    }


}