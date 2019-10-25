/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ntnuerc.android.halloweencamera

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ntnuerc.android.halloweencamera.bluetooth.BluetoothConnectionService
import com.ntnuerc.android.halloweencamera.bluetooth.SPPBluetoothEventListener

class CameraActivity : AppCompatActivity() {

    private val bluetoothConnectionService : BluetoothConnectionService =
            BluetoothConnectionService(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        val sppBluetoothEventListener = SPPBluetoothEventListener( bluetoothConnectionService)
        bluetoothConnectionService.setBluetoothEventListener( sppBluetoothEventListener )
        bluetoothConnectionService.discoverDevices()

        val frag = Camera2VideoFragment.newInstance()
        savedInstanceState ?: supportFragmentManager.beginTransaction()
                    .replace(R.id.container, frag)
                    .commit()
    }

}
