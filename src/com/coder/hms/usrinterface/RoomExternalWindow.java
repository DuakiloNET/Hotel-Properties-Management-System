/**
 * @author Coder ACJHP
 * @Email hexa.octabin@gmail.com
 * @Date 15/07/2017
 */
package com.coder.hms.usrinterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.table.DefaultTableModel;

import com.coder.hms.daoImpl.CustomerDaoImpl;
import com.coder.hms.daoImpl.PaymentDaoImpl;
import com.coder.hms.daoImpl.PostingDaoImpl;
import com.coder.hms.daoImpl.ReservationDaoImpl;
import com.coder.hms.daoImpl.RoomDaoImpl;
import com.coder.hms.entities.Customer;
import com.coder.hms.entities.Payment;
import com.coder.hms.entities.Posting;
import com.coder.hms.entities.Reservation;
import com.coder.hms.entities.Room;
import com.coder.hms.utils.ApplicationLogoSetter;
import com.coder.hms.utils.PayPostTableCellRenderer;
import com.coder.hms.utils.RoomExternalTableHeaderRenderer;
import com.toedter.calendar.JDateChooser;

public class RoomExternalWindow extends JDialog {

	/**
	 * 
	 */
	private JTextPane roomNote;
	private Customer theCustomer;
	private NumberFormat formatter;
	private static String roomNumber;
	private Reservation reservation;
	private JTable payPostTable, customerTable;
	private JDateChooser checkinDate, checkoutDate;
	private static final long serialVersionUID = 1L;
	private final RoomDaoImpl roomDaoImpl = new RoomDaoImpl();
	private final CustomerDaoImpl customerDaoImpl = new CustomerDaoImpl();
	final ReservationDaoImpl reservationDaoImpl = new ReservationDaoImpl();
	private JButton postingBtn, paymentBtn, saveChangesBtn, checkoutBtn;
	final static CustomerDetailWindow custWindow = new CustomerDetailWindow();
	private final String LOGOPATH = "/com/coder/hms/icons/main_logo(128X12).png";
	private final ApplicationLogoSetter logoSetter = new ApplicationLogoSetter();
	private final PayPostTableCellRenderer payPostRenderer = new PayPostTableCellRenderer();
	private JFormattedTextField priceField, totalPriceField, balanceField, remainDebtField;
	private final RoomExternalTableHeaderRenderer THR = new RoomExternalTableHeaderRenderer();

	private final String[] customerColnames = new String[] { "INDEX", "FIRSTNAME", "LASTNAME" };
	private final DefaultTableModel customerModel = new DefaultTableModel(customerColnames, 0);

	private final String[] postPayColnames = new String[] { "DOC. NO", "TYPE", "TITLE", "PRICE", "CURRENCY",
			"EXPLANATION", "DATE TIME" };
	private final DefaultTableModel postPayModel = new DefaultTableModel(postPayColnames, 0);
	private JTextField IdField, groupNameField, agencyField, currencyField, creditField, hostTypeField, totalDaysField;

