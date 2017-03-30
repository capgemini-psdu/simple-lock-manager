package com.capgemini.lock;

import com.capgemini.lock.LockManager;
import com.capgemini.lock.LockManagerConfig;
import com.capgemini.lock.exception.LockFailException;
import com.capgemini.lock.exception.UnlockFailException;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit test
 */
public class TestLockManager {

    public static final String LOCK_LIST = "MyLockList";
    HazelcastInstance hz;
    LockManager lockManager;

    @Before
    public void setup() {
        hz = Hazelcast.newHazelcastInstance();
        lockManager = LockManager.getInstance(LOCK_LIST, new LockManagerConfig("","","",0));
        lockManager.clearAllLocks();
    }

    @After
    public void tearDown() {
        hz.shutdown();
        lockManager.shutdown();
    }

    @Test
    public void testLockSuccessful() {

        String lockObject = "someFile";

        try {
            lockManager.aquireLock(lockObject);
        } catch (LockFailException lfe) {
            fail();
        }

        IList<Object> locks = hz.getList(LOCK_LIST);
        assertTrue(locks.contains(lockObject));

    }

    @Test
    public void testLockDuplicate() {

        String lockObject = "someFile";
        try {
            lockManager.aquireLock(lockObject);
            lockManager.aquireLock(lockObject);
            fail();
        } catch (LockFailException lfe) {

        }
        IList<Object> locks = hz.getList(LOCK_LIST);
        assertTrue(Collections.frequency(locks, lockObject) == 1);


    }

    @Test
    public void testUnlockSuccessful() {

        String lockObject1 = "someFile1";
        String lockObject2 = "someFile2";

        try {
            lockManager.aquireLock(lockObject1);
            lockManager.aquireLock(lockObject2);
            lockManager.releaseLock(lockObject1);
        } catch (Exception lfe) {
            if (lfe instanceof UnlockFailException) {
                fail();
            }
        }

        IList<Object> locks = hz.getList(LOCK_LIST);
        assertTrue(locks.contains(lockObject2));

    }

    @Test
    public void testUnlockFail() {

        String lockObject1 = "someFile1";
        String lockObject2 = "someFile2";

        try {
            lockManager.aquireLock(lockObject1);
            lockManager.aquireLock(lockObject2);
            lockManager.releaseLock(lockObject1);
            lockManager.releaseLock(lockObject1);
            fail();
        } catch (Exception lfe) {
            if (lfe instanceof UnlockFailException) {
                IList<Object> locks = hz.getList(LOCK_LIST);
                assertTrue(!locks.contains(lockObject1));
                assertTrue(locks.contains(lockObject2));
            }
        }

    }

    @Test
    public void testListLocks() {

        String lockObject1 = "someFile1";
        String lockObject2 = "someFile2";
        String lockObject3 = "someFile3";
        String lockObject4 = "someFile4";
        String lockObject5 = "someFile5";

        try {
            lockManager.aquireLock(lockObject1);
            lockManager.aquireLock(lockObject2);
            lockManager.aquireLock(lockObject3);
            lockManager.aquireLock(lockObject4);
            lockManager.aquireLock(lockObject5);

            lockManager.releaseLock(lockObject3);
            lockManager.releaseLock(lockObject5);
        } catch (Exception lfe) {
            fail();
        }

        List<Object> locks = lockManager.listLocks();
        assertTrue(locks.contains(lockObject1));
        assertTrue(locks.contains(lockObject2));
        assertTrue(!locks.contains(lockObject3));
        assertTrue(locks.contains(lockObject4));
        assertTrue(!locks.contains(lockObject5));

    }

    @Test
    public void testListLocksForConcurrency() {

        String lockObject1 = "someFile1";
        String lockObject2 = "someFile2";
        String lockObject3 = "someFile3";
        String lockObject4 = "someFile4";
        String lockObject5 = "someFile5";

        try {
            lockManager.aquireLock(lockObject1);
            lockManager.aquireLock(lockObject2);
            lockManager.aquireLock(lockObject3);
            lockManager.aquireLock(lockObject4);
            lockManager.aquireLock(lockObject5);

            lockManager.releaseLock(lockObject3);
            lockManager.releaseLock(lockObject5);
        } catch (Exception lfe) {
            fail();
        }

        List<Object> locks = lockManager.listLocks();
        assertTrue(locks.contains(lockObject1));
        assertTrue(locks.contains(lockObject2));
        assertTrue(!locks.contains(lockObject3));
        assertTrue(locks.contains(lockObject4));
        assertTrue(!locks.contains(lockObject5));

        locks.remove(lockObject1);
        locks = lockManager.listLocks();
        assertTrue(locks.contains(lockObject1));

    }
}
