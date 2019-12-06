package hu.grouper.app.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
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
import hu.grouper.app.data.models.Group
import hu.grouper.app.data.models.Profile
import hu.grouper.app.screens.MainScreen
import io.vortex.android.prefs.VortexPrefs
import io.vortex.android.ui.fragment.VortexBaseFragment
import kotlinx.android.synthetic.main.fragment_create_group.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Created By : Yazan Tarifi
 * Date : 12/6/2019
 * Time : 1:55 PM
 */

class CreateNewGroupFragment : VortexBaseFragment() {

    private val membersAdapter: MembersAdapter by lazy {
        MembersAdapter()
    }

    private val membersAdapter2: MembersAdapter by lazy {
        MembersAdapter()
    }

    private val loader: ProgressDialog? by lazy {
        activity?.let {
            ProgressDialog(it)
        }
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
                loader?.let {
                    it.setMessage("Loading ...")
                    GlobalScope.launch {
                        createGroup()
                    }
                }
            }
        }

        GlobalScope.launch {
            VortexPrefs.get("UserID", "")?.let {
                FirebaseFirestore.getInstance().collection("users")
                        .document(it as String).get().addOnCompleteListener {
                            it.result?.let {
                                membersAdapter2.add(Profile(
                                        id = it.getString("id"),
                                        bio = it.getString("bio"),
                                        name = it.getString("name")
                                ))
                            }
                        }
            }
        }

        activity?.let {
            GroupMemebers?.apply {
                this.layoutManager = LinearLayoutManager(it, LinearLayoutManager.VERTICAL, false)
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

    private suspend fun createGroup() {
        withContext(Dispatchers.IO) {
            val ids = ArrayList<String>()
            membersAdapter2.items.forEach {
                it.id?.let {
                    ids.add(it)
                }
            }
            val id = UUID.randomUUID().toString()
            val adminId = VortexPrefs.get("UserID" , "") as String
            FirebaseFirestore.getInstance().collection("groups").document(id)
                    .set(Group(
                            name = GroupName?.text.toString(),
                            adminID = adminId,
                            id = id,
                            members = ids
                    )).addOnCompleteListener {
                        val items = HashMap<String , Any>()
                        items["groupID"] = id
                        FirebaseFirestore.getInstance().collection("users")
                                .document(adminId).update(items).addOnCompleteListener {
                                    GlobalScope.launch {
                                        val result = HashMap<String , Any>()
                                        result["groupID"] = id
                                        for(spec in ids) {
                                            FirebaseFirestore.getInstance().collection("users")
                                                    .document(spec).update(result)
                                        }
                                        VortexPrefs.put("GroupID" , id)
                                        hideLoading()
                                        start()
                                    }
                                }
                    }
        }
    }

    private suspend fun start() {
        withContext(Dispatchers.Main) {
            VortexPrefs.put("UserStatus" , true)
            startScreen<MainScreen>(true)
        }
    }

    private suspend fun hideLoading() {
        withContext(Dispatchers.Main) {
            loader?.let {
                it.dismiss()
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
                    membersAdapter.mainSelected.forEach {
                        membersAdapter2.add(it)
                    }
                    membersAdapter2.setStatus("NO")
                    activity?.let {
                        GroupMemebers?.apply {
                            this.layoutManager = LinearLayoutManager(it, LinearLayoutManager.VERTICAL, false)
                            this.adapter = membersAdapter2
                            (this.adapter as MembersAdapter).context = it
                        }
                    }

                }
            }

            updateDialog.show()
        }
    }
}