package hu.grouper.app.fragment

import android.view.View
import hu.grouper.app.R
import io.vortex.android.ui.fragment.VortexBaseFragment

/**
 * Created By : Yazan Tarifi
 * Date : 12/6/2019
 * Time : 6:15 PM
 */

class HomeFragment : VortexBaseFragment() {
    override fun getLayoutRes(): Int {
        return R.layout.fragment_home
    }

    override fun initScreen(view: View) {

    }
}