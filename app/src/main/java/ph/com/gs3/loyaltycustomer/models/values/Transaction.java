package ph.com.gs3.loyaltycustomer.models.values;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bryan-PC on 04/02/2016.
 */
public class Transaction implements Parcelable {

    private int id;
    private int store_id;
    private int customer_id;
    private float amount;
    private float total_discount;
    private String transaction_date;
    private int is_synced;


    public Transaction() {
        super();
    }

    private Transaction(Parcel in) {
        super();
        this.id = in.readInt();
        this.store_id = in.readInt();
        this.customer_id = in.readInt();
        this.amount = in.readFloat();
        this.total_discount = in.readFloat();
        this.transaction_date = in.readString();
        this.is_synced = in.readInt();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStore_id() {
        return store_id;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getTotal_discount() {
        return total_discount;
    }

    public void setTotal_discount(float total_discount) {
        this.total_discount = total_discount;
    }

    public String getTransaction_date() {
        return transaction_date;
    }

    public void setTransaction_date(String transaction_date) {
        this.transaction_date = transaction_date;
    }

    public int getIs_synced() {
        return is_synced;
    }

    public void setIs_synced(int is_synced) {
        this.is_synced = is_synced;
    }

    @Override
    public String toString() {
        return "SALES INFO [id=" + id + ", store_id=" + store_id + ", customer_id=" + customer_id +
                ", amount=" + amount + ", total_discount=" + total_discount +
                ", transaction_date=" + transaction_date + ", is_synced=" + is_synced + " ]";
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
        Transaction other = (Transaction) obj;
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
        parcel.writeInt(getStore_id());
        parcel.writeInt(getCustomer_id());
        parcel.writeFloat(getAmount());
        parcel.writeFloat(getTotal_discount());
        parcel.writeString(getTransaction_date());
        parcel.writeInt(getIs_synced());
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };
}
