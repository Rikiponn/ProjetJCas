package fr.esisar.compilation.verif;

import fr.esisar.compilation.global.src.*;
 
/**
 * Cette classe permet de réaliser la vérification et la décoration 
 * de l'arbre abstrait d'un programme.
 */
public class Verif {

   private Environ env; // L'environnement des identificateurs

   /**
    * Constructeur.
    */
   public Verif() {
      env = new Environ();
   }

   /**
    * Vérifie les contraintes contextuelles du programme correspondant à 
    * l'arbre abstrait a, qui est décoré et enrichi. 
    * Les contraintes contextuelles sont décrites 
    * dans Context.txt.
    * En cas d'erreur contextuelle, un message d'erreur est affiché et 
    * l'exception ErreurVerif est levée.
    */
   public void verifierDecorer(Arbre a) throws ErreurVerif {
      verif_PROGRAMME(a);
   }

   /**
    * Initialisation de l'environnement avec les identificateurs prédéfinis.
    */
   private void initialiserEnv() {
      Defn def;
      // integer
      def = Defn.creationType(Type.Integer);
      def.setGenre(Genre.PredefInteger);
      env.enrichir("integer", def);
      
      // boolean
      def = Defn.creationType(Type.Boolean);
      def.setGenre(Genre.PredefBoolean);
      env.enrichir("boolean", def);
      
      // real
      def = Defn.creationType(Type.Real);
      def.setGenre(Genre.PredefReal);
      env.enrichir("real", def);
      
      // true
      def = Defn.creationConstBoolean(true);
      def.setGenre(Genre.PredefTrue);
      def.setValeurBoolean(true);
      env.enrichir("true", def);
      
      // false
      def = Defn.creationConstBoolean(false);
      def.setGenre(Genre.PredefFalse);
      def.setValeurBoolean(false);
      env.enrichir("false", def);
      
      // max_int
      def = Defn.creationConstInteger(java.lang.Integer.MAX_VALUE);
      def.setGenre(Genre.PredefMaxInt);
      def.setValeurInteger(java.lang.Integer.MAX_VALUE);
      env.enrichir("max_int", def);
      
      // ------------
      // A COMPLETER
      // ------------
   }

   /**************************************************************************
    * PROGRAMME
    **************************************************************************/
   private void verif_PROGRAMME(Arbre a) throws ErreurVerif {
      initialiserEnv();
      decor(a);
      verif_ListeDecl(a.getFils1());
      verif_ListeInst(a.getFils2());
   }

   /**************************************************************************
    * LISTE_DECL
    **************************************************************************/
   private void verif_ListeDecl(Arbre a) throws ErreurVerif {
	   switch(a.getNoeud()){
    	case Vide:
    		break;
    	case ListeDecl:
    		verif_ListeDecl(a.getFils1());
			verif_Decl(a.getFils2());
    		break;    		
    	default:
    		throw new ErreurInterneVerif("Arbre incorrect dans verif_LISTE_DECL");
    }
   }

   
   private void verif_Decl(Arbre a) throws ErreurVerif {
	   switch(a.getNoeud()){
	   case Decl:
		   verif_ListeIdent(a,a.getFils1());
		   verif_Type(a.getFils2());
		   break;
	   default:
		   throw new ErreurInterneVerif("Arbre incorrect dans verif_DECL");
    }
   }
   
   
   private void verif_ListeIdent(Arbre pere,Arbre a) throws ErreurVerif{
	   switch(a.getNoeud()){
	   case Vide:
		   break;
	   case ListeIdent:
		   verif_ListeIdent(a,a.getFils1());
		   verif_Ident(a,a.getFils2());
		   break;
	   
	   default:
		   throw new ErreurInterneVerif("Arbre incorrect dans verif_ListeIdent");
	   }
   }
   
   private void verif_Ident(Arbre pere, Arbre a) throws ErreurVerif{
	   Defn def = Defn.creationVar(cherche_Type(pere.getFils2()));
	   a.setDecor(new Decor(def));
	   env.enrichir(a.getChaine(), def);
   }
   
