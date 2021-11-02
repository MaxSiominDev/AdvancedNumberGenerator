package dev.maxsiomin.advancednumbergenerator.base

import androidx.annotation.LayoutRes
import androidx.core.view.allViews
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

/**
 * All classes that has super class [Fragment] by default must extend this class instead of [Fragment]
 */
abstract class BaseFragment(@LayoutRes resId: Int, val usedByBaseActivity: Boolean) : Fragment(resId) {

    protected open val mViewModel by viewModels<BaseViewModel>()

    @Suppress("PropertyName")
    abstract var _binding: ViewDataBinding?

    // This property is only valid between onCreateView and onDestroyView
    private val binding get() = _binding!!

    override fun onStart() {
        super.onStart()
        binding.root.setOnClickListener { onClearFocusesAndHideKeyboard() }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    protected open fun onClearFocusesAndHideKeyboard() {
        mViewModel.hideKeyboard(binding.root.windowToken)

        binding.root.allViews.forEach {
            it.clearFocus()
        }
    }
}
