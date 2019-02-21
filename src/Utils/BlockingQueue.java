package Utils;


import java.util.LinkedList;
import java.util.Queue;

public class BlockingQueue<T> {

	private int size = -1;
	private Queue<T> queue = new LinkedList<T>();

	public BlockingQueue() {
	}

	public BlockingQueue(int i) {
		if (i > 0)
			this.size = i;
	}

	public synchronized void offer(T e) throws InterruptedException {
		while (queue.size() == size)
			wait();
		queue.add(e);
		notifyAll();
	}

	public synchronized T poll() throws InterruptedException {
		while (queue.isEmpty())
			wait();
		if (size != -1)
			notifyAll();
		return queue.poll();
	}

	public synchronized int size() {
		return queue.size();
	}

	public synchronized void clear() {
		queue.clear();
	}
}
