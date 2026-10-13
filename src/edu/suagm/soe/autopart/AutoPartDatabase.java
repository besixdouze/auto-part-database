package edu.suagm.soe.autopart;

import java.util.ArrayList;

/**************************************************************************
 * 
 * @author Auto Part Database System
 * CPEN 410 - Mobile, web, and Internet programming
 * AutoPartDatabase class - manages the Binary Search Tree of subsystems
 * All methods are synchronized for thread-safe access
 *
 *************************************************************************/

public class AutoPartDatabase {
	
	//Root of the Binary Search Tree
	private SubsystemNode root;
	
	/**************************************************************************
	 * Constructor
	 *************************************************************************/
	public AutoPartDatabase()
	{
		this.root = null;
	}
	
	/**************************************************************************
	 * addSubsystem method
	 * Adds a new subsystem to the BST (synchronized for thread safety)
	 * @param title: subsystem title
	 * @param description: subsystem description
	 * @return true if added, false if already exists
	 *************************************************************************/
	public synchronized boolean addSubsystem(String title, String description)
	{
		if(root == null)
		{
			root = new SubsystemNode(title, description);
			return true;
		}
		
		return addSubsystemRecursive(root, title, description);
	}
	
	/**************************************************************************
	 * addSubsystemRecursive method
	 * Helper method to recursively add a subsystem to the BST
	 * @param node: current node
	 * @param title: subsystem title
	 * @param description: subsystem description
	 * @return true if added, false if already exists
	 *************************************************************************/
	private boolean addSubsystemRecursive(SubsystemNode node, String title, String description)
	{
		int comparison = title.compareToIgnoreCase(node.getTitle());
		
		if(comparison == 0)
		{
			//Subsystem already exists
			return false;
		}
		else if(comparison < 0)
		{
			//Go left
			if(node.getLeft() == null)
			{
				node.setLeft(new SubsystemNode(title, description));
				return true;
			}
			else
			{
				return addSubsystemRecursive(node.getLeft(), title, description);
			}
		}
		else
		{
			//Go right
			if(node.getRight() == null)
			{
				node.setRight(new SubsystemNode(title, description));
				return true;
			}
			else
			{
				return addSubsystemRecursive(node.getRight(), title, description);
			}
		}
	}
	
	/**************************************************************************
	 * findSubsystem method
	 * Searches for a subsystem by title (synchronized for thread safety)
	 * @param title: subsystem title to search
	 * @return SubsystemNode if found, null otherwise
	 *************************************************************************/
	public synchronized SubsystemNode findSubsystem(String title)
	{
		return findSubsystemRecursive(root, title);
	}
	
	/**************************************************************************
	 * findSubsystemRecursive method
	 * Helper method to recursively search for a subsystem
	 * @param node: current node
	 * @param title: subsystem title to search
	 * @return SubsystemNode if found, null otherwise
	 *************************************************************************/
	private SubsystemNode findSubsystemRecursive(SubsystemNode node, String title)
	{
		if(node == null)
		{
			return null;
		}
		
		int comparison = title.compareToIgnoreCase(node.getTitle());
		
		if(comparison == 0)
		{
			return node;
		}
		else if(comparison < 0)
		{
			return findSubsystemRecursive(node.getLeft(), title);
		}
		else
		{
			return findSubsystemRecursive(node.getRight(), title);
		}
	}
	
	/**************************************************************************
	 * getAllSubsystems method
	 * Returns all subsystems in alphabetical order (synchronized for thread safety)
	 * @return ArrayList of all subsystems
	 *************************************************************************/
	public synchronized ArrayList<SubsystemNode> getAllSubsystems()
	{
		ArrayList<SubsystemNode> list = new ArrayList<SubsystemNode>();
		inOrderTraversal(root, list);
		return list;
	}
	
	/**************************************************************************
	 * inOrderTraversal method
	 * Helper method for in-order traversal of BST (gives alphabetical order)
	 * @param node: current node
	 * @param list: list to store subsystems
	 *************************************************************************/
	private void inOrderTraversal(SubsystemNode node, ArrayList<SubsystemNode> list)
	{
		if(node != null)
		{
			inOrderTraversal(node.getLeft(), list);
			list.add(node);
			inOrderTraversal(node.getRight(), list);
		}
	}
	
	/**************************************************************************
	 * addProduct method
	 * Adds a product to a subsystem (synchronized for thread safety)
	 * @param subsystemTitle: subsystem to add product to
	 * @param product: product to add
	 * @return true if added, false if subsystem not found
	 *************************************************************************/
	public synchronized boolean addProduct(String subsystemTitle, Product product)
	{
		SubsystemNode subsystem = findSubsystem(subsystemTitle);
		if(subsystem == null)
		{
			return false;
		}
		
		subsystem.addProduct(product);
		return true;
	}
	
	/**************************************************************************
	 * findProduct method
	 * Searches for a product by name in a specific subsystem (synchronized)
	 * @param subsystemTitle: subsystem to search in
	 * @param productName: product name to search
	 * @return Product if found, null otherwise
	 *************************************************************************/
	public synchronized Product findProduct(String subsystemTitle, String productName)
	{
		SubsystemNode subsystem = findSubsystem(subsystemTitle);
		if(subsystem == null)
		{
			return null;
		}
		
		return subsystem.findProduct(productName);
	}
}
