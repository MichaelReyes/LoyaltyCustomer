package ph.com.gs3.loyaltycustomer.models.tasks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ph.com.gs3.loyaltycustomer.LoyaltyCustomerApplication;
import ph.com.gs3.loyaltycustomer.models.Customer;
import ph.com.gs3.loyaltycustomer.models.WifiDirectConnectivityState;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.Reward;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.RewardDao;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.Store;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.StoreDao;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.Transaction;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.TransactionDao;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.TransactionHasReward;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.TransactionHasRewardDao;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.TransactionProduct;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.TransactionProductDao;

/**
 * Created by Bryan-PC on 03/03/2016.
 */
public class AcquireTransactionsTask extends AsyncTask<Void,AcquireTransactionsTask.ProgressType,Void> {

    public static final String TAG = AcquireTransactionsTask.class.getSimpleName();

    public enum ProgressType {
       CUSTOMER_ID, TRANSACTION_RECORDS_COUNT, TRANSACTIONS, REWARDS, PRODUCTS, CLAIMED_REWARDS
    }

    private int port;
    private Context context;

    private AcquireTransactionsTaskListener acquireTransactionsTaskListener;

    private Customer customer;

    private List<Transaction> transactionsRecieved;
    private List<Reward> rewardsRecieved = new ArrayList<>();
    private List<TransactionHasReward> transactionRewardsRecieved = new ArrayList<>();

    public AcquireTransactionsTask(int port, Context context, AcquireTransactionsTaskListener acquireTransactionsTaskListener) {
        this.port = port;
        this.context = context;
        this.acquireTransactionsTaskListener = acquireTransactionsTaskListener;

        customer = Customer.getDeviceRetailerFromSharedPreferences(context);
        transactionsRecieved = new ArrayList<>();
        rewardsRecieved = new ArrayList<>();
    }

