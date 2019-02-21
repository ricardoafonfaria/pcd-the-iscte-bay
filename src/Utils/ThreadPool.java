package Utils;

public class ThreadPool {

	private BlockingQueue<Runnable> queue;

	public ThreadPool(int n) {
		queue = new BlockingQueue<Runnable>();
		for (int i = 0; i < n; i++) {
			new WorkerThread(queue).start();
		}
	}

	public void submit(Runnable runnable) {
		try {
			queue.offer(runnable);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
