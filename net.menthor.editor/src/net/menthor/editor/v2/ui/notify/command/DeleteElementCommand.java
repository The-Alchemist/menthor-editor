package net.menthor.editor.v2.ui.notify.command;

/**
 * Copyright 2007 Wei-ju Wu
 *
 * This file is part of TinyUML.
 *
 * TinyUML is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * TinyUML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TinyUML; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.emf.edit.command.DeleteCommand;
import org.tinyuml.draw.CompositeNode;
import org.tinyuml.draw.Connection;
import org.tinyuml.draw.DiagramElement;
import org.tinyuml.draw.Node;
import org.tinyuml.ui.diagram.OntoumlEditor;
import org.tinyuml.ui.diagram.SelectionHandler;
import org.tinyuml.umldraw.ClassElement;
import org.tinyuml.umldraw.StructureDiagram;
import org.tinyuml.umldraw.shared.BaseConnection;

import RefOntoUML.Derivation;
import RefOntoUML.Element;
import RefOntoUML.Generalization;
import RefOntoUML.GeneralizationSet;
import RefOntoUML.MaterialAssociation;
import RefOntoUML.Relationship;
import net.menthor.editor.v2.commanders.UpdateCommander;
import net.menthor.editor.v2.managers.OccurenceManager;
import net.menthor.editor.v2.managers.ProjectManager;
import net.menthor.editor.v2.resource.RefOntoUMLEditingDomain;
import net.menthor.editor.v2.ui.notify.ActionType;
import net.menthor.editor.v2.ui.notify.DiagramCommand;
import net.menthor.editor.v2.ui.notify.NotificationType;

/**
 * A command class to remove allElements from a diagram.
 * 
 * @author Wei-ju Wu, John Guerson
 */
public class DeleteElementCommand extends DiagramCommand{

	private static final long serialVersionUID = 2456036038567915529L;	
	
	private Collection<DiagramElement> diagramElemList = new ArrayList<DiagramElement>();
	private Collection<DiagramElement> diagramElemDep1List = new ArrayList<DiagramElement>(); 
	private Collection<DiagramElement> diagramElemDep2List = new ArrayList<DiagramElement>();
	
	private Collection<Element> elemList= new ArrayList<Element>();
	private Collection<Element> elemDep1List= new ArrayList<Element>(); 
	private Collection<Element> elemDep2List= new ArrayList<Element>();
	
	private List<ParentChildRelation> parentChildRelations = new ArrayList<ParentChildRelation>();
	private List<ParentChildRelation> parentChildRelationsDep1 = new ArrayList<ParentChildRelation>(); 
	private List<ParentChildRelation> parentChildRelationsDep2 = new ArrayList<ParentChildRelation>();
	
	private boolean deleteFromModel;
	private boolean deleteFromDiagram;
	
	/** A helper class to store the original parent child relation. */
	private static class ParentChildRelation {
		DiagramElement element;
		@SuppressWarnings("unused")
		CompositeNode parent;		
		public ParentChildRelation(DiagramElement anElement, CompositeNode aParent){
			parent = aParent;
			element = anElement;
		}
	}

