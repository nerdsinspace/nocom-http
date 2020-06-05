package space.nerdsin.nocom.server.jooq;

import org.jooq.Converter;
import org.jooq.lambda.tuple.Range;
import org.jooq.lambda.tuple.Tuple;

import java.util.regex.Pattern;

public class Int8RangeJOOQConverter implements Converter<Object, Range<Long>> {
  private static final Pattern PATTERN = Pattern.compile("[\\[\\(](.*?),(.*?)[\\]\\)]");

  @Override
  public Range<Long> from(Object databaseObject) {
    if (databaseObject == null) {
      return null;
    }

    var matcher = PATTERN.matcher(String.valueOf(databaseObject));

    if (matcher.find()) {
      var min = Long.parseLong(matcher.group(1));
      var max = Long.parseLong(matcher.group(2));
      return Tuple.range(min, max);
    }

    throw new IllegalArgumentException("Could not parse \"" + databaseObject + "\" as int8range");
  }

  @Override
  public Object to(Range<Long> userObject) {
    return userObject == null ? null : userObject.toString();
  }

  @Override
  public Class<Object> fromType() {
    return Object.class;
  }

  @Override
  public Class<Range<Long>> toType() {
    return (Class) Range.class;
  }
}
