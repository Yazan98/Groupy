package hu.grouper.app.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import hu.grouper.app.R
import hu.grouper.app.adapter.MembersAdapter
import hu.grouper.app.data.models.Profile
import io.vortex.android.ui.fragment.VortexBaseFragment
import kotlinx.android.synthetic.main.fragment_create_group.*

/**
 * Created By : Yazan Tarifi
 * Date : 12/6/2019
 * Time : 1:55 PM
 */

class CreateNewGroupFragment : VortexBaseFragment() {

    private val membersAdapter: MembersAdapter by lazy {
        MembersAdapter()
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_create_group
    }

    @SuppressLint("WrongConstant")
    override fun initScreen(view: View) {

        AddUserButton?.apply {
            this.setOnClickListener {
                showUserDialog()
            }
        }

        SuccessButton?.apply {
            this.setOnClickListener {

            }
        }

        activity?.let {
            GroupMemebers?.apply {
                this.layoutManager = LinearLayoutManager(it, LinearLayoutManager.VERTICAL, false)
                this.adapter = membersAdapter
                (this.adapter as MembersAdapter).context = it
            }
        }

        FirebaseFirestore.getInstance().collection("users").get()
                .addOnCompleteListener {
                    it.result?.let {
                        for (doc in it.documents) {
                            FirebaseFirestore.getInstance().collection("users")
                                    .document(doc.id).get().addOnCompleteListener {
                                        it.result?.let {
                                            membersAdapter.add(Profile(
                                                    id = it.getString("id"),
                                                    name = it.getString("name"),
                                                    bio = it.getString("bio")
                                            ))
                                        }
                                    }
                        }
                    }
                }

    }

    @SuppressLint("WrongConstant")
    private fun showUserDialog() {
        activity?.let {
            val updateDialog = Dialog(it)
            updateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            updateDialog.setCancelable(true)
            updateDialog.setContentView(R.layout.dialog_select_users)
            val lWindowParams = WindowManager.LayoutParams()
            lWindowParams.copyFrom(updateDialog.window.attributes)
            lWindowParams.width = WindowManager.LayoutParams.FILL_PARENT; // this is where the magic happens
            lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            updateDialog.window?.let {
                it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                it.attributes = lWindowParams
            }

            updateDialog.findViewById<RecyclerView>(R.id.recyclerView)?.apply {
                this.layoutManager = LinearLayoutManager(it, LinearLayoutManager.VERTICAL, false)
                this.adapter = membersAdapter
                (this.adapter as MembersAdapter).context = it
            }

            updateDialog.findViewById<Button>(R.id.ApplyButton)?.let {
                it.setOnClickListener {
                    updateDialog.dismiss()
                }
            }

            updateDialog.show()
        }
    }
}