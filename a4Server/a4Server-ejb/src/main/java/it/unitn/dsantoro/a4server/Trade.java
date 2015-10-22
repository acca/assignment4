/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unitn.dsantoro.a4server;

//import it.unitn.dsantoro.a4server.TradeRemote;
import java.util.List;
import java.util.Random;
//import javax.ejb.Stateful;
import javax.ejb.Stateless;
import org.hibernate.Session;


/**
 *
 * @author Daniele Santoro <daniele.santoro@studenti.unitn.it>
 */
@Stateless
public class Trade implements TradeRemote {

    private static final float NOMINAL_PRICE = 10;
    
    RemoteUser ruser;

    public Trade () {        
        ruser = new RemoteUser();
//        Session session = HibernateUtil.getSessionFactory().openSession();
//        session.beginTransaction();        
//        session.save(ruser);
//        ruser = readDb(ruser);
//        ruser.toString();
//        session.getTransaction().commit();
//        session.close();           
    }
    
    @Override
    public float currentValue(float nominalValue) {
        System.out.println("--- Converting stock nominalValue to currentValue ---");
        System.out.println("Stock nominal value: "+ nominalValue);
        float minX = 0.95f;
        float maxX = 1.05f;
        Random rand = new Random();
        float perc = rand.nextFloat() * (maxX - minX) + minX;
	System.out.println("Percentage applied: "+ perc);
	float currentValue = nominalValue * perc;
        currentValue = round(currentValue);
	System.out.println("Stock current value: "+ currentValue);      
        return currentValue;
    }

    private static float round(float n){
        int nI = (int)(n*10); 
	return (float)nI/10;		
    }
    
    private RemoteUser readDb(RemoteUser user) {                
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        String query = "FROM RemoteUser U WHERE U.id = " + user.getId();
        List result = session.createQuery(query).list();
        for ( RemoteUser u : (List<RemoteUser>) result ) {
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
    
    @Override
    public String sell() {
        String message;
        ruser = readDb(ruser);
        int userStocks = ruser.getStocksAmount();
        if (userStocks < 10) {
            message = "\tUser can not sell since user stocks amount is less than the minium amount for sell: " + userStocks + " stocks left.";
        }
        else {
            float tot = 0;
            for (int i=0; i<10; i++){                
                tot += currentValue(NOMINAL_PRICE);
            }
            ruser.setStocksAmount(userStocks-10);
            ruser.setMoney(ruser.getMoney() + tot);
            saveDb(ruser);
            message = "\t"+ruser;
        }        
        return message;
    }

    @Override
    public String buy() {
        String message;
        ruser = readDb(ruser);
        float userMoney = ruser.getMoney();
        float tot = 0;
        for (int i=0; i<10; i++){                
            tot += currentValue(NOMINAL_PRICE);
        }
        if ( userMoney <= 0 || (userMoney < tot) ) {
            message = "\tUser can not buy since user money are not enough to buy a minimum amount of 10 stocks.\n" +
                    "\tUser has: "+ userMoney + " euro left.\n" + 
                    "\tTotal stocks price is: " + tot;
        }
        else {            
            ruser.setStocksAmount(ruser.getStocksAmount()+10);
            ruser.setMoney(userMoney - tot);
            saveDb(ruser);
            message = "\t"+ruser;
        }        
        return message;
    }

    private void saveDb(RemoteUser user) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(user);
        session.getTransaction().commit();
        session.close();        
    }

    @Override
    public String list() {
        String message = "\tList of all the oepration for user: " + ruser.getId() + "\n";
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        String query = "FROM RemoteUser U WHERE U.id = " + ruser.getId();
        List result = session.createQuery(query).list();
        for ( RemoteUser u : (List<RemoteUser>) result ) {
            if (u.getId().equals(ruser.getId())) { 
                message += "\tOperation ID: " + u.getIdOp() + " - Stocks: " + u.getStocksAmount() + " - Money: " + u.getMoney() + "\n";
            }
            else {
                message += "Error in SELECT query from DB.\n";
            }
        }
        session.getTransaction().commit();
        session.close();
        return message;
    }
}
