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
      verif_LISTE_INST(a.getFils2());
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
		   verif_ListeIdent(a.getFils1());
		   verif_Type(a.getFils2());
		   break;
	   default:
		   throw new ErreurInterneVerif("Arbre incorrect dans verif_DECL");
    }
   }
   
   
   private void verif_ListeIdent(Arbre a) throws ErreurVerif{
	   switch(a.getNoeud()){
	   case Vide:
		   break;
	   case ListeIdent:
		   verif_ListeIdent(a.getFils1());
		   verif_Ident(a.getFils2());
		   break;
	   
	   default:
		   throw new ErreurInterneVerif("Arbre incorrect dans verif_ListeIdent");
	   }
   }
   
	private void verif_Ident(Arbre a) throws ErreurVerif{
		
	}
   
   private void verif_Type(Arbre a) {
	   switch(a.getNoeud()){
	   case Entier:
	   case Reel:
	   case Chaine:
	   case Tableau:
		   break;
	   default:
		   throw new ErreurInterneVerif("Arbre incorrect dans verif_Type");
			   
	   }
	
   }

/**************************************************************************
    * LISTE_INST
    **************************************************************************/
   private void verif_LISTE_INST(Arbre a) throws ErreurVerif {
	   switch(a.getNoeud()){
     	case Vide:
     		break;
     	case ListeInst:
     		verif_LISTE_INST(a.getFils1());
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
	   case DivReel:
	   case Reste:
		   verif_DivReel(a);
		   break;
	   case Ecriture:
		   verif_Ecriture(a);
		   break;
	   case Egal:
	   case Inf:
	   case InfEgal:
	   case NonEgal:
	   case Sup:
	   case SupEgal:
		   verif_Egal(a);
		   break;
	   case Et:
	   case Ou:
		   verif_Et(a);
		   break;
	   case Lecture:
		   verif_Lecture(a);
		   break;
	   case Moins:
	   case Plus:
		   verif_Moins(a);
		   break;
	   case MoinsUnaire:
	   case PlusUnaire:
		   verif_MoinsUnaire(a);
		   break;
	   case Mult:
		   verif_Mult(a);
		   break;
	   case Non:
		   verif_Non(a);
		   break;
	   case Pour:
		   verif_Pour(a);
		   break;
	   case Quotient:
		   verif_Quotient(a);
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

	private void verif_Et(Arbre a) {
	// TODO Auto-generated method stub
	
}

	private void verif_TantQue(Arbre a) throws ErreurVerif{
		// TODO Auto-generated method stub
		
	}
	
	private void verif_Tableau(Arbre a) throws ErreurVerif{
		// TODO Auto-generated method stub
		
	}
	
	private void verif_Si(Arbre a) throws ErreurVerif{
		// TODO Auto-generated method stub
		
	}
	
	private void verif_Quotient(Arbre a) throws ErreurVerif{
		// TODO Auto-generated method stub
		
	}
	
	private void verif_Pour(Arbre a) throws ErreurVerif{
		// TODO Auto-generated method stub
		
	}
	
	private void verif_Ou(Arbre a) throws ErreurVerif{
		// TODO Auto-generated method stub
		
	}

	private void verif_Non(Arbre a) throws ErreurVerif{
		// TODO Auto-generated method stub
		
	}
	
	private void verif_Mult(Arbre a) throws ErreurVerif{
		// TODO Auto-generated method stub
		
	}
	
	private void verif_MoinsUnaire(Arbre a) throws ErreurVerif{
		// TODO Auto-generated method stub
		
	}
	
	private void verif_Moins(Arbre a) throws ErreurVerif{
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
	
	private void verif_DivReel(Arbre a) throws ErreurVerif{
		// TODO Auto-generated method stub
		
	}
	
	private void verif_Decrement(Arbre a) throws ErreurVerif{
		// TODO Auto-generated method stub
		
	}
	
	private void verif_Conversion(Arbre a) throws ErreurVerif{
		// TODO Auto-generated method stub
		
	}
	private void verif_Affect(Arbre a) throws ErreurVerif{
		// TODO Auto-generated method stub
		
	}
}
