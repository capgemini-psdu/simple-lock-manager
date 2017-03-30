# Simple Lock Manager

A simple distributed lock manager.

Uses Hazelcast to create a distributed list of lockable objects across multiple nodes.


```java
    lockManager = LockManager.getInstance("MyLockList", new LockManagerConfig());
    String lockObject = "someObject";

    try {
        lockManager.aquireLock(lockObject);
    } catch (LockFailException lfe) {
        System.out.print("lock failed");
    }
    
    try {
        lockManager.releaseLock(lockObject);
    } catch (UnlockFailException ufe) {
        System.out.print("unlock failed");
    }
```

