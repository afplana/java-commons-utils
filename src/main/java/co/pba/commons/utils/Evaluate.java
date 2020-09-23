package co.pba.commons.utils;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Simple custom helper condition class for evaluate predicates and execute actions based on the
 * results (BETA)
 *
 * @param <T>
 */
public final class Evaluate<T> {

    private T value;
    private boolean globalCondition;

    private Evaluate(T value) {
        globalCondition = false;
        this.value = value;
    }

    /**
     * Init val variable for perform evaluations and update or get it's value
     *
     * @param val variable to eval
     * @param <T> variable abstract type
     * @return new Conditions instance of val
     */
    public static <T> Evaluate<T> of(T val) {
        Objects.requireNonNull(val);
        return new Evaluate<T>(val);
    }

    /**
     * Eval conditions to exec actions if condition true
     *
     * @param predicate Condition to eval
     * @param consumer  action to execute
     * @return current instance of val
     */
    public Evaluate<T> _if(Predicate<? super T> predicate, Consumer<? super T> consumer) {
        Objects.requireNonNull(predicate);
        globalCondition = predicate.test(value);
        if (predicate.test(value)) {
            Objects.requireNonNull(consumer);
            consumer.accept(value);
        }
        return this;
    }

    public Evaluate<T> _if(boolean condition, Consumer<? super T> consumer) {
        if (condition) {
            Objects.requireNonNull(consumer);
            consumer.accept(value);
        }
        return this;
    }


    /**
     * If conditions is false and orElse is called then actions will be executed over val
     *
     * @param consumer actions to execute
     * @return current instance of val
     */
    public Evaluate<T> or(Consumer<? super T> consumer) {
        if (!globalCondition) {
            Objects.requireNonNull(consumer);
            consumer.accept(value);
        }
        return this;
    }

    /**
     * If global condition eval to false can be used to provide an exception to throw
     *
     * @param ex  exception to throw
     * @param <E> exception type that extends runtime
     * @throws E exception
     */
    public <E extends RuntimeException> void orThrow(Supplier<? extends E> ex) throws E {
        if (!globalCondition) {
            value = null;
            throw ex.get();
        }
    }

    /**
     * If eval new condition that will not classify as global and if eval to true can be used to
     * provide an exception to throw
     *
     * @param predicate condition to eval
     * @param ex        exception to throw
     * @param <E>       exception type that extends runtime
     * @throws E exception
     */
    public <E extends RuntimeException> void ifTrow(Predicate<? super T> predicate,
                                                    Supplier<? extends E> ex) throws E {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(ex);
        if (predicate.test(value)) {
            value = null;
            throw ex.get();
        }
    }


    // Only works conditionally but can be out of scope (needs improvements)
    public <V> void lastly(V v, Consumer<V> action) {
        Objects.requireNonNull(action);
        Objects.requireNonNull(v);
        if (value != null) {
            action.accept(v);
        }
    }

    /**
     * Use this method to get the value that was evaluated in previous expressions
     *
     * @return val
     * @throws NoSuchElementException if {@code val == null}
     */
    public T get() throws NoSuchElementException {
        if (value == null) {
            throw new NoSuchElementException("Not valid result item");
        }
        T aux = value;
        // prepare val for garbage collection and allow to control that no further ops are performed
        value = null;
        return aux;
    }
}

