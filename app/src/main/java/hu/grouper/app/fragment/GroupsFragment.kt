package hu.grouper.app.fragment

import android.annotation.SuppressLint
import android.view.View
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import hu.grouper.app.R
import hu.grouper.app.adapter.GroupsAdapter
import hu.grouper.app.data.models.Group
import hu.grouper.app.screens.MainScreen
import io.vortex.android.prefs.VortexPrefs
import io.vortex.android.ui.fragment.VortexBaseFragment
import kotlinx.android.synthetic.main.groups_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created By : Yazan Tarifi
 * Date : 12/6/2019
 * Time : 12:42 PM
 */

class GroupsFragment : VortexBaseFragment() {

    private val mainAdapter: GroupsAdapter by lazy {
        GroupsAdapter(object : GroupsAdapter.GroupListener {
            override suspend fun onGroupSuccess() {
                withContext(Dispatchers.Main) {
                    activity?.let {
                        Toast.makeText(it, "The Request Sent Perfectly", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    override fun getLayoutRes(): Int {
        return R.layout.groups_fragment
    }

    @SuppressLint("WrongConstant")
    override fun initScreen(view: View) {
        GlobalScope.launch {
            getAllGroups()
            val type = VortexPrefs.get("AccountType", "") as String
            if (!type.equals("ADMIN")) {
                CreateNewGroup?.let {
                    it.text = "Finish"
                }
            }
        }

        activity?.let {
            GroupsList?.apply {
                this.layoutManager = LinearLayoutManager(it, LinearLayoutManager.VERTICAL, false)
                this.adapter = mainAdapter
                (this.adapter as GroupsAdapter).context = it
            }
        }

        CreateNewGroup?.apply {
            this.setOnClickListener {
                if (this.text.toString().equals("Finish")) {
                    GlobalScope.launch {
                        startScreen<MainScreen>(true)
                    }
                } else {
                    findNavController().navigate(R.id.action_groupsFragment_to_createNewGroupFragment)
                }
            }
        }
    }

    private suspend fun getAllGroups() {
        withContext(Dispatchers.IO) {
            FirebaseFirestore.getInstance().collection("groups")
                    .get().addOnCompleteListener {
                        if (it.isSuccessful) {
                            it.result?.let {
                                for (doc in it.documents) {
                                    GlobalScope.launch {
                                        getDocumentById(doc.id)
                                    }
                                }
                            }
                        } else {
                            GlobalScope.launch {
                                it.exception?.let {
                                    it.message?.let {
                                        showMessage(it)
                                    }
                                }
                            }
                        }
                    }
        }
    }

    private suspend fun getDocumentById(id: String) {
        withContext(Dispatchers.IO) {
            FirebaseFirestore.getInstance().collection("groups")
                    .document(id).get()
                    .addOnCompleteListener {
                        it.result?.let {
                            mainAdapter.addGroup(Group(
                                    id = it.getString("id"),
                                    name = it.getString("name"),
                                    adminID = it.getString("adminID"),
                                    members = it.get("members") as List<String>
                            ))
                        }
                    }
        }
    }

    private suspend fun showMessage(message: String) {
        withContext(Dispatchers.Main) {
            activity?.let {
                Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

}
