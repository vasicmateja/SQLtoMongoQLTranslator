package observer;

public interface Publisher {

    void addSubscriber(Subscriber subscriber);
    void notify(Notification notification);
}
