package bgu.spl.mics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {


    private static class MessageBusImplHolder {
        private static MessageBusImpl _messageBus = new MessageBusImpl();
    }

    // this hash map represents each micro service and its queue
    // whenever a micro service's message vector is null, it means it has been unregistered
    private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> _messagesQueues;

    // this hash map represents events as keys
    // and the value of each event is the array list which holds all micro services subscribed to a certain event
    private final ConcurrentHashMap<Class, ArrayList<MicroService>> _eventSubscriptions;

    // this hash map represents broadcasts as keys
    // and the value of each broadcast is the array list which holds all micro services subscribed to a certain broadcast
    private final ConcurrentHashMap<Class,ArrayList<MicroService> >_broadcastSubscriptions;

    // this hash map represents messages as keys
    // and the value of each message is the future object represents the result might become out of the event
    private ConcurrentHashMap<Message,Future> _messagesAndFutures;

    // this hash map represents messages as keys
    // and the value of each message is the last index of micro service that got the message
    private HashMap<Class, AtomicInteger> _roundRobinNum;

    public static MessageBusImpl getInstance()
    {
        return MessageBusImplHolder._messageBus;
    }

    private MessageBusImpl()
    {
        _messagesQueues = new ConcurrentHashMap<>();
        _eventSubscriptions = new ConcurrentHashMap<>();
        _broadcastSubscriptions = new ConcurrentHashMap<>();
        _messagesAndFutures = new ConcurrentHashMap<>();
        _roundRobinNum = new HashMap<>();
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m)
    {
        synchronized (_eventSubscriptions){
            if (!_messagesQueues.keySet().contains(m) || _messagesQueues.get(m) == null) {
                System.out.println(" event subscribtion failed");
                return;
            }
            if (!_eventSubscriptions.keySet().contains(type)){
                _eventSubscriptions.put(type, new ArrayList<>());
                _roundRobinNum.put(type,new AtomicInteger(0));
            }
            _eventSubscriptions.get(type).add(m);
        }
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m)
    {
        synchronized (_broadcastSubscriptions){
            if (!_messagesQueues.keySet().contains(m) || _messagesQueues.get(m) == null)
            {
                return;
            }
            if (!_broadcastSubscriptions.keySet().contains(type)){
                _broadcastSubscriptions.put(type, new ArrayList<>());
            }
            _broadcastSubscriptions.get(type).add(m);
        }

    }

    @Override
    public <T> void complete(Event<T> e, T result)
    {
        Future future = _messagesAndFutures.get(e);
        if (future != null)
            future.resolve(result);
    }

    @Override
    public void sendBroadcast(Broadcast b)
    {
        // firstly, retrieves micro services subscribed to broadcast b
        ArrayList<MicroService> subscribers = _broadcastSubscriptions.get(b.getClass());
        if (subscribers == null)        // if b appears to be not found, return
            return;
        synchronized (subscribers) {
            for (MicroService microService : subscribers)   // b's vector is found and it might hold micro services which want to get b
            {
                LinkedBlockingQueue queue = _messagesQueues.get(microService);
                if (queue != null) {
                    try {
                        queue.put(b);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        Future future = null;
        AtomicInteger atomicInteger = _roundRobinNum.get(e.getClass());
        if (atomicInteger != null){
            int currIndex = atomicInteger.get();
            while (!atomicInteger.compareAndSet(currIndex,currIndex+1)){
                currIndex = atomicInteger.get();
            }
            MicroService microService = null;
            LinkedBlockingQueue microServiceQueue = null;
            synchronized (_eventSubscriptions.get(e.getClass())) {      //sync the specific vector so unregister method is not allowed to remove it
                int vectorSize = _eventSubscriptions.get(e.getClass()).size();
                if (vectorSize > 0) {
                    microService = _eventSubscriptions.get(e.getClass()).get(currIndex % vectorSize);
                    microServiceQueue = _messagesQueues.get(microService);
                }
            }
            if (microServiceQueue != null) {
                synchronized (microServiceQueue) {
                    if (_messagesQueues.keySet().contains(microService)) {
                        future = new Future<>();
                        _messagesAndFutures.put(e, future);
                        try {
                            microServiceQueue.put(e);
                        } catch (InterruptedException e1) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        }
        return future;
    }


    /**
     * Finds the micro service that is going to get the {@code e} event and return it
     * @param e
     * @param <T>
     * @return
     */
    private <T> MicroService roundRobinAlgo(Event<T> e) {
        MicroService m = null;

        return m;
    }

    @Override
    public void register(MicroService m)
    {
        _messagesQueues.put(m,new LinkedBlockingQueue<>());
    }

    @Override
    public void unregister(MicroService m)
    {
        for (ArrayList<MicroService> vector : _eventSubscriptions.values()){
            if (vector.contains(m)){
                synchronized (vector){
                    vector.remove(m);
                }
            }
        }
        for (ArrayList<MicroService> vector : _broadcastSubscriptions.values()){
            if (vector.contains(m)){
                synchronized (vector){
                    vector.remove(m);
                }
            }
        }
        synchronized (_messagesQueues.get(m)) {
            for (Message msg : _messagesQueues.get(m))
            {
                Future tmpFuture = _messagesAndFutures.get(msg);
                if (tmpFuture != null) {
                    tmpFuture.resolve(null);
                    System.out.println("REACHED HERE");
                }
            }
            _messagesQueues.remove(m);
        }
    }

    private void safeRemove(MicroService m) {

    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        Message msg;
        LinkedBlockingQueue<Message> mQueue = null;
        mQueue = _messagesQueues.get(m);
        if (mQueue == null)
            return null;
        msg = _messagesQueues.get(m).take();
        return msg;
    }
}

