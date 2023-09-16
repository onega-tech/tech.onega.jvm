package tech.onega.jvm.jdbc.domain;

import tech.onega.jvm.std.annotation.Immutable;
import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.lang.Lambda;
import tech.onega.jvm.std.struct.list.IList;
import tech.onega.jvm.std.struct.list.MList;

@Immutable
@ThreadSafe
public class JdbcModelMapper<M> implements JdbcResultMapper<M> {

  private final String tableName;

  private final Lambda.Function<JdbcRecord, M, Exception> fromRecord;

  private final Lambda.Consumer2<JdbcRecord, M, Exception> toRecord;

  private final IList<String> immutableFields;

  private final IList<String> mutableFields;

  private final IList<String> allFields;

  private final IList<String> idFields;

  private final Lambda.Supplier<JdbcRecord, RuntimeException> recordFactory;

  public JdbcModelMapper(
    final Lambda.Supplier<JdbcRecord, RuntimeException> recordFactory,
    final String tableName,
    final String[] immutableFields,
    final String[] mutableFields,
    final String[] idFields,
    final Lambda.Consumer2<JdbcRecord, M, Exception> toRecord,
    final Lambda.Function<JdbcRecord, M, Exception> fromRecord) {
    //
    this.recordFactory = recordFactory;
    this.tableName = tableName;
    this.immutableFields = IList.of(immutableFields);
    this.mutableFields = IList.of(mutableFields);
    this.idFields = IList.of(idFields);
    this.allFields = MList.<String>create()
      .addAll(immutableFields)
      .addAll(mutableFields)
      .destroy();
    this.fromRecord = fromRecord;
    this.toRecord = toRecord;
  }

  public IList<String> allFields() {
    return this.allFields;
  }

  @Override
  public M fromRecord(final JdbcRecord record) throws Exception {
    return this.fromRecord.invoke(record);
  }

  public IList<String> idFields() {
    return this.idFields;
  }

  public IList<String> immutableFields() {
    return this.immutableFields;
  }

  public IList<String> mutableFields() {
    return this.mutableFields;
  }

  public String tableName() {
    return this.tableName;
  }

  public JdbcRecord toRecord(final M entity) throws Exception {
    final var record = this.recordFactory.invoke();
    this.toRecord.invoke(record, entity);
    return record;
  }

}