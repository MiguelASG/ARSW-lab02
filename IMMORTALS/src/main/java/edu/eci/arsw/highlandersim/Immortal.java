package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback=null;
    
    private int health;
    
    private int defaultDamageValue;

    private final List<Immortal> immortalsPopulation;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());
    
    private boolean disponible = true;
    
    private boolean alive = true;


    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb) {
        super(name);
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = health;
        this.defaultDamageValue=defaultDamageValue;
    }

    public void run() {

        while (true) {
        	
        	if(!disponible) {
        		try {
        			synchronized (immortalsPopulation) {
        				immortalsPopulation.wait();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        	else {
	            Immortal im;
	
	            int myIndex = immortalsPopulation.indexOf(this);
	            
	            int nextFighterIndex = r.nextInt(immortalsPopulation.size());
	            while(!immortalsPopulation.get(nextFighterIndex).alive) {
	            	nextFighterIndex = r.nextInt(immortalsPopulation.size());
	            }
	
	            //avoid self-fight
	            if (nextFighterIndex == myIndex) {
	                nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
	            }
	            im = immortalsPopulation.get(nextFighterIndex);
	            int h1 = (int) this.getId();
	            int h2 = (int) im.getId();
	            if(h1>h2) {
	            	synchronized (this) {
		            	synchronized (im) {
		            		this.fight(im);
						}
						
					}
	            }
	            else {
	            	synchronized (im) {
		            	synchronized (this) {
		            		this.fight(im);
						}
						
					}
	            	
	            }
	            
	
	            
	
	            try {
	                Thread.sleep(1);
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
        	}

        }

    }

    public void fight(Immortal i2) {

        if (i2.getHealth() > 0) {
            i2.changeHealth(i2.getHealth() - defaultDamageValue);
            this.health += defaultDamageValue;
            updateCallback.processReport("Fight: " + this + " vs " + i2+"\n");
        } else {
            updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
        }

    }    

    public void changeHealth(int v) {
        health = v;
    }

    public int getHealth() {
        return health;
    }

    @Override
    public String toString() {

        return name + "[" + health + "]";
    }
    
    public void setDisponible() {
    	disponible = !disponible;
    }

}
