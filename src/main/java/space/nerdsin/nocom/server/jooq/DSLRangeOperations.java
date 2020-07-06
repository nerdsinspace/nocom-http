package space.nerdsin.nocom.server.jooq;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.Support;
import org.jooq.impl.DSL;
import org.jooq.lambda.tuple.Range;
import org.jooq.lambda.tuple.Tuple;

public class DSLRangeOperations {
  @Support({SQLDialect.POSTGRES})
  public static <T extends Comparable<T>> Condition rangeOverlaps(Field<Range<T>> f1, Range<T> f2) {
    return DSL.condition("{0} && {1}", f1, DSL.val(f2, f1.getDataType()));
  }

  @Support({SQLDialect.POSTGRES})
  public static <T extends Comparable<T>> Condition rangeOverlaps(Field<Range<T>> f1, T min, T max) {
    return rangeOverlaps(f1, Tuple.range(min, max));
  }

  @Support({SQLDialect.POSTGRES})
  public static <T extends Comparable<T>> Condition rangeIncludes(Field<Range<T>> field, Long value) {
    return DSL.condition("{0} @> {1}", field, DSL.val(value));
  }

  @Support({SQLDialect.POSTGRES})
  public static <T extends Comparable<T>> Field<Object> upperRange(Field<Range<T>> field) {
    return DSL.field("upper({0})", field);
  }
}
