package dog.snow.androidrecruittest.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {
    fun<T> buildService(service: Class<T>, context: Context): T{
        val cacheSize = (10 * 1024 * 1024).toLong()
        val myCache = Cache(context.cacheDir, cacheSize)

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                var request = chain.request()
                if (!hasNetwork(context)!!) {
                    val maxStale = 60 * 10
                    request = request.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                        .header("Age", "0")
                        .removeHeader("Pragma")
                        .build()
                }
                else
                    request = request.newBuilder()
                        .header("User-agent", "Cool-app")
                        .build()
                chain.proceed(request)
            }
            .addNetworkInterceptor { chain ->
                val response = chain.proceed(chain.request())
                val maxAge = 60 * 5

                response.newBuilder()
                    .header("Cache-Control", "public, max-age=$maxAge")
                    .header("Age", "0")
                    .removeHeader("Pragma")
                    .build()
            }
            .cache(myCache)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(service)
    }

    @Suppress("UNREACHABLE_CODE")
    fun hasNetwork(context: Context): Boolean? {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }
}
