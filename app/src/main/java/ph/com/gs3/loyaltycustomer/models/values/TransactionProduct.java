package ph.com.gs3.loyaltycustomer.models.values;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bryan-PC on 29/01/2016.
 */
public class TransactionProduct implements Parcelable {

    private int id;
    private int sales_id;
    private int product_id;
    private int quantity;
    private float sub_total;
    private String sale_type;
    private String productName;

    public TransactionProduct() {
        super();
    }

    private TransactionProduct(Parcel in) {
        super();
        this.id = in.readInt();
        this.sales_id = in.readInt();
        this.product_id = in.readInt();
        this.quantity = in.readInt();
        this.sub_total = in.readFloat();
        this.sale_type = in.readString();
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

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getSub_total() {
        return sub_total;
    }

    public void setSub_total(float sub_total) {
        this.sub_total = sub_total;
    }

    public String getSale_type() {
        return sale_type;
    }

    public void setSale_type(String sale_type) {
        this.sale_type = sale_type;
    }

    @Override
    public String toString() {
        return "SALES PRODUCT INFO [id=" + id + ", sales_id=" + sales_id + ", product_id=" + product_id +
                ", quantity=" + quantity + ", sub_total=" + sub_total + ", sale_type=" + sale_type + " ]";
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
        TransactionProduct other = (TransactionProduct) obj;
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
        parcel.writeInt(getSales_id());
        parcel.writeInt(getProduct_id());
        parcel.writeInt(getQuantity());
        parcel.writeFloat(getSub_total());
        parcel.writeString(getSale_type());
    }

    public static final Creator<TransactionProduct> CREATOR = new Creator<TransactionProduct>() {
        public TransactionProduct createFromParcel(Parcel in) {
            return new TransactionProduct(in);
        }

        public TransactionProduct[] newArray(int size) {
            return new TransactionProduct[size];
        }
    };
}
