package dog.snow.androidrecruittest.network

import dog.snow.androidrecruittest.repository.model.RawAlbum
import dog.snow.androidrecruittest.repository.model.RawPhoto
import dog.snow.androidrecruittest.repository.model.RawUser
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface JsonplaceholderEndpoints {
    @GET("/photos")
    fun getPhotos(@Query("_limit") limit: Int): Call<List<RawPhoto>>

    @GET("/albums/{id}")
    fun getAlbum(@Path("id") id: Int): Call<RawAlbum>

    @GET("/users/{id}")
    fun getUser(@Path("id") id: Int): Call<RawUser>
}