   private Type cherche_Type(Arbre a) throws ErreurVerif{
	   switch(a.getNoeud()){
		   case Entier:
			   return(Type.Integer);
		   case Reel:
			   return(Type.Real);
		   case Chaine:
			   return(Type.String);
		   case Tableau:
			   if(a.getArite() != 2){
	   			   ErreurContext e = ErreurContext.ErreurArite;
	   			   e.leverErreurContext(null, a.getNumLigne());
	   		   }
			   return(Type.creationArray(Type.creationInterval(a.getFils1().getFils1().getEntier(), a.getFils1().getFils2().getEntier()), cherche_Type(a.getFils2())));
		   case Ident:
			   if(env.chercher(a.toString()) == null){
	  			   ErreurContext e = ErreurContext.ErreurIdentNonDeclaree;
	  			   e.leverErreurContext(a.getFils1().toString(), a.getNumLigne());
	    		}
			   return(a.getDecor().getType());
			   
		default:
			   throw new ErreurInterneVerif("Arbre incorrect dans cherche_Type");
	   }
   }
   
   private void verif_Type(Arbre a) {
	   switch(a.getNoeud()){
	   case Entier:
		   a.setDecor(new Decor(new Defn(NatureDefn.Type,Type.Integer),Type.Integer));
		   break;
	   case Reel:
		   a.setDecor(new Decor(new Defn(NatureDefn.Type,Type.Real),Type.Real));
		   break;
	   case Chaine:
		   a.setDecor(new Decor(new Defn(NatureDefn.Type,Type.String),Type.String));
		   break;
	   case Tableau:
		   verif_Type(a.getFils2());
		   break;
	   default:
		   throw new ErreurInterneVerif("Arbre incorrect dans verif_Type");
			   
	   }
	
   }

/**************************************************************************
    * LISTE_INST
    **************************************************************************/
   private void verif_ListeInst(Arbre a) throws ErreurVerif {
	   switch(a.getNoeud()){
     	case Vide:
     		break;
     	case ListeInst:
     		verif_ListeInst(a.getFils1());
     		verif_INST(a.getFils2());
     		break;
     		
     	default:
     		throw new ErreurInterneVerif("Arbre incorrect dans verifier_LISTE_INST");
     }
   }

   /**************************************************************************
    * INST
    **************************************************************************/
   private void verif_INST(Arbre a) throws ErreurVerif {
	   switch(a.getNoeud()){
	   case Affect:
		   verif_Affect(a);
		   break;
	   case Ecriture:
		   verif_Ecriture(a);
		   break;
	   case Lecture:
		   verif_Lecture(a);
		   break;
	   case Pour:
		   verif_Pour(a);
		   break;
	   case Si:
		   verif_Si(a);
		   break;
	   case TantQue:
		   verif_TantQue(a);
		   break;
	   default:
		   throw new ErreurInterneVerif("Arbre incorrect dans verifier_INST"); 
   }
   // ------------------------------------------------------------------------
   // COMPLETER les operations de vérifications et de décoration pour toutes 
   // les constructions d'arbres
   // ------------------------------------------------------------------------

}
   private void decor(Arbre a) throws ErreurVerif{
	   
	   switch(a.getNoeud()){
	   case Entier:
		   a.setDecor(new Decor(Type.Integer));
		   break;
	   case Reel:
		   a.setDecor(new Decor(Type.Real));
		   break;
	   case Chaine:
		   a.setDecor(new Decor(Type.String));
		   break;
	   case Tableau:
		   verif_Type(a.getFils2());
		   break;
	   case Ident:
		   a.setDecor(new Decor(env.chercher(a.getFils1().toString()), env.chercher(a.getFils1().toString()).getType()));
		   break;
	   case Conversion:
		   if(a.getFils1().getNoeud()==Noeud.Entier){
			   a.setDecor(new Decor(new Defn(NatureDefn.Type, Type.Real), Type.Real));
			   break;
		   }
		   else if(a.getFils1().getNoeud()==Noeud.Reel){
			   a.setDecor(new Decor(new Defn(NatureDefn.Type, Type.Integer), Type.Integer));
			   break;
		   }
		   
	   default:
			   
	   }
	   
	   switch(a.getArite()){
	   case 0: break;
	   case 1: 
		   decor(a.getFils1());
		   break;
	   case 2:
		   decor(a.getFils1());
		   decor(a.getFils2());
		   break;
	   case 3:
		   decor(a.getFils1());
		   decor(a.getFils2());
		   decor(a.getFils3());
		   break;		   
	   
	   default:
		   ErreurContext e = ErreurContext.ErreurArite;
		   e.leverErreurContext(null, a.getNumLigne());
	   }
   }

