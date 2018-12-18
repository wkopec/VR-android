package pl.kopec.vr_android

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import com.google.vr.sdk.base.*
import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener
import com.google.vr.sdk.widgets.pano.VrPanoramaView
import kotlinx.android.synthetic.main.activity_main.*
import javax.microedition.khronos.egl.EGLConfig
import com.google.vr.sdk.base.GvrView
import jmini3d.android.Renderer3d
import jmini3d.android.ResourceLoader
import jmini3d.*
import jmini3d.material.PhongMaterial
import jmini3d.geometry.BoxGeometry
import jmini3d.light.PointLight
import jmini3d.light.AmbientLight
import jmini3d.Object3d
import android.animation.ValueAnimator
import android.view.animation.Animation


class MainActivity : GvrActivity(), GvrView.StereoRenderer {

    private lateinit var scene: Scene
    private lateinit var renderer: Renderer3d
    private var eyeRender = VREyeRender()
    private lateinit var cube: Object3d

    private lateinit var xAnimator: ValueAnimator
    private lateinit var yAnimator: ValueAnimator
    private lateinit var zAnimator: ValueAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        renderer = Renderer3d(ResourceLoader(this))
        //setupPanorama()
        setupGvr()
        setupScene()
        setupAnimator()

    }

//    private fun setupPanorama() {
//        val istr = resources.openRawResource(
//            resources.getIdentifier(
//                "andes", "raw", packageName
//            )
//        )
//        val panoOptions = VrPanoramaView.Options()
//        panoOptions.inputType = VrPanoramaView.Options.TYPE_STEREO_OVER_UNDER
//        panoView.loadImageFromBitmap(BitmapFactory.decodeStream(istr), panoOptions)
//        panoView.displayMode = 3
//
//        panoView.setEventListener(object : VrPanoramaEventListener() {
//            override fun onClick() {
//                super.onClick()
//                Log.d("test", "CLICK!")
//            }
//        })
//    }

    private fun setupGvr() {

        JMini3d.useOpenglAxisSystem()
        gvr.setEGLConfigChooser(8, 8, 8, 8, 16, 8)
        gvr.setRenderer(this)
        gvr.setTransitionViewEnabled(true)
        gvr.distortionCorrectionEnabled = true
        gvr.asyncReprojectionEnabled = true

        AndroidCompat.setSustainedPerformanceMode(this, true)
        gvrView = gvr

    }

    private fun setupScene() {
        scene = Scene()

        scene.camera?.setPosition(0f, 0f, 0f)
        scene.camera?.setTarget(0f, 0f, -1f)
        scene.camera?.setUpAxis(0f, 1f, 0f)
        scene.camera?.updateViewMatrix()

        val white = Color4(255, 255, 255)
        val red = Color4(255, 128, 128)

        val light = AmbientLight(white, 0.5f)
        scene.addLight(light)

        val light2 = PointLight(Vector3(3f, 0.5f, 1f), white, 0.5f)
        scene.addLight(light2)

        val geometry = BoxGeometry(0.5f)
        val material = PhongMaterial(red, white, white)
        cube = Object3d(geometry, material)
        cube.setPosition(0f, 0f, -5f)

        scene.addChild(cube)
    }

    private fun setupAnimator() {
        xAnimator = ValueAnimator.ofFloat(2f, 0f)
        yAnimator = ValueAnimator.ofFloat(3f, 0f)
        zAnimator = ValueAnimator.ofFloat(-212f, 1f)

        xAnimator.repeatCount = Animation.INFINITE
        xAnimator.duration = 5000
        yAnimator.repeatCount = Animation.INFINITE
        yAnimator.duration = 5000
        zAnimator.repeatCount = Animation.INFINITE
        zAnimator.duration = 5000

        xAnimator.start()
        yAnimator.start()
        zAnimator.start()
    }


    override fun onCardboardTrigger() {
        setupScene()
        vibrate()
    }


    override fun onNewFrame(headTransform: HeadTransform?) {
        cube.setPosition(xAnimator.animatedValue as Float, yAnimator.animatedValue as Float, zAnimator.animatedValue as Float)
    }

    override fun onSurfaceChanged(width: Int, height: Int) {

    }

    override fun onSurfaceCreated(config: EGLConfig?) {

    }

    override fun onDrawEye(eye: Eye?) {
        if(eye != null) {
            eyeRender.render(scene, eye, renderer)
        }
    }

    override fun onFinishFrame(viewport: Viewport?) {

    }

    override fun onRendererShutdown() {

    }

    private fun vibrate() {
        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            val pattern = longArrayOf(0, 100, 100, 100)
            v.vibrate(pattern, -1)
        }
    }

}
