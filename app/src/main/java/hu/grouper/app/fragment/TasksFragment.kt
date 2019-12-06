package hu.grouper.app.fragment

import android.view.View
import hu.grouper.app.R
import hu.grouper.app.screens.NewTaskScreen
import io.vortex.android.prefs.VortexPrefs
import io.vortex.android.ui.fragment.VortexBaseFragment
import kotlinx.android.synthetic.main.fragment_tasks.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Created By : Yazan Tarifi
 * Date : 12/6/2019
 * Time : 5:43 PM
 */

class TasksFragment : VortexBaseFragment() {
    override fun getLayoutRes(): Int {
        return R.layout.fragment_tasks
    }

    override fun initScreen(view: View) {

        GlobalScope.launch {
            VortexPrefs.get("AccountType" , "")?.also {
                (it as String).let {
                    if(it.equals("ADMIN")) {
                        AddNewTask?.let {
                            it.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

        AddNewTask?.apply {
            this.setOnClickListener {
                GlobalScope.launch {
                    startScreen<NewTaskScreen>(false)
                }
            }
        }
    }
}