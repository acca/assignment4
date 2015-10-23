/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unitn.dsantoro.a4server;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author Daniele Santoro <daniele.santoro@studenti.unitn.it>
 */
@Entity
@Table
public class RemoteUser implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idOp;    
    private Long id;
    private float money;
    private int stocksAmount;
    
    private final static float DEF_MONEY=1000;
    private final static int DEF_STOCKSAMOUNT=50;

    public RemoteUser(){
        this.id=new Long(1);
        this.money = DEF_MONEY;
        this.stocksAmount = DEF_STOCKSAMOUNT;
    }
    
    public void setIdOp(Long idOp) {
        this.idOp = idOp;
    }

    public Long getIdOp() {
        return idOp;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public float getMoney() {
        return money;
    }

    public int getStocksAmount() {
        return stocksAmount;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public void setStocksAmount(int stocksAmount) {
        this.stocksAmount = stocksAmount;
    }
        
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RemoteUser)) {
            return false;
        }
        RemoteUser other = (RemoteUser) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        // TODO Read from DB ?
        return "User status -> Id: " + id + " - Money: " +this.money + " - Stocks: " + this.stocksAmount + " - Last operation id: " + this.getIdOp();
    }
    
    public void buy() {
        
    }
    
    public void sell() {
        
    }
    
}
