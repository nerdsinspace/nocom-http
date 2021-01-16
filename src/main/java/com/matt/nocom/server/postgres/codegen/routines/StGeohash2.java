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
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class StGeohash2 extends AbstractRoutine<String> {

    private static final long serialVersionUID = 1659990693;

    /**
     * The parameter <code>st_geohash.RETURN_VALUE</code>.
     */
    public static final Parameter<String> RETURN_VALUE = Internal.createParameter("RETURN_VALUE", org.jooq.impl.SQLDataType.CLOB, false, false);

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static final Parameter<Object> GEOG = Internal.createParameter("geog", org.jooq.impl.DefaultDataType.getDefaultDataType("\"public\".\"geography\""), false, false);

    /**
     * The parameter <code>st_geohash.maxchars</code>.
     */
    public static final Parameter<Integer> MAXCHARS = Internal.createParameter("maxchars", org.jooq.impl.SQLDataType.INTEGER.defaultValue(org.jooq.impl.DSL.field("0", org.jooq.impl.SQLDataType.INTEGER)), true, false);

    /**
     * Create a new routine call instance
     */
    public StGeohash2() {
        super("st_geohash", DefaultSchema.DEFAULT_SCHEMA, org.jooq.impl.SQLDataType.CLOB);

        setReturnParameter(RETURN_VALUE);
        addInParameter(GEOG);
        addInParameter(MAXCHARS);
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
     * Set the <code>maxchars</code> parameter IN value to the routine
     */
    public void setMaxchars(Integer value) {
        setValue(MAXCHARS, value);
    }

    /**
     * Set the <code>maxchars</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setMaxchars(Field<Integer> field) {
        setField(MAXCHARS, field);
    }
}
