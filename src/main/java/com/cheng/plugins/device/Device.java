package com.cheng.plugins.device;

public class Device {
    private final String serialNumber;
    private final String model;

    public Device(String serialNumber, String model) {
        this.serialNumber = serialNumber;
        this.model = model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getModel() {
        return model;
    }

    @Override
    public String toString() {
        return "<html>\n" +
                model + "\n" +
                "<nobr style=\"color:gray;margin-left:20px\">\n" +
                serialNumber + "</nobr>\n" +
                "</html>";
    }
}
