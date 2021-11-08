package dev.maxsiomin.advancednumbergenerator.fragments.home

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import dev.maxsiomin.advancednumbergenerator.base.BaseFragment
import dev.maxsiomin.advancednumbergenerator.util.SharedDataKeys.GENERATED_NUMBER
import dev.maxsiomin.advancednumbergenerator.fragments.contract.navigator
import dev.maxsiomin.advancednumbergenerator.util.sharedData
import dagger.hilt.android.AndroidEntryPoint
import dev.maxsiomin.advancednumbergenerator.R
import dev.maxsiomin.advancednumbergenerator.databinding.FragmentHomeBinding
import dev.maxsiomin.advancednumbergenerator.fragments.settings.SettingsFragment
import dev.maxsiomin.advancednumbergenerator.util.SharedDataKeys.MAX_NUMBER
import dev.maxsiomin.advancednumbergenerator.util.SharedDataKeys.MIN_NUMBER
import dev.maxsiomin.advancednumbergenerator.util.*

@AndroidEntryPoint
class HomeFragment : BaseFragment(R.layout.fragment_home) {

    override var _binding: ViewDataBinding? = null
    // This property is only valid between onCreateView and onDestroyView
    private val binding get() = _binding!! as FragmentHomeBinding

    override val mViewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        setHasOptionsMenu(true)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        with (binding) {
            viewModel = mViewModel
            lifecycleOwner = viewLifecycleOwner

            val loadedMin = sharedData.getSharedLong(MIN_NUMBER)
            editTextMinNum.setEms(
                if (loadedMin != null) {
                    editTextMinNum.text = loadedMin.toEditable()
                    getEmsByLength(loadedMin.toInt())
                } else {
                    0
                }
            )

            val loadedMax = sharedData.getSharedLong(MAX_NUMBER)
            editTextMaxNum.setEms(
                if (loadedMax != null) {
                    editTextMaxNum.text = loadedMax.toEditable()
                    getEmsByLength(loadedMax.toInt())
                } else {
                    0
                }
            )

            val loadedGeneratedNumber = sharedData.getSharedLong(GENERATED_NUMBER)
            loadedGeneratedNumber?.let {
                mViewModel.generatedNumberLiveData.value = it.toString()
            }

            generateButton.setOnClickListener {
                val min = editTextMinNum.text.toString()
                val max = editTextMaxNum.text.toString()
                val cheatingEnabled =
                    mViewModel.sharedPrefs.getBoolean(
                        R.string.key_cheating_enabled,
                        false,
                        requireContext()
                    )
                val cheatingValue =
                    mViewModel.sharedPrefs.getNullableString(R.string.key_cheating_value, requireContext())

                when (val result: GenerationResult = mViewModel.generateNumber(min, max, cheatingEnabled, cheatingValue)) {
                    is GenerationSuccess -> mViewModel.onNumberGenerated(result.generatedNumber)
                    is GenerationFailure -> {
                        mViewModel.toast(result.error, Toast.LENGTH_LONG)
                        mViewModel.onNumberGenerated(null)
                    }
                }

                onClearFocusesAndHideKeyboard()
            }

            editTextMinNum.addTextChangedListener { newText ->
                newText?.let {
                    try {
                        sharedData.putSharedLong(MAX_NUMBER, it.toLong())
                        sharedData.putSharedLong(MIN_NUMBER, it.toLong())
                    } catch (e: NumberFormatException) {
                        //Timber.e(e)
                    }

                    editTextMinNum.setEms(getEmsByLength(it.length))
                }
            }

            editTextMaxNum.addTextChangedListener { newText ->
                newText?.let {
                    try {
                        sharedData.putSharedLong(MAX_NUMBER, it.toLong())
                    } catch (e: NumberFormatException) {
                        //Timber.e(e)
                    }

                    editTextMaxNum.setEms(getEmsByLength(it.length))
                }
            }
        }

        mViewModel.generatedNumberLiveData.observe(viewLifecycleOwner, { newValue ->
            newValue.safeToLong()?.let {
                sharedData.putSharedLong(GENERATED_NUMBER, it)
            }
        })

        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.id) {
            R.id.settings_menu_item -> navigator.launchFragment(
                R.id.main_activity_fragment_container,
                SettingsFragment.newInstance()
            )
            else -> return false
        }
        return true
    }

    companion object {

        @JvmStatic
        fun newInstance(): HomeFragment = HomeFragment()
    }
}
