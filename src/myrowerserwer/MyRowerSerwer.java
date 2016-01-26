/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myrowerserwer;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import java.io.Console;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author Nerexis
 */
public class MyRowerSerwer {
    static Server server;
    static MySQLAccess mysql;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        // TODO code application logic here
        
        mysql = new MySQLAccess();
        if(!mysql.Connect())
        {
            System.out.println("Nie można połączyc się z bazą");
            return;
        }
        
        server = new Server();
        
        server.getKryo().register(LoginRequest.class);
        server.getKryo().register(LoginReply.class);
        
                
        
        new Thread(server).start();
        
        server.addListener(new Listener()
        {
            @Override
            public void connected(Connection c) {
                System.out.println("Connection made");
            }
            @Override
            public void received (Connection connection, Object object) {
                    if (object instanceof LoginRequest) {
                        try {
                            LoginRequest request = (LoginRequest) object;
                            
                            System.out.println("Login: " + request.login + " Password: " + request.password);
                            
                            ResultSet res = mysql.GetAccountDataForLogin(request.login);
                            while(res.next())
                            {
                                String dbPassword = res.getString("password");
                                if(dbPassword.equals(request.password)  )
                                {
                                    LoginReply reply = new LoginReply();
                                    reply.id = res.getInt("id");
                                    
                                    connection.sendTCP(reply);
                                    System.out.println("User " + request.login + " logged in");
                                    return;
                                }
                            }
                            
                            // not found/bad password
                            System.out.println("Failed login for " + request.login);
                            LoginReply reply = new LoginReply();
                            reply.id = 0;
                            connection.sendTCP(reply);
                            return;
                            
                            
                        } catch (SQLException ex) {
                            Logger.getLogger(MyRowerSerwer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
        });
        
        try{
            
            
            server.bind(1234);
        }catch(Exception e)
        {
            System.out.println("Ex" + e.getMessage());
            //return;
        }
        
        
//        while(true)
//        {
//            try {
//                server.update(100);
//                Thread.sleep(100);
//                System.out.println(".");
//            } catch (IOException ex) {
//                Logger.getLogger(MyRowerSerwer.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
    }
    
}