    @Override
    protected Void doInBackground(Void... params) {

        Log.d(TAG, "AcquireTransactionsTask STARTED");

        WifiDirectConnectivityState connectivityState = WifiDirectConnectivityState.getInstance();

        ServerSocket serverSocket = null;
        Socket socket;
        try {
            if (connectivityState.isServer()) {
                serverSocket = new ServerSocket(port);
                socket = serverSocket.accept();
            } else {
                socket = new Socket();
                socket.connect(new InetSocketAddress(connectivityState.getGroupOwnerAddress(), port));
            }

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            if (!connectivityState.isServer()) {
                sendClientReadyConfirmation(dataOutputStream);
            }

            sendCustomerId(dataOutputStream);
            publishProgress(ProgressType.CUSTOMER_ID);
            sendTransactionRecordsCount(dataOutputStream);
            publishProgress(ProgressType.TRANSACTION_RECORDS_COUNT);
            acquireSales(dataOutputStream, dataInputStream);
            publishProgress(ProgressType.TRANSACTIONS);
            acquireRewards(dataInputStream);
            publishProgress(ProgressType.REWARDS);

            /*if(transactionRewardsRecieved.size() > 0){

            }*/

            dataOutputStream.close();
            dataInputStream.close();
            socket.close();

            if (serverSocket != null) {
                serverSocket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(ProgressType... progressTypes) {
        super.onProgressUpdate(progressTypes);

        if (progressTypes[0] == ProgressType.CUSTOMER_ID) {
            acquireTransactionsTaskListener.onCustomerIdSent();
        } else if (progressTypes[0] == ProgressType.TRANSACTION_RECORDS_COUNT) {
            acquireTransactionsTaskListener.onTransactionRecordCountSent();
        } else if (progressTypes[0] == ProgressType.TRANSACTIONS) {
            acquireTransactionsTaskListener.onSalesRecieved(transactionsRecieved);
        } else if (progressTypes[0] == ProgressType.REWARDS) {
            acquireTransactionsTaskListener.onRewardsRecieved(rewardsRecieved);
        }

    }

    private void awaitClientReadyConfirmation(DataInputStream dataInputStream) throws IOException {

        String clientReadyConfirmation = dataInputStream.readUTF();
        Log.v(TAG, clientReadyConfirmation);

    }

    private void sendClientReadyConfirmation(DataOutputStream dataOutputStream) throws IOException {

        dataOutputStream.writeUTF("CLIENT_READY");
        dataOutputStream.flush();

    }


    private void sendCustomerId(DataOutputStream dataOutputStream) throws IOException {

        dataOutputStream.writeUTF("CUSTOMER_ID");
        dataOutputStream.writeUTF(String.valueOf(customer.getCustomerId()));

    }

    private void sendTransactionRecordsCount(DataOutputStream dataOutputStream) throws  IOException{

        dataOutputStream.writeUTF("CUSTOMER_TRANSACTION_RECORD_COUNT");

        TransactionDao transactionDao = LoyaltyCustomerApplication.getSession().getTransactionDao();

        List<Transaction> transactions = transactionDao.loadAll();

        dataOutputStream.writeUTF(String.valueOf(transactions.size()));

    }

    private void sendClaimedRewards(DataOutputStream dataOutputStream){

        AlertDialog.Builder builderDialog = new AlertDialog.Builder(context);
        builderDialog.setTitle("Claim Rewards");

        List<String> rewards = new ArrayList<>();

        RewardDao rewardDao = LoyaltyCustomerApplication.getSession().getRewardDao();

        for(TransactionHasReward transactionHasReward : transactionRewardsRecieved){

           List<Reward> rewardList =
                   rewardDao
                           .queryBuilder()
                           .where(
                                RewardDao.Properties.Id.eq(
                                        transactionHasReward.getReward_id()
                                )
                           ).list();

            for(Reward reward : rewardList){

                rewards.add(reward.getReward());

            }

        }

        CharSequence[] dialogList=  rewards.toArray(new CharSequence[rewards.size()]);
        int count = transactionRewardsRecieved.size();
        boolean[] isChecked = new boolean[count];

        // Creating multiple selection by using setMutliChoiceItem method
        builderDialog.setMultiChoiceItems(dialogList, isChecked,
                new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton, boolean isChecked) {
                    }
                });



    }

    private void acquireSales(DataOutputStream dataOutputStream, DataInputStream dataInputStream) throws IOException {
        Gson gson=  new GsonBuilder().setDateFormat("EEE MMM d HH:mm:ss zzz yyyy").create();

        TransactionDao transactionDao = LoyaltyCustomerApplication.getSession().getTransactionDao();
        TransactionHasRewardDao transactionHasRewardDao = LoyaltyCustomerApplication.getSession().getTransactionHasRewardDao();
        TransactionProductDao transactionProductDao = LoyaltyCustomerApplication.getSession().getTransactionProductDao();

        String storeName;

        dataOutputStream.writeUTF("SALES");
        String response;
        String storeJsonString = dataInputStream.readUTF();
        Log.d(TAG, "STORE : " + storeJsonString);
        Store store = gson.fromJson(storeJsonString, Store.class);
        storeName = store.getName();

        StoreDao storeDao = LoyaltyCustomerApplication.getSession().getStoreDao();
        storeDao.insertOrReplace(store);

        do {
            response = dataInputStream.readUTF();

            if (!"SALES_END".equals(response)) {

                Log.d(TAG, "RESPONSE : " + response);

                Transaction originalTransaction = gson.fromJson(response, Transaction.class);
                Transaction transaction = cloneTransaction(originalTransaction);
                transaction.setStore_name(storeName);

                transactionDao.insert(transaction);

                dataOutputStream.writeUTF("SALES_RECEIVED");

                String transactionRewardListString = dataInputStream.readUTF();
                TransactionHasReward[] transactionHasRewards =
                        gson.fromJson(transactionRewardListString, TransactionHasReward[].class);

                for (TransactionHasReward transactionHasReward : transactionHasRewards) {

                    Log.v(TAG, "Transaction reward inserted: " +
                            transactionHasReward.getId() + " " +
                            transactionHasReward.getSales_transaction_number() + " " +
                            transactionHasReward.getReward_id());

                    transactionHasRewardDao.insertOrReplace(transactionHasReward);
                    transactionRewardsRecieved.add(transactionHasReward);

                }

                dataOutputStream.writeUTF("SALES_REWARDS_RECEIVED");


                String salesProductListString = dataInputStream.readUTF();
                Log.v(TAG, "sales products: " + salesProductListString);
                TransactionProduct[] transactionProducts =
                        gson.fromJson(
                                salesProductListString,
                                TransactionProduct[].class
                        );

                for(TransactionProduct transactionProduct : transactionProducts){

                    transactionProductDao.insert(cloneTransactionProduct(transactionProduct));

                }

                transactionsRecieved.add(transaction);

                dataOutputStream.writeUTF("SALES_PRODUCTS_RECEIVED");

            }

        } while (!"SALES_END".equals(response));

        List<Transaction> allTransactions = transactionDao.loadAll();

        Log.v(TAG, "=========================================================");
        for (Transaction transaction : allTransactions) {
            Log.v(TAG, "Transaction on db: "
                            + transaction.getId() + " "
                            + transaction.getStore_id() + " "
                            + transaction.getCustomer_id() + " "
                            + transaction.getAmount() + " "
                            + transaction.getTotal_discount() + " "
                            + transaction.getTransaction_date()
            );
        }

        List<TransactionProduct> transactionProducts = transactionProductDao.loadAll();

        Log.v(TAG, "=========================================================");
        for (TransactionProduct transactionProduct : transactionProducts) {
            Log.v(TAG, "Transaction product on db: "
                            + transactionProduct.getId() + " "
                            + transactionProduct.getQuantity() + " "
                            + transactionProduct.getSub_total() + " "
                            + transactionProduct.getProduct_id()
            );
        }

    }

    private void acquireRewards(DataInputStream dataInputStream) throws IOException {
        String preMessage = dataInputStream.readUTF();

        if ("REWARDS".equals(preMessage)) {
            String rewardsJsonString = dataInputStream.readUTF();
            Log.v(TAG, "Acquired Rewards: " + rewardsJsonString);

            Gson gson = new Gson();

            Reward[] rewards = gson.fromJson(rewardsJsonString, Reward[].class);
            rewardsRecieved = Arrays.asList(rewards);
        }
    }


    private Transaction cloneTransaction(Transaction transaction) {
        Transaction clone = new Transaction();

        clone.setTransaction_number(transaction.getTransaction_number());
        clone.setStore_id(transaction.getStore_id());
        clone.setCustomer_id(transaction.getCustomer_id());
        clone.setAmount(transaction.getAmount());
        clone.setTotal_discount(transaction.getTotal_discount());
        clone.setTransaction_date(transaction.getTransaction_date());

        Log.v(TAG, "Sales store id: " + clone.getStore_id());

        return clone;
    }

    private TransactionProduct cloneTransactionProduct(TransactionProduct transactionProduct) {
        TransactionProduct clone = new TransactionProduct();

        clone.setSales_transaction_number(transactionProduct.getSales_transaction_number());
        clone.setSale_type(transactionProduct.getSale_type());
        clone.setSub_total(transactionProduct.getSub_total());
        clone.setSku(transactionProduct.getSku());
        clone.setProduct_id(transactionProduct.getProduct_id());
        clone.setProduct_name(transactionProduct.getProduct_name());
        clone.setQuantity(transactionProduct.getQuantity());
        clone.setUnit_cost(transactionProduct.getUnit_cost());

        return clone;

    }

    public interface AcquireTransactionsTaskListener {

        void onCustomerIdSent();

        void onTransactionRecordCountSent();

        void onSalesRecieved(List<Transaction> transactions);

        void onRewardsRecieved(List<Reward> rewards);

    }

}