   /**
    * Insère un Noeud Conversion sur les Noeuds signalés
    * @param a : arbre dont la racine est un Noeud d'arite 2, dont au moins un des fils doit utiliser un Noeud Conversion
    * @param checker : objet contenant le signalement sur les deux fils de a
    */
   private void add_Conversion(Arbre a, ResultatBinaireCompatible checker) {
	   if(checker.getConv1()) {
    	   Arbre filsTamp = a.getFils1();
    	   a.setFils1(Arbre.creation1(Noeud.Conversion, filsTamp, filsTamp.getNumLigne()));
	   }
	   if(checker.getConv2()) {
    	   Arbre filsTamp = a.getFils2();
    	   a.setFils2(Arbre.creation1(Noeud.Conversion, filsTamp, filsTamp.getNumLigne()));
	   }
   } 
   
   /**
    * Verification AFFECT
    * 		les fils doivent avoir un type compatible, si besoin avec une conversion.
    * @param a : arbre dont la racine est un Noeud Affect
    * @throws ErreurVerif
    */
   private void verif_Affect(Arbre a) throws ErreurVerif{ 

	   if(a.getArite() != Noeud.Affect.arite){
		   ErreurContext e = ErreurContext.ErreurArite;
		   e.leverErreurContext(null, a.getNumLigne());
	   }else{
		   //Si l'ident utilisé n'existe pas dans l'environnement : erreur
		   if(env.chercher(a.getFils1().toString()) == null){
			   ErreurContext e = ErreurContext.ErreurIdentNonDeclaree;
			   e.leverErreurContext(a.getFils1().toString(), a.getNumLigne());
		   }
		   else{
			   Type type2 = verif_Exp(a.getFils2());
			   ResultatAffectCompatible affectOk = ReglesTypage.affectCompatible(env.chercher(a.getFils1().getChaine()).getType(),type2);
			   
	           if(affectOk.getOk() == false){
	               ErreurContext e = ErreurContext.ErreurType;
	               e.leverErreurContext(null, a.getNumLigne());
	           }
	           if(affectOk.getConv2()) {
	        	   Arbre filsTamp = a.getFils2();
	        	   a.setFils1(Arbre.creation1(Noeud.Conversion, filsTamp, filsTamp.getNumLigne()));
	           }
		   }
       }
   }
   
