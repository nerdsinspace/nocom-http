/*
 * This file is generated by jOOQ.
 */
package com.matt.nocom.server.postgres.codegen.tables.records;


import com.matt.nocom.server.postgres.codegen.tables.OldTrackAssociatorProgress;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Row1;
import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class OldTrackAssociatorProgressRecord extends TableRecordImpl<OldTrackAssociatorProgressRecord> implements Record1<Long> {

    private static final long serialVersionUID = 112187996;

    /**
     * Setter for <code>old_track_associator_progress.max_updated_at_processed</code>.
     */
    public void setMaxUpdatedAtProcessed(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>old_track_associator_progress.max_updated_at_processed</code>.
     */
    public Long getMaxUpdatedAtProcessed() {
        return (Long) get(0);
    }

    // -------------------------------------------------------------------------
    // Record1 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row1<Long> fieldsRow() {
        return (Row1) super.fieldsRow();
    }

    @Override
    public Row1<Long> valuesRow() {
        return (Row1) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return OldTrackAssociatorProgress.OLD_TRACK_ASSOCIATOR_PROGRESS.MAX_UPDATED_AT_PROCESSED;
    }

    @Override
    public Long component1() {
        return getMaxUpdatedAtProcessed();
    }

    @Override
    public Long value1() {
        return getMaxUpdatedAtProcessed();
    }

    @Override
    public OldTrackAssociatorProgressRecord value1(Long value) {
        setMaxUpdatedAtProcessed(value);
        return this;
    }

    @Override
    public OldTrackAssociatorProgressRecord values(Long value1) {
        value1(value1);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached OldTrackAssociatorProgressRecord
     */
    public OldTrackAssociatorProgressRecord() {
        super(OldTrackAssociatorProgress.OLD_TRACK_ASSOCIATOR_PROGRESS);
    }

    /**
     * Create a detached, initialised OldTrackAssociatorProgressRecord
     */
    public OldTrackAssociatorProgressRecord(Long maxUpdatedAtProcessed) {
        super(OldTrackAssociatorProgress.OLD_TRACK_ASSOCIATOR_PROGRESS);

        set(0, maxUpdatedAtProcessed);
    }
}
