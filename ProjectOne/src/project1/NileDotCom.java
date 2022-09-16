/*
	Name: Joshua Wood
	Course: CNT 4714 – Fall 2022
	Assignment title: Project 1 – Event-driven Enterprise Simulation
	Date: September 15, 2022

*/

package project1;

// ************************************************************************
// IMPORTS
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

// ************************************************************************
public class NileDotCom extends JFrame {
	// static variables to hold frame dimensions in pixels
	private static final int WIDTH = 700; // FlowLayout for buttons
	private static final int HEIGHT = 330; // GridLayout for buttons
	
	// labels, fields, and buttons
	private JLabel		blankLabel, idLabel, qtyLabel, itemLabel, totalLabel;
	private JTextField	blankTextField, idTextField, qtyTextField, itemTextField, totalTextField;
	private JButton		processButton, confirmButton, viewButton, finishButton, newButton, exitButton;
	
	// declare reference variables for event handlers
	private ProcessButtonHandler 	procbHandler ; // eventHandler variable for ProcessButtonHandler
	private ConfirmButtonHandler 	confbHandler ; // eventHandler variable for ConfirmButtonHandler
	private ViewButtonHandler		viewbHandler ; // eventHandler variable for ViewButtonHandler
	private FinishButtonHandler		finbHandler  ; // eventHandler variable for FinishButtonHandler
	private NewButtonHandler		newbHandler  ; // eventHandler variable for NewButtonHandler
	private ExitButtonHandler		exitbHandler ; // eventHandler variable for ExitButtonHandler
	
	// declare formatting variables
	static NumberFormat 	currencyFormatter = NumberFormat.getCurrencyInstance();
	static NumberFormat 	percentFormatter = NumberFormat.getPercentInstance();
	static DecimalFormat	decimalFormatter = (DecimalFormat) percentFormatter;
	
	// arrays for holding shopping cart items for later processing
	static String [] itemIDArray		;
	static String [] itemTitleArray		;
	static String [] itemInStockArray	;
	static double [] itemPriceArray		;
	static int	  [] itemQuantityArray	;
	static double [] itemDiscountArray	;
	static double [] itemSubtotalArray 	;
	
	// initialize variables
	static String itemID="", itemTitle="", outputStr="", maxArraySizeStr="",
			itemPriceStr="", itemInStock="", itemQuantityStr="", itemSubtotalStr="", itemDiscountStr="",
			taxRateStr, discountRateStr, orderSubTotalStr;
	static double itemPrice=0, itemSubTotal=0, orderSubTotal=0, orderTotal=0,
			itemDiscount=0, orderTaxAmount;
	
	static int itemQuantity=0, itemCount=0, maxArraySize=0;
	
	final static double TAX_RATE = 0.060,
						DISCOUNT_FOR_05 = .10,
						DISCOUNT_FOR_10 = .15,
						DISCOUNT_FOR_15 = .20;
	
