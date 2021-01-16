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
public class StGeneratepoints1 extends AbstractRoutine<Object> {

    private static final long serialVersionUID = -1957948729;

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static final Parameter<Object> RETURN_VALUE = Internal.createParameter("RETURN_VALUE", org.jooq.impl.DefaultDataType.getDefaultDataType("\"public\".\"geometry\""), false, false);

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static final Parameter<Object> AREA = Internal.createParameter("area", org.jooq.impl.DefaultDataType.getDefaultDataType("\"public\".\"geometry\""), false, false);

    /**
     * The parameter <code>st_generatepoints.npoints</code>.
     */
    public static final Parameter<Integer> NPOINTS = Internal.createParameter("npoints", org.jooq.impl.SQLDataType.INTEGER, false, false);

    /**
     * Create a new routine call instance
     */
    public StGeneratepoints1() {
        super("st_generatepoints", DefaultSchema.DEFAULT_SCHEMA, org.jooq.impl.DefaultDataType.getDefaultDataType("\"public\".\"geometry\""));

        setReturnParameter(RETURN_VALUE);
        addInParameter(AREA);
        addInParameter(NPOINTS);
        setOverloaded(true);
    }

    /**
     * Set the <code>area</code> parameter IN value to the routine
     */
    public void setArea(Object value) {
        setValue(AREA, value);
    }

    /**
     * Set the <code>area</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setArea(Field<Object> field) {
        setField(AREA, field);
    }

    /**
     * Set the <code>npoints</code> parameter IN value to the routine
     */
    public void setNpoints(Integer value) {
        setValue(NPOINTS, value);
    }

    /**
     * Set the <code>npoints</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setNpoints(Field<Integer> field) {
        setField(NPOINTS, field);
    }
}
