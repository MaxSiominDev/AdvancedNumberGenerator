package dev.maxsiomin.advancednumbergenerator.fragments.resetpassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import dev.maxsiomin.advancednumbergenerator.R
import dev.maxsiomin.advancednumbergenerator.base.BaseFragment
import dev.maxsiomin.advancednumbergenerator.databinding.FragmentResetPasswordBinding
import dev.maxsiomin.advancednumbergenerator.util.*

@AndroidEntryPoint
class ResetPasswordFragment : BaseFragment(R.layout.fragment_reset_password, false) {

    override var _binding: ViewDataBinding? = null
    private val binding get() = _binding!! as FragmentResetPasswordBinding

    override val mViewModel by viewModels<ResetPasswordViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentResetPasswordBinding.inflate(layoutInflater, container, false)

        (requireActivity() as AppCompatActivity).supportActionBar!!.title = getString(R.string.reset_password)

        with (binding) {
            emailEditText.text = sharedData.getSharedString(SharedDataKeys.EMAIL).notNull().toEditable()

            resetPasswordButton.setOnClickListener {
                val email = Email(emailEditText.text)

                if (!email.isCorrect) {
                    emailEditTextLayout.error = getString(R.string.invalid_email)
                    emailEditText.requestFocus()
                } else {
                    mViewModel.sendPasswordResetEmail(email, FirebaseAuth.getInstance())
                }
            }

            emailEditText.addTextChangedListener {
                emailEditTextLayout.clearError()
            }
        }

        return binding.root
    }

    override fun onStop() {
        sharedData.putSharedString(SharedDataKeys.EMAIL, binding.emailEditText.text?.toString())
        super.onStop()
    }

    companion object {

        @JvmStatic
        fun newInstance() = ResetPasswordFragment()
    }
}
