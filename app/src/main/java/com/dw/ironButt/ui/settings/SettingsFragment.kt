package com.dw.ironButt.ui.settings

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.denisovdw.ironbutt.utils.IronButtConstant.Companion.TAG_DIALOG_INFO
import com.denisovdw.ironbutt.utils.IronUtils
import com.denisovdw.ironbutt.utils.SharedPrefsManager.Companion.STATE_PRESENTER_LOGIN
import com.dw.ironButt.App
import com.dw.ironButt.R
import com.dw.ironButt.database.file.FileHelper
import com.dw.ironButt.databinding.FragmentSettingsBinding
import com.dw.ironButt.ui.main.IOnBackPressed
import com.dw.ironButt.utils.DialogFragmentInfo

class SettingsFragment : Fragment(), IOnBackPressed {
    private val viewModel by viewModels<SettingsViewModel>()
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var fileHelper: FileHelper


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fileHelper = FileHelper(requireContext())
        fileHelper.deleteAllImages()
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_settings,
            container,
            false
        )
        binding.viewModelStart = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.error.observe(viewLifecycleOwner, {
            IronUtils.showToast(requireContext(), getString(it))
        })
        viewModel.successSaveUserList.observe(viewLifecycleOwner,{
            if (it){
                findNavController().navigate(R.id.startFragment)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.back_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_log_in -> {
                App.sharedPrefsManager.setStateApplication(STATE_PRESENTER_LOGIN)
                findNavController().popBackStack()
                findNavController().navigate(R.id.loginFragment)
            }
            R.id.action_info -> {
                val dialogInfo = DialogFragmentInfo(getString(R.string.info_text_comment))
                val transaction: FragmentTransaction =
                    (context as FragmentActivity)
                        .supportFragmentManager
                        .beginTransaction()
                dialogInfo.show(transaction, TAG_DIALOG_INFO)
            }
            android.R.id.home -> {
                App.sharedPrefsManager.setStateApplication(STATE_PRESENTER_LOGIN)
                findNavController().popBackStack()
                findNavController().navigate(R.id.loginFragment)
            }
        }
        return true
    }

    override fun onBackPressed(double: Boolean) {
        if (double) {
            activity?.finish()
        } else {
            IronUtils.showToast(requireContext(), getString(R.string.press_to_exit))
        }

    }
}