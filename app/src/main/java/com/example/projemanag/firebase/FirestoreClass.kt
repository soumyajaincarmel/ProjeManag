package com.example.projemanag.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.projemanag.activities.*
import com.example.projemanag.models.Board
import com.example.projemanag.models.User
import com.example.projemanag.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    /**
     * A function to make an entry of the registered user in the firestore database.
     */

    fun registerUser(activity: SignUpActivity, userInfo: User) {

        mFireStore.collection(Constants.USERS)
            // Document ID for users fields. Here the document it is the User ID.
            .document(getCurrentUserID())
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.userRegisteredSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(
                    activity.javaClass.simpleName,
                    "Error writing document",
                    e
                )
            }
    }

    fun createBoard(activity: CreateBoardActivity, board : Board)
    {
        mFireStore.collection(Constants.BOARDS)
            .document().set(board, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Board Created Successfully")
                Toast.makeText(activity, "Board Created Successfully!", Toast.LENGTH_SHORT).show()
                activity.boardCreatedSuccessfully()
            }.addOnFailureListener {
                exception ->
                    activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error While Creating a Board", exception)
            }

    }

    /**
     * A function to SignIn using firebase and get the user details from Firestore Database.
     */
    fun loadUserData(activity: Activity, readBoardsList: Boolean = false) {

        // Here we pass the collection name from which we wants the data.
        mFireStore.collection(Constants.USERS)
            // The document id to get the Fields of user.
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                // Here we have received the document snapshot which is converted into the User Data model object.
                val loggedInUser = document.toObject(User::class.java)!!

                when(activity)
                {
                    is SignInActivity -> {
                        activity.signInSuccess(loggedInUser)
                    }

                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser, readBoardsList)
                    }

                    is MyProfileActivity -> {

                        activity.setUserDataInUi(loggedInUser)

                    }
                }

            }
            .addOnFailureListener {
                    e ->

                when(activity)
                {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }

                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error while getting loggedIn user details", e)
            }
    }

    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    fun updateUserProfileData(activity: MyProfileActivity, userHashMap : HashMap<String, Any>)
    {
        mFireStore.collection(Constants.USERS).
        document(getCurrentUserID()).update(userHashMap).addOnSuccessListener {
            Log.i(activity.javaClass.simpleName, "Profile Data Updated Successfully!")
            Toast.makeText(activity, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
            activity.profileUpdateSuccess()
        }.addOnFailureListener{
            e -> activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName, "Error while creating a board", e)
            Toast.makeText(activity, "Error when updating the profile!", Toast.LENGTH_SHORT).show()
        }
    }

    fun getBoardsList(activity: MainActivity)
    {
        mFireStore.collection(Constants.BOARDS).whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserID())
            .get()
            .addOnSuccessListener {
                document -> Log.i(activity.javaClass.simpleName, document.documents.toString())
                val boardsList : ArrayList<Board> = ArrayList()
                for(i in document.documents)
                {
                    val board = i.toObject(Board::class.java)
                    board!!.documentId = i.id
                    boardsList.add(board)
                }

                activity.populateBoardsListToUi(boardsList)
            }.addOnFailureListener {
                e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a Board!")
            }
    }

    fun getBoardDetails(activity: TaskListActivity, documentId : String)
    {
        mFireStore.collection(Constants.BOARDS).document(documentId)
            .get()
            .addOnSuccessListener {
                    document -> Log.i(activity.javaClass.simpleName, document.toString())

                activity.boardDetails(document.toObject(Board::class.java)!!)
            }.addOnFailureListener {
                    e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a Board!")
            }
    }
}