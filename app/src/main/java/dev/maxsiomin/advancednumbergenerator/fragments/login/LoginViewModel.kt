package dev.maxsiomin.advancednumbergenerator.fragments.login

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
class LoginViewModel @Inject constructor(uiActions: UiActions) : BaseViewModel(uiActions) {

    /**
     * Login via [FirebaseAuth]
     */
    fun login(email: Email, password: Password, auth: FirebaseAuth, onLogin: () -> Unit) {
        auth.signInWithEmailAndPassword(email.value, password.value).addOnCompleteListener { task ->
            if (task.isSuccessful)
                onLogin()
            else
                toast(R.string.unable_to_login, Toast.LENGTH_LONG)
        }
    }
}
