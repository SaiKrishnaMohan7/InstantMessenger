import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class IM_Client extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField userText; //Area where text is entered
	private JTextArea chatWindow; //Area where text is shown
	private ObjectOutputStream output;//From CLIENT to SERVER
	private ObjectInputStream input;
	private String message = " ";
	private String serverIP;
	private Socket connection;
	
	//constructor
	//needs an IP Address of the server that we are connecting to
	//When hosting on own server, just change IP Address and port 
	public IM_Client(String host){
		super("Papa Kancha Client");
		serverIP = host;
		
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
					new ActionListener(){
						public void actionPerformed(ActionEvent e){
							sendMessage(e.getActionCommand());
							userText.setText(" ");
						}
					}
				);
		add(userText,BorderLayout.NORTH);
		
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(300, 150);
		setVisible(true);
	}
	
	//setting up...
	public void startRunning(){
		try{
			connectToServer();//we want to connect to the server, which is just ONE (read Unique)!!
			setupStream();
			whileChatting();
		}catch(EOFException eofException){
			showMessage("\n Client terminated the connecton");
		}catch(IOException ioException){
			ioException.printStackTrace();
		}finally{
			closeAll();
		}
	}
	
	//connect to server
	private void connectToServer() throws IOException{
		showMessage("Attempting connection...\n ");
		connection = new Socket(InetAddress.getByName(serverIP),6789);
		showMessage("Connected to: " + connection.getInetAddress().getHostName());
	}
	
	//setting up the streams
	private void setupStream()throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("Your streams are ready to carry your messages\n");
	}
	
	//while chatting with server
	private void whileChatting() throws IOException{
		ableToType(true);
		do{
			try{
				message = (String)input.readObject();
				showMessage("\n " + message);
			}catch(ClassNotFoundException classNotFoundException){
				showMessage("\n IDK the object type");
			}
		}while(!message.equals("SERVER - END"));
	}
	
	//close all streams and sockets 
	private void closeAll(){
		showMessage("\n closing connections down");
		ableToType(false);
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	//send messages to server
	private void sendMessage(String Message ){
		try{
			output.writeObject("CLIENT - "+ message);
			output.flush();
			showMessage("\n CLIENT - "+ message);
		}catch(IOException ioException){
			chatWindow.append("\n something didn't go right");
		}
	}
	
	//updating the chat window
	private void showMessage(final String m){
		SwingUtilities.invokeLater(
					new Runnable(){
						public void run(){
							chatWindow.append(m);
						}
					}
				);
	}
	
	//gives permission to type
	private void ableToType(final boolean tof){
		SwingUtilities.invokeLater(
					new Runnable(){
						public void run(){
							userText.setEditable(tof);
					}
				}
			);
	}
}
