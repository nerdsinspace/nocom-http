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
public class StAslatlontext extends AbstractRoutine<String> {

    private static final long serialVersionUID = -929145985;

    /**
     * The parameter <code>st_aslatlontext.RETURN_VALUE</code>.
     */
    public static final Parameter<String> RETURN_VALUE = Internal.createParameter("RETURN_VALUE", org.jooq.impl.SQLDataType.CLOB, false, false);

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static final Parameter<Object> GEOM = Internal.createParameter("geom", org.jooq.impl.DefaultDataType.getDefaultDataType("\"public\".\"geometry\""), false, false);

    /**
     * The parameter <code>st_aslatlontext.tmpl</code>.
     */
    public static final Parameter<String> TMPL = Internal.createParameter("tmpl", org.jooq.impl.SQLDataType.CLOB.defaultValue(org.jooq.impl.DSL.field("''::text", org.jooq.impl.SQLDataType.CLOB)), true, false);

    /**
     * Create a new routine call instance
     */
    public StAslatlontext() {
        super("st_aslatlontext", DefaultSchema.DEFAULT_SCHEMA, org.jooq.impl.SQLDataType.CLOB);

        setReturnParameter(RETURN_VALUE);
        addInParameter(GEOM);
        addInParameter(TMPL);
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
     * Set the <code>tmpl</code> parameter IN value to the routine
     */
    public void setTmpl(String value) {
        setValue(TMPL, value);
    }

    /**
     * Set the <code>tmpl</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setTmpl(Field<String> field) {
        setField(TMPL, field);
    }
}