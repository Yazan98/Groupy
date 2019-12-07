package hu.grouper.app.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import hu.grouper.app.R
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element

/**
 * Created By : Yazan Tarifi
 * Date : 12/7/2019
 * Time : 9:24 AM
 */

class AboutUsScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val result = AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.ic_team)
                .addItem(Element("Yazan Tarifi : Android Developer" , R.drawable.account))
                .addItem(Element("Eyad Hewware : Android Developer" , R.drawable.account))
                .addItem(Element("Yazan Waleed : Database Manager" , R.drawable.account))
                .addItem(Element("Mohammad Almomani : Authentication Manager" , R.drawable.account))
                .addItem(Element("Ayob Sarhan : Team Leader" , R.drawable.account))
                .addGroup("Connect with us")
                .addEmail("grouper@support.com")
                .addWebsite("https://github.com/Yazan98")
                .addGitHub("Yazan98")
                .setDescription("Groupy is The First Android Application To Manage Students With Project Admin To Complete Graduation Project And This Application Has Many Features To Manage Them Into Beautiful Application By Manage The Tasks And Assign Each Task To Specific User")
                .create()

        setContentView(result)
    }
}