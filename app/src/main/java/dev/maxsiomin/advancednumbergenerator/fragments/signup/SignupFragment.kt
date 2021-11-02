package dev.maxsiomin.advancednumbergenerator.fragments.signup

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
import dev.maxsiomin.advancednumbergenerator.databinding.FragmentSignupBinding
import dev.maxsiomin.advancednumbergenerator.fragments.login.LoginFragment
import dev.maxsiomin.advancednumbergenerator.util.*
import dev.maxsiomin.advancednumbergenerator.util.SharedDataKeys.EMAIL
import dev.maxsiomin.advancednumbergenerator.util.SharedDataKeys.PASSWORD
import javax.inject.Inject

@AndroidEntryPoint
class SignupFragment : BaseFragment(R.layout.fragment_signup, false) {

    override var _binding: ViewDataBinding? = null
    private val binding get() = _binding!! as FragmentSignupBinding

    override val mViewModel by viewModels<SignupViewModel>()

    private val loginActivity get() = requireActivity() as LoginActivity

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSignupBinding.inflate(layoutInflater, container, false)

        (requireActivity() as AppCompatActivity).supportActionBar!!.title = getString(R.string.sign_up)

        with (binding) {
            emailEditText.text = sharedData.getSharedString(EMAIL).notNull().toEditable()
            passwordEditText.text = sharedData.getSharedString(PASSWORD).notNull().toEditable()

            loginTextView.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.login_activity_fragment_container, LoginFragment.newInstance())
                    .commit()
            }

            signupButton.setOnClickListener {
                val email = Email(emailEditText.text)
                val password = Password(passwordEditText.text)
                val confirmedPassword = Password(confirmPasswordEditText.text)

                when {
                    !email.isCorrect -> {
                        emailEditTextLayout.error = getString(R.string.invalid_email)
                        emailEditText.requestFocus()
                    }

                    !password.isStrong -> {
                        passwordEditTextLayout.error = getString(R.string.weak_password, PASSWORD_MIN_LENGTH)
                        passwordEditText.requestFocus()
                    }

                    confirmedPassword != password -> {
                        confirmPasswordEditTextLayout.error = getString(R.string.passwords_not_match)
                    }

                    else -> {
                        mViewModel.signup(email, password, auth) {
                            auth.signInWithEmailAndPassword(email.value, password.value).addOnCompleteListener {
                                loginActivity.onSignup()
                            }
                        }
                    }
                }
            }

            emailEditText.addTextChangedListener {
                emailEditTextLayout.clearError()
            }

            passwordEditText.addTextChangedListener {
                passwordEditTextLayout.clearError()
            }

            confirmPasswordEditText.addTextChangedListener {
                confirmPasswordEditTextLayout.clearError()
            }
        }

        return binding.root
    }

    override fun onStop() {
        sharedData.putSharedString(EMAIL, binding.emailEditText.text?.toString())
        sharedData.putSharedString(PASSWORD, binding.passwordEditText.text?.toString())
        super.onStop()
    }

    companion object {

        @JvmStatic
        fun newInstance() = SignupFragment()
    }
}
