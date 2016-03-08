package ph.com.gs3.loyaltycustomer.models.values.objects;

/**
 * Created by Bryan-PC on 03/03/2016.
 */
public class SalesHasReward {

    private Long id;
    private Long reward_id;
    private String sales_transaction_number;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReward_id() {
        return reward_id;
    }

    public void setReward_id(Long reward_id) {
        this.reward_id = reward_id;
    }

    public String getSales_transaction_number() {
        return sales_transaction_number;
    }

    public void setSales_transaction_number(String sales_transaction_number) {
        this.sales_transaction_number = sales_transaction_number;
    }
}