	public DeleteElementCommand(OntoumlEditor editor, Collection<Element> theElements, boolean deleteFromModel, boolean deleteFromDiagram) 
	{
		this.ontoumlEditor = editor;	
		this.deleteFromDiagram = deleteFromDiagram;
		this.deleteFromModel = deleteFromModel;
		
		// requested element for deletion
		elemList.addAll(theElements);		
		if(ontoumlEditor!=null){
			diagramElemList.addAll(OccurenceManager.get().getDiagramElements(elemList,ontoumlEditor.getDiagram()));
		}
		
		//System.out.println("Requested for deletion: \n- "+elemList);
		//System.out.println("Related diagram elements requested for deletion: \n- "+diagramElemList);
		
		// init the dependecies of level 1 and 2.
		for (Element elem : theElements) 
		{
			// level 1 of dependency
			ArrayList<Relationship> depList = ProjectManager.get().getProject().getRefParser().getDirectRelationships(elem);			
			depList.removeAll(elemList);
			for(Element e: depList) { if(!elemDep1List.contains(e)) elemDep1List.add(e); }			
			
			// level 2 of dependency
			// the case in which there is a material in depList. We must include the derivation too.
			for(Relationship r: depList )
			{ 
				if (r instanceof MaterialAssociation) 
				{ 
					Derivation d =  ProjectManager.get().getProject().getRefParser().getDerivation((MaterialAssociation)r);
					if(d!=null) {
						if(!elemDep2List.contains(d)) elemDep2List.add(d);						
					}
				}
			}			
		}		
		if((notificator)!=null){
			diagramElemDep1List.addAll(OccurenceManager.get().getDiagramElements(elemDep1List,ontoumlEditor.getDiagram()));		
			diagramElemDep2List.addAll(OccurenceManager.get().getDiagramElements(elemDep2List,ontoumlEditor.getDiagram()));
		}
		
		//System.out.println("Dependences level 1 for deletion: \n- "+elemDep1List);
		//System.out.println("Related diagram elements of dependences level 1 for deletion: \n- "+diagramElemDep1List);
		
		//System.out.println("Dependences level 2 for deletion: \n- "+elemDep2List);
		//System.out.println("Related diagram elements of dependences level 2 for deletion: \n- "+diagramElemDep2List);
		
		// Parent children and their dependencies
		for (DiagramElement elem : diagramElemList){
			parentChildRelations.add(new ParentChildRelation(elem, elem.getParent()));
		}
		for(DiagramElement elem: diagramElemDep1List){
			parentChildRelationsDep1.add(new ParentChildRelation(elem, elem.getParent()));
		}
		for(DiagramElement elem: diagramElemDep2List){
			parentChildRelationsDep2.add(new ParentChildRelation(elem, elem.getParent()));
		}
		
		//System.out.println("Parent and their children for deletion: \n- "+parentChildRelations);
		//System.out.println("Parent and their children in dependency of level 1 for deletion: \n- "+parentChildRelationsDep1);
		//System.out.println("Parent and their children in dependency of level 2 for deletion: \n- "+parentChildRelationsDep2);
		
	}

	@Override
	public void redo() 
	{	
		isRedo = true;
		super.redo();
		run();
	}

	public void run() 
	{
		ArrayList<DiagramElement> list = new ArrayList<DiagramElement>();
		
		// deletes dependencies level2 (derivations)
		runDelete(diagramElemDep2List,elemDep2List);	
		list.addAll(diagramElemDep2List);
		
		// deletes dependencies level1
		runDelete(diagramElemDep1List,elemDep1List);
		list.addAll(diagramElemDep1List);
		
		// delete the element requested
		runDelete(diagramElemList, elemList);
		list.addAll(diagramElemList);
		
		if (notificator!=null) {
			notificator.notify(this,(List<DiagramElement>) list, NotificationType.DELETE, isRedo ? ActionType.REDO : ActionType.DO);		
						
		}
	}

	@Override
	public void undo() 
	{		
		super.undo();
		
		// undo the element requested
		runUndo(parentChildRelations,elemList);
		
		// undo dependencies level1
		runUndo(parentChildRelationsDep1,elemDep1List);		
		
		// undo dependencies level2 (derivations)
		runUndo(parentChildRelationsDep2,elemDep2List);	
		
		//notify
		if(notificator!=null){
			ArrayList<DiagramElement> list = new ArrayList<DiagramElement>();
			list.addAll(diagramElemList);
			list.addAll(diagramElemDep1List);
			list.addAll(diagramElemDep2List);
			notificator.notify(this,(List<DiagramElement>) list, NotificationType.DELETE, ActionType.UNDO);		
		}
	}
	
	public void runDelete(Collection<DiagramElement> diagramElemList, Collection<Element> elemList)
	{
		if(deleteFromDiagram) {
			runDeleteFromDiagram(diagramElemList);			
		}
		
		if(deleteFromModel) {
			runDeleteFromModel(elemList);			
		}						
	}
	
