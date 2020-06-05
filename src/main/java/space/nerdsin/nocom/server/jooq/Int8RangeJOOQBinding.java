package space.nerdsin.nocom.server.jooq;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.lambda.tuple.Range;

import java.sql.SQLException;

public class Int8RangeJOOQBinding implements Binding<Object, Range<Long>> {
  @Override
  public Converter<Object, Range<Long>> converter() {
    return new Int8RangeJOOQConverter();
  }

  @Override
  public void sql(BindingSQLContext<Range<Long>> ctx) throws SQLException {
    ctx.render()
        .visit(DSL.val(ctx.convert(converter()).value()))
        .sql("::int8range");
  }

  @Override
  public void register(BindingRegisterContext<Range<Long>> ctx) throws SQLException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void set(BindingSetStatementContext<Range<Long>> ctx) throws SQLException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void set(BindingSetSQLOutputContext<Range<Long>> ctx) throws SQLException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void get(BindingGetResultSetContext<Range<Long>> ctx) throws SQLException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void get(BindingGetStatementContext<Range<Long>> ctx) throws SQLException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void get(BindingGetSQLInputContext<Range<Long>> ctx) throws SQLException {
    throw new UnsupportedOperationException();
  }
}
