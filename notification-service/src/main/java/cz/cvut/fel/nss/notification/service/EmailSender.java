package cz.cvut.fel.nss.notification.service;

import cz.cvut.fel.nss.notification.dto.OrderEventDto;
import org.springframework.stereotype.Service;

@Service
public class EmailSender implements NotificationObserver{

    @Override
    public void onOrderCreated(OrderEventDto event) {
        System.out.println("OBSERVER: EmailSender zachytil událost.");
        String text = "Dobrý den, vaše objednávka produktu (ID: " + event.getProductId() + ") byla přijata.";
        sendEmail(event.getUserEmail(), text);
    }

    public void sendEmail(String to, String message) {
        System.out.println("--------------------------------------------------");
        System.out.println("ODESÍLÁNÍ E-MAILU...");
        System.out.println("Příjemce: " + to);
        System.out.println("Obsah: " + message);
        System.out.println("--------------------------------------------------");
    }
}
