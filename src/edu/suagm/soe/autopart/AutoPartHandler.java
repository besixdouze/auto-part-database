package edu.suagm.soe.autopart;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**************************************************************************
 * 
 * @author Auto Part Database System
 * CPEN 410 - Mobile, web, and Internet programming
 * This class is designed for handling client connections to the 
 * Auto Part Database server
 *
 *************************************************************************/

public class AutoPartHandler extends Thread {
	
	// Data stream and output streams for data transfer
	private DataInputStream clientInputStream;
	private DataOutputStream clientOutputStream;
	
	//Client socket for maintaining connection with the client
	private Socket clientSocket;
	
	//Shared database
	private AutoPartDatabase database;
	
	/***************************************************************************
	 * Constructor
	 * @param clientSocket: client socket created when the client connects to
	 * the server
	 * @param database: shared database
	 */
	
	public AutoPartHandler(Socket clientSocket, AutoPartDatabase database)
	{
		try{
			this.clientSocket = clientSocket;
			this.database = database;
			clientInputStream = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
			clientOutputStream = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/*************************************
	 * 
	 * Run method: run the dataTransfer method
	 */
	public void run()
	{
		this.dataTransfer();
	}
	
	/*****************************************************************************
	 * Data transfer method
	 * Tasks: Manage connection with the client.
	 * 		Receive commands from the client
	 * 		Invoke the appropriate method depending on the received command
	 * 
	 *****************************************************************************/
	public void dataTransfer()
	{
		try{
			
			int readCommand;
			
			do {
				//Wait for command
				System.out.println("Waiting for a command");
				readCommand=clientInputStream.readInt();
				System.out.println("Received Command: " + readCommand);
				
				switch(readCommand)
				{
					//Add subsystem command
					case AutoPartCodes.ADD_SUBSYSTEM:
						addSubsystemCommand();
						break;
					
					//Add product command
					case AutoPartCodes.ADD_PRODUCT:
						addProductCommand();
						break;
					
					//Modify product command
					case AutoPartCodes.MODIFY_PRODUCT:
						modifyProductCommand();
						break;
					
					//List all subsystems command
					case AutoPartCodes.LIST_SUBSYSTEMS:
						listSubsystemsCommand();
						break;
					
					//List all products by subsystem command
					case AutoPartCodes.LIST_PRODUCTS_BY_SUBSYSTEM:
						listProductsBySubsystemCommand();
						break;
					
					//List products for particular subsystem command
					case AutoPartCodes.LIST_PRODUCTS_FOR_SUBSYSTEM:
						listProductsForSubsystemCommand();
						break;
					
					//Buy product command
					case AutoPartCodes.BUY_PRODUCT:
						buyProductCommand();
						break;
					
					//Exit command
					case AutoPartCodes.EXIT:
						exitCommand();
						break;
				}
			}while(readCommand != AutoPartCodes.EXIT);
			
			//Close connection and socket
			clientInputStream.close();
			clientOutputStream.close();
			clientSocket.close();
			
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/***************************************************************************
	 * exitCommand Method
	 * Tasks: Sends OK confirmation code to the client for closing connection 
	 * 
	 **************************************************************************/
	
	private void exitCommand()
	{
		try{
			clientOutputStream.writeInt(AutoPartCodes.OK);
			clientOutputStream.flush();
			System.out.println("The connection has been closed!");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/***************************************************************************
	 * addSubsystemCommand Method
	 * Tasks: Receives subsystem title and description, adds to database
	 * 
	 **************************************************************************/
	
	private void addSubsystemCommand()
	{
		try{
			//Send OK acknowledgment
			clientOutputStream.writeInt(AutoPartCodes.OK);
			clientOutputStream.flush();
			
			//Receive title
			String title = clientInputStream.readUTF();
			
			//Receive description
			String description = clientInputStream.readUTF();
			
			System.out.println("Adding subsystem: " + title);
			
			//Add to database
			boolean added = database.addSubsystem(title, description);
			
			if(added)
			{
				clientOutputStream.writeInt(AutoPartCodes.OK);
			}
			else
			{
				clientOutputStream.writeInt(AutoPartCodes.WRONGCOMMAND);
			}
			clientOutputStream.flush();
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/***************************************************************************
	 * addProductCommand Method
	 * Tasks: Receives product information and adds to database
	 * 
	 **************************************************************************/
	
	private void addProductCommand()
	{
		try{
			//Send OK acknowledgment
			clientOutputStream.writeInt(AutoPartCodes.OK);
			clientOutputStream.flush();
			
			//Receive subsystem title
			String subsystemTitle = clientInputStream.readUTF();
			
			//Receive product information
			String name = clientInputStream.readUTF();
			String description = clientInputStream.readUTF();
			String upc = clientInputStream.readUTF();
			String manufacturer = clientInputStream.readUTF();
			double price = clientInputStream.readDouble();
			int quantity = clientInputStream.readInt();
			
			//Create product
			Product product = new Product(name, description, upc, manufacturer, price, quantity);
			
			//Receive distributors
			int distributorCount = clientInputStream.readInt();
			for(int i = 0; i < distributorCount; i++)
			{
				String companyName = clientInputStream.readUTF();
				String contactPerson = clientInputStream.readUTF();
				String phoneNumber = clientInputStream.readUTF();
				product.addDistributor(companyName, contactPerson, phoneNumber);
			}
			
			System.out.println("Adding product: " + name + " to subsystem: " + subsystemTitle);
			
			//Add to database
			boolean added = database.addProduct(subsystemTitle, product);
			
			if(added)
			{
				clientOutputStream.writeInt(AutoPartCodes.OK);
			}
			else
			{
				clientOutputStream.writeInt(AutoPartCodes.SUBSYSTEM_NOT_FOUND);
			}
			clientOutputStream.flush();
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/***************************************************************************
	 * modifyProductCommand Method
	 * Tasks: Finds product and modifies price and quantity
	 * 
	 **************************************************************************/
	
	private void modifyProductCommand()
	{
		try{
			//Send OK acknowledgment
			clientOutputStream.writeInt(AutoPartCodes.OK);
			clientOutputStream.flush();
			
			//Receive product name
			String productName = clientInputStream.readUTF();
			
			//Search for product in all subsystems
			ArrayList<SubsystemNode> subsystems = database.getAllSubsystems();
			Product foundProduct = null;
			
			for(SubsystemNode subsystem : subsystems)
			{
				foundProduct = subsystem.findProduct(productName);
				if(foundProduct != null)
				{
					break;
				}
			}
			
			if(foundProduct == null)
			{
				//Product not found
				clientOutputStream.writeInt(AutoPartCodes.PRODUCT_NOT_FOUND);
				clientOutputStream.flush();
				return;
			}
			
			//Send product information
			clientOutputStream.writeInt(AutoPartCodes.OK);
			clientOutputStream.writeUTF(foundProduct.toString());
			clientOutputStream.flush();
			
			//Wait for confirmation
			int confirmation = clientInputStream.readInt();
			
			if(confirmation == AutoPartCodes.OK)
			{
				//Receive new price and quantity
				double newPrice = clientInputStream.readDouble();
				int newQuantity = clientInputStream.readInt();
				
				//Update product
				foundProduct.setPrice(newPrice);
				foundProduct.setQuantityInStock(newQuantity);
				
				System.out.println("Modified product: " + productName);
				
				clientOutputStream.writeInt(AutoPartCodes.OK);
				clientOutputStream.flush();
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/***************************************************************************
	 * listSubsystemsCommand Method
	 * Tasks: Sends all subsystems in alphabetical order
	 * 
	 **************************************************************************/
	
	private void listSubsystemsCommand()
	{
		try{
			//Get all subsystems
			ArrayList<SubsystemNode> subsystems = database.getAllSubsystems();
			
			//Send count
			clientOutputStream.writeInt(subsystems.size());
			
			//Send each subsystem
			for(SubsystemNode subsystem : subsystems)
			{
				clientOutputStream.writeUTF(subsystem.toString());
			}
			
			clientOutputStream.flush();
			
			System.out.println("Sent " + subsystems.size() + " subsystems");
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/***************************************************************************
	 * listProductsBySubsystemCommand Method
	 * Tasks: Sends all products organized by subsystem
	 * 
	 **************************************************************************/
	
	private void listProductsBySubsystemCommand()
	{
		try{
			//Get all subsystems
			ArrayList<SubsystemNode> subsystems = database.getAllSubsystems();
			
			StringBuilder result = new StringBuilder();
			
			for(SubsystemNode subsystem : subsystems)
			{
				result.append("\n=== ").append(subsystem.getTitle()).append(" ===\n");
				
				Product productList = subsystem.getProductList();
				
				if(productList == null)
				{
					result.append("  No products\n");
				}
				else
				{
					Product current = productList;
					Product start = productList;
					
					do {
						result.append(current.toString()).append("\n");
						current = current.getNext();
					} while(current != start);
				}
			}
			
			//Send result
			clientOutputStream.writeUTF(result.toString());
			clientOutputStream.flush();
			
			System.out.println("Sent products by subsystem");
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/***************************************************************************
	 * listProductsForSubsystemCommand Method
	 * Tasks: Lists products for a particular subsystem
	 * 
	 **************************************************************************/
	
	private void listProductsForSubsystemCommand()
	{
		try{
			//Send OK acknowledgment
			clientOutputStream.writeInt(AutoPartCodes.OK);
			clientOutputStream.flush();
			
			//Receive subsystem title
			String subsystemTitle = clientInputStream.readUTF();
			
			//Find subsystem
			SubsystemNode subsystem = database.findSubsystem(subsystemTitle);
			
			if(subsystem == null)
			{
				clientOutputStream.writeInt(AutoPartCodes.SUBSYSTEM_NOT_FOUND);
				clientOutputStream.flush();
				return;
			}
			
			StringBuilder result = new StringBuilder();
			result.append("Products for subsystem: ").append(subsystemTitle).append("\n\n");
			
			Product productList = subsystem.getProductList();
			
			if(productList == null)
			{
				result.append("No products available\n");
			}
			else
			{
				Product current = productList;
				Product start = productList;
				
				do {
					result.append("Name: ").append(current.getName()).append("\n");
					result.append("Manufacturer: ").append(current.getManufacturer()).append("\n");
					result.append("Distributors:\n");
					
					Distributor dist = current.getDistributorList();
					if(dist == null)
					{
						result.append("  None\n");
					}
					else
					{
						while(dist != null)
						{
							result.append("  - ").append(dist.toString()).append("\n");
							dist = dist.getNext();
						}
					}
					result.append("\n");
					
					current = current.getNext();
				} while(current != start);
			}
			
			clientOutputStream.writeInt(AutoPartCodes.OK);
			clientOutputStream.writeUTF(result.toString());
			clientOutputStream.flush();
			
			System.out.println("Sent products for subsystem: " + subsystemTitle);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/***************************************************************************
	 * buyProductCommand Method
	 * Tasks: Handles product purchase and updates stock
	 * 
	 **************************************************************************/
	
	private void buyProductCommand()
	{
		try{
			//Send OK acknowledgment
			clientOutputStream.writeInt(AutoPartCodes.OK);
			clientOutputStream.flush();
			
			//Receive subsystem title
			String subsystemTitle = clientInputStream.readUTF();
			
			//Receive product name
			String productName = clientInputStream.readUTF();
			
			//Find product
			Product product = database.findProduct(subsystemTitle, productName);
			
			if(product == null)
			{
				clientOutputStream.writeInt(AutoPartCodes.PRODUCT_NOT_FOUND);
				clientOutputStream.flush();
				return;
			}
			
			//Send product information
			clientOutputStream.writeInt(AutoPartCodes.OK);
			clientOutputStream.writeUTF(product.toString());
			clientOutputStream.flush();
			
			//Wait for purchase confirmation
			int confirmation = clientInputStream.readInt();
			
			if(confirmation == AutoPartCodes.OK)
			{
				//Try to decrease stock
				boolean success = product.decreaseStock();
				
				if(success)
				{
					System.out.println("Product purchased: " + productName + 
									   ". New stock: " + product.getQuantityInStock());
					clientOutputStream.writeInt(AutoPartCodes.OK);
				}
				else
				{
					clientOutputStream.writeInt(AutoPartCodes.OUT_OF_STOCK);
				}
				clientOutputStream.flush();
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
