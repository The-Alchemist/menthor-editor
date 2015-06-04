package net.menthor.editor.transformation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.DefaultCellEditor;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.table.TableCellEditor;

import net.menthor.editor.dialog.properties.AttributesEditionPanel;
import net.menthor.editor.palette.ColorPalette;
import net.menthor.editor.palette.ColorPalette.ThemeColor;
import RefOntoUML.parser.OntoUMLParser;

public class BaseMappingPane extends JPanel {
	
	private static final long serialVersionUID = -7587547341203464118L;

	protected JScrollPane scrollpane = new JScrollPane();
	protected JTable table = new JTable();
	protected ElementMappingTableModel tableModel;
	protected JPanel headerPane = new JPanel();
	protected JButton btnAdd;
	protected JButton btnDelete;
	protected JTextPane textPane = new JTextPane();
	
	public ElementMappingTableModel getTableModel()
	{
		return tableModel;
	}
	
	public JPanel getHeaderPane()
	{
		return headerPane;
	}
	public void setText(String text)
	{
		textPane.setText(text);		
	}
	
	public BaseMappingPane(String sourceColumnTitle, OntoUMLParser refparser, String targetColumnTitle, String[] targetOptions)
	{
		tableModel = new ElementMappingTableModel(sourceColumnTitle, targetColumnTitle);
		
		scrollpane.setMinimumSize(new Dimension(0, 0));
		scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollpane.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));				
		scrollpane.setViewportView(table);
		
		table.setModel(tableModel);		
		table.setBorder(new EmptyBorder(0, 0, 0, 0));
		table.setFillsViewportHeight(true);
		table.setGridColor(Color.LIGHT_GRAY);		
		table.setSelectionBackground(ColorPalette.getInstance().getColor(ThemeColor.BLUE_MEDIUM));
		table.setSelectionForeground(Color.BLACK);
		table.setFocusable(false);	    
		table.setRowHeight(23);
		headerPane.setPreferredSize(new Dimension(10, 60));
		
		setLayout(new BorderLayout(0,0));		
		add(headerPane, BorderLayout.NORTH);
		textPane.setEditable(false);
		textPane.setMargin(new Insets(10, 10, 5, 3));
		textPane.setPreferredSize(new Dimension(6, 50));
		textPane.setBackground(UIManager.getColor("Panel.background"));
		
		btnAdd = new JButton();
		btnAdd.setPreferredSize(new Dimension(33, 30));
		btnAdd.setFocusable(false);
		btnAdd.setToolTipText("Add new primitive type mapping");
		//btnAdd.setText("Add");
		btnAdd.setIcon(new ImageIcon(AttributesEditionPanel.class.getResource("/resources/icons/x16/new.png")));
		btnAdd.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addMapping(arg0);
			}
		});
		
		btnDelete = new JButton();
		btnDelete.setPreferredSize(new Dimension(33, 30));
		btnDelete.setFocusable(false);
		btnDelete.setToolTipText("Delete selected primitive type mapping");
		//btnDelete.setText("Delete");
		btnDelete.setIcon(new ImageIcon(AttributesEditionPanel.class.getResource("/resources/icons/x16/cross.png")));
		
		btnDelete.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				deleteMapping(arg0);
			}
		});
		GroupLayout gl_headerPane = new GroupLayout(headerPane);
		gl_headerPane.setHorizontalGroup(
			gl_headerPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_headerPane.createSequentialGroup()
					.addComponent(textPane, GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnAdd, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnDelete, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		gl_headerPane.setVerticalGroup(
			gl_headerPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_headerPane.createSequentialGroup()
					.addGroup(gl_headerPane.createParallelGroup(Alignment.LEADING)
						.addComponent(textPane, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_headerPane.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_headerPane.createParallelGroup(Alignment.LEADING)
								.addComponent(btnDelete, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnAdd, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		headerPane.setLayout(gl_headerPane);
		add(scrollpane, BorderLayout.CENTER);
	}
	
	public void deleteMapping(ActionEvent evt) 
	{
		int selectedRow = table.getSelectedRow();
		if (selectedRow >= 0 && selectedRow < tableModel.getRowCount()) 
		{
			tableModel.removeEntryAt(selectedRow);
		}
	}
	
	public void addMapping(ActionEvent evt) 
	{
		tableModel.addEmptyEntry();		
	}
	
	public void refreshData()
	{
		tableModel.fireTableDataChanged();
	}
	
	protected TableCellEditor createEditor(Object[] objects) 
	{
        @SuppressWarnings({ "rawtypes", "unchecked" })
		JComboBox combo = new JComboBox(objects) {
        	private static final long serialVersionUID = 1L;			
			@Override
			protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) 
			{
				boolean retValue = super.processKeyBinding(ks, e, condition,pressed);
                if (!retValue && isStartingCellEdit() && editor != null) {
                    // this is where the magic happens
                    // not quite right; sets the value, but doesn't advance the
                    // cursor position for AC
                    editor.setItem(String.valueOf(ks.getKeyChar()));
                }
                return retValue;
			}			
            private boolean isStartingCellEdit() 
            {
                JTable table = (JTable) SwingUtilities.getAncestorOfClass(JTable.class, this);
                return table != null && table.isFocusOwner() && !Boolean.FALSE.equals((Boolean)table.getClientProperty("JTable.autoStartsEdit"));
            }
        };        
        //AutoCompleteDecorator.decorate(combo);
        combo.setEditable(true);
        return new DefaultCellEditor(combo);
    }
}