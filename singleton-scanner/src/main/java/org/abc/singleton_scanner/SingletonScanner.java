package org.abc.singleton_scanner;

import java.util.Objects;
import java.util.Scanner;

/**
 * <p>
 * Provides single instance of Scanner class.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class SingletonScanner {

    private static Scanner scanner;

    /**
     * <p>
     * Default constructor of InputHandler class. Kept private to restrict from creating object outside this class.
     * </p>
     */
    private SingletonScanner() {}

    /**
     * <p>
     * Creates a single object of Scanner class and returns it.
     * </p>
     *
     * @return the single instance of Scanner class.
     */
    public static Scanner getScanner() {
        return Objects.isNull(scanner) ? scanner = new Scanner(System.in) : scanner;
    }
}
