/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sergenttech.quickchat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author bserg_000
 */
public class Server {
    
    private static int nextConId = 0;
    private ArrayList<ClientThread> connections = new ArrayList<>();
    private boolean closeRequested = false;
    private int port;
    
    public Server(int port) {
        this.port = port;
        start();
    }
    
    public static void main(String args[]) {
        int port = QuickChat.MAINSERVER_PORT;
        switch (args.length) {
            case 1: // Port given
                try {
                    port = Integer.parseInt(args[0]);
                } catch (Exception e) {
                    System.out.println("Invalid port number");
                    System.out.println("Usage: > java Server [port]");
                }
            case 0:
                break;
            default:
                System.out.println("Usage: > java Server [port]");
                return;
        }
        new Server(port);
    }
    
    private void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (!closeRequested) {
                print("Waiting for clients on port "+port+"...");
                Socket socket = serverSocket.accept();
                if (closeRequested)
                    break;
                ClientThread t = new ClientThread(socket);
                connections.add(t);
                t.start();
            }
            // After close requested
            serverSocket.close();
            for (ClientThread tc : connections) {
                tc.input.close();
                tc.output.close();
                tc.socket.close();
            }
        } catch (IOException ex) {
            print("Could not open server socket.");
        }
    }
    
    public synchronized void remove(int id) {
        for (ClientThread ct : connections) {
            if (ct.id == id) {
                connections.remove(ct);
                return;
            }
        }
    }
    
    public void print(String msg) {
        System.out.println(msg);
    }
    
    /* INNER CLASSES */
    
    public class ClientThread extends Thread {
        Socket socket;
        ObjectInputStream input;
        ObjectOutputStream output;
        int id;
        String username = "?";
        String startDate;

        public ClientThread(Socket socket) {
            this.socket = socket;
            id = nextConId++;
            print("Creating input and output streams.");
            try {
                output = new ObjectOutputStream(socket.getOutputStream());
                input = new ObjectInputStream(socket.getInputStream());
                print("Connection established.");
            } catch (IOException e) {
                print("Could not create streams.");
            }
            startDate = new Date().toString();
        }

        @Override
        public void run() {
            boolean closeRequested = false;
            while (!closeRequested) {
                try {
                    Packet p = (Packet) input.readObject();
                    
                    if (p instanceof PacketChat) {
                        PacketChat pChat = (PacketChat) p;
                        if ("".equals(pChat.destination)) {
                            print("["+username+"] "+pChat.message);
                            for (ClientThread con : connections) {
                                con.output.writeObject(p);
                            }
                        } else {
                            print("["+username+">"+pChat.destination+"] "+pChat.message);
                            for (ClientThread con : connections) {
                                if (con.username.equals(pChat.destination)) {
                                    con.output.writeObject(p);
                                }
                            }
                        }
                    }
                } catch (IOException ex) {
                    if (ex instanceof SocketException) {
                        print("Client disconnected.");
                        break;
                    } else {
                        print("Exception reading packet.");
                    }
                } catch (ClassNotFoundException ex) {
                    print("Packet received was not actually a packet.");
                }
            }
            remove(id);
            close();
        }

        private void close() {
            try {
                if (output != null) output.close();
            } catch (Exception e) {}
            try {
                if (input != null) input.close();
            } catch (Exception e) {}
            try {
                if (socket != null) socket.close();
            } catch (Exception e) {}
        }

        private boolean sendPacket(Packet p) {
            if (!socket.isConnected()) {
                close();
                return false;
            }
            try {
                output.writeObject(p);
            } catch (IOException e) {
                print("Error sending message");
            }
            return true;
        }
    }
}
