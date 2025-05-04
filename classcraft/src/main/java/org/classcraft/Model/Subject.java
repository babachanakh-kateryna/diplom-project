package org.classcraft.Model;

import org.classcraft.View.Observer;

/**
 * Інтерфейс, що описує модель Subject у шаблоні Observer.
 */
public interface Subject {
    void registerObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers();
}
