/*
 * Copyright (c) 2018 LocalBuzz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thanksmister.iot.mqtt.alarmpanel.utils

import android.content.Context
import android.hardware.Camera
import com.thanksmister.iot.mqtt.alarmpanel.R
import timber.log.Timber
import java.util.ArrayList

/**
 * Created by Michael Ritchie on 7/9/18.
 */
class CameraUtils {

    companion object {
        @Throws(RuntimeException::class)
        fun getCameraList(): ArrayList<String> {
            val cameraList: ArrayList<String> = ArrayList()
            for (i in 0 until Camera.getNumberOfCameras()) {
                var description: String
                try {
                    val c = Camera.open(i)
                    val p = c.parameters
                    val previewSize = p.previewSize
                    val width = previewSize.width
                    val height = previewSize.height
                    val info = Camera.CameraInfo()
                    Camera.getCameraInfo(i, info)
                    description = java.text.MessageFormat.format(
                            "{0}: {1} Camera {3}x{4} {2}º",
                            i,
                            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) "Front" else "Back",
                            info.orientation,
                            width,
                            height)
                    c.release()
                } catch (e: RuntimeException) {
                    Timber.e("Had a problem reading camera $i")
                    e.printStackTrace()
                    description = java.text.MessageFormat.format("{0}: Error", i)
                }
                cameraList.add(description)
            }
            return cameraList
        }

        @Throws(RuntimeException::class)
        fun getCameraListError(context: Context): ArrayList<String> {
            val cameraList: ArrayList<String> = ArrayList()
            for (i in 0 until Camera.getNumberOfCameras()) {
                var description: String
                val c = Camera.open(i)
                val p = c.parameters
                val previewSize = p.previewSize
                val width = previewSize.width
                val height = previewSize.height
                val info = Camera.CameraInfo()
                Camera.getCameraInfo(i, info)
                description = java.text.MessageFormat.format(
                        context.getString(R.string.text_camera_pattern),
                        i,
                        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) context.getString(R.string.text_front) else context.getString(R.string.text_back),
                        info.orientation,
                        width,
                        height)
                c.stopPreview()
                c.release()
                cameraList.add(description)
            }
            return cameraList
        }
    }
}
