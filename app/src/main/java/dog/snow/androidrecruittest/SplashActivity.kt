package dog.snow.androidrecruittest

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.splash_activity.*
import java.net.HttpURLConnection
import java.net.URL

class SplashActivity : AppCompatActivity(R.layout.splash_activity) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val url = "https://jsonplaceholder.typicode.com/photos?_limit=100"

        AsyncTaskHandleJson().execute(url)

        throbber.visibility = View.VISIBLE
    }

    //Get a response from specified URL
    inner class AsyncTaskHandleJson : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg url: String?): String {
            var response: String
            val connection = URL(url[0]).openConnection() as HttpURLConnection
            try {
                connection.connect()
                response = connection.inputStream.use { it.reader().use { reader -> reader.readText() } }
            } finally {
                connection.disconnect()
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            // TODO process JSON

            throbber.visibility = View.GONE
        }
    }

    private fun showError(errorMessage: String?) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.cant_download_dialog_title)
            .setMessage(getString(R.string.cant_download_dialog_message, errorMessage))
            .setPositiveButton(R.string.cant_download_dialog_btn_positive) { _, _ -> /*tryAgain()*/ }
            .setNegativeButton(R.string.cant_download_dialog_btn_negative) { _, _ -> finish() }
            .create()
            .apply { setCanceledOnTouchOutside(false) }
            .show()
    }
}