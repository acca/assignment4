/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unitn.dsantoro.a4client;

import it.unitn.dsantoro.a4server.TradeRemote;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Daniele Santoro <daniele.santoro@studenti.unitn.it>
 */
public class Client {
    
    private TradeRemote trade = null;
    private InitialContext initialContext = null;
    private static final String USER_MSG = "--> Please tell me if you want to [S]ell, [B]uy, [L]ist transactions or [Q]uit: ";

    public Client() throws NamingException {
        setupRemoteTrade();     
    }
    
    /**
     * @param args the command line arguments
     * @throws javax.naming.NamingException
     */
    public static void main(String[] args) throws NamingException {
        Client client = new Client();
        
        System.out.println(USER_MSG);
        
        int ch;
        boolean quit = false;

        try {
            while ( ((ch = System.in.read()) != -1) && (quit == false) ) {                
                if (ch != '\n' && ch != '\r') {                    
                    switch((char)ch){
                        case 's':
                        case 'S':
                            System.out.println("\tUser choose to sell. Default stocks amount is 10");
                            System.out.println(client.trade.sell());
                            break;
                        case 'b':
                        case 'B':
                            System.out.println("\tUser choose to buy. Default stocks amount is 10");
                            System.out.println(client.trade.buy());
                            break;
                        case 'l':
                        case 'L':
                            System.out.println("\tUser choose to list the business transactions");
                            System.out.println(client.trade.list());
                            //client.printUserOperation(user);
                            break;
                        case 'q':
                        case 'Q':
                            System.out.println("Quitting. Hope you had good business !!!");
                            //client.releaseRemoteTrade();
                            quit = true;
                            break;                    
                    }
                    System.out.println(USER_MSG);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }        
    
    private void setupRemoteTrade () throws NamingException {
        Properties jndiProps = new Properties();
        jndiProps.put(Context.INITIAL_CONTEXT_FACTORY,"org.jboss.naming.remote.client.InitialContextFactory");
        jndiProps.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");        
        jndiProps.put(Context.PROVIDER_URL, "http-remoting://127.0.0.1:8080");
        //This property is important for remote resolving
        jndiProps.put("jboss.naming.client.ejb.context", true);
        //This propert is not important for remote resolving
        jndiProps.put("org.jboss.ejb.client.scoped.context", true);
    
        // username
        jndiProps.put(Context.SECURITY_PRINCIPAL, "user");
        // password
        jndiProps.put(Context.SECURITY_CREDENTIALS, "pw");
        this.initialContext = new InitialContext(jndiProps);                 
        this.trade = (TradeRemote) initialContext.lookup("java:a4Server-ear-1.0-SNAPSHOT/a4Server-ejb-1.0-SNAPSHOT/Trade!it.unitn.dsantoro.a4server.TradeRemote");
    }

    
    private void releaseRemoteTrade() throws NamingException {
        this.initialContext.close();
    }
}