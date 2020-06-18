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
public class StLinecrossingdirection extends AbstractRoutine<Integer> {

    private static final long serialVersionUID = 416987503;

    /**
     * The parameter <code>st_linecrossingdirection.RETURN_VALUE</code>.
     */
    public static final Parameter<Integer> RETURN_VALUE = Internal.createParameter("RETURN_VALUE", org.jooq.impl.SQLDataType.INTEGER, false, false);

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static final Parameter<Object> LINE1 = Internal.createParameter("line1", org.jooq.impl.DefaultDataType.getDefaultDataType("\"public\".\"geometry\""), false, false);

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static final Parameter<Object> LINE2 = Internal.createParameter("line2", org.jooq.impl.DefaultDataType.getDefaultDataType("\"public\".\"geometry\""), false, false);

    /**
     * Create a new routine call instance
     */
    public StLinecrossingdirection() {
        super("st_linecrossingdirection", DefaultSchema.DEFAULT_SCHEMA, org.jooq.impl.SQLDataType.INTEGER);

        setReturnParameter(RETURN_VALUE);
        addInParameter(LINE1);
        addInParameter(LINE2);
    }

    /**
     * Set the <code>line1</code> parameter IN value to the routine
     */
    public void setLine1(Object value) {
        setValue(LINE1, value);
    }

    /**
     * Set the <code>line1</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setLine1(Field<Object> field) {
        setField(LINE1, field);
    }

    /**
     * Set the <code>line2</code> parameter IN value to the routine
     */
    public void setLine2(Object value) {
        setValue(LINE2, value);
    }

    /**
     * Set the <code>line2</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setLine2(Field<Object> field) {
        setField(LINE2, field);
    }
}