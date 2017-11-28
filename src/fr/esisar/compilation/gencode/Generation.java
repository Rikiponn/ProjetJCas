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
      GestionRegistre.initRegTab();
      Inst inst;
      // -----------
      // A COMPLETER
      // -----------
      
      coder_Decl(a.getFils1());
      //Réserve de la place pour les variables locales

			//TODO il faut prendre en compte le fait qu'un tableau c'est plus gros, du coup, la bez
      inst = Inst.creation1(Operation.ADDSP, Operande.creationOpEntier(decl.size()));
      Prog.ajouter(inst,"Réservation en pile des variables locales");
      
      coder_Inst(a.getFils2());
      // L'instruction "new_line"
      // L'instruction "write"
      


      inst = Inst.creation1(Operation.SUBSP, Operande.creationOpEntier(decl.size()));
      // On ajoute l'instruction à la fin du programme
      Prog.ajouter(inst,"test");
      // Fin du programme
      // L'instruction "HALT"
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
	   case Lecture :
		   coder_Lecture(a.getFils1());
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
   
   /**Fonction s'occupant de l'instruction write
    * Etat : terminé
    * @param un arbre
    * @return void
    */
   private static void coder_Ecriture(Arbre a){
	   Registre r = null;

	   //On descend dans l'arbre
	   Arbre f1 = a.getFils1();
	   Arbre f2 = a.getFils2();
	   
	   //Si le fils 1 est un Liste exp, on fais un appel recursif pour descendre
	   if(f1.getNoeud().equals(Noeud.ListeExp)){
		   coder_Ecriture(f1);
	   }
	   //Dans tous les cas, le fils 2 est une Expression, donc on l'écrit
	   
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
			   GestionRegistre.popPile(Registre.R1);
			 //On s'assure que R1 reste occupé car il y avait qqch dedans avant l'appel de cette fonction
			   GestionRegistre.occuperRegistre(Registre.R1);
		   }
		   //Si r = Rm (on a changé sa valeur) et r != R1 (pour éviter le faire un LOAD R1 R1) , on rétablit le registre dans R1
		   else{
			   if(r != null){
				   inst = Inst.creation2(Operation.LOAD, Operande.opDirect(r), Operande.R1);
				   Prog.ajouter(inst,"Registre retablis depuis "+r+" après écriture");
				   GestionRegistre.libererRegistre(r);
				   //On s'assure que R1 reste occupé car il y avait qqch dedans avant l'appel de cette fonction
				   GestionRegistre.occuperRegistre(Registre.R1);
			   }
		   }
	   }
	   
	   //Si on veut écrire un real
	   if(f2.getDecor().getType().equals(Type.Real)){
		   Operande op = coder_EXP(f2);
		   //Si R1 est occupé
		   if(!GestionRegistre.estRegistreLibre(1)){	
			 //On le déplace dans un registre libre (et on le met comme occupé) ou on le met en pile 
			   r = GestionRegistre.deplaceRegistre(Registre.R1);
		   }
		   Inst inst = Inst.creation2(Operation.LOAD,op,Operande.R1);
		   Prog.ajouter(inst,"Chargement dans R1 d'un réel");
		   inst = Inst.creation0(Operation.WFLOAT);
		   Prog.ajouter(inst,"Ecriture d'un réel" );
		   
		   //Si r = R1 , on a placé le registre précédent en pile, on le replace donc dans R1
		   if(r.equals(Registre.R1)){
			   GestionRegistre.popPile(Registre.R1);
			 //On s'assure que R1 reste occupé car il y avait qqch dedans avant l'appel de cette fonction
			   GestionRegistre.occuperRegistre(Registre.R1);
		   }
		   //Si r = Rm (on a changé sa valeur) et r != R1 (pour éviter le faire un LOAD R1 R1) , on rétablit le registre dans R1
		   else{
			   if(r != null){
				   inst = Inst.creation2(Operation.LOAD, Operande.opDirect(r), Operande.R1);
				   Prog.ajouter(inst,"Registre retablis depuis "+r+" après écriture");
				   GestionRegistre.libererRegistre(r);
				 //On s'assure que R1 reste occupé car il y avait qqch dedans avant l'appel de cette fonction
				   GestionRegistre.occuperRegistre(Registre.R1);
			   }
		   }  
	   }
	   
	   //Si on veut écrire un string
	   if(f2.getDecor().getType().equals(Type.String)){
		   Inst inst = Inst.creation1(Operation.WSTR,Operande.creationOpChaine(f2.getChaine()));
		   Prog.ajouter(inst,"Ecriture du string : "+f2.getChaine());
	   }
   }
   
   /**Fonction s'occupant de  l'instruction read
    * Etat : en cours
    * @return void
    * @param a (Un Noeud.Ident)
    */
   //TODO finir SEB (verif libre + test + tablal + restore)
   private static void coder_Lecture(Arbre a) {
		//Le fils d'un Noeud Lecture est forcément un Noeud Ident de type Integer ou Reel
		//On vérifie si R1 est libre
	   
	   
	   	//On lit soit un entier, soit un réel
	   	if(a.getDecor().getType().getNature().equals(NatureType.Interval)){
			Inst inst = Inst.creation0(Operation.RINT);
			Prog.ajouter(inst, "Lecture d'un entier");
		}
		else{
			Inst inst = Inst.creation0(Operation.RFLOAT);
			Prog.ajouter(inst, "Lecture d'un flotant");
		}
	    // On test si R1 possède une valeur correcte (pour les intervalles)
	   
	   	//On le replace en pile
	   	if(!a.getFils1().getDecor().getType().equals(NatureType.Array)){
		   String varName = a.getFils1().getChaine();
			int placeEnPile = decl.indexOf(varName);
			Inst inst = Inst.creation2(Operation.STORE, Operande.R1, Operande.creationOpIndirect(placeEnPile, Registre.GB));
	   	}else{
	   		//TODO pour les tablals quand ce sera corrigé 
	   	}
	   	
	   	//On restore les états des registres si besoin
   }
  
   
   /**
    * A étant un Noeud.Index, charge la zone mémoire pointée par a dans un registre et le renvoie
    * @param a un Noeud.Index
    * @return Un opérande contenant la valeur pointée par a
    */
   private static Operande load_Index(Arbre a) {
	   Operande registreLibre = GestionRegistre.getFreeRegToOpTab();
	   a.getDecor().getType().getBorneInf();
	   a.getDecor().getType().getBorneSup();
	   load_Index(a.getFils1());
	   coder_EXP(a.getFils2());
	   
	   return registreLibre;
   }
   /* TODO A TERMINER TIMOTHEE
   private static Operande getSubIndex(Arbre a) {
	   if(a.getNoeud().equals(Noeud.Index)) {
		   int len = getLength(a.getFils1());
		   Operande subexp = getSubIndex(a.getFils1());
		   Operande exp = coder_EXP(a.getFils2());
		   Inst.creation2(Operation.MULTipl, Operande.creationOpEntier(0), registreLibre);
	   }
	   else {
		   return GestionRegistre.getFreeRegToOpTab();
	   }
   }*/
   
   private static int getLength(Arbre a) {
	   if(a.getNoeud().equals(Noeud.Index)) {
		   return((a.getDecor().getType().getBorneSup() - a.getDecor().getType().getBorneInf() + 1)*getLength(a.getFils1()));
	   }
	   else {
		   return 1;
	   }
   }
   
   private static String getIdent(Arbre a) {
	   if(a.getNoeud().equals(Noeud.Ident)) {
		   return a.getChaine();
	   }
	   else {
		   return getIdent(a.getFils1());
	   }
   }
   
   private static Operande coder_EXP(Arbre a){
	   Inst inst;
	   //chercher en fonction du nom de la variable, sa position en pile,PUIS la charger dans un registre libre
	   //Il faut donc utiliser la fonction getGrosRegistreLibre()
	   //Il faut ensuite modifier le tableau regTab pour indiquer les registres qui ne sont plus disponible
	   //
	   
	   if(a.getArite() == 0){
		   switch(a.getNoeud()){
		   case Entier:
			   return Operande.creationOpEntier(a.getEntier());
		   case Reel:
			   return Operande.creationOpReel(a.getReel());
		   }
	   }
	   
	   if(a.getArite()==1){
		   
	   		String varName;
  			int placeEnPile;
  			Operande registreLibre;
  			
		   switch(a.getNoeud()){
		   	
		   	
		   	case PlusUnaire:
	   			return coder_EXP(a.getFils1());	   			
	   			
		   	case Non:
		   	case MoinsUnaire:
	   			registreLibre = GestionRegistre.getFreeRegToOpTab();
	   			
		   		if(a.getFils1().getNoeud()==Noeud.Ident){
		   			
			   		varName = a.getFils1().getChaine();
		   			placeEnPile = decl.indexOf(varName);
		   			
		   			if(a.getFils1().getChaine()=="true"){
		   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(-1), registreLibre);
		   				Prog.ajouter(loadInst, "Chargement de la variable booleenne false (-1) dans le registre " + registreLibre.getRegistre().toString());
		   				return registreLibre;
		   			}
		   			else if(a.getFils1().getChaine()=="false"){
		   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(1), registreLibre);
		   				Prog.ajouter(loadInst, "Chargement de la variable booleenne true (1) dans le registre " + registreLibre.getRegistre().toString());
		   				return registreLibre;
		   			}else{
		   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), registreLibre);
			   			Inst moinsUnaire = Inst.creation2(Operation.MUL, Operande.creationOpEntier(-1), registreLibre);
			   			Prog.ajouter(loadInst, "Chargement de la variable dans le registre " + registreLibre.getRegistre().toString());
			   			Prog.ajouter(moinsUnaire, "Operation moins unaire et resultat mis dans le registre " + registreLibre.getRegistre().toString() );
		   			}
			 
		   		}
		   		else{
		   			Operande reg = coder_EXP(a.getFils1());
		   			GestionRegistre.libererRegistre(reg.getRegistre());
	   				Inst loadInst = Inst.creation2(Operation.LOAD, reg, registreLibre);
	   				Inst moinsUnaire = Inst.creation2(Operation.MUL, Operande.creationOpEntier(-1), registreLibre);
		   			Prog.ajouter(loadInst, "Chargement de la valeur reelle dans le registre " + registreLibre.getRegistre().toString());
		   			Prog.ajouter(moinsUnaire, "Operation moins unaire et resultat mis dans le registre " + registreLibre.getRegistre().toString());
		   		}
	   			return registreLibre;
		   
		   }
	   }
	   
	   
	   if(a.getArite()==2){
		   
		   String varName;
		   int placeEnPile;
		   Operande reg1;
		   Operande reg2;
		   
		   switch(a.getNoeud()){
		   	case Mult :	
		   		varName = a.getFils1().getChaine();
	   			placeEnPile = decl.indexOf(varName);
		   		reg1 = GestionRegistre.getFreeRegToOpTab();
		   		reg2 = GestionRegistre.getFreeRegToOpTab();
		   		
		   		if(a.getFils1().getNoeud()==Noeud.Ident){
		   			Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg1);
		   			Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre().toString());
		   		}
		   		else if(a.getFils1().getDecor().getType() == Type.Integer){
		   			Inst loadFils1Inst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(a.getEntier()), reg1);
		   			Prog.ajouter(loadFils1Inst, "Ajout de l'entier operande gauche pour la mult");
		   		} else if(a.getFils1().getDecor().getType() == Type.Real){
		   			Inst loadFils1Inst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getReel()), reg1);
		   			Prog.ajouter(loadFils1Inst, "Ajout de l'entier operande gauche pour la mult");
		   		}
		   		else reg1 = coder_EXP(a.getFils1());
		   		
		   		if(a.getFils2().getNoeud()==Noeud.Ident){
		   			Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg2);
		   			Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre().toString());
		   		}
		   		else if(a.getFils2().getDecor().getType() == Type.Integer){
		   			Inst loadFils2Inst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(a.getEntier()), reg2);
		   			Prog.ajouter(loadFils2Inst, "Ajout de l'entier operande droit pour la mult");
		   		} else if(a.getFils2().getDecor().getType() == Type.Real){
		   			Inst loadFils2Inst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getReel()), reg2);
		   			Prog.ajouter(loadFils2Inst, "Ajout de l'entier operande droit pour la mult");
		   		}
		   		else reg2 = coder_EXP(a.getFils2());
		   		Inst multInst = Inst.creation2(Operation.MUL, reg1, reg2);
		   		Prog.ajouter(multInst, "Ajout de l'instruction multiplication");
		   		return reg2;
 
	   		case Plus :
	   			varName = a.getFils1().getChaine();
	   			placeEnPile = decl.indexOf(varName);
		   		reg1 = GestionRegistre.getFreeRegToOpTab();
		   		reg2 = GestionRegistre.getFreeRegToOpTab();
		   		
		   		if(a.getFils1().getNoeud()==Noeud.Ident){
		   			Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg1);
		   			Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre().toString());
		   		}
		   		else if(a.getFils1().getDecor().getType() == Type.Integer){
		   			Inst loadFils1Inst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(a.getEntier()), reg1);
		   			Prog.ajouter(loadFils1Inst, "Ajout de l'entier operande gauche pour la mult");
		   		} else if(a.getFils1().getDecor().getType() == Type.Real){
		   			Inst loadFils1Inst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getReel()), reg1);
		   			Prog.ajouter(loadFils1Inst, "Ajout de l'entier operande gauche pour la mult");
		   		}
		   		else reg1 = coder_EXP(a.getFils1());
		   		
		   		if(a.getFils2().getNoeud()==Noeud.Ident){
		   			Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg2);
		   			Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre().toString());
		   		}
		   		else if(a.getFils2().getDecor().getType() == Type.Integer){
		   			Inst loadFils2Inst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(a.getEntier()), reg2);
		   			Prog.ajouter(loadFils2Inst, "Ajout de l'entier operande droit pour la mult");
		   		} else if(a.getFils2().getDecor().getType() == Type.Real){
		   			Inst loadFils2Inst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getReel()), reg2);
		   			Prog.ajouter(loadFils2Inst, "Ajout de l'entier operande droit pour la mult");
		   		}
		   		else reg2 = coder_EXP(a.getFils2());
		   		Inst addInst = Inst.creation2(Operation.ADD, reg1, reg2);
		   		Prog.ajouter(addInst, "Ajout de l'instruction addition");
		   		return reg2;
		   		
	   		case Moins :
	   			varName = a.getFils1().getChaine();
	   			placeEnPile = decl.indexOf(varName);
		   		reg1 = GestionRegistre.getFreeRegToOpTab();
		   		reg2 = GestionRegistre.getFreeRegToOpTab();
		   		
		   		if(a.getFils1().getNoeud()==Noeud.Ident){
		   			Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg1);
		   			Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre().toString());
		   		}
		   		else if(a.getFils1().getDecor().getType() == Type.Integer){
		   			Inst loadFils1Inst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(a.getEntier()), reg1);
		   			Prog.ajouter(loadFils1Inst, "Ajout de l'entier operande gauche pour la mult");
		   		} else if(a.getFils1().getDecor().getType() == Type.Real){
		   			Inst loadFils1Inst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getReel()), reg1);
		   			Prog.ajouter(loadFils1Inst, "Ajout de l'entier operande gauche pour la mult");
		   		}
		   		else reg1 = coder_EXP(a.getFils1());
		   		
		   		if(a.getFils2().getNoeud()==Noeud.Ident){
		   			Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg2);
		   			Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre().toString());
		   		}
		   		else if(a.getFils2().getDecor().getType() == Type.Integer){
		   			Inst loadFils2Inst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(a.getEntier()), reg2);
		   			Prog.ajouter(loadFils2Inst, "Ajout de l'entier operande droit pour la mult");
		   		} else if(a.getFils2().getDecor().getType() == Type.Real){
		   			Inst loadFils2Inst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getReel()), reg2);
		   			Prog.ajouter(loadFils2Inst, "Ajout de l'entier operande droit pour la mult");
		   		}
		   		else reg2 = coder_EXP(a.getFils2());
		   		Inst subInst = Inst.creation2(Operation.SUB, reg1, reg2);
		   		Prog.ajouter(subInst, "Ajout de l'instruction soustraction");
		   		return reg2;

	   		case Quotient :
	   		case DivReel :
	   			varName = a.getFils1().getChaine();
	   			placeEnPile = decl.indexOf(varName);
		   		reg1 = GestionRegistre.getFreeRegToOpTab();
		   		reg2 = GestionRegistre.getFreeRegToOpTab();
		   		
		   		if(a.getFils1().getNoeud()==Noeud.Ident){
		   			Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg1);
		   			Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre().toString());
		   		}
		   		else if(a.getFils1().getDecor().getType() == Type.Integer){
		   			Inst loadFils1Inst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(a.getEntier()), reg1);
		   			Prog.ajouter(loadFils1Inst, "Ajout de l'entier operande gauche pour la mult");
		   		} else if(a.getFils1().getDecor().getType() == Type.Real){
		   			Inst loadFils1Inst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getReel()), reg1);
		   			Prog.ajouter(loadFils1Inst, "Ajout de l'entier operande gauche pour la mult");
		   		}
		   		else reg1 = coder_EXP(a.getFils1());
		   		
		   		if(a.getFils2().getNoeud()==Noeud.Ident){
		   			Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg2);
		   			Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre().toString());
		   		}
		   		else if(a.getFils2().getDecor().getType() == Type.Integer){
		   			Inst loadFils2Inst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(a.getEntier()), reg2);
		   			Prog.ajouter(loadFils2Inst, "Ajout de l'entier operande droit pour la mult");
		   		} else if(a.getFils2().getDecor().getType() == Type.Real){
		   			Inst loadFils2Inst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getReel()), reg2);
		   			Prog.ajouter(loadFils2Inst, "Ajout de l'entier operande droit pour la mult");
		   		}
		   		else reg2 = coder_EXP(a.getFils2());
		   		Inst divRInst = Inst.creation2(Operation.DIV, reg1, reg2);
		   		Prog.ajouter(divRInst, "Ajout de l'instruction division");
		   		return reg2;

            case Et :// Il faudrait faire en sorte que si la valeur de a.getFils1() est à faux ou 0, ne pas évaluer le deuxième et juste mettre faux ou à 0 dans un registre
                //Tu ne peux faire celà que quand tu es face à un ident, si le fils1 est un et, ou... tu ne peux pas faire un getchaine dessus
            	varName = a.getFils1().getChaine();
            	//De plus, si ton fils1 est un true ou un false (c'est donc bien un ident) tu ne peux pas aller le cherche en pile car il n'y sera pas
                placeEnPile = decl.indexOf(varName);
                reg1 = GestionRegistre.getFreeRegToOpTab();
                reg2 = GestionRegistre.getFreeRegToOpTab();
                boolean forcementFaux = false;
                if(a.getFils1().getNoeud() == Noeud.Ident){
                    if(a.getFils1().getChaine()=="true"){
                        Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(1), reg1);
                        Prog.ajouter(loadInst, "Chargement de la variable booleenne true (1) dans le registre " + reg1.getRegistre().toString());
                    }
                    else if(a.getFils1().getChaine() == "false"){
                        Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(-1), reg1);
                        Prog.ajouter(loadInst, "Chargement de la variable booleenne false (-1) dans le registre " + reg1.getRegistre().toString());
                        forcementFaux = true;
                    }
                    else{
                        Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg1);
                        Prog.ajouter(loadInst, "Chargement de la variable booleenne dans le registre " + reg1.getRegistre().toString());                            
                    }
                }
                else{//TODO completer
                    Operande reg = coder_EXP(a.getFils1());
                    /*Pas utile de juste déplacer un registre vers un autre
                     * GestionRegistre.libererRegistre(reg.getRegistre());
                    Inst loadInst = Inst.creation2(Operation.LOAD, reg, reg1);
                    Prog.ajouter(loadInst, "Chargement de la valeur reelle dans le registre " + reg1.getRegistre().toString());*/
                }

	   		case Ou :// Il faudrait faire en sorte que si la valeur de a.getFils1() est à vrai, ne pas évaluer le deuxième et juste mettre vrai (pour le OU) dans un registre
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


