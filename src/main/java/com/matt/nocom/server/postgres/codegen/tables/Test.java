/*
 * This file is generated by jOOQ.
 */
package com.matt.nocom.server.postgres.codegen.tables;


import com.matt.nocom.server.postgres.codegen.DefaultSchema;
import com.matt.nocom.server.postgres.codegen.tables.records.TestRecord;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Test extends TableImpl<TestRecord> {

    private static final long serialVersionUID = 649994828;

    /**
     * The reference instance of <code>test</code>
     */
    public static final Test TEST = new Test();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TestRecord> getRecordType() {
        return TestRecord.class;
    }

    /**
     * The column <code>test.x</code>.
     */
    public final TableField<TestRecord, Integer> X = createField(DSL.name("x"), org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>test.z</code>.
     */
    public final TableField<TestRecord, Integer> Z = createField(DSL.name("z"), org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>test.cnt</code>.
     */
    public final TableField<TestRecord, Long> CNT = createField(DSL.name("cnt"), org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * Create a <code>test</code> table reference
     */
    public Test() {
        this(DSL.name("test"), null);
    }

    /**
     * Create an aliased <code>test</code> table reference
     */
    public Test(String alias) {
        this(DSL.name(alias), TEST);
    }

    /**
     * Create an aliased <code>test</code> table reference
     */
    public Test(Name alias) {
        this(alias, TEST);
    }

    private Test(Name alias, Table<TestRecord> aliased) {
        this(alias, aliased, null);
    }

    private Test(Name alias, Table<TestRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> Test(Table<O> child, ForeignKey<O, TestRecord> key) {
        super(child, key, TEST);
    }

    @Override
    public Schema getSchema() {
        return DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public Test as(String alias) {
        return new Test(DSL.name(alias), this);
    }

    @Override
    public Test as(Name alias) {
        return new Test(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Test rename(String name) {
        return new Test(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Test rename(Name name) {
        return new Test(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<Integer, Integer, Long> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}
