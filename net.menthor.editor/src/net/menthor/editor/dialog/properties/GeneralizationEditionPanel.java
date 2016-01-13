package net.menthor.editor.dialog.properties;

/**
 * ============================================================================================
 * Menthor Editor -- Copyright (c) 2015 
 *
 * This file is part of Menthor Editor. Menthor Editor is based on TinyUML and as so it is 
 * distributed under the same license terms.
 *
 * Menthor Editor is free software; you can redistribute it and/or modify it under the terms 
 * of the GNU General Public License as published by the Free Software Foundation; either 
 * version 2 of the License, or (at your option) any later version.
 *
 * Menthor Editor is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Menthor Editor; 
 * if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, 
 * MA  02110-1301  USA
 * ============================================================================================
 */

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;

import org.eclipse.emf.ecore.EObject;
import org.tinyuml.umldraw.GeneralizationElement;

import RefOntoUML.Classifier;
import RefOntoUML.Generalization;
import RefOntoUML.GeneralizationSet;
import RefOntoUML.PackageableElement;
import RefOntoUML.parser.OntoUMLParser;
import RefOntoUML.util.RefOntoUMLElement;
import net.menthor.editor.ui.DiagramManager;
import net.menthor.editor.ui.Models;
import net.menthor.editor.v2.icon.IconMap;
import net.menthor.editor.v2.icon.IconType;

/**
 * @author John Guerson
 */
