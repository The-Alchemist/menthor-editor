package net.menthor.antipattern.wizard.relcomp;

import net.menthor.antipattern.relcomp.RelCompOccurrence;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.wb.swt.layout.grouplayout.GroupLayout;

/**
 * @author Tiago Sales
 * @author John Guerson
 *
 */

public class RelCompThirdPage extends RelCompPage {

	//GUI
	public Button btnSourceDependsOnTarget;
	public Button btnTargetDependsOnSource;
	public Button btnBoth;
	public Button btnNone;
	
	/**
	 * Create the wizard.
	 */
	public RelCompThirdPage(RelCompOccurrence relSpec) 
	{
		super(relSpec);
		setDescription("Associations: "+relComp.getParser().getStringRepresentation(relComp.getA1())+" and "+relComp.getParser().getStringRepresentation(relComp.getA2()));
		
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		
		Label lblQuestion = new Label(container, SWT.WRAP);
		lblQuestion.setText("To complete the analysis, let's evaluate the remainder constraint possibilities.\r\n\r\n" +
							"If an instance 'x' of '"+getRelComp().getA2Source().getName()+"' is connected to an instance 'y' of '"+getRelComp().getA2Target().getName()+
							"', through '"+getRelComp().getParser().getStringRepresentation(getRelComp().getA2())+"', is it NECESSARY that:");
		
		SelectionAdapter listener = new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent e) {
	        if (isPageComplete()==false) setPageComplete(true);
	      }
	    };
		    
	    setPageComplete(false);

	    btnSourceDependsOnTarget = new Button(container, SWT.RADIO);
		btnSourceDependsOnTarget.addSelectionListener(listener);
		btnSourceDependsOnTarget.setText("'x' is connected to NO instances of '' to which 'y' is connected to through ''");	
		
		btnTargetDependsOnSource = new Button(container, SWT.RADIO);
		btnTargetDependsOnSource.addSelectionListener(listener);
		btnTargetDependsOnSource.setText("'y' is connected to NO instances of '' to which 'x' is connected to through ''");	
		
		if(getRelComp().a2EndsSpecializeA1Target()){
			btnSourceDependsOnTarget.setText("'x' is connected to NO instances of '"+getRelComp().getA1Source().getName()+"' to which 'y' is connected to through '"+
											 getRelComp().getParser().getStringRepresentation(getRelComp().getA1())+"'");	
			btnTargetDependsOnSource.setText("'y' is connected to NO instances of '"+getRelComp().getA1Source().getName()+"' to which 'x' is connected to through '"+
											 getRelComp().getParser().getStringRepresentation(getRelComp().getA1())+"'");	
		}
		else{
			btnSourceDependsOnTarget.setText("'x' is connected to NO instances of '"+getRelComp().getA1Target().getName()+"' to which 'y' is connected to through '"+
					 getRelComp().getParser().getStringRepresentation(getRelComp().getA1())+"'");
			btnTargetDependsOnSource.setText("'y' is connected to NO instances of '"+getRelComp().getA1Target().getName()+"' to which 'x' is connected to through '"+
					 getRelComp().getParser().getStringRepresentation(getRelComp().getA1())+"'");	
		}
		
		btnBoth = new Button(container, SWT.RADIO);
		btnBoth.setText("First two options are necessities");
		btnBoth.addSelectionListener(listener);
		
		btnNone = new Button(container, SWT.RADIO);
		btnNone.setText("None of the options are necessities");
		btnNone.addSelectionListener(listener);
		
		Label lblNote = new Label(container, SWT.WRAP | SWT.RIGHT);
		lblNote.setText("Note that if '"+getRelComp().getParser().getStringRepresentation(getRelComp().getA2())+"' is a symmetric relation and that is enforced in the model, the effects of the first three options are the same.");
		lblNote.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		lblNote.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.ITALIC));
		GroupLayout gl_container = new GroupLayout(container);
		gl_container.setHorizontalGroup(
			gl_container.createParallelGroup(GroupLayout.LEADING)
				.add(gl_container.createSequentialGroup()
					.add(10)
					.add(gl_container.createParallelGroup(GroupLayout.LEADING)
						.add(btnSourceDependsOnTarget, GroupLayout.DEFAULT_SIZE, 699, Short.MAX_VALUE)
						.add(btnTargetDependsOnSource, GroupLayout.DEFAULT_SIZE, 699, Short.MAX_VALUE)
						.add(btnBoth, GroupLayout.DEFAULT_SIZE, 699, Short.MAX_VALUE)
						.add(btnNone, GroupLayout.DEFAULT_SIZE, 699, Short.MAX_VALUE)
						.add(GroupLayout.TRAILING, lblNote, GroupLayout.DEFAULT_SIZE, 699, Short.MAX_VALUE)
						.add(GroupLayout.TRAILING, lblQuestion, GroupLayout.DEFAULT_SIZE, 699, Short.MAX_VALUE))
					.add(10))
		);
		gl_container.setVerticalGroup(
			gl_container.createParallelGroup(GroupLayout.LEADING)
				.add(gl_container.createSequentialGroup()
					.add(10)
					.add(lblQuestion, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)
					.add(6)
					.add(btnSourceDependsOnTarget)
					.add(6)
					.add(btnTargetDependsOnSource)
					.add(6)
					.add(btnBoth)
					.add(6)
					.add(btnNone)
					.add(74)
					.add(lblNote, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
					.add(10))
		);
		container.setLayout(gl_container);
		
	}
	
	@Override
	public IWizardPage getNextPage() {
		RelCompAction action;
		
		getRelCompWizard().removeAllActions();
		
		if(btnSourceDependsOnTarget.getSelection() || btnBoth.getSelection()){
			action = new RelCompAction(relComp);
			action.setExcludesAllSource();
			getRelCompWizard().addAction(0, action);
		}
		
		if(btnTargetDependsOnSource.getSelection() || btnBoth.getSelection()){
			action = new RelCompAction(relComp);
			action.setExcludesAllTarget();
			getRelCompWizard().addAction(0, action);
		}
		
		return getRelCompWizard().getFinishing();
	}
}
