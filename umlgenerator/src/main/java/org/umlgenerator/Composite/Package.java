package org.umlgenerator.Composite;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Package extends FileComposite {
    protected List<FileComposite> fcomps = new ArrayList<FileComposite>();

    public Package(File dir) {
        this.f = dir;

        File[] liste = dir.listFiles();
        for (File item : liste) {
            if (item.isFile()) {
                if (item.getName().contains(".class") && !item.getName().contains("module-info")) {
                    fcomps.add(new ClassUML(item));
                }
            } else if (item.isDirectory()) {
                fcomps.add(new Package(item));
            }
        }

    }

    public List<FileComposite> getFcomps() {
        return fcomps;
    }

    @Override
    public void display(String s) {
        System.out.println(s + "|-" + f.getName());
        File[] liste = f.listFiles();
        for (FileComposite fic : fcomps) {
            fic.display(s + "|  ");
        }
    }
}