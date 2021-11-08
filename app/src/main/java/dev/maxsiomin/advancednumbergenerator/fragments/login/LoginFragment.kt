package dev.maxsiomin.advancednumbergenerator.fragments.login

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
import dev.maxsiomin.advancednumbergenerator.activities.login.LoginActivity
import dev.maxsiomin.advancednumbergenerator.base.BaseFragment
import dev.maxsiomin.advancednumbergenerator.databinding.FragmentLoginBinding
import dev.maxsiomin.advancednumbergenerator.fragments.contract.navigator
import dev.maxsiomin.advancednumbergenerator.fragments.resetpassword.ResetPasswordFragment
import dev.maxsiomin.advancednumbergenerator.fragments.signup.SignupFragment
import dev.maxsiomin.advancednumbergenerator.util.*
import dev.maxsiomin.advancednumbergenerator.util.SharedDataKeys.EMAIL
import dev.maxsiomin.advancednumbergenerator.util.SharedDataKeys.PASSWORD
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment(R.layout.fragment_login) {

    override var _binding: ViewDataBinding? = null
    private val binding get() = _binding!! as FragmentLoginBinding

    override val mViewModel by viewModels<LoginViewModel>()

    @Inject
    lateinit var auth: FirebaseAuth

    val loginActivity get() = requireActivity() as LoginActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.d("onCreate called")

        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)

        (requireActivity() as AppCompatActivity).supportActionBar!!.title = getString(R.string.log_in)

        with (binding) {
            emailEditText.text = sharedData.getSharedString(EMAIL).notNull().toEditable()
            passwordEditText.text = sharedData.getSharedString(PASSWORD).notNull().toEditable()

            loginButton.setOnClickListener {
                val email = Email(emailEditText.text)
                val password = Password(passwordEditText.text)

                when {
                    !email.isCorrect -> {
                        emailEditTextLayout.error = getString(R.string.invalid_email)
                        emailEditText.requestFocus()
                    }

                    password.isEmpty -> {
                        passwordEditTextLayout.error = getString(R.string.password_not_provided)
                        passwordEditText.requestFocus()
                    }

                    else -> {
                        mViewModel.login(email, password, auth) {
                            onLogin()
                        }
                    }
                }
            }

            emailEditText.addTextChangedListener { emailEditTextLayout.clearError() }
            passwordEditText.addTextChangedListener { passwordEditTextLayout.clearError() }

            forgotPasswordTextView.setOnClickListener {
                navigator.launchFragment(R.id.login_activity_fragment_container, ResetPasswordFragment.newInstance())
            }

            signupTextView.setOnClickListener {
                // Do not add to backstack!
                navigator.launchFragment(
                    R.id.login_activity_fragment_container,
                    SignupFragment.newInstance(),
                    addToBackStack = false
                )
            }
        }

        return binding.root
    }

    override fun onStop() {
        sharedData.putSharedString(EMAIL, binding.emailEditText.text?.toString())
        sharedData.putSharedString(PASSWORD, binding.passwordEditText.text?.toString())
        super.onStop()
    }

    private fun onLogin() {
        loginActivity.onLogin()
    }

    companion object {

        @JvmStatic
        fun newInstance() = LoginFragment()
    }
}
