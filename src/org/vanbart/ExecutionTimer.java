package org.vanbart;

import java.util.function.Function;

/**
 * Times the execution of a closure.
 * 
 * @author Ton van Bart
 * @since 3/4/14 10:01 PM
 */
public class ExecutionTimer {
    
    public static <T,R> R time(Function<T,R> function, T argument) {
        long start = System.currentTimeMillis();
        R result = function.apply(argument);
        System.out.println("execution took " + (System.currentTimeMillis() - start) + "ms");
        return result;
    }
    
}
