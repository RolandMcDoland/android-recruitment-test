package dog.snow.androidrecruittest.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import dog.snow.androidrecruittest.R
import dog.snow.androidrecruittest.ui.model.Detail
import kotlinx.android.synthetic.main.details_fragment.*

class DetailsFragment : Fragment(R.layout.details_fragment) {
    private var detail: Detail? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        detail = requireArguments().getParcelable("detail")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_photo_title.text = detail!!.photoTitle
        tv_album_title.text = detail!!.albumTitle
        tv_username.text = detail!!.username
        tv_email.text = detail!!.email
        tv_phone.text = detail!!.phone
        Picasso.get().load(detail!!.url).into(iv_photo)
    }
}