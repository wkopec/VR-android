package pl.kopec.vr_android

import jmini3d.MatrixUtils
import com.google.vr.sdk.base.Eye
import jmini3d.Renderer3d
import jmini3d.Scene

class VREyeRender {

    var leftProjectionMatrix = FloatArray(16)
    var leftViewMatrix = FloatArray(16)
    var rightProjectionMatrix = FloatArray(16)
    var rightViewMatrix = FloatArray(16)

    fun render(scene: Scene, eye: Eye, renderer3d: Renderer3d) {
        // Allow concurrence separating matices
        if (eye.type == Eye.Type.LEFT) {
            MatrixUtils.copyMatrix(
                eye.getPerspective(
                    scene.camera.getNear(),
                    scene.camera.getFar()
                ), leftProjectionMatrix
            )
            MatrixUtils.multiply(eye.eyeView, scene.camera.viewMatrix, leftViewMatrix)
            renderer3d.render(scene, leftProjectionMatrix, leftViewMatrix)
        } else {
            MatrixUtils.copyMatrix(
                eye.getPerspective(
                    scene.camera.getNear(),
                    scene.camera.getFar()
                ), rightProjectionMatrix
            )
            MatrixUtils.multiply(eye.eyeView, scene.camera.viewMatrix, rightViewMatrix)
            renderer3d.render(scene, rightProjectionMatrix, rightViewMatrix)
        }
    }
}