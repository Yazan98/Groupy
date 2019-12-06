package hu.grouper.app.fragment

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import hu.grouper.app.R
import hu.grouper.app.adapter.HomeMembersAdapter
import hu.grouper.app.data.models.Profile
import hu.grouper.app.data.models.Task
import io.vortex.android.prefs.VortexPrefs
import io.vortex.android.ui.fragment.VortexBaseFragment
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created By : Yazan Tarifi
 * Date : 12/6/2019
 * Time : 6:15 PM
 */

class HomeFragment : VortexBaseFragment() {

    var length = 1
    private var tasks = ArrayList<Task>()
    private val homeAdapter: HomeMembersAdapter by lazy {
        HomeMembersAdapter()
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_home
    }

    @SuppressLint("WrongConstant")
    override fun initScreen(view: View) {
        GlobalScope.launch {
            showLoading()
            val groupID = VortexPrefs.get("GroupID", "") as String
            FirebaseFirestore.getInstance().collection("groups").document(groupID)
                    .get().addOnCompleteListener {
                        it.result?.let {
                            GlobalScope.launch {
                                getAdminName(it.getString("name"), it.getString("adminID"))
                                showMembersByIds(it.get("members") as List<String>)
                                showProgressByGroupId(groupID)
                            }
                        }
                    }
        }

        activity?.let {
            MembersGroup?.apply {
                this.layoutManager = LinearLayoutManager(it, LinearLayoutManager.VERTICAL, false)
                this.adapter = homeAdapter
                (this.adapter as HomeMembersAdapter).context = it
            }
        }
    }

    private suspend fun getAdminName(name: String?, id: String?) {
        withContext(Dispatchers.IO) {
            id?.let {
                FirebaseFirestore.getInstance().collection("users")
                        .document(it).get().addOnCompleteListener {
                            it.result?.let {
                                GlobalScope.launch {
                                    showGroupHeader(it.getString("name"), name)
                                }
                            }
                        }
            }
        }
    }

    private suspend fun showMembersByIds(ids: List<String>) {
        withContext(Dispatchers.IO) {
            ids.forEach {
                FirebaseFirestore.getInstance().collection("users")
                        .document(it).get().addOnCompleteListener {
                            it.result?.let {
                                if (homeAdapter.items.size <= 3) {
                                    homeAdapter.add(Profile(
                                            id = it.getString("id"),
                                            name = it.getString("name"),
                                            bio = it.getString("bio"),
                                            lng = it.getDouble("lng"),
                                            lat = it.getDouble("lat")
                                    ))
                                }
                            }
                        }
            }
            hideLoading()
        }
    }

    private suspend fun showProgressByGroupId(groupId: String) {
        withContext(Dispatchers.IO) {
            FirebaseFirestore.getInstance().collection("tasks")
                    .whereEqualTo("groupId", groupId)
                    .get().addOnCompleteListener {
                        it.result?.let {
                            for (doc in it.documents) {
                                FirebaseFirestore.getInstance().collection("tasks")
                                        .document(doc.id).get().addOnCompleteListener {
                                            it.result?.let {
                                                tasks.add(Task(
                                                        id = it.getString("id"),
                                                        name = it.getString("name"),
                                                        groupId = it.getString("groupId"),
                                                        status = it.getString("status"),
                                                        userId = it.getString("userId")
                                                ))

                                                if (it.getString("status").equals("DONE")) {
                                                    length += 1
                                                }

                                                progress?.apply {
                                                    this.max = tasks.size
                                                    this.progress = length
                                                }
                                            }
                                        }
                            }

                        }
                    }
        }
    }

    private suspend fun showGroupHeader(adminName: String?, groupName: String?) {
        withContext(Dispatchers.Main) {
            groupName?.let { res ->
                textView4?.let {
                    it.text = res
                }
            }

            adminName?.let { res ->
                AdminName?.let {
                    it.text = "Group Admin : $res"
                }
            }
        }
    }

    private suspend fun showLoading() {
        withContext(Dispatchers.Main) {
            Loading?.let {
                it.visibility = View.VISIBLE
            }

            ScreenContent?.let {
                it.visibility = View.GONE
            }
        }
    }

    private suspend fun hideLoading() {
        withContext(Dispatchers.Main) {
            Loading?.let {
                it.visibility = View.GONE
            }

            ScreenContent?.let {
                it.visibility = View.VISIBLE
            }
        }
    }

}
