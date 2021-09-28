package com.example.projemanag.activities

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projemanag.R
import com.example.projemanag.adapters.CardMemberListItemsAdapter
import com.example.projemanag.dialogs.LabelColorListDialog
import com.example.projemanag.firebase.FirestoreClass
import com.example.projemanag.models.*
import com.example.projemanag.utils.Constants
import com.projemanag.dialogs.MembersListDialog

class CardDetailsActivity : BaseActivity() {

    private lateinit var mBoardDetails: Board

    private var mTaskListPosition = -1
    private var mCardListPosition = -1

    private var mSelectedColor = ""

    private lateinit var mMembersDetailsList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)


        getIntentData()
        setUpActionBar()

        val et_name_card_details = findViewById<EditText>(R.id.et_name_card_details)
        et_name_card_details.setText(mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].name)
        et_name_card_details.setSelection(et_name_card_details.text.toString().length) // The cursor after the string length

        val btn_update_card_details = findViewById<Button>(R.id.btn_update_card_details)
        btn_update_card_details.setOnClickListener {
            if (et_name_card_details.text.toString().isNotEmpty()) {
                updateCardDetails()
            } else {
                Toast.makeText(this, "Please Enter a Card Name", Toast.LENGTH_SHORT).show()
            }
        }

        val tv_select_label_color = findViewById<TextView>(R.id.tv_select_label_color)
        tv_select_label_color.setOnClickListener {
            showLabelColorsListDialog()
        }

        mSelectedColor =
            mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].labelColor
        if (mSelectedColor.isNotEmpty()) {
            setColor()
        }

        val tv_select_members = findViewById<TextView>(R.id.tv_select_members)
        tv_select_members.setOnClickListener {
            membersListDialog()
        }

        setUpSelectedMembersList()


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

    private fun alertDialogForDeleteCard(cardName: String) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.alert))
        //set message for alert dialog
        builder.setMessage(
            resources.getString(
                R.string.confirmation_message_to_delete_card,
                cardName
            )
        )
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
            deleteCard()

        }
        //performing negative action
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete_card -> {
                alertDialogForDeleteCard(mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].name)

                return true
            }
        }
        return super.onOptionsItemSelected(item)
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
        if (intent.hasExtra(Constants.BOARD_MEMBERS_LIST)) {
            mMembersDetailsList = intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST)!!
        }
    }

    fun addUpdateTaskListSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun membersListDialog() {

        // Here we get the updated assigned members list
        val cardAssignedMembersList =
            mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].assignedTo

        if (cardAssignedMembersList.size > 0) {
            // Here we got the details of assigned members list from the global members list which is passed from the Task List screen.
            for (i in mMembersDetailsList.indices) {
                for (j in cardAssignedMembersList) {
                    if (mMembersDetailsList[i].id == j) {
                        mMembersDetailsList[i].selected = true
                    }
                }
            }
        } else {
            for (i in mMembersDetailsList.indices) {
                mMembersDetailsList[i].selected = false
            }
        }

        val listDialog = object : MembersListDialog(
            this@CardDetailsActivity,
            mMembersDetailsList,
            resources.getString(R.string.str_select_member)
        ) {
            override fun onItemSelected(user: User, action: String) {
                if (action == Constants.SELECT) {
                    if (!mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].assignedTo.contains(
                            user.id
                        )
                    ) {
                        mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].assignedTo.add(
                            user.id
                        )
                    }
                } else {
                    mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].assignedTo.remove(
                        user.id
                    )

                    for (i in mMembersDetailsList.indices) {
                        if (mMembersDetailsList[i].id == user.id) {
                            mMembersDetailsList[i].selected = false
                        }
                    }
                }

                setUpSelectedMembersList()
            }
        }
        listDialog.show()
    }


    private fun setUpSelectedMembersList() {
        val cardAssignedMembersList =
            mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].assignedTo

        val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()

        for (i in mMembersDetailsList.indices) {
            for (j in cardAssignedMembersList) {
                if (mMembersDetailsList[i].id == j) {
                    val selectedMember = SelectedMembers(
                        mMembersDetailsList[i].id,
                        mMembersDetailsList[i].image
                    )
                    selectedMembersList.add(selectedMember)
                }
            }
        }

        if (selectedMembersList.size > 0) {
            selectedMembersList.add(SelectedMembers("", ""))
            val tv_select_members = findViewById<TextView>(R.id.tv_select_members)
            tv_select_members.visibility = View.GONE
            val rv_selected_members_list = findViewById<RecyclerView>(R.id.rv_selected_members_list)
            rv_selected_members_list.visibility = View.VISIBLE

            rv_selected_members_list.layoutManager = GridLayoutManager(this, 6)
            val adapter = CardMemberListItemsAdapter(this, selectedMembersList)
            rv_selected_members_list.adapter = adapter
            adapter.setOnClickListener(
                object : CardMemberListItemsAdapter.OnClickListener {
                    override fun onClick() {
                        membersListDialog()
                    }

                }
            )
        } else {
            val tv_select_members = findViewById<TextView>(R.id.tv_select_members)
            tv_select_members.visibility = View.VISIBLE

            val rv_selected_members_list = findViewById<RecyclerView>(R.id.rv_selected_members_list)
            rv_selected_members_list.visibility = View.GONE
        }
    }

    private fun updateCardDetails() {
        val etNameCardDetails = findViewById<EditText>(R.id.et_name_card_details)
        val card = Card(
            etNameCardDetails.text.toString(),
            mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].createdBy,
            mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition].assignedTo,
            mSelectedColor
        )

        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size - 1)

        mBoardDetails.taskList[mTaskListPosition].cards[mCardListPosition] = card

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity, mBoardDetails)
    }

    private fun deleteCard() {
        val cardsList: ArrayList<Card> = mBoardDetails.taskList[mTaskListPosition].cards

        cardsList.removeAt(mCardListPosition)

        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size - 1)

        taskList[mTaskListPosition].cards = cardsList

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity, mBoardDetails)
    }

    private fun colorsList(): ArrayList<String> {
        val colorsList: ArrayList<String> = ArrayList()

        colorsList.add("#43C86F")
        colorsList.add("#0C90F1")
        colorsList.add("#F72400")
        colorsList.add("#7A8089")
        colorsList.add("#D57C1D")
        colorsList.add("#770000")
        colorsList.add("#0022F8")

        return colorsList
    }

    private fun setColor() {
        findViewById<TextView>(R.id.tv_select_label_color).text = ""
        findViewById<TextView>(R.id.tv_select_label_color).setBackgroundColor(
            Color.parseColor(
                mSelectedColor
            )
        )

    }

    private fun showLabelColorsListDialog() {

        val colorsList: ArrayList<String> = colorsList()

        val listDialog = object : LabelColorListDialog(
            this@CardDetailsActivity,
            colorsList,
            resources.getString(R.string.str_select_label_color),
            mSelectedColor
        ) {
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setColor()
            }
        }
        listDialog.show()
    }
}