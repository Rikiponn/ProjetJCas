package fr.esisar.compilation.verif;

import fr.esisar.compilation.global.src.*;

/**
 * La classe ReglesTypage permet de définir les différentes règles 
 * de typage du langage JCas.
 */

public class ReglesTypage {

   /**
    * Teste si le type t1 et le type t2 sont compatibles pour l'affectation, 
    * c'est à dire si on peut affecter un objet de t2 à un objet de type t1.
    */

   static ResultatAffectCompatible affectCompatible(Type t1, Type t2) {
	   ResultatAffectCompatible result = new ResultatAffectCompatible();
	   result.setOk(false);
	   result.setConv2(false);
	   if(t1.getNature().equals(t2.getNature()) && (t1.getNature().equals(NatureType.Real) || t1.getNature().equals(NatureType.Boolean) || t1.getNature().equals(NatureType.Array))){
		   result.setOk(true);
	   }
	   if(t1.getNature().equals(NatureType.Real) && t2.getNature().equals(NatureType.Interval)){
		   result.setOk(true);
		   result.setConv2(true);
	   }
	   if(t1.getNature().equals(NatureType.Array) && t2.getNature().equals(NatureType.Array)){
		   if(t1.getIndice().getNature().equals(NatureType.Interval) && t2.getIndice().getNature().equals(NatureType.Interval)){
			   if(t1.getBorneInf() == (t2.getBorneInf()) && (t1.getBorneSup() == t2.getBorneSup())){
				   result = affectCompatible(t1.getElement(),t2.getElement());
			   }
		   }
	   }
	   return (result);
   }

   /**
    * Teste si le type t1 et le type t2 sont compatible pour l'opération 
    * binaire représentée dans noeud.
    */

   static ResultatBinaireCompatible binaireCompatible
      (Noeud noeud, Type t1, Type t2) {
      return null;
   }

   /**
    * Teste si le type t est compatible pour l'opÃ©ration unaire représentée 
    * dans noeud.
    */
   static ResultatUnaireCompatible unaireCompatible
         (Noeud noeud, Type t) {
      return null;
   }
         
}

