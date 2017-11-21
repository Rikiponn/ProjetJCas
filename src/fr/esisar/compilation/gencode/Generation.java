package fr.esisar.compilation.gencode;

import java.util.ArrayList;

import fr.esisar.compilation.global.src.*;
import fr.esisar.compilation.global.src3.*;

/**
 * Génération de code pour un programme JCas à partir d'un arbre décoré.
 */

class Generation {
	public static ArrayList<String> decl;
	public static GestionRegistre reg;
   /**
    * Méthode principale de génération de code.
    * Génère du code pour l'arbre décoré a.
    */
	
   static Prog coder(Arbre a) {
      Prog.ajouterGrosComment("Programme généré par JCasc");
      decl = new ArrayList<String>();
      reg = new GestionRegistre();
      Inst inst;
      // -----------
      // A COMPLETER
      // -----------
      
      coder_Decl(a.getFils1());
      //Réserve de la place pour les variables locales

			//TODO il faut prendre en compte le fait qu'un tableau c'est plus gros, du coup, la bez
      inst = Inst.creation1(Operation.ADDSP, Operande.creationOpEntier(decl.size()));
      Prog.ajouter(inst,"test");
      
      coder_Inst(a.getFils2());
      // L'instruction "new_line"
      // L'instruction "write"
      

      // Fin du programme
      // L'instruction "HALT"
      inst = Inst.creation1(Operation.SUBSP, Operande.creationOpEntier(decl.size()));
      // On ajoute l'instruction à la fin du programme
      Prog.ajouter(inst,"test");
      inst = Inst.creation0(Operation.HALT);
      // On ajoute l'instruction à la fin du programme
      Prog.ajouter(inst,"On arrete le programme");

      // On retourne le programme assembleur généré
      return Prog.instance(); 
   }
   // Remplis une ArrayList qui contient la liste des variables
   private static void coder_Decl(Arbre a){
	   switch(a.getNoeud()){
   		case Vide:
   			break;
   		case ListeDecl:
   			coder_Decl(a.getFils1());
			coder_Decl(a.getFils2());
   			break;
   		case Decl :
   			create_Variables(a.getFils2(),get_Idents(a.getFils1()));
   			break;
   		default :
   			break;
	   }
   }
   
   /**
    * L'arbre a étant un Tableau, un intervalle ou un Ident, créé autant de variables que d(éléments)*(les identifiants stockés dans idents) 
    * @param a le sous-arbre
    * @param idents un array d'idents qui doivent être déclarés avec le type de a
    */
   private static void create_Variables(Arbre a, ArrayList<String> idents) {
	   switch(a.getNoeud()) {
	   case Ident :
	   case Intervalle :
		   for(int i=0;i<idents.size();i++) {
			   decl.add(idents.get(i));
		   }
		   break;
	   case Tableau :
		   ArrayList<String> identsPlus = new ArrayList<String>();
		   for(int i=0;i<idents.size();i++) {
			   for(int j=a.getFils1().getFils1().getEntier();j<=a.getFils1().getFils2().getEntier();j++) {
				   identsPlus.add(idents.get(i)+"["+Integer.toString(j)+"]");
				   create_Variables(a.getFils2(),identsPlus);
			   }
		   }
		   break;
	   default :
		   break;
	   }
   }
   
   /**
    * Etant donné une liste d'ident a, renvoie un arrayList contenant l'ensemble des ident de cette liste.
    * @param a une Liste_Ident
    * @return liste de String
    */
   private static ArrayList<String> get_Idents(Arbre a) {
	   ArrayList<String> liste = new ArrayList<String>();
	   switch(a.getNoeud()) {
	   case ListeIdent :
		   liste = get_Idents(a.getFils1());
		   liste.add(a.getFils2().getChaine());
		   break;
	   case Ident :
		   liste.add(a.getChaine());
		   break;
		default :
			break;
	   }
	   return liste;
   }
   
   private static void coder_Inst(Arbre a){
	   Inst inst;
	   switch(a.getNoeud()){
	   case Ecriture:
		 //Attention, le fils d'un Noeud Ecriture est un Noeud.ListeExp qui contient lui même des Noeud.ListeExp
		   coder_Ecriture(a.getFils1());
		   break;
		   
	   case Ligne:
		   inst = Inst.creation0(Operation.WNL);
		   Prog.ajouter(inst, "new line");
		   break; 
	   case ListeInst:
		   coder_Inst(a.getFils1());
		   coder_Inst(a.getFils2());
		   break;
	   }
   }
   