	public void runUndo(Collection<ParentChildRelation> parentChildList, Collection<Element> elemList)
	{	
		if(deleteFromModel) {
			runUndoFromModel(elemList);			
		}
		
		if(deleteFromDiagram) {
			runUndoFromDiagram(parentChildList, elemList);			
		}		
	}
	
	private void runDeleteFromDiagram(Collection<DiagramElement> diagramElemList)
	{
		for (DiagramElement element : diagramElemList) 
		{
			deleteFromDiagram(element);	
			OccurenceManager.get().remove(element);
		}
		SelectionHandler selHandler = ontoumlEditor.getSelectionHandler();
		selHandler.elementRemoved((List<DiagramElement>)diagramElemList);
	}
	
	private void runUndoFromDiagram(Collection<ParentChildRelation> parentChildList, Collection<Element> elemList)
	{
		int i=0;
		for (ParentChildRelation relation : parentChildList) 
		{
			undoFromDiagram(relation);	
			OccurenceManager.get().add(((ArrayList<Element>)elemList).get(i),relation.element);
			i++;
		}
	}
	
	private void deleteFromDiagram (DiagramElement element)
	{
//		System.out.println("deleting from diagram="+element);
		//detach ends
//		if (element instanceof Connection) detachConnectionFromNodes((Connection) element);				
//		else if (element instanceof Node) detachNodeConnections((Node)element);
				
		// delete
		if(element instanceof BaseConnection || element instanceof ClassElement) 
		{
			if(element.getParent() instanceof StructureDiagram){
				element.getParent().removeChild(element);
				element.getParent().invalidate();
			}
		}
		
	}
	
	private void undoFromDiagram (ParentChildRelation relation)
	{
//		System.out.println("undoing from diagram="+relation.element);
//		if (relation.element instanceof Connection) reattachConnectionToNodes((Connection) relation.element);
//		else if (relation.element instanceof Node) reattachNodeConnections((Node) relation.element);		
		
		if(relation.element instanceof BaseConnection || relation.element instanceof ClassElement) 
		{
			if(relation.element.getParent() instanceof StructureDiagram){
				relation.element.getParent().addChild(relation.element);
				relation.element.getParent().invalidate();
			}			
		}				
	}
	
	private void runDeleteFromModel(Collection<Element> elemList)
	{	
		//delete first the derivations, comments and constraints (third level of dependence)
		for(Element elem: elemList) {
			if (elem instanceof RefOntoUML.Derivation)  delete(elem); 
			if (elem instanceof RefOntoUML.Comment) delete(elem);
			if (elem instanceof RefOntoUML.Constraintx) delete(elem);					
		}				
		
		// then generalization sets (third level of dependence)
		for(Element elem: elemList) {			
			if (elem instanceof RefOntoUML.GeneralizationSet) deleteGeneralizationSet((GeneralizationSet)elem);			
		}
						
		//then the rest of associations and generalizations (second level of dependence)
		for(Element elem: elemList) {				
			if (elem instanceof RefOntoUML.Association) delete(elem);
			if (elem instanceof RefOntoUML.Generalization) deleteGeneralization((Generalization)elem);
		}
		
		//then the classes and datatypes (first level of dependence)
		for(Element elem: elemList) {
			if (elem instanceof RefOntoUML.Class || elem instanceof RefOntoUML.DataType) delete(elem);			
		}		
	}
	
	private void runUndoFromModel(Collection<Element> elemList)
	{
		// classes and datatypes (first level of dependence)
		for(Element elem: elemList) {
			if (elem instanceof RefOntoUML.Class || elem instanceof RefOntoUML.DataType) undo(elem);			
		}
		
		//the rest of associations and generalizations (second level of dependence)
		for(Element elem: elemList) {				
			if ((elem instanceof RefOntoUML.Association) && !(elem instanceof RefOntoUML.Derivation)) undo(elem);
			if (elem instanceof RefOntoUML.Generalization) undoGeneralization((Generalization)elem);
		}

		// generalization sets (third level of dependence)
		for(Element elem: elemList) {			
			if (elem instanceof RefOntoUML.GeneralizationSet) {			
				undoGeneralizationSet((GeneralizationSet)elem);			
			}
		}
		
		//derivations, comments and constraints (third level of dependence)
		for(Element elem: elemList) {
			if (elem instanceof RefOntoUML.Derivation)  undo(elem); 
			if (elem instanceof RefOntoUML.Comment) undo(elem);
			if (elem instanceof RefOntoUML.Constraintx) undo(elem);					
		}				
	}
		