public class GeneralizationEditionPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unused")
	private GeneralizationElement genElement;	
	private Generalization element;
	private DiagramManager diagramManager;
	@SuppressWarnings("unused")
	private Component parent;
	
	@SuppressWarnings("rawtypes")
	private JComboBox generalCombo;
	private JScrollPane scrollPane;
	@SuppressWarnings("rawtypes")
	private JComboBox specificCombo;
	private JList<RefOntoUMLElement> genSetList;
	private JButton btnRemove;
	private JButton btnAdd;
	@SuppressWarnings("rawtypes")
	private DefaultListModel genSetModel;

	private JButton btnNew; 
	private JButton btnEdit;
	
	public GeneralizationEditionPanel(JDialog parent, final DiagramManager diagramManager, final GeneralizationElement genElement, final Generalization element) 
	{		
		initData(parent,diagramManager,genElement,element);
		initGUI();		
	}
	
	/**
	 * @wbp.parser.constructor 
	 */
	public GeneralizationEditionPanel(JFrame parent, final DiagramManager diagramManager, final GeneralizationElement genElement, final Generalization element) 
	{		
		initData(parent,diagramManager,genElement,element);
		initGUI();		
	}		
	
	public GeneralizationEditionPanel(final Component parent, final DiagramManager diagramManager, final GeneralizationElement genElement, final Generalization element) 
	{
		initData(parent,diagramManager,genElement,element);
		initGUI();
	}
	
	public void initData(final Component parent, final DiagramManager diagramManager, final GeneralizationElement genElement, final Generalization element) 
	{
		this.diagramManager = diagramManager;
		this.genElement = genElement;
		this.element = element;
		this.parent=parent;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void initGUI()
	{
		
		JPanel gsPanel = new JPanel();
		gsPanel.setBorder(BorderFactory.createTitledBorder(""));
		
		JPanel generalSpecificPanel = new JPanel();
		generalSpecificPanel.setBorder(BorderFactory.createTitledBorder(""));
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(gsPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
						.addComponent(generalSpecificPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(generalSpecificPanel, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(gsPanel, GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
					.addGap(11))
		);
		
		JLabel lblSpecific = new JLabel("Specific:");
		
		specificCombo = new JComboBox();
		specificCombo.setPreferredSize(new Dimension(350, 20));
		specificCombo.addItem(getStereotype(element.getSpecific())+" "+element.getSpecific().getName());
		
		JLabel lblGeneral = new JLabel("General:");
		
		generalCombo = new JComboBox();
		generalCombo.setPreferredSize(new Dimension(350, 20));
		GroupLayout gl_generalSpecificPanel = new GroupLayout(generalSpecificPanel);
		gl_generalSpecificPanel.setHorizontalGroup(
			gl_generalSpecificPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_generalSpecificPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_generalSpecificPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblGeneral)
						.addComponent(lblSpecific))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_generalSpecificPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(specificCombo, 0, 355, Short.MAX_VALUE)
						.addComponent(generalCombo, 0, 355, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_generalSpecificPanel.setVerticalGroup(
			gl_generalSpecificPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_generalSpecificPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_generalSpecificPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(generalCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblGeneral))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_generalSpecificPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(specificCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblSpecific))
					.addGap(20))
		);
		gl_generalSpecificPanel.linkSize(SwingConstants.HORIZONTAL, new Component[] {lblSpecific, lblGeneral});
		generalSpecificPanel.setLayout(gl_generalSpecificPanel);
		
		JLabel lblThisGeneralizationParticipates = new JLabel("Participating generalization sets :");
		
		genSetModel = new DefaultListModel();
		for(GeneralizationSet gs: element.getGeneralizationSet())
		{
			genSetModel.addElement(new RefOntoUMLElement(gs,""));
		}		
		genSetList= new JList<RefOntoUMLElement>(genSetModel);
		
		scrollPane = new JScrollPane(genSetList);
		
		btnRemove = new JButton("");
		btnRemove.setIcon(IconMap.getInstance().getIcon(IconType.MENTHOR_DELETE));
		btnRemove.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (genSetModel.size()>0){
					RefOntoUMLElement genSet = (RefOntoUMLElement)genSetList.getSelectedValue();
					
					((GeneralizationSet)genSet.getElement()).getGeneralization().remove(element);
					element.getGeneralizationSet().remove(genSet.getElement());
					
					genSetModel.removeElement(genSetList.getSelectedValue());				
				}
			}
		});
		
		btnAdd = new JButton("");
		btnAdd.setIcon(IconMap.getInstance().getIcon(IconType.MENTHOR_ADD));
		btnAdd.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				OntoUMLParser refparser = Models.getRefparser();
				ArrayList<RefOntoUMLElement> genSetList = new ArrayList<RefOntoUMLElement>();
				for(GeneralizationSet gs: refparser.getAllInstances(GeneralizationSet.class))
				{
					if (!(element.getGeneralizationSet().contains(gs))) genSetList.add(new RefOntoUMLElement(gs,""));
				}				
				if (genSetList.size()==0) {
					JOptionPane.showMessageDialog(GeneralizationEditionPanel.this, "No generalization set left in the model.", "Add", JOptionPane.INFORMATION_MESSAGE);
				}else{
					RefOntoUMLElement genSet = (RefOntoUMLElement) JOptionPane.showInputDialog(GeneralizationEditionPanel.this, 
					        "To which generalization set do you want to include "+element.getSpecific().getName()+"->"+element.getGeneral().getName(),
					        "Add",
					        JOptionPane.QUESTION_MESSAGE, 
					        null, 
					        genSetList.toArray(), 
					        genSetList.toArray()[0]
					);
					if(genSet!=null){
						genSetModel.addElement(genSet);
					
						((GeneralizationSet)genSet.getElement()).getGeneralization().add(element);
						element.getGeneralizationSet().add((GeneralizationSet)genSet.getElement());
					}
				}				
			}
		});
		
		btnNew = new JButton("");
		btnNew.setIcon(IconMap.getInstance().getIcon(IconType.MENTHOR_ADD_GREEN));
		btnNew.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int response = JOptionPane.showConfirmDialog(GeneralizationEditionPanel.this, "Are you sure you want to create a new generalization set?", "Creating Generalization Set", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(response==JOptionPane.OK_OPTION){
					PackageableElement genSet = (PackageableElement)diagramManager.addGeneralizationSet((RefOntoUML.Package)element.eContainer().eContainer());
					genSet.setName("gs");
					((GeneralizationSet)genSet).setIsCovering(true);
					((GeneralizationSet)genSet).setIsDisjoint(true);
					((GeneralizationSet)genSet).getGeneralization().add(element);
					element.getGeneralizationSet().add((GeneralizationSet)genSet);					
					ElementDialogCaller.callGeneralizationSetDialog(diagramManager.getFrame(), (GeneralizationSet)genSet,true);
					genSetModel.addElement(new RefOntoUMLElement(genSet,""));				
				}
			}
		});
		
		btnEdit = new JButton("");
		btnEdit.setIcon(IconMap.getInstance().getIcon(IconType.MENTHOR_EDIT));
		btnEdit.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(genSetModel.size()>0){
					GeneralizationSet genSet = (GeneralizationSet)((RefOntoUMLElement)genSetList.getSelectedValue()).getElement();
					ElementDialogCaller.callGeneralizationSetDialog(diagramManager.getFrame(), genSet,true);
				}
			}
		});
		
		GroupLayout gl_gsPanel = new GroupLayout(gsPanel);
		gl_gsPanel.setHorizontalGroup(
			gl_gsPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_gsPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_gsPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_gsPanel.createSequentialGroup()
							.addGroup(gl_gsPanel.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl_gsPanel.createSequentialGroup()
									.addComponent(lblThisGeneralizationParticipates, GroupLayout.PREFERRED_SIZE, 243, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED, 46, Short.MAX_VALUE))
								.addGroup(gl_gsPanel.createSequentialGroup()
									.addComponent(btnAdd, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)))
							.addComponent(btnNew, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnRemove, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnEdit, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE))
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 406, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_gsPanel.setVerticalGroup(
			gl_gsPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_gsPanel.createSequentialGroup()
					.addGap(3)
					.addComponent(lblThisGeneralizationParticipates, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
					.addGap(4)
					.addComponent(scrollPane)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_gsPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(btnRemove)
						.addComponent(btnNew, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnAdd, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnEdit))
					.addContainerGap())
		);
		gl_gsPanel.linkSize(SwingConstants.VERTICAL, new Component[] {btnRemove, btnAdd, btnNew, btnEdit});
		gl_gsPanel.linkSize(SwingConstants.HORIZONTAL, new Component[] {btnRemove, btnAdd, btnNew, btnEdit});
		gsPanel.setLayout(gl_gsPanel);
		
		setLayout(groupLayout);
		
		setInitialData();
	}
	
	public class CustomComparator implements Comparator<RefOntoUMLElement> 
    {
        @Override
        public int compare(RefOntoUMLElement o1, RefOntoUMLElement o2) {
            return o1.toString().compareToIgnoreCase(o2.toString());
        }
    }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setInitialData()
	{
		ArrayList<RefOntoUMLElement> generallist = new ArrayList<RefOntoUMLElement>();
		ArrayList<RefOntoUMLElement> specificlist = new ArrayList<RefOntoUMLElement>();
		RefOntoUMLElement generalValue = null;
		RefOntoUMLElement specificValue = null;
		OntoUMLParser refparser = Models.getRefparser();
		if (element.getGeneral()!=null) generalValue = new RefOntoUMLElement(element.getGeneral(),"");
		else generalValue = new RefOntoUMLElement(null,"");
		if (element.getSpecific()!=null) specificValue = new RefOntoUMLElement(element.getSpecific(),"");
		else specificValue = new RefOntoUMLElement(null,"");
    	for(RefOntoUML.Type t: refparser.getAllInstances(RefOntoUML.Type.class))
    	{
			if(t instanceof RefOntoUML.Class || t instanceof RefOntoUML.DataType || t instanceof RefOntoUML.Association)
			{
				if (((RefOntoUMLElement) generalValue).getElement()!=null && t.equals(((RefOntoUMLElement) generalValue).getElement())) generallist.add((RefOntoUMLElement)generalValue);				
    			else generallist.add(new RefOntoUMLElement(t,""));
				if (((RefOntoUMLElement) specificValue).getElement()!=null && t.equals(((RefOntoUMLElement) specificValue).getElement())) specificlist.add((RefOntoUMLElement)specificValue);				
    			else specificlist.add(new RefOntoUMLElement(t,""));
    		}	    					
    	}
    	if (((RefOntoUMLElement) generalValue).getElement()==null) generallist.add((RefOntoUMLElement)generalValue);
    	else if (!refparser.getAllInstances(RefOntoUML.Type.class).contains(element.getGeneral())) generallist.add((RefOntoUMLElement)generalValue);
    	if (((RefOntoUMLElement) specificValue).getElement()==null) specificlist.add((RefOntoUMLElement)specificValue);
    	else if (!refparser.getAllInstances(RefOntoUML.Type.class).contains(element.getSpecific())) specificlist.add((RefOntoUMLElement)specificValue);
    	Collections.sort(generallist,new CustomComparator());	    	
    	Collections.sort(specificlist,new CustomComparator());
    	generalCombo.setModel(new DefaultComboBoxModel(generallist.toArray()));
    	generalCombo.setSelectedItem(generalValue);	
    	specificCombo.setModel(new DefaultComboBoxModel(specificlist.toArray()));
    	specificCombo.setSelectedItem(specificValue);	
	}
	
	public static String getStereotype(EObject element)
	{
		String type = element.getClass().toString().replaceAll("class RefOntoUML.impl.","");
	    type = type.replaceAll("Impl","");
	    type = Normalizer.normalize(type, Normalizer.Form.NFD);
	    if (!type.equalsIgnoreCase("association")) type = type.replace("Association","");
	    return type;
	}
	
	public void transferGenData()
	{
		boolean redesign = false;
		
		RefOntoUML.Type general = (RefOntoUML.Type)((RefOntoUMLElement)generalCombo.getSelectedItem()).getElement();			
		if (general!=null && !general.equals(element.getGeneral())) redesign = true;
		element.setGeneral((Classifier)general);
		
		RefOntoUML.Type specific = (RefOntoUML.Type)((RefOntoUMLElement)specificCombo.getSelectedItem()).getElement();			
		if (specific!=null && !specific.equals(element.getSpecific())) redesign = true;
		element.setSpecific((Classifier)specific);
		
		diagramManager.updateMenthorFromModification(element, redesign);
	}
}
