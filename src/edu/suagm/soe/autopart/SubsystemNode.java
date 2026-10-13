package edu.suagm.soe.autopart;

/**************************************************************************
 * 
 * @author Auto Part Database System
 * CPEN 410 - Mobile, web, and Internet programming
 * SubsystemNode class - represents a node in the Binary Search Tree
 * Contains subsystem information and a double circular linked list of products
 *
 *************************************************************************/

public class SubsystemNode {
	
	//Subsystem information
	private String title;
	private String description;
	
	//Double circular linked list of products
	private Product productList;
	
	//BST pointers
	private SubsystemNode left;
	private SubsystemNode right;
	
	/**************************************************************************
	 * Constructor
	 * @param title: subsystem title
	 * @param description: subsystem description
	 *************************************************************************/
	public SubsystemNode(String title, String description)
	{
		this.title = title;
		this.description = description;
		this.productList = null;
		this.left = null;
		this.right = null;
	}
	
	//Getters
	public String getTitle() { return title; }
	public String getDescription() { return description; }
	public Product getProductList() { return productList; }
	public SubsystemNode getLeft() { return left; }
	public SubsystemNode getRight() { return right; }
	
	//Setters
	public void setLeft(SubsystemNode left) { this.left = left; }
	public void setRight(SubsystemNode right) { this.right = right; }
	
	/**************************************************************************
	 * addProduct method
	 * Adds a product to the double circular linked list in alphabetical order
	 * @param product: the product to add
	 *************************************************************************/
	public void addProduct(Product product)
	{
		//If list is empty
		if(productList == null)
		{
			productList = product;
			product.setNext(product);
			product.setPrevious(product);
			return;
		}
		
		//Find position to insert (alphabetically by name)
		Product current = productList;
		boolean inserted = false;
		
		//Check if should insert before head
		if(product.getName().compareToIgnoreCase(productList.getName()) < 0)
		{
			//Insert before head
			Product tail = productList.getPrevious();
			
			product.setNext(productList);
			product.setPrevious(tail);
			tail.setNext(product);
			productList.setPrevious(product);
			
			productList = product; //Update head
			return;
		}
		
		//Traverse to find insertion point
		Product start = productList;
		current = productList;
		
		do {
			Product nextNode = current.getNext();
			
			//Check if we should insert between current and next
			if(nextNode == start || product.getName().compareToIgnoreCase(nextNode.getName()) < 0)
			{
				product.setNext(nextNode);
				product.setPrevious(current);
				current.setNext(product);
				nextNode.setPrevious(product);
				inserted = true;
				break;
			}
			
			current = nextNode;
		} while(current != start);
	}
	
	/**************************************************************************
	 * findProduct method
	 * Searches for a product by name
	 * @param name: product name to search
	 * @return Product if found, null otherwise
	 *************************************************************************/
	public Product findProduct(String name)
	{
		if(productList == null) return null;
		
		Product current = productList;
		Product start = productList;
		
		do {
			if(current.getName().equalsIgnoreCase(name))
			{
				return current;
			}
			current = current.getNext();
		} while(current != start);
		
		return null;
	}
	
	/**************************************************************************
	 * toString method
	 * @return String representation of subsystem
	 *************************************************************************/
	public String toString()
	{
		return title + " - " + description;
	}
}
