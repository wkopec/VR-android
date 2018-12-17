package pl.kopec.vr_android

import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener
import com.google.vr.sdk.widgets.pano.VrPanoramaView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupView()
    }

    private fun setupView() {

        val istr = resources.openRawResource(
            resources.getIdentifier(
                "andes", "raw", packageName
            )
        )
        val panoOptions = VrPanoramaView.Options()
        panoOptions.inputType = VrPanoramaView.Options.TYPE_STEREO_OVER_UNDER
        panoView.loadImageFromBitmap(BitmapFactory.decodeStream(istr), panoOptions)
        panoView.displayMode = 3

        panoView.setEventListener(object : VrPanoramaEventListener() {
            override fun onClick() {
                super.onClick()
                Log.d("test", "CLICK!")
            }
        })
    }
}
