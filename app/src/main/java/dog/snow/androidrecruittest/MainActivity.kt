package dog.snow.androidrecruittest

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import dog.snow.androidrecruittest.network.JsonplaceholderEndpoints
import dog.snow.androidrecruittest.network.ServiceBuilder
import dog.snow.androidrecruittest.repository.model.RawPhoto
import dog.snow.androidrecruittest.ui.adapter.ListAdapter
import dog.snow.androidrecruittest.ui.model.ListItem
import kotlinx.android.synthetic.main.layout_empty_view.*
import kotlinx.android.synthetic.main.list_fragment.*
import kotlinx.android.synthetic.main.splash_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(R.layout.main_activity){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(findViewById(R.id.toolbar))
    }

    override fun onStart() {
        super.onStart()
        System.out.println("Starting")
        val request = ServiceBuilder.buildService(JsonplaceholderEndpoints::class.java, this)
        val call = request.getPhotos(100)

        call.enqueue(object : Callback<List<RawPhoto>> {
            override fun onResponse(call: Call<List<RawPhoto>>, response: Response<List<RawPhoto>>) {
                if (response.isSuccessful){
                    var li = mutableListOf<ListItem>()
                    response.body()!!.forEach {
                        li.add(ListItem(it.id, it.title, it.title, it.thumbnailUrl))
                    }
                    val buz: (ListItem, Int, View)->Unit = { l, i, v ->   println("another message: $i") }
                    val la = ListAdapter(buz)
                    la.submitList(li)
                    rv_items.adapter = la
                    rv_items.visibility = View.VISIBLE
                    tv_empty.visibility = View.GONE
                } else {
                    System.out.println(response.message())
                }
            }
            override fun onFailure(call: Call<List<RawPhoto>>, t: Throwable) {
                System.out.println(t.message)
            }
        })
    }
}