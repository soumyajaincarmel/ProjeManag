package com.example.projemanag.activities

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projemanag.R
import com.example.projemanag.adapters.TaskListItemsAdapter
import com.example.projemanag.firebase.FirestoreClass
import com.example.projemanag.models.Board
import com.example.projemanag.models.Task
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

        val rvTaskList = findViewById<RecyclerView>(R.id.rv_task_list)
        hideProgressDialog()
        setUpActionBar(board.name)

        val addTaskList = Task(resources.getString(R.string.add_list))
        board.taskList.add(addTaskList)

        rvTaskList.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL, false)

        rvTaskList.setHasFixedSize(true)

        val adapter = TaskListItemsAdapter(this, board.taskList)
        rvTaskList.adapter = adapter


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