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
   
   ErreurNonRepertoriee,
   ErreurType,
   ErreurDefn,
   ErreurAriteAffect,
   ErreurIdentNonDeclaree;

	   void leverErreurContext(String s, int numLigne) throws ErreurVerif {
	      System.err.println("Erreur contextuelle : ");
	      switch (this) {
	      case ErreurType : 
	          System.err.print("Erreur de type");
	          break;
	      case ErreurDefn:
	          System.err.print("Erreur de définition");
	          break;          
	      case ErreurAriteAffect:
	    	  System.err.print("Erreur d'arité");
	          break; 
	      case ErreurIdentNonDeclaree:
	    	  System.err.print("Ident non déclarer : "+s);
	    	  break;
	      default:
	            System.err.print("non repertoriee");
	      }
	      System.err.println(" ... ligne " + numLigne);
	      throw new ErreurVerif();
	   }

}


