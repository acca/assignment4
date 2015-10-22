/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unitn.dsantoro.a3client;

import it.unitn.dsantoro.a3server.TradeRemote;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 *
 * @author Daniele Santoro <daniele.santoro@studenti.unitn.it>
 */
public class Client {
    
    private TradeRemote trade = null;
    private InitialContext initialContext = null;
    private static final String USER_MSG = "--> Please tell me if you want to [S]ell, [B]uy, [L]ist transactions or [Q]uit: ";
    private static final float NOMINAL_PRICE = 10;
    private SessionFactory sessionFactory;

    public Client() throws NamingException {
        setupRemoteTrade();     
    }
    
    /**
     * @param args the command line arguments
     * @throws javax.naming.NamingException
     */
    public static void main(String[] args) throws NamingException {
        Client client = new Client();        
        User user = new User();        
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(user);
        user = client.readDb(user);
        user.toString();
        session.getTransaction().commit();
        session.close();        
        
        System.out.println(USER_MSG);
        
        int ch;
        boolean quit = false;

        try {
            while ( ((ch = System.in.read()) != -1) && (quit == false) ) {                
                if (ch != '\n' && ch != '\r') {                    
                    switch((char)ch){
                        case 's':
                        case 'S':
                            client.sell(user);
                            break;
                        case 'b':
                        case 'B':
                            client.buy(user);
                            break;
                        case 'l':
                        case 'L':
                            client.printUserOperation(user);
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
        this.trade = (TradeRemote) initialContext.lookup("java:a3Server-ear-1.0-SNAPSHOT/a3Server-ejb-1.0-SNAPSHOT/Trade!it.unitn.dsantoro.a3server.TradeRemote");
    }

    private void sell(User user) {
        System.out.println("\tUser choose to sell. Default stocks amount is 10");
        user = readDb(user);
        int userStocks = user.getStocksAmount();
        if (userStocks < 10) {
            System.out.println("\tUser can not sell since user stocks amount is less than the minium amount for sell: " + userStocks + " stocks left.");
        }
        else {
            float tot = 0;
            for (int i=0; i<10; i++){                
                tot += this.trade.currentValue(NOMINAL_PRICE);
            }
            user.setStocksAmount(userStocks-10);
            user.setMoney(user.getMoney() + tot);
            saveDb(user);
        }
        System.out.println("\t"+user);
    }

    private void buy(User user) {
        System.out.println("\tUser choose to buy. Default stocks amount is 10");
        user = readDb(user);
        float userMoney = user.getMoney();
        float tot = 0;
        for (int i=0; i<10; i++){                
            tot += this.trade.currentValue(NOMINAL_PRICE);
        }
        if ( userMoney <= 0 || (userMoney < tot) ) {
            System.out.println("\tUser can not buy since user money are not enough to buy a minimum amount of 10 stocks.\n" +
                    "\tUser has: "+ userMoney + " euro left.\n" + 
                    "\tTotal stocks price is: " + tot);
        }
        else {            
            user.setStocksAmount(user.getStocksAmount()+10);
            user.setMoney(userMoney - tot);
            saveDb(user);
        }
        System.out.println("\t"+user);
    }

    private void releaseRemoteTrade() throws NamingException {
        this.initialContext.close();
    }
    
    private void saveDb(User user) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(user);
        session.getTransaction().commit();
        session.close();        
    }
    
    private User readDb(User user) {                
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        String query = "FROM User U WHERE U.id = " + user.getId();
        List result = session.createQuery(query).list();
        for ( User u : (List<User>) result ) {
            if (u.getId().equals(user.getId())) {
                user.setId(u.getId());
                user.setMoney(u.getMoney());
                user.setStocksAmount(u.getStocksAmount());
            }
            else {
                System.err.println("Error in SELECT query from DB.");
            }
        }
        session.getTransaction().commit();
        session.close();
        return user;
    }
    
    private void printUserOperation(User user) {
        System.out.println("\tList of all the oepration for user: " + user.getId());
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        String query = "FROM User U WHERE U.id = " + user.getId();
        List result = session.createQuery(query).list();
        for ( User u : (List<User>) result ) {
            if (u.getId().equals(user.getId())) { 
                System.out.println("\tOperation ID: " + u.getIdOp() + " - Stocks: " + u.getStocksAmount() + " - Money: " + u.getMoney());
            }
            else {
                System.err.println("Error in SELECT query from DB.");
            }
        }
        session.getTransaction().commit();
        session.close();        
    }
}