package com.yazan98.groupyadmin.screens


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.yazan98.groupyadmin.R
import io.vortex.android.ui.activity.VortexScreen
import kotlinx.android.synthetic.main.screen_profile_external.*

class ExternalProfileScreen : VortexScreen() {

    override fun getLayoutRes(): Int {
        return R.layout.screen_profile_external
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.extras?.let { bundle ->
            bundle.getString("name")?.let { res ->
                Name?.let {
                    it.text = res
                }
            }

            bundle.getString("email")?.let { les ->
                Email?.let {
                    it.text = les
                }

                bundle.getString("accountType")?.let { det ->
                    AccountType?.let {
                        it.text = det
                    }
                }

                bundle.getString("bio")?.let { res ->
                    Bio?.let {
                        it.text = res
                    }
                }

                bundle.getString("id")?.let { res ->
                    ID?.let {
                        it.text = res
                    }
                }
            }
        }

        ID?.apply {
            this.setOnClickListener {
                setClipboard(this@ExternalProfileScreen , ID?.text.toString())
                onBackPressed()
            }
        }

    }


    @SuppressLint("ObsoleteSdkInt")
    private fun setClipboard(context: Context, text: String) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.text.ClipboardManager
            clipboard.text = text
        } else {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("Copied Text", text)
            clipboard.primaryClip = clip
        }
        Toast.makeText(context , "Copy Member ID" , Toast.LENGTH_SHORT).show()
    }

}