package com.example.projemanag.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.projemanag.R
import com.example.projemanag.models.Board
import com.example.projemanag.utils.Constants

class CardDetailsActivity : AppCompatActivity() {

    private lateinit var mBoardDetails: Board

    private var mTaskListPosition = -1
    private var mCardListPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)


        getIntentData()
        setUpActionBar()

    }


    private fun setUpActionBar() {
        val toolbarCardDetailsActivity = findViewById<Toolbar>(R.id.toolbar_card_details_activity)
        setSupportActionBar(findViewById(R.id.toolbar_card_details_activity))

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title =
                mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].name
        }

        toolbarCardDetailsActivity.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    private fun getIntentData() {
        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }
        if (intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)) {
            mCardListPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION, -1)
        }
        if (intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)) {
            mTaskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION, -1)
        }
    }
}