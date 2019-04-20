package com.cheng.activitystack.adb;

import com.cheng.activitystack.device.Device;
import com.intellij.openapi.application.ApplicationManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DeviceService {
    public void startService() {
        ApplicationManager.getApplication().executeOnPooledThread(() -> new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Process process;
                try {
                    process = Runtime.getRuntime().exec("adb devices -l");
                    successOut(process.getInputStream());
                    process.waitFor();
                    process.exitValue();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000));
    }

    private void successOut(InputStream in) throws IOException {
        List<Device> devices = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        reader.readLine();
        while ((line = reader.readLine()) != null) {
            if (line.contains("device")) {
                String id = line.substring(0, line.indexOf(" "));
                String name = line.substring(line.indexOf("model:") + 6, line.indexOf("device:"));
                devices.add(new Device(name, id));
            }
        }
        if (devicesListener != null) {
            ApplicationManager.getApplication().invokeLater(() -> devicesListener.findDevices(devices));
        }
    }

    private DevicesListener devicesListener;

    public void setDevicesListener(DevicesListener devicesListener) {
        this.devicesListener = devicesListener;
    }

    public interface DevicesListener {
        void findDevices(List<Device> devices);
    }
}
