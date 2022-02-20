package com.matrimony.common.orika;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

public interface OrikaMapperFacade<S, D> {

    default MapperFactory getMapperFactory(){
        return new DefaultMapperFactory.Builder().mapNulls(false).build();
    }

    D writeToD(S source);

    S writeToS(D destination);
}
