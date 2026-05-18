package cz.cvut.fel.nss.order.exception;

public class EmptyBasketException extends RuntimeException {
    public EmptyBasketException(String message) {
        super(message);
    }
}