	// deleted generalization and its dependent generalization sets
	private HashMap<GeneralizationSet, Generalization> decoupledGenMap = new HashMap<GeneralizationSet,Generalization>();
	// deleted generalization sets and its dependent generalizations
	private HashMap<Generalization, GeneralizationSet> decoupledGenSetMap = new HashMap<Generalization,GeneralizationSet>();
	// empty generalization sets that needed to be deleted
	private ArrayList<GeneralizationSet> deletedEmptyGenSets = new ArrayList<GeneralizationSet>();
	
	private void deleteGeneralization(Generalization gen)
	{		
		//decouple generalization and its generalization sets		
		for(GeneralizationSet genSet: gen.getGeneralizationSet()) {			
			decoupledGenMap.put(genSet,gen);
		}
		for(GeneralizationSet genSet: decoupledGenMap.keySet()) { 
			genSet.getGeneralization().remove(gen); 
			gen.getGeneralizationSet().remove(genSet); 
		}
		
		//delete remaining empty generalization sets		
		for(GeneralizationSet genSet: decoupledGenMap.keySet()) {
			if(genSet.getGeneralization().size()==0) deletedEmptyGenSets.add(genSet);
		}				
		for(GeneralizationSet genSet: deletedEmptyGenSets) {
			deleteGeneralizationSet(genSet);			
		}
		
		delete(gen);
	}
	
	private void undoGeneralization(Generalization gen)
	{
		undo(gen);
		
		//undo empty generalization sets that were emptied
		ArrayList<GeneralizationSet> genSetsForAddition = new ArrayList<GeneralizationSet>();
		for(GeneralizationSet genSet: deletedEmptyGenSets) {
			if(genSet.getGeneralization().size()==0) genSetsForAddition.add(genSet);
		}	
		for(GeneralizationSet genSet: genSetsForAddition) {
			undoGeneralizationSet(genSet);			
		}
		
		//couple generalization and its generalization sets again
		ArrayList<GeneralizationSet> genSets = new ArrayList<GeneralizationSet>();
		for(GeneralizationSet genSet: decoupledGenMap.keySet()) {			
			genSets.add(genSet);
		}
		for(GeneralizationSet genSet: genSets) { 
			genSet.getGeneralization().add(gen); 
			gen.getGeneralizationSet().add(genSet); 		
		}
	}
	
	private void deleteGeneralizationSet(GeneralizationSet elem)
	{
		//decouple generalization sets and its generalizations before deletion
		for(Generalization gen: ((GeneralizationSet)elem).getGeneralization())
		{				
			if(gen!=null) decoupledGenSetMap.put(gen,elem);			
		}			
		((GeneralizationSet)elem).getGeneralization().removeAll(decoupledGenSetMap.keySet());		
		for(Generalization gen: decoupledGenSetMap.keySet()) {
			gen.getGeneralizationSet().remove(elem);
			UpdateCommander.get().updateFromChange(gen, false);
		}
		
		delete(elem);		
	}
	
	private void undoGeneralizationSet(GeneralizationSet elem)
	{
		undo(elem);
		
		//couple generalization set and its generalizations again
		((GeneralizationSet)elem).getGeneralization().addAll(decoupledGenSetMap.keySet());		
		for(Generalization gen: decoupledGenSetMap.keySet()) {
			gen.getGeneralizationSet().add(elem);
			UpdateCommander.get().updateFromChange(gen, false);
		}
	}
	
