/*
 * This file is generated by jOOQ.
 */
package com.matt.nocom.server.postgres.codegen.tables;


import com.matt.nocom.server.postgres.codegen.DefaultSchema;
import com.matt.nocom.server.postgres.codegen.Indexes;
import com.matt.nocom.server.postgres.codegen.Keys;
import com.matt.nocom.server.postgres.codegen.tables.records.LastByServerRecord;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;

import javax.annotation.processing.Generated;
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
public class LastByServer extends TableImpl<LastByServerRecord> {

    private static final long serialVersionUID = 878340103;

    /**
     * The reference instance of <code>last_by_server</code>
     */
    public static final LastByServer LAST_BY_SERVER = new LastByServer();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<LastByServerRecord> getRecordType() {
        return LastByServerRecord.class;
    }

    /**
     * The column <code>last_by_server.server_id</code>.
     */
    public final TableField<LastByServerRecord, Short> SERVER_ID = createField(DSL.name("server_id"), org.jooq.impl.SQLDataType.SMALLINT.nullable(false), this, "");

    /**
     * The column <code>last_by_server.created_at</code>.
     */
    public final TableField<LastByServerRecord, Long> CREATED_AT = createField(DSL.name("created_at"), org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * Create a <code>last_by_server</code> table reference
     */
    public LastByServer() {
        this(DSL.name("last_by_server"), null);
    }

    /**
     * Create an aliased <code>last_by_server</code> table reference
     */
    public LastByServer(String alias) {
        this(DSL.name(alias), LAST_BY_SERVER);
    }

    /**
     * Create an aliased <code>last_by_server</code> table reference
     */
    public LastByServer(Name alias) {
        this(alias, LAST_BY_SERVER);
    }

    private LastByServer(Name alias, Table<LastByServerRecord> aliased) {
        this(alias, aliased, null);
    }

    private LastByServer(Name alias, Table<LastByServerRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> LastByServer(Table<O> child, ForeignKey<O, LastByServerRecord> key) {
        super(child, key, LAST_BY_SERVER);
    }

    @Override
    public Schema getSchema() {
        return DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.LAST_BY_SERVER_PKEY);
    }

    @Override
    public UniqueKey<LastByServerRecord> getPrimaryKey() {
        return Keys.LAST_BY_SERVER_PKEY;
    }

    @Override
    public List<UniqueKey<LastByServerRecord>> getKeys() {
        return Arrays.<UniqueKey<LastByServerRecord>>asList(Keys.LAST_BY_SERVER_PKEY);
    }

    @Override
    public List<ForeignKey<LastByServerRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<LastByServerRecord, ?>>asList(Keys.LAST_BY_SERVER__LAST_BY_SERVER_SERVER_ID_FKEY);
    }

    public Servers servers() {
        return new Servers(this, Keys.LAST_BY_SERVER__LAST_BY_SERVER_SERVER_ID_FKEY);
    }

    @Override
    public LastByServer as(String alias) {
        return new LastByServer(DSL.name(alias), this);
    }

    @Override
    public LastByServer as(Name alias) {
        return new LastByServer(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public LastByServer rename(String name) {
        return new LastByServer(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public LastByServer rename(Name name) {
        return new LastByServer(name, null);
    }

    // -------------------------------------------------------------------------
    // Row2 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row2<Short, Long> fieldsRow() {
        return (Row2) super.fieldsRow();
    }
}
