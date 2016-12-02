package se.mathenri.bandapp;

/**
 * Created by MattiasHe on 2016-12-01.
 */

public class UnexpectedResponseCodeException extends Exception {
    public UnexpectedResponseCodeException(String message) {
        super(message);
    }
}
