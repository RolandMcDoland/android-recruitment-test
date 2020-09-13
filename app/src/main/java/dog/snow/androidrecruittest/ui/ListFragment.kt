package dog.snow.androidrecruittest.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import dog.snow.androidrecruittest.R
import dog.snow.androidrecruittest.network.JsonplaceholderEndpoints
import dog.snow.androidrecruittest.network.ServiceBuilder
import dog.snow.androidrecruittest.repository.model.RawAlbum
import dog.snow.androidrecruittest.repository.model.RawPhoto
import dog.snow.androidrecruittest.repository.model.RawUser
import dog.snow.androidrecruittest.ui.adapter.ListAdapter
import dog.snow.androidrecruittest.ui.model.Detail
import dog.snow.androidrecruittest.ui.model.ListItem
import kotlinx.android.synthetic.main.layout_empty_view.*
import kotlinx.android.synthetic.main.layout_search.*
import kotlinx.android.synthetic.main.list_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListFragment : Fragment(R.layout.list_fragment) {
    private var request: JsonplaceholderEndpoints? = null

    private var itemList: MutableList<ListItem> = mutableListOf<ListItem>()

    private var listAdapter: ListAdapter? = null

    private var photoMap: MutableMap<Int, RawPhoto> = mutableMapOf()
    private var albumMap: MutableMap<Int, RawAlbum> = mutableMapOf()
    private var userMap: MutableMap<Int, RawUser> = mutableMapOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        request = ServiceBuilder.buildService(JsonplaceholderEndpoints::class.java, this.requireContext())
        val call = request!!.getPhotos(100)

        call.enqueue(object : Callback<List<RawPhoto>> {
            override fun onResponse(call: Call<List<RawPhoto>>, response: Response<List<RawPhoto>>) {
                if (response.isSuccessful){
                    getDataAndCreateRecyclerView(response)

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

    private fun getDataAndCreateRecyclerView(orgResponse: Response<List<RawPhoto>>) {
        val albumIds = mutableListOf<Int>()

        orgResponse.body()!!.forEach {
            photoMap[it.id] = it

            if (!albumIds.contains(it.albumId))
                albumIds.add(it.albumId)
        }

        albumIds.forEach {
            val call = request!!.getAlbum(it)

            call.enqueue(object : Callback<RawAlbum> {
                override fun onResponse(call: Call<RawAlbum>, response: Response<RawAlbum>) {
                    if (response.isSuccessful) {
                        albumMap[it] = response.body()!!

                        if(albumIds.size == albumMap.size)
                            getUsers(orgResponse)
                    }
                    else
                        System.out.println(response.message())
                }

                override fun onFailure(call: Call<RawAlbum>, t: Throwable) {
                    System.out.println(t.message)
                }
            })
        }
    }

    private fun getUsers(orgResponse: Response<List<RawPhoto>>) {
        val userIds = mutableListOf<Int>()

        albumMap.forEach {
            if (!userIds.contains(it.value.userId))
                userIds.add(it.value.userId)
        }

        userIds.forEach {
            val call = request!!.getUser(it)

            call.enqueue(object : Callback<RawUser> {
                override fun onResponse(call: Call<RawUser>, response: Response<RawUser>) {
                    if (response.isSuccessful) {
                        userMap[it] = response.body()!!

                        if(userIds.size == userMap.size)
                            createRecyclerView(orgResponse)
                    }
                    else
                        System.out.println(response.message())
                }

                override fun onFailure(call: Call<RawUser>, t: Throwable) {
                    System.out.println(t.message)
                }
            })
        }
    }

    private fun createRecyclerView(response: Response<List<RawPhoto>>) {
        itemList = mutableListOf<ListItem>()
        response.body()!!.forEach {
            itemList.add(ListItem(it.id, it.title, albumMap[it.albumId]!!.title, it.thumbnailUrl))
        }

        val buz: (ListItem, Int, View)->Unit = { listItem, i, view ->
            val userId = albumMap[photoMap[listItem.id]!!.albumId]!!.userId
            val detail = Detail(listItem.id, listItem.title, listItem.albumTitle,
                userMap[userId]!!.username, userMap[userId]!!.email, userMap[userId]!!.phone,
                photoMap[listItem.id]!!.url)
            val bundle = bundleOf("detail" to detail)
            view.findNavController().navigate(R.id.action_view_details, bundle)
        }

        val la = ListAdapter(buz)
        la.submitList(itemList)
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

        if(filteredList.isEmpty()) {
            tv_empty.text = getString(R.string.empty_filter_list, s)
            rv_items.visibility = View.GONE
            tv_empty.visibility = View.VISIBLE
        }
        else {
            listAdapter!!.submitList(filteredList)

            rv_items.visibility = View.VISIBLE
            tv_empty.visibility = View.GONE
        }
    }
}