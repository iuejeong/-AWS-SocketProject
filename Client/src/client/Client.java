package client;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.google.gson.Gson;

import clientDto.CreateRoomReqDto;
import clientDto.ExitReqDto;
import clientDto.JoinReqDto;
import clientDto.JoinRoomReqDto;
import clientDto.MessageReqDto;
import clientDto.RequestDto;
import lombok.Getter;

@Getter
public class Client extends JFrame {

	private Socket socket;
	private Gson gson;
	private String username;
	private String roomname;
	private String input;

	private static Client instance;

	private JPanel mainPanel;
	private JTextField usernameField;
	private JTextField messageField;
	private JLabel inputMessage;
	private CardLayout mainCard;
	private JTextArea contentView;
	private JList<String> roomList;
	private DefaultListModel<String> roomListModel;
	private JScrollPane roomListPanel;
	private JScrollPane contentViewPanel;
	private JScrollPane messagePanel;
	private JLabel usernameLabel;
	private String selectRoom;
	private JLabel roomLabel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Client frame = Client.getInstance();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static Client getInstance() {
		if (instance == null) {
			instance = new Client();
		}
		return instance;
	}

	/**
	 * Create the frame.
	 */
	private Client() {
		setIconImage(Toolkit.getDefaultToolkit().getImage("./image/아이콘.png"));
		setFont(new Font("D2Coding", Font.BOLD, 15));
		setTitle("오로라");
		gson = new Gson();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 480, 800);
		mainPanel = new JPanel();
		mainPanel.setBorder(null);

		setContentPane(mainPanel);
		mainCard = new CardLayout();
		mainPanel.setLayout(mainCard);

