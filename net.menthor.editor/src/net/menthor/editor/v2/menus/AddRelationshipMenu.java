package net.menthor.editor.v2.menus;

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

import net.menthor.editor.v2.commands.CommandListener;
import net.menthor.editor.v2.commands.CommandType;
import net.menthor.editor.v2.icon.IconType;
import net.menthor.editor.v2.types.RelationshipType;

public class AddRelationshipMenu extends BaseMenu {

	private static final long serialVersionUID = 3797953970276009760L;
	
	public AddRelationshipMenu(CommandListener listener, String text){
		super(listener, text);	
		build();
	}
	
	public AddRelationshipMenu(CommandListener listener){
		super(listener, "Add Relationship");	
		build();
	}
	
	public void build(){
		createMenuItem(RelationshipType.GENERALIZATION.getName(), IconType.MENTHOR_GENERALIZATION, CommandType.ADD_GENERALIZATION);
		createMenuItem(RelationshipType.CHARACTERIZATION.getName(), IconType.MENTHOR_ASSOCIATION, CommandType.ADD_CHARACTERIZATION);
		createMenuItem(RelationshipType.MEDIATION.getName(), IconType.MENTHOR_ASSOCIATION, CommandType.ADD_MEDIATION);
		createMenuItem(RelationshipType.FORMAL.getName(), IconType.MENTHOR_ASSOCIATION, CommandType.ADD_FORMAL);
		createMenuItem(RelationshipType.MATERIAL.getName(), IconType.MENTHOR_ASSOCIATION, CommandType.ADD_MATERIAL);
		createMenuItem(RelationshipType.DERIVATION.getName(), IconType.MENTHOR_DERIVATION, CommandType.ADD_DERIVATION);
		createMenuItem(RelationshipType.COMPONENTOF.getName(), IconType.MENTHOR_COMPONENTOF, CommandType.ADD_COMPONENTOF);
		createMenuItem(RelationshipType.MEMBEROF.getName(), IconType.MENTHOR_MEMBEROF, CommandType.ADD_MEMBEROF);
		createMenuItem(RelationshipType.SUBCOLLECTIONOF.getName(), IconType.MENTHOR_SUBCOLLECTIONOF, CommandType.ADD_SUBCOLLECTIONOF);
		createMenuItem(RelationshipType.SUBQUANTITYOF.getName(), IconType.MENTHOR_SUBQUANTITYOF, CommandType.ADD_SUBQUANTITYOF);
		createMenuItem(RelationshipType.STRUCTURATION.getName(), IconType.MENTHOR_ASSOCIATION, CommandType.ADD_STRUCTURATION);
		createMenuItem(RelationshipType.ASSOCIATION.getName(), IconType.MENTHOR_ASSOCIATION, CommandType.ADD_ASSOCIATION);
		sort();
	}
}