   /**
    * Cette fonction vérifie tous les noeuds expression possible 
    * 	- Identifiants.
    * 	- Arité.
    * @param a Arbre partant d'un Noeud expression
    * @return le Type de l'expression : bool / reel / interval.
    * @throws ErreurVerif
    */
   private Type verif_Exp(Arbre a) throws ErreurVerif{
	   Type t1, t2;
	   ResultatBinaireCompatible resultB;
	   ResultatUnaireCompatible resultU;
	   switch(a.getNoeud()) {
	   		// opération binaire donnant un boolean
	   		case Et :
	   		case Ou :
	   		case Egal :
	   		case InfEgal :
	   		case Inf :
	   		case SupEgal :
	   		case Sup :
	   		case NonEgal :
	   		   if(a.getArite() != 2){
	   			   ErreurContext e = ErreurContext.ErreurArite;
	   			   e.leverErreurContext(null, a.getNumLigne());
	   		   }
	   		   resultB = ReglesTypage.binaireCompatible(a.getNoeud(), t1 =  verif_Exp(a.getFils1()), t2 = verif_Exp(a.getFils2()));
	   		   if(resultB.getOk() == false) {
	               ErreurContext e = ErreurContext.ErreurType;
	               e.leverErreurContext(null, a.getNumLigne());
	   		   }
	   		   else {
	   			   add_Conversion(a,resultB);
	   		   }
	   		   return Type.Boolean;
	   		// opération unaire donnant un boolean
	   		case Non :
	   		   if(a.getArite() != 1){
	   			   ErreurContext e = ErreurContext.ErreurArite;
	   			   e.leverErreurContext(null, a.getNumLigne());
	   		   }
	   		   resultU = ReglesTypage.unaireCompatible(a.getNoeud(), t1 = verif_Exp(a.getFils1()));
	   		   if(resultU.getOk() == false) {
	               ErreurContext e = ErreurContext.ErreurType;
	               e.leverErreurContext(null, a.getNumLigne());
	   		   }
	   		   return Type.Boolean;
	   		   
	   		// opération unaire donnant un real/integer
	   		case PlusUnaire :
	   		case MoinsUnaire :
	   		   if(a.getArite() != 1){
	   			   ErreurContext e = ErreurContext.ErreurArite;
	   			   e.leverErreurContext(null, a.getNumLigne());
	   		   }
	   		   resultU = ReglesTypage.unaireCompatible(a.getNoeud(), t1 = verif_Exp(a.getFils1()));
	   		   if(resultU.getOk() == false) {
	               ErreurContext e = ErreurContext.ErreurType;
	               e.leverErreurContext(null, a.getNumLigne());
	   		   }
	   		   return t1;
	   		   
	   		// Opération binaire donnant un real/integer
	   		case Plus :
	   		case Moins :
	   		case Mult :
	   		case Quotient :
	   		case DivReel :
	   		case Reste :
	   			if(a.getArite() != 2){
		   			   ErreurContext e = ErreurContext.ErreurArite;
		   			   e.leverErreurContext(null, a.getNumLigne());
	   		   }
	   		   resultB = ReglesTypage.binaireCompatible(a.getNoeud(), t1 =  verif_Exp(a.getFils1()), t2 = verif_Exp(a.getFils2()));
	   		   if(resultB.getOk() == false) {
	               ErreurContext e = ErreurContext.ErreurType;
	               e.leverErreurContext(null, a.getNumLigne());
	   		   }
	   		   else {
	   			   add_Conversion(a,resultB);
	   		   }
	   		   if(a.getNoeud() == Noeud.DivReel)
	   			   return Type.Integer;
	   		   if(t1 == Type.Integer || t2 == Type.Integer) {
	   			   return Type.Integer;
	   		   }
	   		   else {
	   			   return Type.Real;
	   		   }
	   		   
	   		// Si c'est un facteur
	   		case Entier :
	   			return Type.Integer;
	   		case Chaine :
	   			return Type.String;
	   		case Reel :
	   			return Type.Real;
	   			
	   		// Si c'est un ident / index
	   		case Ident :
	   			if(env.chercher(a.toString()) == null){
	  			   ErreurContext e = ErreurContext.ErreurIdentNonDeclaree;
	  			   e.leverErreurContext(a.getFils1().toString(), a.getNumLigne());
	    		}
	    		else {
	    			return a.getDecor().getType();
	    		}
	   		case Index :
	   			return verif_Index(a);
	   		default:
	 		   throw new ErreurInterneVerif("Arbre incorrect dans verifier_EXP");
	 		   
	   }
   }
   
   /**
    * Verification INDEX (place)
    * @param a : arbre dont la racine est un Noeud Index
    * @return le type du tableau indexé
    * @throws ErreurVerif
    */
   private Type verif_Index(Arbre a) throws ErreurVerif {
	   ErreurContext e;
	   if(a.getNoeud() == Noeud.Ident) {
		   if(a.getArite() != Noeud.Ident.arite) {
			   e = ErreurContext.ErreurArite;
			   e.leverErreurContext(null, a.getNumLigne());
		   }
		   else {
			   return a.getDecor().getType();
		   }
	   }
	   else {
		   if(a.getArite() != Noeud.Index.arite) {
			   e = ErreurContext.ErreurArite;
			   e.leverErreurContext(null, a.getNumLigne());
		   }
		   else {
			   verif_Index(a.getFils1());
		   	   return verif_Exp(a.getFils2());
		   }
	   }
	   // retour nécessaire pour ne pas avoir d'erreur, est théoriquement inutilisé
	   return Type.String;
   }
	
   /**
    * Verification READ
    * 		Fils1 doit être un identificateur (potentiel tableau) de type Interval/Real.
    * @param a : arbre dont la racine est Noeud Lecture
    * @throws ErreurVerif
    */
	private void verif_Lecture(Arbre a) throws ErreurVerif{
		Type t1 = verif_Index(a);
		if(t1 != Type.Integer && t1 != Type.Real) {
			ErreurContext e = ErreurContext.ErreurType;
            e.leverErreurContext(null, a.getNumLigne());
		}
	}
	
