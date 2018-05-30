package jp.co.asaichi.pubrepo.model;

/**
 * Created by nguyentu on 12/28/17.
 */

public class SelectableItem {
    private boolean isSelected = false;
    private String name;

    public SelectableItem() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
