package a7;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class RingBuffer<T> implements BlockingQueue<T>{
	private int index;
	private int tail;
	private int capacity1;
	private T[] threads;
	
	public RingBuffer(int capacity){
		index = 0;
		tail = 0;
		capacity1 = capacity;
		threads = new T[20];
	}
	
	@Override
	public T element() {
		return threads[index];
	}

	@Override
	public T peek() {
		if (index >= threads.length){
			return null;
		}
		return threads[index];
	}

	@Override
	public T poll() {
		if (index >= threads.length){
			return null;
		}
		return remove();
	}

	@Override
	public T remove() {
		T temp = threads[index];
		index++;
		return temp;
	}

	@Override
	public boolean addAll(Collection<? extends T> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEmpty() {
		return index >= threads.length;
	}

	@Override
	public Iterator<T> iterator() {
		return Arrays.asList(threads).iterator();
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return threads.length;
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean add(T arg0) {
		if (threads.length-index > capacity){
			throw new IllegalStateException();
		}
		threads = Arrays.copyOf(threads, threads.length+1);
		threads[threads.length-1] = arg0;
		return true;
	}

	@Override
	public boolean contains(Object arg0) {
		boolean result = false;
		for (int i = 0; i < threads.length; i++){
			if (threads[i].equals(arg0)){
				result = true;
			}
		}
		return result;
	}

	@Override
	public int drainTo(Collection<? super T> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int drainTo(Collection<? super T> arg0, int arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean offer(T arg0) {
		if (threads.length-index > capacity){
			return false;
		}
		threads = Arrays.copyOf(threads, threads.length+1);
		threads[threads.length-1] = arg0;
		return true;
	}

	@Override
	public boolean offer(T arg0, long arg1, TimeUnit arg2)
			throws InterruptedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public T poll(long arg0, TimeUnit arg1) throws InterruptedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void put(T arg0) throws InterruptedException {
		//NEEDTODO
		
	}

	@Override
	public int remainingCapacity() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object arg0) {
		for (int i = 0; i < threads.length; i++){
			if (threads[i].equals(arg0)){
				T[] thread1 = Arrays.copyOfRange(threads, 0, i);
				T[] thread2 = Arrays.copyOfRange(threads, i+1, threads.length);
				threads.
				return true;
			}
		}
		return false;
	}

	@Override
	public T take() throws InterruptedException {
		//NEEDTODO
		return null;
	}

}
