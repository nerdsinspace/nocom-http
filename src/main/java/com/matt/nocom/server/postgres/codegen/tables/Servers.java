/*
 * This file is generated by jOOQ.
 */
package com.matt.nocom.server.postgres.codegen.tables;


import com.matt.nocom.server.postgres.codegen.DefaultSchema;
import com.matt.nocom.server.postgres.codegen.Indexes;
import com.matt.nocom.server.postgres.codegen.Keys;
import com.matt.nocom.server.postgres.codegen.tables.records.ServersRecord;
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
public class Servers extends TableImpl<ServersRecord> {

    private static final long serialVersionUID = 821507075;

    /**
     * The reference instance of <code>servers</code>
     */
    public static final Servers SERVERS = new Servers();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ServersRecord> getRecordType() {
        return ServersRecord.class;
    }

    /**
     * The column <code>servers.id</code>.
     */
    public final TableField<ServersRecord, Short> ID = createField(DSL.name("id"), org.jooq.impl.SQLDataType.SMALLINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('servers_id_seq'::regclass)", org.jooq.impl.SQLDataType.SMALLINT)), this, "");

    /**
     * The column <code>servers.hostname</code>.
     */
    public final TableField<ServersRecord, String> HOSTNAME = createField(DSL.name("hostname"), org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * Create a <code>servers</code> table reference
     */
    public Servers() {
        this(DSL.name("servers"), null);
    }

    /**
     * Create an aliased <code>servers</code> table reference
     */
    public Servers(String alias) {
        this(DSL.name(alias), SERVERS);
    }

    /**
     * Create an aliased <code>servers</code> table reference
     */
    public Servers(Name alias) {
        this(alias, SERVERS);
    }

    private Servers(Name alias, Table<ServersRecord> aliased) {
        this(alias, aliased, null);
    }

    private Servers(Name alias, Table<ServersRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> Servers(Table<O> child, ForeignKey<O, ServersRecord> key) {
        super(child, key, SERVERS);
    }

    @Override
    public Schema getSchema() {
        return DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.SERVERS_HOSTNAME_KEY, Indexes.SERVERS_PKEY);
    }

    @Override
    public Identity<ServersRecord, Short> getIdentity() {
        return Keys.IDENTITY_SERVERS;
    }

    @Override
    public UniqueKey<ServersRecord> getPrimaryKey() {
        return Keys.SERVERS_PKEY;
    }

    @Override
    public List<UniqueKey<ServersRecord>> getKeys() {
        return Arrays.<UniqueKey<ServersRecord>>asList(Keys.SERVERS_PKEY, Keys.SERVERS_HOSTNAME_KEY);
    }

    @Override
    public Servers as(String alias) {
        return new Servers(DSL.name(alias), this);
    }

    @Override
    public Servers as(Name alias) {
        return new Servers(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Servers rename(String name) {
        return new Servers(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Servers rename(Name name) {
        return new Servers(name, null);
    }

    // -------------------------------------------------------------------------
    // Row2 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row2<Short, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }
}
