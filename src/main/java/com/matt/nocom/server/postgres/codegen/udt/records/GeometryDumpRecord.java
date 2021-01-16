/*
 * This file is generated by jOOQ.
 */
package com.matt.nocom.server.postgres.codegen.udt.records;


import com.matt.nocom.server.postgres.codegen.udt.GeometryDump;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UDTRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class GeometryDumpRecord extends UDTRecordImpl<GeometryDumpRecord> implements Record2<Integer[], Object> {

    private static final long serialVersionUID = -521156088;

    /**
     * Setter for <code>geometry_dump.path</code>.
     */
    public void setPath(Integer[] value) {
        set(0, value);
    }

    /**
     * Getter for <code>geometry_dump.path</code>.
     */
    public Integer[] getPath() {
        return (Integer[]) get(0);
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public void setGeom(Object value) {
        set(1, value);
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public Object getGeom() {
        return get(1);
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row2<Integer[], Object> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    @Override
    public Row2<Integer[], Object> valuesRow() {
        return (Row2) super.valuesRow();
    }

    @Override
    public Field<Integer[]> field1() {
        return GeometryDump.PATH;
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    @Override
    public Field<Object> field2() {
        return GeometryDump.GEOM;
    }

    @Override
    public Integer[] component1() {
        return getPath();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    @Override
    public Object component2() {
        return getGeom();
    }

    @Override
    public Integer[] value1() {
        return getPath();
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    @Override
    public Object value2() {
        return getGeom();
    }

    @Override
    public GeometryDumpRecord value1(Integer[] value) {
        setPath(value);
        return this;
    }

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    @Override
    public GeometryDumpRecord value2(Object value) {
        setGeom(value);
        return this;
    }

    @Override
    public GeometryDumpRecord values(Integer[] value1, Object value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached GeometryDumpRecord
     */
    public GeometryDumpRecord() {
        super(GeometryDump.GEOMETRY_DUMP);
    }

    /**
     * Create a detached, initialised GeometryDumpRecord
     */
    public GeometryDumpRecord(Integer[] path, Object geom) {
        super(GeometryDump.GEOMETRY_DUMP);

        set(0, path);
        set(1, geom);
    }
}
