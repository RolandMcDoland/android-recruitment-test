package dog.snow.androidrecruittest

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.splash_activity.*
import java.net.HttpURLConnection
import java.net.URL
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dog.snow.androidrecruittest.repository.model.RawPhoto
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.Exception

class SplashActivity : AppCompatActivity(R.layout.splash_activity) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val url = "https://jsonplaceholder.typicode.com/photos?_limit=100"

        doAsync {
            var response: String
            val connection = URL(url).openConnection() as HttpURLConnection
            try {
                connection.connect()
                response = connection.inputStream.use { it.reader().use { reader -> reader.readText() } }
                uiThread {
                    handleJSON(response)
                    throbber.visibility = View.GONE
                }
            } catch (e: Exception) {
                uiThread { showError(e.message) }
            } finally {
                connection.disconnect()
            }
        }

        throbber.visibility = View.VISIBLE
    }

    private fun handleJSON(result: String?) {
        val gson = Gson()
        val arrayPhotosType = object : TypeToken<Array<RawPhoto>>() {}.type

        val photos: Array<RawPhoto> = gson.fromJson(result, arrayPhotosType)
        //TODO cache data
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