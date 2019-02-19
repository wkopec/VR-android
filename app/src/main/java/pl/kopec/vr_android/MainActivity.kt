package pl.kopec.vr_android

import android.content.Context
import android.os.*
import com.google.vr.sdk.base.*
import kotlinx.android.synthetic.main.activity_main.*
import javax.microedition.khronos.egl.EGLConfig
import com.google.vr.sdk.base.GvrView
import jmini3d.android.Renderer3d
import jmini3d.android.ResourceLoader
import jmini3d.*
import jmini3d.material.PhongMaterial
import jmini3d.light.PointLight
import jmini3d.light.AmbientLight
import jmini3d.Object3d

import jmini3d.android.loader.ObjLoader
import jmini3d.Color4
import jmini3d.Texture

class MainActivity : GvrActivity(), GvrView.StereoRenderer, OnAsteroidHitListener {

    private lateinit var scene: Scene
    private lateinit var renderer: Renderer3d
    private var eyeRender = VREyeRender()
    private var asteroids = ArrayList<Asteroid>()

    private lateinit var staticAsteroid: Asteroid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        renderer = Renderer3d(ResourceLoader(this))

        setupGvr()
        setupScene()
        generateNextAsteroid(6000)

    }

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

        val light = AmbientLight(white, 0.5f)
        scene.addLight(light)

        val light2 = PointLight(Vector3(3f, 0.5f, 1f), white, 0.5f)
        scene.addLight(light2)

    }

    private fun generateNextAsteroid(nextAsteroidDelay: Long) {
        //addAsteroid()
        object : CountDownTimer(10000, nextAsteroidDelay) {
            override fun onTick(millisUntilFinished: Long) {
                addAsteroid()
            }

            override fun onFinish() {
                generateNextAsteroid((nextAsteroidDelay * 0.8).toLong())
            }

        }.start()
    }

    private fun addAsteroid() {
        val white = Color4(255, 255, 255, 255)
        val transparent = Color4(0, 0, 0, 0)
        val geometry = ObjLoader().load(resources.assets.open("asteroid_geometry.obj"))
        val material = PhongMaterial(Texture("asteroid_material.jpg"), white, white, transparent)

        val asteroid = Asteroid(Object3d(geometry, material), this)
        asteroid.initAsteroid()
        scene.addChild(asteroid.object3d)
        asteroids.add(asteroid)

    }

    override fun onCardboardTrigger() {
        setupScene()
        vibrate()
    }


    override fun onNewFrame(headTransform: HeadTransform?) {
        asteroids.forEach {
            it.updatePosition()
        }
    }

    override fun onSurfaceChanged(width: Int, height: Int) {

    }

    override fun onSurfaceCreated(config: EGLConfig?) {

    }

    override fun onDrawEye(eye: Eye?) {
        if (eye != null) {
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

    override fun onAsteroidHit(asteroid: Asteroid) {
        scene.removeChild(asteroid.object3d)
        asteroids.remove(asteroid)
    }

}

