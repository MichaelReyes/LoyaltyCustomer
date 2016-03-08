package ph.com.gs3.loyaltycustomer.models.values.objects;

import java.util.Date;

/**
 * Created by Bryan-PC on 03/03/2016.
 */
public class Sales {

    private Long id;
    private String transaction_number;
    private Long store_id;
    private Long customer_id;
    private Float amount;
    private Float total_discount;
    private Boolean is_synced;
    private java.util.Date transaction_date;
    private String remarks;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransaction_number() {
        return transaction_number;
    }

    public void setTransaction_number(String transaction_number) {
        this.transaction_number = transaction_number;
    }

    public Long getStore_id() {
        return store_id;
    }

    public void setStore_id(Long store_id) {
        this.store_id = store_id;
    }

    public Long getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(Long customer_id) {
        this.customer_id = customer_id;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Float getTotal_discount() {
        return total_discount;
    }

    public void setTotal_discount(Float total_discount) {
        this.total_discount = total_discount;
    }

    public Boolean getIs_synced() {
        return is_synced;
    }

    public void setIs_synced(Boolean is_synced) {
        this.is_synced = is_synced;
    }

    public Date getTransaction_date() {
        return transaction_date;
    }

    public void setTransaction_date(Date transaction_date) {
        this.transaction_date = transaction_date;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
