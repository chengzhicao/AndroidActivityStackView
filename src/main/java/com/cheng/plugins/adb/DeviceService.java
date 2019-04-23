/*
 * Copyright 2019 ChengzhiCao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cheng.plugins.adb;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.android.sdk.AndroidSdkUtils;

import java.util.*;

public class DeviceService {
    private AndroidDebugBridge debugBridge;
    private Project project;

    public DeviceService(Project project) {
        this.project = project;
        debugBridge = AndroidSdkUtils.getDebugBridge(project);
    }

    public void startService() {
        ApplicationManager.getApplication().executeOnPooledThread(() -> new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ApplicationManager.getApplication().invokeLater(() -> task());
            }
        }, 0, 1000));
    }

    private void task() {
        if (debugBridge != null) {
            IDevice[] devices = debugBridge.getDevices();
            if (devicesListener != null) {
                devicesListener.findDevices(devices);
            }
        } else {
            debugBridge = AndroidSdkUtils.getDebugBridge(project);
            if (devicesListener != null) {
                devicesListener.adbNotFind();
            }
        }
    }

    private DevicesListener devicesListener;

    public void setDevicesListener(DevicesListener devicesListener) {
        this.devicesListener = devicesListener;
    }

    public interface DevicesListener {
        void findDevices(IDevice[] devices);

        void adbNotFind();
    }
}