   //TODO voir le cas où on passe dans le premier else, ça me semble ambiguë ma merde
   private static void coder_Ecriture(Arbre a){
	   Registre r = null;

	   //On descend dans l'arbre
	   Arbre f1 = a.getFils1();
	   Arbre f2 = a.getFils2();
	   
	   //Si le fils 1 est un Liste exp, on fais un appel recursif pour descendre
	   if(f1.equals(Noeud.ListeExp)){
		   coder_Ecriture(f1);
	   }
	   //Dans tout les cas, le fils 2 est une Expression, donc on l'écrit
	   
	   //Si on veut écrire un integer
	   if(f2.getDecor().getType().equals(Type.Integer)){
		   Operande op = coder_EXP(f2);
		   //Si R1 est occupé
		   if(!GestionRegistre.estRegistreLibre(1)){
			   //On le déplace dans un registre libre (et on le met comme occupé) ou on le met en pile 
			   r = GestionRegistre.deplaceRegistre(Registre.R1);
		   }
		   Inst inst = Inst.creation2(Operation.LOAD,op,Operande.R1);
		   Prog.ajouter(inst,"Chargement dans R1 d'un entier");
		   inst = Inst.creation0(Operation.WINT);
		   Prog.ajouter(inst,"Ecriture de l'entier");
		   
		   //Si r = R1 , on a placé le registre précédent en pile, on le replace donc dans R1
		   if(r.equals(Registre.R1)){
			   inst = Inst.creation1(Operation.POP, Operande.R1);
			   Prog.ajouter(inst,"Registre retablis depuis la pile après écriture");
		   }
		   //Si r = Rm (on a changé sa valeur) et r != R1 (pour éviter le faire un LOAD R1 R1) , on rétablit le registre dans R1
		   else{
			   if(r != null){
				   inst = Inst.creation2(Operation.LOAD, Operande.opDirect(r), Operande.R1);
				   GestionRegistre.libererRegistre(r);
				   Prog.ajouter(inst,"Registre retablis depuis "+r+" après écriture");
			   }
		   }
		   //On libère R1
		   GestionRegistre.libererRegistre(1);
	   }
	   
	   //Si on veut écrire un real
	   if(f2.getDecor().getType().equals(Type.Real)){
		   //Si R1 est occupé
		   Operande op = coder_EXP(f2);
		   if(!GestionRegistre.estRegistreLibre(1)){		    
			   r = GestionRegistre.deplaceRegistre(Registre.R1);
		   }
		   Inst inst = Inst.creation2(Operation.LOAD,op,Operande.R1);
		   Prog.ajouter(inst,"Chargement dans R1 d'un réel");
		   inst = Inst.creation0(Operation.WFLOAT);
		   Prog.ajouter(inst,"Ecriture d'un réel" );
		   
		   //Si r = R1 , on a placé le registre précédent en pile, on le replace donc dans R1
		   if(r.equals(Registre.R1)){
			   inst = Inst.creation1(Operation.POP, Operande.R1);
			   Prog.ajouter(inst,"Registre retablis depuis la pile après écriture");
		   }
		   //Si r = Rm (on a changé sa valeur) et r != R1 (pour éviter le faire un LOAD R1 R1) , on rétablit le registre dans R1
		   else{
			   if(r != null){
				   inst = Inst.creation2(Operation.LOAD, Operande.opDirect(r), Operande.R1);
				   GestionRegistre.libererRegistre(r);
				   Prog.ajouter(inst,"Registre retablis depuis "+r+" après écriture");
			   }
		   }
		   //On libère R1
		   GestionRegistre.libererRegistre(1);
	   }
	   
	   //Si on veut écrire un string
	   if(f2.getDecor().getType().equals(Type.String)){
		   Inst inst = Inst.creation1(Operation.WSTR,Operande.creationOpChaine(f2.getChaine()));
		   Prog.ajouter(inst,"Ecriture du string : "+f2.getChaine());
	   }
   }
   
