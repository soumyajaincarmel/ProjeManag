package com.example.projemanag.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.projemanag.R
import com.example.projemanag.firebase.FirestoreClass
import com.example.projemanag.models.User
import com.example.projemanag.utils.Constants
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {


    companion object{
        const val MY_PROFILE_REQUEST_CODE : Int = 11
    }

    private lateinit var mUserName : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpActionBar()

        val navView = findViewById<NavigationView>(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)

        FirestoreClass().loadUserData(this)

        val fabCreateBoard = findViewById<FloatingActionButton>(R.id.fab_create_board)
        fabCreateBoard.setOnClickListener {
            val intent = Intent(this, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            startActivity(intent)
        }

    }

    private fun setUpActionBar() {
        val toolbarMainActivity = findViewById<Toolbar>(R.id.toolbar_main_activity)
        setSupportActionBar(findViewById(R.id.toolbar_main_activity))
        toolbarMainActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        toolbarMainActivity.setNavigationOnClickListener {
            // Toggle Drawer
            toggleDrawer()
        }

    }

    private fun toggleDrawer() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            doubleBackToExit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE)
        {
            FirestoreClass().loadUserData(this)
        }
        else
        {
            Log.e("Cancelled", "Cancelled")
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        when(item.itemId)
        {
            R.id.nav_my_profile -> {
                startActivityForResult(Intent(this, MyProfileActivity::class.java), MY_PROFILE_REQUEST_CODE)
            }

            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun updateNavigationUserDetails(user: User) {

        mUserName = user.name

        Glide
            .with(this@MainActivity)
            .load(user.image) // URL of the image
            .centerCrop() // Scale type of the image.
            .placeholder(R.drawable.ic_user_place_holder) // A default place holder
            .into(findViewById(R.id.nav_user_image)) // the view in which the image will be loaded.


        findViewById<TextView>(R.id.tv_username).text = user.name



    }

}