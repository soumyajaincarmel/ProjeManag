package com.example.projemanag.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.projemanag.R
import com.example.projemanag.firebase.FirestoreClass
import com.example.projemanag.models.User
import com.example.projemanag.utils.Constants
import com.example.projemanag.utils.Constants.PICK_IMAGE_REQUEST_CODE
import com.example.projemanag.utils.Constants.READ_STORAGE_PERMISSION_CODE
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class MyProfileActivity : BaseActivity() {


    private var mSelectedImageFileUri: Uri? = null
    private var mProfileImageURL: String = ""

    private lateinit var mUserDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        setUpActionBar()

        FirestoreClass().loadUserData(this)

        val ivUserProfileImage: ImageView = findViewById(R.id.iv_user_profile_image)

        ivUserProfileImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Constants.showImageChooser(this@MyProfileActivity)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        val btnUpdate = findViewById<Button>(R.id.btn_update)

        btnUpdate.setOnClickListener {
            if (mSelectedImageFileUri != null) {
                uploadUserImage()
            } else {
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserProfileData()
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@MyProfileActivity)
            } else {
                Toast.makeText(
                    this,
                    "Oops! You just denied permission for storage. You can also allow it from settings",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST_CODE && data!!.data != null) {
            mSelectedImageFileUri = data.data

            try {
                Glide.with(this@MyProfileActivity).load(mSelectedImageFileUri).centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(findViewById(R.id.iv_user_profile_image))
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }


    private fun setUpActionBar() {
        val toolbarMyProfileActivity = findViewById<Toolbar>(R.id.toolbar_my_profile_activity)
        setSupportActionBar(findViewById(R.id.toolbar_my_profile_activity))

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.my_profile_title)

        }

        toolbarMyProfileActivity.setNavigationOnClickListener {
            onBackPressed()
        }


    }


    fun setUserDataInUi(user: User) {

        mUserDetails = user

        Glide.with(this@MyProfileActivity).load(user.image).centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(findViewById(R.id.iv_user_profile_image))


        findViewById<EditText>(R.id.et_name).setText(user.name)
        findViewById<EditText>(R.id.et_email).setText(user.email)

        if (user.mobile != 0L) {
            findViewById<EditText>(R.id.et_mobile).setText(user.mobile.toString())

        }

    }


    private fun uploadUserImage() {
        showProgressDialog(resources.getString(R.string.please_wait))

        if (mSelectedImageFileUri != null) {

            //getting the storage reference
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "USER_IMAGE" + System.currentTimeMillis() + "." + Constants.getFileExtension(
                    this@MyProfileActivity, mSelectedImageFileUri
                )
            )

            //adding the file to reference
            sRef.putFile(mSelectedImageFileUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    // The image upload is success
                    Log.e(
                        "Firebase Image URL",
                        taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                    )

                    // Get the downloadable url from the task snapshot
                    taskSnapshot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            Log.e("Downloadable Image URL", uri.toString())

                            // assign the image url to the variable.
                            mProfileImageURL = uri.toString()

                            // Call a function to update user details in the database.
                            updateUserProfileData()
                        }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        this@MyProfileActivity,
                        exception.message,
                        Toast.LENGTH_LONG
                    ).show()

                    hideProgressDialog()
                }
        }
    }

    private fun updateUserProfileData() {
        val userHashMap = HashMap<String, Any>()
        var anyChangesMde = false

        val etName = findViewById<EditText>(R.id.et_name)
        val etMobile = findViewById<EditText>(R.id.et_mobile)



        if (mProfileImageURL.isNotEmpty() && mProfileImageURL != mUserDetails.image) {
            userHashMap[Constants.IMAGE] = mProfileImageURL
            anyChangesMde = true
        }

        if (etName.text.toString() != mUserDetails.name) {
            userHashMap[Constants.NAME] = etName.text.toString()
            anyChangesMde = true

        }

        if (etMobile.text.toString() != mUserDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = etMobile.text.toString().toLong()
            anyChangesMde = true

        }

        if (anyChangesMde) {
            FirestoreClass().updateUserProfileData(this, userHashMap)
        } else {
            hideProgressDialog()
            Toast.makeText(
                this@MyProfileActivity,
                "You haven't made any changes!",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    fun profileUpdateSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
}