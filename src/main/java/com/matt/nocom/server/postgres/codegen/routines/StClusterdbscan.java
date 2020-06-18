/*
 * This file is generated by jOOQ.
 */
package com.matt.nocom.server.postgres.codegen.routines;


import com.matt.nocom.server.postgres.codegen.DefaultSchema;
import org.jooq.Field;
import org.jooq.Parameter;
import org.jooq.impl.AbstractRoutine;
import org.jooq.impl.Internal;

import javax.annotation.processing.Generated;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.3"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class StClusterdbscan extends AbstractRoutine<Integer> {

    private static final long serialVersionUID = 1836049818;

    /**
     * The parameter <code>st_clusterdbscan.RETURN_VALUE</code>.
     */
    public static final Parameter<Integer> RETURN_VALUE = Internal.createParameter("RETURN_VALUE", org.jooq.impl.SQLDataType.INTEGER, false, false);

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static final Parameter<Object> _1 = Internal.createParameter("_1", org.jooq.impl.DefaultDataType.getDefaultDataType("\"public\".\"geometry\""), false, true);

    /**
     * The parameter <code>st_clusterdbscan.eps</code>.
     */
    public static final Parameter<Double> EPS = Internal.createParameter("eps", org.jooq.impl.SQLDataType.DOUBLE, false, false);

    /**
     * The parameter <code>st_clusterdbscan.minpoints</code>.
     */
    public static final Parameter<Integer> MINPOINTS = Internal.createParameter("minpoints", org.jooq.impl.SQLDataType.INTEGER, false, false);

    /**
     * Create a new routine call instance
     */
    public StClusterdbscan() {
        super("st_clusterdbscan", DefaultSchema.DEFAULT_SCHEMA, org.jooq.impl.SQLDataType.INTEGER);

        setReturnParameter(RETURN_VALUE);
        addInParameter(_1);
        addInParameter(EPS);
        addInParameter(MINPOINTS);
    }

    /**
     * Set the <code>_1</code> parameter IN value to the routine
     */
    public void set__1(Object value) {
        setValue(_1, value);
    }

    /**
     * Set the <code>_1</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void set__1(Field<Object> field) {
        setField(_1, field);
    }

    /**
     * Set the <code>eps</code> parameter IN value to the routine
     */
    public void setEps(Double value) {
        setValue(EPS, value);
    }

    /**
     * Set the <code>eps</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setEps(Field<Double> field) {
        setField(EPS, field);
    }

    /**
     * Set the <code>minpoints</code> parameter IN value to the routine
     */
    public void setMinpoints(Integer value) {
        setValue(MINPOINTS, value);
    }

    /**
     * Set the <code>minpoints</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setMinpoints(Field<Integer> field) {
        setField(MINPOINTS, field);
    }
}
