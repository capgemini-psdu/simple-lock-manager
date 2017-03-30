package com.capgemini.lock;

import com.capgemini.lock.exception.LockFailException;
import com.capgemini.lock.exception.UnlockFailException;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IList;
import com.hazelcast.core.ILock;

import java.util.ArrayList;
import java.util.List;

/**
 * JIRA-ID
 * DESCRIPTION
 * <p>
 * Created by jzbhhx on 25/08/16.
 */
public class LockManager {

    private static LockManager instance;

    private HazelcastInstance hz;
    private String lock_List;

    private LockManager(LockManagerConfig config, String lockList) {
        hz = Hazelcast.newHazelcastInstance(config.getConfig());
        lock_List = lockList;
    }

    /**
     * Factory method to create a lock manager with the supplied config containing a named lockList
     *
     * @param lockList named lock list
     * @param config   hazelcast config
     * @return LockManager instance
     */
    public static LockManager getInstance(String lockList, LockManagerConfig config) {
        if (instance == null)
            instance = new LockManager(config, lockList);
        return instance;
    }

    /**
     * Attempts to acquire a lock on the lockable.
     * Internally adds the lockable to a hazelcast IList.
     * If there is an duplicate lockable in the hazelcast IList then a LockFailException is thrown
     *
     * @param lockable the object that is being locked
     * @return true for success
     * @throws LockFailException when a duplicate is detected
     */
    public boolean aquireLock(Object lockable) throws LockFailException {
        IList<Object> lockList = hz.getList(this.lock_List);
        ILock lock = hz.getLock(lock_List);

        lock.lock();
        try {
            if (lockList.contains(lockable)) {
                throw new LockFailException("Cannot lock, Lockable object already locked");
            }
            lockList.add(lockable);
        } finally {
            lock.unlock();
        }
        return true;
    }

    /**
     * Attempts to release a lock on the lockable.
     * Internally removes the lockable from the list.
     *
     * @param lockable the object to be locked
     * @return true for success
     * @throws UnlockFailException if lockable is not currently locked
     */
    public boolean releaseLock(Object lockable) throws UnlockFailException {
        IList<Object> lockList = hz.getList(this.lock_List);
        ILock lock = hz.getLock(lock_List);

        lock.lock();
        try {
            if (!lockList.contains(lockable))
                throw new UnlockFailException("Cannot unlock, Lockable object not locked");
            lockList.remove(lockable);
        } finally {
            lock.unlock();
        }
        return true;
    }

    /**
     * Returns a copy of the list of locks.
     *
     * @return lock list
     */
    public List<Object> listLocks() {
        IList<Object> lockList = hz.getList(this.lock_List);
        ILock lock = hz.getLock(lock_List);

        lock.lock();
        try {
            return new ArrayList<Object>(lockList);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Convenience method to clear all existing locks.
     *
     * @return true for success
     */
    public boolean clearAllLocks() {
        IList<Object> lockList = hz.getList(this.lock_List);
        ILock lock = hz.getLock(lock_List);

        lock.lock();
        try {
            lockList.clear();
        } finally {
            lock.unlock();
        }
        return true;
    }

    /**
     * Shutdown the lock manager and underlying hazelcast instances.
     *
     * @return true for success
     */
    public boolean shutdown() {
        instance = null;
        hz.shutdown();
        return true;
    }
}
