/*
 * This file is generated by jOOQ.
 */
package com.matt.nocom.server.postgres.codegen.tables.records;


import com.matt.nocom.server.postgres.codegen.tables.HeatmapRecentOverworld;
import org.jooq.Field;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.TableRecordImpl;

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
public class HeatmapRecentOverworldRecord extends TableRecordImpl<HeatmapRecentOverworldRecord> implements Record3<Integer, Integer, Long> {

    private static final long serialVersionUID = -2074055833;

    /**
     * Setter for <code>heatmap_recent_overworld.x</code>.
     */
    public void setX(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>heatmap_recent_overworld.x</code>.
     */
    public Integer getX() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>heatmap_recent_overworld.z</code>.
     */
    public void setZ(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>heatmap_recent_overworld.z</code>.
     */
    public Integer getZ() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>heatmap_recent_overworld.cnt</code>.
     */
    public void setCnt(Long value) {
        set(2, value);
    }

    /**
     * Getter for <code>heatmap_recent_overworld.cnt</code>.
     */
    public Long getCnt() {
        return (Long) get(2);
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row3<Integer, Integer, Long> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    @Override
    public Row3<Integer, Integer, Long> valuesRow() {
        return (Row3) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return HeatmapRecentOverworld.HEATMAP_RECENT_OVERWORLD.X;
    }

    @Override
    public Field<Integer> field2() {
        return HeatmapRecentOverworld.HEATMAP_RECENT_OVERWORLD.Z;
    }

    @Override
    public Field<Long> field3() {
        return HeatmapRecentOverworld.HEATMAP_RECENT_OVERWORLD.CNT;
    }

    @Override
    public Integer component1() {
        return getX();
    }

    @Override
    public Integer component2() {
        return getZ();
    }

    @Override
    public Long component3() {
        return getCnt();
    }

    @Override
    public Integer value1() {
        return getX();
    }

    @Override
    public Integer value2() {
        return getZ();
    }

    @Override
    public Long value3() {
        return getCnt();
    }

    @Override
    public HeatmapRecentOverworldRecord value1(Integer value) {
        setX(value);
        return this;
    }

    @Override
    public HeatmapRecentOverworldRecord value2(Integer value) {
        setZ(value);
        return this;
    }

    @Override
    public HeatmapRecentOverworldRecord value3(Long value) {
        setCnt(value);
        return this;
    }

    @Override
    public HeatmapRecentOverworldRecord values(Integer value1, Integer value2, Long value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached HeatmapRecentOverworldRecord
     */
    public HeatmapRecentOverworldRecord() {
        super(HeatmapRecentOverworld.HEATMAP_RECENT_OVERWORLD);
    }

    /**
     * Create a detached, initialised HeatmapRecentOverworldRecord
     */
    public HeatmapRecentOverworldRecord(Integer x, Integer z, Long cnt) {
        super(HeatmapRecentOverworld.HEATMAP_RECENT_OVERWORLD);

        set(0, x);
        set(1, z);
        set(2, cnt);
    }
}