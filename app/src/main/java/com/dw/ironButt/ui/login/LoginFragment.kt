package com.dw.ironButt.ui.login

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dw.ironButt.database.room.UserList
import com.dw.ironButt.utils.IronButtConstant.Companion.TAG_DIALOG_INFO
import com.dw.ironButt.utils.IronUtils
import com.dw.ironButt.utils.SharedPrefsManager.Companion.STATE_PRESENTER_FINISH
import com.dw.ironButt.utils.SharedPrefsManager.Companion.STATE_PRESENTER_LOGIN
import com.dw.ironButt.utils.SharedPrefsManager.Companion.STATE_PRESENTER_ROOT
import com.dw.ironButt.utils.SharedPrefsManager.Companion.STATE_PRESENTER_SETTINGS
import com.dw.ironButt.utils.SharedPrefsManager.Companion.STATE_PRESENTER_START
import com.dw.ironButt.App
import com.dw.ironButt.R
import com.dw.ironButt.databinding.FragmentLoginBinding
import com.dw.ironButt.ui.main.IOnBackPressed
import com.dw.ironButt.utils.DialogFragmentInfo
import kotlinx.coroutines.*


class LoginFragment : Fragment(), IOnBackPressed {
    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var binding: FragmentLoginBinding
    private val pref = App.sharedPrefsManager
    private var myRequestPermission = false

    private val mJob = Job()
    private val mUiScope = CoroutineScope(Dispatchers.Main + mJob)

    private val requestMultiplePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    )
    { resultsMap ->
        val countPermission = resultsMap.size
        var i = 0
        resultsMap.forEach {
            if (it.value) {
                i++
                //myLog(" permission: ${it.value}")
                //myLog(" permission: ${it.key}")
            } else {
                IronUtils.showToast(
                    requireContext(),
                    "Необходима разрешение ${it.key}"
                )
            }
            if (countPermission == i) {
                myRequestPermission = true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login,
            container,
            false
        )
        binding.loginViewModelBind = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        viewModel.getError().observe(viewLifecycleOwner, { error ->
            IronUtils.showToast(requireContext(), error!!)

        })
        requestMultiplePermissionLauncher.launch(IronUtils.arrayPermissions)
        betaVersion()
        qrFragment()
        infoQr()
        infoBetta()
        appState()
        return binding.root
    }


    private fun appState() {
        //pref.setStateApplication(STATE_PRESENTER_START)
        //findNavController().popBackStack()
        when (pref.getStateApplication()) {
            STATE_PRESENTER_LOGIN -> viewModel.getAuthenticated(false)
            STATE_PRESENTER_SETTINGS -> findNavController().navigate(R.id.settingsFragment)
            STATE_PRESENTER_START -> findNavController().navigate(R.id.startFragment)
            STATE_PRESENTER_ROOT -> findNavController().navigate(R.id.rootFragment)
            STATE_PRESENTER_FINISH -> findNavController().navigate(R.id.finishFragment)
        }
    }

    private fun qrFragment() {
        binding.root.findViewById<TextView>(R.id.btn_qr).setOnClickListener {
            if (myRequestPermission)
                findNavController().navigate(R.id.qrFragment)
            else
                requestMultiplePermissionLauncher.launch(IronUtils.arrayPermissions)

        }
    }

    private fun infoQr() {
        binding.root.findViewById<ImageView>(R.id.info_qr).setOnClickListener {
            dialogInfo(getString(R.string.description_qr))
        }
    }
    private fun infoBetta() {
        binding.root.findViewById<ImageView>(R.id.info_betta).setOnClickListener {
            dialogInfo("Для тестироывания. Вы авирпизовываетесь автоматичеки под логином тестировщика")
        }
    }

    private fun authUser(userList: UserList) {
        viewModel.authUser(userList) { result ->
            if (result) {
                mUiScope.launch {
                    withContext(Dispatchers.Main) {
                        appState()
                    }
                }

            }
        }
    }


    private fun betaVersion() {
        binding.root.findViewById<TextView>(R.id.beta_version).setOnClickListener {
            if (myRequestPermission) {
                val userList = UserList()
                if (pref.getUserName() != null) {
                    userList.fullName = pref.getUserName()!!
                }
                authUser(userList)
            } else
                requestMultiplePermissionLauncher.launch(IronUtils.arrayPermissions)


        }
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
        inflater.inflate(R.menu.start_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_info -> {
                dialogInfo(getString(R.string.info_application))
            }
            android.R.id.home -> activity?.finish()
        }
        return true
    }

    private fun dialogInfo(info: String) {
        val dialogInfo = DialogFragmentInfo(info)
        val transaction: FragmentTransaction =
            (context as FragmentActivity)
                .supportFragmentManager
                .beginTransaction()
        dialogInfo.show(transaction, TAG_DIALOG_INFO)
    }


}