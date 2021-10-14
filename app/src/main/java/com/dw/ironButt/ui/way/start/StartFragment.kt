package com.dw.ironButt.ui.way.start

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
import com.denisovdw.ironbutt.utils.SharedPrefsManager.Companion.STATE_PRESENTER_LOGIN
import com.denisovdw.ironbutt.utils.SharedPrefsManager.Companion.STATE_PRESENTER_SETTINGS
import com.denisovdw.ironbutt.utils.SharedPrefsManager.Companion.STATE_PRESENTER_START
import com.dw.ironButt.App
import com.dw.ironButt.R
import com.dw.ironButt.database.file.FileHelper
import com.dw.ironButt.databinding.FragmentStartBinding
import com.dw.ironButt.ui.main.IOnBackPressed
import com.dw.ironButt.utils.DialogFragmentInfo
import com.dw.ironButt.utils.HelperMaps
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class StartFragment : Fragment(), IOnBackPressed {
    private lateinit var binding: FragmentStartBinding
    private val viewModel by viewModels<StartViewModel>()
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
            R.layout.fragment_start,
            container,
            false
        )
        binding.viewModelStart = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.sharedPrefsManager.setStateApplication(STATE_PRESENTER_START)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_start) as SupportMapFragment?
        mapFragment?.getMapAsync(callbackMap)
        fileHelper = FileHelper(requireContext())
        zeroInt()
        listenerObserve()

    }
    private fun zeroInt() {
        pref.setLastOdometer(0)
        fileHelper.deleteAllImages()
        viewModel.deleteBaseTrack()
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
        viewModel.rootFragment.observe(viewLifecycleOwner, {
            if (it) {
                findNavController().navigate(R.id.rootFragment)
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
                fileHelper.saveImage(getString(R.string.start)) { resSaveFile ->
                    if (resSaveFile) {
                        viewModel.isSuccessPhoto()
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
        viewModel.startMarkerMap.clear()
        currentMarker()
    }

    private fun currentMarker() {
            App.currentLocation.observe(viewLifecycleOwner, { loc ->
                viewModel.latestLocationData = loc
                viewModel.hideProgressBar(true)
                viewModel.visibilityBtnStart(true)
                viewModel.visibilityEditText(true)
                viewModel.newPointLocation = IronUtils.newCurrentMarker(loc)
                HelperMaps.mapMarker(
                    mMap, viewModel.startMarkerMap,
                    IronUtils.newCurrentMarker(loc),
                    false
                )
            })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.start_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_info -> infoDialog()
            R.id.action_settings -> settings()
            R.id.action_log_in -> toLogin()
        }
        return true
    }

    private fun settings() {
        pref.setStateApplication(STATE_PRESENTER_SETTINGS)
        findNavController().navigate(R.id.settingsFragment)
    }

    private fun infoDialog() {
        val dialogInfo = DialogFragmentInfo(getString(R.string.start_information))
        val transaction: FragmentTransaction =
            (context as FragmentActivity)
                .supportFragmentManager
                .beginTransaction()
        dialogInfo.show(transaction, TAG_DIALOG_INFO)
    }


    private fun toLogin() {
        pref.setStateApplication(STATE_PRESENTER_LOGIN)
        findNavController().navigate(R.id.action_startFragment_to_loginFragment)
    }

    override fun onStart() {
        super.onStart()
        App.startStopLocation.value = true
    }

    override fun onStop() {
        super.onStop()
        App.startStopLocation.value = false
    }

    override fun onBackPressed(double: Boolean) {
        if (double) {
            activity?.finish()
        } else {
            IronUtils.showToast(requireContext(), getString(R.string.press_to_exit))
        }
    }


}