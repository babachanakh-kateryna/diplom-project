package org.umlgenerator.Model;

import org.umlgenerator.View.Observer;

/**
 * Інтерфейс, що описує модель Subject у шаблоні Observer.
 */
public interface Subject {
    public void registerObserver(Observer observer);
    public void removeObserver(Observer observer);
    public void notifyObservers();
}
