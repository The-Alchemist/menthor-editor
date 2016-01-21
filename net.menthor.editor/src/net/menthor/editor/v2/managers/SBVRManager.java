package net.menthor.editor.v2.managers;

import java.awt.Desktop;
import java.io.File;

import RefOntoUML.parser.OntoUMLParser;
import RefOntoUML.util.RefOntoUMLResourceUtil;

import net.menthor.editor.ui.Models;

import net.menthor.editor.v2.types.ResultType;
import net.menthor.editor.v2.types.ResultType.Result;
import net.menthor.editor.v2.util.Directories;
import net.menthor.editor.v2.util.Util;

import net.menthor.ontouml2sbvr.OntoUML2SBVR;

public class SBVRManager extends BaseManager {

	private static SBVRManager instance = new SBVRManager();
	public static SBVRManager get() { return instance; }
	
	/**  Generate SBVR documentation.
	 *   In order to use the plug-in, we need to store the model into a file before. */
	public void generateSbvr(){
		OntoUMLParser refparser = Models.getRefparser();		
		generateSbvr(refparser.getModel());
	}
	
	/**  Generate SBVR documentation. 
	 *   In order to use the plug-in, we need to store the model into a file before. */
	public void generateSbvr(RefOntoUML.Package refpackage){
		ResultType result;
		String name = new String();
		if(refpackage.getName()==null || refpackage.getName().isEmpty()) name = "model";
		else name = refpackage.getName();
		String modelFileName = Util.getCanonPath(Directories.getTempDir(), name+".refontouml");
		File modelFile = new File(modelFileName);  	
    	modelFile.deleteOnExit();    	
		try {
			RefOntoUMLResourceUtil.saveModel(modelFileName, refpackage);
			OntoUML2SBVR.Transformation(modelFileName);			
			String docPage = modelFile.getPath().replace(".refontouml", ".html");			
			infoManager.showOutputText("SBVR generated successfully", true, true); 
			result = new ResultType(Result.SUCESS, "SBVR generated successfully", new Object[] { docPage });			
		} catch (Exception ex) {
			ex.printStackTrace();
			result = new ResultType(Result.ERROR, "Error while generating the SBVR representaion. Details: " + ex.getMessage(), null);
		}		
		if(result.getResultType() != Result.ERROR)
		{
			infoManager.showOutputText(result.toString(), true, true);			
			String htmlFilePath = (String) result.getData()[0];
			File file = new File(htmlFilePath);
			openLinkWithBrowser(file.toURI().toString());
		}else{
			infoManager.showOutputText(result.toString(), true, true); 
		}
	}
	
	public void openLinkWithBrowser(String link){
		Desktop desktop = Desktop.getDesktop();
		if( !desktop.isSupported(Desktop.Action.BROWSE)){
			System.err.println( "Desktop doesn't support the browse action (fatal)" );
			return;
		}
		try {
			java.net.URI uri = new java.net.URI(link);
			desktop.browse(uri);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
