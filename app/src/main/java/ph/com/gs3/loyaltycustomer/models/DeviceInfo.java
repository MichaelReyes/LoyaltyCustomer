package ph.com.gs3.loyaltycustomer.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Ervinne Sodusta on 8/18/2015.
 */
public class DeviceInfo implements Serializable {

    private String ownerDisplayName;
    private Type type;

    public enum Type {
        RETAILER, CUSTOMER
    }

    public String serialize() throws JSONException {
        JSONObject json = new JSONObject();

        json.put("O", ownerDisplayName);
        json.put("T", type == Type.RETAILER ? 1 : 0);

        return json.toString();
    }

    public static DeviceInfo unserialize(String serializedDeviceInfo) throws JSONException {

        if (serializedDeviceInfo == null) {
            return null;
        }

        JSONObject json = new JSONObject(serializedDeviceInfo);
        DeviceInfo deviceInfo = new DeviceInfo();

        deviceInfo.setOwnerDisplayName(json.getString("O"));
        deviceInfo.setType(json.getInt("T") == 1 ? Type.RETAILER : Type.CUSTOMER);

        return deviceInfo;

    }

    public String getOwnerDisplayName() {
        return ownerDisplayName;
    }

    public void setOwnerDisplayName(String ownerDisplayName) {
        this.ownerDisplayName = ownerDisplayName;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
