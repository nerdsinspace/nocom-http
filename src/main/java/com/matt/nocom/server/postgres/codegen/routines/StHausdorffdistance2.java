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
public class StHausdorffdistance2 extends AbstractRoutine<Double> {

    private static final long serialVersionUID = 706647493;

    /**
     * The parameter <code>st_hausdorffdistance.RETURN_VALUE</code>.
     */
    public static final Parameter<Double> RETURN_VALUE = Internal.createParameter("RETURN_VALUE", org.jooq.impl.SQLDataType.DOUBLE, false, false);

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static final Parameter<Object> GEOM1 = Internal.createParameter("geom1", org.jooq.impl.DefaultDataType.getDefaultDataType("\"public\".\"geometry\""), false, false);

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static final Parameter<Object> GEOM2 = Internal.createParameter("geom2", org.jooq.impl.DefaultDataType.getDefaultDataType("\"public\".\"geometry\""), false, false);

    /**
     * The parameter <code>st_hausdorffdistance._3</code>.
     */
    public static final Parameter<Double> _3 = Internal.createParameter("_3", org.jooq.impl.SQLDataType.DOUBLE, false, true);

    /**
     * Create a new routine call instance
     */
    public StHausdorffdistance2() {
        super("st_hausdorffdistance", DefaultSchema.DEFAULT_SCHEMA, org.jooq.impl.SQLDataType.DOUBLE);

        setReturnParameter(RETURN_VALUE);
        addInParameter(GEOM1);
        addInParameter(GEOM2);
        addInParameter(_3);
        setOverloaded(true);
    }

    /**
     * Set the <code>geom1</code> parameter IN value to the routine
     */
    public void setGeom1(Object value) {
        setValue(GEOM1, value);
    }

    /**
     * Set the <code>geom1</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setGeom1(Field<Object> field) {
        setField(GEOM1, field);
    }

    /**
     * Set the <code>geom2</code> parameter IN value to the routine
     */
    public void setGeom2(Object value) {
        setValue(GEOM2, value);
    }

    /**
     * Set the <code>geom2</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setGeom2(Field<Object> field) {
        setField(GEOM2, field);
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
