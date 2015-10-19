import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

//see vid 41, 42, 43 again!! when revising
//this app goes on the server!!
public class IM_Server extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField userText;
	private JTextArea chatWindow; // Displaying the whole conversation
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;//Connection between the two computers
	
	public IM_Server(){
		super("Papa Kancha Instant Messenger");
		
		userText = new JTextField();
		//Just to prevent any malicious behaviour if we send across the stream with no one listening 
		userText.setEditable(false);
		userText.addActionListener(
					new ActionListener(){
						public void actionPerformed(ActionEvent event){
							sendMessage(event.getActionCommand());
							//When the message is sent we need the user txt box to be empty!! Imagine not doing this and ending up with the message still there in the text box
							userText.setText(" ");
							
						}
					}
				);
		add(userText, BorderLayout.NORTH);
		
		//the area where the text will be displayed!!
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(300,150);
		setVisible(true);
	}

	//Set up and running the server
	public void startRunning(){
		try{
			server = new ServerSocket(6789, 100);//this method accepts port number and a backlog(100 ppl queue, in this case)
		
				try{
					waitForConnection();
					setupStream();//sets input and output streams
					whileChatting();
					
				}catch(EOFException eofException){
					showMessage("\n Server ended the connection");
				}finally{
					//to close everything when we are done with it!!
					closeAll();
				}
			
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	//wait for connection, then display connection information
	private void waitForConnection()throws IOException{
		showMessage("waiting for someone to connect... \n");
		connection = server.accept();//Accepting the connection to the socket
		showMessage("Now connected to " +connection.getInetAddress().getHostName());
	}
	
	//Setting up Streams to send and receive data
	private void setupStream() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();//to flush the buffer
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Streams are now setup \n");
	}
	
	//during chat
	private void whileChatting() throws IOException{
		String message = "you are connected";
		sendMessage(message);
		ableToType(true);
		do{
			try{
				message = (String)input.readObject();//reading a message from the sender
				showMessage("\n" + message); 
				System.out.println(message);
			}catch(ClassNotFoundException classNotFoundException){
				showMessage("\n I don't know what the user sent \n");
			}
		}while(!message.equals("CLIENT - SERVER - END"));
	}
	
	//close streams and sockets when done
	private void closeAll(){
		showMessage("\n Closing connections... \n");
		ableToType(false);
		try{
			output.close();//Stream TO them closed
			input.close();//Stream FROM them closed
			connection.close();//Socket Closed
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	//send a message to the client
	private void sendMessage(String message){
		try{
			output.writeObject("SERVER - " + message);
			output.flush();
			showMessage("\n SERVER -" + message);
		}catch(IOException ioException){
			chatWindow.append("\n ERROR ERROR ERROR!!!! \n");
		}	
	}
	
	//Displaying messages in the chat window; updating chat window
	private void showMessage(final String text){
		//creating threads to update the gui
		SwingUtilities.invokeLater(
					new Runnable(){
						public void run(){
							chatWindow.append(text);
						}
					}
				);	
	}
	
	//let the user type stuff into their box
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