	String fileName;
//************************************************************************
	public NileDotCom () {
		/*
			constructor for GUI
			define all labels, fields, and buttons
			set all basic dimensions
		*/

		// set the titel and size of the frame
		setTitle("Nile Dot Com - Fall 2022"); // set the title of the frame
		setSize	(WIDTH, HEIGHT); // set the size of the frame
	
		// instantiate JLabel Objects
		blankLabel  = new JLabel(" ", SwingConstants.RIGHT); // instantiate blankLabel text, swinging the constants to the right
		idLabel 	= new JLabel("Enter item ID for Item #" + (itemCount+1) + ":", SwingConstants.RIGHT); // instantiate the idLabel text,   swinging the constants to the right
		qtyLabel	= new JLabel("Enter quantity for Item #"+ (itemCount+1) + ":", SwingConstants.RIGHT); // instantiate the qtyLabel text,  swinging the constants to the right
		itemLabel	= new JLabel("Details for Item #" + (itemCount+1) + ":", SwingConstants.RIGHT      ); // instantiate the itemLabel text, swinging the constants to the right
		totalLabel	= new JLabel("Order Subtotal for " + itemCount + " item(s):", SwingConstants.RIGHT ); // instantiate the totalLabel text, swining the constants to the right
		
		// instantiate JTextField Objects
		blankTextField = new JTextField (); // instantiate the blankTextField
		idTextField		= new JTextField(); // instantiate the idTextField
		qtyTextField 	= new JTextField(); // instantiate the qtyTextField
		itemTextField	= new JTextField(); // instantiate the itemTextField
		totalTextField	= new JTextField(); // instantiate the totalTextField
		
		// instantiate buttons and register handles
		processButton		= new JButton("Process Item #" + (itemCount+1)); // instantiate processButton with text
		procbHandler 		= new ProcessButtonHandler(); // instantiate the procbHandler to the ProcessButtonHandler method
		processButton.addActionListener(procbHandler); // adds action listener to the button to call the procbHandler
		
		confirmButton		= new JButton("Confirm Item #" + (itemCount+1)); // instantiate confirmButton with text
		confbHandler		= new ConfirmButtonHandler(); // instantiate the confirmbHandler to the ConfirmButtonHandler method
		confirmButton.addActionListener(confbHandler); // adds action listener to the button to call the confirmbHandler
		
		viewButton			= new JButton("View Order"); // instantiate viewButton with text
		viewbHandler		= new ViewButtonHandler(); // instantiate the viewbHandler to the ViewButtonHandler method
		viewButton.addActionListener(viewbHandler); // adds action listener to the button to call the viewbHandler
		
		finishButton		= new JButton("Finish Order"); // instantiate finishButton with text
		finbHandler			= new FinishButtonHandler(); // instantiate the finbHandler to the FinishButtonHandler method
		finishButton.addActionListener(finbHandler); // adds action listener to the button to call the finbHandler
		
		newButton			= new JButton("New Order"); // instantiate newButton with text
		newbHandler			= new NewButtonHandler(); // instantiate the newbHandler to the NewButtonHandler method
		newButton.addActionListener(newbHandler); // adds action listener to the button to call the newbHandler
		
		exitButton			= new JButton("Exit"); // instantiate exitButton with text
		exitbHandler 		= new ExitButtonHandler(); // instantiate the exitbHandler to the ExitButtonHandler method
		exitButton.addActionListener(exitbHandler); // adds action listener to the button to call the exitbHandler
		
		// initial visibility/accessibility settings for buttons, fields
		confirmButton.setEnabled			(false); // disable confirmButton until processing complete
		viewButton.setEnabled				(false); // disable viewButton until processing is complete
		finishButton.setEnabled				(false); // disable finish until itme has been confirmed
		itemTextField.setEditable			(false); // field should not be editable
		totalTextField.setEditable			(false); // field should not be editable
		blankTextField.setEditable			(false); // field should not be editable
		blankTextField.setBackground		(Color.BLACK); // sets the blankTextField background to black
		blankTextField.setVisible			(false); // sets the blankTextField as invisible
		
		// set the layouts for fields and buttons
		Container pane = getContentPane(); // create the content pane for the frame
		GridLayout grid6by2 = new GridLayout(6,2,8,2); // 6-row, 2-column grid layout with a horizontal gap of 8 and a vertical gap of 2
		GridLayout grid4by2 = new GridLayout(4,2,8,4);  // 4 row, 2-column grid layout with a horizontal gap of 8 and a vertical gap of 4
		
		// create panels
		JPanel northPanel = new JPanel(); // creates the north panel
		JPanel southPanel = new JPanel(); // creates the south panel
		
		// set layouts for panel
		northPanel.setLayout(grid6by2); // sets the north panel layout to the 6 x 2 grid
		southPanel.setLayout(grid4by2); // sets the south panel layout to the 4 x 2 grid
		
		// add labels to northpanel
		northPanel.add(blankLabel				); // adds the blankLabel to the north panel
		northPanel.add(blankTextField			); // adds the blankTextField to the north panel

		idLabel.setForeground(Color.YELLOW		); // sets the idLabel text to yellow
		northPanel.add(idLabel		 	 		); // adds the idLabel to the north panel
		northPanel.add(idTextField	 			); // adds the idTextField to the north panel

		qtyLabel.setForeground(Color.YELLOW		); // sets the qtyLabel text to yellow
		northPanel.add(qtyLabel		 			); // adds the qtyLabel to the north panel
		northPanel.add(qtyTextField	 			); // adds the qtyTextField to the north panel

		itemLabel.setForeground(Color.YELLOW	); // sets the itemLabel text to yellow
		northPanel.add(itemLabel	 			); // adds the itemLabel to the north panel
		northPanel.add(itemTextField 			); // adds the itemTextField to the north panel

		totalLabel.setForeground(Color.YELLOW	); // sets the totalLabel text to yellow
		northPanel.add(totalLabel	 			); // adds the totalLabel to the north panel
		northPanel.add(totalTextField			); // adds the totalTextField to the north panel
		
		// add buttons to southpanel
		southPanel.add(processButton 	); // process button
		southPanel.add(confirmButton 	); // confirm button
		southPanel.add(viewButton		); // view button
		southPanel.add(finishButton  	); // finish button
		southPanel.add(newButton		); // new button
		southPanel.add(exitButton		); // exit button
		
		// add panels to content pane using BorderLayout
		pane.add(northPanel, BorderLayout.NORTH); // adds north panel to the North of the pane
		pane.add(southPanel, BorderLayout.SOUTH); // adds south panel to the South of the pane
		
		// call method to center frame on screen
		centerFrame(WIDTH, HEIGHT); 

		// style panels and content pane
		pane.setBackground(Color.BLACK); // set background of the content pane to black
		northPanel.setBackground(Color.BLACK); // set background of northpanel to black
		northPanel.setForeground(Color.YELLOW); // set text yellow
		southPanel.setBackground(Color.BLUE); // set background of southpanel to blue
	}// end constructor 
// ************************************************************************
	public void centerFrame(int frameWidth, int frameHeight) {
		// create a Toolkit Object
		Toolkit aToolkit = Toolkit.getDefaultToolkit();
		
		// create a Dimension object with user screen information
		Dimension screen = aToolkit.getScreenSize();
		
		// assign x, y position of upper-left corner of frame
		int xPositionOfFrame = (screen.width - frameWidth) / 2; // assign the x position of the frame to the (screenwidth - framewidth) / 2
		int yPositionOfFrame = (screen.height - frameHeight) / 2; // assign the y position of the frame to the (screenheight - frameheight) / 2
		
		// method to center frame on user's screen
		setBounds(xPositionOfFrame, yPositionOfFrame, frameWidth, frameHeight);
	} // end method
//************************************************************************
	private class ProcessButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// define variables
			String outputMessage, priceData, itemIDFromFile, itemInStockFromFile; // defines all strings
			boolean foundID=false, isNumItemsOK=false, isitemQtyOK=false, isitemInStock=true; // defines all booleans
			double priceMultiQty = 0; // defines all doubles
		