	/**
	 * Create the dialog.
	 * 
	 * @param roomText
	 */
	public RoomExternalWindow(String roomText) {

		RoomExternalWindow.roomNumber = roomText;

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		setMinimumSize(new Dimension(1000, 700));
		setSize(new Dimension(1184, 700));
		setPreferredSize(new Dimension(1000, 700));
		// set upper icon for dialog frame
		logoSetter.setApplicationLogoJDialog(this, LOGOPATH);

		getContentPane().setForeground(new Color(255, 99, 71));
		getContentPane().setFocusCycleRoot(true);
		getContentPane().setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		getContentPane().setFont(new Font("Verdana", Font.BOLD, 12));
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setModal(true);
		setResizable(false);

		this.setTitle("Coder for HMS - [RoomEx] : " + roomText);

		/* Set default size of frame */
		final Dimension computerScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
		String opSystem = System.getProperty("os.name").toLowerCase();

		if (opSystem.contains("windows") || opSystem.contains("nux")) {

			this.setSize(computerScreenSize);
		} else {

			final Dimension wantedRoomFrameSize = new Dimension(computerScreenSize.width,
					computerScreenSize.height - 90);
			this.setSize(wantedRoomFrameSize);
		}

		this.setLocationRelativeTo(null);
		this.getContentPane().setBackground(Color.decode("#066d95"));
		getContentPane().setLayout(new BorderLayout(0, 0));

		formatter = NumberFormat.getCurrencyInstance();
		formatter.setCurrency(Currency.getInstance(Locale.getDefault()));

		JPanel panel = new JPanel();
		panel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		panel.setAutoscrolls(true);
		panel.setPreferredSize(new Dimension(10, 55));
		getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(null);

		postingBtn = new JButton("Posting");
		postingBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						new PostingExternalWindow(roomText);
						populatePostPayTable(postPayModel);
					}
				});
			}
		});
		postingBtn.setAutoscrolls(true);
		postingBtn.setFont(new Font("Arial", Font.PLAIN, 15));
		postingBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
		postingBtn
				.setIcon(new ImageIcon(RoomExternalWindow.class.getResource("/com/coder/hms/icons/room_posting.png")));
		postingBtn.setBounds(10, 5, 125, 43);
		panel.add(postingBtn);

		paymentBtn = new JButton("Payment");
		paymentBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						new PaymentExternalWindow(roomText);
						populatePostPayTable(postPayModel);
					}
				});
			}
		});
		paymentBtn.setAutoscrolls(true);
		paymentBtn.setFont(new Font("Arial", Font.PLAIN, 15));
		paymentBtn
				.setIcon(new ImageIcon(RoomExternalWindow.class.getResource("/com/coder/hms/icons/payment_cash.png")));
		paymentBtn.setBounds(142, 5, 125, 43);
		panel.add(paymentBtn);

		checkoutBtn = new JButton("Checkout");
		checkoutBtn.setAutoscrolls(true);
		checkoutBtn.setFont(new Font("Arial", Font.PLAIN, 15));
		checkoutBtn
				.setIcon(new ImageIcon(RoomExternalWindow.class.getResource("/com/coder/hms/icons/room_checkout.png")));
		checkoutBtn.setBounds(274, 5, 125, 43);
		checkoutBtn.addActionListener(checkoutListener());
		panel.add(checkoutBtn);

		Component verticalStrut = Box.createVerticalStrut(20);
		verticalStrut.setSize(new Dimension(5, 20));
		verticalStrut.setMinimumSize(new Dimension(5, 20));
		verticalStrut.setIgnoreRepaint(true);
		verticalStrut.setPreferredSize(new Dimension(5, 20));
		verticalStrut.setBackground(Color.BLACK);
		verticalStrut.setBounds(406, 5, 10, 43);
		panel.add(verticalStrut);

		totalPriceField = new JFormattedTextField(formatter);
		totalPriceField.setAlignmentY(Component.TOP_ALIGNMENT);
		totalPriceField.setAlignmentX(Component.RIGHT_ALIGNMENT);
		totalPriceField.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		totalPriceField.setFont(new Font("Arial", Font.BOLD, 15));
		totalPriceField.setBackground(new Color(240, 128, 128));
		totalPriceField.setEditable(false);
		totalPriceField.setBounds(976, 28, 86, 26);
		panel.add(totalPriceField);
		totalPriceField.setColumns(10);

		JLabel balanceLbl = new JLabel("Balance : ");
		balanceLbl.setAutoscrolls(true);
		balanceLbl.setAlignmentY(Component.TOP_ALIGNMENT);
		balanceLbl.setAlignmentX(Component.RIGHT_ALIGNMENT);
		balanceLbl.setFont(new Font("Arial", Font.BOLD, 13));
		balanceLbl.setHorizontalTextPosition(SwingConstants.CENTER);
		balanceLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		balanceLbl.setBounds(859, 5, 114, 20);
		panel.add(balanceLbl);

		balanceField = new JFormattedTextField(formatter);
		balanceField.setAlignmentY(Component.TOP_ALIGNMENT);
		balanceField.setAlignmentX(Component.RIGHT_ALIGNMENT);
		balanceField.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		balanceField.setFont(new Font("Arial", Font.BOLD, 15));
		balanceField.setBackground(new Color(102, 205, 170));
		balanceField.setEditable(false);
		balanceField.setBounds(976, 1, 86, 26);
		balanceField.setColumns(10);
		panel.add(balanceField);

		JLabel totalLbl = new JLabel(" Total account : ");
		totalLbl.setAutoscrolls(true);
		totalLbl.setAlignmentY(Component.TOP_ALIGNMENT);
		totalLbl.setAlignmentX(Component.RIGHT_ALIGNMENT);
		totalLbl.setFont(new Font("Arial", Font.BOLD, 13));
		totalLbl.setHorizontalTextPosition(SwingConstants.CENTER);
		totalLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		totalLbl.setBounds(859, 30, 114, 20);
		panel.add(totalLbl);
		
		JLabel lblReamainingDebt = new JLabel("Remaining debt");
		lblReamainingDebt.setHorizontalTextPosition(SwingConstants.CENTER);
		lblReamainingDebt.setFont(new Font("Arial", Font.BOLD, 13));
		lblReamainingDebt.setHorizontalAlignment(SwingConstants.CENTER);
		lblReamainingDebt.setBounds(1065, 4, 114, 16);
		panel.add(lblReamainingDebt);
		
		remainDebtField = new JFormattedTextField(formatter);
		remainDebtField.setAlignmentY(Component.TOP_ALIGNMENT);
		remainDebtField.setAlignmentX(Component.RIGHT_ALIGNMENT);
		remainDebtField.setFont(new Font("Arial", Font.BOLD, 15));
		remainDebtField.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		remainDebtField.setBackground(Color.ORANGE);
		remainDebtField.setBounds(1080, 24, 86, 26);
		remainDebtField.setEditable(false);
		panel.add(remainDebtField);

		JPanel reservInfoHolder = new JPanel();
		reservInfoHolder.setAlignmentY(Component.TOP_ALIGNMENT);
		reservInfoHolder.setAlignmentX(Component.RIGHT_ALIGNMENT);
		reservInfoHolder.setAutoscrolls(true);
		reservInfoHolder.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		reservInfoHolder.setBackground(new Color(176, 196, 222));
		reservInfoHolder.setPreferredSize(new Dimension(220, 10));
		getContentPane().add(reservInfoHolder, BorderLayout.EAST);
		reservInfoHolder.setLayout(null);

		JLabel lblReservatonInfo = new JLabel("RESERVATION INFO");
		lblReservatonInfo.setFont(new Font("Verdana", Font.BOLD, 14));
		lblReservatonInfo.setHorizontalTextPosition(SwingConstants.CENTER);
		lblReservatonInfo.setHorizontalAlignment(SwingConstants.CENTER);
		lblReservatonInfo.setBounds(2, 4, 216, 29);
		reservInfoHolder.add(lblReservatonInfo);

		saveChangesBtn = new JButton("SAVE CHANGES");
		saveChangesBtn
				.setIcon(new ImageIcon(RoomExternalWindow.class.getResource("/com/coder/hms/icons/reserv_save.png")));
		saveChangesBtn.setAutoscrolls(true);
		saveChangesBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				changeReservationDate();
			}
		});
		saveChangesBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
		saveChangesBtn.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		saveChangesBtn.setBounds(2, 285, 218, 29);
		reservInfoHolder.add(saveChangesBtn);

		JLabel IdLbl = new JLabel("Id : ");
		IdLbl.setBounds(12, 42, 46, 14);
		reservInfoHolder.add(IdLbl);

		IdField = new JTextField();
		IdField.setEditable(false);
		IdField.setBounds(91, 36, 86, 20);
		reservInfoHolder.add(IdField);
		IdField.setColumns(10);

		JLabel lblNewLabel = new JLabel("Checkin : ");
		lblNewLabel.setBounds(12, 205, 70, 14);
		reservInfoHolder.add(lblNewLabel);

		checkinDate = new JDateChooser();
		checkinDate.setEnabled(false);
		checkinDate.setDateFormatString("yyyy-MM-dd");
		checkinDate.setBounds(91, 202, 127, 20);
		reservInfoHolder.add(checkinDate);

		JLabel lblCheckoutDate = new JLabel("Checkout : ");
		lblCheckoutDate.setBounds(12, 233, 70, 14);
		reservInfoHolder.add(lblCheckoutDate);

		checkoutDate = new JDateChooser();
		checkoutDate.setDateFormatString("yyyy-MM-dd");
		checkoutDate.setBounds(91, 230, 127, 20);
		reservInfoHolder.add(checkoutDate);

		JLabel lblGroup = new JLabel("Group : ");
		lblGroup.setBounds(12, 65, 70, 14);
		reservInfoHolder.add(lblGroup);

		groupNameField = new JTextField();
		groupNameField.setEditable(false);
		groupNameField.setBounds(91, 62, 125, 20);
		reservInfoHolder.add(groupNameField);
		groupNameField.setColumns(10);

		JLabel lblAgency = new JLabel("Agency : ");
		lblAgency.setBounds(12, 93, 70, 14);
		reservInfoHolder.add(lblAgency);

		agencyField = new JTextField();
		agencyField.setEditable(false);
		agencyField.setBounds(91, 90, 125, 20);
		reservInfoHolder.add(agencyField);
		agencyField.setColumns(10);

		JLabel lblPrice = new JLabel("Price : ");
		lblPrice.setBounds(12, 121, 70, 14);
		reservInfoHolder.add(lblPrice);

		priceField = new JFormattedTextField();
		priceField.setEditable(false);
		priceField.setBounds(91, 118, 64, 20);
		reservInfoHolder.add(priceField);

		currencyField = new JTextField();
		currencyField.setEditable(false);
		currencyField.setBounds(156, 118, 61, 20);
		reservInfoHolder.add(currencyField);
		currencyField.setColumns(10);

		JLabel lblCreditType = new JLabel("Credit type : ");
		lblCreditType.setBounds(12, 150, 70, 14);
		reservInfoHolder.add(lblCreditType);

		creditField = new JTextField();
		creditField.setBounds(91, 146, 125, 20);
		reservInfoHolder.add(creditField);
		creditField.setColumns(10);

		JLabel lblHostType = new JLabel("Host type : ");
		lblHostType.setBounds(12, 177, 70, 14);
		reservInfoHolder.add(lblHostType);

		hostTypeField = new JTextField();
		hostTypeField.setBounds(91, 174, 127, 20);
		reservInfoHolder.add(hostTypeField);
		hostTypeField.setColumns(10);

		JLabel lblTotalDays = new JLabel("Total days : ");
		lblTotalDays.setBounds(12, 260, 70, 14);
		reservInfoHolder.add(lblTotalDays);

		totalDaysField = new JTextField();
		totalDaysField.setEditable(false);
		totalDaysField.setBounds(91, 257, 86, 20);
		reservInfoHolder.add(totalDaysField);
		totalDaysField.setColumns(10);

		JPanel cusomerTableHolder = new JPanel();
		cusomerTableHolder.setBackground(Color.decode("#066d95"));
		cusomerTableHolder.setAutoscrolls(true);
		getContentPane().add(cusomerTableHolder, BorderLayout.CENTER);
		cusomerTableHolder.setLayout(new BorderLayout(0, 0));

		roomNote = new JTextPane();
		roomNote.setLocale(new Locale("tr", "TR"));
		roomNote.setToolTipText("Write some note.");
		roomNote.setMargin(new Insets(5, 5, 5, 5));
		roomNote.setPreferredSize(new Dimension(0, 45));
		roomNote.setBackground(new Color(255, 255, 224));
		roomNote.setAlignmentX(Component.LEFT_ALIGNMENT);
		roomNote.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		roomNote.setFont(new Font("Arial", Font.BOLD, 15));
		roomNote.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		cusomerTableHolder.add(roomNote, BorderLayout.SOUTH);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBackground(Color.decode("#e1fcff"));
		scrollPane.setAlignmentY(Component.TOP_ALIGNMENT);
		scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		scrollPane.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		cusomerTableHolder.add(scrollPane, BorderLayout.NORTH);

		populateCustomerTable(roomText, customerModel);

		customerTable = new JTable(customerModel);
		customerTable.setCellSelectionEnabled(false);
		customerTable.setColumnSelectionAllowed(false);
		customerTable.getTableHeader().setDefaultRenderer(THR);
		customerTable.addMouseListener(openCustomerListener());
		scrollPane.setViewportView(customerTable);

		JPanel postTableHolder = new JPanel();
		postTableHolder.setPreferredSize(new Dimension(10, 300));
		getContentPane().add(postTableHolder, BorderLayout.SOUTH);
		postTableHolder.setLayout(new BorderLayout(0, 0));

		JScrollPane postableScrollPane = new JScrollPane();
		postableScrollPane.setBackground(Color.decode("#e1fcff"));
		postableScrollPane.setBackground(new Color(230, 230, 250));
		postableScrollPane.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		postTableHolder.add(postableScrollPane, BorderLayout.CENTER);

		populatePostPayTable(postPayModel);

		final JPopupMenu popupMenu = new JPopupMenu();
		final JMenuItem menuItem = new JMenuItem("Delete");
		menuItem.setIcon(new ImageIcon(Main_AllRooms.class.getResource("/com/coder/hms/icons/room_checkout.png")));
		menuItem.addActionListener(ActionListener -> {
			deleteRowsListener();
		});
		popupMenu.add(menuItem);

		payPostTable = new JTable(postPayModel);
		payPostTable.setDefaultRenderer(Object.class, payPostRenderer);
		payPostTable.setCellSelectionEnabled(false);
		payPostTable.setAutoCreateRowSorter(true);
		payPostTable.getTableHeader().setDefaultRenderer(THR);
		postableScrollPane.setViewportView(payPostTable);
		payPostTable.setComponentPopupMenu(popupMenu);
		populateReservationDetail();

		custWindow.setActionListener(saveChanges());

		this.setVisible(true);
	}

	private ActionListener checkoutListener() {
		final ActionListener listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final Room checkingRoom = roomDaoImpl.getRoomByRoomNumber(RoomExternalWindow.roomNumber);

				if (checkingRoom.getUsageStatus().equals("FULL")) {
					if (Double.parseDouble(checkingRoom.getRemainingDebt()) == 0) {

						roomDaoImpl.setRoomCheckedOut(RoomExternalWindow.roomNumber);
						dispose();
					}

					else {
						JOptionPane.showMessageDialog(null, "All room balances need to be zero!",
								JOptionPane.MESSAGE_PROPERTY, JOptionPane.ERROR_MESSAGE);
						return;
					}
				}

				else {
					JOptionPane.showMessageDialog(null, "Choosed room is empty!\nFor checkingout it must be full.",
							JOptionPane.MESSAGE_PROPERTY, JOptionPane.ERROR_MESSAGE);
					return;
				}

			}
		};
		return listener;
	}

	private void deleteRowsListener() {

		final int rowIndex = payPostTable.getSelectedRow();

		final String theId = payPostTable.getValueAt(rowIndex, 0).toString();
		final String type = payPostTable.getValueAt(rowIndex, 1).toString();
		final String amount = payPostTable.getValueAt(rowIndex, 3).toString();

		final Room theRoom = roomDaoImpl.getRoomByRoomNumber(roomNumber);

		float finalBalance = 0;

		if (type.equalsIgnoreCase("System")) {

			final PostingDaoImpl postImpl = new PostingDaoImpl();
			final boolean result = postImpl.deletePosting(Long.parseLong(theId));

			if (result) {

				finalBalance = Float.parseFloat(theRoom.getTotalPrice()) - Float.parseFloat(amount);
				theRoom.setTotalPrice(finalBalance + "");
			}
		}

		else if (type.equalsIgnoreCase("CASH PAYMENT") || type.equalsIgnoreCase("CREDIT CARD")) {

			final PaymentDaoImpl payImpl = new PaymentDaoImpl();
			final boolean result = payImpl.deletePayment(Long.parseLong(theId));

			if (result) {

				finalBalance = Float.parseFloat(theRoom.getBalance()) - Float.parseFloat(amount);
				theRoom.setBalance(finalBalance + "");
			}
		}
		
		
		remainDebtField.setValue(Float.parseFloat(theRoom.getTotalPrice()) - Float.parseFloat(theRoom.getBalance()));
		remainDebtField.revalidate();
		remainDebtField.repaint();
		
		theRoom.setRemainingDebt(remainDebtField.getValue() + "");
		roomDaoImpl.saveRoom(theRoom);
		
		populatePostPayTable(postPayModel);

	}

	private void populateReservationDetail() {

		final Room theRoom = roomDaoImpl.getRoomByRoomNumber(roomNumber);
		reservation = reservationDaoImpl.getReservationById(theRoom.getReservationId());

		IdField.setText(reservation.getId() + "");

		groupNameField.setText(reservation.getGroupName());

		agencyField.setText(reservation.getAgency());

		priceField.setValue(theRoom.getPrice());

		if (theRoom.getCurrency().equalsIgnoreCase("TURKISH LIRA")) {
			currencyField.setText("TL");
		}

		else {
			currencyField.setText(theRoom.getCurrency());
		}

		creditField.setText(reservation.getCreditType());

		hostTypeField.setText(reservation.getHostType());

		LocalDate localDate = LocalDate.parse(reservation.getCheckinDate());
		Date date = java.sql.Date.valueOf(localDate);
		checkinDate.setDate(date);

		localDate = LocalDate.parse(reservation.getCheckoutDate());
		date = java.sql.Date.valueOf(localDate);
		checkoutDate.setDate(date);

		totalDaysField.setText(reservation.getTotalDays() + "");

		final double totalPrice = Double.parseDouble(theRoom.getTotalPrice());
		totalPriceField.setValue(totalPrice);
		final double roombalance = Double.parseDouble(theRoom.getBalance());
		balanceField.setValue(roombalance);
		
		remainDebtField.setValue(totalPrice - roombalance);
	}

	private void changeReservationDate() {

		LocalDate lic = LocalDate.parse(reservation.getCheckinDate());
		Date oldDate = java.sql.Date.valueOf(lic);
		
		LocalDate loc = LocalDate.parse(reservation.getCheckoutDate());
		Date updateDate = java.sql.Date.valueOf(loc);
		
		
		int totalDayResult = (int) ((updateDate.getTime() - oldDate.getTime()) / (1000 * 60 * 60 * 24));
		
		reservation.setTotalDays(Math.abs(totalDayResult));
		String resultDate = new SimpleDateFormat("yyyy-MM-dd").format(checkoutDate.getDate());
		reservation.setCheckoutDate(resultDate);
		reservationDaoImpl.updateReservation(reservation);
	}

	public void populateCustomerTable(String roomText, DefaultTableModel model) {

		// clean table model
		model.setRowCount(0);

		// import all customers from database
		final Room foundedRoom = roomDaoImpl.getRoomByRoomNumber(roomText);

		final CustomerDaoImpl customerDaoImpl = new CustomerDaoImpl();
		final List<Customer> custmerList = customerDaoImpl.getCustomerByReservId(foundedRoom.getReservationId());

		int index = 0;
		// populate table model with loop
		for (Customer cst : custmerList) {
			index++;
			final Object[] rowData = new Object[] { index, cst.getFirstName(), cst.getLastName() };
			model.addRow(rowData);
		}
	}

	private MouseListener openCustomerListener() {
		final MouseAdapter adapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

				if (e.getClickCount() == 2) {

					final int rowIndex = customerTable.getSelectedRow();
					final String name = customerTable.getValueAt(rowIndex, 1).toString();
					final String lastname = customerTable.getValueAt(rowIndex, 2).toString();

					theCustomer = customerDaoImpl.findCustomerByName(name, lastname);

					custWindow.setId(theCustomer.getCustomerId() + "");
					custWindow.setName(theCustomer.getFirstName());
					custWindow.setSurname(theCustomer.getLastName());
					custWindow.setDocument(theCustomer.getDocument());
					custWindow.setDocNo(theCustomer.getDocumentNo());
					custWindow.setCountry(theCustomer.getCountry());
					custWindow.setDateOfBirth(theCustomer.getDateOfBirth());
					custWindow.setEmail(theCustomer.getEmail());
					custWindow.setFatherName(theCustomer.getFatherName());
					custWindow.setMotherName(theCustomer.getMotherName());
					custWindow.setGender(theCustomer.getGender());
					custWindow.setPhone(theCustomer.getPhone());
					custWindow.setMariaggeStaus(theCustomer.getMaritalStatus());
					custWindow.setReservationId(theCustomer.getReservationId() + "");

					custWindow.setVisible(true);
				}

				super.mousePressed(e);
			}
		};
		return adapter;
	}

	// save all changed properties in customer table
	private ActionListener saveChanges() {
		final ActionListener listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				theCustomer.setCountry(custWindow.getCountry());
				theCustomer.setFirstName(custWindow.getName());
				theCustomer.setLastName(custWindow.getSurname());
				theCustomer.setDocument(custWindow.getDocument());
				theCustomer.setDocumentNo(custWindow.getDocNo());
				theCustomer.setCountry(custWindow.getCountry());
				theCustomer.setDateOfBirth(custWindow.getDateOfBirth());
				theCustomer.setEmail(custWindow.getEmail());
				theCustomer.setFatherName(custWindow.getFatherName());
				theCustomer.setMotherName(custWindow.getMotherName());
				theCustomer.setGender(custWindow.getGender());
				theCustomer.setPhone(custWindow.getPhone());
				theCustomer.setMaritalStatus(custWindow.getMariageStatus());
				theCustomer.setReservationId(Long.parseLong(custWindow.getReservationId()));

				boolean success = customerDaoImpl.save(theCustomer);

				if (success) {

					custWindow.setInfoMessage("<html>SUCCESSFULLY ACCOMPLISHED</html>");
					custWindow.setInfoLabelColor(Color.decode("#00FF00"));
				} else {
					custWindow.setInfoMessage("<html>OPERTION IS FAILED!</html>");
					custWindow.setInfoLabelColor(Color.decode("#cd2626"));
				}

			}
		};
		return listener;
	}

	private void populatePostPayTable(DefaultTableModel model) {

		// import all customers from database
		final PostingDaoImpl postingDaoImpl = new PostingDaoImpl();
		List<Posting> postingList = postingDaoImpl.getAllPostingsByRoomNumber(roomNumber);

		final PaymentDaoImpl paymentDaoImpl = new PaymentDaoImpl();
		List<Payment> paymentlist = paymentDaoImpl.getAllPaymentsByRoomNumber(roomNumber);

		// clean table model
		model.setRowCount(0);

		for (Posting pos : postingList) {

			model.addRow(new Object[] { pos.getId(), pos.getPostType(), pos.getTitle(), pos.getPrice(),
					pos.getCurrency(), pos.getExplanation(), pos.getDateTime() });
		}

		for (Payment pay : paymentlist) {

			model.addRow(new Object[] { pay.getId(), pay.getPaymentType(), pay.getTitle(), pay.getPrice(),
					pay.getCurrency(), pay.getExplanation(), pay.getDateTime() });
		}
	}
}
