package hu.grouper.app.fragment

import android.annotation.SuppressLint
import android.view.View
import hu.grouper.app.R
import io.vortex.android.ui.fragment.VortexBaseFragment
import kotlinx.android.synthetic.main.fragment_encryption.*
import android.widget.Toast
import android.content.DialogInterface
import android.app.AlertDialog
import android.content.Context
import hu.grouper.app.XOR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Created By : Yazan Tarifi
 * Date : 12/6/2019
 * Time : 11:47 AM
 */

class EncryptionFragment : VortexBaseFragment() {

    override fun getLayoutRes(): Int {
        return R.layout.fragment_encryption
    }

    override fun initScreen(view: View) {
        InfoIcon?.apply {
            this.setOnClickListener {
                GlobalScope.launch {
                    showDialog()
                }
            }
        }

        EncryptButton?.apply {
            this.setOnClickListener {
                activity?.let {
                    when {
                        Message?.text.toString().isEmpty() -> Toast.makeText(it , "Message Required" , Toast.LENGTH_SHORT).show()
                        else -> {
                            GlobalScope.launch {
                                showEncryptedMessage(Message?.text.toString())
                            }
                        }
                    }
                }
            }
        }

        DecryptMessage?.apply {
            this.setOnClickListener {
                activity?.let {
                    when {
                        Message?.text.toString().isEmpty() -> Toast.makeText(it , "Message Required" , Toast.LENGTH_SHORT).show()
                        else -> {
                            GlobalScope.launch {
                                showEncryptedMessage(Message?.text.toString())
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun showDialog() {
        withContext(Dispatchers.Main) {
            activity?.let {
                val alertDialog = AlertDialog.Builder(it)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("About This Section")
                        .setMessage("This Section To Encrypt Messages At Group Chat And Apply XOR Algorithem At Project")
                        .setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.dismiss()
                        })
                        .show()
            }
        }
    }

    private suspend fun showEncryptedMessage(message: String) {
        withContext(Dispatchers.Main) {
            activity?.let {
                val result = XOR.encryptDecrypt(message)
                val alertDialog = AlertDialog.Builder(it)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Result")
                        .setMessage(result)
                        .setPositiveButton("Copy") { dialogInterface, i ->
                            setClipboard(it , result)
                            dialogInterface.dismiss()
                        }
                        .show()
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
        Toast.makeText(context , "Copy Message" , Toast.LENGTH_SHORT).show()
    }

}
