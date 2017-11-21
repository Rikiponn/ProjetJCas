package fr.esisar.compilation.global.src3;

public class GestionRegistre {
	
	private static int[] regTab = new int[18];
	
	/**Méthode initialisant tous les registres à l'état LIBRE.
	 * Méthode à appeler en début de programme.
	 */
	
	public static void initRegTab(){
		for(int i = 0; i<regTab.length; i++){
			regTab[i] = 0;
		}
	}
	
	/**Méthode permettant de tester l'état d'occupation d'un registre.
	 * @param reg Registre à tester
	 * @return Etat du registre, TRUE s'il est libre, FALSE s'il est occupé.
	 */
	public static boolean estRegistreLibre(Registre reg){
		if(regTab[getRegIndex(reg)] == 0){
			return true;
		}
		return false;
	}
	public static boolean estRegistreLibre(int i){
		if(regTab[i] == 0){
			return true;
		}
		return false;
	}
	
	/**
	 * Retourne le premier Registre libre
	 * Réserve ce registre
	 * @return index Operande lié au registre libre
	 */
	public static Operande getFreeRegToOpTab(){
		for(int i = 0; i<regTab.length; i++){
			if(regTab[i] == 0){
				Operande index=GestionRegistre.getRegOp(i);		
				GestionRegistre.occuperRegistre(i);
				return index;
			}
		}
		return null;
	}   
	public static Registre getFreeRegTab(){
		return getFreeRegToOpTab().getRegistre();
	}
	
	 /**
	    * Déplace le contenu du registre en param, vers un autre registre libre ou en pile si aucun autre registre n'est dispo si celui-ci est occupé
	    * @param reg
	    * @return indique si la fonction a mis le précédent registre en pile (aka si la valeur de retour est égale au param)
	    */
	public static Registre deplaceRegistre(Registre reg){
	   	if(!estRegistreLibre(reg)){
	   		Registre r1 = getFreeRegTab();
	   		libererRegistre(reg);
	   		return r1;
	   	}
	   	pushPile(reg);
	   	return reg;
   }
	
	
	/**
	 * Méthode permettant de marquer un registre comme libéré.
	 * @param reg Registre a libéré.
	 */
	public static void libererRegistre(Registre reg){
		regTab[getRegIndex(reg)] = 0;
	}
	/**
	 * Méthode permettant de marquer un registre comme libéré.
	 * @param i numéro du registre a libéré.
	 */
	public static void libererRegistre(int i){
		regTab[i] = 0;
	}
	
	/**
	 * Méthode permettant de marquer un registre comme occupé.
	 * @param reg Registre occupé.
	 */
	public static void occuperRegistre(Registre reg){
		regTab[getRegIndex(reg)] = 1;
	}
	public static void occuperRegistre(int i){
		regTab[i] = 1;
	}
	
	public static void pushPile (Registre reg){
		Inst inst = Inst.creation1(Operation.PUSH,Operande.opDirect(reg));
	   	Prog.ajouter(inst,"Placement en pile de "+reg.toString());
	}
	public static void popPile (Registre reg){
		Inst inst = Inst.creation1(Operation.POP, Operande.opDirect(reg));
		Prog.ajouter(inst,"Registre"+reg.toString()+" retablis depuis la pile");
	}
	
	private static int getRegIndex(Registre reg){
		int index = 0;
		switch(reg){
		case R0: 
			index = 0;
			break;		
		case R1: 
			index = 1;
			break;	
		case R2: 
			index = 2;
			break;	
		case R3: 
			index = 3;
			break;	
		case R4: 
			index = 4;
			break;	
		case R5:
			index = 5;
			break;	
		case R6:
			index = 6;
			break;	
		case R7:
			index = 7;
			break;	
		case R8:
			index = 8;
			break;	
		case R9:
			index = 9;
			break;	
		case R10:
			index = 10;
			break;	
		case R11:
			index = 11;
			break;	
		case R12: 
			index = 12;
			break;	
		case R13: 
			index = 13;
			break;	
		case R14: 
			index = 14;
			break;	
		case R15:
			index = 15;
			break;	
		case GB: 
			index = 16;
			break;	
		case LB:
			index = 17;
			break;
		}
		return index;		
	}
	private static Operande getRegOp(int i){
		Operande index=null;
		switch(i){
		case 0: 
			index = Operande.R0;
			break;		
		case 1: 
			index = Operande.R1;
			break;	
		case 2: 
			index = Operande.R2;
			break;	
		case 3: 
			index = Operande.R3;
			break;	
		case 4: 
			index = Operande.R4;
			break;	
		case 5:
			index = Operande.R5;
			break;	
		case 6:
			index = Operande.R6;
			break;	
		case 7:
			index = Operande.R7;
			break;	
		case 8:
			index = Operande.R8;
			break;	
		case 9:
			index = Operande.R9;
			break;	
		case 10:
			index = Operande.R10;
			break;	
		case 11:
			index = Operande.R11;
			break;	
		case 12: 
			index = Operande.R12;
			break;	
		case 13: 
			index = Operande.R13;
			break;	
		case 14: 
			index = Operande.R14;
			break;	
		case 15:
			index = Operande.R15;
			break;	
		case 16: 
			index = Operande.GB;
			break;	
		case 17:
			index = Operande.LB;
			break;
		}
		return index;
	}
}
