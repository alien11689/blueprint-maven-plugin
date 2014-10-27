package org.apache.aries.blueprint.plugin.model;

import java.util.HashMap;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class SpringTransactionFactory {
    private static HashMap<Propagation, String> txTypeNames;

    static {
        txTypeNames = new HashMap<Propagation, String>();
        txTypeNames.put(Propagation.REQUIRED, TransactionalDef.TYPE_REQUIRED);
        txTypeNames.put(Propagation.REQUIRES_NEW, TransactionalDef.TYPE_REQUIRES_NEW);
    }

    TransactionalDef create(Class<?> clazz) {
        Transactional transactional = clazz.getAnnotation(Transactional.class);
        return transactional != null ? 
                new TransactionalDef("*", txTypeNames.get(transactional.propagation())) : null;
    }
}
