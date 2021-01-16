/*
 * This file is generated by jOOQ.
 */
package com.matt.nocom.server.postgres.codegen.routines;


import com.matt.nocom.server.postgres.codegen.DefaultSchema;

import org.jooq.Field;
import org.jooq.Parameter;
import org.jooq.impl.AbstractRoutine;
import org.jooq.impl.Internal;



/**
 * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
 */
@java.lang.Deprecated
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class StSetsrid2 extends AbstractRoutine<Object> {

    private static final long serialVersionUID = 1139843646;

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static final Parameter<Object> RETURN_VALUE = Internal.createParameter("RETURN_VALUE", org.jooq.impl.DefaultDataType.getDefaultDataType("\"public\".\"geography\""), false, false);

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static final Parameter<Object> GEOG = Internal.createParameter("geog", org.jooq.impl.DefaultDataType.getDefaultDataType("\"public\".\"geography\""), false, false);

    /**
     * The parameter <code>st_setsrid.srid</code>.
     */
    public static final Parameter<Integer> SRID = Internal.createParameter("srid", org.jooq.impl.SQLDataType.INTEGER, false, false);

    /**
     * Create a new routine call instance
     */
    public StSetsrid2() {
        super("st_setsrid", DefaultSchema.DEFAULT_SCHEMA, org.jooq.impl.DefaultDataType.getDefaultDataType("\"public\".\"geography\""));

        setReturnParameter(RETURN_VALUE);
        addInParameter(GEOG);
        addInParameter(SRID);
        setOverloaded(true);
    }

    /**
     * Set the <code>geog</code> parameter IN value to the routine
     */
    public void setGeog(Object value) {
        setValue(GEOG, value);
    }

    /**
     * Set the <code>geog</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setGeog(Field<Object> field) {
        setField(GEOG, field);
    }

    /**
     * Set the <code>srid</code> parameter IN value to the routine
     */
    public void setSrid(Integer value) {
        setValue(SRID, value);
    }

    /**
     * Set the <code>srid</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setSrid(Field<Integer> field) {
        setField(SRID, field);
    }
}
