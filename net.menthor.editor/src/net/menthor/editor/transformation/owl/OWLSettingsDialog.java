package net.menthor.editor.transformation.owl;

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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.menthor.editor.AppFrame;
import net.menthor.editor.DiagramManager;
import net.menthor.editor.dialog.MappingTypeComboItem;
import net.menthor.editor.model.UmlProject;
import net.menthor.editor.transformation.ElementFilterPane;
import net.menthor.editor.util.ApplicationResources;
import net.menthor.editor.util.ConfigurationHelper;
import net.menthor.editor.util.ProjectSettings;
import RefOntoUML.parser.OntoUMLParser;

public class OWLSettingsDialog extends javax.swing.JDialog {

	private static final long serialVersionUID = -4770351584655675698L;
	
	private JTabbedPane tabbedPane = new JTabbedPane();
	private ElementFilterPane filterPane = new ElementFilterPane();
	private OWLPrimitiveMappingPane primitivePane;
	private OWLQualityMappingPane qualityPane;

	private ButtonGroup destinationGroup;
	private JRadioButton fileButton;
	private JTextField filePathText;
	@SuppressWarnings("rawtypes")
	private JComboBox mappingTypeCombo;
	private JButton cancelButton;
	private JButton okButton;
	private JLabel typeLabel;
	private JPanel typePanel;
	private JButton browseButton;
	private JLabel filePathLabel;
	private JPanel destinationPanel;
	private JRadioButton newTabButton;
	private JPanel generateOWLPanel;
	private DiagramManager manager;
	private JTextPane descriptionPane;
	private JTextField iriText;
	private JLabel iriLabel;

	public OWLSettingsDialog(AppFrame frame, DiagramManager diagramManager, boolean modal) {
		super(frame, modal);
//		setIconImage(Toolkit.getDefaultToolkit().getImage(OWLSettingsDialog.class.getResource("/resources/icons/x16/sw-cube.png")));
		this.setManager(diagramManager);
		OntoUMLParser refparser = frame.getProjectBrowser().getParser();
		
		generateOWLPanel = createGenerationPane();		
		filterPane.fillContent(refparser);		
		primitivePane = new OWLPrimitiveMappingPane(refparser);
		qualityPane = new OWLQualityMappingPane(refparser);
		
		addNonClosable(tabbedPane,"Config", generateOWLPanel);
		addNonClosable(tabbedPane,"Filter", filterPane);
		addNonClosable(tabbedPane,"Primitive Type", primitivePane);
		addNonClosable(tabbedPane,"Quality", qualityPane);
		
		tabbedPane.setSelectedComponent(generateOWLPanel);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		myPostInit();
	}

	/** Add Non Closable Tab */
	public Component addNonClosable(JTabbedPane pane, String text, Component component)
	{
		if (component==null) component = new JPanel();
		pane.addTab(text, component);		
		pane.setSelectedComponent(component);
		return component;
	}
	
	private void  myPostInit()
	{
		mappingTypeCombo.setSelectedIndex(0);
		updateDescriptionPane((MappingTypeComboItem) mappingTypeCombo.getSelectedItem());
		
		UmlProject project = manager.getCurrentProject();
 		
		if(ProjectSettings.OWL_ONTOLOGY_IRI.getValue(project) != null && ProjectSettings.OWL_ONTOLOGY_IRI.getValue(project).length() > 0)
			iriText.setText(ProjectSettings.OWL_ONTOLOGY_IRI.getValue(project));
		else
			iriText.setText(getDefaultIRI());
		
		fileButton.setSelected(ProjectSettings.OWL_GENERATE_FILE.getBoolValue(project));
		enableOrDisableFileChooser(ProjectSettings.OWL_GENERATE_FILE.getBoolValue(project));
		filePathText.setText(ProjectSettings.OWL_FILE_PATH.getValue(project));
 		
		String mappingType = ProjectSettings.OWL_MAPPING_TYPE.getValue(project);
		
		for (int i = 0; i < mappingTypeCombo.getItemCount(); i++) {
			MappingTypeComboItem item = (MappingTypeComboItem) mappingTypeCombo.getItemAt(i);
			if(item.getValue().equals(mappingType))
			{
				mappingTypeCombo.setSelectedItem(item);
				break;
			}
		}
		
		getRootPane().setDefaultButton(okButton);
	}
	
