package server;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import controls.ServerControls;

public class FrameStatusVps extends JFrame {
	private static final long serialVersionUID = 1L;
	DefaultListModel<String> listDefaut;
	JScrollPane scrollPane;
	JList<String> jlIp;
	
	DefaultListModel<String> listDefautWarning;
	JScrollPane scrollPaneWarning;
	JList<String> jlIpWarning;

	public FrameStatusVps() {
		setLayout(null);
		setTitle("VPS Status");
		setSize(1000, 630);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		
		JLabel lbAllIp = new JLabel("All IP Address");
		lbAllIp.setBounds(200, 10, 150, 20);
		add(lbAllIp);
		
		JLabel lbWarningIP = new JLabel("Warning IP Address");
		lbWarningIP.setBounds(700, 10, 150, 20);
		add(lbWarningIP);
		
		listDefaut = new DefaultListModel<>();
		jlIp = new JList<String>(listDefaut);
		jlIp.setBounds(10, 40, 550, 550);
		jlIp.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane = new JScrollPane(jlIp);
		scrollPane.setBounds(10, 40, 550, 550);
		add(scrollPane);
		
		listDefautWarning = new DefaultListModel<>();
		jlIpWarning = new JList<String>(listDefautWarning);
		jlIpWarning.setBounds(590, 40, 380, 550);
		jlIpWarning.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPaneWarning = new JScrollPane(jlIpWarning);
		scrollPaneWarning.setBounds(590, 40, 380, 550);
		add(scrollPaneWarning);
	}

	public static void main(String[] args) {
		System.out.println("ShowStatusVps");
		FrameStatusVps myUI = new FrameStatusVps();
		myUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myUI.setLocationRelativeTo(null);
		myUI.setVisible(true);

		while (true) {
			ServerControls myControler = new ServerControls();
			try {
				myControler.getAllAccountFromMySQL();
				
				// list all ip
				{
					int listIpSize = myControler.listIps.size();
					int index = myUI.jlIp.getLastVisibleIndex();
					if(index > listIpSize)
					{
						index = listIpSize;
					}
					int selectedIndex = myUI.jlIp.getSelectedIndex();
					
					String arrayStrings[] = new String[myControler.listInfos.size() + 1];
					arrayStrings[0] = "0.     0.0.0.0     SUPER IP ADDRESS";
					for(int i=0; i<listIpSize; i++)
					{
						String tempString = myControler.listInfos.get(i);
						arrayStrings[i+1] = (i+1) + ".     " + tempString;
					}
					myUI.jlIp.setListData(new String [0]);
					Thread.sleep(1);
					myUI.jlIp.setListData(arrayStrings);
					myUI.jlIp.ensureIndexIsVisible(index);
					if(selectedIndex>=0)
					{
						myUI.jlIp.setSelectedIndex(selectedIndex);
					}
				}
				
				// list warning ip
				{
					int listIpSize = myControler.listWarning.size();
					int index = myUI.jlIpWarning.getLastVisibleIndex();
					if(index > listIpSize)
					{
						index = listIpSize;
					}
					int selectedIndex = myUI.jlIpWarning.getSelectedIndex();
					
					String arrayStrings[] = new String[myControler.listWarning.size() + 1];
					arrayStrings[0] = "0.     0.0.0.0     SUPER IP ADDRESS";
					for(int i=0; i<listIpSize; i++)
					{
						String tempString = myControler.listWarning.get(i);
						arrayStrings[i+1] = (i+1) + ".     " + tempString;
					}
					myUI.jlIpWarning.setListData(new String [0]);
					Thread.sleep(1);
					myUI.jlIpWarning.setListData(arrayStrings);
					myUI.jlIpWarning.ensureIndexIsVisible(index);
					if(selectedIndex>=0)
					{
						myUI.jlIpWarning.setSelectedIndex(selectedIndex);
					}
				}
				
				Thread.sleep(5000);
			} catch (Exception e) {
				System.out.println("Error thread load all ip!");
			}
		}
	}
}
