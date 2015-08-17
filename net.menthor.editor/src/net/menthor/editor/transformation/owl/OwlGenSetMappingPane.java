package net.menthor.editor.transformation.owl;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import net.menthor.editor.transformation.GeneralizationSetMappingPane;
import net.menthor.editor.v2.tables.ElementChoiceTableModel;
import RefOntoUML.parser.OntoUMLParser;

public class OwlGenSetMappingPane extends JPanel{

	private static final long serialVersionUID = -164010334881840365L;
	
	protected OntoUMLParser refparser;	
	protected GeneralizationSetMappingPane gsPane;
	
	public Object[][] getGenSetEnumMappingMap(){
		return ((ElementChoiceTableModel)gsPane.getTableModel()).getEntries();
	}
	
	public OwlGenSetMappingPane(OntoUMLParser refparser){
		this.refparser = refparser;
		buildUI();
	}
	
	private void buildUI(){
		gsPane = new GeneralizationSetMappingPane(refparser);		
		gsPane.setText("Map specific classes from a Generalization Set to a Enumeration.");		
		setLayout(new BorderLayout(0,0));		
		add(gsPane, BorderLayout.CENTER);
	}
}