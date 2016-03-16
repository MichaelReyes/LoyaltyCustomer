package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class LoyaltyCustomerDAOGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, "ph.com.gs3.loyaltycustomer.models.sqlite.dao");

        Entity transaction = schema.addEntity("Transaction");
        transaction.addIdProperty().autoincrement();
        transaction.addStringProperty("transaction_number");
        transaction.addLongProperty("store_id");
        transaction.addStringProperty("store_name");
        transaction.addLongProperty("customer_id");
        transaction.addFloatProperty("amount");
        transaction.addFloatProperty("total_discount");
        transaction.addDateProperty("transaction_date");

        Entity transationProduct = schema.addEntity("TransactionProduct");
        transationProduct.addIdProperty().autoincrement();
        transationProduct.addStringProperty("sales_transaction_number");
        transationProduct.addLongProperty("product_id");
        transationProduct.addStringProperty("product_name");
        transationProduct.addFloatProperty("unit_cost");
        transationProduct.addStringProperty("sku");
        transationProduct.addIntProperty("quantity");
        transationProduct.addFloatProperty("sub_total");
        transationProduct.addStringProperty("sale_type");

        Entity promoImages = schema.addEntity("PromoImages");
        promoImages.addIdProperty().autoincrement();
        promoImages.addStringProperty("name");
        promoImages.addStringProperty("description");
        promoImages.addStringProperty("image_file");

        Entity promo = schema.addEntity("Promo");
        promo.addIdProperty().autoincrement();
        promo.addStringProperty("name");
        promo.addIntProperty("required_points");
        promo.addIntProperty("required_visit_count");
        promo.addDateProperty("created_at");
        promo.addDateProperty("update_at");
        promo.addIntProperty("is_active");

        Entity reward = schema.addEntity("Reward");
        reward.addIdProperty();
        reward.addStringProperty("reward_condition");
        reward.addIntProperty("condition_product_id");
        reward.addStringProperty("condition");
        reward.addFloatProperty("condition_value");
        reward.addStringProperty("reward_type");
        reward.addStringProperty("reward");
        reward.addStringProperty("reward_value");
        reward.addDateProperty("valid_from");
        reward.addDateProperty("valid_until");
        reward.addDateProperty("created_at");
        reward.addDateProperty("updated_at");

        /*Entity store = schema.addEntity("Store");
        store.addIdProperty();
        store.addStringProperty("device_id");
        store.addStringProperty("name");*/

        Entity store = schema.addEntity("Store");
        store.addIdProperty().autoincrement();
        store.addStringProperty("device_id");
        store.addStringProperty("mac_address");
        store.addStringProperty("name");
        store.addIntProperty("is_active");
        store.addDateProperty("created_at");
        store.addDateProperty("updated_at");

        Entity transactionHasReward = schema.addEntity("TransactionHasReward");
        transactionHasReward.addIdProperty().autoincrement();
        transactionHasReward.addLongProperty("reward_id");
        transactionHasReward.addStringProperty("sales_transaction_number");

        new DaoGenerator().generateAll(schema, "../app/src/main/java");

    }

}
