package org.apache.aries.blueprint.plugin.model;

import java.util.HashMap;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

public class JavaxTransactionFactory {
    private static HashMap<TxType, String> txTypeNames;

    static {
        txTypeNames = new HashMap<TxType, String>();
        txTypeNames.put(TxType.REQUIRED, TransactionalDef.TYPE_REQUIRED);
        txTypeNames.put(TxType.REQUIRES_NEW, TransactionalDef.TYPE_REQUIRES_NEW);
    }
    
    TransactionalDef create(Class<?> clazz) {
        Transactional transactional = clazz.getAnnotation(Transactional.class);
        return transactional != null ? 
                new TransactionalDef("*", txTypeNames.get(transactional.value())) : null;
    }
}
