package org.classcraft.Composite;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Package extends FileComposite {
    protected List<FileComposite> fileComponents = new ArrayList<>();

    public Package(File directory) {
        this.f = directory;

        File[] list = directory.listFiles();
        for (File item : list) {
            if (item.isFile()) {
                if (item.getName().endsWith(".class") && !item.getName().contains("module-info")) {
                    fileComponents.add(new ClassModel(item));
                }
            } else if (item.isDirectory()) {
                fileComponents.add(new Package(item));
            }
        }
    }

    public List<FileComposite> getFileComponents() {
        return fileComponents;
    }

    @Override
    public void display(String indent) {
        System.out.println(indent + "|-" + f.getName());
        for (FileComposite component : fileComponents) {
            component.display(indent + "|  ");
        }
    }
}