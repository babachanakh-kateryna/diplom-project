package org.classcraft.Export;

import org.classcraft.Model.DiagramModel;

// Інтерфейс для стратегії експорту діаграми
public interface ExportStrategy {
    // Експортує модель діаграми
    void export(DiagramModel model);
}