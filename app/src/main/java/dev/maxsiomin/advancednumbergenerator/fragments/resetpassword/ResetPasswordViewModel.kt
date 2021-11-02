package dev.maxsiomin.advancednumbergenerator.fragments.resetpassword

import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.maxsiomin.advancednumbergenerator.R
import dev.maxsiomin.advancednumbergenerator.base.BaseViewModel
import dev.maxsiomin.advancednumbergenerator.util.Email
import dev.maxsiomin.advancednumbergenerator.util.UiActions
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(uiActions: UiActions) : BaseViewModel(uiActions) {

    fun sendPasswordResetEmail(email: Email, auth: FirebaseAuth) {
        auth.sendPasswordResetEmail(email.value).addOnCompleteListener { task ->
            toast(
                if (task.isSuccessful) R.string.check_email else R.string.unable_to_send_reset_email,
                Toast.LENGTH_LONG,
            )
        }
    }
}
