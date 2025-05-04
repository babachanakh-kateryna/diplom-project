package org.classcraft.View;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.classcraft.Composite.FileComposite;
import org.classcraft.Model.DiagramModel;
import org.classcraft.Model.Subject;
import org.classcraft.Composite.Package;

import java.io.File;

// Клас для відображення файлової структури у вигляді дерева
public class ExplorerView extends TreeView<FileComposite> implements Observer {

    private DiagramModel model; // Модель діаграми
    private File directory; // Директорія, що відображається

    // Конструктор для ініціалізації відображення дерева
    public ExplorerView(DiagramModel model) {
        this.model = model;
        model.registerObserver(this);
        this.directory = new File(model.getDirectory());
        this.setCellFactory(tv -> new TreeCell<>() {
            @Override
            protected void updateItem(FileComposite fileComposite, boolean empty) {
                super.updateItem(fileComposite, empty);
                if (!empty && fileComposite != null) {
                    setText(fileComposite.getName());
                    if (fileComposite instanceof Package) {
                        ImageView folderIcon = new ImageView(new Image(getClass().getResourceAsStream("/org/classcraft/dossier.png")));
                        folderIcon.setFitWidth(16);
                        folderIcon.setFitHeight(16);
                        folderIcon.setPreserveRatio(true);
                        setGraphic(folderIcon);
                    } else {
                        ImageView classIcon = new ImageView(new Image(getClass().getResourceAsStream("/org/classcraft/fichierjava.png")));
                        classIcon.setFitWidth(16);
                        classIcon.setFitHeight(16);
                        classIcon.setPreserveRatio(true);
                        setGraphic(classIcon);
                    }
                } else {
                    setText(null);
                    setGraphic(null);
                }
            }
        });
        FileComposite fileComposite = new Package(this.directory);
        TreeItem<FileComposite> rootItem = new TreeItem<>(fileComposite);
        this.setRoot(rootItem);
        buildTree(fileComposite, rootItem);
    }

    // Рекурсивно будує дерево файлової структури
    private void buildTree(FileComposite fileComposite, TreeItem<FileComposite> parentItem) {
        TreeItem<FileComposite> childItem;
        if (fileComposite instanceof Package) {
            for (FileComposite child : ((Package) fileComposite).getFileComponents()) {
                if (child instanceof Package) {
                    childItem = new TreeItem<>(child);
                    parentItem.getChildren().add(childItem);
                    buildTree(child, childItem);
                } else {
                    childItem = new TreeItem<>(child);
                    parentItem.getChildren().add(childItem);
                }
            }
        }
    }

    // Оновлює дерево при зміні моделі
    @Override
    public void update(Subject s) {
        File newDirectory = new File(model.getDirectory());
        if (!this.directory.equals(newDirectory)) { // Перебудова дерева, якщо директорія змінилася
            FileComposite fileComposite = new Package(newDirectory);
            this.directory = newDirectory;
            TreeItem<FileComposite> rootItem = new TreeItem<>(fileComposite);
            this.setRoot(rootItem);
            buildTree(fileComposite, rootItem);
        }
    }
}