   private static Operande coder_EXP(Arbre a){
	   Inst inst;
	   //chercher en fonction du nom de la variable, sa position en pile,PUIS la charger dans un registre libre
	   //Il faut donc utiliser la fonction getGrosRegistreLibre()
	   //Il faut ensuite modifier le tableau regTab pour indiquer les registres qui ne sont plus disponible
	   //
	   
	   if(a.getArite()==1){
		   
	   		String varName;
  			int placeEnPile;
  			Operande registreLibre;
  			
		   switch(a.getNoeud()){
		   	case Non:
	   			registreLibre = GestionRegistre.getFreeRegToOpTab();
	   			if(a.getFils1().getNoeud()==Noeud.Ident){

	   				varName = a.getFils1().getChaine();
		   			placeEnPile = decl.indexOf(varName);
		   			
		   			if(a.getFils1().getChaine()=="true"){
		   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(0), registreLibre);
		   				Prog.ajouter(loadInst, "Chargement de la variable booleenne false (0) dans le registre " + registreLibre.getRegistre().toString());
		   				return registreLibre;
		   			}
		   			else if(a.getFils1().getChaine()=="false"){
		   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(1), registreLibre);
		   				Prog.ajouter(loadInst, "Chargement de la variable booleenne true (1) dans le registre " + registreLibre.getRegistre().toString());
		   				return registreLibre;
		   			}
		   			else{ //TODO il faut agir en fonction de la valeur dans le registre
		   				if(a.getFils1().getDecor().getType() == Type.Boolean){
				   			Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), registreLibre);
				   			Prog.ajouter(loadInst, "Chargement de la variable booleenne dans le registre " + registreLibre.getRegistre().toString());		   				
			   			}
		   			}
		   			
		   			
	   			}
	   			
		   		
		   		break;
		   	
		   	case PlusUnaire:
	   			registreLibre = GestionRegistre.getFreeRegToOpTab();
	   			
		   		if(a.getFils1().getNoeud()==Noeud.Ident){
		   			
		   			varName = a.getFils1().getChaine();
		   			placeEnPile = decl.indexOf(varName);
		   			
			   		if(a.getFils1().getDecor().getType()==Type.Integer || a.getFils1().getDecor().getType()==Type.Real){
			   			Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), registreLibre);
			   			Prog.ajouter(loadInst, "Chargement de la variable dans le registre " + registreLibre.getRegistre().toString());
			   		}
		   		}
		   		else{
		   			if(a.getFils1().getDecor().getType()==Type.Integer){
			   			Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(a.getFils1().getEntier()), registreLibre);
			   			Prog.ajouter(loadInst, "Chargement de la valeur entiere dans le registre " + registreLibre.getRegistre().toString());
		   			}
		   			else if(a.getFils1().getDecor().getType()==Type.Real){
			   			Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils1().getEntier()), registreLibre);
			   			Prog.ajouter(loadInst, "Chargement de la valeur reelle dans le registre " + registreLibre.getRegistre().toString());
		   			}
		   		}
		   		return registreLibre;
		   	
		   	case MoinsUnaire:
	   			registreLibre = GestionRegistre.getFreeRegToOpTab();
	   			
		   		if(a.getFils1().getNoeud()==Noeud.Ident){
		   			
			   		varName = a.getFils1().getChaine();
		   			placeEnPile = decl.indexOf(varName);
		   			
			   		if(a.getFils1().getDecor().getType()==Type.Integer || a.getFils1().getDecor().getType()==Type.Real){
			   			Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), registreLibre);
			   			Inst moinsUnaire = Inst.creation2(Operation.MUL, Operande.creationOpEntier(-1), registreLibre);
			   			Prog.ajouter(loadInst, "Chargement de la variable dans le registre " + registreLibre.getRegistre().toString());
			   			Prog.ajouter(moinsUnaire, "Operation moins unaire et resultat mis dans le registre " + registreLibre.getRegistre().toString() );
			   		}
		   		}
		   		else{
		   			if(a.getFils1().getDecor().getType()==Type.Integer){
			   			Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(a.getFils1().getEntier()), registreLibre);
		   				Inst moinsUnaire = Inst.creation2(Operation.MUL, Operande.creationOpEntier(-1), Operande.creationOpEntier(a.getFils1().getEntier()));
			   			Prog.ajouter(loadInst, "Chargement de la valeur entiere dans le registre " + registreLibre.getRegistre().toString());
			   			Prog.ajouter(moinsUnaire, "Operation moins unaire et resultat mis dans le registre " + registreLibre.getRegistre().toString());
		   			}
		   			else if(a.getFils1().getDecor().getType()==Type.Real){
			   			Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils1().getEntier()), registreLibre);
		   				Inst moinsUnaire = Inst.creation2(Operation.MUL, Operande.creationOpEntier(-1), Operande.creationOpReel(a.getFils1().getReel()));
			   			Prog.ajouter(loadInst, "Chargement de la valeur reelle dans le registre " + registreLibre.getRegistre().toString());
			   			Prog.ajouter(moinsUnaire, "Operation moins unaire et resultat mis dans le registre " + registreLibre.getRegistre().toString());
		   			}
		   			
		   		}
	   			return registreLibre;
		   
		   }
	   }
	   if(a.getArite()==2){
		   switch(a.getNoeud()){
		   	case Et :
		   	case Mult :
		   		// Il faudrait faire en sorte que si la valeur de a.getFils1() est à faux ou 0, ne pas évaluer le deuxième et juste mettre faux ou à 0 dans un registre 
		   		inst = Inst.creation2(Operation.MUL, coder_EXP(a.getFils1()), coder_EXP(a.getFils2()));
		   		break;
		   		
	   		case Ou :
	   		case Plus :
	   			// Il faudrait faire en sorte que si la valeur de a.getFils1() est à vrai, ne pas évaluer le deuxième et juste mettre vrai (pour le OU) dans un registre 
	   			inst = Inst.creation2(Operation.ADD, coder_EXP(a.getFils1()), coder_EXP(a.getFils2()));
		   		break;
		   		
	   		case Moins :
	   			inst = Inst.creation2(Operation.SUB, coder_EXP(a.getFils1()), coder_EXP(a.getFils2()));
		   		break;
		   		
	   		case DivReel :
	   		case Quotient :
	   			inst = Inst.creation2(Operation.DIV, coder_EXP(a.getFils1()), coder_EXP(a.getFils2()));
		   		break;	
		   		
	   		case Egal :
	   		case InfEgal :
	   		case Inf :
	   		case SupEgal :
	   		case Sup :
	   		case NonEgal :
	   			inst = Inst.creation2(Operation.CMP, coder_EXP(a.getFils1()), coder_EXP(a.getFils2()));
		   		break;
		   }
	   }
	   if(a.getArite()==3){
		   switch(a.getNoeud()){
		   }
	   }
	   
	   
	   
	   return Operande.R0;
   }
   
}


