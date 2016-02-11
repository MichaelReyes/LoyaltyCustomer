package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class LoyaltyCustomerDAOGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, "ph.com.gs3.loyaltycustomer.models.sqlite.dao");

        Entity transaction = schema.addEntity("Transaction");
        transaction.addIdProperty().autoincrement();
        transaction.addLongProperty("store_sales_id");
        transaction.addLongProperty("store_id");
        transaction.addLongProperty("customer_id");
        transaction.addFloatProperty("amount");
        transaction.addFloatProperty("total_discount");
        transaction.addDateProperty("transaction_date");

        Entity transationProduct = schema.addEntity("TransactionProduct");
        transationProduct.addIdProperty().autoincrement();
        transationProduct.addLongProperty("sales_id");
        transationProduct.addLongProperty("product_id");
        transationProduct.addStringProperty("product_name");
        transationProduct.addFloatProperty("unit_cost");
        transationProduct.addStringProperty("sku");
        transationProduct.addIntProperty("quantity");
        transationProduct.addFloatProperty("sub_total");
        transationProduct.addStringProperty("sale_type");

        new DaoGenerator().generateAll(schema, "../app/src/main/java");

    }

}
