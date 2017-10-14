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
		//TODO recuperer le nom de la variable pour enrichir l'environnement
		//env.enrichir(a.getNoeud()., def);
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
	   case Nop:
		   break;
	   case Affect:
		   verif_Affect(a);
		   break;
	   case Conversion:
		   verif_Conversion(a);
		   break;		   
	   case Decrement:
	   case Increment:
		   verif_Decrement(a);
		   break;
	   case Ecriture:
		   verif_Ecriture(a);
		   break;
		   
		   /* opérateur binaire*/
	   case DivReel:
	   case Reste:
	   case Egal:
	   case Inf:
	   case InfEgal:
	   case NonEgal:
	   case Sup:
	   case SupEgal:
	   case Moins:
	   case Plus:
	   case Et:
	   case Ou:
	   case Mult:
	   case Quotient:
		   verif_Et(a);
		   break;
	   case Lecture:
		   verif_Lecture(a);
		   break;
		   /*opérateur unaire*/
	   case MoinsUnaire:
	   case PlusUnaire:
	   case Non:
		   verif_MoinsUnaire(a);
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
   private void verif_Affect(Arbre a) throws ErreurVerif{
       if(a.getArite() != Noeud.Affect.arite){
           ErreurContext e = ErreurContext.ErreurAriteAffect;
           e.leverErreurContext(null, a.getNumLigne());
       }
       else{
           ResultatAffectCompatible affectOk = ReglesTypage.affectCompatible(a.getFils1().getDecor().getType(), a.getFils2().getDecor().getType());
           if(affectOk.getOk() == false){
               ErreurContext e = ErreurContext.ErreurType;
               e.leverErreurContext(null, a.getNumLigne());
           }
           // TODO
           // Faut-il insérer les noeud.conversion dans ces fonctions ? exemple ci-dessous
           if(affectOk.getConv2()) {
        	   Arbre filsTamp = a.getFils2();
        	   a.setFils1(Arbre.creation1(Noeud.Conversion, filsTamp, filsTamp.getNumLigne()));
           }
       }

   }
	private void verif_Et(Arbre a) throws ErreurVerif {
		if(a.getArite() != Noeud.Et.arite){
			ErreurContext e = ErreurContext.ErreurAriteAffect;
			e.leverErreurContext(null, a.getNumLigne());
		}
		else{
			ResultatBinaireCompatible affectOk = ReglesTypage.binaireCompatible(a.getNoeud(),a.getFils1().getDecor().getType(), a.getFils2().getDecor().getType());
			if(affectOk.getOk() == false){
               ErreurContext e = ErreurContext.ErreurType;
               e.leverErreurContext(null, a.getNumLigne());
	        }
		}
	}

	private void verif_TantQue(Arbre a) throws ErreurVerif{
		if(a.getArite() != Noeud.TantQue.arite){
			ErreurContext e = ErreurContext.ErreurAriteAffect;
			e.leverErreurContext(null, a.getNumLigne());
		}
		else{
			ResultatBinaireCompatible affectOk = ReglesTypage.binaireCompatible(a.getNoeud(),a.getFils1().getDecor().getType(), a.getFils2().getDecor().getType());
			if(affectOk.getOk() == false){
               ErreurContext e = ErreurContext.ErreurType;
               e.leverErreurContext(null, a.getNumLigne());
	        }
		}
	}
	
	private void verif_Tableau(Arbre a) throws ErreurVerif{
		if(a.getArite() != Noeud.Tableau.arite){
			ErreurContext e = ErreurContext.ErreurAriteAffect;
			e.leverErreurContext(null, a.getNumLigne());
		}
		else{
			ResultatBinaireCompatible affectOk = ReglesTypage.binaireCompatible(a.getNoeud(),a.getFils1().getDecor().getType(), a.getFils2().getDecor().getType());
			if(affectOk.getOk() == false){
               ErreurContext e = ErreurContext.ErreurType;
               e.leverErreurContext(null, a.getNumLigne());
	        }
		}
	}
	
	// TODO Faut-il créer une classe ternaireCompatible pour les noeud SI et INCREMENT ? (arite 3)
	private void verif_Si(Arbre a) throws ErreurVerif{
		if(a.getArite() != Noeud.Si.arite){
			ErreurContext e = ErreurContext.ErreurAriteAffect;
			e.leverErreurContext(null, a.getNumLigne());
		}
		else{
			if(a.getFils1().getDecor().getType() != Type.Boolean) {
	               ErreurContext e = ErreurContext.ErreurType;
	               e.leverErreurContext(null, a.getNumLigne());
	        }
		}
	}
	
	private void verif_Pour(Arbre a) throws ErreurVerif{
		if(a.getArite() != Noeud.Pour.arite){
			ErreurContext e = ErreurContext.ErreurAriteAffect;
			e.leverErreurContext(null, a.getNumLigne());
		}
	}
	
	private void verif_MoinsUnaire(Arbre a) throws ErreurVerif{
		// TODO Auto-generated method stub
	}
	
	
	private void verif_ListeExp(Arbre a) throws ErreurVerif{
		// TODO Auto-generated method stub
		
	}
	
	private void verif_Lecture(Arbre a) throws ErreurVerif{
		// TODO Auto-generated method stub
		
	}
	
	private void verif_Intervalle(Arbre a) throws ErreurVerif{
		// TODO Auto-generated method stub
		
	}
	
	private void verif_Egal(Arbre a) throws ErreurVerif{
		// TODO Auto-generated method stub
		
	}
	
	private void verif_Ecriture(Arbre a) throws ErreurVerif{
		// TODO Auto-generated method stub
		
	}
	
	private void verif_Decrement(Arbre a) throws ErreurVerif{
		// TODO Auto-generated method stub
		
	}
	
	private void verif_Conversion(Arbre a) throws ErreurVerif{
		// TODO Auto-generated method stub
		
	}
}
