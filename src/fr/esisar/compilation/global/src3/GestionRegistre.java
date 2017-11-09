package fr.esisar.compilation.global.src3;

public class GestionRegistre {
	
	private int[] regTab = new int[18];
	
	/**Méthode initialisant tous les registres à l'état LIBRE.
	 * Méthode à appeler en début de programme.
	 */
	public GestionRegistre(){
		this.initRegTab();
	}
	
	public void initRegTab(){
		for(int i = 0; i<regTab.length; i++){
			regTab[i] = 0;
		}
	}
	
	/**Méthode permettant de tester l'état d'occupation d'un registre.
	 * @param reg Registre à tester
	 * @return Etat du registre, TRUE s'il est libre, FALSE s'il est occupé.
	 */
	public boolean estRegistreLibre(Registre reg){
		if(regTab[getRegIndex(reg)] == 0){
			return true;
		}
		return false;
	}
	
	public void getRegTab(){
		for(int i = 0; i<regTab.length; i++){
			regTab[i] = 0;
		}
	}
	
	/**
	 * Méthode permettant de marquer un registre comme libéré.
	 * @param reg Registre libéré.
	 */
	public void libererRegistre(Registre reg){
		regTab[getRegIndex(reg)] = 0;
	}
	
	/**
	 * Méthode permettant de marquer un registre comme occupé.
	 * @param reg Registre occupé.
	 */
	public void occuperRegistre(Registre reg){
		regTab[getRegIndex(reg)] = 1;
	}
	
	private int getRegIndex(Registre reg){
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

}
