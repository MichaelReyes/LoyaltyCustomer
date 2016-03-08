package ph.com.gs3.loyaltycustomer.models.sqlite.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import ph.com.gs3.loyaltycustomer.models.sqlite.dao.Transaction;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.TransactionProduct;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.PromoImages;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.Promo;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.Reward;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.Store;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.TransactionHasReward;

import ph.com.gs3.loyaltycustomer.models.sqlite.dao.TransactionDao;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.TransactionProductDao;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.PromoImagesDao;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.PromoDao;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.RewardDao;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.StoreDao;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.TransactionHasRewardDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig transactionDaoConfig;
    private final DaoConfig transactionProductDaoConfig;
    private final DaoConfig promoImagesDaoConfig;
    private final DaoConfig promoDaoConfig;
    private final DaoConfig rewardDaoConfig;
    private final DaoConfig storeDaoConfig;
    private final DaoConfig transactionHasRewardDaoConfig;

    private final TransactionDao transactionDao;
    private final TransactionProductDao transactionProductDao;
    private final PromoImagesDao promoImagesDao;
    private final PromoDao promoDao;
    private final RewardDao rewardDao;
    private final StoreDao storeDao;
    private final TransactionHasRewardDao transactionHasRewardDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        transactionDaoConfig = daoConfigMap.get(TransactionDao.class).clone();
        transactionDaoConfig.initIdentityScope(type);

        transactionProductDaoConfig = daoConfigMap.get(TransactionProductDao.class).clone();
        transactionProductDaoConfig.initIdentityScope(type);

        promoImagesDaoConfig = daoConfigMap.get(PromoImagesDao.class).clone();
        promoImagesDaoConfig.initIdentityScope(type);

        promoDaoConfig = daoConfigMap.get(PromoDao.class).clone();
        promoDaoConfig.initIdentityScope(type);

        rewardDaoConfig = daoConfigMap.get(RewardDao.class).clone();
        rewardDaoConfig.initIdentityScope(type);

        storeDaoConfig = daoConfigMap.get(StoreDao.class).clone();
        storeDaoConfig.initIdentityScope(type);

        transactionHasRewardDaoConfig = daoConfigMap.get(TransactionHasRewardDao.class).clone();
        transactionHasRewardDaoConfig.initIdentityScope(type);

        transactionDao = new TransactionDao(transactionDaoConfig, this);
        transactionProductDao = new TransactionProductDao(transactionProductDaoConfig, this);
        promoImagesDao = new PromoImagesDao(promoImagesDaoConfig, this);
        promoDao = new PromoDao(promoDaoConfig, this);
        rewardDao = new RewardDao(rewardDaoConfig, this);
        storeDao = new StoreDao(storeDaoConfig, this);
        transactionHasRewardDao = new TransactionHasRewardDao(transactionHasRewardDaoConfig, this);

        registerDao(Transaction.class, transactionDao);
        registerDao(TransactionProduct.class, transactionProductDao);
        registerDao(PromoImages.class, promoImagesDao);
        registerDao(Promo.class, promoDao);
        registerDao(Reward.class, rewardDao);
        registerDao(Store.class, storeDao);
        registerDao(TransactionHasReward.class, transactionHasRewardDao);
    }
    
    public void clear() {
        transactionDaoConfig.getIdentityScope().clear();
        transactionProductDaoConfig.getIdentityScope().clear();
        promoImagesDaoConfig.getIdentityScope().clear();
        promoDaoConfig.getIdentityScope().clear();
        rewardDaoConfig.getIdentityScope().clear();
        storeDaoConfig.getIdentityScope().clear();
        transactionHasRewardDaoConfig.getIdentityScope().clear();
    }

    public TransactionDao getTransactionDao() {
        return transactionDao;
    }

    public TransactionProductDao getTransactionProductDao() {
        return transactionProductDao;
    }

    public PromoImagesDao getPromoImagesDao() {
        return promoImagesDao;
    }

    public PromoDao getPromoDao() {
        return promoDao;
    }

    public RewardDao getRewardDao() {
        return rewardDao;
    }

    public StoreDao getStoreDao() {
        return storeDao;
    }

    public TransactionHasRewardDao getTransactionHasRewardDao() {
        return transactionHasRewardDao;
    }

}
