/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package onlinesos;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author user
 */
public class MessageReader {
    Socket socket;
    BufferedReader reader;
    DataOutputStream outStream;

    public MessageReader() {
        try {
            this.socket =SocketManger.getSocket();
            reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outStream=new DataOutputStream(socket.getOutputStream());
         } catch (IOException ex) {
            Logger.getLogger(MessageReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String readMessage(){
        String message=null;
        try {
            message=reader.readLine();
         } catch (IOException ex) {
            Logger.getLogger(MessageReader.class.getName()).log(Level.SEVERE, null, ex);
        }
     return message;
    }
    
}
