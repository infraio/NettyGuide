package paio;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TimeServerExecutePool implements AutoCloseable {

  public static final int DEFAULT_KEEPALIVE_SECONDS = 120;
  private ExecutorService executor;

  public TimeServerExecutePool(int maxPoolSize, int queueSize) {
    executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), maxPoolSize,
        DEFAULT_KEEPALIVE_SECONDS, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(queueSize));
  }
  
  public void execute(Runnable task) {
    executor.execute(task);
  }

  @Override
  public void close() throws Exception {
    executor.shutdown();
  }
}
