package app.src.com.walletapp.wifip2p.beans;

/**
 * Created by SONY on 3/7/2018.
 * Device Bean class to input data into userdata table
 */

public class DeviceData {

    String deviceAddress,deviceName,deviceStatus;


    public DeviceData(String deviceAddress, String deviceName, String deviceStatus) {
        this.deviceAddress = deviceAddress;
        this.deviceName = deviceName;
        this.deviceStatus = deviceStatus;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }
}
