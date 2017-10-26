package fr.esisar.compilation.gencode;

import fr.esisar.compilation.global.src.*;
import fr.esisar.compilation.global.src3.*;

/**
 * Génération de code pour un programme JCas à partir d'un arbre décoré.
 */

class Generation {
   
   /**
    * Méthode principale de génération de code.
    * Génère du code pour l'arbre décoré a.
    */
   static Prog coder(Arbre a) {
      Prog.ajouterGrosComment("Programme généré par JCasc");

      // -----------
      // A COMPLETER
      // -----------
      
      coder_Decl(a.getFils1());
      coder_Inst(a.getFils2());
      // L'instruction "new_line"
      // L'instruction "write"
      

      // Fin du programme
      // L'instruction "HALT"
      Inst inst = Inst.creation0(Operation.HALT);
      // On ajoute l'instruction à la fin du programme
      Prog.ajouter(inst);

      // On retourne le programme assembleur généré
      return Prog.instance(); 
   }
   private static void coder_Decl(Arbre a){
   
   }
   
   private static void coder_Inst(Arbre a){
	   switch(a.getNoeud()){
	   case Ecriture:
		 //Attention, le fils d'un Noeud Ecriture est un Noeud.ListeExp qui contient lui même des Noeud.ListeExp
		   coder_Ecriture(a.getFils1());
		   break;
		   
	   case Ligne:
		   Inst inst = Inst.creation0(Operation.WNL);
		   Prog.ajouter(inst, "new line");
		   break;
	   }   
   }
   
   private static void coder_Ecriture(Arbre a){

	   //Si l'arbre est vide, on arrete la fonction
	   if(a.getNoeud().equals(Noeud.Vide)){
		   return;
	   }
	   //Sinon on descend dans l'arbre
	   Arbre f1 = a.getFils1();
	   Arbre f2 = a.getFils2();
	   
	   //Si le fils 1 est un Liste exp, on fais un appel recursif pour descendre
	   if(f1.equals(Noeud.ListeExp)){
		   coder_Ecriture(f1.getFils1());
		   coder_Ecriture(f1.getFils2());
	   }
	   //Sinon, celà veut dire qu'on est le plus profond possible, et qu'on ne se servira pas du fils 1 (car il est Vide)
	   
	   //Si on veut écrire un string
	   if(f2.getDecor().getType().equals(Type.String)){
		   Inst inst = Inst.creation1(Operation.WSTR,Operande.creationOpChaine(f2.getChaine()));
		   Prog.ajouter(inst,"Ecriture du string : "+f2.getChaine());
	   }
	   //Si on veut écrire un integer
	   if(f2.getDecor().getType().equals(Type.Integer)){
		   Inst inst = Inst.creation1(Operation.WINT,coder_EXP(f2));
		   Prog.ajouter(inst,"Ecriture de l'integer : " );
	   }
	   //Si on veut écrire un real
	   if(f2.getDecor().getType().equals(Type.Real)){
		   Inst inst = Inst.creation1(Operation.WFLOAT,coder_EXP(f2));
		   Prog.ajouter(inst,"Ecriture du reel : " );
	   }
	   
   }
   
   private static Operande coder_EXP(Arbre a){
	   //chercher en fonction du nom de la variable, sa position en pile,PUIS la charger dans un registre libre
	   //Il faut donc utiliser la fonction getGrosRegistreLibre()
	   //Il faut ensuite modifier le tableau regTab pour indiquer les registres qui ne sont plus disponible
	   //
	   
	   return Operande.R0;
   }
   
}



