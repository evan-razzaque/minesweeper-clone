/**
 * Represents a function that takes 3 arguments and returns a boolean.
 * @param <A> The type of the first argument to the function
 * @param <B> The type of the second argument to the function
 * @param <C> The type of the third argument to the function
 */
@FunctionalInterface
public interface TriFunction<A, B, C> {
    /**
     * Applies this function to the given arguments.
     * @param a The first function argument
     * @param b The second function argument
     * @param c The third function arguemnt
     * @return true or false
     */
    boolean apply(A a, B b, C c);
}
