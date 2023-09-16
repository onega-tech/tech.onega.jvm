package tech.onega.jvm.std.concurrent;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import tech.onega.jvm.std.lang.Lambda;

public class Lock {

  private final ReadWriteLock lock = new ReentrantReadWriteLock();

  public ReadWriteLock asReadWriteLock() {
    return lock;
  }

  public <R> R readLock(Lambda.Supplier<R, Throwable> lambda) {
    lock.readLock().lock();
    try {
      return lambda.invoke();
    }
    catch (Throwable e) {
      throw new RuntimeException(e);
    }
    finally {
      lock.readLock().unlock();
    }
  }

  public void readLock(Lambda.Void<Throwable> lambda) {
    readLock(() -> {
      lambda.invoke();
      return null;
    });
  }

  public <R> R writeLock(Lambda.Supplier<R, Throwable> lambda) {
    lock.writeLock().lock();
    try {
      return lambda.invoke();
    }
    catch (Throwable e) {
      throw new RuntimeException(e);
    }
    finally {
      lock.writeLock().unlock();
    }
  }

  public void writeLock(Lambda.Void<Throwable> lambda) {
    writeLock(() -> {
      lambda.invoke();
      return null;
    });
  }

}