			//define file, file readers, and buffered readers, plus a scanner object
			File priceFile                = new File("inventory.txt"); // set priceFile to inventory.txt
			FileReader priceFileReader    = null; // defines FileReader as priceFileReader and sets to null
			BufferedReader priceBufReader = null; // defines BufferedReader priceBufReader and sets to null
			Scanner aScanner              = null; // defines Scanner aScanner and sets to null
		
			try {
				maxArraySize=10; // set the maxArraySize to 10
				
				// check to make sure we have a maxArraySize more than 0
				if(maxArraySize > 0) // if maxArraySize is more than 0
					isNumItemsOK = true; // set isNumItemsOK to true since we have enough items
				
				// assign itemQuantityStr and itemQuantity
				itemQuantityStr = qtyTextField.getText(); // pull the value from the qtyTextField and set it to itemQuantityStr
				itemQuantity = Integer.parseInt(itemQuantityStr); // turn the itemQuantityStr to an integer and assign it to itemQuantity
				
				// check itemQuantity to make sure we have a valid quantity 
				if(itemQuantity > 0) // if itemQuantity is more than 0
					isitemQtyOK = true; // set isItemQtyOK to true since we have a valid quantity
				
				// check to see if itemCount is zero. If it is, declare the shopping cart arrays
				if (itemCount == 0) { // if itemCount equals 0
					// declare arrays with maxArraySize
					itemIDArray		  = new String  [maxArraySize]; // itemIDArray holds all the transaction ID's
					itemTitleArray 	  = new String  [maxArraySize]; // itemTitleArray holds all the transaction titles
					itemInStockArray  = new String  [maxArraySize]; // itemInStoclArray holds all the itransaction nStock boolean values
					itemPriceArray	  = new double  [maxArraySize]; // itemPriceArray holds all the transaction prices
					itemQuantityArray = new int		[maxArraySize]; // itemQuantityArray holds all the transaction quantities 
					itemDiscountArray = new double  [maxArraySize]; // itemDiscountArray holds all the transaction discounts
					itemSubtotalArray = new double  [maxArraySize]; // itemSubtotalArray holds all the transaction subtotals
				} // end if
				
				// assign itemID
				itemID          = idTextField.getText(); // pull the value from the qtyTextField and set it to itemQuantityStr
				
				// Assign priceFileReader, priceBufReader, and priceData
				priceFileReader	= new FileReader(priceFile); // set the priceFileReader to read the priceFile (inventory.txt)
				priceBufReader  = new BufferedReader(priceFileReader); // set the priceBufReader to use the priceFileReader value
				priceData		= priceBufReader.readLine(); // read from the priceFile using the priceBufReader and assign it to priceData
			
				// while loop that runs when priceData isn't null and when isItemInStock is true
				whileloop:while(priceData != null && isitemInStock) {
					aScanner = new Scanner(priceData).useDelimiter("\\s*,\\s*"); // scans in data from the priceData and uses the designated delimiter pattern
					itemIDFromFile = aScanner.next(); // assigns the itemIDFromFile based on the next item the scanner finds in file
					
					// check to see if the itemID from the user matches the itemIDFromFile
					if (itemID.equals(itemIDFromFile)) {
						foundID = true; // set foundID to true since we have found a match
						itemTitle = aScanner.next(); // proceed to assign the itemTitle that matches the itemID using the scanner.next feature
						itemInStockFromFile = aScanner.next(); // proceed to assign the itemInStockFromFile that matches the itemID using the scanner.next feature
						
						// check to see if the itemInStockFromFile is false, then we need to set the isItemInStock to false to generate error message
						if (itemInStockFromFile.equals("false"))
							isitemInStock = false; // set isItemInStock to false since the item is not in stock
						
						itemPrice = aScanner.nextDouble(); // proceed to assign the itemPrice that matches the itemID using the scanner.nextDouble feature
						
						break whileloop; // break loop since we have found all the information we need
					} // end if
					priceData = priceBufReader.readLine(); // read next line from file
				} // end while
				
				// if statement to generate errors based on if foundID or isNumItemsOK or isitemQtyOK or isitemInStock false
				if (foundID == false || isNumItemsOK == false || isitemQtyOK == false || isitemInStock == false) {
					if (foundID == false) { // if foundID is false
						outputMessage = "item ID " + itemID + " not in file"; // build outputMessage
						JOptionPane.showMessageDialog(null, outputMessage, "Nile Dot Com - ERROR", JOptionPane.ERROR_MESSAGE); // create ERROR_MESSAGE JOptionPane
						idTextField.setText(""); // reset the idTextField to be empty
						qtyTextField.setText(""); // resest the qtyTextField to be empty
					} // end if
					// if isNumItemsOK or isitemQtyOK is false
					if (isNumItemsOK == false || isitemQtyOK == false) { 
						outputMessage = "Please enter a positive number for quantity of items."; // build outputMessage
						JOptionPane.showMessageDialog(null, outputMessage, "Nile Dot Com - ERROR", JOptionPane.ERROR_MESSAGE); // create ERROR_MESSAGE JOptionPane
					} // end if
					// if isitemInStock is false
					if (isitemInStock == false) {
						outputMessage = "Sorry ... that item is out of stock, please try another item."; // build outputMessage
						JOptionPane.showMessageDialog(null, outputMessage, "Nile Dot Com - ERROR", JOptionPane.ERROR_MESSAGE); // create ERROR_MESSAGE JOptionPane
						idTextField.setText(""); // reset the idTextField to be empty
						qtyTextField.setText(""); // resest the qtyTextField to be empty
						isitemInStock = true; // reset isitemInStock to true
					} // end if
				} else { // if no error messages occur, do these actions ...
					// determine quantity discount
					if (itemQuantity >= 5 && itemQuantity < 10) // if itemQuantity is greater than or equal to 5 but less than 10
						itemDiscount = DISCOUNT_FOR_05; // assign itemDiscount with the provided discount rate
					if (itemQuantity >=10 && itemQuantity < 15) // if itemQuantity is greater than or equal to 10 but less than 15
						itemDiscount = DISCOUNT_FOR_10; // assign itemDiscount with the provided discount rate
					if (itemQuantity >= 15) // // if itemQuantity is greater than or equal to 15
						itemDiscount = DISCOUNT_FOR_15; // assign itemDiscount with the provided discount rate
					
					// set subtotals
					priceMultiQty = itemPrice * itemQuantity; // assign itemMultQty with the itemPrice x itemQuantity
					itemSubTotal  = priceMultiQty * itemDiscount; // assign itemSubTotal with the priceMultiQty x itemDiscount
					itemSubTotal  = priceMultiQty - itemSubTotal; // assign the itemSubTotal with priceMultiQty - itemSubTotal
					// orderSubTotal = orderSubTotal + itemSubTotal; //assign the orderSubTotal with orderSubTotal - itemSubTotal
					
					// set curency format and percentage formats for output strings
					itemPriceStr     = currencyFormatter.format(itemPrice); // assign and format itemPriceStr to make itemPrice matches the designated format for currency
					itemDiscountStr  = percentFormatter.format(itemDiscount); // assign and set the itemDiscountStr to make sure itemDiscount matches the designated format for percents
					itemSubtotalStr  = currencyFormatter.format(itemSubTotal); // assign and set the itemSubtotalStr to make sure itemSubTotal matches the designated format for currency
					orderSubTotalStr = currencyFormatter.format(orderSubTotal); // assign and set the orderSubTotalStr to make sure orderSubTotal matches the designated format for currency
	
					// build output message string
					outputStr = itemID + " " + itemTitle + " " + itemPriceStr + " " + itemQuantityStr + " " + itemDiscountStr + " "+ itemSubtotalStr;
				
					// update labels - increment item #
					itemLabel.setText("Details for Item #" + (itemCount+1) + ":");
					
					// set editable fields and disable process button, enable confirm button.
					processButton.setEnabled(false); // disable the processButton
					confirmButton.setEnabled(true); // enable the confirm button
					idTextField.setEditable(false); // disable editing of idTextField
					qtyTextField.setEditable(false); // disable editing of qtyTextField
			
					// send outputStr to itemTextField to show user what was selected from the inventory file
					itemTextField.setText(outputStr);
				}// end else
			} // end try
			// output error if the file is not found
			catch(FileNotFoundException fileNotFoundException) { 
				JOptionPane.showMessageDialog(null, "Error: File not found", "Nile Dot Com - ERROR", JOptionPane.ERROR_MESSAGE); // create ERROR_MESSAGE JOptionPane
			} // end catch
			// output error if there is a problem reading from the file
			catch(IOException IOException){
				JOptionPane.showMessageDialog(null, "Error: Problem reading from file", "Nile Dot Com - ERROR", JOptionPane.ERROR_MESSAGE); // create ERROR_MESSAGE JOptionPane
			} // end catch
			// output error if there is an invalid input
			catch(NumberFormatException numberFormatException) {
				JOptionPane.showMessageDialog(null,  "Error: Invalid input for number of line items or quantity of items", "Nile Dot Com - ERROR", JOptionPane.ERROR_MESSAGE); // create ERROR_MESSAGE JOptionPane
			} // end catch
		} // end method
	} // end class	
