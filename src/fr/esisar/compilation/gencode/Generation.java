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
	
	static int nbEtiq = 0;
   /**
    * Méthode principale de génération de code.
    * Génère du code pour l'arbre décoré a.
    */
	
   static Prog coder(Arbre a) {
      Prog.ajouterGrosComment("Programme généré par JCasc");
      decl = new ArrayList<String>();
      GestionRegistre.initRegTab();
      Inst inst;
      
      coder_Decl(a.getFils1());
      //Réserve de la place pour les variables locales
      inst = Inst.creation1(Operation.ADDSP, Operande.creationOpEntier(decl.size()));
      Prog.ajouter(inst,"Réservation en pile des variables locales");
      
      coder_Inst(a.getFils2());
      


      inst = Inst.creation1(Operation.SUBSP, Operande.creationOpEntier(decl.size()));
      // On ajoute l'instruction à la fin du programme
      Prog.ajouter(inst,"test");
      // Fin du programme
      // L'instruction "HALT"
      inst = Inst.creation0(Operation.HALT);
      // On ajoute l'instruction à la fin du programme
      Prog.ajouter(Etiq.nouvelle(new String("Halt")));
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
	   case Affect:
           Operande opdroite = coder_EXP(a.getFils2());
           switch(a.getFils1().getNoeud()) {
           case Ident:
               int placeEnPile = -1;
               if((placeEnPile = decl.indexOf(a.getFils1().getChaine()) + 1) == -1) {
            	   System.exit(0);;
               }
               inst = Inst.creation2(Operation.STORE,opdroite,Operande.creationOpIndirect(placeEnPile,Operande.GB.getRegistre()));
               Prog.ajouter(inst, "écriture en mémoire (pile)");
               break;
           case Index:
               Indice indice = load_Index(a.getFils1());
               inst = Inst.creation2(Operation.STORE,opdroite,Operande.creationOpIndexe(indice.placeEnPileOrigine,Operande.GB.getRegistre(), indice.offset.getRegistre()));
               Prog.ajouter(inst, "écriture en mémoire (pile)");
               GestionRegistre.libererRegistre(indice.offset.getRegistre());
               break;
           }
           GestionRegistre.libererRegistre(opdroite.getRegistre());
           break;
	   case Si:
		   coder_Si(a);
		   break;
	   case Pour:
		   coder_Pour(a);
		   break;
	   case TantQue:
		   coder_TantQue(a);
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
   
   private static void coder_TantQue(Arbre a) {
       Operande reg1;
       String debutWhile = "debutWhile" + nbEtiq;
       Etiq etiqDebutWhile = Etiq.lEtiq(debutWhile);
       nbEtiq++;
       Prog.ajouter(etiqDebutWhile, "Ajout de l etiquette au debut du Tant Que");

       reg1 = coder_EXP(a.getFils1());
       
       String finWhile = "finWhile" + nbEtiq;
       Etiq etiqFinWhile = Etiq.lEtiq(finWhile);
       nbEtiq++;
       
       Inst compareInst = Inst.creation2(Operation.CMP, Operande.creationOpEntier(0), reg1);
       Prog.ajouter(compareInst, "Comparaison de la valeur du registre " + reg1.getRegistre() + " par rapport a 0");
       Inst jumpIfFalse = Inst.creation1(Operation.BLT, Operande.creationOpEtiq(etiqFinWhile));
       Prog.ajouter(jumpIfFalse, "Ajout de l'instruction de saut vers la fin du Tant Que");
       
       coder_Inst(a.getFils2());
       
       Inst jumpWhileBegin = Inst.creation1(Operation.BRA, Operande.creationOpEtiq(etiqDebutWhile));
       Prog.ajouter(jumpWhileBegin, "Ajout de l'instruction de saut vers le debut du Tant Que");
       
       Prog.ajouter(etiqFinWhile, "Ajout de l etiquette a la fin du Tant Que");
       
   }
   
   private static void coder_Pour(Arbre a) {
       Operande reg1, borneInf, borneSup;
       
       String debutFor = "debutFor" + nbEtiq;
       Etiq etiqDebutFor = Etiq.lEtiq(debutFor);
       nbEtiq++;
       Prog.ajouter(etiqDebutFor, "Ajout de l etiquette au debut du For");       
       String finFor = "finPour" + nbEtiq;
       Etiq etiqFinFor = Etiq.lEtiq(finFor);
       nbEtiq++;
       
       String varName = a.getFils1().getChaine();
       int placeEnPile = decl.indexOf(varName) + 1;
       reg1 = GestionRegistre.getFreeRegToOpTab();
       Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg1);
       Prog.ajouter(loadInst, "Ajout de la valeur de la variable dans le registre " + reg1.getRegistre());       
       
       if(a.getFils1().getNoeud()==Noeud.Increment) {
           borneInf = coder_EXP(a.getFils1().getFils2());
           borneSup = coder_EXP(a.getFils1().getFils3());
           
           Inst compareInst = Inst.creation2(Operation.CMP, borneInf, reg1);
           Prog.ajouter(compareInst, "Comparaison du registre " + reg1.getRegistre() + " par rapport au registre " + borneInf.getRegistre());
           Inst jumpIfInf = Inst.creation1(Operation.BLT, Operande.creationOpEtiq(Etiq.lEtiq(finFor)));
           Prog.ajouter(jumpIfInf, "On saute a la fin du for si l identificateur est inferieur a la borne inferieure");
           Inst compareInst2 = Inst.creation2(Operation.CMP, borneSup, reg1);
           Prog.ajouter(compareInst2, "Comparaison du registre " + reg1.getRegistre() + " par rapport au registre " + borneInf.getRegistre());
           Inst jumpIfInf2 = Inst.creation1(Operation.BGT, Operande.creationOpEtiq(Etiq.lEtiq(finFor)));
           Prog.ajouter(jumpIfInf2, "On saute a la fin du for si l identificateur est superieur a la borne superieure");

           coder_Inst(a.getFils2());
           
           Inst incrementInst = Inst.creation2(Operation.ADD, Operande.creationOpEntier(1), reg1);
           Prog.ajouter(incrementInst, "On incremente la valeur du registre " + reg1.getRegistre());
       
       }
       else{ 
    	   if(a.getFils1().getNoeud()==Noeud.Decrement) {
	           borneSup = coder_EXP(a.getFils1().getFils2());
	           borneInf = coder_EXP(a.getFils1().getFils3());
	           Inst compareInst = Inst.creation2(Operation.CMP, borneInf, reg1);
	           Prog.ajouter(compareInst, "Comparaison du registre " + reg1.getRegistre() + " par rapport au registre " + borneInf.getRegistre());
	           Inst jumpIfInf = Inst.creation1(Operation.BLT, Operande.creationOpEtiq(Etiq.lEtiq(finFor)));
	           Prog.ajouter(jumpIfInf, "On saute a la fin du for si l identificateur est inferieur a la borne inferieure");
	           Inst compareInst2 = Inst.creation2(Operation.CMP, borneSup, reg1);
	           Prog.ajouter(compareInst2, "Comparaison du registre " + reg1.getRegistre() + " par rapport au registre " + borneInf.getRegistre());
	           Inst jumpIfInf2 = Inst.creation1(Operation.BGT, Operande.creationOpEtiq(Etiq.lEtiq(finFor)));
	           Prog.ajouter(jumpIfInf2, "On saute a la fin du for si l identificateur est superieur a la borne superieure");
	
	           coder_Inst(a.getFils2());
	           
	           Inst incrementInst = Inst.creation2(Operation.SUB, Operande.creationOpEntier(1), reg1);
	           Prog.ajouter(incrementInst, "On decremente la valeur du registre " + reg1.getRegistre());
	       }
       }
       
       Inst storeInst = Inst.creation2(Operation.STORE, reg1, Operande.creationOpIndirect(placeEnPile, Registre.GB));
       Prog.ajouter(storeInst, "Remise de la valeur de l identificateur dans la pile");
       
       Inst jumpForBegin = Inst.creation1(Operation.BRA, Operande.creationOpEtiq(Etiq.lEtiq(debutFor)));
       Prog.ajouter(jumpForBegin, "Ajout de l'instruction de saut vers le debut du For");
       
       Prog.ajouter(etiqFinFor, "Ajout de l etiquette a la fin du For");
   }
   
   
