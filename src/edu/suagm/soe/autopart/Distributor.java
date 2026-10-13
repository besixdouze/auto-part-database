package edu.suagm.soe.autopart;

/**************************************************************************
 * 
 * @author Auto Part Database System
 * CPEN 410 - Mobile, web, and Internet programming
 * Distributor class - represents a distributor for a product
 * Implements a node in a singly linked list
 *
 *************************************************************************/

public class Distributor {
	
	//Distributor information
	private String companyName;
	private String contactPerson;
	private String phoneNumber;
	
	//Next distributor in the linked list
	private Distributor next;
	
	/**************************************************************************
	 * Constructor
	 * @param companyName: company name
	 * @param contactPerson: contact person name
	 * @param phoneNumber: phone number
	 *************************************************************************/
	public Distributor(String companyName, String contactPerson, String phoneNumber)
	{
		this.companyName = companyName;
		this.contactPerson = contactPerson;
		this.phoneNumber = phoneNumber;
		this.next = null;
	}
	
	//Getters
	public String getCompanyName() { return companyName; }
	public String getContactPerson() { return contactPerson; }
	public String getPhoneNumber() { return phoneNumber; }
	public Distributor getNext() { return next; }
	
	//Setters
	public void setNext(Distributor next) { this.next = next; }
	
	/**************************************************************************
	 * toString method
	 * @return String representation of distributor
	 *************************************************************************/
	public String toString()
	{
		return companyName + " (Contact: " + contactPerson + ", Phone: " + phoneNumber + ")";
	}
}
