/*
 * This file is generated by jOOQ.
 */
package com.matt.nocom.server.postgres.codegen.tables.records;


import com.matt.nocom.server.postgres.codegen.tables.GeneratorCache;

import org.jooq.Field;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class GeneratorCacheRecord extends TableRecordImpl<GeneratorCacheRecord> implements Record5<byte[], Integer, Integer, Short, Short> {

    private static final long serialVersionUID = 1364368298;

    /**
     * Setter for <code>generator_cache.data</code>.
     */
    public void setData(byte[] value) {
        set(0, value);
    }

    /**
     * Getter for <code>generator_cache.data</code>.
     */
    public byte[] getData() {
        return (byte[]) get(0);
    }

    /**
     * Setter for <code>generator_cache.x</code>.
     */
    public void setX(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>generator_cache.x</code>.
     */
    public Integer getX() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>generator_cache.z</code>.
     */
    public void setZ(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>generator_cache.z</code>.
     */
    public Integer getZ() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>generator_cache.dimension</code>.
     */
    public void setDimension(Short value) {
        set(3, value);
    }

    /**
     * Getter for <code>generator_cache.dimension</code>.
     */
    public Short getDimension() {
        return (Short) get(3);
    }

    /**
     * Setter for <code>generator_cache.server_id</code>.
     */
    public void setServerId(Short value) {
        set(4, value);
    }

    /**
     * Getter for <code>generator_cache.server_id</code>.
     */
    public Short getServerId() {
        return (Short) get(4);
    }

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row5<byte[], Integer, Integer, Short, Short> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    @Override
    public Row5<byte[], Integer, Integer, Short, Short> valuesRow() {
        return (Row5) super.valuesRow();
    }

    @Override
    public Field<byte[]> field1() {
        return GeneratorCache.GENERATOR_CACHE.DATA;
    }

    @Override
    public Field<Integer> field2() {
        return GeneratorCache.GENERATOR_CACHE.X;
    }

    @Override
    public Field<Integer> field3() {
        return GeneratorCache.GENERATOR_CACHE.Z;
    }

    @Override
    public Field<Short> field4() {
        return GeneratorCache.GENERATOR_CACHE.DIMENSION;
    }

    @Override
    public Field<Short> field5() {
        return GeneratorCache.GENERATOR_CACHE.SERVER_ID;
    }

    @Override
    public byte[] component1() {
        return getData();
    }

    @Override
    public Integer component2() {
        return getX();
    }

    @Override
    public Integer component3() {
        return getZ();
    }

    @Override
    public Short component4() {
        return getDimension();
    }

    @Override
    public Short component5() {
        return getServerId();
    }

    @Override
    public byte[] value1() {
        return getData();
    }

    @Override
    public Integer value2() {
        return getX();
    }

    @Override
    public Integer value3() {
        return getZ();
    }

    @Override
    public Short value4() {
        return getDimension();
    }

    @Override
    public Short value5() {
        return getServerId();
    }

    @Override
    public GeneratorCacheRecord value1(byte[] value) {
        setData(value);
        return this;
    }

    @Override
    public GeneratorCacheRecord value2(Integer value) {
        setX(value);
        return this;
    }

    @Override
    public GeneratorCacheRecord value3(Integer value) {
        setZ(value);
        return this;
    }

    @Override
    public GeneratorCacheRecord value4(Short value) {
        setDimension(value);
        return this;
    }

    @Override
    public GeneratorCacheRecord value5(Short value) {
        setServerId(value);
        return this;
    }

    @Override
    public GeneratorCacheRecord values(byte[] value1, Integer value2, Integer value3, Short value4, Short value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached GeneratorCacheRecord
     */
    public GeneratorCacheRecord() {
        super(GeneratorCache.GENERATOR_CACHE);
    }

    /**
     * Create a detached, initialised GeneratorCacheRecord
     */
    public GeneratorCacheRecord(byte[] data, Integer x, Integer z, Short dimension, Short serverId) {
        super(GeneratorCache.GENERATOR_CACHE);

        set(0, data);
        set(1, x);
        set(2, z);
        set(3, dimension);
        set(4, serverId);
    }
}