/**Fonction s'occupant de l'instruction write
    * Etat : terminé
    * @param un arbre (Premier appel, a est un Noeud.ListeExp)
    * @return void
    */
   private static void coder_Ecriture(Arbre a){
	   if(a.getNoeud().equals(Noeud.Vide)) return;
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
		   if(r != null && r.equals(Registre.R1)){
			   GestionRegistre.popPile(Registre.R1);
		   }
		   //Si r = Rm (on a changé sa valeur) et r != R1 (pour éviter le faire un LOAD R1 R1) , on rétablit le registre dans R1
		   else{
			   if(r != null){
				   inst = Inst.creation2(Operation.LOAD, Operande.opDirect(r), Operande.R1);
				   Prog.ajouter(inst,"Registre retablis depuis "+r+" après écriture");
				   GestionRegistre.libererRegistre(r);
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
		   if(r != null && r.equals(Registre.R1)){
			   GestionRegistre.popPile(Registre.R1);
		   }
		   //Si r = Rm (on a changé sa valeur) et r != R1 (pour éviter le faire un LOAD R1 R1) , on rétablit le registre dans R1
		   else{
			   if(r != null){
				   inst = Inst.creation2(Operation.LOAD, Operande.opDirect(r), Operande.R1);
				   Prog.ajouter(inst,"Registre retablis depuis "+r+" après écriture");
				   GestionRegistre.libererRegistre(r);
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
    * Etat : Fini
    * @return void
    * @param a (Un Noeud.Ident ou un Noeud.Index)
    */
   private static void coder_Lecture(Arbre a) {
	   Registre r = null;
		//Le fils d'un Noeud Lecture est forcément un Noeud Ident de type Integer ou Reel
		//On vérifie si R1 est libre
	   //Si R1 est occupé
	   if(!GestionRegistre.estRegistreLibre(1)){
		   //On le déplace dans un registre libre (et on le met comme occupé) ou on le met en pile 
		   r = GestionRegistre.deplaceRegistre(Registre.R1);
	   }
	   	//On lit soit un entier, soit un réel qui sera ensuite placer dans R1
	   	if(a.getDecor().getType().getNature().equals(NatureType.Interval)){
			Inst inst = Inst.creation0(Operation.RINT);
			Prog.ajouter(inst, "Lecture d'un entier");
		}
		else{
			if(a.getDecor().getType().getNature().equals(NatureType.Real)){
				Inst inst = Inst.creation0(Operation.RFLOAT);
				Prog.ajouter(inst, "Lecture d'un flotant");
			}
			else{
				Inst inst = Inst.creation1(Operation.BRA,Operande.creationOpEtiq(Etiq.lEtiq("Halt.1")));
			   	Prog.ajouter(inst, "On arrete le programme car on essaye de read autre chose qu'un int ou un reel");
			}
		}
	    // On test si R1 possède une valeur correcte (pour les intervalles)
	   	
	   	//On test si R1 est supérieur à la borne inf du fils
	   	Inst inst = Inst.creation2(Operation.CMP, Operande.creationOpEntier(a.getDecor().getDefn().getType().getIndice().getBorneInf()),Operande.R1);
	   	Prog.ajouter(inst, "Comparaison de la borne inf pour l'affectation suite à un read");
	   	inst = Inst.creation1(Operation.BLT,Operande.creationOpEtiq(Etiq.lEtiq("Halt.1")));
	   	Prog.ajouter(inst, "On arrete le programme s'il y a une erreur BorneInf intervale");
	   	
	   	//On test si R1 est supérieur à la borne sup du fils
	   	inst = Inst.creation2(Operation.CMP, Operande.creationOpEntier(a.getDecor().getDefn().getType().getIndice().getBorneSup()),Operande.R1);
	   	Prog.ajouter(inst, "Comparaison de la borne sup pour l'affectation suite à un read");
	   	inst = Inst.creation1(Operation.BGT,Operande.creationOpEtiq(Etiq.lEtiq("Halt.1")));
	   	Prog.ajouter(inst, "On arrete le programme s'il y a une erreur BorneSup intervale");
	   	
	   	//On le replace en pile
	   	if(!a.getDecor().getType().getNature().equals(NatureType.Array)){
	   		String varName = a.getChaine();
			int placeEnPile = decl.indexOf(varName) + 1;
			Inst inst2 = Inst.creation2(Operation.STORE, Operande.R1, Operande.creationOpIndirect(placeEnPile, Registre.GB));
			Prog.ajouter(inst2, "Ecriture dans la variable "+varName+" en pile à l'emplacement "+placeEnPile);
			
	   	}else{
	   		//Trouver le nom puis trouver le décalage en parcours profondeur
	   		int length = getLength(a);
	   		//Trouver dynamiquement l'endroit où l'on veut écrire
	   		//TODO faire la même chose que pour affect
	   		Indice indice = load_Index(a);
	   		Inst inst2 = Inst.creation2(Operation.STORE, Operande.R1, Operande.creationOpIndexe(indice.placeEnPileOrigine, Registre.GB, indice.offset.getRegistre()));
	   		Prog.ajouter(inst2);
	   		
	   		if(indice.offset.getNature().equals(NatureOperande.OpDirect))
	   			GestionRegistre.libererRegistre(indice.offset.getRegistre());
	   	}	   	
	   	//On restore les états des registres si besoin
	   	//Si r = R1 , on a placé le registre précédent en pile, on le replace donc dans R1
	   	if(r != null && r.equals(Registre.R1)){
	   		GestionRegistre.popPile(Registre.R1);
	   	}
	   	//Si r = Rm (on a changé sa valeur) et r != R1 (pour éviter le faire un LOAD R1 R1) , on rétablit le registre dans R1
	   	else{
	   		if(r != null){
	   			inst = Inst.creation2(Operation.LOAD, Operande.opDirect(r), Operande.R1);
	   			Prog.ajouter(inst,"Registre retablis depuis "+r+" après écriture");
	   			GestionRegistre.libererRegistre(r);
	   		}
	   	}  
   }
   /**Code l'instruction Si
    * 
    * @param a
    */
   private static void coder_Si(Arbre a) {
	   Operande fils1 = coder_EXP(a.getFils1());
	   Inst inst = Inst.creation2(Operation.CMP, Operande.creationOpEntier(0), fils1);
	   GestionRegistre.libererRegistre(fils1.getRegistre());
	   Prog.ajouter(inst);
	   if(a.getFils3().getFils2().getNoeud().equals(Noeud.Nop) && a.getFils3().getFils1().getNoeud().equals(Noeud.Vide)){
		   String str = new String("FinSi"+nbEtiq);
		   Etiq eti = Etiq.lEtiq(str);
		   nbEtiq++;
		   
		   inst = Inst.creation1(Operation.BLT, Operande.creationOpEtiq(eti));
		   Prog.ajouter(inst,"Branch vers le FinSi");
		   coder_Inst(a.getFils2());	// Instruction alors
		   Prog.ajouter(eti);// etiquette du FinSi
	   }else{
		   String str = new String("Sinon"+nbEtiq);
		   Etiq eti = Etiq.lEtiq(str);
		   nbEtiq++;
		   
		   String str2 = new String("FinSi"+nbEtiq);
		   Etiq eti2 = Etiq.lEtiq(str2);
		   nbEtiq++;
		   
		   inst = Inst.creation1(Operation.BLT, Operande.creationOpEtiq(eti));
		   Prog.ajouter(inst,"Branch vers le sinon");
		   coder_Inst(a.getFils2());	// Instruction alors
		   inst = Inst.creation1(Operation.BRA, Operande.creationOpEtiq(eti2));
		   Prog.ajouter(eti);	// etiquette du sinon
		   coder_Inst(a.getFils3());	// Instruction sinon
		   Prog.ajouter(eti2);	// etiquette du fin si
	   }
   }
   
   
   /**
    * A étant un Noeud.Index, charge la zone mémoire pointée par a dans un registre et le renvoie
    * @param a un Noeud.Index
    * @return Un opérande contenant la valeur pointée par a
    */
   private static Indice load_Index(Arbre a) {
	   String ident = getIdent(a);
	   Operande offset = getSubIndex(a);
	   int placeEnPile = decl.indexOf(ident) + 1;
	   Indice indice = new Indice(offset,placeEnPile);
	   return indice;
   }
   /**
    * Etant donné a un index, code la récupération de l'indice du tableau voulu, de façon récursive.
    * De la forme (Expression de Fils2 * dimension du tableau pointé par le Fils1) + expression retournée par le Fils1
    * Vérifie également que cet index est inclue dans les bornes du tableau.
    * @param a un arbre index
    * @return un registre contenant l'offset de l'indice du tableau.
    */
   private static Operande getSubIndex(Arbre a) {
	   if(a.getNoeud().equals(Noeud.Index)) {
		   int len = getLength(a.getFils1()); // Dimension du tableau pointé par le Fils1
		   Operande subexp = getSubIndex(a.getFils1()); // expression retournée par Fils1
		   Operande exp = coder_EXP(a.getFils2()); //Valeur de l'expression de Fils2
		   
		   // vérification des bornes
		   Inst inst = Inst.creation2(Operation.CMP, Operande.creationOpEntier(a.getDecor().getDefn().getType().getIndice().getBorneInf()),exp);
		   Prog.ajouter(inst, "Comparaison de la borne inf pour un index");
		   inst = Inst.creation1(Operation.BLT,Operande.creationOpEtiq(Etiq.lEtiq("Halt.1")));
		   Prog.ajouter(inst, "On arrete le programme s'il y a une erreur BorneInf intervale");
		   
		   inst = Inst.creation2(Operation.CMP, Operande.creationOpEntier(a.getDecor().getDefn().getType().getIndice().getBorneSup()),exp);
		   Prog.ajouter(inst, "Comparaison de la borne sup pour un index");
		   inst = Inst.creation1(Operation.BGT,Operande.creationOpEtiq(Etiq.lEtiq("Halt.1")));
		   Prog.ajouter(inst, "On arrete le programme s'il y a une erreur BorneSup intervale");
		   // fin de vérification des bornes
		   
		   Inst machin = Inst.creation2(Operation.MUL, Operande.creationOpEntier(len), exp);
		   Prog.ajouter(machin,"calcul de la dimension du tableau : exp*dimf...");
		   machin = Inst.creation2(Operation.ADD, exp,subexp);
		   Prog.ajouter(machin,"calcul de la dimension du tableau : (exp*dimf)+expf");
		   GestionRegistre.libererRegistre(exp.getRegistre());
		   return subexp;
	   }
	   else {
		   return GestionRegistre.getFreeRegToOpTab();
	   }
   }
   
   /*
    * Donne la longueur du tableau pointé par a, en incluant les éventuels sous-tableaux.
    */
   private static int getLength(Arbre a) {
	   if(a.getNoeud().equals(Noeud.Index)) {
		   return((a.getDecor().getDefn().getType().getIndice().getBorneSup() - a.getDecor().getDefn().getType().getIndice().getBorneInf() + 1)*getLength(a.getFils1()));
	   }
	   else {
		   return 1;
	   }
   }
   
   /*
    * Utilisé par load_index, va récupérer l'identifiant associé au premier élément du tableau de façon récursive.
    */
   private static String getIdent(Arbre a) {
	   if(a.getNoeud().equals(Noeud.Index)) {
		   String ident = getIdent2(a.getFils1());
		   while(a.getNoeud().equals(Noeud.Index)){
			   ident+="["+a.getDecor().getDefn().getType().getIndice().getBorneInf()+"]";
			   a=a.getFils1();
		   }
		   return ident;
	   }
	   else {
		   return(a.getChaine());
	   }
   }
   
   private static String getIdent2(Arbre a) {
	   if(a.getNoeud().equals(Noeud.Index)) {
		   return (getIdent2(a.getFils1()));
	   }
	   else {
		   return(a.getChaine());
	   }
   }
   
   private static Operande coder_EXP(Arbre a){
	   //chercher en fonction du nom de la variable, sa position en pile,PUIS la charger dans un registre libre
	   //Il faut donc utiliser la fonction getGrosRegistreLibre()
	   //Il faut ensuite modifier le tableau regTab pour indiquer les registres qui ne sont plus disponible
	   //
	   
	   if(a.getArite() == 0){
		   switch(a.getNoeud()){
		   case Entier:
			   Operande registreLibre = GestionRegistre.getFreeRegToOpTab();
			   Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(a.getEntier()), registreLibre);
			   Prog.ajouter(loadInst);
			   return registreLibre;
		   case Reel:

			   Operande registreLibre2 = GestionRegistre.getFreeRegToOpTab();
			   Inst loadInst2 = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getReel()), registreLibre2);
			   Prog.ajouter(loadInst2);
			   return registreLibre2;
		   
		   case Ident:
			   Operande registreLibre3 = GestionRegistre.getFreeRegToOpTab();
			   Inst loadInst3;
			   String varName = a.getChaine();
			   int placeEnPile = decl.indexOf(varName) + 1;
			   if(placeEnPile == 0) {
				   if(varName.toLowerCase().equals("true")){
					   loadInst3 = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(1), registreLibre3);
					   Prog.ajouter(loadInst3);
					   return registreLibre3;
				   }
				   if(varName.toLowerCase().equals("false")){
					   loadInst3 = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(-1), registreLibre3);
					   Prog.ajouter(loadInst3);
					   return registreLibre3;
				   }
				   System.exit(0);
			   }
  			   loadInst3 = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), registreLibre3);
  			   Prog.ajouter(loadInst3, "Ajout de la valeur de la variable dans le registre " + registreLibre3.getRegistre());
  			   return registreLibre3;
		   }
	   }
	   
	   if(a.getArite()==1){
		   
	   		String varName;
  			int placeEnPile;
  			Operande registreLibre;
  			boolean needPop = false;
  			
		   switch(a.getNoeud()){
		   	
		   	
		   	case PlusUnaire:
	   			return coder_EXP(a.getFils1());	   			
	   			
		   	case Non:
		   	case MoinsUnaire:
	   			registreLibre = GestionRegistre.getFreeRegToOpTab();
	   			
	   			if(registreLibre==null) {
	   				GestionRegistre.pushPile(Registre.R2);
	   				registreLibre = Operande.R2;
	   				needPop = true;
	   			}
	   			
		   		if(a.getFils1().getNoeud()==Noeud.Ident){
		   			
			   		varName = a.getFils1().getChaine();
		   			placeEnPile = decl.indexOf(varName) + 1;
		   			
		   			if(a.getFils1().getChaine().toLowerCase().equals("true")){
		   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(-1), registreLibre);
		   				Prog.ajouter(loadInst, "Chargement de la variable booleenne false (-1) dans le registre " + registreLibre.getRegistre());
		   				return registreLibre;
		   			}
		   			else{ 
		   				if(a.getFils1().getChaine().toLowerCase().equals("false")){
			   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(1), registreLibre);
			   				Prog.ajouter(loadInst, "Chargement de la variable booleenne true (1) dans le registre " + registreLibre.getRegistre());
			   				return registreLibre;
			   			}else{
			   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), registreLibre);
				   			Inst moinsUnaire = Inst.creation2(Operation.MUL, Operande.creationOpEntier(-1), registreLibre);
				   			Prog.ajouter(loadInst, "Chargement de la variable dans le registre " + registreLibre.getRegistre());
				   			Prog.ajouter(moinsUnaire, "Operation moins unaire et resultat mis dans le registre " + registreLibre.getRegistre() );
			   			}
		   			}
			 
		   		}
		   		else{
		   			Operande reg = coder_EXP(a.getFils1());
		   			GestionRegistre.libererRegistre(reg.getRegistre());
	   				Inst loadInst = Inst.creation2(Operation.LOAD, reg, registreLibre);
	   				Inst moinsUnaire = Inst.creation2(Operation.MUL, Operande.creationOpEntier(-1), registreLibre);
		   			Prog.ajouter(loadInst, "Chargement de la valeur reelle dans le registre " + registreLibre.getRegistre());
		   			Prog.ajouter(moinsUnaire, "Operation moins unaire et resultat mis dans le registre " + registreLibre.getRegistre());
		   		}
		   		
		   		if(needPop) {
		   			GestionRegistre.popPile(Registre.R2);
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
		   		reg1 = GestionRegistre.getFreeRegToOpTab();
		   		reg2 = GestionRegistre.getFreeRegToOpTab();
		   		
		   		if(a.getFils1().getNoeud()==Noeud.Conversion) {
		   			if(a.getFils1().getFils1().getNoeud()==Noeud.Ident){
			   			varName = a.getFils1().getFils1().getChaine();
			   			placeEnPile = decl.indexOf(varName) + 1;
			   			Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg1);
			   			Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
			   		}
			   		else{ 
			   			if(a.getFils1().getFils1().getDecor().getType() == Type.Integer){
				   			Inst loadFils1Inst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils1().getFils1().getEntier()), reg1);
				   			Prog.ajouter(loadFils1Inst, "Ajout de l'entier operande gauche pour la mult");
				   		} else{ 
				   			if(a.getFils1().getFils1().getDecor().getType() == Type.Real){
					   			Inst loadFils1Inst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils1().getFils1().getReel()), reg1);
					   			Prog.ajouter(loadFils1Inst, "Ajout de l'entier operande gauche pour la mult");
					   		}
					   		else {
					   			GestionRegistre.libererRegistre(reg1.getRegistre());
					   			reg1 = coder_EXP(a.getFils1().getFils1());
					   		}
				   		}
			   		}
		   		}
		   		else {
		   			if(a.getFils1().getNoeud()==Noeud.Ident){
			   			varName = a.getFils1().getChaine();
			   			placeEnPile = decl.indexOf(varName) + 1;
			   			Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg1);
			   			Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
			   		}
			   		else{
			   			if(a.getFils1().getDecor().getType() == Type.Integer){
				   			Inst loadFils1Inst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(a.getFils1().getEntier()), reg1);
				   			Prog.ajouter(loadFils1Inst, "Ajout de l'entier operande gauche pour la mult");
				   		} else{
				   			if(a.getFils1().getDecor().getType() == Type.Real){
					   			Inst loadFils1Inst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils1().getReel()), reg1);
					   			Prog.ajouter(loadFils1Inst, "Ajout de l'entier operande gauche pour la mult");
					   		}
					   		else {
					   			GestionRegistre.libererRegistre(reg1.getRegistre());
					   			reg1 = coder_EXP(a.getFils1());
					   		}
				   		}
			   		}
		   		}
		   		
		   		if(a.getFils2().getNoeud()==Noeud.Conversion) {
		   			if(a.getFils2().getFils1().getNoeud()==Noeud.Ident){
			   			varName = a.getFils2().getFils1().getChaine();
			   			placeEnPile = decl.indexOf(varName) + 1;
			   			Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg2);
			   			Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
			   		}
			   		else{
			   			if(a.getFils2().getFils1().getDecor().getType() == Type.Integer){
				   			Inst loadFils2Inst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils2().getFils1().getEntier()), reg2);
				   			Prog.ajouter(loadFils2Inst, "Ajout de l'entier operande droit pour la mult");
				   		} else{
				   			if(a.getFils2().getFils1().getDecor().getType() == Type.Real){
					   			Inst loadFils2Inst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils2().getFils1().getReel()), reg2);
					   			Prog.ajouter(loadFils2Inst, "Ajout de l'entier operande droit pour la mult");
					   		}
					   		else {
					   			GestionRegistre.libererRegistre(reg2.getRegistre());
					   			reg2 = coder_EXP(a.getFils2().getFils1());
					   		}
				   		}
			   		}
		   		}
		   		else {
		   			if(a.getFils2().getNoeud()==Noeud.Ident){
			   			varName = a.getFils2().getChaine();
			   			placeEnPile = decl.indexOf(varName) + 1;
			   			Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg2);
			   			Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
			   		}
			   		else {	
			   			if(a.getFils2().getDecor().getType() == Type.Integer){
				   			Inst loadFils2Inst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(a.getFils2().getEntier()), reg2);
				   			Prog.ajouter(loadFils2Inst, "Ajout de l'entier operande droit pour la mult");
				   		} else{ 
				   			if(a.getFils2().getDecor().getType() == Type.Real){
					   			Inst loadFils2Inst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils2().getReel()), reg2);
					   			Prog.ajouter(loadFils2Inst, "Ajout de l'entier operande droit pour la mult");
					   		}
					   		else {
					   			GestionRegistre.libererRegistre(reg2.getRegistre());
					   			reg2 = coder_EXP(a.getFils2());
					   		}
				   		}
			   		}
		   		}
		   		
		   		Inst multInst = Inst.creation2(Operation.MUL, reg1, reg2);
		   		Prog.ajouter(multInst, "Ajout de l'instruction multiplication");
		   		
		   		GestionRegistre.libererRegistre(reg1.getRegistre());
		   		return reg2;
 
	   		case Plus :
		   		reg1 = GestionRegistre.getFreeRegToOpTab();
		   		reg2 = GestionRegistre.getFreeRegToOpTab();
		   		
		   		if(a.getFils1().getNoeud()==Noeud.Conversion) {
		   			if(a.getFils1().getFils1().getNoeud()==Noeud.Ident){
			   			varName = a.getFils1().getFils1().getChaine();
			   			placeEnPile = decl.indexOf(varName) + 1;
			   			Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg1);
			   			Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
			   		}
			   		else{
			   			if(a.getFils1().getFils1().getDecor().getType() == Type.Integer){
				   			Inst loadFils1Inst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils1().getFils1().getEntier()), reg1);
				   			Prog.ajouter(loadFils1Inst, "Ajout de l'entier operande gauche pour la mult");
				   		} else{ 
					   		if(a.getFils1().getFils1().getDecor().getType() == Type.Real){
					   			Inst loadFils1Inst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils1().getFils1().getReel()), reg1);
					   			Prog.ajouter(loadFils1Inst, "Ajout de l'entier operande gauche pour la mult");
					   		}
					   		else {
					   			GestionRegistre.libererRegistre(reg1.getRegistre());
					   			reg1 = coder_EXP(a.getFils1().getFils1());
					   		}
				   		}
			   		}
		   		}
		   		else {
		   			if(a.getFils1().getNoeud()==Noeud.Ident){
		   			varName = a.getFils1().getChaine();
		   			placeEnPile = decl.indexOf(varName) + 1;
		   			Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg1);
		   			Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
			   		}
			   		else{ 
			   			if(a.getFils1().getDecor().getType() == Type.Integer){
				   			Inst loadFils1Inst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(a.getFils1().getEntier()), reg1);
				   			Prog.ajouter(loadFils1Inst, "Ajout de l'entier operande gauche pour la mult");
				   		} else{ 
				   			if(a.getFils1().getDecor().getType() == Type.Real){
					   			Inst loadFils1Inst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils1().getReel()), reg1);
					   			Prog.ajouter(loadFils1Inst, "Ajout de l'entier operande gauche pour la mult");
					   		}
					   		else {
					   			GestionRegistre.libererRegistre(reg1.getRegistre());
					   			reg1 = coder_EXP(a.getFils1());
					   		}
				   		}
			   		}
		   		}
		   		
		   		if(a.getFils2().getNoeud()==Noeud.Conversion) {
		   			if(a.getFils2().getFils1().getNoeud()==Noeud.Ident){
			   			varName = a.getFils2().getFils1().getChaine();
			   			placeEnPile = decl.indexOf(varName) + 1;
			   			Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg2);
			   			Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
			   		}
			   		else{ 
			   			if(a.getFils2().getFils1().getDecor().getType() == Type.Integer){
				   			Inst loadFils2Inst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils2().getFils1().getEntier()), reg2);
				   			Prog.ajouter(loadFils2Inst, "Ajout de l'entier operande droit pour la mult");
				   		} else{ 
				   			if(a.getFils2().getFils1().getDecor().getType() == Type.Real){
					   			Inst loadFils2Inst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils2().getFils1().getReel()), reg2);
					   			Prog.ajouter(loadFils2Inst, "Ajout de l'entier operande droit pour la mult");
					   		}
					   		else {
					   			GestionRegistre.libererRegistre(reg2.getRegistre());
					   			reg2 = coder_EXP(a.getFils2().getFils1());
					   		}
				   		}
			   		}
		   		}
		   		else {
		   			if(a.getFils2().getNoeud()==Noeud.Ident){
		   			varName = a.getFils2().getChaine();
		   			placeEnPile = decl.indexOf(varName) + 1;
		   			Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg2);
		   			Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
			   		}
			   		else{ 
			   			if(a.getFils2().getDecor().getType() == Type.Integer){
				   			Inst loadFils2Inst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(a.getFils2().getEntier()), reg2);
				   			Prog.ajouter(loadFils2Inst, "Ajout de l'entier operande droit pour la mult");
				   		} else{ 
				   			if(a.getFils2().getDecor().getType() == Type.Real){
					   			Inst loadFils2Inst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils2().getReel()), reg2);
					   			Prog.ajouter(loadFils2Inst, "Ajout de l'entier operande droit pour la mult");
					   		}
					   		else {
					   			GestionRegistre.libererRegistre(reg2.getRegistre());
					   			reg2 = coder_EXP(a.getFils2());
					   		}
				   		}
			   		}
		   		}
		   		
		   		Inst addInst = Inst.creation2(Operation.ADD, reg1, reg2);
		   		Prog.ajouter(addInst, "Ajout de l'instruction addition");

	   			GestionRegistre.libererRegistre(reg1.getRegistre());
		   		return reg2;
		   		
	   		case Moins :
		   		reg1 = GestionRegistre.getFreeRegToOpTab();
		   		reg2 = GestionRegistre.getFreeRegToOpTab();
		   		
		   		if(a.getFils1().getNoeud()==Noeud.Conversion) {
		   			if(a.getFils1().getFils1().getNoeud()==Noeud.Ident){
			   			varName = a.getFils1().getFils1().getChaine();
			   			placeEnPile = decl.indexOf(varName) + 1;
			   			Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg1);
			   			Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
			   		}
			   		else{ 
			   			if(a.getFils1().getFils1().getDecor().getType() == Type.Integer){
				   			Inst loadFils1Inst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils1().getFils1().getEntier()), reg1);
				   			Prog.ajouter(loadFils1Inst, "Ajout de l'entier operande gauche pour la mult");
				   		} else{
				   			if(a.getFils1().getFils1().getDecor().getType() == Type.Real){
					   			Inst loadFils1Inst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils1().getFils1().getReel()), reg1);
					   			Prog.ajouter(loadFils1Inst, "Ajout de l'entier operande gauche pour la mult");
					   		}
					   		else {
					   			GestionRegistre.libererRegistre(reg1.getRegistre());
					   			reg1 = coder_EXP(a.getFils1().getFils1());
					   		}
				   		}
			   		}
		   		}
		   		else {
		   			if(a.getFils1().getNoeud()==Noeud.Ident){
			   			varName = a.getFils1().getChaine();
			   			placeEnPile = decl.indexOf(varName) + 1;
			   			Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg1);
			   			Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
			   		}
			   		else{ if(a.getFils1().getDecor().getType() == Type.Integer){
				   			Inst loadFils1Inst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(a.getFils1().getEntier()), reg1);
				   			Prog.ajouter(loadFils1Inst, "Ajout de l'entier operande gauche pour la mult");
				   		} else{ 
				   			if(a.getFils1().getDecor().getType() == Type.Real){
					   			Inst loadFils1Inst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils1().getReel()), reg1);
					   			Prog.ajouter(loadFils1Inst, "Ajout de l'entier operande gauche pour la mult");
					   		}
					   		else {
					   			GestionRegistre.libererRegistre(reg1.getRegistre());
					   			reg1 = coder_EXP(a.getFils1());
					   		}
				   		}
			   		}
		   		}
		   		
		   		if(a.getFils2().getNoeud()==Noeud.Conversion) {
		   			if(a.getFils2().getFils1().getNoeud()==Noeud.Ident){
			   			varName = a.getFils2().getFils1().getChaine();
			   			placeEnPile = decl.indexOf(varName) + 1;
			   			Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg2);
			   			Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
			   		}
			   		else{ 
			   			if(a.getFils2().getFils1().getDecor().getType() == Type.Integer){
				   			Inst loadFils2Inst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils2().getFils1().getEntier()), reg2);
				   			Prog.ajouter(loadFils2Inst, "Ajout de l'entier operande droit pour la mult");
				   		} else{ 
				   			if(a.getFils2().getFils1().getDecor().getType() == Type.Real){
					   			Inst loadFils2Inst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils2().getFils1().getReel()), reg2);
					   			Prog.ajouter(loadFils2Inst, "Ajout de l'entier operande droit pour la mult");
					   		}
					   		else {
					   			GestionRegistre.libererRegistre(reg2.getRegistre());
					   			reg2 = coder_EXP(a.getFils2().getFils1());
					   		}
				   		}
			   		}
		   		}
		   		else {
		   			if(a.getFils2().getNoeud()==Noeud.Ident){
			   			varName = a.getFils2().getChaine();
			   			placeEnPile = decl.indexOf(varName) + 1;
			   			Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg2);
			   			Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
			   		}
			   		else{ 
			   			if(a.getFils2().getDecor().getType() == Type.Integer){
				   			Inst loadFils2Inst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(a.getFils2().getEntier()), reg2);
				   			Prog.ajouter(loadFils2Inst, "Ajout de l'entier operande droit pour la mult");
				   		} else{ 
				   			if(a.getFils2().getDecor().getType() == Type.Real){
					   			Inst loadFils2Inst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils2().getReel()), reg2);
					   			Prog.ajouter(loadFils2Inst, "Ajout de l'entier operande droit pour la mult");
					   		}
					   		else {
					   			GestionRegistre.libererRegistre(reg2.getRegistre());
					   			reg2 = coder_EXP(a.getFils2());
					   		}
				   		}
			   		}
		   		}
		   		
		   		Inst subInst = Inst.creation2(Operation.SUB, reg2, reg1);
		   		Prog.ajouter(subInst, "Ajout de l'instruction soustraction");

	   			GestionRegistre.libererRegistre(reg2.getRegistre());
		   		return reg1;

	   		case Quotient :
	   		case DivReel :
		   		reg1 = GestionRegistre.getFreeRegToOpTab();
		   		reg2 = GestionRegistre.getFreeRegToOpTab();
		   		
		   		if(a.getFils1().getNoeud() == Noeud.Conversion) {
		   			if(a.getFils1().getFils1().getNoeud()==Noeud.Ident){
			   			varName = a.getFils1().getFils1().getChaine();
			   			placeEnPile = decl.indexOf(varName) + 1;
			   			Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg1);
			   			Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
			   		}
			   		else{ 
			   			if(a.getFils1().getFils1().getDecor().getType() == Type.Integer){
				   			Inst loadFils1Inst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils1().getFils1().getEntier()), reg1);
				   			Prog.ajouter(loadFils1Inst, "Ajout de l'entier operande gauche pour la mult");
				   		} else{ 
				   			if(a.getFils1().getFils1().getDecor().getType() == Type.Real){
					   			Inst loadFils1Inst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils1().getFils1().getReel()), reg1);
					   			Prog.ajouter(loadFils1Inst, "Ajout de l'entier operande gauche pour la mult");
					   		}
					   		else {
					   			GestionRegistre.libererRegistre(reg1.getRegistre());
					   			reg1 = coder_EXP(a.getFils1().getFils1());
					   		}
				   		}
			   		}
		   		}
		   		else {
		   			if(a.getFils1().getNoeud()==Noeud.Ident){
			   			varName = a.getFils1().getChaine();
			   			placeEnPile = decl.indexOf(varName) + 1;
			   			Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg1);
			   			Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
			   		}
			   		else{ 
			   			if(a.getFils1().getDecor().getType() == Type.Integer){
				   			Inst loadFils1Inst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(a.getFils1().getEntier()), reg1);
				   			Prog.ajouter(loadFils1Inst, "Ajout de l'entier operande gauche pour la mult");
				   		} else{ 
				   			if(a.getFils1().getDecor().getType() == Type.Real){
					   			Inst loadFils1Inst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils1().getReel()), reg1);
					   			Prog.ajouter(loadFils1Inst, "Ajout de l'entier operande gauche pour la mult");
					   		}
					   		else {
					   			GestionRegistre.libererRegistre(reg1.getRegistre());
					   			reg1 = coder_EXP(a.getFils1());
					   		}
				   		}
			   		}
		   		}
		   		
		   		if(a.getFils2().getNoeud() == Noeud.Conversion) {
		   			if(a.getFils2().getFils1().getNoeud()==Noeud.Ident){
			   			varName = a.getFils2().getFils1().getChaine();
			   			placeEnPile = decl.indexOf(varName) + 1;
			   			Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg2);
			   			Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
			   		}
			   		else{ 
			   			if(a.getFils2().getFils1().getDecor().getType() == Type.Integer){
				   			Inst loadFils2Inst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils2().getFils1().getEntier()), reg2);
				   			Prog.ajouter(loadFils2Inst, "Ajout de l'entier operande droit pour la mult");
				   		} else{ 
				   			if(a.getFils2().getFils1().getDecor().getType() == Type.Real){
					   			Inst loadFils2Inst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils2().getFils1().getReel()), reg2);
					   			Prog.ajouter(loadFils2Inst, "Ajout de l'entier operande droit pour la mult");
					   		}
					   		else {
					   			GestionRegistre.libererRegistre(reg2.getRegistre());
					   			reg2 = coder_EXP(a.getFils2().getFils1());
					   		}
				   		}
			   		}
		   		}
		   		else {
		   			if(a.getFils2().getNoeud()==Noeud.Ident){
			   			varName = a.getFils2().getChaine();
			   			placeEnPile = decl.indexOf(varName) + 1;
			   			Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg2);
			   			Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
			   		}
			   		else{ 
			   			if(a.getFils2().getDecor().getType() == Type.Integer){
				   			Inst loadFils2Inst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(a.getFils2().getEntier()), reg2);
				   			Prog.ajouter(loadFils2Inst, "Ajout de l'entier operande droit pour la mult");
				   		} else{ 
				   			if(a.getFils2().getDecor().getType() == Type.Real){
					   			Inst loadFils2Inst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils2().getReel()), reg2);
					   			Prog.ajouter(loadFils2Inst, "Ajout de l'entier operande droit pour la mult");
					   		}
					   		else {
					   			GestionRegistre.libererRegistre(reg2.getRegistre());
					   			reg2 = coder_EXP(a.getFils2());
					   		}
				   		}
			   		}
		   		}
		   		
		   		Inst divRInst = Inst.creation2(Operation.DIV, reg2, reg1);
		   		Prog.ajouter(divRInst, "Ajout de l'instruction division");

		   		
	   			GestionRegistre.libererRegistre(reg2.getRegistre());
		   		return reg1;

            case Et :

            	String nomEtiqNegative = "negative"+nbEtiq;
                Etiq negEtiq = Etiq.lEtiq(nomEtiqNegative);
                nbEtiq++;
                reg1 = GestionRegistre.getFreeRegToOpTab();
                boolean forcementFaux = false;
                if(a.getFils1().getNoeud() == Noeud.Ident){
                    if(a.getFils1().getChaine().equals("true")){
                        Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(1), reg1);
                        Prog.ajouter(loadInst, "Chargement de la variable booleenne true (1) dans le registre " + reg1.getRegistre());
                    }
                    else{
                    	if(a.getFils1().getChaine().equals("false")){
                    		Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(-1), reg1);
                    		Prog.ajouter(loadInst, "Chargement de la variable booleenne false (-1) dans le registre " + reg1.getRegistre());
                    		forcementFaux = true;
                    	}
                    	else{
                    		varName = a.getFils1().getChaine();
                    		placeEnPile = decl.indexOf(varName) + 1;
                    		Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg1);
                    		Prog.ajouter(loadInst, "Chargement de la variable booleenne dans le registre " + reg1.getRegistre());                            
                    	}
                    }
                    Inst compareToZero = Inst.creation2(Operation.CMP, Operande.creationOpEntier(0), reg1);
                    Prog.ajouter(compareToZero, "Comparaison du registre " + reg1.getRegistre() + "à 0");
                    Inst jump = Inst.creation1(Operation.BLT, Operande.creationOpEtiq(Etiq.lEtiq(nomEtiqNegative)));
                    Prog.ajouter(jump, "On saute a la fin du et");
                }
                else{
		   			GestionRegistre.libererRegistre(reg1.getRegistre());
                    reg1 = coder_EXP(a.getFils1());            
                    
                }
                                
                if(forcementFaux){
                }
                else{
                	if(a.getFils2().getNoeud() == Noeud.Ident){
                        if(a.getFils2().getChaine().equals("true")){
                            Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(1), reg1);
                            Prog.ajouter(loadInst, "Chargement de la variable booleenne true (1) dans le registre " + reg1.getRegistre());
                        }
                        else {
                        	if(a.getFils2().getChaine().equals("false")){
                        		Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(-1), reg1);
                        		Prog.ajouter(loadInst, "Chargement de la variable booleenne false (-1) dans le registre " + reg1.getRegistre());
                        	}
                        	else{
                        		varName = a.getFils2().getChaine();
                        		placeEnPile = decl.indexOf(varName) + 1;
                        		Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg1);
                        		Prog.ajouter(loadInst, "Chargement de la variable booleenne dans le registre " + reg1.getRegistre());                            
                        	}
                        }
                    }
                    else{
    		   			GestionRegistre.libererRegistre(reg1.getRegistre());
                        reg1 = coder_EXP(a.getFils2());
                    }   
                }
            	Prog.ajouter(Etiq.lEtiq(nomEtiqNegative));
                return reg1;

	   		case Ou :
	   			String nomEtiqPositive = "positive" + nbEtiq;
	   			Etiq posEtiq = Etiq.lEtiq(nomEtiqPositive);
	   			nbEtiq++;
                reg1 = GestionRegistre.getFreeRegToOpTab();
                boolean forcementVrai = false;
                if(a.getFils1().getNoeud() == Noeud.Ident){
                    if(a.getFils1().getChaine().toLowerCase().equals("true")){
                        Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(1), reg1);
                        Prog.ajouter(loadInst, "Chargement de la variable booleenne true (1) dans le registre " + reg1.getRegistre());
                        forcementVrai = true;
                    }
                    else{ 
                    	if(a.getFils1().getChaine().toLowerCase() == "false"){
	                        Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(-1), reg1);
	                        Prog.ajouter(loadInst, "Chargement de la variable booleenne false (-1) dans le registre " + reg1.getRegistre());
	                    }
	                    else{
	                    	varName = a.getFils1().getChaine();
	                        placeEnPile = decl.indexOf(varName) + 1;
	                        Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg1);
	                        Prog.ajouter(loadInst, "Chargement de la variable booleenne dans le registre " + reg1.getRegistre());                            
	                    }                    
	                    Inst compareToZero = Inst.creation2(Operation.CMP, Operande.creationOpEntier(0), reg1);
	                    Prog.ajouter(compareToZero, "Comparaison du registre " + reg1.getRegistre() + " Ã  0");
	                    Inst jump = Inst.creation1(Operation.BGT, Operande.creationOpEtiq(Etiq.lEtiq(nomEtiqPositive)));
	                    Prog.ajouter(jump, "On saute a la fin du ou");
                    }
                }
                else{
		   			GestionRegistre.libererRegistre(reg1.getRegistre());
                    reg1 = coder_EXP(a.getFils1());    
                }
                                
                if(forcementVrai){
                }
                else{
                	if(a.getFils2().getNoeud() == Noeud.Ident){
                        if(a.getFils2().getChaine().toLowerCase().equals("true")){
                            Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(1), reg1);
                            Prog.ajouter(loadInst, "Chargement de la variable booleenne true (1) dans le registre " + reg1.getRegistre());
                        }
                        else{
                        	if(a.getFils2().getChaine().toLowerCase() == "false"){
	                            Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(-1), reg1);
	                            Prog.ajouter(loadInst, "Chargement de la variable booleenne false (-1) dans le registre " + reg1.getRegistre());
	                        }
	                        else{
	                        	varName = a.getFils2().getChaine();
	                            placeEnPile = decl.indexOf(varName) + 1;
	                            Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg1);
	                            Prog.ajouter(loadInst, "Chargement de la variable booleenne dans le registre " + reg1.getRegistre());                            
	                        }
                        }
                    }
                    else{
    		   			GestionRegistre.libererRegistre(reg1.getRegistre());
                        reg1 = coder_EXP(a.getFils2());
                    }
                	
                }
            	Prog.ajouter(Etiq.lEtiq(nomEtiqPositive));
                return reg1;
                
	   		case Egal :
		   		reg1 = GestionRegistre.getFreeRegToOpTab();
		   		reg2 = GestionRegistre.getFreeRegToOpTab();
		   		
		   		if(a.getFils1().getNoeud()==Noeud.Ident){
		   			if(a.getFils1().getChaine().toLowerCase().equals("true")){
		   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(1), reg1);
                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
		   			}
		   			else{ 
		   				if(a.getFils1().getChaine().toLowerCase().equals("false")){
			   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(-1), reg1);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
			   			}
			   			else{
	                    	varName = a.getFils1().getChaine();
	                        placeEnPile = decl.indexOf(varName) + 1;
	                        Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg1);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
			   				
			   			}
		   			}
		   		}
		   		else{
		   			if(a.getFils1().getDecor().getType()==Type.Integer){
                        Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(a.getFils1().getEntier()), reg1);
                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
		   						   				
		   			}
		   			else{ 
		   				if(a.getFils1().getDecor().getType()==Type.Real){
			   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils1().getReel()), reg1);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
			   			}
			   			else{ 
			   				if(a.getFils1().getDecor().getType()==Type.String){
				   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpChaine(a.getFils1().getChaine()), reg1);
		                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
				   			}
				   			else{
		    		   			GestionRegistre.libererRegistre(reg1.getRegistre());
				   				reg1 = coder_EXP(a.getFils1());
				   			}
			   			}
		   			}
		   		}
		   		
		   		if(a.getFils2().getNoeud()==Noeud.Ident){
		   			if(a.getFils2().getChaine().toLowerCase().equals("true")){
		   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(1), reg2);
                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
		   			}
		   			else{ 
		   				if(a.getFils2().getChaine().toLowerCase().equals("false")){
			   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(-1), reg2);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
			   			}
			   			else{
	                    	varName = a.getFils2().getChaine();
	                        placeEnPile = decl.indexOf(varName) + 1;
	                        Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg2);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
			   				
			   			}
		   			}
		   		}
		   		else{
		   			if(a.getFils2().getDecor().getType()==Type.Integer){
                        Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(a.getFils2().getEntier()), reg2);
                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
		   						   				
		   			}
		   			else{ 
		   				if(a.getFils2().getDecor().getType()==Type.Real){
			   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils2().getReel()), reg2);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
			   			}
			   			else{ 
			   				if(a.getFils2().getDecor().getType()==Type.String){
				   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpChaine(a.getFils2().getChaine()), reg2);
		                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
				   			}
				   			else{
		    		   			GestionRegistre.libererRegistre(reg2.getRegistre());
				   				reg2 = coder_EXP(a.getFils2());
				   			}
			   			}
		   			}
		   		}
		   		String nomEtiqInegalite = "inegalite" + nbEtiq;
		   		Etiq equalEtiq = Etiq.lEtiq(nomEtiqInegalite);
		   		nbEtiq++;
		   		Inst compareInst = Inst.creation2(Operation.CMP, reg1, reg2);
		   		Inst setFalseInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(-1), reg1);
		   		Inst jumpIfNonEqualInst = Inst.creation1(Operation.BNE, Operande.creationOpEtiq(Etiq.lEtiq(nomEtiqInegalite)));
		   		Inst setTrueInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(1), reg1);
		   		
		   		Prog.ajouter(compareInst, "Ajout de l'instruction de comparaison entre " + reg1.getRegistre() + " et " + reg2.getRegistre());
		   		Prog.ajouter(setFalseInst, "Mise a false (-1) du registre " + reg1.getRegistre());
		   		Prog.ajouter(jumpIfNonEqualInst, "Ajout de l'instruction de saut s'il n'y a pas Ã©galitÃ©");
		   		Prog.ajouter(setTrueInst, "Mise a true (1) du registre " + reg1.getRegistre());
		   		Prog.ajouter(Etiq.lEtiq(nomEtiqInegalite));

		   		
	   			GestionRegistre.libererRegistre(reg2.getRegistre());
		   		return reg1;
		   		
	   		case InfEgal :
	   			reg1 = GestionRegistre.getFreeRegToOpTab();
		   		reg2 = GestionRegistre.getFreeRegToOpTab();
		   		
		   		
		   		if(a.getFils1().getNoeud()==Noeud.Ident){
		   			if(a.getFils1().getChaine().toLowerCase().equals("true")){
		   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(1), reg1);
                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
		   			}
		   			else{ 
		   				if(a.getFils1().getChaine().toLowerCase().equals("false")){
			   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(-1), reg1);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
			   			}
			   			else{
	                    	varName = a.getFils1().getChaine();
	                        placeEnPile = decl.indexOf(varName) + 1;
	                        Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg1);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
			   			}
		   			}
		   		}
		   		else{
		   			if(a.getFils1().getDecor().getType()==Type.Integer){
                        Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(a.getFils1().getEntier()), reg1);
                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
		   						   				
		   			}
		   			else{ 
		   				if(a.getFils1().getDecor().getType()==Type.Real){
			   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils1().getReel()), reg1);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
			   			}
			   			else{ 
			   				if(a.getFils1().getDecor().getType()==Type.String){
				   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpChaine(a.getFils1().getChaine()), reg1);
		                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
				   			}
				   			else{
		    		   			GestionRegistre.libererRegistre(reg1.getRegistre());
				   				reg1 = coder_EXP(a.getFils1());
				   			}
			   			}
		   			}
		   		}
		   		
		   		if(a.getFils2().getNoeud()==Noeud.Ident){
		   			if(a.getFils2().getChaine().toLowerCase().equals("true")){
		   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(1), reg2);
                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
		   			}
		   			else{ 
		   				if(a.getFils2().getChaine().toLowerCase().equals("false")){
			   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(-1), reg2);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
			   			}
			   			else{
	                    	varName = a.getFils2().getChaine();
	                        placeEnPile = decl.indexOf(varName) + 1;
	                        Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg2);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
			   			}		   				
		   			}
		   		}
		   		else{
		   			if(a.getFils2().getDecor().getType()==Type.Integer){
                        Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(a.getFils2().getEntier()), reg2);
                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
		   						   				
		   			}
		   			else{ 
		   				if(a.getFils2().getDecor().getType()==Type.Real){
			   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils2().getReel()), reg2);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
			   			}
			   			else{ 
			   				if(a.getFils2().getDecor().getType()==Type.String){
				   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpChaine(a.getFils2().getChaine()), reg2);
		                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
				   			}
				   			else{
		    		   			GestionRegistre.libererRegistre(reg2.getRegistre());
				   				reg2 = coder_EXP(a.getFils2());
				   			}
			   			}
		   			}
		   		}
		   		String nomEtiqNonInferieurOuEgal = "nonInferieurOuEgal" + nbEtiq;
		   		Etiq equalEtiq2 = Etiq.lEtiq(nomEtiqNonInferieurOuEgal);
		   		nbEtiq++;
		   		Inst compareInst2 = Inst.creation2(Operation.CMP, reg1, reg2);
		   		Inst setFalseInst2 = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(-1), reg1);
		   		Inst jumpIfNonEqualInst2 = Inst.creation1(Operation.BLE, Operande.creationOpEtiq(Etiq.lEtiq(nomEtiqNonInferieurOuEgal)));
		   		Inst setTrueInst2 = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(1), reg1);
		   		
		   		Prog.ajouter(compareInst2, "Ajout de l'instruction de comparaison entre " + reg1.getRegistre() + " et " + reg2.getRegistre());
		   		Prog.ajouter(setFalseInst2, "Mise a false (-1) du registre " + reg1.getRegistre());
		   		Prog.ajouter(jumpIfNonEqualInst2, "Ajout de l'instruction de saut s'il n'y a pas inferioritÃ© ou Ã©galitÃ©");
		   		Prog.ajouter(setTrueInst2, "Mise a true (1) du registre " + reg1.getRegistre());
		   		Prog.ajouter(Etiq.lEtiq(nomEtiqNonInferieurOuEgal));

		   		
	   			GestionRegistre.libererRegistre(reg2.getRegistre());
		   		return reg1;
	   		case Inf :
	   			reg1 = GestionRegistre.getFreeRegToOpTab();
		   		reg2 = GestionRegistre.getFreeRegToOpTab();
		   		
		   		
		   		if(a.getFils1().getNoeud()==Noeud.Ident){
		   			if(a.getFils1().getChaine().toLowerCase().equals("true")){
		   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(1), reg1);
                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
		   			}
		   			else{ 
		   				if(a.getFils1().getChaine().toLowerCase().equals("false")){
			   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(-1), reg1);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
			   			}
			   			else{
	                    	varName = a.getFils1().getChaine();
	                        placeEnPile = decl.indexOf(varName) + 1;
	                        Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg1);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
			   			}
		   			}
		   		}
		   		else{
		   			if(a.getFils1().getDecor().getType()==Type.Integer){
                        Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(a.getFils1().getEntier()), reg1);
                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
		   						   				
		   			}
		   			else{ 
		   				if(a.getFils1().getDecor().getType()==Type.Real){
			   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils1().getReel()), reg1);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
			   			}
			   			else{ 
			   				if(a.getFils1().getDecor().getType()==Type.String){
				   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpChaine(a.getFils1().getChaine()), reg1);
		                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
				   			}
				   			else{
		    		   			GestionRegistre.libererRegistre(reg1.getRegistre());
				   				reg1 = coder_EXP(a.getFils1());
				   			}
			   			}
		   			}
		   		}
		   		
		   		if(a.getFils2().getNoeud()==Noeud.Ident){
		   			if(a.getFils2().getChaine().toLowerCase().equals("true")){
		   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(1), reg2);
                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
		   			}
		   			else{ 
		   				if(a.getFils2().getChaine().toLowerCase().equals("false")){
			   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(-1), reg2);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
			   			}
			   			else{
	                    	varName = a.getFils2().getChaine();
	                        placeEnPile = decl.indexOf(varName) + 1;
	                        Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg2);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
			   			}
		   			}
		   		}
		   		else{
		   			if(a.getFils2().getDecor().getType()==Type.Integer){
                        Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(a.getFils2().getEntier()), reg2);
                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
		   						   				
		   			}
		   			else{ 
		   				if(a.getFils2().getDecor().getType()==Type.Real){
			   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils2().getReel()), reg2);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
			   			}
			   			else{ 
			   				if(a.getFils2().getDecor().getType()==Type.String){
				   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpChaine(a.getFils2().getChaine()), reg2);
		                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
				   			}
				   			else{
		    		   			GestionRegistre.libererRegistre(reg2.getRegistre());
				   				reg2 = coder_EXP(a.getFils2());
				   			}
			   			}
		   			}
		   		}
		   		String nomEtiqNonInferieurStrict = "nonInferieurStrict" + nbEtiq;
		   		Etiq equalEtiq3 = Etiq.lEtiq(nomEtiqNonInferieurStrict);
		   		nbEtiq++;
		   		Inst compareInst3 = Inst.creation2(Operation.CMP, reg1, reg2);
		   		Inst setFalseInst3 = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(-1), reg1);
		   		Inst jumpIfNonEqualInst3 = Inst.creation1(Operation.BLT, Operande.creationOpEtiq(Etiq.lEtiq("nomEtiqNonInferieurStrict")));
		   		Inst setTrueInst3 = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(1), reg1);
		   		
		   		Prog.ajouter(compareInst3, "Ajout de l'instruction de comparaison entre " + reg1.getRegistre() + " et " + reg2.getRegistre());
		   		Prog.ajouter(setFalseInst3, "Mise a false (-1) du registre " + reg1.getRegistre());
		   		Prog.ajouter(jumpIfNonEqualInst3, "Ajout de l'instruction de saut s'il n'y a pas inferioritÃ© stricte");
		   		Prog.ajouter(setTrueInst3, "Mise a true (1) du registre " + reg1.getRegistre());
		   		Prog.ajouter(Etiq.lEtiq("nomEtiqNonInferieurStrict"));

		   		
	   			GestionRegistre.libererRegistre(reg2.getRegistre());
		   		return reg1;
	   		case SupEgal :
	   			reg1 = GestionRegistre.getFreeRegToOpTab();
		   		reg2 = GestionRegistre.getFreeRegToOpTab();
		   		
		   		
		   		if(a.getFils1().getNoeud()==Noeud.Ident){
		   			if(a.getFils1().getChaine().toLowerCase().equals("true")){
		   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(1), reg1);
                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
		   			}
		   			else{ 
		   				if(a.getFils1().getChaine().toLowerCase().equals("false")){
			   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(-1), reg1);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
			   			}
			   			else{
	                    	varName = a.getFils1().getChaine();
	                        placeEnPile = decl.indexOf(varName) + 1;
	                        Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg1);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
			   			}
		   			}
		   		}
		   		else{
		   			if(a.getFils1().getDecor().getType()==Type.Integer){
                        Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(a.getFils1().getEntier()), reg1);
                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
		   						   				
		   			}
		   			else{ 
		   				if(a.getFils1().getDecor().getType()==Type.Real){
			   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils1().getReel()), reg1);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
			   			}
			   			else{ 
			   				if(a.getFils1().getDecor().getType()==Type.String){
				   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpChaine(a.getFils1().getChaine()), reg1);
		                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
				   			}
				   			else{
		    		   			GestionRegistre.libererRegistre(reg1.getRegistre());
				   				reg1 = coder_EXP(a.getFils1());
				   			}
			   			}
		   			}
		   		}
		   		
		   		if(a.getFils2().getNoeud()==Noeud.Ident){
		   			if(a.getFils2().getChaine().toLowerCase().equals("true")){
		   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(1), reg2);
                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
		   			}
		   			else{ 
		   				if(a.getFils2().getChaine().toLowerCase().equals("false")){
			   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(-1), reg2);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
			   			}
			   			else{
	                    	varName = a.getFils2().getChaine();
	                        placeEnPile = decl.indexOf(varName) + 1;
	                        Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg2);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
			   			}	
		   			}
		   		}
		   		else{
		   			if(a.getFils2().getDecor().getType()==Type.Integer){
                        Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(a.getFils2().getEntier()), reg2);
                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
		   						   				
		   			}
		   			else{ 
		   				if(a.getFils2().getDecor().getType()==Type.Real){
			   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils2().getReel()), reg2);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
			   			}
			   			else{ 
			   				if(a.getFils2().getDecor().getType()==Type.String){
				   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpChaine(a.getFils2().getChaine()), reg2);
		                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
				   			}
				   			else{
		    		   			GestionRegistre.libererRegistre(reg2.getRegistre());
				   				reg2 = coder_EXP(a.getFils2());
				   			}
			   			}
		   			}
		   		}
		   		String nomEtiqNonSuperieurOuEgal = "nonSuperieurOuEgal" + nbEtiq;
		   		Etiq equalEtiq4 = Etiq.lEtiq(nomEtiqNonSuperieurOuEgal);
		   		nbEtiq++;
		   		Inst compareInst4 = Inst.creation2(Operation.CMP, reg1, reg2);
		   		Inst setFalseInst4 = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(-1), reg1);
		   		Inst jumpIfNonEqualInst4 = Inst.creation1(Operation.BGE, Operande.creationOpEtiq(Etiq.lEtiq(nomEtiqNonSuperieurOuEgal)));
		   		Inst setTrueInst4 = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(1), reg1);
		   		
		   		Prog.ajouter(compareInst4, "Ajout de l'instruction de comparaison entre " + reg1.getRegistre() + " et " + reg2.getRegistre());
		   		Prog.ajouter(setFalseInst4, "Mise a false (-1) du registre " + reg1.getRegistre());
		   		Prog.ajouter(jumpIfNonEqualInst4, "Ajout de l'instruction de saut s'il n'y a pas supÃ©rioritÃ© ou Ã©galitÃ©");
		   		Prog.ajouter(setTrueInst4, "Mise a true (1) du registre " + reg1.getRegistre());
		   		Prog.ajouter(Etiq.lEtiq(nomEtiqNonSuperieurOuEgal));

		   		
	   			GestionRegistre.libererRegistre(reg2.getRegistre());
		   		return reg1;
	   		case Sup :
	   			reg1 = GestionRegistre.getFreeRegToOpTab();
		   		reg2 = GestionRegistre.getFreeRegToOpTab();
		   		
		   		
		   		if(a.getFils1().getNoeud()==Noeud.Ident){
		   			if(a.getFils1().getChaine().toLowerCase().equals("true")){
		   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(1), reg1);
                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
		   			}
		   			else{ 
		   				if(a.getFils1().getChaine().toLowerCase().equals("false")){
			   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(-1), reg1);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
			   			}
			   			else{
	                    	varName = a.getFils1().getChaine();
	                        placeEnPile = decl.indexOf(varName) + 1;
	                        Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg1);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
			   			}
		   			}
		   		}
		   		else{
		   			if(a.getFils1().getDecor().getType()==Type.Integer){
                        Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(a.getFils1().getEntier()), reg1);
                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
		   						   				
		   			}
		   			else{ 
		   				if(a.getFils1().getDecor().getType()==Type.Real){
			   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils1().getReel()), reg1);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
			   			}
			   			else{ 
			   				if(a.getFils1().getDecor().getType()==Type.String){
				   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpChaine(a.getFils1().getChaine()), reg1);
		                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
				   			}
				   			else{
		    		   			GestionRegistre.libererRegistre(reg1.getRegistre());
				   				reg1 = coder_EXP(a.getFils1());
				   			}
			   			}
		   			}
		   		}
		   		
		   		if(a.getFils2().getNoeud()==Noeud.Ident){
		   			if(a.getFils2().getChaine().toLowerCase().equals("true")){
		   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(1), reg2);
                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
		   			}
		   			else{ 
		   				if(a.getFils2().getChaine().toLowerCase().equals("false")){
			   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(-1), reg2);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
			   			}
			   			else{
	                    	varName = a.getFils2().getChaine();
	                        placeEnPile = decl.indexOf(varName) + 1;
	                        Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg2);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
			   			}
		   			}
		   		}
		   		else{
		   			if(a.getFils2().getDecor().getType()==Type.Integer){
                        Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(a.getFils2().getEntier()), reg2);
                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
		   						   				
		   			}
		   			else{ 
		   				if(a.getFils2().getDecor().getType()==Type.Real){
			   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils2().getReel()), reg2);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
			   			}
			   			else{ 
			   				if(a.getFils2().getDecor().getType()==Type.String){
				   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpChaine(a.getFils2().getChaine()), reg2);
		                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
				   			}
				   			else{
		    		   			GestionRegistre.libererRegistre(reg2.getRegistre());
				   				reg2 = coder_EXP(a.getFils2());
				   			}
			   			}
		   			}
		   		}
		   		String nomEtiqNonSuperieurStrict = "nonSuperieurStrict" + nbEtiq;
		   		Etiq equalEtiq5 = Etiq.lEtiq(nomEtiqNonSuperieurStrict);
		   		nbEtiq++;
		   		Inst compareInst5 = Inst.creation2(Operation.CMP, reg1, reg2);
		   		Inst setFalseInst5 = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(-1), reg1);
		   		Inst jumpIfNonEqualInst5 = Inst.creation1(Operation.BGT, Operande.creationOpEtiq(Etiq.lEtiq(nomEtiqNonSuperieurStrict)));
		   		Inst setTrueInst5 = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(1), reg1);
		   		
		   		Prog.ajouter(compareInst5, "Ajout de l'instruction de comparaison entre " + reg1.getRegistre() + " et " + reg2.getRegistre());
		   		Prog.ajouter(setFalseInst5, "Mise a false (-1) du registre " + reg1.getRegistre());
		   		Prog.ajouter(jumpIfNonEqualInst5, "Ajout de l'instruction de saut s'il n'y a pas supÃ©rioritÃ© stricte");
		   		Prog.ajouter(setTrueInst5, "Mise a true (1) du registre " + reg1.getRegistre());
		   		Prog.ajouter(Etiq.lEtiq(nomEtiqNonSuperieurStrict));

	   			GestionRegistre.libererRegistre(reg2.getRegistre());
		   		return reg1;
	   		case NonEgal :
	   			reg1 = GestionRegistre.getFreeRegToOpTab();
		   		reg2 = GestionRegistre.getFreeRegToOpTab();
		   		
		   		
		   		if(a.getFils1().getNoeud()==Noeud.Ident){
		   			if(a.getFils1().getChaine().toLowerCase().equals("true")){
		   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(1), reg1);
                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
		   			}
		   			else{ 
		   				if(a.getFils1().getChaine().toLowerCase().equals("false")){
			   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(-1), reg1);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
			   			}
			   			else{
	                    	varName = a.getFils1().getChaine();
	                        placeEnPile = decl.indexOf(varName) + 1;
	                        Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg1);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
			   			}
		   			}
		   		}
		   		else{
		   			if(a.getFils1().getDecor().getType()==Type.Integer){
                        Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(a.getFils1().getEntier()), reg1);
                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
		   						   				
		   			}
		   			else{ 
		   				if(a.getFils1().getDecor().getType()==Type.Real){
			   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils1().getReel()), reg1);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
			   			}
			   			else{ 
			   				if(a.getFils1().getDecor().getType()==Type.String){
				   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpChaine(a.getFils1().getChaine()), reg1);
		                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg1.getRegistre());
				   			}
				   			else{
		    		   			GestionRegistre.libererRegistre(reg1.getRegistre());
				   				reg1 = coder_EXP(a.getFils1());
				   			}
			   			}
		   			}
		   		}
		   		
		   		if(a.getFils2().getNoeud()==Noeud.Ident){
		   			if(a.getFils2().getChaine().toLowerCase().equals("true")){
		   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(1), reg2);
                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
		   			}
		   			else{ 
		   				if(a.getFils2().getChaine().toLowerCase().equals("false")){
			   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(-1), reg2);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
			   			}
			   			else{
	                    	varName = a.getFils2().getChaine();
	                        placeEnPile = decl.indexOf(varName) + 1;
	                        Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpIndirect(placeEnPile, Registre.GB), reg2);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
			   			}
		   			}
		   		}
		   		else{
		   			if(a.getFils2().getDecor().getType()==Type.Integer){
                        Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(a.getFils2().getEntier()), reg2);
                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
		   						   				
		   			}
		   			else{ 
		   				if(a.getFils2().getDecor().getType()==Type.Real){
			   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpReel(a.getFils2().getReel()), reg2);
	                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
			   			}
			   			else{ 
			   				if(a.getFils2().getDecor().getType()==Type.String){
				   				Inst loadInst = Inst.creation2(Operation.LOAD, Operande.creationOpChaine(a.getFils2().getChaine()), reg2);
		                        Prog.ajouter(loadInst, "Chargement de la valeur dans le registre " + reg2.getRegistre());
				   			}
				   			else{
		    		   			GestionRegistre.libererRegistre(reg2.getRegistre());
				   				reg2 = coder_EXP(a.getFils2());
				   			}
			   			}
		   			}
		   		}
		   		String nomEtiqEgalite = "egalite" + nbEtiq;
		   		Etiq equalEtiq6 = Etiq.lEtiq(nomEtiqEgalite);
		   		nbEtiq++;
		   		Inst compareInst6 = Inst.creation2(Operation.CMP, reg1, reg2);
		   		Inst setFalseInst6 = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(-1), reg1);
		   		Inst jumpIfNonEqualInst6 = Inst.creation1(Operation.BEQ, Operande.creationOpEtiq(Etiq.lEtiq(nomEtiqEgalite)));
		   		Inst setTrueInst6 = Inst.creation2(Operation.LOAD, Operande.creationOpEntier(1), reg1);
		   		
		   		Prog.ajouter(compareInst6, "Ajout de l'instruction de comparaison entre " + reg1.getRegistre() + " et " + reg2.getRegistre());
		   		Prog.ajouter(setFalseInst6, "Mise a false (-1) du registre " + reg1.getRegistre());
		   		Prog.ajouter(jumpIfNonEqualInst6, "Ajout de l'instruction de saut s'il y a Ã©galitÃ©");
		   		Prog.ajouter(setTrueInst6, "Mise a true (1) du registre " + reg1.getRegistre());
		   		Prog.ajouter(Etiq.lEtiq(nomEtiqEgalite));

		   		
	   			GestionRegistre.libererRegistre(reg2.getRegistre());
		   		return reg1;
	   		case Index:
	   			//TODO faire appel à load_index, verif que les intervalles sont dans la taille du tableau sinon retourner une erreur 
	   			//(ou bien modif load_index pour que ce soit elle qui fasse la verification) , et retourner un registre (via OpIndirect)
	   			//contenant l'adresse de la pile retourner par load_index (attribut offset et placeEnPile)
	   			Indice i = load_Index(a);
	   			Operande reg = GestionRegistre.getFreeRegToOpTab();
	   			if(reg == null){
	   				//alors aucun registre est libre
	   				//GestionRegistre.pushPile(i.offset.getRegistre());
	   				//Faudrait modif le retour de coder_EXP pour indiquer si on a placé en Pile ou pas.
	   			}
	   			else{
	   				
		   			Inst inst = Inst.creation2(Operation.LOAD, Operande.creationOpIndexe(i.placeEnPileOrigine, Registre.GB,i.offset.getRegistre()),reg);
		   			Prog.ajouter(inst,"Stockage en registre de la valeur pointée par le tableau");
	   			}
	   			if(i.offset.getNature().equals(NatureOperande.OpDirect)){
	   				GestionRegistre.libererRegistre(i.offset.getRegistre());
	   			}
	   			return reg;
		   }
	   }
	   return null;
   }
}

