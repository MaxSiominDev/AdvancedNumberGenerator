package dev.maxsiomin.advancednumbergenerator.fragments.signup

import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.maxsiomin.advancednumbergenerator.R
import dev.maxsiomin.advancednumbergenerator.base.BaseViewModel
import dev.maxsiomin.advancednumbergenerator.util.Email
import dev.maxsiomin.advancednumbergenerator.util.Password
import dev.maxsiomin.advancednumbergenerator.util.UiActions
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(uiActions: UiActions) : BaseViewModel(uiActions) {

    fun signup(email: Email, password: Password, auth: FirebaseAuth, onSignup: () -> Unit) {
        auth.createUserWithEmailAndPassword(email.value, password.value).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSignup()
            } else {
                toast(R.string.unable_to_signup, Toast.LENGTH_LONG)
            }
        }
    }
}
