/*
 * This file is generated by jOOQ.
 */
package com.matt.nocom.server.postgres.codegen;


import org.jooq.Schema;
import org.jooq.impl.CatalogImpl;

import javax.annotation.processing.Generated;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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
public class DefaultCatalog extends CatalogImpl {

    private static final long serialVersionUID = -1662637767;

    /**
     * The reference instance of <code></code>
     */
    public static final DefaultCatalog DEFAULT_CATALOG = new DefaultCatalog();

    /**
     * The schema <code></code>.
     */
    public final DefaultSchema DEFAULT_SCHEMA = com.matt.nocom.server.postgres.codegen.DefaultSchema.DEFAULT_SCHEMA;

    /**
     * No further instances allowed
     */
    private DefaultCatalog() {
        super("");
    }

    @Override
    public final List<Schema> getSchemas() {
        List result = new ArrayList();
        result.addAll(getSchemas0());
        return result;
    }

    private final List<Schema> getSchemas0() {
        return Arrays.<Schema>asList(
            DefaultSchema.DEFAULT_SCHEMA);
    }
}