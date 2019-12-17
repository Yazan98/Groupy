package hu.grouper.app.screens

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import hu.grouper.app.R
import hu.grouper.app.adapter.RequestsAdapter
import hu.grouper.app.data.models.JoinRequest
import io.vortex.android.prefs.VortexPrefs
import io.vortex.android.ui.activity.VortexScreen
import kotlinx.android.synthetic.main.screen_requests.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created By : Yazan Tarifi
 * Date : 12/17/2019
 * Time : 6:53 PM
 */

class RequestsScreen : VortexScreen() {

    private val mainAdapter: RequestsAdapter by lazy { RequestsAdapter() }

    override fun getLayoutRes(): Int {
        return R.layout.screen_requests
    }

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RequestsRecycler?.apply {
            this.layoutManager = LinearLayoutManager(this@RequestsScreen, LinearLayoutManager.VERTICAL, false)
            this.adapter = mainAdapter
            (this.adapter as RequestsAdapter).context = this@RequestsScreen
        }

        GlobalScope.launch {
            VortexPrefs.get("GroupID", "")?.let {
                FirebaseFirestore.getInstance().collection("requests")
                        .whereEqualTo("groupID", (it as String)).get()
                        .addOnCompleteListener {
                            it.result?.let {
                                it.documents?.forEach {
                                    GlobalScope.launch {
                                        showRequest(it.id)
                                    }
                                }
                            }
                        }
            }
        }
    }

    private suspend fun showRequest(id: String) {
        withContext(Dispatchers.IO) {
            FirebaseFirestore.getInstance().collection("requests")
                    .document(id).get().addOnCompleteListener {
                        it.result?.let {
                            mainAdapter.add(JoinRequest(
                                    id = it.getString("id"),
                                    name = it.getString("name"),
                                    groupID = it.getString("groupID"),
                                    userId = it.getString("userId"),
                                    docuementId = id
                            ))
                        }
                    }
        }
    }
}
