package pl.kopec.vr_android

import android.content.Context
import android.opengl.Matrix
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
import android.os.AsyncTask
import java.lang.ref.WeakReference
import android.os.Vibrator

class MainActivity : GvrActivity(), GvrView.StereoRenderer, OnAsteroidHitListener {

    private lateinit var scene: Scene
    private lateinit var renderer: Renderer3d
    private var eyeRender = VREyeRender()
    private var asteroids = HashSet<Asteroid>()

    private var camera: FloatArray? = null
    private var view: FloatArray? = null
    private var headView: FloatArray? = null
    private var modelViewProjection: FloatArray? = null
    private var modelView: FloatArray? = null
    private var tempPosition: FloatArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        renderer = Renderer3d(ResourceLoader(this))

        setupArrays()
        setupGvr()
        setupScene()
        generateNextAsteroid(8000)

    }

    private fun setupArrays() {

        //modelCube = FloatArray(16)
        camera = FloatArray(16)
        view = FloatArray(16)
        modelViewProjection = FloatArray(16)
        modelView = FloatArray(16)
        tempPosition = FloatArray(4)
        headView = FloatArray(16)

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
        CreateAsteroidTask(this).execute()
    }

    private class CreateAsteroidTask constructor(context: MainActivity) : AsyncTask<Void, Void, Asteroid>() {

        private val activityReference: WeakReference<MainActivity> = WeakReference(context)

        override fun doInBackground(vararg params: Void): Asteroid {
            val white = Color4(255, 255, 255, 255)
            val transparent = Color4(0, 0, 0, 0)
            val geometry = ObjLoader().load(activityReference.get()!!.resources.assets.open("asteroid_geometry.obj"))
            val material = PhongMaterial(Texture("asteroid_material.jpg"), white, white, transparent)
            return Asteroid(Object3d(geometry, material), activityReference.get()!!)
        }

        override fun onPostExecute(asteroid: Asteroid) {
            asteroid.initAsteroid()
            activityReference.get()!!.scene.addChild(asteroid.object3d)
            activityReference.get()!!.asteroids.add(asteroid)
        }
    }

    override fun onCardboardTrigger() {
        asteroids.filter { !it.isAsteroidDestroyed }.forEach {
            if (isLookingAtObject(it.object3d)) {
                destroyAsteroid(it)
                vibrate(100)
            }
        }
    }


    override fun onNewFrame(headTransform: HeadTransform?) {
        // Build the camera matrix and apply it to the ModelView.
        Matrix.setLookAtM(camera, 0, 0.0f, 0.0f, CAMERA_Z, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f)

        headTransform!!.getHeadView(headView, 0)

        asteroids.filter { !it.isAsteroidDestroyed }.forEach {
            it.updatePosition()
        }
    }

    override fun onDrawEye(eye: Eye?) {
        if (eye != null) {
            // Apply the eye transformation to the camera.
            Matrix.multiplyMM(view, 0, eye.eyeView, 0, camera, 0)

            eyeRender.render(scene, eye, renderer)
        }
    }

    override fun onSurfaceChanged(width: Int, height: Int) {

    }

    override fun onSurfaceCreated(config: EGLConfig?) {

    }

    override fun onFinishFrame(viewport: Viewport?) {

    }

    override fun onRendererShutdown() {

    }

    override fun onAsteroidHit(asteroid: Asteroid) {
        vibrate(1000)
        //GAME OVER
    }

    private fun destroyAsteroid(asteroid: Asteroid) {
        scene.removeChild(asteroid.object3d)
        asteroid.isAsteroidDestroyed = true
    }

    private fun isLookingAtObject(object3d: Object3d): Boolean {
        // Convert object space to camera space. Use the headView from onNewFrame.
        Matrix.multiplyMM(modelView, 0, headView, 0, object3d.modelMatrix, 0)
        Matrix.multiplyMV(tempPosition, 0, modelView, 0, POS_MATRIX_MULTIPLY_VEC, 0)

        val pitch = Math.atan2(this.tempPosition!![1].toDouble(), (-tempPosition!![2]).toDouble()).toFloat()
        val yaw = Math.atan2(tempPosition!![0].toDouble(), (-tempPosition!![2]).toDouble()).toFloat()

        return Math.abs(pitch) < PITCH_LIMIT && Math.abs(yaw) < YAW_LIMIT
    }

    private fun vibrate(duration: Long) {
        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            val pattern = longArrayOf(0, duration, duration, duration)
            v.vibrate(pattern, -1)
        }
    }

    companion object {
        // Convenience vector for extracting the position from a matrix via multiplication.
        private val POS_MATRIX_MULTIPLY_VEC = floatArrayOf(0f, 0f, 0f, 1.0f)
        private const val YAW_LIMIT = 0.12f
        private const val PITCH_LIMIT = 0.12f
        private const val CAMERA_Z = 0.01f
    }

}

