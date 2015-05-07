

import com.mongodb.Mongo;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import com.mongodb.ServerAddress;
import com.mongodb.WriteResult;

import java.util.Arrays;
import java.util.Set;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * 
 * MongoTest Class used as main class.
 *
 */
public class MongoTest implements ActionListener {
	String userinput;
	JFrame frame;
	JLabel userlabel, countlabel, listUsers, lookText;
	JTextField usertext, counttext, listUsersText;
	JButton searchbutton;
	JTextArea userArea;
	JScrollPane scroll;
	JComboBox combo;
	JPanel panel1, panel2;
	JMenuBar mbar;
	JMenu File, Help;
	JMenuItem Search, Exit, About;
	Mongo client = null;
	DBCollection collection = null;

	/**
	 * Front-end
	 */
	public void awtsrch() {
		frame = new JFrame("SEARCH ENGINE");
		frame.setLayout(new BorderLayout());
		frame.setLocation(520, 300);

		/* Menu bar for GUI */

		mbar = new JMenuBar();
		File = new JMenu("File");
		frame.setJMenuBar(mbar);
		mbar.add(File);
		Help = new JMenu("Help");
		frame.setJMenuBar(mbar);
		mbar.add(Help);
		Search = new JMenuItem("Search"); // Adding search menu item inside the
											// File menu
		File.add(Search);
		Exit = new JMenuItem("Exit");
		File.add(Exit);
		About = new JMenuItem("About");
		Help.add(About);

		userlabel = new JLabel("Enter Word: ");
		usertext = new JTextField(10);
		countlabel = new JLabel("Count:");
		counttext = new JTextField(10);
		listUsers = new JLabel("List of Users:");
		listUsersText = new JTextField(30);
		searchbutton = new JButton("Search");
		userArea = new JTextArea(10, 50);
		scroll = new JScrollPane(userArea);
		userArea.setLineWrap(true);
		userArea.setWrapStyleWord(true);

		lookText = new JLabel("Here is the text that contains your search:\n");
		combo = new JComboBox();

		// Adding the components to the panels

		panel1 = new JPanel();
		panel1.add(userlabel);
		panel1.add(usertext);
		panel1.add(searchbutton);
		panel1.add(countlabel);
		panel1.add(counttext);
		panel1.add(combo);

		panel2 = new JPanel();
		panel2.add(lookText);
		panel2.add(scroll);

		// Adding panels to the frame
		frame.add(panel1, BorderLayout.NORTH);
		frame.add(panel2, BorderLayout.CENTER);

		/*
		 * adding action listener for the components so that when the component
		 * is clicked it performs some action
		 */

		searchbutton.addActionListener(this);
		Search.addActionListener(this);
		Exit.addActionListener(this);
		About.addActionListener(this);

		frame.setSize(800, 300);
		frame.setVisible(true);
		frame.setLocation(250, 250);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 *  Connection with Back-end i.e. MongoDb
	 */
	public void mongodb() {

		try {
			// connect to the MongoDB server
			client = new Mongo("localhost", 27017);
			System.out.println("\nConnection to server completed");

		} catch (Exception e) {
			System.out.println("Error on connection");
			System.out.println(e.getMessage());
			System.out.println(e.toString());
			e.printStackTrace();
		}

		// access the database
		DB sampleDB = client.getDB("SampleSocial");
		System.out.println("\nConnection to database completed");


		// get the collection
		try {
			collection = sampleDB.getCollection("Tweets");

			System.out.println("\nCollection of tweets obtainied");
		} catch (MongoException e) {
			System.out.println("Error on collection");
			System.out.println(e.getMessage());
			System.out.println(e.toString());
			e.printStackTrace();
		}

		// count the documents
		try {
			System.out.println("\n Count of users :" + collection.getCount());
		}

		catch (Exception e) {
		}

	}

	/**
	 * 
	 * @param args
	 * main call to 2 functions.
	 */
	public static void main(String[] args) {
		MongoTest m = new MongoTest();  //Creating object of main class MongoTest.
		m.mongodb();  //Call to database for connection.
		m.awtsrch();  //Call to front-end.

	} // main ends

	
	
	/**
	 * Action performed when the user clicks the search button or the search
	 * item from File menu
	 */
	public void actionPerformed(ActionEvent ae) {
		Object eobj = ae.getSource();
		// Action performed when the user clicks the search button or the search
		// item from File menu
		if (eobj == searchbutton || eobj == Search) {

			userinput = usertext.getText().trim();

			if (userinput.isEmpty()) {
				JOptionPane.showMessageDialog(frame,
						"Please enter a valid search criteria", "Search error",
						JOptionPane.ERROR_MESSAGE);
			} else if ((userinput.length() <= 2)) // insufficient characters to
													// search
			{
				JOptionPane.showMessageDialog(frame,
						"Enter minimum 3 characters !", "Search error",
						JOptionPane.ERROR_MESSAGE);

			} else {
				DBObject selection = new BasicDBObject("text",
						new BasicDBObject("$regex", ".*" + userinput + ".*")
						.append("$options", "i"));
				DBObject projection = new BasicDBObject("text", 1).append(
						"_id", 0).append("id", 1);
				DBCursor cursor = collection.find(selection, projection);
				Integer returnCount = cursor.size(); // gets the number of
														// instances of the word
														// being searched.

				DBObject projection2 = new BasicDBObject("fromUser", 1).append(
						"_id", 0);
				DBCursor cursor2 = collection.find(selection, projection2);

				combo.removeAllItems();
				combo.addItem("SELECT USER");

				try {

					while (cursor2.hasNext()) {
						DBObject outObj = cursor2.next();
						Object us = outObj.get("fromUser");

						System.out.println(us.toString());
						combo.addItem(us.toString());
					}
					combo.addActionListener(new MongoTest.comboListener());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					cursor2.close();
				}

				counttext.setText(returnCount.toString());

			}
		}
		// if the user selects exit menu item from File menu the window will
		// close.
		else if (eobj == Exit) {
			System.exit(0);
		}
		// if the user selects about menu item from Help, it displays about the
		// project
		else if (eobj == About) {
			
			JOptionPane.showMessageDialog(null,
					"This is a search engine project using MongoDB");
		}

	}

	/**
	 *Action performed when a User is selected from ComboBox 
	 * 
	 */
	public class comboListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JComboBox cb = (JComboBox) e.getSource();

			String uName = (String) (cb.getSelectedItem());
			DBObject selection3 = new BasicDBObject("fromUser", uName);
			DBObject projection3 = new BasicDBObject("text", 1)
					.append("_id", 0);
			DBCursor cursor3 = collection.find(selection3, projection3);
			Object uText = null;
			while (cursor3.hasNext()) {
				DBObject outObj = cursor3.next();
				uText = outObj.get("text");
				userArea.setEditable(false);
				userArea.setText(uText.toString());
				// To display in eclipse for cross verification.
				System.out.println("Text is:" + uText.toString());
			}

		}

	}// End of comboListener

}// End of MongoTest class

