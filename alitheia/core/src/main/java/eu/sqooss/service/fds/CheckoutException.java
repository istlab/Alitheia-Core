package eu.sqooss.service.fds;

/**
 * Exception thrown when there is an error while constructing, updating
 * or releasing an FDS checkout.
 */
public class CheckoutException extends FDSException {

    public CheckoutException(String message) {
        super(message);
    }

    private static final long serialVersionUID = 1L;

}
