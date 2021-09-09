package com.example.projemanag.activities

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.projemanag.R
import com.example.projemanag.firebase.FirestoreClass
import com.example.projemanag.models.Board
import com.example.projemanag.utils.Constants

class TaskListActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        var boardDocumentId = ""
        if(intent.hasExtra(Constants.DOCUMENT_ID))
        {
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }

        showProgressDialog(getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this, boardDocumentId)
    }

    fun boardDetails(board : Board)
    {
        hideProgressDialog()
        setUpActionBar(board.name)
    }

    private fun setUpActionBar(title : String) {
        val toolbarTaskListActivity = findViewById<Toolbar>(R.id.toolbar_task_list_activity)
        setSupportActionBar(findViewById(R.id.toolbar_task_list_activity))

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = title

        }

        toolbarTaskListActivity.setNavigationOnClickListener {
            onBackPressed()
        }


    }
}