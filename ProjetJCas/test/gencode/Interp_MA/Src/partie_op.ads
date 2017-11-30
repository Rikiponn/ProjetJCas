------------------------------------------------------------------------
-- partie_op.ads : specification de la "partie operative"             --
--                                                                    --
-- Auteur : X. Nicollin                                               --
--                                                                    --
-- Date de creation : 01/94                                           --
-- Date de derniere modification : 21/11/94                           --
------------------------------------------------------------------------

with PSEUDO_CODE;
use PSEUDO_CODE;

package PARTIE_OP is

  procedure init_mem;
  -- initialise les registres GB, LB, SP et la pile

  procedure exec_inst;
  -- execute l'instruction de RI, en mode mise au point
  -- si mise_au_point = true

  procedure finir_lecture;
  -- termine une lecture d'entiers ou de reels : va jusqu'a la fin de ligne
  -- Cette procedure n'est appelee qu'en mode mise-au point, et est utilisee
  -- lorsqu'une instruction de lecture precede un point d'arret.

  procedure finir_ecriture;
  -- termine une ecriture : va a la ligne
  -- Cette procedure n'est appelee qu'en mode mise-au point, et est utilisee
  -- lorsqu'une instruction d'ecriture precede un point d'arret.

  procedure afficher_val_reg;
  -- affiche la valeur des registres SP, GB, LB et des Ri.
  -- affiche aussi la valeur des codes condition
  -- Remarque : cette procedure d'affichage n'a en principe pas grand chose
  -- a voir avec une partie opertive, mais on la met la pour simplifier.

  procedure afficher_val_pile(dmin, dmax : deplacement);
  -- affiche le contenu de la pile entre les adresses dmin(GB) et dmax(GB).
  -- Remarque : cette procedure d'affichage n'a en principe pas grand chose
  -- a voir avec une partie opertive, mais on la met la pour simplifier.

  procedure init_temps;
  -- remet a zero le temps d'execution 

  function temps_d_exec return natural;
  -- retourne le temps d'execution depuis la derniere initialisation 

  function message_erreur_exec_inst return string;
  -- rend la chaine de caractere decrivant la derniere erreur 
  -- survenue lors de l'execution d'une instruction (cette chaine
  -- permet d'avoir une information supplementaire lorsque
  -- l'exception erreur_exec_inst est levee)

  erreur_exec_inst, fin_du_programme : exception;

end PARTIE_OP;
