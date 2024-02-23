package fr.seynax.puissance4.core.view;

import fr.seynax.puissance4.api.view.IView;

import java.util.Objects;

public abstract class ViewEngine implements IView {
    private int x;
    private int y;

    public ViewEngine() {
        this.x = 0;
        this.y = 0;
    }

    @Override
    public void clear() {
        this.x = 0;
        this.y = 0;
    }

    String toString(Object... objects) {
        StringBuilder objectsStringBuilder = new StringBuilder();

        var i = 0;
        for(var object : objects) {
            if(i > 0) {
                objectsStringBuilder.append(" ");
            }
            objectsStringBuilder.append(object);
            i ++;
        }

        return objectsStringBuilder.toString();
    }

    void print(Object... objects) {
        draw(toString(objects));
    }

    void printLn(Object... objects) {
        print(objects);
        draw("\n");
        increaseY();
    }

    void printAt(int x, int y, Objects... objects) {
        for(int i = y(); i < y-1; i ++) {
            draw("\n");
        }
        var start = " ".repeat(x);
        print(start, objects);
    }

    void printAtLn(int x, int y, Objects... objects) {
        printAt(x, y, objects);
        print('\n');
    }

    int increaseX() {
        this.x ++;

        return this.x;
    }

    int increaseX(int x) {
        this.x += x;

        return this.x;
    }

    int x() {
        return this.x;
    }

    int increaseY() {
        this.y ++;

        return this.y;
    }

    int increaseY(int y) {
        this.y += y;

        return this.y;
    }

    int y() {
        return this.y;
    }
}