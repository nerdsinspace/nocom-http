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
public class StProject extends AbstractRoutine<Object> {

    private static final long serialVersionUID = 901306777;

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
     * The parameter <code>st_project.distance</code>.
     */
    public static final Parameter<Double> DISTANCE = Internal.createParameter("distance", org.jooq.impl.SQLDataType.DOUBLE, false, false);

    /**
     * The parameter <code>st_project.azimuth</code>.
     */
    public static final Parameter<Double> AZIMUTH = Internal.createParameter("azimuth", org.jooq.impl.SQLDataType.DOUBLE, false, false);

    /**
     * Create a new routine call instance
     */
    public StProject() {
        super("st_project", DefaultSchema.DEFAULT_SCHEMA, org.jooq.impl.DefaultDataType.getDefaultDataType("\"public\".\"geography\""));

        setReturnParameter(RETURN_VALUE);
        addInParameter(GEOG);
        addInParameter(DISTANCE);
        addInParameter(AZIMUTH);
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
     * Set the <code>distance</code> parameter IN value to the routine
     */
    public void setDistance(Double value) {
        setValue(DISTANCE, value);
    }

    /**
     * Set the <code>distance</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setDistance(Field<Double> field) {
        setField(DISTANCE, field);
    }

    /**
     * Set the <code>azimuth</code> parameter IN value to the routine
     */
    public void setAzimuth(Double value) {
        setValue(AZIMUTH, value);
    }

    /**
     * Set the <code>azimuth</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setAzimuth(Field<Double> field) {
        setField(AZIMUTH, field);
    }
}
