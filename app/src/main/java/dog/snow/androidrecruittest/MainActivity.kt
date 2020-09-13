package dog.snow.androidrecruittest

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import dog.snow.androidrecruittest.network.NetworkConnection
import kotlinx.android.synthetic.main.layout_banner.*

class MainActivity : AppCompatActivity(R.layout.main_activity){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(findViewById(R.id.toolbar))

        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this, Observer { isConnected ->
            if(isConnected)
                banner.visibility = View.GONE
            else
                banner.visibility = View.VISIBLE
        })
    }


}