	/**
	 * Verification WRITE
	 * 		Fils1 doit être une liste d'expressions de type Interval/String/Integer.
	 * @param a : arbre dont la racine est Noeud Ecriture
	 * @throws ErreurVerif
	 */
	private void verif_Ecriture(Arbre a) throws ErreurVerif{
		ErreurContext e;
		if(a.getArite() != 2){
			   e = ErreurContext.ErreurArite;
			   e.leverErreurContext(null, a.getNumLigne());
	   }
		while(a.getFils1().getNoeud() != Noeud.Vide) {
			// les arguments de write ne peuvent pas être des boolean
			if(verif_Exp(a.getFils2()) == Type.Boolean) {
				e = ErreurContext.ErreurType;
	            e.leverErreurContext(null, a.getNumLigne());
			}
			
			a = a.getFils1();
			
			if(a.getArite() != 2){
	   			   e = ErreurContext.ErreurArite;
	   			   e.leverErreurContext(null, a.getNumLigne());
		   }
			
		}
	}

	/**
	 * Verification POUR
	 * 		Fils1 : Doit être un noeud In/Decrement, ses 3 Fils doivent être de type Integer.
	 * 		Fils2 : Doit être une liste d'instructions valide.
	 * @param a : arbre dont la racine est un Noeud Pour
	 * @throws ErreurVerif
	 */
	private void verif_Pour(Arbre a) throws ErreurVerif{
		ErreurContext e;
		if(a.getArite() != Noeud.Pour.arite){
			   e = ErreurContext.ErreurArite;
			   e.leverErreurContext(null, a.getNumLigne());
	   }
		if(a.getFils1().getNoeud() == Noeud.Increment || a.getFils1().getNoeud() == Noeud.Decrement) {
			if(a.getFils1().getFils1().getDecor().getType() != Type.Integer) {
				e = ErreurContext.ErreurType;
	            e.leverErreurContext(null, a.getNumLigne());
			}
			if(verif_Exp(a.getFils2()) != Type.Integer || verif_Exp(a.getFils3()) != Type.Integer) {
				e = ErreurContext.ErreurType;
	            e.leverErreurContext(null, a.getNumLigne());
			}
		}
		else {
			// TODO Nouveau type d'errreur : mauvais noeud increment/decrement dans FOR
			e = ErreurContext.ErreurType;
            e.leverErreurContext(null, a.getNumLigne());
		}
		verif_ListeInst(a.getFils2());
	}

	/**
	 * Verification SI :
	 * 		Fils1 doit être de type boolean.
	 * 		Fils2 et Fils3 doivent être des listes d'instruction valides
	 * @param a : arbre dont la racine est un Noeud Si
	 * @throws ErreurVerif
	 */
	private void verif_Si(Arbre a) throws ErreurVerif{
		ErreurContext e;
		if(a.getArite() != Noeud.Si.arite){
			   e = ErreurContext.ErreurArite;
			   e.leverErreurContext(null, a.getNumLigne());
	   }
		if(a.getFils1().getDecor().getType() != Type.Boolean) {
			// TODO Nouveau type d'errreur : mauvais type de condition if, expected boolean
			e = ErreurContext.ErreurType;
			e.leverErreurContext(null, a.getNumLigne());
		}
		verif_ListeInst(a.getFils2());
		verif_ListeInst(a.getFils3());
	}
	
	/**
	 * Verification TANTQUE :
	 * 		Fils1 doit être de type boolean.
	 * 		Fils2 doit être une liste d'instructions valide
	 * @param a : arbre dont la racine est un Noeud TantQue
	 * @throws ErreurVerif
	 */
	private void verif_TantQue(Arbre a) throws ErreurVerif{
		ErreurContext e;
		if(a.getArite() != Noeud.TantQue.arite){
			   e = ErreurContext.ErreurArite;
			   e.leverErreurContext(null, a.getNumLigne());
	   }
		if(a.getFils1().getDecor().getType() != Type.Boolean) {
			// TODO Nouveau type d'errreur : mauvais type de condition while, expected boolean
			e = ErreurContext.ErreurType;
			e.leverErreurContext(null, a.getNumLigne());
		}
		verif_ListeInst(a.getFils2());
	}
}
