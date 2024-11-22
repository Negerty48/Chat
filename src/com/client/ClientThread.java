/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.client;

import com.message.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 *
 * @author anyel
 */
public class ClientThread extends Thread {
    private Socket connectionServer;
    
    public ClientThread(Socket connectionServer) {
        this.connectionServer = connectionServer;
    }
    
    public void run() {
        ObjectInputStream ois = null;
        Message m;
        try {
            for (;;) {
                ois = new ObjectInputStream(this.connectionServer.getInputStream());
                m = (Message)ois.readObject();
                if (m.getType().equals("all")) {
                    System.out.print(m.getSender() + ": " + m.getMessage() + "\n> ");
                } else if (m.getType().equals("private")) {
                    System.out.print("Mensaje privado de " + m.getSender() + ": " + m.getMessage() + "\n> ");
                } else if (m.getType().equals("listUsers") || m.getType().equals("listChannels") 
                        || m.getType().equals("listMyChannels") || m.getType().equals("listChannelUsers")) {
                    System.out.print(m.getMessage()+ "\n> ");
                } else if (m.getType().equals("channel")) {
                    System.out.print("Mensaje al canal "+ m.getRecipient() + " de " + m.getSender() + ": " + m.getMessage()+ "\n> ");
                } else if (m.getType().equals("create")) {
                    System.out.print(m.getMessage() + "\n> ");
                } else if (m.getType().equals("leave")) {
                    System.out.print(m.getMessage() + "\n> ");
                } else if (m.getType().equals("join")) {
                    System.out.print(m.getMessage() + "\n> ");
                } else {
                    System.out.print("Error: " + m.getMessage() + "\n> ");
                }
            }
        } catch (Exception e) {            
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException ex) {
                }
            }
        }
    }
}