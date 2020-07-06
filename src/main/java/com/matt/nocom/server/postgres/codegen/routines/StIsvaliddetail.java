/*
 * This file is generated by jOOQ.
 */
package com.matt.nocom.server.postgres.codegen.routines;


import com.matt.nocom.server.postgres.codegen.DefaultSchema;
import com.matt.nocom.server.postgres.codegen.udt.records.ValidDetailRecord;
import org.jooq.Field;
import org.jooq.Parameter;
import org.jooq.impl.AbstractRoutine;
import org.jooq.impl.Internal;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class StIsvaliddetail extends AbstractRoutine<ValidDetailRecord> {

    private static final long serialVersionUID = 193693493;

    /**
     * The parameter <code>st_isvaliddetail.RETURN_VALUE</code>.
     */
    public static final Parameter<ValidDetailRecord> RETURN_VALUE = Internal.createParameter("RETURN_VALUE", com.matt.nocom.server.postgres.codegen.udt.ValidDetail.VALID_DETAIL.getDataType(), false, false);

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static final Parameter<Object> GEOM = Internal.createParameter("geom", org.jooq.impl.DefaultDataType.getDefaultDataType("\"public\".\"geometry\""), false, false);

    /**
     * The parameter <code>st_isvaliddetail.flags</code>.
     */
    public static final Parameter<Integer> FLAGS = Internal.createParameter("flags", org.jooq.impl.SQLDataType.INTEGER.defaultValue(org.jooq.impl.DSL.field("0", org.jooq.impl.SQLDataType.INTEGER)), true, false);

    /**
     * Create a new routine call instance
     */
    public StIsvaliddetail() {
        super("st_isvaliddetail", DefaultSchema.DEFAULT_SCHEMA, com.matt.nocom.server.postgres.codegen.udt.ValidDetail.VALID_DETAIL.getDataType());

        setReturnParameter(RETURN_VALUE);
        addInParameter(GEOM);
        addInParameter(FLAGS);
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
     * Set the <code>flags</code> parameter IN value to the routine
     */
    public void setFlags(Integer value) {
        setValue(FLAGS, value);
    }

    /**
     * Set the <code>flags</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void setFlags(Field<Integer> field) {
        setField(FLAGS, field);
    }
}