	private void undo (RefOntoUML.Element elem)
	{		
//		System.out.println("Undoing from model = "+elem);
		RefOntoUMLEditingDomain.getInstance().createDomain().getCommandStack().undo();
		UpdateCommander.get().updateFromAddition(elem);
	}
	
	private void delete (RefOntoUML.Element elem)
	{			
		//System.out.println("Deleting = "+elem);
		System.out.println("DELETING: "+elem);
		DeleteCommand cmd = (DeleteCommand) DeleteCommand.create(RefOntoUMLEditingDomain.getInstance().createDomain(), elem);
		RefOntoUMLEditingDomain.getInstance().createDomain().getCommandStack().execute(cmd);
		UpdateCommander.get().updateFromDeletion(elem);
	}
	
	public Collection<DiagramElement> getDiagramElements() 
	{
		ArrayList<DiagramElement> list = new ArrayList<DiagramElement>();
		list.addAll(diagramElemList);
		list.addAll(diagramElemDep1List);
		list.addAll(diagramElemDep2List);
		return list;
	}
	
	//===========================================================================
	
	/**
	 * Called when a node is removed. Detach the connections associated with
	 * this node from the other end nodes and remove them from their parents.
	 * 
	 * @param node
	 *            the node that is removed
	 * @return 
	 */
	@SuppressWarnings("unused")
	private Collection<DiagramElement> detachNodeConnections(Node node) 
	{
		ArrayList<DiagramElement> detachedConnections = new ArrayList<>();
		detachedConnections.addAll(node.getConnections());
		
		for (Connection conn : node.getConnections()) 
		{
			if (conn.getNode1() != node)
				if(conn.getNode1()!=null)
					conn.getNode1().removeConnection(conn);
				else
					conn.getConnection1().removeConnection(conn);
			if (conn.getNode2() != node){
				if(conn.getNode2()!=null)
					conn.getNode2().removeConnection(conn);
				else 
					conn.getConnection2().removeConnection(conn);
			}			
			//node.getParent().removeChild(conn);
		}
		
		return detachedConnections;
	}

	/**
	 * Called when a remove node operation is undone. Re-attaches the
	 * connections associated with the specified node to the former other end
	 * nodes and readd them to their parents.
	 * 
	 * @param node
	 *            the node that is readded
	 */
	@SuppressWarnings({ "unused" })
	private void reattachNodeConnections(Node node) 
	{
		for (Connection conn : node.getConnections()) 
		{
			if (conn.getNode1() != node)
				if (conn.getNode1()!=null)
					conn.getNode1().addConnection(conn);
				else
					conn.getConnection1().addConnection(conn);
			if (conn.getNode2() != node){
				if (conn.getNode2()!=null)
					conn.getNode2().addConnection(conn);
				else 
					conn.getConnection2().addConnection(conn);
			}			
		}
	}

	/**
	 * Called when a connection is removed. Detaches the connection from its
	 * associated nodes, but keeps them in the connection to restore them in the
	 * undo operation.
	 * 
	 * @param conn
	 *            the connection that is removed
	 */
	@SuppressWarnings("unused")
	private void detachConnectionFromNodes(Connection conn) 
	{
		if (conn.getNode1()!=null) conn.getNode1().removeConnection(conn);
		else conn.getConnection1().removeConnection(conn);
		if (conn.getNode2()!=null) conn.getNode2().removeConnection(conn);
		else conn.getConnection2().removeConnection(conn);
	}

	/**
	 * Called when a remove connection operation is undone. Reattaches the
	 * connection to its associated nodes.
	 * 
	 * @param conn
	 *            the connection that is readded
	 */
	@SuppressWarnings("unused")
	private void reattachConnectionToNodes(Connection conn) 
	{
		if (conn.getNode1()!=null)
			conn.getNode1().addConnection(conn);
		else
			conn.getConnection1().addConnection(conn);
		if (conn.getNode2()!=null)
			conn.getNode2().addConnection(conn);
		else
			conn.getConnection2().addConnection(conn);
	}
}