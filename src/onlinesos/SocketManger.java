package onlinesos;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author user
 */
public class SocketManger {
    static Socket socket;
    static String serverIP;
    static int serverPort;
    static MessageReader messageReader;
    static MessageSender messageSender;
    static{
        try {
            String input=JOptionPane.showInputDialog("Enter IP and port of server separated by space");
            String arr[]=input.split(" ");
            serverIP=arr[0];
            serverPort=Integer.parseInt(arr[1]);
            socket=new Socket(InetAddress.getByName(serverIP), serverPort);
            messageReader=new MessageReader();
            messageSender=new MessageSender();
         } catch(ConnectException ex){
            JOptionPane.showMessageDialog(null, "Server offline");
            System.exit(0);
        }catch(SocketException ex){
            JOptionPane.showMessageDialog(null, "Oops! Probably the information was wrong.");
            System.exit(0);
        }
        catch (UnknownHostException ex) {
            JOptionPane.showMessageDialog(null, "Oops! Unknown Server");
            System.exit(0);
        } catch (IOException ex) {
            Logger.getLogger(SocketManger.class.getName()).log(Level.SEVERE, null, ex);
        } catch(Exception ex){
            JOptionPane.showMessageDialog(null, "Oops! Probably the information was wrong or it did not follow the format.");
            System.exit(0);
        }
    }

    static Socket getSocket() {
        return socket;
    }
    static void initSocket(){
         try {
            socket=new Socket(InetAddress.getByName(serverIP), serverPort);
            messageReader=new MessageReader();
            messageSender=new MessageSender();
            JSONObject obj = new JSONObject();
            obj.put("Type", "BACK_ONLINE");
            obj.put("UserName", GameDetails.myUsername);
            messageSender.sendMessage(obj);
            System.out.println("Online message sent");
        } catch(ConnectException ex){
            JOptionPane.showMessageDialog(null, "Server offline");
        }
        catch (UnknownHostException ex) {
            Logger.getLogger(SocketManger.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SocketManger.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(SocketManger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
