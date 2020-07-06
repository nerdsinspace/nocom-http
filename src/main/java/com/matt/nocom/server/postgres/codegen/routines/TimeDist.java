/*
 * This file is generated by jOOQ.
 */
package com.matt.nocom.server.postgres.codegen.routines;


import com.matt.nocom.server.postgres.codegen.DefaultSchema;
import org.jooq.Field;
import org.jooq.Parameter;
import org.jooq.impl.AbstractRoutine;
import org.jooq.impl.Internal;
import org.jooq.types.YearToSecond;

import java.time.LocalTime;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TimeDist extends AbstractRoutine<YearToSecond> {

    private static final long serialVersionUID = 268938760;

    /**
     * The parameter <code>time_dist.RETURN_VALUE</code>.
     */
    public static final Parameter<YearToSecond> RETURN_VALUE = Internal.createParameter("RETURN_VALUE", org.jooq.impl.SQLDataType.INTERVAL, false, false);

    /**
     * The parameter <code>time_dist._1</code>.
     */
    public static final Parameter<LocalTime> _1 = Internal.createParameter("_1", org.jooq.impl.SQLDataType.LOCALTIME, false, true);

    /**
     * The parameter <code>time_dist._2</code>.
     */
    public static final Parameter<LocalTime> _2 = Internal.createParameter("_2", org.jooq.impl.SQLDataType.LOCALTIME, false, true);

    /**
     * Create a new routine call instance
     */
    public TimeDist() {
        super("time_dist", DefaultSchema.DEFAULT_SCHEMA, org.jooq.impl.SQLDataType.INTERVAL);

        setReturnParameter(RETURN_VALUE);
        addInParameter(_1);
        addInParameter(_2);
    }

    /**
     * Set the <code>_1</code> parameter IN value to the routine
     */
    public void set__1(LocalTime value) {
        setValue(_1, value);
    }

    /**
     * Set the <code>_1</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void set__1(Field<LocalTime> field) {
        setField(_1, field);
    }

    /**
     * Set the <code>_2</code> parameter IN value to the routine
     */
    public void set__2(LocalTime value) {
        setValue(_2, value);
    }

    /**
     * Set the <code>_2</code> parameter to the function to be used with a {@link org.jooq.Select} statement
     */
    public void set__2(Field<LocalTime> field) {
        setField(_2, field);
    }
}
