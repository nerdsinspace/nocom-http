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
public class StExpand4 extends AbstractRoutine<Object> {

    private static final long serialVersionUID = 1861222604;

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static final Parameter<Object> RETURN_VALUE = Internal.createParameter("RETURN_VALUE", org.jooq.impl.DefaultDataType.getDefaultDataType("\"public\".\"box3d\""), false, false);

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static final Parameter<Object> BOX = Internal.createParameter("box", org.jooq.impl.DefaultDataType.getDefaultDataType("\"public\".\"box3d\""), false, false);

    /**
     * The parameter <code>st_expand.dx</code>.
     */
    public static final Parameter<Double> DX = Internal.createParameter("dx", org.jooq.impl.SQLDataType.DOUBLE, false, false);

    /**
     * The parameter <code>st_expand.dy</code>.
     */
    public static final Parameter<Double> DY = Internal.createParameter("dy", org.jooq.impl.SQLDataType.DOUBLE, false, false);

    /**
     * The parameter <code>st_expand.dz</code>.
     */
    public static final Parameter<Double> DZ = Internal.createParameter("dz", org.jooq.impl.SQLDataType.DOUBLE.defaultValue(org.jooq.impl.DSL.field("0", org.jooq.impl.SQLDataType.DOUBLE)), true, false);

    /**
     * Create a new routine call instance
     */
    public StExpand4() {
        super("st_expand", DefaultSchema.DEFAULT_SCHEMA, org.jooq.impl.DefaultDataType.getDefaultDataType("\"public\".\"box3d\""));

        setReturnParameter(RETURN_VALUE);
        addInParameter(BOX);
        addInParameter(DX);
        addInParameter(DY);
        addInParameter(DZ);
        setOverloaded(true);
    }

    /**
     * Set the <code>box</code> parameter IN value to the routine
     */
    public void setBox(Object value) {
        setValue(BOX, value);
    }

    /**
     * Set the <code>box</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setBox(Field<Object> field) {
        setField(BOX, field);
    }

    /**
     * Set the <code>dx</code> parameter IN value to the routine
     */
    public void setDx(Double value) {
        setValue(DX, value);
    }

    /**
     * Set the <code>dx</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setDx(Field<Double> field) {
        setField(DX, field);
    }

    /**
     * Set the <code>dy</code> parameter IN value to the routine
     */
    public void setDy(Double value) {
        setValue(DY, value);
    }

    /**
     * Set the <code>dy</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setDy(Field<Double> field) {
        setField(DY, field);
    }

    /**
     * Set the <code>dz</code> parameter IN value to the routine
     */
    public void setDz(Double value) {
        setValue(DZ, value);
    }

    /**
     * Set the <code>dz</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setDz(Field<Double> field) {
        setField(DZ, field);
    }
}
