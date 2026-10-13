package edu.suagm.soe.autopart;

/**************************************************************************
 * 
 * @author Auto Part Database System
 * CPEN 410 - Mobile, web, and Internet programming
 * This class defines command codes for client-server communication
 *
 *************************************************************************/

public class AutoPartCodes {

	//Buffer size
	public static final int BUFFER_SIZE=1024;
	
	//TCP Port
	public static final int TCP_PORT=9000;
	
	//Command codes--------------------------------------------
	//OK 
	public static final int OK=1;
	
	//Add subsystem
	public static final int ADD_SUBSYSTEM=2;
	
	//Add product
	public static final int ADD_PRODUCT=3;
	
	//Modify product
	public static final int MODIFY_PRODUCT=4;
	
	//List all subsystems
	public static final int LIST_SUBSYSTEMS=5;
	
	//List all products by subsystem
	public static final int LIST_PRODUCTS_BY_SUBSYSTEM=6;
	
	//List products for particular subsystem
	public static final int LIST_PRODUCTS_FOR_SUBSYSTEM=7;
	
	//Buy product
	public static final int BUY_PRODUCT=8;
	
	//Close connection
	public static final int EXIT=9;
	
	
	//Error messages----------------------------------------------------
	//Subsystem not found
	public static final int SUBSYSTEM_NOT_FOUND=20;
	
	//Product not found
	public static final int PRODUCT_NOT_FOUND=21;
	
	//Out of stock
	public static final int OUT_OF_STOCK=22;
	
	//No valid command
	public static final int WRONGCOMMAND=30;
	
}
