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
      verif_ListeDecl(a.getFils1());
      decor(a.getFils2());
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
		   verif_ListeIdent(pere,a.getFils1());
		   verif_Ident(pere,a.getFils2());
		   break;
	   
	   default:
		   throw new ErreurInterneVerif("Arbre incorrect dans verif_ListeIdent");
	   }
   }
   
   private void verif_Ident(Arbre pere, Arbre a) throws ErreurVerif{
	   Defn def = Defn.creationVar(cherche_TypeDecl(pere.getFils2()));
	   a.setDecor(new Decor(def));
	   if(env.chercher(a.getChaine()) == null){
		   env.enrichir(a.getChaine(), def);
	   }
	   else{
		   ErreurContext e = ErreurContext.ErreurIdentReserve;
		   e.leverErreurContext(null, a.getNumLigne());
	   }
   }
   /**
    * Cette fonction renvoie l'entier pointé par a, a pouvant être un MoinsUnaire, PlusUnaire ou Entier
    * @param a Arbre
    * @return L'entier pointé par a
    * @throws ErreurVerif S'il n'y a pas d'entier dans la branche de a.
    */
   private int getIntArbre(Arbre a) throws ErreurVerif{
	   switch(a.getNoeud()) {
	   case Entier :
		   return a.getEntier();
	   case MoinsUnaire :
	   case PlusUnaire :
		   return getIntArbre(a.getFils1());
	   case Ident :
	   default :
		   ErreurContext e = ErreurContext.ErreurEntierAttendu;
		   e.leverErreurContext(null, a.getNumLigne());
	   }
	   return 0;
   }
   
   private Type cherche_TypeDecl(Arbre a) throws ErreurVerif{
	   switch(a.getNoeud()){
		   case Tableau:
			   if(a.getArite() != 2){
	   			   ErreurContext e = ErreurContext.ErreurArite;
	   			   e.leverErreurContext(null, a.getNumLigne());
	   		   }
			   return(Type.creationArray(Type.creationInterval(getIntArbre(a.getFils1().getFils1()), getIntArbre(a.getFils1().getFils2())), cherche_TypeDecl(a.getFils2())));
		   case Ident:
			   if(a.getChaine().toLowerCase().equals("integer")){
				   return Type.Integer;
			   }
			   if(a.getChaine().toLowerCase().equals("boolean")){
				   return Type.Boolean;
			   }
			   if(a.getChaine().toLowerCase().equals("string")){
				   return Type.String;
			   }
			   if(a.getChaine().toLowerCase().equals("float")){
				   return Type.Real;
			   }
			   ErreurContext e = ErreurContext.ErreurIdentNonDeclaree;
  			   e.leverErreurContext(a.getChaine(), a.getNumLigne());
		   case Intervalle:
			   return Type.creationInterval(getIntArbre(a.getFils1()), getIntArbre(a.getFils2()));
			   
			
		default:
			   throw new ErreurInterneVerif("Arbre incorrect dans cherche_TypeDecl "+ a.getNoeud().toString());
	   }
   }
   private Type cherche_Type(Arbre a) throws ErreurVerif{
	   ErreurContext e;
	switch(a.getNoeud()){
	   	   case Intervalle:
	   		   return(Type.creationInterval(getIntArbre(a.getFils1()),getIntArbre(a.getFils2())));
	   	   case Index:
	   		   if(a.getFils2().getNoeud().equals(Noeud.MoinsUnaire)){
	   			   e = ErreurContext.ErreurIndexNegatif;
		   		   e.leverErreurContext(null, a.getNumLigne());
	   		   }
	   		   if(a.getFils1().getNoeud().equals(Noeud.Ident)){
	   			   //Si l'identificateur n'existe pas
	   			   if(env.chercher(a.getFils1().getChaine()) == null){
	   				   e = ErreurContext.ErreurIdentNonDeclaree;
	   				   e.leverErreurContext(null, a.getNumLigne());
	   			   }
	   			   //Si le type des indices n'est pas Entier
	   			   if(cherche_Type(a.getFils2()) != (Type.Integer)){
	   				   e = ErreurContext.ErreurEntierAttendu;
	   				   e.leverErreurContext("dans Index", a.getNumLigne());
	   			   }
	   			   if(env.chercher(a.getFils1().getChaine()).getType().getNature().equals(NatureType.Array)){
	   				   return (env.chercher(a.getFils1().getChaine())).getType().getElement();
	   			   }
	   			   else{
	   				   e = ErreurContext.ErreurTableauAttendu;
	   				   e.leverErreurContext(null, a.getNumLigne());
	   			   }
	   		   }
	   		   
	   		   if(a.getFils1().getNoeud().equals(Noeud.Index)){
	   			   return cherche_Type(a.getFils1()).getElement();
	   		   }
	   	   case PlusUnaire:
	   	   case MoinsUnaire:
	   		   return cherche_Type(a.getFils1());
		   case Entier:
			   return(Type.Integer);
		   case Reel:
			   return(Type.Real);
		   case Chaine:
			   return(Type.String);
		   case Tableau:
			   if(a.getArite() != 2){
	   			   e = ErreurContext.ErreurArite;
	   			   e.leverErreurContext(null, a.getNumLigne());
	   		   }
			   return(Type.creationArray(Type.creationInterval(getIntArbre(a.getFils1().getFils1()), getIntArbre(a.getFils1().getFils2())), cherche_Type(a.getFils2())));
		   case Ident:
			   if(env.chercher(a.getChaine()) == null){
				   e = ErreurContext.ErreurIdentNonDeclaree;
	  			   e.leverErreurContext(a.getChaine(), a.getNumLigne());
			   }
			   return(a.getDecor().getType());
			   
		default:
			   throw new ErreurInterneVerif("Arbre incorrect dans cherche_Type "+a.getNoeud());
	   }
   }
   
   private void verif_Type(Arbre a) {
	   switch(a.getNoeud()){
	   case Intervalle:
		   break;
	   case Ident:
		   if(a.getChaine().toLowerCase().equals("integer")){
			   a.setDecor(new Decor(new Defn(NatureDefn.Type,Type.Integer)));
		   }
		   if(a.getChaine().toLowerCase().equals("boolean")){
			   a.setDecor(new Decor(new Defn(NatureDefn.Type,Type.Boolean)));
		   }
		   if(a.getChaine().toLowerCase().equals("string")){
			   a.setDecor(new Decor(new Defn(NatureDefn.Type,Type.String)));
		   }
		   if(a.getChaine().toLowerCase().equals("float")){
			   a.setDecor(new Decor(new Defn(NatureDefn.Type,Type.Real)));
		   }
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
	   case Nop:
		   break;
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
	   case Ligne:
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
	   /*if(a == null){
		   return;
	   }*/
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
	   case Index:
		   // prérequis : fils1 est un array, fonctionne à tous les degrés d'un tableau si celui-ci est valide. Renvoie le type du fils pour décorer a, a étant un Noeud.Index
		   decor(a.getFils1()); 
		   Type t = a.getFils1().getDecor().getType();
		   if(a.getFils1().getNoeud().equals(Noeud.Index)){
			   a.setDecor(new Decor(new Defn(NatureDefn.Var, t),t.getElement()));
		   }
		   else{
			   a.setDecor(new Decor(new Defn(NatureDefn.Var,t),t));
		   }
		   break;
		   
	   case Ident:
		   if(a.getChaine().toLowerCase().equals("integer")){
			   break;
		   }
		   if(a.getChaine().toLowerCase().equals("boolean")){
			   break;
		   }
		   if(a.getChaine().toLowerCase().equals("string")){
			   break;
		   }
		   if(a.getChaine().toLowerCase().equals("float")){
			   break;			  
		   }
		   if(env.chercher(a.getChaine()) == null){
			   ErreurContext e = ErreurContext.ErreurIdentNonDeclaree;
  			   e.leverErreurContext(a.getChaine(), a.getNumLigne());
		   }
		   a.setDecor(new Decor(env.chercher(a.getChaine()), env.chercher(a.getChaine()).getType()));
		   break;
	   case Affect:
		   //Si le fils est un index, on décor d'abord le fils, puis on décor le Noeud Affect
		   decor(a.getFils1());
		   
		   if(a.getFils1().getNoeud().equals(Noeud.Index)){
			   a.setDecor(new Decor(a.getFils1().getDecor().getType()));
		   }
		   
		   
		   //Si le Noeud est un Ident, on récupère son type depuis l'environnement
		   if(a.getFils1().getNoeud().equals(Noeud.Ident)){
			   a.setDecor(new Decor(env.chercher(a.getFils1().getChaine()).getType()));
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
	   Arbre filsTamp = null;
	   if(checker.getConv1()) {
    	   filsTamp = a.getFils1();
    	   a.setFils1(Arbre.creation1(Noeud.Conversion, filsTamp, filsTamp.getNumLigne()));
    	   a.getFils1().setDecor(new Decor(Type.Integer));
	   }
	   if(checker.getConv2()) {
    	   filsTamp = a.getFils2();
    	   a.setFils2(Arbre.creation1(Noeud.Conversion, filsTamp, filsTamp.getNumLigne()));
    	   a.getFils2().setDecor(new Decor(Type.Integer));
	   }
	   //Décoration du Noeud Conversion juste après son ajout
	   
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
		   Type type1;
		   //Si le fils n'est pas un ident mais un Index (contenant donc un ident)
		   //TODO modifier cherche_type pour le noeud index
		   if(a.getFils1().getNoeud().equals(Noeud.Index)){
			   type1 = cherche_Type(a.getFils1());
		   }
		   else{
			   // Si l'ident n'est pas une var (donc une constante) : erreur (interdit)
			   if(!a.getFils1().getDecor().getDefn().getNature().equals(NatureDefn.Var)) {
				   ErreurContext e = ErreurContext.ErreurIdentReserve;
				   e.leverErreurContext(a.getFils1().getChaine(), a.getNumLigne());
			   }
			 //Si l'ident utilisé n'existe pas dans l'environnement : erreur
			   if(env.chercher(a.getFils1().getChaine()) == null){
				   ErreurContext e = ErreurContext.ErreurIdentNonDeclaree;
				   e.leverErreurContext(a.getFils1().getChaine(), a.getNumLigne());
			   }
			   type1 = env.chercher(a.getFils1().getChaine()).getType();
			   
		   }
		   Type type2 = verif_Exp(a.getFils2());
		   ResultatAffectCompatible affectOk = ReglesTypage.affectCompatible(type1 , type2);
           if(affectOk.getOk() == false){
               ErreurContext e = ErreurContext.ErreurType;
               e.leverErreurContext("dans "+a.getNoeud().toString(), a.getNumLigne());
           }
           if(affectOk.getConv2()) {
        	   Arbre filsTamp = a.getFils2();
        	   a.setFils2(Arbre.creation1(Noeud.Conversion, filsTamp, filsTamp.getNumLigne()));
        	   a.getFils2().setDecor(new Decor(Type.Integer));
		   }
       }
   }
   
   /**
    * Cette fonction vérifie tous les noeuds expression possible 
    * 	- Identifiants.
    * 	- Arité.
    *  décors également le noeud expression.
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
	               ErreurContext e = ErreurContext.ErreurBooleenAttendu;
	               e.leverErreurContext(null, a.getNumLigne());
	   		   }
	   		   else {
	   			   add_Conversion(a,resultB);
	   		   }
	   		   a.setDecor(new Decor(Type.Boolean));
	   		   return Type.Boolean;
	   		// opération unaire donnant un boolean
	   		case Non :
	   		   if(a.getArite() != 1){
	   			   ErreurContext e = ErreurContext.ErreurArite;
	   			   e.leverErreurContext(null, a.getNumLigne());
	   		   }
	   		   resultU = ReglesTypage.unaireCompatible(a.getNoeud(), t1 = verif_Exp(a.getFils1()));
	   		   if(resultU.getOk() == false) {
	               ErreurContext e = ErreurContext.ErreurBooleenAttendu;
	               e.leverErreurContext(null, a.getNumLigne());
	   		   }
	   		   a.setDecor(new Decor(Type.Boolean));
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
	   		a.setDecor(new Decor(t1));
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
	   		   resultB = ReglesTypage.binaireCompatible(a.getNoeud(), t1 = verif_Exp(a.getFils1()), t2 = verif_Exp(a.getFils2()));
	   		   if(resultB.getOk() == false) {
	               ErreurContext e = ErreurContext.ErreurType;
	               e.leverErreurContext(null, a.getNumLigne());
	   		   }
	   		   else {
	   			   add_Conversion(a,resultB);
	   			
	   		   }
	   		   if(a.getNoeud() == Noeud.DivReel) {
	   			   a.setDecor(new Decor(Type.Integer));
	   			   return Type.Integer;
	   		   }
	   		   if(t1 == Type.Integer || t2 == Type.Integer) {
	   			a.setDecor(new Decor(Type.Integer));
	   			   return Type.Integer;
	   		   }
	   		   else {
	   			   a.setDecor(new Decor(Type.Real));
	   			   return Type.Real;
	   		   }
	   		   
	   		// Si c'est un facteur
	   		case Entier :
	   			a.setDecor(new Decor(Type.Integer));
	   			return Type.Integer;
	   		case Chaine :
	   			a.setDecor(new Decor(Type.String));
	   			return Type.String;
	   		case Reel :
	   			a.setDecor(new Decor(Type.Real));
	   			return Type.Real;
	   			
	   		// Si c'est un ident / index
	   		case Ident :
	   			
	   			if(env.chercher(a.getChaine()) == null){
	  			   ErreurContext e = ErreurContext.ErreurIdentNonDeclaree;
	  			   e.leverErreurContext(a.getChaine(), a.getNumLigne());
	    		}
	    		else {
	    			return a.getDecor().getType();
	    		}
	   		case Index :
	   			Type tui;
	   			a.setDecor(new Decor(tui = verif_Index(a)));
	   			return tui;
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
			   if(!(verif_Exp(a.getFils2()).equals(Type.Integer)));{
				   e = ErreurContext.ErreurEntierAttendu;
				   e.leverErreurContext(null, a.getNumLigne());
			   }
			   return cherche_Type(a);
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
		ErreurContext e;
		if(a.getArite() != Noeud.Lecture.arite){
		   e = ErreurContext.ErreurArite;
		   e.leverErreurContext(null, a.getNumLigne());
		}
	
		if(!a.getFils1().getDecor().getDefn().getNature().equals(NatureDefn.Var)){
			e = ErreurContext.ErreurIdentReserve;
			e.leverErreurContext(a.getFils1().getChaine(), a.getNumLigne());
		}
		
		Type t1 = verif_Index(a.getFils1());
		
		// Si tu veux pas l'intervalle enlève ce commentaire et met l'autre en commentaire
		//if(t1 != Type.Integer && t1 != Type.Real) { 
		if(t1.getNature() != NatureType.Interval && t1.getNature() != NatureType.Real) {
			e = ErreurContext.ErreurType;
            e.leverErreurContext(a.getNoeud().toString(), a.getNumLigne());
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
		if(a.getArite() != Noeud.Ecriture.arite){
			   e = ErreurContext.ErreurArite;
			   e.leverErreurContext(null, a.getNumLigne());
	   }
		a = a.getFils1();
		while(a.getFils1().getNoeud() != Noeud.Vide) {
			// les arguments de write ne peuvent pas être des boolean
			if(verif_Exp(a.getFils2()) == Type.Boolean) {
				e = ErreurContext.ErreurType;
				e.leverErreurContext(a.getNoeud().toString(), a.getNumLigne());
			}
			a = a.getFils1();
			
			if(a.getArite() != 2){
	   			   e = ErreurContext.ErreurArite;
	   			   e.leverErreurContext(null, a.getNumLigne());
		   }
		}
		if(verif_Exp(a.getFils2()) == Type.Boolean) {
			e = ErreurContext.ErreurType;
			e.leverErreurContext(a.getFils2().getDecor().getType().toString(), a.getNumLigne());
			
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
				e.leverErreurContext(a.getNoeud().toString(), a.getNumLigne());
			}
			Arbre atamp = a;
			a=a.getFils1();
			if(verif_Exp(a.getFils2()) != Type.Integer || verif_Exp(a.getFils3()) != Type.Integer) {
				e = ErreurContext.ErreurType;
				e.leverErreurContext(a.getNoeud().toString(), a.getNumLigne());
			}
			a=atamp;
		}
		else {
			throw new ErreurInterneVerif("Arbre incorrect dans Pour : increment ou decrement attendu : "+ a.getFils1().getNoeud().toString());
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
		if(verif_Exp(a.getFils1()) != Type.Boolean) {
			e = ErreurContext.ErreurBooleenAttendu;
			e.leverErreurContext(a.getNoeud().toString(), a.getNumLigne());
		}
		if(a.getArite() != Noeud.Si.arite){
			   e = ErreurContext.ErreurArite;
			   e.leverErreurContext(null, a.getNumLigne());
		}
		if(a.getFils3().getNoeud() != Noeud.Vide){
			verif_ListeInst(a.getFils3());
		}
		verif_ListeInst(a.getFils2());
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
		verif_Exp(a.getFils1());
		if(a.getArite() != Noeud.TantQue.arite){
			   e = ErreurContext.ErreurArite;
			   e.leverErreurContext(null, a.getNumLigne());
		}
		if(a.getFils1().getDecor().getType() != Type.Boolean) {
			e = ErreurContext.ErreurBooleenAttendu;
			e.leverErreurContext(a.getNoeud().toString(), a.getNumLigne());
		}
		verif_ListeInst(a.getFils2());
	}
}
