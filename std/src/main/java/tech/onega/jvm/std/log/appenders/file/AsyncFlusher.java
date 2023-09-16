package tech.onega.jvm.std.log.appenders.file;

import java.util.concurrent.TimeUnit;
import com.fasterxml.jackson.annotation.JsonIgnore;
import tech.onega.jvm.std.annotation.ThreadAttached;
import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.lang.Exec;
import tech.onega.jvm.std.struct.buffer.RingBuffer;

@ThreadSafe
abstract class AsyncFlusher<V> implements AutoCloseable {

  private final long flushIntervalMillis;

  @JsonIgnore
  private final Thread thread;

  private volatile boolean running;

  @JsonIgnore
  private RingBuffer<V> currentBuffer;

  @JsonIgnore
  private RingBuffer<V> backupBuffer;

  @JsonIgnore
  private final AtomicExchanger<RingBuffer<V>> bufferExchanger = new AtomicExchanger<>();

  @ThreadSafe
  public AsyncFlusher(
    final String name,
    final long flushIntervalMillis,
    final int bufferSize) {
    this.running = true;
    this.flushIntervalMillis = flushIntervalMillis;
    this.currentBuffer = RingBuffer.create(bufferSize);
    this.backupBuffer = RingBuffer.create(bufferSize);
    this.bufferExchanger.exchange(this.currentBuffer);
    this.thread = new Thread(this::run, name);
  }

  @ThreadSafe
  public boolean add(final V value, final long fullBlockMillis) {
    final RingBuffer<V> buffer = this.getBuffer(fullBlockMillis);
    if (buffer == null) {
      return false;
    }
    buffer.add(value);
    this.bufferExchanger.exchange(buffer);
    return true;
  }

  @ThreadSafe
  @Override
  public void close() {
    this.running = false;
    Exec.quietly(this.thread::join);
  }

  @ThreadSafe
  private RingBuffer<V> getBuffer(final long ifFullBlockMillis) {
    RingBuffer<V> buffer = null;
    while (this.running) {
      buffer = this.bufferExchanger.exchange(null);
      if (buffer != null && buffer.isFull() && ifFullBlockMillis > 0) {
        this.bufferExchanger.exchange(buffer);
        buffer = null;
        try {
          TimeUnit.MILLISECONDS.sleep(ifFullBlockMillis);
        }
        catch (final InterruptedException e) {
          return null;
        }
      }
      else {
        return buffer;
      }
    }
    return null;
  }

  @ThreadAttached
  protected void onClose() throws InterruptedException {
  }

  @ThreadAttached
  protected void onFlush(final Iterable<V> values, final int count) throws InterruptedException {
  }

  @ThreadAttached
  protected void onStart() throws InterruptedException {
  }

  @ThreadAttached
  private void run() {
    try {
      this.onStart();
      while (this.running) {
        try {
          final long start = System.currentTimeMillis();
          final RingBuffer<V> buffer = this.switchBuffer();
          if (buffer.isNotEmpty()) {
            this.onFlush(buffer, buffer.size());
            buffer.clear();
          }
          final long flushDuration = System.currentTimeMillis() - start;
          final long delayMillis = this.flushIntervalMillis - flushDuration;
          if (delayMillis > 0) {
            TimeUnit.MILLISECONDS.sleep(delayMillis);
          }
          else {
            Thread.yield();
          }
        }
        catch (final InterruptedException e) {
          this.running = false;
          break;
        }
      }
      final RingBuffer<V> buffer = this.switchBuffer();
      if (buffer.isNotEmpty()) {
        this.onFlush(buffer, buffer.size());
        buffer.clear();
      }
      this.onClose();
    }
    catch (final InterruptedException e) {
      //ignore
    }
  }

  @ThreadSafe
  public void start() {
    this.thread.start();
  }

  @ThreadSafe
  private RingBuffer<V> switchBuffer() throws InterruptedException {
    final RingBuffer<V> buffer = this.bufferExchanger.exchange(null);
    this.currentBuffer = this.backupBuffer;
    this.backupBuffer = buffer;
    this.bufferExchanger.exchange(this.currentBuffer);
    return buffer;
  }

}
