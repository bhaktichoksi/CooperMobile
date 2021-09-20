package app.usage.locationtask.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.usage.locationtask.Common.alertErrorAndValidationDialog
import app.usage.locationtask.Common.isCheckNetwork
import app.usage.locationtask.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sign_in_button = findViewById<SignInButton>(R.id.sign_in_button)

        sign_in_button.setOnClickListener {

            if (isCheckNetwork(this@MainActivity)) {
                signInGoogle()
            } else {
                alertErrorAndValidationDialog(
                    this@MainActivity,
                    resources.getString(R.string.no_internet)
                )
            }

        }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN_GOOGLE) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }else{

            // Google Sign In failed, update UI appropriately
            Log.e("TAG", "Login Unsuccessful. $data");
            Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();
        }

        // callbackManager!!.onActivityResult(requestCode, resultCode, data)
    }



    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val RC_SIGN_IN_GOOGLE = 1

    private fun signInGoogle() {
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        mGoogleSignInClient!!.signOut()
            .addOnCompleteListener(this) { task: Task<Void?>? ->
                val signInIntent = mGoogleSignInClient!!.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE)
            }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)!!
            nextGmailActivity(account)
            Log.e("GoogleLogin", account.displayName.toString())
        } catch (e: ApiException) {
            Log.e("GoogleLogin", "signInResult:failed code=" + e.statusCode)
        }
    }

    @SuppressLint("HardwareIds")
    private fun nextGmailActivity(profile: GoogleSignInAccount?) {
        if (profile != null) {
            val FirstName = profile.displayName
            var profileEmail = profile.email
            val profileId = profile.id
            var personPhotoUrl = ""

            if (profile.photoUrl != null) {
                personPhotoUrl = profile.photoUrl.toString()
            }

            if (profile.email == null) {
                profileEmail = ""
            }

            val i = Intent(this@MainActivity, MapActivity::class.java)
            i.putExtra("FirstName", FirstName)
            i.putExtra("profileEmail", profileEmail)
            i.putExtra("profileId", profileId)
            i.putExtra("personPhotoUrl", personPhotoUrl)

            startActivity(i)


        }
    }
    //Google Login Method end


}