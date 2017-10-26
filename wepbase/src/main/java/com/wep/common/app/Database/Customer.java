/*
 * **************************************************************************
 * Project Name		:	VAJRA
 * 
 * File Name		:	Customer
 * 
 * Purpose			:	Represents Customer table, takes care of intializing
 * 						assining and returning values of all the variables.
 * 
 * DateOfCreation	:	15-October-2012
 * 
 * Author			:	Balasubramanya Bharadwaj B S
 * 
 * **************************************************************************
 */

package com.wep.common.app.Database;

public class Customer {

	// Private Variable
	String strCustName, strCustContactNumber, strCustAddress, strCustGSTIN;
	int iCustId;
	double fLastTransaction, fTotalTransaction;
	double fCreditAmount, dCreditLimit;

	// Default constructor
	public Customer() {
		this.strCustAddress = "";
		this.strCustName = "";
		this.strCustGSTIN="";
		this.strCustContactNumber = "";
		this.iCustId = 0;
		this.fLastTransaction = 0;
		this.fTotalTransaction = 0;
		this.fCreditAmount = 0;
		this.dCreditLimit = 0;
	}

	// Parameterized construcor
	public Customer(String CustAddress, String CustName, String CustContactNumber, double LastTransaction,
					double TotalTransaction, double CreaditAmount,String gstin, double dCreditLimit) {
		this.strCustAddress = CustAddress;
		this.strCustName = CustName;
		this.strCustContactNumber = CustContactNumber;
		this.fLastTransaction = LastTransaction;
		this.fTotalTransaction = TotalTransaction;
		this.fCreditAmount = CreaditAmount;
		this.dCreditLimit = dCreditLimit;
		this.strCustGSTIN= gstin;
	}

	public double getdCreditLimit() {
		return dCreditLimit;
	}

	public void setdCreditLimit(double dCreditLimit) {
		this.dCreditLimit = dCreditLimit;
	}

	public String getStrCustGSTIN() {
		return strCustGSTIN;
	}

	public void setStrCustGSTIN(String strCustGSTIN) {
		this.strCustGSTIN = strCustGSTIN;
	}

	// getting CustAddress
	public String getCustAddress() {
		return this.strCustAddress;
	}

	// getting CustName
	public String getCustName() {
		return this.strCustName;
	}

	// getting Contact number
	public String getCustContactNumber() {
		return this.strCustContactNumber;
	}

	// getting CustId
	public int getCustId() {
		return this.iCustId;
	}

	// getting LastTransaction
	public double getLastTransaction() {
		return this.fLastTransaction;
	}

	// getting TotalTransaction
	public double getTotalTransaction() {
		return this.fTotalTransaction;
	}

	// getting CreditAmount
	public double getCreditAmount() {
		return this.fCreditAmount;
	}

	// setting CustAddress
	public void setCustAddress(String CustAddress) {
		this.strCustAddress = CustAddress;
	}

	// setting CustName
	public void setCustName(String CustName) {
		this.strCustName = CustName;
	}

	// setting CustContactNumber
	public void setCustContactNumber(String CustContactNumber) {
		this.strCustContactNumber = CustContactNumber;
	}

	// setting EmployeeId
	public void setCustId(int CustId) {
		this.iCustId = CustId;
	}

	// setting LastTransaction
	public void setLastTransaction(double LastTransaction) {
		this.fLastTransaction = LastTransaction;
	}

	// setting TotalTransaction
	public void setTotalTransaction(double TotalTransaction) {
		this.fTotalTransaction = TotalTransaction;
	}

	// setting CreaditAmount
	public void setCeraditAmount(double CreaditAmount) {
		this.fCreditAmount = CreaditAmount;
	}
}
