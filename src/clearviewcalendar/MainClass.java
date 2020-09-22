package clearviewcalendar;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class MainClass {
	static JLabel mLabelMonth, mLabelYear;
	static JButton mBtnPrev, mBtnNext;
	static JTable mTblCalendar;
	static JComboBox mComboYear;
	static JFrame mFrameMain;
	static Container mPane;
	static DefaultTableModel mMtblCalendar; // table model
	static JScrollPane mStblCalendar; // scrollpane
	static JPanel mPanelCalendar;
	static int mRealYear, mRealMonth, mRealDay, mCurrentYear, mCurrentMonth;

	public static void main(String[] args) {
		// Look and feel
		try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}
        catch (ClassNotFoundException e) {}
        catch (InstantiationException e) {}
        catch (IllegalAccessException e) {}
        catch (UnsupportedLookAndFeelException e) {}

		// Prepare the frame
		mFrameMain = new JFrame("Calendar Now");  //create frame
		mFrameMain.setSize(330, 375);
		mPane = mFrameMain.getContentPane();
		mPane.setLayout(null);
		mFrameMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Create the controls
		mLabelMonth = new JLabel ("January");
		mLabelYear = new JLabel ("Year:");
		mComboYear = new JComboBox();

		mBtnPrev = new JButton ("<<"); // less than
		mBtnNext = new JButton (">>"); // greater than
		mMtblCalendar = new DefaultTableModel(){public boolean isCellEditable(int rowIndex, int mColIndex){return false;}};
		mTblCalendar = new JTable(mMtblCalendar);
		mStblCalendar = new JScrollPane(mTblCalendar);
		mPanelCalendar = new JPanel(null);

		// Set the border
		//mPanelCalendar.setBorder(BorderFactory.createTitledBorder("Calendar")); 
		
		//Register the Action listeners
		mBtnPrev.addActionListener(new btnPrev_Action());
		mBtnNext.addActionListener(new btnNext_Action());
		mComboYear.addActionListener(new cmbYear_Action());
		
		// Add controls to the pane
		mPane.add(mPanelCalendar);
		mPanelCalendar.add(mLabelMonth);
		mPanelCalendar.add(mLabelYear);
		mPanelCalendar.add(mComboYear);
		mPanelCalendar.add(mBtnPrev);
		mPanelCalendar.add(mBtnNext);
		mPanelCalendar.add(mStblCalendar);
		
		// Set the bounds
		mPanelCalendar.setBounds(0, 0, 320, 335);
		mLabelMonth.setBounds(160-mLabelMonth.getPreferredSize().width/2, 25, 100, 25);
		mLabelYear.setBounds(10,305, 80, 20);
		mComboYear.setBounds(230, 305, 80, 20);
		mBtnPrev.setBounds(10, 25, 50, 25);
		mBtnNext.setBounds(260, 25, 50, 25);
		mStblCalendar.setBounds(10, 50, 300, 250);
		
		// Make the frame visible
		mFrameMain.setResizable(false);
		mFrameMain.setVisible(true);
		
		// Get real month/year
		GregorianCalendar cal = new GregorianCalendar(); //create calendar
		mRealDay = cal.get(GregorianCalendar.DAY_OF_MONTH);
		mRealMonth = cal.get(GregorianCalendar.MONTH);
		mRealYear = cal.get(GregorianCalendar.YEAR);
		mCurrentMonth = mRealMonth;
		mCurrentYear = mRealYear;
		
		// Add headers
		String[] headers = {"Sun", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat"};
		for (int i=0; i<7; i++){
				mMtblCalendar.addColumn(headers[i]);
		}
		
		mTblCalendar.getParent().setBackground(mTblCalendar.getBackground());;
		
		// No resize/reorder
		mTblCalendar.getTableHeader().setResizingAllowed(false);
		mTblCalendar.getTableHeader().setReorderingAllowed(false);
		
		// Single cell selection
		mTblCalendar.setColumnSelectionAllowed(true);
		mTblCalendar.setRowSelectionAllowed(true);
		mTblCalendar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// Set row/column count
		mTblCalendar.setRowHeight(38);
		mMtblCalendar.setColumnCount(7);
		mMtblCalendar.setRowCount(6);
		
		// Populate table
		for (int i=mRealYear-100; i<=mRealYear+100; i++) {
		mComboYear.addItem(String.valueOf(i));
		}
		
		//mComboYear.setPrototypeDisplayValue("2020");  //not getting used yet
		
		// Refresh calendar
		refreshCalendar (mRealMonth, mRealYear); 
		
		}


	public static void refreshCalendar(int month, int year) {
		// Variables
		String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec" };
		int nod, som; // number of days, start of month

		// Allow/disallow buttons
		mBtnPrev.setEnabled(true);
		mBtnNext.setEnabled(true);
		if (month == 0 && year <= mRealYear - 10) {mBtnPrev.setEnabled(false);} // Too early
		if (month == 0 && year <= mRealYear + 100) {mBtnPrev.setEnabled(false);} // Too late
		mLabelMonth.setText(months[month]); // Refresh month label at top
		mLabelMonth.setBounds(160 - mLabelMonth.getPreferredSize().width / 2, 25, 180, 25);
		mComboYear.setSelectedItem(String.valueOf(year));

		
		// Clear the table
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				mMtblCalendar.setValueAt(null, i, j);
			}
		}

		// Get first day of month and number of days
		GregorianCalendar cal = new GregorianCalendar(year, month, 1);
		nod = cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
		som = cal.get(GregorianCalendar.DAY_OF_WEEK);

		// Draw calendar
		for (int i = 1; i <= nod; i++) {
			int row = new Integer((i + som - 2) / 7);
			int column = (i + som - 2) % 7;
			mMtblCalendar.setValueAt(i, row, column);
		}

		// Apply renderers
		mTblCalendar.setDefaultRenderer(mTblCalendar.getColumnClass(0), new tblCalendarRenderer());

	}

	
	//colors: 
	//steel blue: 176, 204, 216
	//beige: 241, 232, 219
	//med gray: 221, 218, 215
	static class tblCalendarRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused, int row, int column) {
			super.getTableCellRendererComponent(table, value, selected, focused, row, column);
			if (column == 0 || column == 6){ // Weekend
				setBackground(new Color(221, 218, 215));
			} else { // Week
				setBackground(new Color(241, 232, 219));
			}
			if (value != null) {
				if (Integer.parseInt(value.toString()) == mRealDay && mCurrentMonth == mRealMonth && mCurrentYear == mRealYear) {
					setBackground(new Color(176, 204, 216));
				}
			}
			setBorder(null);
			setForeground(Color.black);
			return this;
		}
	}

	static class btnPrev_Action implements ActionListener {
		public void actionPerformed(ActionEvent e) {               // event
			if (mCurrentMonth == 0) { // back one year
				mCurrentMonth = 11;
				mCurrentYear -= 1;
			}
			else { //back one month
				mCurrentMonth -= 1;
			}
			refreshCalendar(mCurrentMonth, mCurrentYear);
		}
	}

	static class btnNext_Action implements ActionListener {
		public void actionPerformed (ActionEvent e) {
			if (mCurrentMonth == 11) { // forward one year
				mCurrentMonth = 0;
				mCurrentYear += 1;
			} 
			else { // forward one month
				mCurrentMonth += 1;
			}
			refreshCalendar(mCurrentMonth, mCurrentYear);
		}
	}

	static class cmbYear_Action implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (mComboYear.getSelectedItem() != null) {
				String b = mComboYear.getSelectedItem().toString();
				mCurrentYear = Integer.parseInt(b);
				refreshCalendar(mCurrentMonth, mCurrentYear);
			}
		}
	}
}