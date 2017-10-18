/**
 * Type énuméré pour les erreurs contextuelles.
 * Ce type énuméré définit toutes les erreurs contextuelles possibles et 
 * permet l'affichage des messages d'erreurs pour la passe 2.
 */

// -------------------------------------------------------------------------
// A COMPLETER, avec les différents types d'erreur et les messages d'erreurs 
// correspondants
// -------------------------------------------------------------------------

package fr.esisar.compilation.verif;

public enum ErreurContext {
   
   ErreurType,
   ErreurArite,
   ErreurBooleenAttendu,
   ErreurChaineAttendue,
   ErreurEntierAttendu,
   ErreurTableauAttendu,
   ErreurIdentReserve,
   ErreurIdentInexistant,
   ErreurIdentNonDeclaree;
	
	

	   void leverErreurContext(String s, int numLigne) throws ErreurVerif {
	      System.err.println("Erreur contextuelle : ");
	      switch (this) {
	      case ErreurType : 
	          System.err.print("Erreur de type");
	          break;
	      case ErreurArite:
	    	  System.err.print("Erreur d'arité"+s);
	          break; 
	      case ErreurIdentNonDeclaree:
	    	  System.err.print("Ident non déclaré : "+s);
	    	  break;
	      case ErreurBooleenAttendu:
	    	  System.err.print("Boolean attendu");
	    	  break;
	      case ErreurChaineAttendue:
	    	  System.err.print("Chaine attendue");
	    	  break;
	      case ErreurEntierAttendu:
	    	  System.err.print("Entier attendue");
	    	  break;
	      case ErreurTableauAttendu:
	    	  System.err.print("Tableau attendue");
	    	  break;
	      case ErreurIdentReserve:
	    	  System.err.print("Identifiant reserve");
	    	  break;
	      default:
	            System.err.print("non repertoriee");
	      }
	      System.err.println(" ... ligne " + numLigne);
	      throw new ErreurVerif();
	   }

}


