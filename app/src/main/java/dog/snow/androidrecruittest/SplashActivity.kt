package dog.snow.androidrecruittest

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.splash_activity.*
import dog.snow.androidrecruittest.network.JsonplaceholderEndpoints
import dog.snow.androidrecruittest.network.ServiceBuilder
import dog.snow.androidrecruittest.repository.model.RawAlbum
import dog.snow.androidrecruittest.repository.model.RawPhoto
import dog.snow.androidrecruittest.repository.model.RawUser
import dog.snow.androidrecruittest.ui.model.ListItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashActivity : AppCompatActivity(R.layout.splash_activity) {
    private var request: JsonplaceholderEndpoints? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        throbber.visibility = View.VISIBLE

        request = ServiceBuilder.buildService(JsonplaceholderEndpoints::class.java, this)
        val call = request!!.getPhotos(100)

        call.enqueue(object : Callback<List<RawPhoto>> {
            override fun onResponse(call: Call<List<RawPhoto>>, response: Response<List<RawPhoto>>) {
                if (response.isSuccessful){
                    throbber.visibility = View.GONE

                    downloadAlbumsAndUsers(response)

                    changeActivity()
                } else {
                    showError(response.message())
                }
            }
            override fun onFailure(call: Call<List<RawPhoto>>, t: Throwable) {
                showError(t.message)
            }
        })
    }

    private fun downloadAlbumsAndUsers(response: Response<List<RawPhoto>>) {
        val albumIds = mutableListOf<Int>()
        val userIds = mutableListOf<Int>()

        response.body()!!.forEach {
            if (!albumIds.contains(it.albumId))
                albumIds.add(it.albumId)
        }

        albumIds.forEach {
            val call = request!!.getAlbum(it)

            call.enqueue(object : Callback<RawAlbum> {
                override fun onResponse(call: Call<RawAlbum>, response: Response<RawAlbum>) {
                    if (response.isSuccessful) {
                        if (!userIds.contains(response.body()!!.userId)) {
                            albumIds.add(response.body()!!.userId)
                            downloadUser(response.body()!!.userId)
                        }
                    } else {
                        showError(response.message())
                    }
                }

                override fun onFailure(call: Call<RawAlbum>, t: Throwable) {
                    showError(t.message)
                }
            })
        }
    }

    private fun downloadUser(id: Int) {
        val call = request!!.getUser(id)

        call.enqueue(object : Callback<RawUser> {
            override fun onResponse(call: Call<RawUser>, response: Response<RawUser>) {
                if (!response.isSuccessful) {
                    showError(response.message())
                } else {
                    System.out.println(response.body())
                }
            }

            override fun onFailure(call: Call<RawUser>, t: Throwable) {
                showError(t.message)
            }
        })
    }

    private fun changeActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
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