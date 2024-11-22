/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.message;

import java.io.Serializable;

/**
 *
 * @author anyel
 */
public class Message implements Serializable{
    private String type;
    private String sender;
    private String message;
    private String recipient;

    public Message(String type, String sender, String message, String recipient) {
        this.type = type;
        this.sender = sender;
        this.message = message;
        this.recipient = recipient;
    }

    @Override
    public String toString() {
        return "Message{" + "type=" + type + ", sender=" + sender + ", message=" + message + ", recipient=" + recipient + '}';
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}