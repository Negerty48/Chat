/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sharedObject;

import com.message.Message;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author anyel
 */
public class SharedObject {
    private HashMap<String, Socket> clients;
    private HashMap<String, HashSet<String>> channels;
    
    public SharedObject() {
        this.clients = new HashMap<>();
        this.channels = new HashMap<>();
    }
    
    public synchronized void addClient(String nick, Socket connectionClient) {
        this.clients.put(nick, connectionClient);
    }
    
    public synchronized void deleteClient(String nick) {        
        try {
            this.clients.get(nick).close();
        } catch (Exception e) {              
        }
        for (HashSet<String> hs: this.channels.values()) {
            if (hs.contains(nick)) {
                hs.remove(nick);
            }
        }
        this.clients.remove(nick);
    }
    
    public synchronized boolean checkNick(String nick) {
        if (!this.clients.containsKey(nick)) {
            return true;
        } else {
            return false;
        }
    }
    
    public synchronized void listUsers(Message m) throws Exception {
        String message = "--------------------- Usuarios ---------------------";
        for (String user : this.clients.keySet()) {
            if (!user.equals(m.getSender())) {
                message += "\n" + user;
            }
        }
        message += "\n" + "---------------------------------------------------";
        m.setMessage(message);
        try {
            Socket recipient = this.clients.get(m.getSender());
            ObjectOutputStream oos = new ObjectOutputStream(recipient.getOutputStream());
            oos.writeObject(m);
        } catch (Exception e) {
            throw e;
        }
    }
    
    public synchronized void listChannelUsers(Message m) throws Exception {
        String message = "------------ Usuarios en " + m.getRecipient() + "------------";
        for (String user : this.channels.get(m.getRecipient())) {
            if (!user.equals(m.getSender())) {
                message += "\n" + user;
            }
        }
        message += "\n" + "--------------------------------------------------------";
        m.setMessage(message);
        try {
            Socket recipient = this.clients.get(m.getSender());
            ObjectOutputStream oos = new ObjectOutputStream(recipient.getOutputStream());
            oos.writeObject(m);
        } catch (Exception e) {
            throw e;
        }
    }
    
    public synchronized void listChannels(Message m) throws Exception {
        String message = "--------------------- Canales ----------------------";
        for (String channel : this.channels.keySet()) {
            message += "\n" + channel;
        }
        message += "\n" + "---------------------------------------------------";
        m.setMessage(message);
        try {
            Socket recipient = this.clients.get(m.getSender());
            ObjectOutputStream oos = new ObjectOutputStream(recipient.getOutputStream());
            oos.writeObject(m);
        } catch (Exception e) {
            throw e;
        }
    }
    
    public synchronized void listMyChannels(Message m) throws Exception {
        String message = "------------------- Mis Canales --------------------";
        for (String channel : this.channels.keySet()) {
            if (this.channels.get(channel).contains(m.getSender())) {
                message += "\n" + channel;
            }
        }
        message += "\n" + "---------------------------------------------------";
        m.setMessage(message);
        try {
            Socket recipient = this.clients.get(m.getSender());
            ObjectOutputStream oos = new ObjectOutputStream(recipient.getOutputStream());
            oos.writeObject(m);
        } catch (Exception e) {
            throw e;
        }
    }
    
    public synchronized void sendMessage(Message m) throws Exception {        
        Socket client;
        for (String nick : this.clients.keySet()) {
            try {
                if (!nick.equals(m.getSender())) {
                    client = this.clients.get(nick);
                    ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                    oos.writeObject(m);
                }
            } catch (Exception e) {
                throw e;
            }
        }
    }
    
    public synchronized void sendPrivateMessage(Message m) throws Exception {       
        Socket client;
        try {
            if (this.clients.containsKey(m.getRecipient())) {
                for (String nick : this.clients.keySet()) {
                    if (nick.equals(m.getRecipient())) {
                        client = this.clients.get(nick);
                        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                        oos.writeObject(m);
                    }
                }
            } else {
                m.setType("error");
                m.setMessage("El usuario no está conectado");
                client = this.clients.get(m.getSender());
                ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                oos.writeObject(m);
            }
        } catch (Exception e) {
            throw e;
        }            
    }
    
    public synchronized void sendChannelMessage(Message m) throws Exception {       
        Socket client;
        try {
            if (this.channels.containsKey(m.getRecipient())) {
                if (this.channels.get(m.getRecipient()).contains(m.getSender())) {
                    for (String user : this.channels.get(m.getRecipient())) {
                        if (!user.equals(m.getSender())) {
                            client = this.clients.get(user);
                            ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                            oos.writeObject(m);
                        }
                    }
                } else {
                    m.setType("error");
                    m.setMessage("No se ha unido al canal");
                    client = this.clients.get(m.getSender());
                    ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                    oos.writeObject(m);
                }
            } else {
                m.setType("error");
                m.setMessage("El canal no ha sido creado");
                client = this.clients.get(m.getSender());
                ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                oos.writeObject(m);
            }
        } catch (Exception e) {
            throw e;
        }
    }
    
    public synchronized void createChannel(Message m) throws Exception {        
        Socket client;
        try {
            if (!this.channels.containsKey(m.getRecipient())) {           
                this.channels.put(m.getRecipient(), new HashSet<>());
                this.channels.get(m.getRecipient()).add(m.getSender());
                m.setMessage("Se ha creado el canal " + m.getRecipient());
                client = this.clients.get(m.getSender());
                ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                oos.writeObject(m);
            } else {
                m.setType("error");
                m.setMessage("El canal ya ha sido creado");
                client = this.clients.get(m.getSender());
                ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                oos.writeObject(m);
            }
        } catch (Exception e) {
            throw e;
        }
    }
    
    public synchronized void joinChannel(Message m) throws Exception {
        Socket client;
        try {
            if (this.channels.containsKey(m.getRecipient())) {
                if (!this.channels.get(m.getRecipient()).contains(m.getSender())) {
                    this.channels.get(m.getRecipient()).add(m.getSender());
                    m.setMessage("Te has unido al canal " + m.getRecipient());
                    client = this.clients.get(m.getSender());
                    ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                    oos.writeObject(m);
                } else {
                    m.setType("error");
                    m.setMessage("Ya te has unido al canal");
                    client = this.clients.get(m.getSender());
                    ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                    oos.writeObject(m);
                }
            } else {
                m.setType("error");
                m.setMessage("El canal no ha sido creado");
                client = this.clients.get(m.getSender());
                ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                oos.writeObject(m);
            }
        } catch (Exception e) {
            throw e;
        }            
    }
    
    public synchronized void leaveChannel(Message m) throws Exception {
        Socket client;
        try {
            if (this.channels.containsKey(m.getRecipient())) {
                if (this.channels.get(m.getRecipient()).contains(m.getSender())) {
                    this.channels.get(m.getRecipient()).remove(m.getSender());
                    m.setMessage("Se ha salido del canal " + m.getRecipient());
                    client = this.clients.get(m.getSender());
                    ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                    oos.writeObject(m);
                } else {
                    m.setType("error");
                    m.setMessage("No se ha unido al canal");
                    client = this.clients.get(m.getSender());
                    ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                    oos.writeObject(m);
                }
            } else {
                m.setType("error");
                m.setMessage("El canal no ha sido creado");
                client = this.clients.get(m.getSender());
                ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                oos.writeObject(m);
            }
        } catch (Exception e) {
            throw e;
        }
    }
}