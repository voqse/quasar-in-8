
package com.voqse.nixieclock;

/**
 * Indicates not fatal error.
 * <p/>
 * Useful for Crashlytics logging.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class NonFatalError extends Exception {

    public NonFatalError(String detailMessage) {
        super(detailMessage);
    }
}
