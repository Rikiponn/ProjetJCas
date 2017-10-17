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
      verif_LISTE_DECL(a.getFils1());
      verif_LISTE_INST(a,a.getFils2());
   }

   /**************************************************************************
    * LISTE_DECL
    **************************************************************************/
   private void verif_LISTE_DECL(Arbre a) throws ErreurVerif {
	   switch(a.getNoeud()){
    	case Vide:
    		break;
    	case ListeDecl:
    		verif_LISTE_DECL(a.getFils1());
    		verif_DECL(a.getFils2());
    		break;    		
    	default:
    		throw new ErreurInterneVerif("Arbre incorrect dans verif_LISTE_DECL");
    }
   }

   
   private void verif_DECL(Arbre a) throws ErreurVerif {
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
   private Type cherche_Type(Arbre a){
	   switch(a.getNoeud()){
		   case Entier:
			   return(Type.Integer);
		   case Reel:
			   return(Type.Real);
		   case Chaine:
			   return(Type.String);
		   case Tableau:
			   return(Type.creationArray(Type.Integer, cherche_Type(a.getFils2())));
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
   private void verif_LISTE_INST(Arbre pere,Arbre a) throws ErreurVerif {
	   switch(a.getNoeud()){
     	case Vide:
     		break;
     	case ListeInst:
     		verif_LISTE_INST(a,a.getFils1());
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
   private void decor_verif(Arbre a) throws ErreurVerif{
	   
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
			   decor_verif(a.getFils1());
			   break;
		   }
		   else if(a.getFils1().getNoeud()==Noeud.Reel){
			   a.setDecor(new Decor(new Defn(NatureDefn.Type, Type.Integer), Type.Integer));
			   decor_verif(a.getFils1());
			   break;
		   }
		   
	   default:
		   throw new ErreurInterneVerif("Arbre incorrect dans verif_Type");
			   
	   }
   }
   
<<<<<<< HEAD
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
   
   private void verif_Affect(Arbre a) throws ErreurVerif{
=======
   
   private void verif_Affect(Arbre a) throws ErreurVerif{ 
>>>>>>> 08a524456dbd59fe656ccfe8fe890c512ceddd11
	   if(a.getArite() != Noeud.Affect.arite){
		   ErreurContext e = ErreurContext.ErreurAriteAffect;
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
	   Type t1, t2, t3;
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
	   			   ErreurContext e = ErreurContext.ErreurAriteAffect;
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
	   			   ErreurContext e = ErreurContext.ErreurAriteAffect;
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
	   			   ErreurContext e = ErreurContext.ErreurAriteAffect;
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
		   			   ErreurContext e = ErreurContext.ErreurAriteAffect;
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
	   		case Index :
	   			return verif_Index(a);
	   		
	   }
   }
   
   private Type verif_Index(Arbre a) throws ErreurVerif{
	   
		if(a.getNoeud() == Noeud.Ident) {
			if(env.chercher(a.toString()) == null){
 			   ErreurContext e = ErreurContext.ErreurIdentNonDeclaree;
 			   e.leverErreurContext(a.getFils1().toString(), a.getNumLigne());
   			}
   			else {
   				return a.getDecor().getType();
   			}
		}
		else {
			
		}
   }
}
