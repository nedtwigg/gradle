/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.cache;

import org.gradle.cache.internal.filelock.FileLockOutcome;
import org.gradle.cache.internal.filelock.LockInfo;

import java.io.File;

/**
 * Thrown on timeout acquiring a lock on a file.
 */
public class LockTimeoutException extends RuntimeException {
    private final File lockFile;

    private LockTimeoutException(String message, File lockFile) {
        super(message);
        this.lockFile = lockFile;
    }

    public static LockTimeoutException timeout(String lockDisplayName, String thisOperation, File lockFile, String thisProcessPid, FileLockOutcome fileLockOutcome, LockInfo lockInfo) {
        if (fileLockOutcome == FileLockOutcome.LOCKED_BY_THIS_PROCESS) {
            String message = String.format("Timeout waiting to lock %s. It is currently in use by another Gradle instance.%nOwner PID: %s%nOur PID: %s%nOwner Operation: %s%nOur operation: %s%nLock file: %s", lockDisplayName, lockInfo.pid, thisProcessPid, lockInfo.operation, thisOperation, lockFile);
            return new LockTimeoutException(message, lockFile);
        } else {
            String message = String.format("Timeout waiting to lock %s. It is currently in use by this Gradle process.Owner Operation: %s%nOur operation: %s%nLock file: %s", lockDisplayName, lockInfo.operation, thisOperation, lockFile);
            return new LockTimeoutException(message, lockFile);
        }
    }

    public File getLockFile() {
        return lockFile;
    }
}