//************************************************************************
	private class ConfirmButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// Calculate orderSubTotal
			orderSubTotal = orderSubTotal + itemSubTotal; //assign the orderSubTotal with orderSubTotal - itemSubTotal
			orderSubTotalStr = currencyFormatter.format(orderSubTotal); // assign and set the orderSubTotalStr to make sure orderSubTotal matches the designated format for currency

			// set array values with item from inventory file user has confirmed - added to cart
			// create the itemIDArray with index of itemCount and assign it to the current itemID
			itemIDArray[itemCount]       = itemID;
			
			// create the itemTitleArray with index of itemCount and assign it to the current itemTitle
			itemTitleArray[itemCount]    = itemTitle;
			
			// create the itemInStockArray with index of itemCount and assign it to the current itenInStock 
			itemInStockArray[itemCount]  = itemInStock; 
			
			// create the itemPriceArray with index of itemCount and assign it to the current itemPrice
			itemPriceArray[itemCount]    = itemPrice;
			
			// create the itemQuantityArray with index of itemCount and assign it to the current itemQuantity
			itemQuantityArray[itemCount] = itemQuantity;
			
			// create the itemDiscountArray with index of itemCount and assign it to the current itemDiscount
			itemDiscountArray[itemCount] = itemDiscount;
			
			// create the itemSubtotalArray with index of itemCount and assign it to the cufrent itemSubTotal
			itemSubtotalArray[itemCount] = itemSubTotal;
			
			// increment item count
			itemCount++;
			
			// dump item confirmed message
			JOptionPane.showMessageDialog(null, "Item #" + itemCount + " accepted. Added to your cart.", "Nile Dot Com - Item Confirmed", JOptionPane.INFORMATION_MESSAGE); // create INFORMATION_MESSAGE JOptionPane
			
			// reset buttons
			processButton.setEnabled(true ); // enable the processButton
			confirmButton.setEnabled(false); // disable the confirmButton
			viewButton.setEnabled(true    ); // enable the viewButton
			finishButton.setEnabled(true	 ); // enable the finishButton
			
			// reset labels
			idLabel.setText("Enter item ID for Item #" + (itemCount+1) + ":"  ); // reset the idLabel with new itemCount
			qtyLabel.setText("Enter quantity for Item #"+ (itemCount+1) + ":" ); // rset the qtyLabel with new itemCount
			totalLabel.setText("Order Subtotal for " + itemCount + " item(s):"); // reset the setText with new itemCount
			
			// rest text fields
			idTextField.setEditable(true			); // enable editing of idTextField
			qtyTextField.setEditable(true		); // enable editing of qtyTextField 
			idTextField.setText("" 				); // reset the idTextField to blank text
			qtyTextField.setText(""				); // reset the qtyTextField to blank text
			totalTextField.setText(orderSubTotalStr ); // reset the totalTextField to the value of the orderSubTotalStr
			
			// set new button text
			processButton.setText("Process Item #" + (itemCount+1)); // reset the processButton with new itemCount
			confirmButton.setText("Confirm Item #" + (itemCount+1)); // reset the confirmButton with new itemCount
		} // end method
	} // end class
