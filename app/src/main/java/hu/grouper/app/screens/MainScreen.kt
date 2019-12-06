package hu.grouper.app.screens

import android.os.Bundle
import hu.grouper.app.R
import hu.grouper.app.adapter.ViewPagerAdapter
import hu.grouper.app.fragment.EncryptionFragment
import hu.grouper.app.fragment.ProfileFragment
import io.vortex.android.ui.activity.VortexScreen
import kotlinx.android.synthetic.main.screen_main.*

/**
 * Created By : Yazan Tarifi
 * Date : 12/5/2019
 * Time : 7:30 AM
 */

class MainScreen : VortexScreen() {

    private val adapter: ViewPagerAdapter by lazy {
        ViewPagerAdapter(supportFragmentManager)
    }

    override fun getLayoutRes(): Int {
        return R.layout.screen_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter.addFragment(ProfileFragment() , "Profile")
        adapter.addFragment(EncryptionFragment() , "Encryption")
        MainViewPager?.let {
            it.adapter = adapter
        }

        BottomNavController?.apply {
            this.setOnNavigationItemSelectedListener {
                when (it.itemId) {

                }
                true
            }
        }
    }
}
