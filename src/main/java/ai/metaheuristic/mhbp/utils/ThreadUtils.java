/*
 *    Copyright 2023, Sergio Lissner, Innovation platforms, LLC
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package ai.metaheuristic.mhbp.utils;

import ai.metaheuristic.mhbp.exceptions.CustomInterruptedException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sergio Lissner
 * Date: 4/21/2023
 * Time: 11:35 AM
 */
@Slf4j
public class ThreadUtils {

    public static class CommonThreadLocker<T> {
        private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

        private final Supplier<T> supplier;

        public CommonThreadLocker(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        private T holder = null;

        public T get() {
            try {
                readLock.lock();
                if (holder != null) {
                    return holder;
                }
            } finally {
                readLock.unlock();
            }

            try {
                writeLock.lock();
                if (holder == null) {
                    holder = supplier.get();
                }
            } finally {
                writeLock.unlock();
            }
            return holder;
        }

        @SuppressWarnings("ConstantConditions")
        public void reset() {
            try {
                writeLock.lock();
                holder = null;
            } finally {
                writeLock.unlock();
            }
        }
    }

    public record PatternMatcherResultWithTimeout(@Nullable String s, boolean withTimeout) {}

    // https://stackoverflow.com/a/910798/2672202
    public static class InterruptableCharSequence implements CharSequence {
        CharSequence inner;
        public InterruptableCharSequence(CharSequence inner) {
            this.inner = inner;
        }

        public char charAt(int index) {
            if (Thread.interrupted()) {
                throw new RuntimeException(new InterruptedException());
            }
            return inner.charAt(index);
        }

        public int length() {
            return inner.length();
        }

        public CharSequence subSequence(int start, int end) {
            return new InterruptableCharSequence(inner.subSequence(start, end));
        }

        @Override
        public String toString() {
            return inner.toString();
        }
    }

    public static void checkInterrupted() {
        if (Thread.currentThread().isInterrupted()) {
            throw new CustomInterruptedException();
        }
    }

    public static void waitTaskCompleted(ThreadPoolExecutor executor) throws InterruptedException {
        waitTaskCompleted(executor, 20);
    }

    public static void waitTaskCompleted(ThreadPoolExecutor executor, int numberOfPeriods) throws InterruptedException {
        int i = 0;
        while ((executor.getTaskCount() - executor.getCompletedTaskCount()) > 0) {
            Thread.sleep(TimeUnit.SECONDS.toMillis(1));
            if (++i % numberOfPeriods == 0) {
                System.out.print("total: " + executor.getTaskCount() + ", completed: " + executor.getCompletedTaskCount());
                final Runtime rt = Runtime.getRuntime();
                System.out.println(", free: " + rt.freeMemory() + ", max: " + rt.maxMemory() + ", total: " + rt.totalMemory());
                i = 0;
            }
        }
    }


    public static long execStat(long mills, ThreadPoolExecutor executor) {
        final long curr = System.currentTimeMillis();
        if (log.isInfoEnabled()) {
            final int sec = (int) ((curr - mills) / 1000);
            String s = S.f("\nprocessed %d tasks for %d seconds", executor.getTaskCount(), sec);
            if (sec!=0) {
                s += (", " + (((int) executor.getTaskCount() / sec)) + " tasks/sec");
            }
            log.info(s);
        }
        return curr;
    }

    @SneakyThrows
    public static PatternMatcherResultWithTimeout matchPattern(Pattern p, String text, Duration timeout) {
        final String[] result = new String[1];
        result[0] = null;
        final AtomicBoolean done = new AtomicBoolean(false);
        long mills = System.currentTimeMillis();
        Thread t = new Thread(()-> {
            Matcher matcher = p.matcher(new InterruptableCharSequence(text));
            if (matcher.find()) {
                result[0] = matcher.group();
            }
            done.set(true);
        });
        t.start();
        t.join(timeout.toMillis());
        long endMills = System.currentTimeMillis();
        long time = endMills - mills;
        return done.get() ? new PatternMatcherResultWithTimeout(result[0], false) : new PatternMatcherResultWithTimeout(null, true);
    }

}
