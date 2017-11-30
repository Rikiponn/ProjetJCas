package fr.esisar.compilation.gencode;

import fr.esisar.compilation.global.src3.Operande;

class Indice {
	Operande offset;
	int placeEnPileOrigine;
	
	public Indice(Operande off, int pla) {
		this.offset = off;
		this.placeEnPileOrigine = pla;
	}
	
	public void setOffset(Operande off) {
		this.offset = off;
	}
	
	public void setPlaceEnPileOrigine(int pla) {
		this.placeEnPileOrigine = pla;
	}
	
	public Operande getOffset() {
		return this.offset;
	}
	
	public int getPlaceEnPileOrigine() {
		return this.placeEnPileOrigine;
	}
}
