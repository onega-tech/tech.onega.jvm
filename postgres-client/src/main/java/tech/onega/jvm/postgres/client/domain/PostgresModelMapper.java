package tech.onega.jvm.postgres.client.domain;

import tech.onega.jvm.std.annotation.Immutable;
import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.lang.Lambda;
import tech.onega.jvm.std.struct.list.IList;
import tech.onega.jvm.std.struct.list.MList;

@Immutable
@ThreadSafe
public class PostgresModelMapper<M> implements PostgresResultMapper<M> {

  private final String tableName;

  private final Lambda.Function<PostgresRecord, M, Exception> fromRecord;

  private final Lambda.Consumer2<PostgresRecord, M, Exception> toRecord;

  private final IList<String> immutableFields;

  private final IList<String> mutableFields;

  private final IList<String> allFields;

  private final IList<String> idFields;

  private final Lambda.Supplier<PostgresRecord, RuntimeException> recordFactory;

  public PostgresModelMapper(
    final Lambda.Supplier<PostgresRecord, RuntimeException> recordFactory,
    final String tableName,
    final String[] immutableFields,
    final String[] mutableFields,
    final String[] idFields,
    final Lambda.Consumer2<PostgresRecord, M, Exception> toRecord,
    final Lambda.Function<PostgresRecord, M, Exception> fromRecord) {
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
  public M fromRecord(final PostgresRecord record) throws Exception {
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

  public PostgresRecord toRecord(final M entity) throws Exception {
    final var record = this.recordFactory.invoke();
    this.toRecord.invoke(record, entity);
    return record;
  }

}