	private String getDefaultIRI()
	{
//		Calendar cal = Calendar.getInstance();
//		int year = cal.get(Calendar.YEAR);
//		int month = cal.get(Calendar.MONTH) + 1;
		
//		return "http://www.semanticweb.org/ontologies/" + year + "/" + month + "/ontology.owl";
		UmlProject project = manager.getCurrentProject();
		String projectName = project.getName().replace(" ","");
		return "http://nemo.inf.ufes.br/" + projectName + ".owl";
	}
	
 	private void saveSettings()
 	{
 		UmlProject project = manager.getCurrentProject();
 		
 		ProjectSettings.OWL_ONTOLOGY_IRI.setValue(project, iriText.getText());
 		ProjectSettings.OWL_GENERATE_FILE.setValue(project, Boolean.toString(fileButton.isSelected()));
 		if(fileButton.isSelected())
 			ProjectSettings.OWL_FILE_PATH.setValue(project, filePathText.getText());
 		
 		ProjectSettings.OWL_MAPPING_TYPE.setValue(project, ((MappingTypeComboItem) mappingTypeCombo.getSelectedItem()).value);
 	}
 	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JPanel createGenerationPane() {
		try {
			{				
				//ApplicationResources.getInstance().getString("dialog.owlsettings.reification")
				setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
				
				setTitle("OWL Settings");
				
				generateOWLPanel = new JPanel();
				GroupLayout GenerateOWLPanelLayout = new GroupLayout((JComponent)generateOWLPanel);				
				generateOWLPanel.setLayout(GenerateOWLPanelLayout);
				generateOWLPanel.setPreferredSize(new java.awt.Dimension(524, 394));
				{
					destinationPanel = new JPanel();
					destinationGroup = new ButtonGroup();
					GroupLayout destinationPanelLayout = new GroupLayout((JComponent)destinationPanel);
					destinationPanel.setLayout(destinationPanelLayout);
					destinationPanel.setBorder(BorderFactory.createTitledBorder(ApplicationResources.getInstance().getString("dialog.owlsettings.destination")));
					
					{
						iriLabel = new JLabel();
						iriLabel.setText(ApplicationResources.getInstance().getString("dialog.owlsettings.ontologyiri"));
					}
					{
						iriText = new JTextField();
					}
					{
						newTabButton = new JRadioButton();
						newTabButton.setText(ApplicationResources.getInstance().getString("dialog.owlsettings.newtab"));
						newTabButton.setSelected(true);
						destinationGroup.add(newTabButton);
						newTabButton.addActionListener(new ActionListener () {
							@Override
						    public void actionPerformed(ActionEvent e) {					        
						        enableOrDisableFileChooser(false);
						    }
						});
					}
					{
						fileButton = new JRadioButton();
						fileButton.setText(ApplicationResources.getInstance().getString("dialog.owlsettings.file"));
						destinationGroup.add(fileButton);
						fileButton.addActionListener(new ActionListener () {
							@Override
						    public void actionPerformed(ActionEvent e) {					        
						        enableOrDisableFileChooser(true);
						    }
						});
					}
					{
					
						filePathLabel = new JLabel();
						filePathLabel.setText(ApplicationResources.getInstance().getString("dialog.owlsettings.filepath"));
						filePathLabel.setEnabled(false);
					}
					{
						filePathText = new JTextField();
						filePathText.setEnabled(false);
						filePathText.setEditable(false);
					}
					{
						typeLabel = new JLabel();
						typeLabel.setText(ApplicationResources.getInstance().getString("dialog.owlsettings.type"));
					}
					{
						browseButton = new JButton();
						browseButton.setText("...");
						browseButton.setEnabled(false);
						browseButton.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								getFilePath();
							}
						});
					}
					{
						ComboBoxModel mappingTypeComboModel = getMappingTypeComboModel();
						mappingTypeCombo = new JComboBox();
						mappingTypeCombo.setModel(mappingTypeComboModel);
						mappingTypeCombo.addActionListener (new ActionListener () {
							@Override
						    public void actionPerformed(ActionEvent e) {					        
						        updateDescriptionPane((MappingTypeComboItem)mappingTypeCombo.getSelectedItem());
						    }
						});
					}
					{
						descriptionPane = new JTextPane();
						descriptionPane.setOpaque(false);
						descriptionPane.setEditable(false);
					}
					{
						okButton = new JButton();
						okButton.setText(ApplicationResources.getInstance().getString("stdcaption.ok"));
						okButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								if(validadeSettings())
								{
									saveSettings();
									getManager().generateOwl();
						 			dispose();
								}
							}
						});
					}
					{
						cancelButton = new JButton();
						cancelButton.setText(ApplicationResources.getInstance().getString("stdcaption.cancel"));
						cancelButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								dispose();
							}
						});
					}
					
					{
						typePanel = new JPanel();
						GroupLayout TypePanelLayout = new GroupLayout((JComponent)typePanel);
						typePanel.setLayout(TypePanelLayout);
						typePanel.setBorder(BorderFactory.createTitledBorder(ApplicationResources.getInstance().getString("dialog.owlsettings.mappingtype")));

						TypePanelLayout.setHorizontalGroup(TypePanelLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(TypePanelLayout.createParallelGroup()
							    .addGroup(GroupLayout.Alignment.LEADING, TypePanelLayout.createSequentialGroup()
							        .addComponent(typeLabel, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE)
							        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							        .addComponent(mappingTypeCombo, 0, 247, Short.MAX_VALUE)
							        .addGap(183))
							    .addComponent(descriptionPane, GroupLayout.Alignment.LEADING, 0, 479, Short.MAX_VALUE))
							.addContainerGap());
						TypePanelLayout.setVerticalGroup(TypePanelLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(TypePanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							    .addComponent(mappingTypeCombo, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
							    .addComponent(typeLabel, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGap(16)
							.addComponent(descriptionPane, 0, 84, Short.MAX_VALUE)
							.addContainerGap());
					}
					
					destinationPanelLayout.setHorizontalGroup(destinationPanelLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(filePathLabel, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(destinationPanelLayout.createParallelGroup()
						    .addComponent(filePathText, GroupLayout.Alignment.LEADING, 0, 362, Short.MAX_VALUE)
						    .addGroup(GroupLayout.Alignment.LEADING, destinationPanelLayout.createSequentialGroup()
						        .addGap(22)
						        .addComponent(newTabButton, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
						        .addGap(122)
						        .addComponent(fileButton, GroupLayout.PREFERRED_SIZE, 93, GroupLayout.PREFERRED_SIZE)
						        .addGap(0, 22, Short.MAX_VALUE)))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(browseButton, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
						.addContainerGap());
					destinationPanelLayout.setVerticalGroup(destinationPanelLayout.createSequentialGroup()
						.addGroup(destinationPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						    .addComponent(newTabButton, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						    .addComponent(fileButton, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGap(14)
						.addGroup(destinationPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						    .addComponent(filePathText, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						    .addComponent(filePathLabel, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						    .addComponent(browseButton, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
						.addContainerGap());
				}

				GenerateOWLPanelLayout.setHorizontalGroup(GenerateOWLPanelLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(GenerateOWLPanelLayout.createParallelGroup()
					    .addComponent(destinationPanel, GroupLayout.Alignment.LEADING, 0, 500, Short.MAX_VALUE)
					    .addComponent(typePanel, GroupLayout.Alignment.LEADING, 0, 500, Short.MAX_VALUE)
					    .addGroup(GroupLayout.Alignment.LEADING, GenerateOWLPanelLayout.createSequentialGroup()
					        .addComponent(iriLabel, 0, 90, Short.MAX_VALUE)
					        .addGroup(GenerateOWLPanelLayout.createParallelGroup()
					            .addComponent(iriText, GroupLayout.Alignment.LEADING, 0, 417, Short.MAX_VALUE)
					            .addGroup(GroupLayout.Alignment.LEADING, GenerateOWLPanelLayout.createSequentialGroup()
					                .addGap(0, 239, Short.MAX_VALUE)
					                .addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
					                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
					                .addComponent(okButton, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)))))
					.addContainerGap());
				GenerateOWLPanelLayout.setVerticalGroup(GenerateOWLPanelLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(GenerateOWLPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					    .addComponent(iriText, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
					    .addComponent(iriLabel, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
					.addComponent(destinationPanel, GroupLayout.PREFERRED_SIZE, 93, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
					.addComponent(typePanel, GroupLayout.PREFERRED_SIZE, 157, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
					.addGroup(GenerateOWLPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					    .addComponent(cancelButton, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					    .addComponent(okButton, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap());
			}
			this.setSize(543, 419);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return generateOWLPanel;
	}
 	
	protected boolean validadeSettings() {
		
		if(newTabButton.isSelected())
			return true;
			
		if(fileButton.isSelected() && filePathText.getText().length() > 0)
			return true;
		else
			JOptionPane.showMessageDialog(this, 
					ApplicationResources.getInstance().getString("dialog.owlsettings.error.informfilepath"), 
					ApplicationResources.getInstance().getString("dialog.owlsettings.title"),	
					JOptionPane.ERROR_MESSAGE);
		
		return false;
	}

	private void enableOrDisableFileChooser(boolean flag) {
		filePathText.setEnabled(flag);
		filePathText.setEditable(flag);
		browseButton.setEnabled(flag);
		filePathLabel.setEnabled(flag);
	}

	private void getFilePath()
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle(ApplicationResources.getInstance().getString("stdcaption.selectfile"));
		FileNameExtensionFilter filter = new FileNameExtensionFilter("OWL File (*.owl)", "owl");
		fileChooser.addChoosableFileFilter(filter);
		fileChooser.setFileFilter(filter);
		fileChooser.setAcceptAllFileFilterUsed(false);
		if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File selectedFile = ConfigurationHelper.getFileWithExtension(fileChooser.getSelectedFile(), ".owl");
			filePathText.setText(selectedFile.getAbsolutePath());
		}
	}
	
	private void updateDescriptionPane(MappingTypeComboItem item)
	{
		if(item != null)
			descriptionPane.setText(item.getDescription());
		else
			descriptionPane.setText("");
	}
	
	public void setManager(DiagramManager manager) {
		this.manager = manager;
	}

	public DiagramManager getManager() {
		return manager;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ComboBoxModel getMappingTypeComboModel()
	{
		MappingTypeComboItem rulesItem = new MappingTypeComboItem();
		rulesItem.setDisplayName("OOTOS");
		rulesItem.setValue("RULES");
		rulesItem.setDescription(ApplicationResources.getInstance().getString("dialog.owlsettings.ontouml2owl"));
		
		MappingTypeComboItem simpleItem = new MappingTypeComboItem();
		simpleItem.setDisplayName("Simple");
		simpleItem.setValue("SIMPLE");
		simpleItem.setDescription(ApplicationResources.getInstance().getString("dialog.owlsettings.simple"));
		
		MappingTypeComboItem reificationItem = new MappingTypeComboItem();
		reificationItem.setDisplayName("Reification");
		reificationItem.setValue("REIFICATION");
		reificationItem.setDescription(ApplicationResources.getInstance().getString("dialog.owlsettings.reification"));
		
		MappingTypeComboItem wormA0Item = new MappingTypeComboItem();
		wormA0Item.setDisplayName("Worm View A0");
		wormA0Item.setValue("WORM_VIEW_A0");
		wormA0Item.setDescription(ApplicationResources.getInstance().getString("dialog.owlsettings.worm-view-a0"));
				
		MappingTypeComboItem wormA1Item = new MappingTypeComboItem();
		wormA1Item.setDisplayName("Worm View A1");
		wormA1Item.setValue("WORM_VIEW_A1");
		wormA1Item.setDescription(ApplicationResources.getInstance().getString("dialog.owlsettings.worm-view-a1"));
		
		MappingTypeComboItem wormA2Item = new MappingTypeComboItem();
		wormA2Item.setDisplayName("Worm View A2");
		wormA2Item.setValue("WORM_VIEW_A2");
		wormA2Item.setDescription(ApplicationResources.getInstance().getString("dialog.owlsettings.worm-view-a2"));
		
		return new DefaultComboBoxModel(new Object[] { rulesItem, simpleItem, reificationItem, wormA0Item, wormA1Item, wormA2Item });
	}
	
}