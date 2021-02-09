/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package onlinesos;

import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.awt.TrayIcon;

public class SendNotification {

    public void displayTray(String notification) throws AWTException {
        
        SystemTray tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().createImage("images/game.png");
        TrayIcon trayIcon = new TrayIcon(image, "OnlineSOS");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("OnlineSOS");
        tray.add(trayIcon);
        trayIcon.displayMessage("OnlineSOS", notification, MessageType.INFO);
        
    }
}
