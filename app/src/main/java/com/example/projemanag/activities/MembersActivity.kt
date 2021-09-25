package com.example.projemanag.activities

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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
    private lateinit var mAssignedMembersList:ArrayList<User>

    private var anyChangesMade : Boolean = false

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
        mAssignedMembersList = list
        hideProgressDialog()

        val rv_member_list = findViewById<RecyclerView>(R.id.rv_members_list)
        rv_member_list.layoutManager = LinearLayoutManager(this)
        rv_member_list.setHasFixedSize(true)

        val adapter = MemberListItemsAdapter(this, list)
        rv_member_list.adapter = adapter

    }

    fun memberDetails(user : User)
    {
        mBoardDetails.assignedTo.add(user.id)
        FirestoreClass().assignMemberToBoard(this, mBoardDetails, user)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.action_add_member -> {
                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun dialogSearchMember()
    {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_search_member)
        dialog.findViewById<TextView>(R.id.tv_add).setOnClickListener {
            val email = dialog.findViewById<EditText>(R.id.et_email_search_member).text.toString()
            if(email.isNotEmpty())
            {
                dialog.dismiss()
                showProgressDialog(resources.getString(R.string.please_wait))
                FirestoreClass().getMemberDetails(this, email)
            }
            else
            {
                Toast.makeText(this@MembersActivity, "Please Enter an Email Address!!", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }

    override fun onBackPressed() {
        if(anyChangesMade)
        {
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }


    fun memberAssignSuccess(user : User)
    {
        hideProgressDialog()
        mAssignedMembersList.add(user)

        anyChangesMade = true

        setUpMembersList(mAssignedMembersList)
    }
}