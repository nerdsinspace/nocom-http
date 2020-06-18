/*
 * This file is generated by jOOQ.
 */
package com.matt.nocom.server.postgres.codegen.tables;


import com.matt.nocom.server.postgres.codegen.DefaultSchema;
import com.matt.nocom.server.postgres.codegen.tables.records.HeatmapNetherRecord;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;

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
public class HeatmapNether extends TableImpl<HeatmapNetherRecord> {

    private static final long serialVersionUID = 492726965;

    /**
     * The reference instance of <code>heatmap_nether</code>
     */
    public static final HeatmapNether HEATMAP_NETHER = new HeatmapNether();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<HeatmapNetherRecord> getRecordType() {
        return HeatmapNetherRecord.class;
    }

    /**
     * The column <code>heatmap_nether.x</code>.
     */
    public final TableField<HeatmapNetherRecord, Integer> X = createField(DSL.name("x"), org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>heatmap_nether.z</code>.
     */
    public final TableField<HeatmapNetherRecord, Integer> Z = createField(DSL.name("z"), org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>heatmap_nether.cnt</code>.
     */
    public final TableField<HeatmapNetherRecord, Long> CNT = createField(DSL.name("cnt"), org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * Create a <code>heatmap_nether</code> table reference
     */
    public HeatmapNether() {
        this(DSL.name("heatmap_nether"), null);
    }

    /**
     * Create an aliased <code>heatmap_nether</code> table reference
     */
    public HeatmapNether(String alias) {
        this(DSL.name(alias), HEATMAP_NETHER);
    }

    /**
     * Create an aliased <code>heatmap_nether</code> table reference
     */
    public HeatmapNether(Name alias) {
        this(alias, HEATMAP_NETHER);
    }

    private HeatmapNether(Name alias, Table<HeatmapNetherRecord> aliased) {
        this(alias, aliased, null);
    }

    private HeatmapNether(Name alias, Table<HeatmapNetherRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> HeatmapNether(Table<O> child, ForeignKey<O, HeatmapNetherRecord> key) {
        super(child, key, HEATMAP_NETHER);
    }

    @Override
    public Schema getSchema() {
        return DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public HeatmapNether as(String alias) {
        return new HeatmapNether(DSL.name(alias), this);
    }

    @Override
    public HeatmapNether as(Name alias) {
        return new HeatmapNether(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public HeatmapNether rename(String name) {
        return new HeatmapNether(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public HeatmapNether rename(Name name) {
        return new HeatmapNether(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<Integer, Integer, Long> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}
