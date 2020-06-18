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
 * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
 */
@java.lang.Deprecated
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.3"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class StTransform4 extends AbstractRoutine<Object> {

    private static final long serialVersionUID = 1256714805;

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static final Parameter<Object> RETURN_VALUE = Internal.createParameter("RETURN_VALUE", org.jooq.impl.DefaultDataType.getDefaultDataType("\"public\".\"geometry\""), false, false);

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static final Parameter<Object> GEOM = Internal.createParameter("geom", org.jooq.impl.DefaultDataType.getDefaultDataType("\"public\".\"geometry\""), false, false);

    /**
     * The parameter <code>st_transform.from_proj</code>.
     */
    public static final Parameter<String> FROM_PROJ = Internal.createParameter("from_proj", org.jooq.impl.SQLDataType.CLOB, false, false);

    /**
     * The parameter <code>st_transform.to_srid</code>.
     */
    public static final Parameter<Integer> TO_SRID = Internal.createParameter("to_srid", org.jooq.impl.SQLDataType.INTEGER, false, false);

    /**
     * Create a new routine call instance
     */
    public StTransform4() {
        super("st_transform", DefaultSchema.DEFAULT_SCHEMA, org.jooq.impl.DefaultDataType.getDefaultDataType("\"public\".\"geometry\""));

        setReturnParameter(RETURN_VALUE);
        addInParameter(GEOM);
        addInParameter(FROM_PROJ);
        addInParameter(TO_SRID);
        setOverloaded(true);
    }

    /**
     * Set the <code>geom</code> parameter IN value to the routine
     */
    public void setGeom(Object value) {
        setValue(GEOM, value);
    }

    /**
     * Set the <code>geom</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setGeom(Field<Object> field) {
        setField(GEOM, field);
    }

    /**
     * Set the <code>from_proj</code> parameter IN value to the routine
     */
    public void setFromProj(String value) {
        setValue(FROM_PROJ, value);
    }

    /**
     * Set the <code>from_proj</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setFromProj(Field<String> field) {
        setField(FROM_PROJ, field);
    }

    /**
     * Set the <code>to_srid</code> parameter IN value to the routine
     */
    public void setToSrid(Integer value) {
        setValue(TO_SRID, value);
    }

    /**
     * Set the <code>to_srid</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setToSrid(Field<Integer> field) {
        setField(TO_SRID, field);
    }
}
