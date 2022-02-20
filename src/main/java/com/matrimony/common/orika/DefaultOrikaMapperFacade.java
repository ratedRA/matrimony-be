package com.matrimony.common.orika;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;

public class DefaultOrikaMapperFacade<S, D> implements OrikaMapperFacade<S, D>{
    private Class<S> sClass;
    private Class<D> dClass;

    public DefaultOrikaMapperFacade(Class<S> sClass, Class<D> dClass) {
        this.sClass = sClass;
        this.dClass = dClass;
    }

    protected MapperFacade getMapperFacade() {
        MapperFactory mapperFactory = getMapperFactory();
        mapperFactory.classMap(sClass, dClass).byDefault();
        return mapperFactory.getMapperFacade();
    }

    @Override
    public D writeToD(S source) {
        MapperFacade mapperFacade = getMapperFacade();

        D d = mapperFacade.map(source, dClass);
        return d;
    }

    @Override
    public S writeToS(D destination) {
        MapperFacade mapperFacade = getMapperFacade();

        S s = mapperFacade.map(destination, sClass);
        return s;
    }
}
