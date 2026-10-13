package edu.suagm.soe.autopart;

/**************************************************************************
 * 
 * @author Auto Part Database System
 * CPEN 410 - Mobile, web, and Internet programming
 * Product class - represents an auto part product
 * Implements a node in a double circular linked list
 *
 *************************************************************************/

public class Product {
	
	//Product information
	private String name;
	private String description;
	private String upc;
	private String manufacturer;
	private double price;
	private int quantityInStock;
	
	//Distributor list (singly linked list, sorted by company name)
	private Distributor distributorList;
	
	//Double circular linked list pointers
	private Product next;
	private Product previous;
	
	/**************************************************************************
	 * Constructor
	 * @param name: product name
	 * @param description: product description
	 * @param upc: Universal Product Code
	 * @param manufacturer: manufacturer name
	 * @param price: product price
	 * @param quantityInStock: quantity in stock
	 *************************************************************************/
	public Product(String name, String description, String upc, String manufacturer, 
				   double price, int quantityInStock)
	{
		this.name = name;
		this.description = description;
		this.upc = upc;
		this.manufacturer = manufacturer;
		this.price = price;
		this.quantityInStock = quantityInStock;
		this.distributorList = null;
		this.next = this;
		this.previous = this;
	}
	
	//Getters
	public String getName() { return name; }
	public String getDescription() { return description; }
	public String getUpc() { return upc; }
	public String getManufacturer() { return manufacturer; }
	public double getPrice() { return price; }
	public int getQuantityInStock() { return quantityInStock; }
	public Distributor getDistributorList() { return distributorList; }
	public Product getNext() { return next; }
	public Product getPrevious() { return previous; }
	
	//Setters
	public void setPrice(double price) { this.price = price; }
	public void setQuantityInStock(int quantityInStock) { this.quantityInStock = quantityInStock; }
	public void setNext(Product next) { this.next = next; }
	public void setPrevious(Product previous) { this.previous = previous; }
	
	/**************************************************************************
	 * addDistributor method
	 * Adds a distributor to the list in alphabetical order by company name
	 * @param companyName: company name
	 * @param contactPerson: contact person
	 * @param phoneNumber: phone number
	 *************************************************************************/
	public void addDistributor(String companyName, String contactPerson, String phoneNumber)
	{
		Distributor newDistributor = new Distributor(companyName, contactPerson, phoneNumber);
		
		//If list is empty
		if(distributorList == null)
		{
			distributorList = newDistributor;
			return;
		}
		
		//Insert at beginning if new company name comes first alphabetically
		if(companyName.compareToIgnoreCase(distributorList.getCompanyName()) < 0)
		{
			newDistributor.setNext(distributorList);
			distributorList = newDistributor;
			return;
		}
		
		//Find correct position to insert
		Distributor current = distributorList;
		while(current.getNext() != null && 
			  current.getNext().getCompanyName().compareToIgnoreCase(companyName) < 0)
		{
			current = current.getNext();
		}
		
		//Insert the new distributor
		newDistributor.setNext(current.getNext());
		current.setNext(newDistributor);
	}
	
	/**************************************************************************
	 * decreaseStock method
	 * Decreases the quantity in stock by 1
	 * @return true if successful, false if out of stock
	 *************************************************************************/
	public boolean decreaseStock()
	{
		if(quantityInStock > 0)
		{
			quantityInStock--;
			return true;
		}
		return false;
	}
	
	/**************************************************************************
	 * toString method
	 * @return String representation of product
	 *************************************************************************/
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Name: ").append(name).append("\n");
		sb.append("Description: ").append(description).append("\n");
		sb.append("UPC: ").append(upc).append("\n");
		sb.append("Manufacturer: ").append(manufacturer).append("\n");
		sb.append("Price: $").append(String.format("%.2f", price)).append("\n");
		sb.append("Quantity in Stock: ").append(quantityInStock).append("\n");
		sb.append("Distributors: ");
		
		if(distributorList == null)
		{
			sb.append("None\n");
		}
		else
		{
			sb.append("\n");
			Distributor current = distributorList;
			while(current != null)
			{
				sb.append("  - ").append(current.toString()).append("\n");
				current = current.getNext();
			}
		}
		
		return sb.toString();
	}
}
