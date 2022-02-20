package com.matrimony.common.orika;

import ma.glasnost.orika.BoundMapperFacade;

public class OrikaBoundMapperFacade<S, D> implements OrikaMapperFacade<S, D> {

    private Class<S> sClass;
    private Class<D> dClass;

    public OrikaBoundMapperFacade(Class<S> sClass, Class<D> dClass) {
        this.sClass = sClass;
        this.dClass = dClass;
    }

    private BoundMapperFacade<S, D> getBoundMapperFacade() {
        return getMapperFactory().getMapperFacade(sClass, dClass);
    }

    @Override
    public D writeToD(S source) {
        BoundMapperFacade<S, D> mapperFacade = getBoundMapperFacade();

        D d = mapperFacade.map(source);
        return d;
    }

    @Override
    public S writeToS(D destination) {
        S s = getBoundMapperFacade().mapReverse(destination);

        return s;
    }
}
