import javax.swing.*;
import java.awt.event.ActionListener;

public class Menu extends JMenu {
    public Menu(String name, String[] menuItemsNames, ActionListener[] actionListener) {
        super(name);
        JMenuItem[] jMenuItems = new JMenuItem[menuItemsNames.length];

        for(int i = 0; i < jMenuItems.length; i++) {
            jMenuItems[i] = new JMenuItem(menuItemsNames[i]);
            jMenuItems[i].addActionListener(actionListener[i]);
            this.add(jMenuItems[i]);
        }
    }
}
