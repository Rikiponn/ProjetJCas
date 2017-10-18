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
	   ResultatAffectCompatible result2 = new ResultatAffectCompatible();
	   result.setOk(false);
	   result.setConv2(false);
	   if(t1.equals(t2) && ((t1.equals(Type.String)|| t1.equals(Type.Integer) || t1.equals(Type.Real) || t1.equals(Type.Boolean) || t1.getNature().equals(NatureType.Array)))){
		   result.setOk(true);
	   }
	   if(t1.equals(Type.Real) && t2.equals(Type.Integer)){
		   result.setOk(true);
		   result.setConv2(true);
	   }
	   if(t1.getNature().equals(NatureType.Array) && t2.getNature().equals(NatureType.Array)){
		   if(t1.getIndice().getNature().equals(NatureType.Interval) && t2.getIndice().getNature().equals(NatureType.Interval)){
			   if(t1.getIndice().getBorneInf() == (t2.getIndice().getBorneInf()) && (t1.getIndice().getBorneSup() == t2.getIndice().getBorneSup())){
				   result2 = affectCompatible(t1.getElement(),t2.getElement());
				   result.setOk(result2.getOk());
				   result.setConv2(result.getConv2() && result2.getConv2());
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
	   ResultatBinaireCompatible result = new ResultatBinaireCompatible();
	   result.setOk(false);
	   result.setConv1(false);
	   result.setConv2(false);
	   
	   if (noeud.arite != 2){
		   return result;
	   }
	   if(t1.equals(Type.Integer) && t2.equals(Type.Real)){
		   result.setConv1(true);
	   }
	   if(t1.equals(Type.Real) && t2.equals(Type.Integer)){
		   result.setConv2(true);
	   }
	   // and, or
	   if((noeud.toString().equals("Noeud.Ou") || noeud.toString().equals("Noeud.Et")) && t1.equals(Type.Boolean) && t2.equals(Type.Boolean)){
		   result.setOk(true);
		   result.setTypeRes(Type.Boolean);
	   }
	   // =, <, >, /=, <=, >=
	   if(noeud.toString().equals("Noeud.Sup") || noeud.toString().equals("Noeud.SupEgal") || noeud.toString().equals("Noeud.Inf") || noeud.toString().equals("Noeud.InfEgal") || noeud.toString().equals("Noeud.NonEgal") || noeud.toString().equals("Noeud.Egal")){
		   result.setTypeRes(Type.Boolean);
		   if(t1.getNature().equals(NatureType.Interval) || t1.getNature().equals(NatureType.Real)){
			   if(t2.getNature().equals(NatureType.Interval) || t2.getNature().equals(NatureType.Real)){
					result.setOk(true);
			   }
		   }
	   }
	   // +,-,*
	   if(noeud.toString().equals("Noeud.Plus") || noeud.toString().equals("Noeud.Moins") || noeud.toString().equals("Noeud.Mult")){
		   if(t1.equals(t2)){
			   if(t1.equals(Type.Real) || t1.getNature().equals(NatureType.Interval)){
				   result.setOk(true);
				   result.setTypeRes(t1);
		   		}   
		   }else{
			   if(t1.equals(Type.Real) && t2.getNature().equals(NatureType.Interval) || (t2.equals(Type.Real) && t1.getNature().equals(NatureType.Interval))){
				   result.setOk(true);
				   result.setTypeRes(Type.Real);
			   }
		   }
	   }
	   //div, mod
	   if(noeud.toString().equals("Noeud.Reste") || noeud.toString().equals("Noeud.Quotient")){
		   if(t1.getNature().equals(NatureType.Interval) && t2.getNature().equals(NatureType.Interval)){
			   result.setOk(true);
			   result.setTypeRes(Type.Integer);
		   }
	   }
	   // /
	   if(noeud.toString().equals("Noeud.DivReel")){
		   result.setTypeRes(Type.Real);
		   if(t1.getNature().equals(NatureType.Interval) || t1.equals(Type.Real)){
			   if(t2.getNature().equals(NatureType.Interval) || t2.equals(Type.Real)){
					result.setOk(true);
			   }
		   }
	   }
	   // [Array] : Array(Type.Interval, <type>), Type.Interval -> <type>
	   if(noeud.toString().equals("Noeud.Tableau")){
		   if(t1.getNature().equals(NatureType.Array) && t1.getIndice().equals(NatureType.Interval) && t2.getNature().equals(NatureType.Interval)){
			   result.setTypeRes(t1.getElement());
			   result.setOk(true);
		   }
	   }
	   /* pas certain que ce soit nécessaire, pas marqué dans Context.txt
	   // [Index]
	   if(noeud.toString().equals("Index")){
		   if(t1.getNature().equals(NatureType.Interval)){
			   result.setTypeRes(Type.);
		   }
	   }
	   */
      return result;
   }

   /**
    * Teste si le type t est compatible pour l'opération unaire représentée 
    * dans noeud.
    */
   static ResultatUnaireCompatible unaireCompatible
         (Noeud noeud, Type t) {
	   ResultatUnaireCompatible result = new ResultatUnaireCompatible();
	   result.setOk(false);
	   if (noeud.arite != 1){
		   return result;
	   }
	   // not
	   if (noeud.toString().equals("Noeud.Non")){
		   if(t.equals(Type.Boolean)){
			   result.setOk(true);
		   }
		   result.setTypeRes(Type.Boolean);
		   return result;
	   }
	   // +, -
	   if (noeud.toString().equals("Noeud.PlusUnaire") || noeud.toString().equals("Noeud.MoinsUnaire")){
		   if(t.equals(Type.Real) || t.equals(Type.Integer)){
			   result.setOk(true);
		   }
		   result.setTypeRes(t);
		   return result;
	   }
      return null;
   }
         
}

