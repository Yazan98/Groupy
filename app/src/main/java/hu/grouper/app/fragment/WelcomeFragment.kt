package hu.grouper.app.fragment

import android.Manifest
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
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
                findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
            }
        }

        activity?.let {
            ActivityCompat.requestPermissions(
                    it,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    123
            )
        }
    }
}
