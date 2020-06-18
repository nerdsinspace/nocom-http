/*
 * This file is generated by jOOQ.
 */
package com.matt.nocom.server.postgres.codegen.tables;


import com.matt.nocom.server.postgres.codegen.DefaultSchema;
import com.matt.nocom.server.postgres.codegen.Indexes;
import com.matt.nocom.server.postgres.codegen.Keys;
import com.matt.nocom.server.postgres.codegen.tables.records.HitsRecord;
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
public class Hits extends TableImpl<HitsRecord> {

    private static final long serialVersionUID = 462890027;

    /**
     * The reference instance of <code>hits</code>
     */
    public static final Hits HITS = new Hits();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<HitsRecord> getRecordType() {
        return HitsRecord.class;
    }

    /**
     * The column <code>hits.id</code>.
     */
    public final TableField<HitsRecord, Long> ID = createField(DSL.name("id"), org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('hits_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>hits.created_at</code>.
     */
    public final TableField<HitsRecord, Long> CREATED_AT = createField(DSL.name("created_at"), org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>hits.x</code>.
     */
    public final TableField<HitsRecord, Integer> X = createField(DSL.name("x"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>hits.z</code>.
     */
    public final TableField<HitsRecord, Integer> Z = createField(DSL.name("z"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>hits.server_id</code>.
     */
    public final TableField<HitsRecord, Short> SERVER_ID = createField(DSL.name("server_id"), org.jooq.impl.SQLDataType.SMALLINT.nullable(false), this, "");

    /**
     * The column <code>hits.dimension</code>.
     */
    public final TableField<HitsRecord, Short> DIMENSION = createField(DSL.name("dimension"), org.jooq.impl.SQLDataType.SMALLINT.nullable(false), this, "");

    /**
     * The column <code>hits.track_id</code>.
     */
    public final TableField<HitsRecord, Integer> TRACK_ID = createField(DSL.name("track_id"), org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>hits.legacy</code>.
     */
    public final TableField<HitsRecord, Boolean> LEGACY = createField(DSL.name("legacy"), org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaultValue(org.jooq.impl.DSL.field("false", org.jooq.impl.SQLDataType.BOOLEAN)), this, "");

    /**
     * Create a <code>hits</code> table reference
     */
    public Hits() {
        this(DSL.name("hits"), null);
    }

    /**
     * Create an aliased <code>hits</code> table reference
     */
    public Hits(String alias) {
        this(DSL.name(alias), HITS);
    }

    /**
     * Create an aliased <code>hits</code> table reference
     */
    public Hits(Name alias) {
        this(alias, HITS);
    }

    private Hits(Name alias, Table<HitsRecord> aliased) {
        this(alias, aliased, null);
    }

    private Hits(Name alias, Table<HitsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> Hits(Table<O> child, ForeignKey<O, HitsRecord> key) {
        super(child, key, HITS);
    }

    @Override
    public Schema getSchema() {
        return DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.HITS_BY_TRACK_ID_2, Indexes.HITS_LOC_INTERESTING_2, Indexes.HITS_PKEY_2);
    }

    @Override
    public Identity<HitsRecord, Long> getIdentity() {
        return Keys.IDENTITY_HITS;
    }

    @Override
    public UniqueKey<HitsRecord> getPrimaryKey() {
        return Keys.HITS_PKEY_2;
    }

    @Override
    public List<UniqueKey<HitsRecord>> getKeys() {
        return Arrays.<UniqueKey<HitsRecord>>asList(Keys.HITS_PKEY_2);
    }

    @Override
    public List<ForeignKey<HitsRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<HitsRecord, ?>>asList(Keys.HITS__HITS_SERVER_ID_FKEY, Keys.HITS__HITS_DIMENSION_FKEY, Keys.HITS__HITS_TRACK_ID_FKEY);
    }

    public Servers servers() {
        return new Servers(this, Keys.HITS__HITS_SERVER_ID_FKEY);
    }

    public Dimensions dimensions() {
        return new Dimensions(this, Keys.HITS__HITS_DIMENSION_FKEY);
    }

    public Tracks tracks() {
        return new Tracks(this, Keys.HITS__HITS_TRACK_ID_FKEY);
    }

    @Override
    public Hits as(String alias) {
        return new Hits(DSL.name(alias), this);
    }

    @Override
    public Hits as(Name alias) {
        return new Hits(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Hits rename(String name) {
        return new Hits(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Hits rename(Name name) {
        return new Hits(name, null);
    }

    // -------------------------------------------------------------------------
    // Row8 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row8<Long, Long, Integer, Integer, Short, Short, Integer, Boolean> fieldsRow() {
        return (Row8) super.fieldsRow();
    }
}
