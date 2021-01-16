/*
 * This file is generated by jOOQ.
 */
package com.matt.nocom.server.postgres.codegen.tables;


import com.matt.nocom.server.postgres.codegen.DefaultSchema;
import com.matt.nocom.server.postgres.codegen.Indexes;
import com.matt.nocom.server.postgres.codegen.Keys;
import com.matt.nocom.server.postgres.codegen.tables.records.ChatRecord;

import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.JSON;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row5;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Chat extends TableImpl<ChatRecord> {

    private static final long serialVersionUID = 2126208316;

    /**
     * The reference instance of <code>chat</code>
     */
    public static final Chat CHAT = new Chat();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ChatRecord> getRecordType() {
        return ChatRecord.class;
    }

    /**
     * The column <code>chat.data</code>.
     */
    public final TableField<ChatRecord, JSON> DATA = createField(DSL.name("data"), org.jooq.impl.SQLDataType.JSON.nullable(false), this, "");

    /**
     * The column <code>chat.chat_type</code>.
     */
    public final TableField<ChatRecord, Short> CHAT_TYPE = createField(DSL.name("chat_type"), org.jooq.impl.SQLDataType.SMALLINT.nullable(false), this, "");

    /**
     * The column <code>chat.reported_by</code>.
     */
    public final TableField<ChatRecord, Integer> REPORTED_BY = createField(DSL.name("reported_by"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>chat.created_at</code>.
     */
    public final TableField<ChatRecord, Long> CREATED_AT = createField(DSL.name("created_at"), org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>chat.server_id</code>.
     */
    public final TableField<ChatRecord, Short> SERVER_ID = createField(DSL.name("server_id"), org.jooq.impl.SQLDataType.SMALLINT.nullable(false), this, "");

    /**
     * Create a <code>chat</code> table reference
     */
    public Chat() {
        this(DSL.name("chat"), null);
    }

    /**
     * Create an aliased <code>chat</code> table reference
     */
    public Chat(String alias) {
        this(DSL.name(alias), CHAT);
    }

    /**
     * Create an aliased <code>chat</code> table reference
     */
    public Chat(Name alias) {
        this(alias, CHAT);
    }

    private Chat(Name alias, Table<ChatRecord> aliased) {
        this(alias, aliased, null);
    }

    private Chat(Name alias, Table<ChatRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> Chat(Table<O> child, ForeignKey<O, ChatRecord> key) {
        super(child, key, CHAT);
    }

    @Override
    public Schema getSchema() {
        return DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.CHAT_BY_TIME, Indexes.CHAT_BY_TIME_2);
    }

    @Override
    public List<ForeignKey<ChatRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<ChatRecord, ?>>asList(Keys.CHAT__CHAT_REPORTED_BY_FKEY, Keys.CHAT__CHAT_SERVER_ID_FKEY);
    }

    public Players players() {
        return new Players(this, Keys.CHAT__CHAT_REPORTED_BY_FKEY);
    }

    public Servers servers() {
        return new Servers(this, Keys.CHAT__CHAT_SERVER_ID_FKEY);
    }

    @Override
    public Chat as(String alias) {
        return new Chat(DSL.name(alias), this);
    }

    @Override
    public Chat as(Name alias) {
        return new Chat(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Chat rename(String name) {
        return new Chat(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Chat rename(Name name) {
        return new Chat(name, null);
    }

    // -------------------------------------------------------------------------
    // Row5 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row5<JSON, Short, Integer, Long, Short> fieldsRow() {
        return (Row5) super.fieldsRow();
    }
}
