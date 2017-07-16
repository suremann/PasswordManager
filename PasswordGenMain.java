package passwordmanager;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class PasswordGenMain extends JFrame {

	private static final long serialVersionUID = 1L;
	private String password;
	private int length = 0;
	private JPanel mainPanel;
	private JFrame mainFrame;
	private int selected_row = -1;
	private String version = "v0.3";
	

	public static void main(String[] args) {
		new PasswordGenMain();
	}
	
	public PasswordGenMain(){
		
		//Get the home directory of machine. Create folder for files.
		String workingDir = System.getProperty("user.home")+"/.Encrypted/";
		try {
			Files.createDirectories(Paths.get(workingDir));
		} catch (IOException e4) {
			e4.printStackTrace();
		}
		
		mainFrame = new JFrame();
		mainFrame.setSize(440,365);
		
		PasswordGenerator pg = new PasswordGenerator();
		ArrayList<String> characters = new ArrayList<String>();
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();
	
		mainFrame.setLocation((dim.width/2) - (mainFrame.getWidth()/2), (dim.height/2) - (mainFrame.getHeight()/2));
		mainFrame.setResizable(false);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setTitle("Password Generator");
		
		mainPanel = new JPanel();
		
		mainPanel.setBorder(new TitledBorder(new EtchedBorder(),"Password Generator " + version));
		
		//Check boxes to determine what type of characters to be in password
		JCheckBox upperBox = new JCheckBox("Upper Case Alphabet");
		JCheckBox lowerBox = new JCheckBox("Lower Case Alphabet");
		JCheckBox digitBox = new JCheckBox("Digits 0-9");
		JCheckBox specialBox = new JCheckBox("Special Characters ");
		JCheckBox customBox = new JCheckBox("Custom (Do not separate characters)");
		mainPanel.add(upperBox);
		mainPanel.add(lowerBox);
		mainPanel.add(digitBox);
		mainPanel.add(specialBox);
		mainPanel.add(customBox);
		
		//Label and text field for custom characters
		JLabel customLabel = new JLabel("Enter Custom:");
		JTextField customChars = new JTextField(29);
		mainPanel.add(customLabel);
		mainPanel.add(customChars);
		
		//Label and text field for desired password length
		JLabel sizeLabel = new JLabel("Enter Password Length:");
		JTextField sizeIn = new JTextField(24);
		mainPanel.add(sizeLabel);
		mainPanel.add(sizeIn);
		
		//Button to generate new password based on specified criteria
		JButton generate = new JButton("Generate Password");
		mainPanel.add(generate);
		
		//Text area to display the generated password
		JTextArea output = new JTextArea(8,37);
		output.setLineWrap(true);
		output.setEditable(false);
		JScrollPane scroll = new JScrollPane(output);
		mainPanel.add(scroll);
		
		//Clipboard used to copy the password to when copy button is pressed
		Clipboard cb = tk.getSystemClipboard();
		JButton copy = new JButton("Copy Password");
		copy.setToolTipText("Copy the generated password to the clipboard");
		mainPanel.add(copy);
		
		//Button that opens the save screen for the generated password
		JButton save = new JButton("Save Password");
		save.setToolTipText("Save and encrypt generated password with relevant information");
		mainPanel.add(save);
		
		//Press to open list of passwords arranged by their titles(all are encrypted).
		JButton view = new JButton("View Passwords");
		view.setToolTipText("View list of saved passwords");
		mainPanel.add(view);
		
		/**
		 * Parent frame : mainFrame
		 * Generate a new password
		 */
		generate.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				characters.clear();
				
				if(upperBox.isSelected()) characters.add(pg.upper_alpha);
				
				if(lowerBox.isSelected()) characters.add(pg.lower_alpha);
				
				if(digitBox.isSelected()) characters.add(pg.digits);
				
				if(specialBox.isSelected()) characters.add(pg.specials);
				
				if(customBox.isSelected()){
					if(customChars.getText().length() > 0){
						characters.add(customChars.getText());
					}
				}
				if(sizeIn.getText().length() > 0){
					length = Integer.valueOf(sizeIn.getText());
				}
				password = pg.generate_password(length, characters);
				output.setText(password);
			}	
		});
		
		/**
		 * Parent Frame : mainFrame
		 * Copy the generated password
		 */
		copy.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				StringSelection s = new StringSelection(password);
				cb.setContents(s, null);
			}
		});
		
		/**
		 * Parent Frame : mainFrame
		 * SAVE GENERATED PASSWORD
		 * Open the save form for the user to save the generated
		 * password with any details they wish to enter
		 */
		save.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JFrame saveFrame = new JFrame();
				saveFrame.setSize(500, 400);
				
				int x = Math.max(0, mainFrame.getLocationOnScreen().x - ((500 - mainFrame.getWidth())/2));
				int y = Math.max(0, mainFrame.getLocationOnScreen().y - ((400 - mainFrame.getHeight())/2));
				
				saveFrame.setLocation(x,y);
				saveFrame.setResizable(false);
				saveFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				saveFrame.setTitle("Save Form");
				
				JPanel savePanel = new JPanel();
				savePanel.setBorder(new TitledBorder(new EtchedBorder(),"Password Information Form " + version));
	
				//Label and text area displaying the generated password the user chose to save
				JLabel generated = new JLabel("Generated Password:");
				savePanel.add(generated);
				JTextArea generated_pass = new JTextArea(5,42);
				generated_pass.setText(password);
				generated_pass.setLineWrap(true);
				generated_pass.setEditable(false);
				JScrollPane gen_scroll = new JScrollPane(generated_pass);
				savePanel.add(gen_scroll);
				
				//Label and text field for the title the user wishes to give this password
				JLabel titleLabel = new JLabel("Provide a title for this password. This will be the file name. (Required):");
				savePanel.add(titleLabel);
				JTextField inTitle = new JTextField(42);
				savePanel.add(inTitle);
				
				//Label and text area for the user to enter in any details about the password or whatever
				JLabel descLabel = new JLabel("Provide a description for the generated password. (Optional):");
				savePanel.add(descLabel);
				JTextArea desc = new JTextArea(5,42);
				desc.setLineWrap(true);
				JScrollPane descScroll = new JScrollPane(desc);
				savePanel.add(descScroll);
				
				//Label and text field for the user specified, 16 character encryption key that is used to save file.
				JLabel encrypt_key = new JLabel("Enter a 16 character encryption key to secure file. (*** DO NOT LOSE THIS ***):");
				savePanel.add(encrypt_key);
				JTextField e_key = new JTextField(30);
				savePanel.add(e_key);
				JButton lazyKey = new JButton("Generate Key");
				savePanel.add(lazyKey);
				
				//Button to indicate that details are done being entered and the file is ready to encrypt
				JButton encrypt = new JButton("Save and Encrypt");
				encrypt.setToolTipText("Save and encrypt password file using encryption key");
				savePanel.add(encrypt);
				
				
				lazyKey.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent arg0) {
						ArrayList<String> cList = new ArrayList<String>();
						cList.add(pg.digits);
						cList.add(pg.specials);
						e_key.setText(pg.generate_password(16, cList));
					}
				});
				
				/**
				 * Parent frame : saveFrame
				 * Use the "CyrptoUtils" to encrypt file with the given key
				 * provided that the key is 16 characters and there exists a title.
				 */
				encrypt.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						String key = e_key.getText();
						String title = inTitle.getText();
						String description = desc.getText();
						String pword = generated_pass.getText();
						
						if(key.length() == 16 && title.length() > 0){
							try {
								Files.createDirectories(Paths.get(workingDir));
								File f = new File(workingDir+"PW_"+title+".txt");
								FileWriter fw = new FileWriter(f);
								BufferedWriter bw = new BufferedWriter(fw);
								
								bw.write("START PASSWORD");
								bw.newLine();
								bw.write(pword);
								bw.newLine();
								bw.write("END PASSWORD");
								bw.newLine();
								bw.write("START DESC");
								bw.newLine();
								bw.write(description);
								bw.newLine();
								bw.write("END DESC");
		
								bw.close();
								fw.close();
								
								
								File encrypted_file = new File(workingDir+"PW_"+title+".encrypted");
								try {
									CryptoUtils.encrypt(key, f, encrypted_file);
								} catch (CryptoException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								f.delete();
								saveFrame.dispose();
							
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
						key = pword = "";
					}
				});
				
				saveFrame.add(savePanel);
				saveFrame.setVisible(true);
			}
		});
	
		
		/**
		 * Parent Frame : mainFrame
		 * VIEW PASSWORD FILES
		 * Open a table that contains the titles of the encrypted
		 * password files on this machine in the working directory
		 */
		view.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JFrame viewFrame = new JFrame();
				viewFrame.setSize(260, 365);
			
				int x = Math.max(0, mainFrame.getLocationOnScreen().x - ((260 - mainFrame.getWidth())/2));
				int y = Math.max(0, mainFrame.getLocationOnScreen().y - ((365 - mainFrame.getHeight())/2));
				
				viewFrame.setLocation(x,y);
				viewFrame.setResizable(false);
				viewFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				viewFrame.setTitle("Saved Passwords");
				
				JPanel viewPanel = new JPanel();
				viewPanel.setBorder(new TitledBorder(new EtchedBorder(),"Saved Passwords " + version));
	
				//If for some reason the working directory no longer exists, recreate it
				try {
					Files.createDirectories(Paths.get(workingDir));
				} catch (IOException e3) { e3.printStackTrace(); }
				
				File dir = new File(workingDir);
				ArrayList<File> l = new ArrayList<File>();
				//Look at all files in the working directory. Add to list of files in they end with ".encrypted"
				for(File f : dir.listFiles()){
					if(f.getName().substring(f.getName().length()-10, f.getName().length()).equals(".encrypted")){
						l.add(f);
					}
				}
				
				//Used to populate JTabel
				Object[] col_title = {"Password Title"};
				Object[][] saved_files = new Object[l.size()][1];
				int i=0;
				for(File f : l){
					saved_files[i][0] = f.getName().substring(3, f.getName().length()-10);
					i++;	
				}
				
				//A JTable filled with the names of encrypted files
				JTable files = new JTable(saved_files,col_title);
				files.setPreferredScrollableViewportSize(new Dimension(200, 250));
				JScrollPane scrollPane = new JScrollPane(files);
				files.setFillsViewportHeight(true);
				viewPanel.add(scrollPane);
				
				//Button that, when clicked, prompts user for selected files password
				//If password correct, displays the password and info.
				JButton vw = new JButton("View");
				vw.setToolTipText("View or edit selected password file");
				viewPanel.add(vw);
	
				//Button that allows the user to add a new password without generating a new one
				JButton add = new JButton("Add");
				add.setToolTipText("Add a custom password");
				viewPanel.add(add);
				
				
				//Button that, when clicked, prompts user for selected files password
				//If password correct, that file is deleted
				JButton delete = new JButton("Delete");
				delete.setToolTipText("Delete selected password file");
				viewPanel.add(delete);
				
				/**
				 * Parent Frame : viewFrame
				 * DELETE PASSWORD FILE
				 * Open a prompt for the encyption key for the selected file
				 * If correct, delete that file and update the table
				 */
				delete.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						if(selected_row > -1){
							JFrame deleteFrame = new JFrame();
							deleteFrame.setSize(230, 140);
							
							String fileTitle = ""+ files.getValueAt(selected_row, 0);
							
							int x = Math.max(0, viewFrame.getLocationOnScreen().x - ((230 - viewFrame.getWidth())/2));
							int y = Math.max(0, viewFrame.getLocationOnScreen().y - ((140 - viewFrame.getHeight())/2));
							
							deleteFrame.setLocation(x,y);
							deleteFrame.setResizable(false);
							deleteFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
							deleteFrame.setTitle("Delete " + fileTitle);
							deleteFrame.setAlwaysOnTop(true);
							
							JPanel deletePanel = new JPanel();
							deletePanel.setBorder(new TitledBorder(new EtchedBorder(),"" + fileTitle));
							
							//Label and text field for user to enter the file's encryption key
							JLabel k = new JLabel("Please enter the 16 character key");
							deletePanel.add(k);
							JPasswordField jps = new JPasswordField(12);
							char defaultEcho = jps.getEchoChar();
							deletePanel.add(jps);
							
							JCheckBox show = new JCheckBox("Show");
							show.setToolTipText("Check this box to reveal key");
							deletePanel.add(show);
							
							//Button to submit the key
							JButton s = new JButton("Delete File");
							deletePanel.add(s);
							
							
							
							
							show.addActionListener( new ActionListener(){
								public void actionPerformed(ActionEvent arg0) {
									jps.setEchoChar(show.isSelected() ? (char)0 : defaultEcho);		
								}
							});
							
							
							/**
							 * Parent Frame : deleteFrame
							 * SUBMIT PASSWORD TO DELETE FILE
							 * When pressed, checks to see that the entered password
							 * is correct. If so, it closes and deletes the file.
							 * Else, clears password and awaits further attempts
							 * BUG NOTE:
							 *  WHEN WRONG PASSWORD OF LENGTH 16 IS ENTERED THEN
							 *  THE CORRECT PASSWORD IS ENTERED, THE FILE WILL ACT
							 *  LIKE IT IS BEING DELETED BUT WILL NEVER ACTUALLY GET
							 *  DELETED.
							 */
							s.addActionListener(new ActionListener(){
								public void actionPerformed(ActionEvent e) {
									
									String fileName = "PW_"+files.getValueAt(selected_row, 0);
									File encrypted = new File(workingDir+fileName+".encrypted");
									File decrypted = new File(workingDir+fileName+".decrypted");
									boolean success = false;
									
									try {
										if(jps.getPassword().length == 16){
											String pass = "";
											for(char c : jps.getPassword()){
												pass += c;
											}
											CryptoUtils.decrypt(pass, encrypted, decrypted);
											pass = "";
											success = true;
										}
									} catch (CryptoException e1) {
										//tk.beep();
										jps.setText("");
										//e1.printStackTrace();
									}
									
									decrypted.delete();
									if(success){
										if(!encrypted.delete()){
											System.out.println("FILE NOT DELETED");
										}
													
										viewFrame.dispose();
										deleteFrame.dispose();
										selected_row = -1;
										view.doClick();
									}			
								}							
							});
							deleteFrame.add(deletePanel);
							deleteFrame.setVisible(true);
						}
					}
					
				});
				
				/**
				 * Parent Frame : viewFrame
				 * ADD CUSTOM PASSWORD
				 * When pressed, opens a screen similar to the "save screen"
				 * that allows the user to enter a custom password that was
				 * not necessarily generated by this program.
				 */
				add.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						
						JFrame addFrame = new JFrame();
						addFrame.setSize(500, 400);
						
						int x = Math.max(0, viewFrame.getLocationOnScreen().x - ((500 - viewFrame.getWidth())/2));
						int y = Math.max(0, viewFrame.getLocationOnScreen().y - ((400 - viewFrame.getHeight())/2));
						viewFrame.dispose();
						addFrame.setLocation(x,y);
						addFrame.setResizable(false);
						addFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
						addFrame.setTitle("Save Form");
						
						JPanel addPanel = new JPanel();
						addPanel.setBorder(new TitledBorder(new EtchedBorder(),"Password Information Form " + version));
			
						//Label and text area for user to enter their desired password
						JLabel generated = new JLabel("Enter Custom Password:");
						
						addPanel.add(generated);
						JTextArea cust_pass = new JTextArea(5,42);
						cust_pass.setLineWrap(true);
						JScrollPane p_scroll = new JScrollPane(cust_pass);
						addPanel.add(p_scroll);
						
						//Label and text field for the user to provide a name for the password
						JLabel titleLabel = new JLabel("Provide a title for this password. This will be the file name. (Required):");
						addPanel.add(titleLabel);		
						JTextField inTitle = new JTextField(42);
						addPanel.add(inTitle);
						
						//Label and text area for user to provide a description or whatever tf they want
						JLabel descLabel = new JLabel("Provide a description for the generated password. (Optional):");
						addPanel.add(descLabel);
						JTextArea desc = new JTextArea(5,42);
						desc.setLineWrap(true);
						JScrollPane descScroll = new JScrollPane(desc);
						addPanel.add(descScroll);
						
						//Label and text field for user to enter the 16 character encryption key they specified.
						JLabel encrypt_key = new JLabel("Enter a 16 character encryption key to secure file. (*** DO NOT LOSE THIS ***):");
						addPanel.add(encrypt_key);	
						JTextField e_key = new JTextField(30);
						addPanel.add(e_key);
						JButton lazyKey = new JButton("Generate Key");
						addPanel.add(lazyKey);
						
						lazyKey.addActionListener( new ActionListener(){
							public void actionPerformed(ActionEvent arg0) {
								ArrayList<String> cList = new ArrayList<String>();
								cList.add(pg.digits);
								cList.add(pg.specials);
								e_key.setText(pg.generate_password(16, cList));
							}
						});
						
						//Button that will save the file using the encryption key
						JButton enc = new JButton("Save and Encrypt");
						enc.setToolTipText("Save and encrypt password file using encryption key");
						addPanel.add(enc);
						
						/**
						 * Parent Frame : addFrame
						 * When pressed, saves the specified password with its
						 * details in an encrypted file using the key the user
						 * enters.
						 */
						enc.addActionListener(new ActionListener(){
							public void actionPerformed(ActionEvent e){
								String key = e_key.getText();
								String title = inTitle.getText();
								String description = desc.getText();
								String pword = cust_pass.getText();
								
								if(title.length() > 0 && key.length() == 16){
									try {
										File f = new File(workingDir+"PW_"+title+".txt");
										FileWriter fw = new FileWriter(f);
										BufferedWriter bw = new BufferedWriter(fw);
										
										bw.write("START PASSWORD");
										bw.newLine();
										bw.write(pword);
										bw.newLine();
										bw.write("END PASSWORD");
										bw.newLine();
										bw.write("START DESC");
										bw.newLine();
										bw.write(description);
										bw.newLine();
										bw.write("END DESC");
				
										bw.close();
										fw.close();
										
										
										File encrypted_file = new File(workingDir+"PW_"+title+".encrypted");
										try {
											CryptoUtils.encrypt(key, f, encrypted_file);
										} catch (CryptoException e1) {
											e1.printStackTrace();
										}
										f.delete();
										addFrame.dispose();
										view.doClick();
									
									} catch (IOException e1) {
										e1.printStackTrace();
									}
								}
							}
						});
						
						addFrame.add(addPanel);
						addFrame.setVisible(true);
					}
				});
		
				
				/**
				 * Parent Frame : viewFrame
				 * When pressed, prompts the user for the encryption key
				 * for the selected file. If correct, displays the info 
				 * for the file. Else clears text and awaits further 
				 * attempts.
				 * BUG NOTE
				 * 	REFER TO DELETE. MAY BE SIMILAR ISSUE.
				 */
				vw.addActionListener(new ActionListener(){

					public void actionPerformed(ActionEvent arg0) {
						if(selected_row > -1){
							JFrame decryptFrame = new JFrame();
							
							String fileTitle = ""+ files.getValueAt(selected_row, 0);
							
							
							decryptFrame.setSize(230, 140);
							
							int x = Math.max(0, viewFrame.getLocationOnScreen().x - ((230  - viewFrame.getWidth())/2));
							int y = Math.max(0, viewFrame.getLocationOnScreen().y - ((140 - viewFrame.getHeight())/2));
							
							decryptFrame.setLocation(x,y);
						
							decryptFrame.setResizable(false);
							decryptFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
							decryptFrame.setTitle("Decrypt " + fileTitle);
							decryptFrame.setAlwaysOnTop(true);
							
							JPanel decryptPanel = new JPanel();
							decryptPanel.setBorder(new TitledBorder(new EtchedBorder(),"" + fileTitle));
							
							//Label and text field for user to enter the encryption key for the selected file
							JLabel k = new JLabel("Please enter the 16 character key");
							decryptPanel.add(k);	
							JPasswordField psf = new JPasswordField(12);
							char defaultEcho = psf.getEchoChar();
							decryptPanel.add(psf);
							
							JCheckBox show = new JCheckBox("Show");
							show.setToolTipText("Check this box to reveal key");
							decryptPanel.add(show);
							
							//Button to submit key to see if correct
							JButton s = new JButton("Decrypt File");
							decryptPanel.add(s);
							
							
							
							
							show.addActionListener( new ActionListener(){
								public void actionPerformed(ActionEvent arg0) {
									psf.setEchoChar(show.isSelected() ? (char)0 : defaultEcho);	
								}
							});
							
							
							/**
							 * Parent Frame : decryptFrame
							 * When pressed, checks to see if the password is of
							 * correct length, if so, checks to see if the password
							 * is the correct key for the file. If so, it will open
							 * the file and display the information.
							 */
							s.addActionListener(new ActionListener(){
								public void actionPerformed(ActionEvent e) {
									String fileName = "PW_"+files.getValueAt(selected_row, 0);
									File encrypted = new File(workingDir+fileName+".encrypted");
									File decrypted = new File(workingDir+fileName+".decrypted");
									
									try{
										if(psf.getPassword().length == 16){
											String pass = "";
											for(char c : psf.getPassword()){
												pass += c;
											}
											CryptoUtils.decrypt(pass, encrypted, decrypted);
											pass = "";
											decryptFrame.dispose();
											JFrame displayFrame = new JFrame();
											displayFrame.setSize(500, 330);
											
											int x = Math.max(0, viewFrame.getLocationOnScreen().x - ((500 - viewFrame.getWidth())/2));
											int y = Math.max(0, viewFrame.getLocationOnScreen().y - ((330 - viewFrame.getHeight())/2));
											viewFrame.dispose();
											
											displayFrame.setLocation(x,y);
	
											displayFrame.setResizable(false);
											displayFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
											displayFrame.setTitle("" + files.getValueAt(selected_row, 0));
											
											JPanel displayPanel = new JPanel();
											displayPanel.setBorder(new TitledBorder(new EtchedBorder(),"Password Viewer " + version));
											displayPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
											
											//Label and text field for the title of the loaded file
											JLabel t = new JLabel("Title:");
											displayPanel.add(t);									
											JTextField ttf = new JTextField(39);
											ttf.setEditable(false);
											displayPanel.add(ttf);
											
											//Label and text area for the password from the loaded file
											JLabel p = new JLabel("Password:");
											displayPanel.add(p);
											JTextArea pta = new JTextArea(5,41);
											pta.setLineWrap(true);
											pta.setEditable(false);
											JScrollPane sppta = new JScrollPane(pta);
											sppta.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
											displayPanel.add(sppta);
											
											//Label and text area for the description from the loaded file
											JLabel d = new JLabel("Description:");
											displayPanel.add(d);			
											JTextArea dta = new JTextArea(5,41);
											dta.setEditable(false);
											dta.setLineWrap(true);
											JScrollPane spdta = new JScrollPane(dta);
											spdta.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
											displayPanel.add(spdta);
											
											//Check box to allow the user to make modifications to the information in the loaded file
											JCheckBox enableChanges = new JCheckBox("Select to edit information");
											displayPanel.add(enableChanges);
											
											JCheckBox wasEdited = new JCheckBox();
											
											
											/**
											 * Parent Frame : displayFrame
											 * When selected, the user is able to edit the
											 * information in the file.
											 */
											enableChanges.addActionListener(new ActionListener(){
												public void actionPerformed(ActionEvent arg0) {					
													ttf.setEditable(enableChanges.isSelected());
													pta.setEditable(enableChanges.isSelected());
													dta.setEditable(enableChanges.isSelected());
													wasEdited.setSelected(true);
												}		
											});
											
											//Button to submit any changes and close the window
											JButton finish = new JButton("Done");
											finish.setToolTipText("Save and encrypt any changes and close window");
											displayPanel.add(finish);
											
											//Button to copy the password from the password text area
											JButton copyP = new JButton("Copy");
											copyP.setToolTipText("Copy the saved password");
											displayPanel.add(copyP);
													
											
											try {
												FileReader fr = new FileReader(decrypted);
												BufferedReader br = new BufferedReader(fr);

												String line1 = br.readLine();
												String s = "";
												if(line1.equals("START PASSWORD")){
													line1 = br.readLine();
													while(!line1.equals("END PASSWORD")){
														s += line1 + "\n";
														line1 = br.readLine();
													}
													pta.setText(s);
													s = "";
													line1 = br.readLine();
												}
												if(line1.equals("START DESC")){
													line1 = br.readLine();
													while(!line1.equals("END DESC")){
														s+= line1 + "\n";
														line1 = br.readLine();
													}
													dta.setText(s);
													s = "";
												}
												br.close();
												fr.close();
												ttf.setText(decrypted.getName().substring(3, decrypted.getName().length()-10));
												
												
												
											} catch (IOException e1) {
												e1.printStackTrace();
											} 
											decrypted.delete();
											
											/**
											 * Parent Frame : displayFrame
											 * Copy the password in the password text area
											 * to the clipboard
											 */
											copyP.addActionListener(new ActionListener(){
												public void actionPerformed(ActionEvent e) {
													StringSelection s = new StringSelection(pta.getText());
													cb.setContents(s, null);	
												}
											});
											
											/**
											 * Parent Frame : displayFrame
											 * Submit and save any changes if any were made
											 * to the loaded file and close the window
											 */
											finish.addActionListener(new ActionListener(){
												public void actionPerformed(ActionEvent e) {
													if(wasEdited.isSelected()){
														String key = "";
														for(char c : psf.getPassword()){
															key += c;
														}
														String title = ttf.getText();
														String description = dta.getText();
														String pass = pta.getText();
			
														try {
															File f = new File(workingDir+"PW_"+title+".txt");
															
															FileWriter fw = new FileWriter(f);
															BufferedWriter bw = new BufferedWriter(fw);
															
															bw.write("START PASSWORD");
															bw.newLine();
															bw.write(pass);
															bw.newLine();
															bw.write("END PASSWORD");
															bw.newLine();
															bw.write("START DESC");
															bw.newLine();
															bw.write(description);
															bw.newLine();
															bw.write("END DESC");
									
															bw.close();
															fw.close();
															
															
															File encrypted_file = new File(workingDir+"PW_"+title+".encrypted");
															try {
																CryptoUtils.encrypt(key, f, encrypted_file);
															} catch (CryptoException e1) {
																e1.printStackTrace();
															}
															f.delete();
															
			
														} catch (IOException e2) {
															e2.printStackTrace();
														}
														key = pass = "";
													}
													displayFrame.dispose();
													viewFrame.dispose();
													view.doClick();
												}							
											});
											
		
											displayFrame.add(displayPanel);
											displayFrame.setVisible(true);
											selected_row = -1;
		
										}
									}
									catch(CryptoException ex){
										System.out.println(ex.getMessage());
										ex.printStackTrace();
									}
									
								}				
							});
							
							decryptFrame.add(decryptPanel);
							decryptFrame.setVisible(true);
						}
					}
					
				});
				
				/**
				 * View Passwords. Parent Frame : mainFrame
				 * Highlights the row clicked on by mouse.
				 */
				files.addMouseListener(new MouseAdapter(){
					public void mouseClicked(MouseEvent e){
						selected_row = files.rowAtPoint(e.getPoint());
					}
				});

				viewFrame.add(viewPanel);
				viewFrame.setVisible(true);
			}
		});
		
		mainFrame.add(mainPanel);
		mainFrame.setVisible(true);
	}
	


}
