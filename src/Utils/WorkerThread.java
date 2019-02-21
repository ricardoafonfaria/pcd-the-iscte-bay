package Utils;

public class WorkerThread extends Thread {

	BlockingQueue<Runnable> queue;

	public WorkerThread(BlockingQueue<Runnable> queue) {
		this.queue = queue;
	}

	@Override
	public void run() {
		while (!isInterrupted()) {
			try {
				queue.poll().run();
			} catch (InterruptedException e) {
				try {
					wait();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
