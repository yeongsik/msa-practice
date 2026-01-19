package com.common.util.id;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class SnowflakeIdentifierGenerator implements IdentifierGenerator {

    private static final SnowflakeIdGenerator snowflakeIdGenerator = new SnowflakeIdGenerator();

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return snowflakeIdGenerator.nextId();
    }
}
