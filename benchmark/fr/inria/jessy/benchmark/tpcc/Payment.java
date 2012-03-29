package fr.inria.jessy.benchmark.tpcc;

import fr.inria.jessy.Jessy;
import fr.inria.jessy.benchmark.tpcc.entities.*;
import fr.inria.jessy.store.*;
import fr.inria.jessy.transaction.*;

import java.util.*;

public class Payment extends Transaction {
	
	private String W_ID;
	private String D_ID;
	private String C_W_ID; 
	private String C_D_ID; 
	private String C_ID;
	private String C_LAST;
    private double H_AMOUNT;

	public Payment(Jessy jessy) throws Exception {
		super(jessy);
	}
	

	@Override
	public ExecutionHistory execute() {

        try {
        	Warehouse warehouse;
        	District district;
        	Customer customer;
        	History history;
        	NURand nur;
        	
        	Random rand = new Random(System.currentTimeMillis());
        	int x = rand.nextInt(100-1)+1;    /* x determines local or remote warehouse */
        	int y = rand.nextInt(100-1)+1;    /* y determines by C_LAST or by C_ID */
        	
        	W_ID = "1";         /* warehouse number (W_ID) is constant  */
        	H_AMOUNT = (float) (((float)rand.nextInt(500000-1)+100)/100.00); /* 2.5.1.3  (H _AMOUN T) is random within [1.00 .. 5,000.00] */

        	/* Selection in the Warehouse table */
        	warehouse = read(Warehouse.class, "W_" + W_ID);
        	warehouse.setW_YTD(warehouse.getW_YTD() + this.H_AMOUNT);   /* increase warehouse's year-to-date by H_AMOUNT */
        	/* Update Warehouse */
        	write(warehouse);        	
        	
        	D_ID = Integer.toString(rand.nextInt(10 - 1) + 1); /* The district number (D_ID) is randomly selected within [1 ..10] */
        	/* Selection in the District table */
        	district = read(District.class, "D_W_"+ W_ID + "_" + "D_"+ D_ID);
        	district.setD_YTD(district.getD_YTD() + this.H_AMOUNT);   /* increase district's year-to-date by H_AMOUNT */        	
        	/* Update District */
        	write(district);
        	
        	if(x<=85) {    /* local warehouse */
        		C_D_ID = this.D_ID;
        		C_W_ID = this.W_ID;        		
        	}
        	else {         /* remote warehouse */
        		C_D_ID = Integer.toString(rand.nextInt(10 - 1) + 1);  /* C_D_ID is randomly selected within [1 .. 10] */
        		while(true) {
        			C_W_ID = Integer.toString(rand.nextInt(89 - 1) + 1);  /* not sure !!! */
        			if(C_W_ID != this.W_ID)   /* different to local warehouse ID 1 */
        				break;
        		}
        		
        	}
        	
        	/* Selection Customer */
        	if(y>60) {    /* by C_ID */
        		nur = new NURand(1023,1,3000);
        		C_ID = Integer.toString(nur.calculate());  /* generate C_ID */
        		customer = read(Customer.class, "C_W_"+C_W_ID + "_" + "C_D_"+C_D_ID + "_" + "C_"+C_ID);
        		customer.setC_BALANCE(customer.getC_BALANCE() - this.H_AMOUNT);
        		customer.setC_YTD_PAYMENT(customer.getC_YTD_PAYMENT() + this.H_AMOUNT);
        		customer.setC_PAYMENT_CNT(customer.getC_PAYMENT_CNT() + 1);
        	}
        	else {     /* by C_LAST */
        		nur = new NURand(255,0,999);
        		C_LAST = Integer.toString(nur.calculate());   /* generate C_LAST */
      /* !!! problem to retrieve, because we have no C_ID, just a subset of PK. so there is no PK helping do a read operation */
        		customer = read(Customer.class, "C_W_"+C_W_ID + "_" + "C_D_"+C_D_ID);
        		
        		customer.setC_BALANCE(customer.getC_BALANCE() - this.H_AMOUNT);
        		customer.setC_YTD_PAYMENT(customer.getC_YTD_PAYMENT() + this.H_AMOUNT);
        		customer.setC_PAYMENT_CNT(customer.getC_PAYMENT_CNT() + 1);       		
        	}
        	
        	if(customer.getC_Credit()=="BC") {
        		String data = "C_"+customer.getC_ID()+"_"+"C_D_"+customer.getC_D_ID()+"_"+"C_W_"+customer.getC_W_ID()+"_"+"D_"+district.getD_ID()+"_"+"W_"+warehouse.getW_ID()+"_"+this.H_AMOUNT+"_"+customer.getC_DATA();
        		if(data.length()>500) {
        			data = data.substring(0, 499);  /* C_DATA field never exceeds 500 characters */
        		}
        		customer.setC_DATA(data);
        	}
        	
        	/* Update Customer */
    		write(customer);
        	
        	String key = "H_C_W_"+this.C_W_ID + "_" + "H_C_D_"+this.C_D_ID + "_" + "H_C_"+this.C_ID;
        	history = new History(key);
        	history.setH_D_ID("H_D_"+this.D_ID);
        	history.setH_W_ID("H_W_"+this.W_ID);
        	history.setH_AMOUNT(this.H_AMOUNT);
        	history.setH_DATA(warehouse.getW_NAME() + "    " + district.getD_NAME());
        	/* Insertion History */
        	write(history);
        	
        	return commitTransaction();	
        	
        } catch (Exception e) {
        	e.printStackTrace();
        }
		return null;
		
	}
	
	

}