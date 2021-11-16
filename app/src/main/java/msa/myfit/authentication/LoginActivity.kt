package msa.myfit.authentication

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import msa.myfit.MainActivity
import msa.myfit.R
import msa.myfit.fragment.TAG
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleClientId: String

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    override fun onStart() {
        super.onStart()
        val intentMain = Intent(this@LoginActivity, MainActivity::class.java)

        if(FirebaseAuth.getInstance().currentUser != null){
            startActivity(intentMain)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        googleClientId = resources.getString(R.string.google_client_ID)
        auth = FirebaseAuth.getInstance()

        val intentMain = Intent(this@LoginActivity, MainActivity::class.java)
        intentMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val textRegister: TextView = findViewById(R.id.tv_register)
        val btnLogin: Button = findViewById(R.id.btn_login)
        val textEmail: EditText = findViewById(R.id.editTextTextEmailAddress)
        val textPassword: EditText = findViewById(R.id.editTextTextPassword)
        val btnLoginWithGoogle: ImageButton = findViewById(R.id.button_login_google)

        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                    .setSupported(true)
                    .build()
            )
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(googleClientId)
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            .build()

        textRegister.setOnClickListener{
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        btnLogin.setOnClickListener {
            val email: String = textEmail.text.toString().trim { it <= ' '}
            val password: String = textPassword.text.toString().trim { it <= ' '}
            when{
                TextUtils.isEmpty(email) -> {
                    Toast.makeText(
                        this@LoginActivity,
                        "Please enter email",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(password) -> {
                    Toast.makeText(
                        this@LoginActivity,
                        "Please enter password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->

                                if(task.isSuccessful) {

                                    Toast.makeText(
                                        this@LoginActivity,
                                        "You were logged in successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    intentMain.putExtra(
                                        "user_id",
                                        FirebaseAuth.getInstance().currentUser!!.uid
                                    )
                                    intentMain.putExtra("email_id", email)
                                    startActivity(intentMain)
                                    finish()
                                }
                                else{
                                    Toast.makeText(
                                        this@LoginActivity,
                                        task.exception!!.message.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            }
                }
            }
        }

        btnLoginWithGoogle.setOnClickListener{
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this) { result ->
                    try {
                        startIntentSenderForResult(
                            result.pendingIntent.intentSender, 2,
                            null, 0, 0, 0, null
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                    }
                }
                .addOnFailureListener(this) { e ->
                    Log.d(TAG, "failed to log in through oneTap")
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            2 -> {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    when {
                        idToken != null -> {
                            Log.d(TAG, "Got ID token.")
                        }
                        else -> {
                            Log.d(TAG, "No ID token!")
                        }
                    }
                    val googleCredential = GoogleAuthProvider.getCredential(idToken, null)
                    auth!!.signInWithCredential(googleCredential)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "signInWithCredential:success")
                            } else {
                                Log.w(TAG, "signInWithCredential:failure", task.exception)
                            }
                        }
                } catch (e: ApiException) {
                    when (e.statusCode) {
                        CommonStatusCodes.CANCELED -> {
                            Log.d(TAG, "One-tap dialog was closed.")
                        }
                        CommonStatusCodes.NETWORK_ERROR -> {
                            Log.d(TAG, "One-tap encountered a network error.")
                        }
                        else -> {
                            Log.d(
                                TAG, "Couldn't get credential from result." +
                                        " (${e.localizedMessage})"
                            )
                        }
                    }
                }
            }
        }

        val intentParent = intent
        setResult(RESULT_OK, intentParent);

        if (resultCode === RESULT_OK) {
            val intentMain = Intent(this@LoginActivity, MainActivity::class.java)
            try {
                startActivity(intentMain)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "can't find intent", Toast.LENGTH_LONG).show()
            }

            finish()
        }
    }
}