//************************************************************************
	private class ViewButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// declare outPutMessage variable
			String outPutMessage = "";
			
			// iterate thru current arrays and dump view of shopping cart
			for (int i=0;i<itemCount;i++) { // for loop that runs until i is equal to itemCount, increments by 1
				outPutMessage += (i+1) + ". " + itemIDArray[i] + " " + itemTitleArray[i] + " " + currencyFormatter.format(itemPriceArray[i]) + 
					" " + itemQuantityArray[i] + " " + percentFormatter.format(itemDiscountArray[i]) + " " + 
					currencyFormatter.format(itemSubtotalArray[i]) + "\n"; // build the outPutMessage
			}	
			JOptionPane.showMessageDialog(null, outPutMessage, "Nile Dot Com - Current Shopping Cart Status", JOptionPane.INFORMATION_MESSAGE); // create INFORMATION_MESSAGE JOptionPane
		} // end method
	} // end class
//************************************************************************
	private class FinishButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// declare variables
			String totalMessage 	= ""						; // totalMessage will contain the string that is put in the invoice message
			String userFormat 		= "MM/dd/yy, hh:mm:ss aa z" ; // userFormat is the time-format utilized in the invoice message
			String fileFormat		= "ddMMyyyyHHmm"			; // fileFormat is the time-format utilized in the transactions file
			String userTimeStamp 	= ""						; // userTimeStamp will contain the string with the current timestamp that is put in the invoice message 
			String fileTimeStamp 	= ""						; // fileTimeStamp will contain the string with the current timestamp that is put in the transaction message
			StringBuilder sb 		= new StringBuilder()		; // sb is the variable that contains the StringBuilder object
			
			// generate timestamp using the userFormat to ensure date matches criteria
			userTimeStamp = new SimpleDateFormat(userFormat).format(new java.util.Date());
			
			try {
				// define and assign the FileWriter to a varialbe set to transactions.txt
				FileWriter write = new FileWriter("transactions.txt", true); // assign the variable and ensure that FileWriter is set to true to ensure it appends the file
			
				// calculate total and format to two decimals
				orderTaxAmount = orderSubTotal * TAX_RATE; // assign orderTaxAmount with orderSubTotal x TAX_RATE
				orderTotal = orderTaxAmount + orderSubTotal; // assign orderTotal with orderTaxAmount + orderSubTotal
				
				// generate timestamp using fileFormat to ensure data matches criteria
				fileTimeStamp = new SimpleDateFormat(fileFormat).format(new java.util.Date());
			
				// totalMessage header for invoice message
				totalMessage = "Date: " + userTimeStamp + "\n\nNumber of line items: " + itemCount + "\n\nItem# / ID / Title / Price / Qty / Disc % / Subtotal:\n"; // builds the totalMessage header
			
				// iterate thru shopping cart items and add to invoice message
				for (int i=0;i<itemCount;i++) { // for loop that runs until i is equal to itemCount, increments by 1
					totalMessage += "\n" + (i+1) + ". " + itemIDArray[i] + ", " + itemTitleArray[i] + ", " + currencyFormatter.format(itemPriceArray[i]) + ", " 
						+ itemQuantityArray[i] + ", " + percentFormatter.format(itemDiscountArray[i]) + ", " + currencyFormatter.format(itemSubtotalArray[i]); // build total message body
					// append details to the stringBuilder object to be put into the transactions.txt
					sb.append(fileTimeStamp).append(" ").append(itemIDArray[i]).append(" ").append(itemTitleArray[i]).append(" ")
						.append(itemPriceArray[i]).append(" ").append(itemQuantityArray[i]).append(" ")
						.append(itemDiscountArray[i]).append(" ").append(currencyFormatter.format(itemSubtotalArray[i])).append(" ")
						.append(userTimeStamp).append("\n");
				} // end for loop
				// write string sb to the file using write.append
				write.append(sb);
				// close FileWrtier write
				write.close();
			} // end try
			// output error if there is a problem writing to file
			catch(IOException ioException) {
				JOptionPane.showMessageDialog(null, "Error: Problem writing to file", "Nile Dot Com - ERROR", JOptionPane.ERROR_MESSAGE); // create ERROR_MESSAGE JOptionPane
			} // end catch

			// build total message footer
			totalMessage += 
					"\n\nOrder Subtotal:	" + currencyFormatter.format(orderSubTotal) // add orderSubTotal with currency formatter
				+	"\n\nTax rate:			" + percentFormatter.format(TAX_RATE) // add TAX_RATE with percent formatter
				+	"\n\nTax amount: 		" + currencyFormatter.format(orderTaxAmount) // add orderTaxAmount with currency formatter
				+ 	"\n\nORDER TOTAL: 		" + currencyFormatter.format(orderTotal) // add orderTotal with currency formatter
				+	"\n\nThanks for shopping at Nile Dot Com!";
			
			// output the totalMessage 
			JOptionPane.showMessageDialog(null, totalMessage, "Nile Dot Com - FINAL INVOICE", JOptionPane.INFORMATION_MESSAGE); // create INFORMATION_MESSAGE JOptionPane
			
			// reset button
			finishButton.setEnabled(false ); // disable finishButton
			processButton.setEnabled(false); // disable processButton
			confirmButton.setEnabled(false); // disable confirmButton 

			// rest text fields
			idTextField.setEditable(false ); // disable editing of idTextField
			qtyTextField.setEditable(false); // diable editing of qtyTextField
		} // end method
	}// end class
