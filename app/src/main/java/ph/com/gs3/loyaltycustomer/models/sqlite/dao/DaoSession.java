package ph.com.gs3.loyaltycustomer.models.sqlite.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import ph.com.gs3.loyaltycustomer.models.sqlite.dao.Transaction;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.TransactionProduct;

import ph.com.gs3.loyaltycustomer.models.sqlite.dao.TransactionDao;
import ph.com.gs3.loyaltycustomer.models.sqlite.dao.TransactionProductDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig transactionDaoConfig;
    private final DaoConfig transactionProductDaoConfig;

    private final TransactionDao transactionDao;
    private final TransactionProductDao transactionProductDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        transactionDaoConfig = daoConfigMap.get(TransactionDao.class).clone();
        transactionDaoConfig.initIdentityScope(type);

        transactionProductDaoConfig = daoConfigMap.get(TransactionProductDao.class).clone();
        transactionProductDaoConfig.initIdentityScope(type);

        transactionDao = new TransactionDao(transactionDaoConfig, this);
        transactionProductDao = new TransactionProductDao(transactionProductDaoConfig, this);

        registerDao(Transaction.class, transactionDao);
        registerDao(TransactionProduct.class, transactionProductDao);
    }
    
    public void clear() {
        transactionDaoConfig.getIdentityScope().clear();
        transactionProductDaoConfig.getIdentityScope().clear();
    }

    public TransactionDao getTransactionDao() {
        return transactionDao;
    }

    public TransactionProductDao getTransactionProductDao() {
        return transactionProductDao;
    }

}