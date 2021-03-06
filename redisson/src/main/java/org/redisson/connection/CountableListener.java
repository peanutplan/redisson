/**
 * Copyright 2018 Nikita Koksharov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.redisson.connection;

import java.util.concurrent.atomic.AtomicInteger;

import org.redisson.misc.RPromise;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

/**
 * 
 * @author Nikita Koksharov
 *
 */
public class CountableListener<T> implements FutureListener<Object> {
    
    protected final AtomicInteger counter = new AtomicInteger();
    protected final RPromise<T> result;
    protected final T value;
    
    public CountableListener() {
        this(null, null);
    }
    
    public CountableListener(RPromise<T> result, T value) {
        super();
        this.result = result;
        this.value = value;
    }
    
    public void setCounter(int newValue) {
        counter.set(newValue);
    }
    
    public void incCounter() {
        counter.incrementAndGet();
    }
    
    @Override
    public void operationComplete(Future<Object> future) throws Exception {
        if (!future.isSuccess()) {
            if (result != null) {
                result.tryFailure(future.cause());
            }
            return;
        }
        
        if (counter.decrementAndGet() == 0) {
            onSuccess(value);
            if (result != null) {
                result.trySuccess(value);
            }
        }
    }

    protected void onSuccess(T value) {
    }

}
