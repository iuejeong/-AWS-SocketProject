package client;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.EventQueue;
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
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.google.gson.Gson;

import clientDto.CreateRoomReqDto;
import clientDto.ExitReqDto;
import clientDto.JoinReqDto;
import clientDto.JoinRoomReqDto;
import clientDto.MessageReqDto;
import clientDto.RequestDto;
import lombok.Getter;
import java.awt.Font;

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
		gson = new Gson();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 480, 800);
		mainPanel = new JPanel();
		mainPanel.setBorder(null);

		setContentPane(mainPanel);
		mainCard = new CardLayout();
		mainPanel.setLayout(mainCard);

		JPanel loginPanel = new JPanel();
		loginPanel.setBackground(new Color(0, 255, 64));
		mainPanel.add(loginPanel, "loginPanel");
		loginPanel.setLayout(null);

		usernameField = new JTextField();
		usernameField.setBounds(100, 500, 250, 50);
		loginPanel.add(usernameField);
		usernameField.setColumns(10);

		JButton loginButton = new JButton("카카오로 시작하기");
		loginButton.setFont(new Font("D2Coding", Font.BOLD, 16));
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
					usernameLabel.setText(username);
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

		JPanel roomPanel = new JPanel();
		mainPanel.add(roomPanel, "roomPanel");
		roomPanel.setLayout(null);

		roomLabel = new JLabel();
		roomLabel.setFont(new Font("D2Coding", Font.BOLD, 16));
		roomLabel.setBounds(92, 25, 133, 15);
		roomPanel.add(roomLabel);

		JLabel exitRoom = new JLabel("");
		exitRoom.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				ExitReqDto exitReqDto = new ExitReqDto(roomname, username);
				sendRequest("exit", gson.toJson(exitReqDto));
				contentView.setText("");
				mainCard.show(mainPanel, "listPanel");
			}
		});
		exitRoom.setIcon(new ImageIcon("C:\\Users\\ITPS\\Desktop\\아이콘\\free-icon-exit-to-app-button-612083.png"));
		exitRoom.setBounds(373, 10, 39, 45);
		roomPanel.add(exitRoom);

		inputMessage = new JLabel("");
		inputMessage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				sendMessage();
			}
		});
		inputMessage.setIcon(new ImageIcon("C:\\Users\\ITPS\\Desktop\\아이콘\\free-icon-right-arrow-4510674 (1).png"));
		inputMessage.setBounds(413, 705, 39, 46);
		roomPanel.add(inputMessage);

		contentViewPanel = new JScrollPane();
		contentViewPanel.setBounds(0, 88, 464, 607);
		roomPanel.add(contentViewPanel);

		contentView = new JTextArea();
		contentView.setFont(new Font("D2Coding", Font.BOLD, 16));
		contentViewPanel.setViewportView(contentView);

		messagePanel = new JScrollPane();
		messagePanel.setBounds(10, 701, 391, 60);
		roomPanel.add(messagePanel);

		messageField = new JTextField();
		messageField.setFont(new Font("D2Coding", Font.PLAIN, 14));
		messagePanel.setViewportView(messageField);
		messageField.setColumns(10);
		messageField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					sendMessage();
				}
			}
		});

		JPanel listPanel = new JPanel();
		mainPanel.add(listPanel, "listPanel");
		listPanel.setLayout(null);

		JLabel createRoom = new JLabel("");
		createRoom.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				input = JOptionPane.showInputDialog(null, "방생성");
				roomname = input;
				if (input != null) {
					mainCard.show(mainPanel, "roomPanel");
					CreateRoomReqDto createRoomReqDto = new CreateRoomReqDto(username, input);
					sendRequest("createRoom", gson.toJson(createRoomReqDto));
					contentView.append(roomname + "방이 생성되었습니다. \n");
				}

			}
		});
		createRoom.setIcon(new ImageIcon("C:\\Users\\ITPS\\Desktop\\아이콘\\free-icon-plus-657023 (2).png"));
		createRoom.setBounds(29, 91, 45, 45);
		listPanel.add(createRoom);

		roomListPanel = new JScrollPane();
		roomListPanel.setBounds(106, 86, 358, 675);

		listPanel.add(roomListPanel);

		roomListModel = new DefaultListModel<>();
		roomList = new JList(roomListModel);
		roomList.setFont(new Font("D2Coding", Font.BOLD, 46));
		roomListPanel.setViewportView(roomList);

		roomList.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if(roomList.getSelectedValue() != null) {
					roomList = (JList) e.getSource();
					if (e.getClickCount() == 2) {
						roomname = roomList.getSelectedValue();
						JoinRoomReqDto joinRoomReqDto = new JoinRoomReqDto(username, roomname);
						sendRequest("joinRoom", gson.toJson(joinRoomReqDto));

						mainCard.show(mainPanel, "roomPanel");
					}
				}
			}
		});
		usernameLabel = new JLabel("");
		usernameLabel.setFont(new Font("D2Coding", Font.BOLD, 16));
		usernameLabel.setBounds(12, 10, 79, 35);
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