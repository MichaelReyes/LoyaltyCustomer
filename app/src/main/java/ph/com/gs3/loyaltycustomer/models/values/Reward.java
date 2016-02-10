package ph.com.gs3.loyaltycustomer.models.values;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bryan-PC on 02/02/2016.
 */
public class Reward implements Parcelable {

    private int id;
    private int web_id;
    private String reward_condition;
    private int condition_product_id;
    private String condition;
    private String condition_value;
    private String reward_type;
    private String reward;
    private String reward_value;
    private String valid_from;
    private String valid_until;
    private int is_active;
    private String created_at;
    private String updated_at;


    public Reward() {
        super();
    }

    private Reward(Parcel in) {
        super();
        this.id = in.readInt();
        this.web_id = in.readInt();
        this.reward_condition = in.readString();
        this.condition_product_id = in.readInt();
        this.condition = in.readString();
        this.condition_value = in.readString();
        this.reward_type = in.readString();
        this.reward = in.readString();
        this.reward_value = in.readString();
        this.valid_from = in.readString();
        this.valid_until = in.readString();
        this.is_active = in.readInt();
        this.created_at = in.readString();
        this.updated_at = in .readString();

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWeb_id() {
        return web_id;
    }

    public void setWeb_id(int web_id) {
        this.web_id = web_id;
    }

    public String getReward_condition() {
        return reward_condition;
    }

    public void setReward_condition(String reward_condition) {
        this.reward_condition = reward_condition;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getCondition_value() {
        return condition_value;
    }

    public void setCondition_value(String condition_value) {
        this.condition_value = condition_value;
    }

    public String getReward_type() {
        return reward_type;
    }

    public void setReward_type(String reward_type) {
        this.reward_type = reward_type;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getReward_value() {
        return reward_value;
    }

    public void setReward_value(String reward_value) {
        this.reward_value = reward_value;
    }

    public String getValid_from() {
        return valid_from;
    }

    public void setValid_from(String valid_from) {
        this.valid_from = valid_from;
    }

    public String getValid_until() {
        return valid_until;
    }

    public void setValid_until(String valid_until) {
        this.valid_until = valid_until;
    }

    public int getIs_active() {
        return is_active;
    }

    public void setIs_active(int is_active) {
        this.is_active = is_active;
    }

    public int getCondition_product_id() {
        return condition_product_id;
    }

    public void setCondition_product_id(int condition_product_id) {
        this.condition_product_id = condition_product_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    @Override
    public String toString() {
        return "REWARD INFO [id=" + id + ", web_id=" + web_id + ", reward_condition=" + reward_condition +
                ", condition_product_id=" + condition_product_id + ", condition=" + condition +
                ", condition_value=" + condition_value + ", reward_type=" + reward_type + ", reward=" + reward +
                ", reward_value=" + reward_value + ", valid_from=" + valid_from + ", valid_until=" + valid_until +
                ", is_active" + is_active + ", created_at=" + created_at + ", updated_at=" + updated_at + " ]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Reward other = (Reward) obj;
        if (id != other.id)
            return false;
        return true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(getId());
        parcel.writeInt(getWeb_id());
        parcel.writeString(getReward_condition());
        parcel.writeInt(getCondition_product_id());
        parcel.writeString(getCondition());
        parcel.writeString(getCondition_value());
        parcel.writeString(getReward_type());
        parcel.writeString(getReward());
        parcel.writeString(getReward_value());
        parcel.writeString(getValid_from());
        parcel.writeString(getValid_until());
        parcel.writeInt(getIs_active());
        parcel.writeString(getCreated_at());
        parcel.writeString(getUpdated_at());

    }

    public static final Creator<Reward> CREATOR = new Creator<Reward>() {
        public Reward createFromParcel(Parcel in) {
            return new Reward(in);
        }

        public Reward[] newArray(int size) {
            return new Reward[size];
        }
    };

}
