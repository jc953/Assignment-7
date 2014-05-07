package a7;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class RingBuffer<T> implements BlockingQueue<T>{

	@Override
	public T element() {
		//NEEDTODO
		return null;
	}

	@Override
	public T peek() {
		//NEEDTODO
		return null;
	}

	@Override
	public T poll() {
		//NEEDTODO
		return null;
	}

	@Override
	public T remove() {
		//NEEDTODO
		return null;
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
		//NEEDTODO
		return false;
	}

	@Override
	public Iterator<T> iterator() {
		//NEEDTODO
		return null;
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
		//NEEDTODO
		return 0;
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
		//NEEDTODO
		return false;
	}

	@Override
	public boolean contains(Object arg0) {
		//NEEDTODO
		return false;
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
		//NEEDTODO
		return false;
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
		//NEEDTODO
		return false;
	}

	@Override
	public T take() throws InterruptedException {
		//NEEDTODO
		return null;
	}

}
