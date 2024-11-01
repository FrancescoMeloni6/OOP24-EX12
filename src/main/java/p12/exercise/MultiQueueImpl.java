package p12.exercise;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class MultiQueueImpl<T, Q> implements MultiQueue<T, Q>{

    private HashMap<Q, LinkedHashSet<T>> multiQueue;

    public <T, Q>MultiQueueImpl() {
        multiQueue = new HashMap<>();
    }

    @Override
    public Set<Q> availableQueues() {
        return multiQueue.keySet();
    }

    @Override
    public void openNewQueue(Q queue) {
        ThrowIfQueueAvailable(queue);

        multiQueue.put(queue, new LinkedHashSet<>());
    }

    @Override
    public boolean isQueueEmpty(Q queue) {
        ThrowIfQueueNotAvailable(queue);

        return multiQueue.get(queue).isEmpty();
    }

    @Override
    public void enqueue(T elem, Q queue) {
        ThrowIfQueueNotAvailable(queue);

        multiQueue.get(queue).addLast(elem);
    }

    @Override
    public T dequeue(Q queue) {
        ThrowIfQueueNotAvailable(queue);
        
        LinkedHashSet<T> tmp = multiQueue.get(queue);
        if (tmp.isEmpty()) {
            return null;
        }
        T elem = tmp.removeFirst();
        multiQueue.put(queue, tmp);

        return elem;
    }

    @Override
    public Map<Q, T> dequeueOneFromAllQueues() {
        HashMap<Q, T> dequeuedEntries = new HashMap<>();

        for (Q queue : availableQueues()) {
            dequeuedEntries.put(queue, dequeue(queue)); 
        }

        return dequeuedEntries;
    }

    @Override
    public Set<T> allEnqueuedElements() {
        LinkedHashSet<T> enqueuedElements = new LinkedHashSet<>();
        
        for (Q queue : availableQueues()) {
            enqueuedElements.addAll(multiQueue.get(queue));
        }

        return enqueuedElements;
    }

    @Override
    public List<T> dequeueAllFromQueue(Q queue) {
        ThrowIfQueueNotAvailable(queue);

        List<T> dequeuedQueue = List.copyOf(multiQueue.get(queue));
        multiQueue.get(queue).clear();

        return dequeuedQueue;
    }

    @Override
    public void closeQueueAndReallocate(Q queue) {
        ThrowIfQueueNotAvailable(queue);
        ThrowIfSingleQueueRemaining();

        List<T> tmp = dequeueAllFromQueue(queue);
        multiQueue.remove(queue);
        multiQueue.get(availableQueues().iterator().next()).addAll(tmp);
    }

    private void ThrowIfQueueAvailable(Q queue) {
        if (multiQueue.containsKey(queue)) {
            throw new IllegalArgumentException("The queue " + queue + " is already available");
        }
    }

    private void ThrowIfQueueNotAvailable(Q queue) {
        if (!multiQueue.containsKey(queue)) { 
            throw new IllegalArgumentException("The queue " + queue + " does not exist");
        }
    }

    private void ThrowIfSingleQueueRemaining() {
        if (availableQueues().size() <= 1) {
            throw new IllegalStateException("There is no alternative queue for moving elements to");
        }
    }

}
