package dev.maxsiomin.advancednumbergenerator.fragments.contract

import androidx.fragment.app.Fragment

val Fragment.navigator get() = requireActivity() as Navigator

/**
 * BaseActivity implement this interface.
 */
interface Navigator {

    fun goBack()

    fun launchFragment(container: Int, fragment: Fragment, addToBackStack: Boolean = true)
}
