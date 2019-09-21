package bgu.spl.mics;

import bgu.spl.mics.accessories.ReaderWriter;

import java.util.concurrent.TimeUnit;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 *
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> extends ReaderWriter {

	private T _result;
	private final Object _resultLocker = new Object();
	private boolean _isDone ;

	/**
	 * This should be the the only public constructor in this class.
	 */
	public Future() {
		_result = null;
		_isDone = false;
	}

	/**
	 * retrieves the result the Future object holds if it has been resolved.
	 * This is a blocking method! It waits for the computation in case it has
	 * not been completed.
	 * <p>
	 * @return return the result of type T if it is available, if not wait until it is available.
	 *
	 */
	public T get() {
		beforeRead();
		synchronized (_resultLocker){
			while (!isDone()) {
				try {
					_resultLocker.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			_resultLocker.notifyAll();
		}
		afterRead();
		return _result;
	}

	/**
	 * Resolves the result of this Future object.
	 */
	public void resolve (T result) {
		beforeWrite();
		synchronized (_resultLocker) {
			if (_result == null)
				_result = result;
			_isDone = true;
			_resultLocker.notifyAll();
		}
		afterWrite();
	}

	/**
	 * @return true if this object has been resolved, false otherwise
	 */
	public boolean isDone() {
		return _isDone;
	}

	/**
	 * retrieves the result the Future object holds if it has been resolved,
	 * This method is non-blocking, it has a limited amount of time determined
	 * by {@code timeout}
	 * <p>
	 * @param timeout 	the maximal amount of time units to wait for the result.
	 * @param unit		the {@link TimeUnit} time units to wait.
	 * @return return the result of type T if it is available, if not,
	 * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
	 *         elapsed, return null.
	 */
	public T get(long timeout, TimeUnit unit) {
		beforeRead();
		synchronized (_resultLocker){
			if (_result != null) {
				_resultLocker.notifyAll();
				return _result;
			}
			long timeToSleep  = TimeUnit.MILLISECONDS.convert(timeout, unit);
			try {
				_resultLocker.wait(timeToSleep);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			_resultLocker.notifyAll();
		}
		afterRead();
		return _result;
	}

	@Override
	protected void read1() {}

	@Override
	protected void write1() {}
}