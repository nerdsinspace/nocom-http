/*
 * This file is generated by jOOQ.
 */
package com.matt.nocom.server.postgres.codegen.tables;


import com.matt.nocom.server.postgres.codegen.DefaultSchema;
import com.matt.nocom.server.postgres.codegen.Indexes;
import com.matt.nocom.server.postgres.codegen.Keys;
import com.matt.nocom.server.postgres.codegen.tables.records.DbscanRecord;
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
public class Dbscan extends TableImpl<DbscanRecord> {

    private static final long serialVersionUID = -125894898;

    /**
     * The reference instance of <code>dbscan</code>
     */
    public static final Dbscan DBSCAN = new Dbscan();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DbscanRecord> getRecordType() {
        return DbscanRecord.class;
    }

    /**
     * The column <code>dbscan.id</code>.
     */
    public final TableField<DbscanRecord, Integer> ID = createField(DSL.name("id"), org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('dbscan_id_seq'::regclass)", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>dbscan.cnt</code>.
     */
    public final TableField<DbscanRecord, Integer> CNT = createField(DSL.name("cnt"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>dbscan.x</code>.
     */
    public final TableField<DbscanRecord, Integer> X = createField(DSL.name("x"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>dbscan.z</code>.
     */
    public final TableField<DbscanRecord, Integer> Z = createField(DSL.name("z"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>dbscan.dimension</code>.
     */
    public final TableField<DbscanRecord, Short> DIMENSION = createField(DSL.name("dimension"), org.jooq.impl.SQLDataType.SMALLINT.nullable(false), this, "");

    /**
     * The column <code>dbscan.server_id</code>.
     */
    public final TableField<DbscanRecord, Short> SERVER_ID = createField(DSL.name("server_id"), org.jooq.impl.SQLDataType.SMALLINT.nullable(false), this, "");

    /**
     * The column <code>dbscan.is_core</code>.
     */
    public final TableField<DbscanRecord, Boolean> IS_CORE = createField(DSL.name("is_core"), org.jooq.impl.SQLDataType.BOOLEAN.nullable(false), this, "");

    /**
     * The column <code>dbscan.cluster_parent</code>.
     */
    public final TableField<DbscanRecord, Integer> CLUSTER_PARENT = createField(DSL.name("cluster_parent"), org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>dbscan.disjoint_rank</code>.
     */
    public final TableField<DbscanRecord, Integer> DISJOINT_RANK = createField(DSL.name("disjoint_rank"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>dbscan.disjoint_size</code>.
     */
    public final TableField<DbscanRecord, Integer> DISJOINT_SIZE = createField(DSL.name("disjoint_size"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>dbscan.updated_at_approx</code>.
     */
    public final TableField<DbscanRecord, Long> UPDATED_AT_APPROX = createField(DSL.name("updated_at_approx"), org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * Create a <code>dbscan</code> table reference
     */
    public Dbscan() {
        this(DSL.name("dbscan"), null);
    }

    /**
     * Create an aliased <code>dbscan</code> table reference
     */
    public Dbscan(String alias) {
        this(DSL.name(alias), DBSCAN);
    }

    /**
     * Create an aliased <code>dbscan</code> table reference
     */
    public Dbscan(Name alias) {
        this(alias, DBSCAN);
    }

    private Dbscan(Name alias, Table<DbscanRecord> aliased) {
        this(alias, aliased, null);
    }

    private Dbscan(Name alias, Table<DbscanRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> Dbscan(Table<O> child, ForeignKey<O, DbscanRecord> key) {
        super(child, key, DBSCAN);
    }

    @Override
    public Schema getSchema() {
        return DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.DBSCAN_CLUSTER_ROOTS, Indexes.DBSCAN_DISJOINT_TRAVERSAL, Indexes.DBSCAN_INGEST, Indexes.DBSCAN_PKEY);
    }

    @Override
    public Identity<DbscanRecord, Integer> getIdentity() {
        return Keys.IDENTITY_DBSCAN;
    }

    @Override
    public UniqueKey<DbscanRecord> getPrimaryKey() {
        return Keys.DBSCAN_PKEY;
    }

    @Override
    public List<UniqueKey<DbscanRecord>> getKeys() {
        return Arrays.<UniqueKey<DbscanRecord>>asList(Keys.DBSCAN_PKEY);
    }

    @Override
    public List<ForeignKey<DbscanRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<DbscanRecord, ?>>asList(Keys.DBSCAN__DBSCAN_DIMENSION_FKEY, Keys.DBSCAN__DBSCAN_SERVER_ID_FKEY, Keys.DBSCAN__DBSCAN_CLUSTER_PARENT_FKEY);
    }

    public Dimensions dimensions() {
        return new Dimensions(this, Keys.DBSCAN__DBSCAN_DIMENSION_FKEY);
    }

    public Servers servers() {
        return new Servers(this, Keys.DBSCAN__DBSCAN_SERVER_ID_FKEY);
    }

    public com.matt.nocom.server.postgres.codegen.tables.Dbscan dbscan() {
        return new com.matt.nocom.server.postgres.codegen.tables.Dbscan(this, Keys.DBSCAN__DBSCAN_CLUSTER_PARENT_FKEY);
    }

    @Override
    public Dbscan as(String alias) {
        return new Dbscan(DSL.name(alias), this);
    }

    @Override
    public Dbscan as(Name alias) {
        return new Dbscan(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Dbscan rename(String name) {
        return new Dbscan(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Dbscan rename(Name name) {
        return new Dbscan(name, null);
    }

    // -------------------------------------------------------------------------
    // Row11 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row11<Integer, Integer, Integer, Integer, Short, Short, Boolean, Integer, Integer, Integer, Long> fieldsRow() {
        return (Row11) super.fieldsRow();
    }
}