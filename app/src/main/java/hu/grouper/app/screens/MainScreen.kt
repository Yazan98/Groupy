package hu.grouper.app.screens

import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import hu.grouper.app.R
import hu.grouper.app.adapter.ViewPagerAdapter
import hu.grouper.app.fragment.*
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

        adapter.addFragment(HomeFragment() , "Home")
        adapter.addFragment(TasksFragment() , "Tasks")
        adapter.addFragment(OptionsFragment() , "Encryption")
        adapter.addFragment(ProfileFragment() , "Profile")

        MainViewPager?.let {
            it.adapter = adapter
        }

        MainViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) = Unit
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit
            override fun onPageSelected(position: Int) {
                BottomNavController.menu.getItem(position).isChecked = true
            }
        })

        BottomNavController.setOnNavigationItemSelectedListener { p0 ->
            when (p0.itemId) {
                R.id.Home -> {
                    MainViewPager.currentItem = 0
                    true
                }

                R.id.Tasks -> {
                    MainViewPager.currentItem = 1
                    true
                }

                R.id.Encryption -> {
                    MainViewPager.currentItem = 2
                    true
                }

                R.id.Profile -> {
                    MainViewPager.currentItem = 3
                    true
                }
                else -> false
            }
        }
    }
}
