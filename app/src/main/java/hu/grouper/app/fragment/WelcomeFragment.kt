package hu.grouper.app.fragment

import android.view.View
import hu.grouper.app.R
import io.vortex.android.ui.fragment.VortexBaseFragment
import kotlinx.android.synthetic.main.fragment_welcome.*

/**
 * Created By : Yazan Tarifi
 * Date : 12/5/2019
 * Time : 6:39 AM
 */

class WelcomeFragment : VortexBaseFragment() {
    override fun getLayoutRes(): Int {
        return R.layout.fragment_welcome
    }

    override fun initScreen(view: View) {
        Continue?.apply {
            this.setOnClickListener {

            }
        }
    }
}
