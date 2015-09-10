/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sergenttech.quickchat;

/**
 *
 * @author bserg_000
 */
public class PacketChat extends Packet {
    
    public String message;
    public String source;
    public String destination;

    public PacketChat() {
    }

    public PacketChat(String source, String message) {
        this.source = source;
        this.message = message;
        destination = "";
    }

    public PacketChat(String source, String message, String destination) {
        this.source = source;
        this.message = message;
        this.destination = destination;
    }
    
}
