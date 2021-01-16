/*
 * This file is generated by jOOQ.
 */
package com.matt.nocom.server.postgres.codegen.routines;


import com.matt.nocom.server.postgres.codegen.DefaultSchema;

import java.time.OffsetDateTime;

import org.jooq.Field;
import org.jooq.Parameter;
import org.jooq.impl.AbstractRoutine;
import org.jooq.impl.Internal;
import org.jooq.types.YearToSecond;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TstzDist extends AbstractRoutine<YearToSecond> {

    private static final long serialVersionUID = -1815295878;

    /**
     * The parameter <code>tstz_dist.RETURN_VALUE</code>.
     */
    public static final Parameter<YearToSecond> RETURN_VALUE = Internal.createParameter("RETURN_VALUE", org.jooq.impl.SQLDataType.INTERVAL, false, false);

    /**
     * The parameter <code>tstz_dist._1</code>.
     */
    public static final Parameter<OffsetDateTime> _1 = Internal.createParameter("_1", org.jooq.impl.SQLDataType.TIMESTAMPWITHTIMEZONE, false, true);

    /**
     * The parameter <code>tstz_dist._2</code>.
     */
    public static final Parameter<OffsetDateTime> _2 = Internal.createParameter("_2", org.jooq.impl.SQLDataType.TIMESTAMPWITHTIMEZONE, false, true);

    /**
     * Create a new routine call instance
     */
    public TstzDist() {
        super("tstz_dist", DefaultSchema.DEFAULT_SCHEMA, org.jooq.impl.SQLDataType.INTERVAL);

        setReturnParameter(RETURN_VALUE);
        addInParameter(_1);
        addInParameter(_2);
    }

    /**
     * Set the <code>_1</code> parameter IN value to the routine
     */
    public void set__1(OffsetDateTime value) {
        setValue(_1, value);
    }

    /**
     * Set the <code>_1</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void set__1(Field<OffsetDateTime> field) {
        setField(_1, field);
    }

    /**
     * Set the <code>_2</code> parameter IN value to the routine
     */
    public void set__2(OffsetDateTime value) {
        setValue(_2, value);
    }

    /**
     * Set the <code>_2</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void set__2(Field<OffsetDateTime> field) {
        setField(_2, field);
    }
}