//************************************************************************
	private class NewButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// reset arrays
			itemIDArray		  = new String  [maxArraySize];
			itemTitleArray 	  = new String  [maxArraySize];
			itemInStockArray  = new String  [maxArraySize];
			itemPriceArray	  = new double  [maxArraySize];
			itemQuantityArray = new int		[maxArraySize];
			itemDiscountArray = new double  [maxArraySize];
			itemSubtotalArray = new double  [maxArraySize];
			
			// reset variables
			itemID			 = "";
			itemTitle		 = "";
			outputStr		 = "";
			maxArraySizeStr	 = ""; 
			itemPriceStr	 = "";
			itemInStock		 = "";
			itemQuantityStr	 = "";
			itemSubtotalStr	 = "";
			itemDiscountStr  = "";
			taxRateStr		 = "";
			discountRateStr  = "";
			orderSubTotalStr = "";

			itemPrice		= 0;
			itemSubTotal	= 0;
			orderSubTotal	= 0;
			orderTotal		= 0;
			itemDiscount	= 0;
			orderTaxAmount	= 0;
	
			itemQuantity 	= 0;
			itemCount    	= 0;
			maxArraySize 	= 0;

			// reset buttons
			processButton.setEnabled(true);
			viewButton.setEnabled(false	);
			idTextField.setEditable(true	); 
			qtyTextField.setEditable(true);
			processButton.setText("Process Item #" + (itemCount+1));
			confirmButton.setText("Confirm Item #" + (itemCount+1));

			// reset textFields
			idTextField.setText(""		);
			qtyTextField.setText(""		);
			itemTextField.setText(""		);
			totalTextField.setText(""	);

			// reset labels
			idLabel.setText("Enter item ID for Item #" + (itemCount+1) + ":"	);
			qtyLabel.setText("Enter quantity for Item #"+ (itemCount+1) + ":"	);
			itemLabel.setText("Details for Item #" + (itemCount+1) + ":"		);
			totalLabel.setText("Order Subtotal for " + itemCount + " item(s):"	);
		} // end method
	} // end class
//************************************************************************
	private class ExitButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.exit(0); // exit with status code 0
		} // end method
	} // end class
//************************************************************************
	public static void main(String [] args) {
		JFrame aNewStore = new NileDotCom(); // create the frame object
		aNewStore.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		aNewStore.setVisible(true); // display the frame
	} // end main
} // end class