package com.example.projemanag.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.projemanag.R
import com.example.projemanag.models.Board
import com.example.projemanag.utils.Constants

class MembersActivity : AppCompatActivity() {

    private lateinit var mBoardDetails: Board
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)

        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
        }

        setUpActionBar()
    }


    private fun setUpActionBar() {
        val toolbarMembersActivity = findViewById<Toolbar>(R.id.toolbar_members_activity)
        setSupportActionBar(findViewById(R.id.toolbar_members_activity))

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.members)

        }

        toolbarMembersActivity.setNavigationOnClickListener {
            onBackPressed()
        }


    }
}