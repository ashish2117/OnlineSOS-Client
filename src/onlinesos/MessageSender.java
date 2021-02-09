/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package onlinesos;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author user
 */
public class MessageSender {

    Socket socket;
    private DataOutputStream outStream;

    public MessageSender() throws UnknownHostException, IOException {
        socket = SocketManger.getSocket();
        outStream = new DataOutputStream(socket.getOutputStream());
    }

    public void sendMessage(JSONArray obj) throws IOException {
        outStream.writeBytes(obj.toString() + "\n");
    }

    public void sendMessage(JSONObject obj) throws IOException {
        outStream.writeBytes(obj.toString() + "\n");
    }
}
