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
public class StAddmeasure extends AbstractRoutine<Object> {

    private static final long serialVersionUID = 1656954268;

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static final Parameter<Object> RETURN_VALUE = Internal.createParameter("RETURN_VALUE", org.jooq.impl.DefaultDataType.getDefaultDataType("\"public\".\"geometry\""), false, false);

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static final Parameter<Object> _1 = Internal.createParameter("_1", org.jooq.impl.DefaultDataType.getDefaultDataType("\"public\".\"geometry\""), false, true);

    /**
     * The parameter <code>st_addmeasure._2</code>.
     */
    public static final Parameter<Double> _2 = Internal.createParameter("_2", org.jooq.impl.SQLDataType.DOUBLE, false, true);

    /**
     * The parameter <code>st_addmeasure._3</code>.
     */
    public static final Parameter<Double> _3 = Internal.createParameter("_3", org.jooq.impl.SQLDataType.DOUBLE, false, true);

    /**
     * Create a new routine call instance
     */
    public StAddmeasure() {
        super("st_addmeasure", DefaultSchema.DEFAULT_SCHEMA, org.jooq.impl.DefaultDataType.getDefaultDataType("\"public\".\"geometry\""));

        setReturnParameter(RETURN_VALUE);
        addInParameter(_1);
        addInParameter(_2);
        addInParameter(_3);
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
     * Set the <code>_2</code> parameter IN value to the routine
     */
    public void set__2(Double value) {
        setValue(_2, value);
    }

    /**
     * Set the <code>_2</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void set__2(Field<Double> field) {
        setField(_2, field);
    }

    /**
     * Set the <code>_3</code> parameter IN value to the routine
     */
    public void set__3(Double value) {
        setValue(_3, value);
    }

    /**
     * Set the <code>_3</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void set__3(Field<Double> field) {
        setField(_3, field);
    }
}
