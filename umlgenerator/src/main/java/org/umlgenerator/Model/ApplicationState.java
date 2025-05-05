package org.umlgenerator.Model;

/**
 * Клас що дозволяє отримати ширину вікна програми
 */
public class ApplicationState {
    // за дефолтом
    private static double currentWindowWidth = 600;

    public static double getCurrentWindowWidth() {
        return currentWindowWidth - 325;
    }

    public static void setCurrentWindowWidth(double width) {
        currentWindowWidth = width;
    }
}