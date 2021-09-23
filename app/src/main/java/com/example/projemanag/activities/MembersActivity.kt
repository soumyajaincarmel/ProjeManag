package com.example.projemanag.activities

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projemanag.R
import com.example.projemanag.adapters.MemberListItemsAdapter
import com.example.projemanag.firebase.FirestoreClass
import com.example.projemanag.models.Board
import com.example.projemanag.models.User
import com.example.projemanag.utils.Constants

class MembersActivity : BaseActivity() {

    private lateinit var mBoardDetails: Board
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)

        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!

            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getAssignedMembersListDetails(this, mBoardDetails.assignedTo)
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

    fun setUpMembersList(list: ArrayList<User>) {
        hideProgressDialog()

        val rv_member_list = findViewById<RecyclerView>(R.id.rv_members_list)
        rv_member_list.layoutManager = LinearLayoutManager(this)
        rv_member_list.setHasFixedSize(true)

        val adapter = MemberListItemsAdapter(this, list)
        rv_member_list.adapter = adapter

    }
}