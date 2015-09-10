package com.sergenttech.quickchat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;

/**
 *
 * @author bserg_000
 */
public class QuickChat extends javax.swing.JFrame {
    
    
    public QuickChat() {
        prefs = java.util.prefs.Preferences.userRoot();
        prefs = prefs.node("com.sergenttech.quickchat.prefs");
        initComponents();
        newMessage.requestFocus();
        print("Connecting...");
        if (!DEBUG) {
            connect(MAINSERVER_ADDRESS,MAINSERVER_PORT);
        } else {
            connect("localhost",MAINSERVER_PORT);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        messagesScrollPane = new javax.swing.JScrollPane();
        messages = new javax.swing.JTextPane();
        newMessage = new javax.swing.JTextField();
        sendButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("QuickChat");

        messages.setEditable(false);
        messagesScrollPane.setViewportView(messages);

        newMessage.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                newMessageKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                newMessageKeyTyped(evt);
            }
        });

        sendButton.setText("Send");
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(messagesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(newMessage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sendButton))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(messagesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sendButton)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendButtonActionPerformed
        sendChat(newMessage.getText());
        sendButton.setEnabled(false);
        newMessage.setText(""); // TODO http://www.dreamincode.net/forums/topic/259777-a-simple-chat-program-with-clientserver-gui-optional/
    }//GEN-LAST:event_sendButtonActionPerformed

    private void newMessageKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_newMessageKeyTyped
        sendButton.setEnabled(true);
    }//GEN-LAST:event_newMessageKeyTyped

    private void newMessageKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_newMessageKeyReleased
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            sendChat(newMessage.getText());
            sendButton.setEnabled(false);
            newMessage.setText("");
        }
    }//GEN-LAST:event_newMessageKeyReleased
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(QuickChat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(QuickChat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(QuickChat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(QuickChat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new QuickChat().setVisible(true);
            }
        });
    }
    
    private boolean sendChat(String msg) {
        try {
            output.writeObject(new PacketChat(username, msg));
        } catch (IOException ex) {
            return false;
        }
        return true;
    }
    
    private boolean connect(String server, int port) { // Default localhost, 21897
        try {
            socket = new Socket(server, port);
        } catch (Exception e) {
            print("Failed to connect.");
            return false;
        }
        try {
            input = new ObjectInputStream(socket.getInputStream());
            output = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException eIO) {
            print("Failed to connect.");
            return false;
        }
        print("Connected to server.");
        new ListenFromServer().start();
        return true;
    }
    
    public void disconnect() {
        try {
            if (input != null) input.close();
        } catch (Exception ex) {}
        try {
            if (output != null) output.close();
        } catch (Exception ex) {}
        try {
            if (socket != null) socket.close();
        } catch (Exception ex) {}
        print("Disconnected from server.");
    }
    
    public void print(String msg) {
        if (!"".equals(messages.getText())) {
            messages.setText(messages.getText()+"\n"+msg);
        } else {
            messages.setText(msg);
        }
    }
    
    // TODO Option to keep above everything else
    
    public static final String MAINSERVER_ADDRESS = "play.lostrealms.net";
    public static final int MAINSERVER_PORT = 21897;
    private final boolean DEBUG = false;
    private final String VERSION = "0.0.1A";
    private static java.util.prefs.Preferences prefs;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket socket;
    private String username = "?";

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextPane messages;
    private javax.swing.JScrollPane messagesScrollPane;
    private javax.swing.JTextField newMessage;
    private javax.swing.JButton sendButton;
    // End of variables declaration//GEN-END:variables

    class ListenFromServer extends Thread {
        public void run() {
            while (true) {
                try {
                    Packet p = (Packet) input.readObject();
                    if (p instanceof PacketChat) {
                        PacketChat pChat = (PacketChat) p;
                        if (pChat.destination != "") {
                            print("["+pChat.source+">"+pChat.destination+"] "+pChat.message);
                        } else {
                            print("["+pChat.source+"] "+pChat.message);
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                JScrollBar vertical = messagesScrollPane.getVerticalScrollBar();
                                vertical.setValue(vertical.getMaximum());
                            }
                        });
                    }
                } catch (IOException ex) {
                    print("Lost connection to server.");
                    disconnect();
                    break;
                } catch (ClassNotFoundException ex) {
                }
            }
        }
    }
}
