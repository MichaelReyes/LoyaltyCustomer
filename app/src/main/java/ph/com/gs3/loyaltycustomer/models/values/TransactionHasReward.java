package ph.com.gs3.loyaltycustomer.models.values;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bryan-PC on 04/02/2016.
 */
public class TransactionHasReward implements Parcelable {

    private int id;
    private int sales_id;
    private int rewards_id;

    public TransactionHasReward() {
        super();
    }

    private TransactionHasReward(Parcel in) {
        super();
        this.id = in.readInt();
        this.sales_id = in.readInt();
        this.rewards_id = in.readInt();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSales_id() {
        return sales_id;
    }

    public void setSales_id(int sales_id) {
        this.sales_id = sales_id;
    }

    public int getRewards_id() {
        return rewards_id;
    }

    public void setRewards_id(int rewards_id) {
        this.rewards_id = rewards_id;
    }

    @Override
    public String toString() {
        return "SALES HAS REWARDS INFO [sales_id=" + sales_id + ", rewards_id=" + rewards_id + " ]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + sales_id;
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
        TransactionHasReward other = (TransactionHasReward) obj;
        if (sales_id != other.sales_id)
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
        parcel.writeInt(getSales_id());
        parcel.writeInt(getRewards_id());
    }

    public static final Creator<TransactionHasReward> CREATOR = new Creator<TransactionHasReward>() {
        public TransactionHasReward createFromParcel(Parcel in) {
            return new TransactionHasReward(in);
        }

        public TransactionHasReward[] newArray(int size) {
            return new TransactionHasReward[size];
        }
    };
}
