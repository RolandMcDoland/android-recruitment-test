package dog.snow.androidrecruittest

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import dog.snow.androidrecruittest.network.JsonplaceholderEndpoints
import dog.snow.androidrecruittest.network.ServiceBuilder
import dog.snow.androidrecruittest.repository.model.RawPhoto
import dog.snow.androidrecruittest.ui.adapter.ListAdapter
import dog.snow.androidrecruittest.ui.model.ListItem
import kotlinx.android.synthetic.main.layout_empty_view.*
import kotlinx.android.synthetic.main.layout_search.*
import kotlinx.android.synthetic.main.list_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(R.layout.main_activity){
    private var itemList: List<ListItem>? = null

    private var listAdapter: ListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(findViewById(R.id.toolbar))
    }

    override fun onStart() {
        super.onStart()
        val request = ServiceBuilder.buildService(JsonplaceholderEndpoints::class.java, this)
        val call = request.getPhotos(100)

        call.enqueue(object : Callback<List<RawPhoto>> {
            override fun onResponse(call: Call<List<RawPhoto>>, response: Response<List<RawPhoto>>) {
                if (response.isSuccessful){
                    createRecyclerView(response)

                    addSearchListener()
                } else {
                    System.out.println(response.message())
                }
            }
            override fun onFailure(call: Call<List<RawPhoto>>, t: Throwable) {
                System.out.println(t.message)
            }
        })
    }

    private fun createRecyclerView(response: Response<List<RawPhoto>>) {
        val il = mutableListOf<ListItem>()
        response.body()!!.forEach {
            il.add(ListItem(it.id, it.title, it.title, it.thumbnailUrl))
        }
        itemList = il
        val buz: (ListItem, Int, View)->Unit = { l, i, v ->   println("another message: $i") }
        val la = ListAdapter(buz)
        la.submitList(il)
        listAdapter = la
        rv_items.adapter = la

        rv_items.visibility = View.VISIBLE
        tv_empty.visibility = View.GONE
    }

    private fun addSearchListener() {
        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterItemList(s)
            }
        })
    }

    private fun filterItemList(s: CharSequence?) {
        val filteredList = mutableListOf<ListItem>()

        itemList!!.forEach{
            if(it.title.contains(s!!, ignoreCase = true) || it.albumTitle.contains(s, ignoreCase = true))
                filteredList.add(it)
        }

        listAdapter!!.submitList(filteredList)
    }
}