		JPanel loginPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Image background = new ImageIcon("./image/오로라.jpg").getImage();
				g.drawImage(background, 0, 0, 480, 800, null);
			}

		};
		mainPanel.add(loginPanel, "loginPanel");
		loginPanel.setLayout(null);

		usernameField = new JTextField();
		usernameField.setFont(new Font("CookieRun Regular", Font.BOLD, 14));
		usernameField.setBounds(100, 500, 250, 50);
		loginPanel.add(usernameField);
		usernameField.setColumns(10);

		JButton loginButton = new JButton("");
		loginButton.setIcon(new ImageIcon("./image/버튼1.png"));
		loginButton.setForeground(new Color(255, 255, 255));
		loginButton.setBackground(new Color(0, 128, 0));
		loginButton.setFont(new Font("CookieRun Regular", Font.BOLD, 18));
		
		loginButton.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseEntered(MouseEvent e) {
				loginButton.setBackground(Color.RED);
			}
			
			public void mouseExited(MouseEvent e) {
				loginButton.setBackground(new Color(0, 128, 0));
			}
		});
		
		usernameField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					try {

						if (!usernameField.getText().isBlank()) {
							socket = new Socket("127.0.0.1", 9090);
							System.out.println("연결");
							mainCard.show(mainPanel, "listPanel");

						} else {
							JOptionPane.showMessageDialog(null, "아이디를 입력하세요.", "접속실패", JOptionPane.ERROR_MESSAGE);
						}

						ClientRecive clientRecive = new ClientRecive(socket);
						clientRecive.start();

						username = usernameField.getText();
						usernameLabel.setText("이름: " + username);
						JoinReqDto joinReqDto = new JoinReqDto(username);
						sendRequest("join", gson.toJson(joinReqDto));
						

					} catch (ConnectException e1) {
						JOptionPane.showMessageDialog(null, "서버에 연결할 수 없습니다.", "접속실패", JOptionPane.ERROR_MESSAGE);
					} catch (IOException e1) {
						e1.printStackTrace();
					}

				}
			}
		});
		loginButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				try {

					if (!usernameField.getText().isBlank()) {
						socket = new Socket("127.0.0.1", 9090);
						System.out.println("연결");
						mainCard.show(mainPanel, "listPanel");

					} else {
						JOptionPane.showMessageDialog(null, "아이디를 입력하세요.", "접속실패", JOptionPane.ERROR_MESSAGE);
					}

					ClientRecive clientRecive = new ClientRecive(socket);
					clientRecive.start();

					username = usernameField.getText();
					usernameLabel.setText("이름: " + username);
					JoinReqDto joinReqDto = new JoinReqDto(username);
					sendRequest("join", gson.toJson(joinReqDto));

				} catch (ConnectException e1) {
					JOptionPane.showMessageDialog(null, "서버에 연결할 수 없습니다.", "접속실패", JOptionPane.ERROR_MESSAGE);
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}
		});
		loginButton.setBounds(100, 560, 250, 40);
		loginPanel.add(loginButton);

		JPanel roomPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Image background = new ImageIcon("./image/오로라.jpg").getImage();
				g.drawImage(background, 0, 0, 480, 800, null);
			}

		};
		mainPanel.add(roomPanel, "roomPanel");
		roomPanel.setLayout(null);

		roomLabel = new JLabel();
		roomLabel.setForeground(new Color(255, 255, 255));
		roomLabel.setBackground(new Color(0, 0, 0));
		roomLabel.setFont(new Font("CookieRun Regular", Font.BOLD, 16));
		roomLabel.setBounds(10, 10, 145, 35);
		roomPanel.add(roomLabel);

		JLabel exitRoom = new JLabel("");
		exitRoom.setIcon(new ImageIcon("./image/나가기1.png"));
		exitRoom.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseEntered(MouseEvent e) {
				exitRoom.setIcon(new ImageIcon("./image/나가기2.png"));
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				exitRoom.setIcon(new ImageIcon("./image/나가기1.png"));
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {

				ExitReqDto exitReqDto = new ExitReqDto(roomname, username);
				sendRequest("exit", gson.toJson(exitReqDto));

				contentView.setText("");
				mainCard.show(mainPanel, "listPanel");
			}
		});
		
		exitRoom.setBounds(370, 10, 60, 60);
		roomPanel.add(exitRoom);

		inputMessage = new JLabel("");
		inputMessage.setIcon(new ImageIcon("./image/전송1.png"));
		
		inputMessage.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseEntered(MouseEvent e) {
				inputMessage.setIcon(new ImageIcon("./image/전송2.png"));
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				inputMessage.setIcon(new ImageIcon("./image/전송1.png"));
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				contentView.setCaretPosition(contentView.getDocument().getLength());
				sendMessage();
			}
		});
		
		inputMessage.setBounds(413, 705, 40, 45);
		roomPanel.add(inputMessage);

		contentViewPanel = new JScrollPane();
		contentViewPanel.setBounds(0, 88, 464, 607);
		roomPanel.add(contentViewPanel);

		contentView = new JTextArea();
		contentView.setEditable(false);
		contentView.setForeground(new Color(255, 255, 255));
		contentView.setBackground(new Color(0, 128, 0));
		contentView.setFont(new Font("CookieRun Regular", Font.PLAIN, 16));
		contentViewPanel.setViewportView(contentView);

		
		messagePanel = new JScrollPane();
		messagePanel.setBounds(10, 701, 391, 60);
		roomPanel.add(messagePanel);

		messageField = new JTextField();
		messageField.setBackground(new Color(0, 128, 0));
		messageField.setForeground(new Color(255, 255, 255));
		messageField.setFont(new Font("CookieRun Regular", Font.PLAIN, 14));
		messagePanel.setViewportView(messageField);
		messageField.setColumns(10);
		messageField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					contentView.setCaretPosition(contentView.getDocument().getLength());
					sendMessage();
				}
			}
		});

		JPanel listPanel = new JPanel(){
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Image background = new ImageIcon("./image/오로라.jpg").getImage();
				g.drawImage(background, 0, 0,480,800, null);
			}
		
		};
		mainPanel.add(listPanel, "listPanel");
		listPanel.setLayout(null);

		JLabel createRoom = new JLabel("");
		createRoom.setIcon(new ImageIcon("./image/추가1.png"));
		createRoom.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseEntered(MouseEvent e) {
				createRoom.setIcon(new ImageIcon("./image/추가2.png"));
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				createRoom.setIcon(new ImageIcon("./image/추가1.png"));
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				input = JOptionPane.showInputDialog(null, "방이름입력", "방생성", JOptionPane.PLAIN_MESSAGE);
				roomname = input;
					if (input == null) {
						JOptionPane.showMessageDialog(null, "취소되었습니다.", "방 생성 실패", JOptionPane.ERROR_MESSAGE);
					} else if (input.isBlank()){
						JOptionPane.showMessageDialog(null, "방 이름이 비어있습니다.", "방 생성 실패", JOptionPane.ERROR_MESSAGE);
					} else if (roomListModel.contains(roomname)) {
						JOptionPane.showMessageDialog(null, "이미 존재하는 방 이름입니다.", "방 생성 실패", JOptionPane.ERROR_MESSAGE);
					} else {
						CreateRoomReqDto createRoomReqDto = new CreateRoomReqDto(username, input);
						sendRequest("createRoom", gson.toJson(createRoomReqDto));
						contentView.append(roomname + "방이 생성되었습니다. \n");
						roomLabel.setText("방이름: " + roomname);
						mainCard.show(mainPanel, "roomPanel");
					}
			
			}
		});
		
		createRoom.setBounds(29, 91, 45, 45);
		listPanel.add(createRoom);

		roomListPanel = new JScrollPane();
		roomListPanel.setBounds(106, 86, 358, 675);

		listPanel.add(roomListPanel);

		roomListModel = new DefaultListModel<>();
		roomList = new JList(roomListModel);
		roomList.setForeground(new Color(255, 255, 255));
		roomList.setBackground(new Color(0, 128, 0));
		roomList.setFont(new Font("CookieRun Regular", Font.BOLD, 30));
		roomListPanel.setViewportView(roomList);

		roomList.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (roomList.getSelectedValue() != null) {
					roomList = (JList) e.getSource();
					if (e.getClickCount() == 2) {
						roomname = roomList.getSelectedValue();
						JoinRoomReqDto joinRoomReqDto = new JoinRoomReqDto(username, roomname);
						sendRequest("joinRoom", gson.toJson(joinRoomReqDto));
						roomLabel.setText(roomname);
						mainCard.show(mainPanel, "roomPanel");

					}
				}
			}
		});
		usernameLabel = new JLabel("");
		usernameLabel.setForeground(new Color(255, 255, 255));
		usernameLabel.setBackground(new Color(255, 255, 255));
		usernameLabel.setFont(new Font("CookieRun Regular", Font.BOLD, 16));
		usernameLabel.setBounds(10, 10, 145, 35);
		listPanel.add(usernameLabel);
	}

	private void sendRequest(String resourse, String body) {
		OutputStream outputStream;
		try {
			outputStream = socket.getOutputStream();
			PrintWriter out = new PrintWriter(outputStream, true);
			RequestDto requestDto = new RequestDto(resourse, body);
			out.println(gson.toJson(requestDto));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void sendMessage() {
		if (!messageField.getText().isBlank()) {

			MessageReqDto messageReqDto = new MessageReqDto(username, messageField.getText(), roomname);

			sendRequest("sendMessage", gson.toJson(messageReqDto));
			messageField.setText("");
		}
	}

}