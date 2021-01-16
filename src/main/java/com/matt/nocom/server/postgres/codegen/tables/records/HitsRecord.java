/*
 * This file is generated by jOOQ.
 */
package com.matt.nocom.server.postgres.codegen.tables.records;


import com.matt.nocom.server.postgres.codegen.tables.Hits;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record8;
import org.jooq.Row8;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class HitsRecord extends UpdatableRecordImpl<HitsRecord> implements Record8<Long, Long, Integer, Integer, Short, Short, Integer, Boolean> {

    private static final long serialVersionUID = 442742445;

    /**
     * Setter for <code>hits.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>hits.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>hits.created_at</code>.
     */
    public void setCreatedAt(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>hits.created_at</code>.
     */
    public Long getCreatedAt() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>hits.x</code>.
     */
    public void setX(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>hits.x</code>.
     */
    public Integer getX() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>hits.z</code>.
     */
    public void setZ(Integer value) {
        set(3, value);
    }

    /**
     * Getter for <code>hits.z</code>.
     */
    public Integer getZ() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>hits.server_id</code>.
     */
    public void setServerId(Short value) {
        set(4, value);
    }

    /**
     * Getter for <code>hits.server_id</code>.
     */
    public Short getServerId() {
        return (Short) get(4);
    }

    /**
     * Setter for <code>hits.dimension</code>.
     */
    public void setDimension(Short value) {
        set(5, value);
    }

    /**
     * Getter for <code>hits.dimension</code>.
     */
    public Short getDimension() {
        return (Short) get(5);
    }

    /**
     * Setter for <code>hits.track_id</code>.
     */
    public void setTrackId(Integer value) {
        set(6, value);
    }

    /**
     * Getter for <code>hits.track_id</code>.
     */
    public Integer getTrackId() {
        return (Integer) get(6);
    }

    /**
     * Setter for <code>hits.legacy</code>.
     */
    public void setLegacy(Boolean value) {
        set(7, value);
    }

    /**
     * Getter for <code>hits.legacy</code>.
     */
    public Boolean getLegacy() {
        return (Boolean) get(7);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record8 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row8<Long, Long, Integer, Integer, Short, Short, Integer, Boolean> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    @Override
    public Row8<Long, Long, Integer, Integer, Short, Short, Integer, Boolean> valuesRow() {
        return (Row8) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return Hits.HITS.ID;
    }

    @Override
    public Field<Long> field2() {
        return Hits.HITS.CREATED_AT;
    }

    @Override
    public Field<Integer> field3() {
        return Hits.HITS.X;
    }

    @Override
    public Field<Integer> field4() {
        return Hits.HITS.Z;
    }

    @Override
    public Field<Short> field5() {
        return Hits.HITS.SERVER_ID;
    }

    @Override
    public Field<Short> field6() {
        return Hits.HITS.DIMENSION;
    }

    @Override
    public Field<Integer> field7() {
        return Hits.HITS.TRACK_ID;
    }

    @Override
    public Field<Boolean> field8() {
        return Hits.HITS.LEGACY;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public Long component2() {
        return getCreatedAt();
    }

    @Override
    public Integer component3() {
        return getX();
    }

    @Override
    public Integer component4() {
        return getZ();
    }

    @Override
    public Short component5() {
        return getServerId();
    }

    @Override
    public Short component6() {
        return getDimension();
    }

    @Override
    public Integer component7() {
        return getTrackId();
    }

    @Override
    public Boolean component8() {
        return getLegacy();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public Long value2() {
        return getCreatedAt();
    }

    @Override
    public Integer value3() {
        return getX();
    }

    @Override
    public Integer value4() {
        return getZ();
    }

    @Override
    public Short value5() {
        return getServerId();
    }

    @Override
    public Short value6() {
        return getDimension();
    }

    @Override
    public Integer value7() {
        return getTrackId();
    }

    @Override
    public Boolean value8() {
        return getLegacy();
    }

    @Override
    public HitsRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public HitsRecord value2(Long value) {
        setCreatedAt(value);
        return this;
    }

    @Override
    public HitsRecord value3(Integer value) {
        setX(value);
        return this;
    }

    @Override
    public HitsRecord value4(Integer value) {
        setZ(value);
        return this;
    }

    @Override
    public HitsRecord value5(Short value) {
        setServerId(value);
        return this;
    }

    @Override
    public HitsRecord value6(Short value) {
        setDimension(value);
        return this;
    }

    @Override
    public HitsRecord value7(Integer value) {
        setTrackId(value);
        return this;
    }

    @Override
    public HitsRecord value8(Boolean value) {
        setLegacy(value);
        return this;
    }

    @Override
    public HitsRecord values(Long value1, Long value2, Integer value3, Integer value4, Short value5, Short value6, Integer value7, Boolean value8) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached HitsRecord
     */
    public HitsRecord() {
        super(Hits.HITS);
    }

    /**
     * Create a detached, initialised HitsRecord
     */
    public HitsRecord(Long id, Long createdAt, Integer x, Integer z, Short serverId, Short dimension, Integer trackId, Boolean legacy) {
        super(Hits.HITS);

        set(0, id);
        set(1, createdAt);
        set(2, x);
        set(3, z);
        set(4, serverId);
        set(5, dimension);
        set(6, trackId);
        set(7, legacy);
    }
}
