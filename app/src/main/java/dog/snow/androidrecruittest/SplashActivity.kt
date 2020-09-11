package dog.snow.androidrecruittest

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.splash_activity.*
import dog.snow.androidrecruittest.network.JsonplaceholderEndpoints
import dog.snow.androidrecruittest.network.ServiceBuilder
import dog.snow.androidrecruittest.repository.model.RawPhoto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashActivity : AppCompatActivity(R.layout.splash_activity) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        throbber.visibility = View.VISIBLE

        val request = ServiceBuilder.buildService(JsonplaceholderEndpoints::class.java, this)
        val call = request.getPhotos(100)

        call.enqueue(object : Callback<List<RawPhoto>> {
            override fun onResponse(call: Call<List<RawPhoto>>, response: Response<List<RawPhoto>>) {
                if (response.isSuccessful){
                    System.out.println(response.body())
                    throbber.visibility = View.GONE
                } else {
                    showError(response.message())
                }
            }
            override fun onFailure(call: Call<List<RawPhoto>>, t: Throwable) {
                showError(t.message)
            }
        })
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