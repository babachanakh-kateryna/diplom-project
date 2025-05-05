package org.umlgenerator.View;

import org.umlgenerator.Composite.FileComposite;
import org.umlgenerator.Composite.Package;
import org.umlgenerator.Model.DiagramModel;
import org.umlgenerator.Model.Subject;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

// Клас для відображення файлової структури у вигляді дерева
public class ExplorerView extends TreeView<FileComposite> implements Observer {

    private DiagramModel m; // Модель діаграми
    private File f; // Директорія, що відображається

    // Конструктор для ініціалізації відображення дерева
    public ExplorerView(DiagramModel m) {
        this.m = m;
        m.registerObserver(this);
        this.f = new File(m.getDirectory());
        this.setCellFactory(tv -> new TreeCell<>() {
            @Override
            protected void updateItem(FileComposite fileComposite, boolean b) {
                super.updateItem(fileComposite, b);
                if(!b && fileComposite != null) {
                    setText(fileComposite.getName());
                    if(fileComposite instanceof Package){
                        ImageView dosImg = new ImageView(new Image(getClass().getResourceAsStream("dossier.png")));
                        dosImg.setFitWidth(16);
                        dosImg.setFitHeight(16);
                        dosImg.setPreserveRatio(true);
                        setGraphic(dosImg);
                    }else{
                        ImageView classImg = new ImageView(new Image(getClass().getResourceAsStream("fichierjava.png")));
                        classImg.setFitWidth(16);
                        classImg.setFitHeight(16);
                        classImg.setPreserveRatio(true);
                        setGraphic(classImg);
                    }
                }else{
                    setText(null);
                    setGraphic(null);
                }
            }
        });
        FileComposite fc = new Package(this.f);
        TreeItem<FileComposite> ti = new TreeItem<>(fc);
        this.setRoot(ti);
        buildTree(fc, ti);
    }

    // Оновлює дерево при зміні моделі
    @Override
    public void update(Subject s) {
        File file = new File(m.getDirectory());
        if(!this.f.equals(file)) {  // Перебудова дерева, якщо директорія змінилася
            FileComposite fc = new Package(file);
            this.f = file;
            TreeItem<FileComposite> ti = new TreeItem<>(fc);
            this.setRoot(ti);
            buildTree(fc, ti);
        }
    }

    // Рекурсивно будує дерево файлової структури
    private void buildTree(FileComposite fcomp, TreeItem<FileComposite> parentItem) {
        TreeItem<FileComposite> childItem;
        if(fcomp instanceof Package) {
            for (FileComposite child : ((Package) fcomp).getFcomps()) {
                if (child instanceof Package) {
                    childItem = new TreeItem<>(child);
                    parentItem.getChildren().add(childItem);
                    buildTree(child, childItem);
                }else{
                    childItem = new TreeItem<>(child);
                    parentItem.getChildren().add(childItem);
                }
            }
        }
    }
}
