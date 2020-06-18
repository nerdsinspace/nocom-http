/*
 * This file is generated by jOOQ.
 */
package com.matt.nocom.server.postgres.codegen.routines;


import com.matt.nocom.server.postgres.codegen.DefaultSchema;
import org.jooq.Field;
import org.jooq.Parameter;
import org.jooq.Record;
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
public class StAsgeojson2 extends AbstractRoutine<String> {

    private static final long serialVersionUID = -2095056987;

    /**
     * The parameter <code>st_asgeojson.RETURN_VALUE</code>.
     */
    public static final Parameter<String> RETURN_VALUE = Internal.createParameter("RETURN_VALUE", org.jooq.impl.SQLDataType.CLOB, false, false);

    /**
     * The parameter <code>st_asgeojson.r</code>.
     */
    public static final Parameter<Record> R = Internal.createParameter("r", org.jooq.impl.SQLDataType.RECORD, false, false);

    /**
     * The parameter <code>st_asgeojson.geom_column</code>.
     */
    public static final Parameter<String> GEOM_COLUMN = Internal.createParameter("geom_column", org.jooq.impl.SQLDataType.CLOB.defaultValue(org.jooq.impl.DSL.field("''::text", org.jooq.impl.SQLDataType.CLOB)), true, false);

    /**
     * The parameter <code>st_asgeojson.maxdecimaldigits</code>.
     */
    public static final Parameter<Integer> MAXDECIMALDIGITS = Internal.createParameter("maxdecimaldigits", org.jooq.impl.SQLDataType.INTEGER.defaultValue(org.jooq.impl.DSL.field("9", org.jooq.impl.SQLDataType.INTEGER)), true, false);

    /**
     * The parameter <code>st_asgeojson.pretty_bool</code>.
     */
    public static final Parameter<Boolean> PRETTY_BOOL = Internal.createParameter("pretty_bool", org.jooq.impl.SQLDataType.BOOLEAN.defaultValue(org.jooq.impl.DSL.field("false", org.jooq.impl.SQLDataType.BOOLEAN)), true, false);

    /**
     * Create a new routine call instance
     */
    public StAsgeojson2() {
        super("st_asgeojson", DefaultSchema.DEFAULT_SCHEMA, org.jooq.impl.SQLDataType.CLOB);

        setReturnParameter(RETURN_VALUE);
        addInParameter(R);
        addInParameter(GEOM_COLUMN);
        addInParameter(MAXDECIMALDIGITS);
        addInParameter(PRETTY_BOOL);
        setOverloaded(true);
    }

    /**
     * Set the <code>r</code> parameter IN value to the routine
     */
    public void setR(Record value) {
        setValue(R, value);
    }

    /**
     * Set the <code>r</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setR(Field<Record> field) {
        setField(R, field);
    }

    /**
     * Set the <code>geom_column</code> parameter IN value to the routine
     */
    public void setGeomColumn(String value) {
        setValue(GEOM_COLUMN, value);
    }

    /**
     * Set the <code>geom_column</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setGeomColumn(Field<String> field) {
        setField(GEOM_COLUMN, field);
    }

    /**
     * Set the <code>maxdecimaldigits</code> parameter IN value to the routine
     */
    public void setMaxdecimaldigits(Integer value) {
        setValue(MAXDECIMALDIGITS, value);
    }

    /**
     * Set the <code>maxdecimaldigits</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setMaxdecimaldigits(Field<Integer> field) {
        setField(MAXDECIMALDIGITS, field);
    }

    /**
     * Set the <code>pretty_bool</code> parameter IN value to the routine
     */
    public void setPrettyBool(Boolean value) {
        setValue(PRETTY_BOOL, value);
    }

    /**
     * Set the <code>pretty_bool</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setPrettyBool(Field<Boolean> field) {
        setField(PRETTY_BOOL, field);
    }
}