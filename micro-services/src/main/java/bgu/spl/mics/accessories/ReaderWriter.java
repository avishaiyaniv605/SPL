package bgu.spl.mics.accessories;

/**
 * This class is a ReaderWriter pattern which manages the locks of an object uses it
 * It retrieves a number in the constructor which defines the number of threads can access the object in the same time
 */
public abstract class ReaderWriter {

    protected int _activeReaders;
    protected int _activeWriters;
    protected int _waitingWriters;

    /**
     * read object process
     */
    public void read(){
        beforeRead();
        read1();
        afterRead();
    }

    /**
     * checks the conditions before it reads the data
     */
    protected synchronized void beforeRead() {
        while(!allowRead()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        _activeReaders ++;

    }

    /**
     * reading operation
     */
    protected abstract void read1();

    /**
     * changes the conditions and notifies that a certain thread finished reading
     */
    protected synchronized void afterRead() {
        _activeReaders -- ;
        notifyAll();
    }

    /**
     * checks if reading is allowed
     * @return boolean which tells if a certain reading can be made (=true) or not (=false)
     */
    private boolean allowRead() {
        return _activeWriters == 0 && _waitingWriters == 0;
    }

    /**
     * write object process
     */
    public void write() {
        beforeWrite();
        write1();
        afterWrite();
    }

    /**
     * checks the conditions before it changes the data
     */
    protected synchronized void beforeWrite() {
        _waitingWriters ++;
        while (!allowWrite()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        _waitingWriters --;
        _activeWriters ++;
    }

    /**
     * writing operation
     */
    protected abstract void write1();

    /**
     * changes the conditions and notifies that a certain thread finished writing
     */
    protected synchronized void afterWrite() {
        _activeWriters --;
        notifyAll();
    }

    /**
     * checks if writing is allowed
     * @return boolean which tells if a certain writing can be made (=true) or not (=false)
     */
    private boolean allowWrite() {
        return _activeWriters == 0;
    }





}
