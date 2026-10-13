package edu.suagm.soe.autopart;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**************************************************************************
 * 
 * @author Auto Part Database System
 * CPEN 410 - Mobile, web, and Internet programming
 * Multi-threaded server for Auto Part Database
 * Creates a new thread for each client connection
 *
 *************************************************************************/

public class AutoPartServer {
	
	public static void main(String[] args) {
		
		//Use default port if not provided
		int port = AutoPartCodes.TCP_PORT;
		
		if(args.length > 0)
		{
			port = Integer.parseInt(args[0]);
		}
		
		//Create shared database
		AutoPartDatabase database = new AutoPartDatabase();
		
		try{
			//Create the socket
			ServerSocket serverSocket = new ServerSocket(port);
			System.out.println("Auto Part Database Server");
			System.out.println("Socket created on port: " + port);
			System.out.println("Waiting for connections...");
			
			//Accept multiple connections (multi-threaded)
			while(true)
			{
				//Wait for connection
				Socket clientSocket = serverSocket.accept();
				System.out.println("New Connection from: " + clientSocket.getInetAddress());
				
				//Create the handler for each client connection
				AutoPartHandler handler = new AutoPartHandler(clientSocket, database);
				
				//Start the handler thread
				handler.start();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
