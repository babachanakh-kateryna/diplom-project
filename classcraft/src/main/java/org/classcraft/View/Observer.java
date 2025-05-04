package org.classcraft.View;

import org.classcraft.Model.Subject;


/**
 * Інтерфейс для спостерігачів, які оновлюються при зміні стану Subject
 */
public interface Observer {
    public void update(Subject s);
}
