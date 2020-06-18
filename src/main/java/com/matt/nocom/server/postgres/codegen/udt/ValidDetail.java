/*
 * This file is generated by jOOQ.
 */
package com.matt.nocom.server.postgres.codegen.udt;


import com.matt.nocom.server.postgres.codegen.DefaultSchema;
import com.matt.nocom.server.postgres.codegen.udt.records.ValidDetailRecord;
import org.jooq.Schema;
import org.jooq.UDTField;
import org.jooq.impl.DSL;
import org.jooq.impl.SchemaImpl;
import org.jooq.impl.UDTImpl;

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
public class ValidDetail extends UDTImpl<ValidDetailRecord> {

    private static final long serialVersionUID = 1195773028;

    /**
     * The reference instance of <code>valid_detail</code>
     */
    public static final ValidDetail VALID_DETAIL = new ValidDetail();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ValidDetailRecord> getRecordType() {
        return ValidDetailRecord.class;
    }

    /**
     * The attribute <code>valid_detail.valid</code>.
     */
    public static final UDTField<ValidDetailRecord, Boolean> VALID = createField(DSL.name("valid"), org.jooq.impl.SQLDataType.BOOLEAN, VALID_DETAIL, "");

    /**
     * The attribute <code>valid_detail.reason</code>.
     */
    public static final UDTField<ValidDetailRecord, String> REASON = createField(DSL.name("reason"), org.jooq.impl.SQLDataType.VARCHAR, VALID_DETAIL, "");

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public static final UDTField<ValidDetailRecord, Object> LOCATION = createField(DSL.name("location"), org.jooq.impl.DefaultDataType.getDefaultDataType("\"public\".\"geometry\""), VALID_DETAIL, "");

    /**
     * No further instances allowed
     */
    private ValidDetail() {
        super("valid_detail", null, null, false);
    }

    @Override
    public Schema getSchema() {
        return DefaultSchema.DEFAULT_SCHEMA != null ? DefaultSchema.DEFAULT_SCHEMA : new SchemaImpl(DSL.name(""));
    }
}
