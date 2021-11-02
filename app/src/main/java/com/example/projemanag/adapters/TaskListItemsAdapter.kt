package com.example.projemanag.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projemanag.activities.TaskListActivity
import com.example.projemanag.databinding.ItemTaskBinding
import com.example.projemanag.models.Task
import java.util.*

open class TaskListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<Task>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mPositionDraggedFrom = -1
    private var mPositionDraggedTo = -1

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val binding = ItemTaskBinding.inflate(LayoutInflater.from(context), parent, false)
        // Here the layout params are converted dynamically according to the screen size as width is 70% and height is wrap_content.
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.7).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        // Here the dynamic margins are applied to the view.
        layoutParams.setMargins((15.toDp()).toPx(), 0, (40.toDp()).toPx(), 0)
        binding.root.layoutParams = layoutParams

        return MyViewHolder(binding)
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val model = list[position]

        (holder as MyViewHolder).binding.apply {

            if (position == list.size - 1) {
                tvAddTaskList.visibility =
                    View.VISIBLE
                llTaskItem.visibility = View.GONE
            } else {
                tvAddTaskList.visibility = View.GONE
                llTaskItem.visibility =
                    View.VISIBLE
            }



            tvTaskListTitle.text = model.title

            tvAddTaskList.setOnClickListener {

                tvAddTaskList.visibility = View.GONE
                cvAddTaskListName.visibility =
                    View.VISIBLE
            }


            ibCloseListName.setOnClickListener {
                tvAddTaskList.visibility =
                    View.VISIBLE
                cvAddTaskListName.visibility =
                    View.GONE
            }

            ibDoneListName.setOnClickListener {
                val listName =
                    etTaskListName.text.toString()

                if (listName.isNotEmpty()) {
                    // Here we check the context is an instance of the TaskListActivity.
                    if (context is TaskListActivity) {
                        context.createTaskList(listName)
                    }
                } else {
                    Toast.makeText(context, "Please Enter List Name.", Toast.LENGTH_SHORT).show()
                }
            }

            ibEditListName.setOnClickListener {
                etEditTaskListName
                    .setText(model.title)
                llTitleView.visibility =
                    View.GONE
                cvEditTaskListName.visibility =
                    View.VISIBLE
            }

            ibCloseEditableView
                .setOnClickListener {
                    llTitleView.visibility =
                        View.VISIBLE
                    cvEditTaskListName.visibility =
                        View.GONE
                }

            ibDoneEditListName
                .setOnClickListener {
                    val listName =
                        etEditTaskListName.text.toString()

                    if (listName.isNotEmpty()) {
                        // Here we check the context is an instance of the TaskListActivity.
                        if (context is TaskListActivity) {
                            model.title = listName
                            context.updateTaskList(position, model)
                        }
                    } else {
                        Toast.makeText(context, "Please Enter List Name.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }


            ibDeleteList.setOnClickListener {
                alertDialogForDeleteList(position, model.title)
            }

            tvAddCard.setOnClickListener {
                tvAddCard.visibility = View.GONE
                cvAddCard.visibility = View.VISIBLE
            }

            ibCloseCardName.setOnClickListener {
                tvAddCard.visibility = View.VISIBLE
                cvAddCard.visibility = View.GONE
            }



            ibDoneCardName.setOnClickListener {
                val cardName =
                    etCardName.text.toString()

                if (cardName.isNotEmpty()) {
                    // Here we check the context is an instance of the TaskListActivity.
                    if (context is TaskListActivity) {
                        context.addCardToTaskList(position, cardName)
                    }
                } else {
                    Toast.makeText(context, "Please Enter Card Name.", Toast.LENGTH_SHORT).show()
                }
            }

            rvCardList.layoutManager =
                LinearLayoutManager(context)
            rvCardList.setHasFixedSize(true)
            val adapter = CardListItemsAdapter(context, model.cards)
            rvCardList.adapter = adapter

            adapter.setOnClickListener(
                object : CardListItemsAdapter.OnClickListener {
                    override fun onClick(cardPosition: Int) {
                        if (context is TaskListActivity) {
                            context.cardDetails(position, cardPosition)
                        }
                    }
                }
            )

            val dividerItemDecoration =
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            rvCardList
                .addItemDecoration(dividerItemDecoration)

            val helper = ItemTouchHelper(
                object :
                    ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        dragged: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        val draggedPosition = dragged.adapterPosition
                        val targetPosition = target.adapterPosition

                        if (mPositionDraggedFrom == -1) {
                            mPositionDraggedFrom = draggedPosition
                        }
                        mPositionDraggedTo = targetPosition

                        Collections.swap(list[position].cards, draggedPosition, targetPosition)

                        adapter.notifyItemMoved(draggedPosition, targetPosition)

                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    }

                    override fun clearView(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder
                    ) {
                        super.clearView(recyclerView, viewHolder)

                        if (mPositionDraggedFrom != -1 && mPositionDraggedTo != -1 && mPositionDraggedFrom != mPositionDraggedTo) {
                            (context as TaskListActivity).updateCardsInTaskList(
                                position,
                                list[position].cards
                            )
                        }

                        mPositionDraggedFrom = -1
                        mPositionDraggedTo = -1

                    }


                }
            )

            helper.attachToRecyclerView(rvCardList)

        }
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A function to get density pixel from pixel
     */
    private fun Int.toDp(): Int =
        (this / Resources.getSystem().displayMetrics.density).toInt()

    /**
     * A function to get pixel from density pixel
     */
    private fun Int.toPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()


    /**
     * Method is used to show the Alert Dialog for deleting the task list.
     */
    private fun alertDialogForDeleteList(position: Int, title: String) {
        val builder = AlertDialog.Builder(context)
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage("Are you sure you want to delete $title.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            dialogInterface.dismiss() // Dialog will be dismissed

            if (context is TaskListActivity) {
                context.deleteTaskList(position)
            }
        }

        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class MyViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root)
}