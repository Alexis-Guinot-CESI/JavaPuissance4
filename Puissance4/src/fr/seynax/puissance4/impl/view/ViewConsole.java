package fr.seynax.puissance4.impl.view;

import fr.seynax.puissance4.core.view.ViewEngine;

public class ViewConsole extends ViewEngine {
    @Override
    public void clear() {
        super.clear();
        for(int i = 0; i < 50; i ++) {
            System.out.println();
        }
    }

    @Override
    public void draw(String content) {
        System.out.print(content);
    }
}
