/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.server;

import com.sharedObject.SharedObject;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author anyel
 */
public class Server {
    public static void main(String[] args) {
        SharedObject so = new SharedObject();
        ServerThread st;
        Socket connectionClient;
        try (ServerSocket ss = new ServerSocket(6666);) {
            for (;;) {
                connectionClient = ss.accept();
                st = new ServerThread(so, connectionClient);
                st.start();
            }
        } catch (Exception e) {
            System.out.println("Error inesperado en el servidor");
        }
    }
}