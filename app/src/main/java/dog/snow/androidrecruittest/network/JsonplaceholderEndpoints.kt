package dog.snow.androidrecruittest.network

import dog.snow.androidrecruittest.repository.model.RawPhoto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface JsonplaceholderEndpoints {
    @GET("/photos")
    fun getPhotos(@Query("_limit") limit: Int): Call<List<RawPhoto>>
}