package edu.suagm.soe.autopart;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**************************************************************************
 * 
 * @author Auto Part Database System
 * CPEN 410 - Mobile, web, and Internet programming
 * Client application with console-based menu interface
 *
 *************************************************************************/

public class AutoPartClient {
	
	//Connection streams
	private DataInputStream serverInputStream;
	private DataOutputStream serverOutputStream;
	private Socket socket;
	private Scanner scanner;
	
	/**************************************************************************
	 * Constructor
	 * @param host: server host
	 * @param port: server port
	 *************************************************************************/
	public AutoPartClient(String host, int port)
	{
		try{
			//Connect to server
			socket = new Socket(host, port);
			System.out.println("Connected to Auto Part Database Server");
			
			//Create streams
			serverInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			serverOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			
			//Create scanner for user input
			scanner = new Scanner(System.in);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**************************************************************************
	 * showMenu method
	 * Displays the main menu
	 *************************************************************************/
	private void showMenu()
	{
		System.out.println("\n========================================");
		System.out.println("   AUTO PART DATABASE SYSTEM");
		System.out.println("========================================");
		System.out.println("1. Add a car subsystem");
		System.out.println("2. Add a product");
		System.out.println("3. Modify a product in stock");
		System.out.println("4. List all car subsystems");
		System.out.println("5. List all products by type");
		System.out.println("6. List all products for a particular subsystem");
		System.out.println("7. Buy a product");
		System.out.println("8. Exit");
		System.out.println("========================================");
		System.out.print("Enter your choice: ");
	}
	
	/**************************************************************************
	 * run method
	 * Main client loop
	 *************************************************************************/
	public void run()
	{
		try{
			int choice = 0;
			
			while(choice != 8)
			{
				showMenu();
				choice = scanner.nextInt();
				scanner.nextLine(); //Consume newline
				
				switch(choice)
				{
					case 1:
						addSubsystem();
						break;
					case 2:
						addProduct();
						break;
					case 3:
						modifyProduct();
						break;
					case 4:
						listSubsystems();
						break;
					case 5:
						listProductsBySubsystem();
						break;
					case 6:
						listProductsForSubsystem();
						break;
					case 7:
						buyProduct();
						break;
					case 8:
						exit();
						break;
					default:
						System.out.println("Invalid choice. Please try again.");
				}
			}
			
			//Close connections
			scanner.close();
			serverInputStream.close();
			serverOutputStream.close();
			socket.close();
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**************************************************************************
	 * addSubsystem method
	 * Adds a new car subsystem
	 *************************************************************************/
	private void addSubsystem()
	{
		try{
			System.out.println("\n--- Add Car Subsystem ---");
			
			//Send command
			serverOutputStream.writeInt(AutoPartCodes.ADD_SUBSYSTEM);
			serverOutputStream.flush();
			
			//Wait for acknowledgment
			int response = serverInputStream.readInt();
			
			if(response == AutoPartCodes.OK)
			{
				//Read subsystem information
				System.out.print("Enter subsystem title: ");
				String title = scanner.nextLine();
				
				System.out.print("Enter description: ");
				String description = scanner.nextLine();
				
				//Send data
				serverOutputStream.writeUTF(title);
				serverOutputStream.writeUTF(description);
				serverOutputStream.flush();
				
				//Wait for confirmation
				response = serverInputStream.readInt();
				
				if(response == AutoPartCodes.OK)
				{
					System.out.println("Subsystem added successfully!");
				}
				else
				{
					System.out.println("Error: Subsystem already exists or could not be added.");
				}
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**************************************************************************
	 * addProduct method
	 * Adds a new product to a subsystem
	 *************************************************************************/
	private void addProduct()
	{
		try{
			System.out.println("\n--- Add Product ---");
			
			//First, list all subsystems
			System.out.println("\nAvailable subsystems:");
			listSubsystemsInternal();
			
			//Send command
			serverOutputStream.writeInt(AutoPartCodes.ADD_PRODUCT);
			serverOutputStream.flush();
			
			//Wait for acknowledgment
			int response = serverInputStream.readInt();
			
			if(response == AutoPartCodes.OK)
			{
				//Read product information
				System.out.print("\nEnter subsystem title: ");
				String subsystemTitle = scanner.nextLine();
				
				System.out.print("Enter product name: ");
				String name = scanner.nextLine();
				
				System.out.print("Enter description: ");
				String description = scanner.nextLine();
				
				System.out.print("Enter UPC: ");
				String upc = scanner.nextLine();
				
				System.out.print("Enter manufacturer: ");
				String manufacturer = scanner.nextLine();
				
				System.out.print("Enter price: ");
				double price = scanner.nextDouble();
				
				System.out.print("Enter quantity in stock: ");
				int quantity = scanner.nextInt();
				scanner.nextLine(); //Consume newline
				
				System.out.print("How many distributors? ");
				int distributorCount = scanner.nextInt();
				scanner.nextLine(); //Consume newline
				
				//Send data
				serverOutputStream.writeUTF(subsystemTitle);
				serverOutputStream.writeUTF(name);
				serverOutputStream.writeUTF(description);
				serverOutputStream.writeUTF(upc);
				serverOutputStream.writeUTF(manufacturer);
				serverOutputStream.writeDouble(price);
				serverOutputStream.writeInt(quantity);
				serverOutputStream.writeInt(distributorCount);
				
				//Send distributor information
				for(int i = 0; i < distributorCount; i++)
				{
					System.out.println("\nDistributor " + (i + 1) + ":");
					System.out.print("  Company name: ");
					String companyName = scanner.nextLine();
					
					System.out.print("  Contact person: ");
					String contactPerson = scanner.nextLine();
					
					System.out.print("  Phone number: ");
					String phoneNumber = scanner.nextLine();
					
					serverOutputStream.writeUTF(companyName);
					serverOutputStream.writeUTF(contactPerson);
					serverOutputStream.writeUTF(phoneNumber);
				}
				
				serverOutputStream.flush();
				
				//Wait for confirmation
				response = serverInputStream.readInt();
				
				if(response == AutoPartCodes.OK)
				{
					System.out.println("Product added successfully!");
				}
				else if(response == AutoPartCodes.SUBSYSTEM_NOT_FOUND)
				{
					System.out.println("Error: Subsystem not found.");
				}
				else
				{
					System.out.println("Error: Could not add product.");
				}
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**************************************************************************
	 * modifyProduct method
	 * Modifies price and quantity of a product
	 *************************************************************************/
	private void modifyProduct()
	{
		try{
			System.out.println("\n--- Modify Product ---");
			
			//Send command
			serverOutputStream.writeInt(AutoPartCodes.MODIFY_PRODUCT);
			serverOutputStream.flush();
			
			//Wait for acknowledgment
			int response = serverInputStream.readInt();
			
			if(response == AutoPartCodes.OK)
			{
				System.out.print("Enter product name: ");
				String productName = scanner.nextLine();
				
				//Send product name
				serverOutputStream.writeUTF(productName);
				serverOutputStream.flush();
				
				//Wait for product information
				response = serverInputStream.readInt();
				
				if(response == AutoPartCodes.PRODUCT_NOT_FOUND)
				{
					System.out.println("Error: Product not found.");
					return;
				}
				
				//Display product information
				String productInfo = serverInputStream.readUTF();
				System.out.println("\nProduct Information:");
				System.out.println(productInfo);
				
				//Ask for confirmation
				System.out.print("Do you want to modify this product? (yes/no): ");
				String confirm = scanner.nextLine();
				
				if(confirm.equalsIgnoreCase("yes") || confirm.equalsIgnoreCase("y"))
				{
					//Send confirmation
					serverOutputStream.writeInt(AutoPartCodes.OK);
					
					//Read new values
					System.out.print("Enter new price: ");
					double newPrice = scanner.nextDouble();
					
					System.out.print("Enter new quantity in stock: ");
					int newQuantity = scanner.nextInt();
					scanner.nextLine(); //Consume newline
					
					//Send new values
					serverOutputStream.writeDouble(newPrice);
					serverOutputStream.writeInt(newQuantity);
					serverOutputStream.flush();
					
					//Wait for confirmation
					response = serverInputStream.readInt();
					
					if(response == AutoPartCodes.OK)
					{
						System.out.println("Product modified successfully!");
					}
					else
					{
						System.out.println("Error: Could not modify product.");
					}
				}
				else
				{
					System.out.println("Modification cancelled.");
				}
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**************************************************************************
	 * listSubsystemsInternal method
	 * Internal method to list subsystems without sending command
	 *************************************************************************/
	private void listSubsystemsInternal()
	{
		try{
			//Send command
			serverOutputStream.writeInt(AutoPartCodes.LIST_SUBSYSTEMS);
			serverOutputStream.flush();
			
			//Receive count
			int count = serverInputStream.readInt();
			
			if(count == 0)
			{
				System.out.println("No subsystems available.");
			}
			else
			{
				for(int i = 0; i < count; i++)
				{
					String subsystem = serverInputStream.readUTF();
					System.out.println("  " + (i + 1) + ". " + subsystem);
				}
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**************************************************************************
	 * listSubsystems method
	 * Lists all car subsystems
	 *************************************************************************/
	private void listSubsystems()
	{
		System.out.println("\n--- All Car Subsystems ---");
		listSubsystemsInternal();
	}
	
	/**************************************************************************
	 * listProductsBySubsystem method
	 * Lists all products organized by subsystem
	 *************************************************************************/
	private void listProductsBySubsystem()
	{
		try{
			System.out.println("\n--- All Products by Subsystem ---");
			
			//Send command
			serverOutputStream.writeInt(AutoPartCodes.LIST_PRODUCTS_BY_SUBSYSTEM);
			serverOutputStream.flush();
			
			//Receive result
			String result = serverInputStream.readUTF();
			System.out.println(result);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**************************************************************************
	 * listProductsForSubsystem method
	 * Lists products for a particular subsystem
	 *************************************************************************/
	private void listProductsForSubsystem()
	{
		try{
			System.out.println("\n--- Products for Subsystem ---");
			
			//First, list all subsystems
			System.out.println("\nAvailable subsystems:");
			listSubsystemsInternal();
			
			//Send command
			serverOutputStream.writeInt(AutoPartCodes.LIST_PRODUCTS_FOR_SUBSYSTEM);
			serverOutputStream.flush();
			
			//Wait for acknowledgment
			int response = serverInputStream.readInt();
			
			if(response == AutoPartCodes.OK)
			{
				System.out.print("\nEnter subsystem title: ");
				String subsystemTitle = scanner.nextLine();
				
				//Send subsystem title
				serverOutputStream.writeUTF(subsystemTitle);
				serverOutputStream.flush();
				
				//Wait for response
				response = serverInputStream.readInt();
				
				if(response == AutoPartCodes.SUBSYSTEM_NOT_FOUND)
				{
					System.out.println("Error: Subsystem not found.");
				}
				else if(response == AutoPartCodes.OK)
				{
					String result = serverInputStream.readUTF();
					System.out.println("\n" + result);
				}
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**************************************************************************
	 * buyProduct method
	 * Handles product purchase
	 *************************************************************************/
	private void buyProduct()
	{
		try{
			System.out.println("\n--- Buy Product ---");
			
			//First, list all subsystems
			System.out.println("\nAvailable subsystems:");
			listSubsystemsInternal();
			
			//Send command
			serverOutputStream.writeInt(AutoPartCodes.BUY_PRODUCT);
			serverOutputStream.flush();
			
			//Wait for acknowledgment
			int response = serverInputStream.readInt();
			
			if(response == AutoPartCodes.OK)
			{
				System.out.print("\nEnter subsystem title: ");
				String subsystemTitle = scanner.nextLine();
				
				System.out.print("Enter product name: ");
				String productName = scanner.nextLine();
				
				//Send data
				serverOutputStream.writeUTF(subsystemTitle);
				serverOutputStream.writeUTF(productName);
				serverOutputStream.flush();
				
				//Wait for response
				response = serverInputStream.readInt();
				
				if(response == AutoPartCodes.PRODUCT_NOT_FOUND)
				{
					System.out.println("Error: Product not found.");
				}
				else if(response == AutoPartCodes.OK)
				{
					//Display product information
					String productInfo = serverInputStream.readUTF();
					System.out.println("\nProduct Information:");
					System.out.println(productInfo);
					
					//Ask for purchase confirmation
					System.out.print("Do you want to buy this product? (yes/no): ");
					String confirm = scanner.nextLine();
					
					if(confirm.equalsIgnoreCase("yes") || confirm.equalsIgnoreCase("y"))
					{
						//Send confirmation
						serverOutputStream.writeInt(AutoPartCodes.OK);
						serverOutputStream.flush();
						
						//Wait for final response
						response = serverInputStream.readInt();
						
						if(response == AutoPartCodes.OK)
						{
							System.out.println("Purchase successful! Stock has been updated.");
						}
						else if(response == AutoPartCodes.OUT_OF_STOCK)
						{
							System.out.println("Error: Product is out of stock.");
						}
						else
						{
							System.out.println("Error: Could not complete purchase.");
						}
					}
					else
					{
						System.out.println("Purchase cancelled.");
					}
				}
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**************************************************************************
	 * exit method
	 * Closes connection and exits
	 *************************************************************************/
	private void exit()
	{
		try{
			System.out.println("\nExiting...");
			
			//Send exit command
			serverOutputStream.writeInt(AutoPartCodes.EXIT);
			serverOutputStream.flush();
			
			//Wait for confirmation
			int response = serverInputStream.readInt();
			
			if(response == AutoPartCodes.OK)
			{
				System.out.println("Goodbye!");
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**************************************************************************
	 * Main method
	 * @param args: command line arguments
	 *************************************************************************/
	public static void main(String[] args)
	{
		String host = "localhost";
		int port = AutoPartCodes.TCP_PORT;
		
		if(args.length > 0)
		{
			host = args[0];
		}
		
		if(args.length > 1)
		{
			port = Integer.parseInt(args[1]);
		}
		
		AutoPartClient client = new AutoPartClient(host, port);
		client.run();
	}
}
