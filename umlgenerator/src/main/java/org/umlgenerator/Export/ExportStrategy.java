package org.umlgenerator.Export;

import org.umlgenerator.Model.DiagramModel;

// Інтерфейс для стратегії експорту діаграми
public interface ExportStrategy {
    // Експортує модель діаграми
    public void exportUML(DiagramModel m);
}
