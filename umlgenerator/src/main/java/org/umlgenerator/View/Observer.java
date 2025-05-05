package org.umlgenerator.View;

import org.umlgenerator.Model.Subject;

/**
 * Інтерфейс для спостерігачів, які оновлюються при зміні стану Subject
 */
public interface Observer {
    public void update(Subject s);
}
