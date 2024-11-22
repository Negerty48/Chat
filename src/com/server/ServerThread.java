/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.server;

import com.message.Message;
import com.sharedObject.SharedObject;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author anyel
 */
public class ServerThread extends Thread {
    private String nick;           
    private SharedObject so;
    private Socket connectionClient;
    
    public ServerThread(SharedObject so, Socket connectionClient) {
        this.so = so;
        this.connectionClient = connectionClient;
    }
    
    public void run() {
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        Message m;
        String nick = "";
        boolean acces = false;
        
        try {
            do {
                ois = new ObjectInputStream(this.connectionClient.getInputStream());
                nick = (String)ois.readObject();                
                acces = this.so.checkNick(nick);                
                oos = new ObjectOutputStream(this.connectionClient.getOutputStream());
                oos.writeObject(acces);
            } while (!acces);
            this.nick = nick;
            this.so.addClient(nick, this.connectionClient);
            do {
                ois = new ObjectInputStream(this.connectionClient.getInputStream());
                m = (Message)ois.readObject();
                if (!m.getType().equals("exit")) {
                    if (m.getType().equals("listUsers")) {                        
                        this.so.listUsers(m);
                    } else if (m.getType().equals("listChannelUsers")) {
                        this.so.listChannelUsers(m);
                    } else if (m.getType().equals("listChannels")) {
                        this.so.listChannels(m);
                    } else if (m.getType().equals("listMyChannels")) {
                        this.so.listMyChannels(m);
                    } else if (m.getType().equals("private")) {
                        this.so.sendPrivateMessage(m);
                    } else if (m.getType().equals("channel")) {
                        this.so.sendChannelMessage(m);
                    } else if (m.getType().equals("create")) {
                        this.so.createChannel(m);
                    } else if (m.getType().equals("join")) {
                        this.so.joinChannel(m);
                    } else if (m.getType().equals("leave")) {
                        this.so.leaveChannel(m);
                    } else {
                        this.so.sendMessage(m);
                    }                    
                }
            } while (!m.getType().equals("exit"));
            System.out.println("Cierre controlado del cliente");
        } catch (Exception e) {            
            System.out.println("Cierre abrupto del cliente");
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException ex) {
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException ex) {
                }
            }
            this.so.deleteClient(nick);
        }
    }
}