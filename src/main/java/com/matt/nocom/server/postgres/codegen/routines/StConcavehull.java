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
public class StConcavehull extends AbstractRoutine<Object> {

    private static final long serialVersionUID = -382270471;

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static final Parameter<Object> RETURN_VALUE = Internal.createParameter("RETURN_VALUE", org.jooq.impl.DefaultDataType.getDefaultDataType("\"public\".\"geometry\""), false, false);

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static final Parameter<Object> PARAM_GEOM = Internal.createParameter("param_geom", org.jooq.impl.DefaultDataType.getDefaultDataType("\"public\".\"geometry\""), false, false);

    /**
     * The parameter <code>st_concavehull.param_pctconvex</code>.
     */
    public static final Parameter<Double> PARAM_PCTCONVEX = Internal.createParameter("param_pctconvex", org.jooq.impl.SQLDataType.DOUBLE, false, false);

    /**
     * The parameter <code>st_concavehull.param_allow_holes</code>.
     */
    public static final Parameter<Boolean> PARAM_ALLOW_HOLES = Internal.createParameter("param_allow_holes", org.jooq.impl.SQLDataType.BOOLEAN.defaultValue(org.jooq.impl.DSL.field("false", org.jooq.impl.SQLDataType.BOOLEAN)), true, false);

    /**
     * Create a new routine call instance
     */
    public StConcavehull() {
        super("st_concavehull", DefaultSchema.DEFAULT_SCHEMA, org.jooq.impl.DefaultDataType.getDefaultDataType("\"public\".\"geometry\""));

        setReturnParameter(RETURN_VALUE);
        addInParameter(PARAM_GEOM);
        addInParameter(PARAM_PCTCONVEX);
        addInParameter(PARAM_ALLOW_HOLES);
    }

    /**
     * Set the <code>param_geom</code> parameter IN value to the routine
     */
    public void setParamGeom(Object value) {
        setValue(PARAM_GEOM, value);
    }

    /**
     * Set the <code>param_geom</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setParamGeom(Field<Object> field) {
        setField(PARAM_GEOM, field);
    }

    /**
     * Set the <code>param_pctconvex</code> parameter IN value to the routine
     */
    public void setParamPctconvex(Double value) {
        setValue(PARAM_PCTCONVEX, value);
    }

    /**
     * Set the <code>param_pctconvex</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setParamPctconvex(Field<Double> field) {
        setField(PARAM_PCTCONVEX, field);
    }

    /**
     * Set the <code>param_allow_holes</code> parameter IN value to the routine
     */
    public void setParamAllowHoles(Boolean value) {
        setValue(PARAM_ALLOW_HOLES, value);
    }

    /**
     * Set the <code>param_allow_holes</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setParamAllowHoles(Field<Boolean> field) {
        setField(PARAM_ALLOW_HOLES, field);
    }
}
