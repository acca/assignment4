/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unitn.dsantoro.a4server;

//import it.unitn.dsantoro.a4server.TradeRemote;
import java.util.List;
import java.util.Random;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.Session;


/**
 *
 * @author Daniele Santoro <daniele.santoro@studenti.unitn.it>
 */
@Stateless
public class Trade implements TradeRemote {

//    @PersistenceContext
//    private EntityManager manager;
    public Trade () {
        RemoteUser ruser = new RemoteUser();  
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();        
        session.save(ruser);
        ruser = readDb(ruser);
        ruser.toString();
        session.getTransaction().commit();
        session.close();           
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

}
