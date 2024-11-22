/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.client;

import com.message.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author anyel
 */
public class Client {
    public static void main(String[] args) {        
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        Message m = null;
        String message;
        String parts[];
        String nick;
        boolean acces = false;
        
        try (Socket connectionServer = new Socket("localhost", 6666);
                Scanner sc = new Scanner(System.in);) {
            do {
                System.out.print("Introduzca un nickname: ");
                nick = sc.nextLine();
                oos = new ObjectOutputStream(connectionServer.getOutputStream());
                oos.writeObject(nick);
                ois = new ObjectInputStream(connectionServer.getInputStream());
                acces = (Boolean)ois.readObject();                
                if (!acces) {
                    System.out.println("El nickname no se encuentra disponible.");
                }
            } while (!acces);
            ClientThread ct = new ClientThread(connectionServer);
            ct.start();
            do {
                System.out.print("> ");
                message = sc.nextLine();
                if (!message.equals(".help")) {
                    if (message.equals(".exit")) {
                        m = new Message("exit", "", message, "");                        
                    } else if (message.equals(".listUsers")) {                            
                        m = new Message("listUsers", nick, "", "");
                    } else if (message.startsWith(".listChannelUsers")) {
                        parts = message.split(" ", 2);
                        if (parts.length < 2) {
                            System.out.println(".listChannelUsers {nombre del canal}");
                        } else {
                            m = new Message("listChannelUsers", nick, "", parts[1]);
                        }
                    } else if (message.equals(".listChannels")) {
                        m = new Message("listChannels", nick, "", "");
                    } else if (message.equals(".listMyChannels")) {
                        m = new Message("listMyChannels", nick, "", "");
                    } else if (message.startsWith(".private")) {
                        parts = message.split(" ", 3);
                        if (parts.length < 3) {
                            System.out.println(".private {destinatario} {mensaje}");
                        } else {
                            m = new Message("private", nick, parts[2], parts[1]);
                        }
                    } else if (message.startsWith(".channel")) {
                        parts = message.split(" ", 3);
                        if (parts.length < 3) {
                            System.out.println(".channel {nombre del canal} {mensaje}");
                        } else {
                            m = new Message("channel", nick, parts[2], parts[1]);
                        }
                    } else if (message.startsWith(".createChannel")) {
                        parts = message.split(" ", 2);
                        if (parts.length < 2) {
                            System.out.println(".createChannel {nombre del canal}");
                        } else {
                            m = new Message("create", nick, "", parts[1]);
                        }
                    } else if (message.startsWith(".join")) {
                        parts = message.split(" ", 2);
                        if (parts.length < 2) {
                            System.out.println(".join {nombre del canal}");
                        } else {
                            m = new Message("join", nick, "", parts[1]);
                        }
                    } else if (message.startsWith(".leave")) {
                        parts = message.split(" ", 2);
                        if (parts.length < 2) {
                            System.out.println(".leave {nombre del canal}");
                        } else {
                            m = new Message("leave", nick, "", parts[1]);
                        }
                    } else {
                        m = new Message("all", nick, message, "");
                    }
                    oos = new ObjectOutputStream(connectionServer.getOutputStream());
                    oos.writeObject(m);
                } else {
                    showHelp();
                }
            } while (!message.equals(".exit"));
        }catch (Exception e) {
            System.out.println("Se ha producido un error en la conexión con el servidor");
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
        }
    }
    
    public static void showHelp() {
        System.out.println("------------------ Comandos -------------------");
        System.out.println(".exit (Salir)");
        System.out.println(".listUsers (Mostrar usuarios conectados");
        System.out.println(".listChannelUsers {nombre del canal} (Mostrar usuarios conectados de ese canal)");
        System.out.println(".private {destinatario} {mensaje} (Enviar mensaje privado)");
        System.out.println(".listChannels (Mostrar todos los canales)");
        System.out.println(".listMyChannels (Mostrar mis canales)");
        System.out.println(".join {nombre del canal} (Unirse al canal)");
        System.out.println(".channel {nombre del canal} {mensaje} (Enviar mensaje a los usuarios del canal)");
        System.out.println(".leave {nombre del canal} (Salir del canal)");
        System.out.println(".createChannel {nombre del canal} (Crear el canal)");
        System.out.println(".help (Mostrar ayuda)");
    }
}