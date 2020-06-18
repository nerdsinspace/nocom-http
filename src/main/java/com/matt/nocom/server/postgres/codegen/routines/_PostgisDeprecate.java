/*
 * This file is generated by jOOQ.
 */
package com.matt.nocom.server.postgres.codegen.routines;


import com.matt.nocom.server.postgres.codegen.DefaultSchema;
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
public class _PostgisDeprecate extends AbstractRoutine<java.lang.Void> {

    private static final long serialVersionUID = -51259463;

    /**
     * The parameter <code>_postgis_deprecate.oldname</code>.
     */
    public static final Parameter<String> OLDNAME = Internal.createParameter("oldname", org.jooq.impl.SQLDataType.CLOB, false, false);

    /**
     * The parameter <code>_postgis_deprecate.newname</code>.
     */
    public static final Parameter<String> NEWNAME = Internal.createParameter("newname", org.jooq.impl.SQLDataType.CLOB, false, false);

    /**
     * The parameter <code>_postgis_deprecate.version</code>.
     */
    public static final Parameter<String> VERSION = Internal.createParameter("version", org.jooq.impl.SQLDataType.CLOB, false, false);

    /**
     * Create a new routine call instance
     */
    public _PostgisDeprecate() {
        super("_postgis_deprecate", DefaultSchema.DEFAULT_SCHEMA);

        addInParameter(OLDNAME);
        addInParameter(NEWNAME);
        addInParameter(VERSION);
    }

    /**
     * Set the <code>oldname</code> parameter IN value to the routine
     */
    public void setOldname(String value) {
        setValue(OLDNAME, value);
    }

    /**
     * Set the <code>newname</code> parameter IN value to the routine
     */
    public void setNewname(String value) {
        setValue(NEWNAME, value);
    }

    /**
     * Set the <code>version</code> parameter IN value to the routine
     */
    public void setVersion(String value) {
        setValue(VERSION, value);
    }
}
