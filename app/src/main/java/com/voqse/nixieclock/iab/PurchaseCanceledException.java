package com.voqse.nixieclock.iab;

/**
 * Exception, сигнализирующий о том, что пользователь закрыл диалог покупок.
 * Нужен только для того, чтобы в багтреккере (Fabric) было легко отделить реальные проблемы покупок и закрытие диалога покупок.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class PurchaseCanceledException extends Exception {

    public PurchaseCanceledException(String message) {
        super(message);
    }
}
