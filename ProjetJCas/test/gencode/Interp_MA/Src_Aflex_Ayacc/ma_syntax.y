------------------------------------------------------------------------
-- ma_tokens.y : codes de la Machine Abstraite                        --
--                                                                    --
-- Auteur : X. Nicollin                                               --
--                                                                    --
-- Date de creation : 01/94                                           --
-- Date de derniere modification : 17/11/94                           --
--                                                                    --
------------------------------------------------------------------------


%token NL_lex          ETIQ_lex        REGB_lex      DEPL_lex
%token CONSTENTPOS_lex CONSTENTNEG_lex CONSTREEL_lex CONSTCHAINE_lex
%token LOAD_lex        STORE_lex       LEA_lex       PEA_lex         PUSH_lex
%token POP_lex         ADDSP_lex       SUBSP_lex     ADD_lex         SUB_lex
%token OPP_lex         MUL_lex         DIV_lex       CMP_lex         MOD_lex
%token FLOAT_lex       SEQ_lex         SNE_lex       SGT_lex         SLT_lex
%token SGE_lex         SLE_lex         SOV_lex       INT_lex         BRA_lex
%token BEQ_lex         BNE_lex         BGT_lex       BLT_lex         BGE_lex
%token BLE_lex         BOV_lex         BSR_lex       RTS_lex         RINT_lex
%token RFLOAT_lex      WINT_lex        WFLOAT_lex    WSTR_lex        WNL_lex
%token TSTO_lex        HALT_lex        LB_lex        GB_lex

%with TYPES_BASE, PSEUDO_CODE;
%use  TYPES_BASE, PSEUDO_CODE;

{ 
  subtype Num_reg_banalise is natural 
    range 0 ..  banalise'pos(banalise'last) - banalise'pos(banalise'first);

  -- discriminant de yystype
  type type_valeur is 
    (nt_list_ligne, nt_adressage, nt_instr, nt_codop, nt_reg,
     lex_etiq, lex_entier, lex_reel, lex_regb, lex_chaine, lex_autre);

  -- le type de yylval (voir MA_lexico.ads) et de yyval (voir MA_rules.y)
  type yystype (discriminant : type_valeur := lex_autre) is record
    case discriminant is
      when nt_list_ligne =>             -- liste de lignes
        val_list_ligne : ligne; 
        val_dern_ligne : ligne;
      when nt_adressage =>              -- adressage
        val_adressage : operande;
      when nt_instr =>                  -- ligne
        num_ligne_instr : positive;
        val_instr : inst;
      when nt_codop =>                  -- code op
        num_ligne_codop : positive;
        val_code : code_operation;
      when nt_reg =>
        val_reg : registre;
      when lex_etiq =>                  -- etiquette
        num_ligne_etiq : positive;
        val_etiq : chaine;
      when lex_entier =>                -- constante entier
        num_ligne_entier : positive;
        val_ent : entier;
      when lex_reel =>                  -- constante reelle
        num_ligne_reel : positive;
        val_reel : reel;
      when lex_regb =>                  -- registre banalise
        num_ligne_regb : positive;
        num_regb : Num_reg_banalise; 
      when lex_chaine =>                -- constante chaine
        num_ligne_chaine : positive;
        val_chaine : chaine;
      when lex_autre =>                 -- autres unites lexicales
        num_ligne_autre : positive;
    end case;
  end record;
}

------------------------------------------------------------------------
-- ma_rules.y : specification Ayacc de la syntaxe de la MA            --
--                                                                    --
-- Auteur : X. Nicollin                                               --
--                                                                    --
-- Date de creation : 01/94                                           --
-- Dates de modifications :                                           --
--   27/11/94 :                                                       --
--     acces_nom --> acces_string (changement dans pseudo_code.ads)   --
--   15/10/95 :                                                       --
--     Changements dans la syntaxe :                                  --
--       PUSH dval --> PUSH Rm                                        --
--       WINT, WFLOAT, RINT et RFLOAT deviennent unaires              -- 
------------------------------------------------------------------------



%start programme

%%

programme 
 : 
     { $$ := (nt_list_ligne, null, null); }
 | programme ligne 
     { if $1.val_list_ligne = null then
         $$ := $2;
       elsif $2.val_list_ligne = null then
         $$ := $1;
       else
         changer_suivant($1.val_dern_ligne, $2.val_list_ligne); 
         $$ := (nt_list_ligne, $1.val_list_ligne, $2.val_dern_ligne); 
       end if; }
  ;

ligne 
  : listeetiq NL_lex 
      { $$ := $1; }
  | listeetiq instruction NL_lex 
      { declare
          L : ligne := creation(null, $2.val_instr, "", 
                                null, $2.num_ligne_instr);
        begin
          if $1.val_list_ligne = null then
            $$ := (nt_list_ligne, L, L);
          else
            changer_suivant($1.val_dern_ligne, L);
            $$ := (nt_list_ligne, $1.val_list_ligne, L);
          end if;
        end; }
  ;

listeetiq 
  :
      { $$ := (nt_list_ligne, null, null); }
  | listeetiq etiquette  
      { if $1.val_list_ligne = null then
          $$ := $2;
        else
          changer_suivant($1.val_dern_ligne, $2.val_list_ligne);
          $$ := (nt_list_ligne, $1.val_list_ligne, $2.val_dern_ligne);
        end if; }
  ;

etiquette
  : ETIQ_lex ':' 
      { declare
          E : etiq;
          L : ligne := creation(null, null, "", null, $1.num_ligne_etiq);
        begin
          ins_def_etiq(acces_string($1.val_etiq), E);
          changer_etiq(L, E);
          changer_ligne_def(E, L);
          $$ := (nt_list_ligne, L, L);
        exception
          when MA_double_defetiq =>
            put_line("ERREUR ligne " & positive'image($1.num_ligne_etiq) &
                     " : etiquette " & acces_string($1.val_etiq) &
                     " deja definie !!");
            raise MA_double_defetiq;
          end; }
  ;

instruction 
  : LOAD_lex dval ',' REGB_lex 
      { $$ := (nt_instr, $1.num_ligne_autre,
               creation_inst2(code_LOAD, $2.val_adressage, 
                                         creation_direct($4.num_regb))); }
  | STORE_lex REGB_lex ',' dadr 
      { $$ := (nt_instr, $1.num_ligne_autre,
               creation_inst2(code_STORE, creation_direct($2.num_regb), 
                                          $4.val_adressage)); }
  | LEA_lex dadr ',' REGB_lex
      { $$ := (nt_instr, $1.num_ligne_autre,
               creation_inst2(code_LEA, $2.val_adressage, 
                                        creation_direct($4.num_regb))); }

  | PEA_lex dadr
      { $$ := (nt_instr, $1.num_ligne_autre,
               creation_inst1(code_PEA, $2.val_adressage)); }
  | ADDSP_lex CONSTENTPOS_lex
      { $$ := (nt_instr, $1.num_ligne_autre,
               creation_inst1(code_ADDSP, creation_op_entier($2.val_ent))); }
  | SUBSP_lex CONSTENTPOS_lex
      { $$ := (nt_instr, $1.num_ligne_autre,
               creation_inst1(code_SUBSP, creation_op_entier($2.val_ent))); }
  | oparithmbin dval ',' REGB_lex
      { $$ := (nt_instr, $1.num_ligne_codop,
               creation_inst2($1.val_code, $2.val_adressage, 
                                           creation_direct($4.num_regb))); } 
  | oparithment dvalent ',' REGB_lex
      { $$ := (nt_instr, $1.num_ligne_codop,
               creation_inst2($1.val_code, $2.val_adressage, 
                                           creation_direct($4.num_regb))); } 
  | oparithmreel dvalreel ',' REGB_lex
      { $$ := (nt_instr, $1.num_ligne_codop,
               creation_inst2($1.val_code, $2.val_adressage, 
                                           creation_direct($4.num_regb))); }
  | opunreg REGB_lex
      { $$ := (nt_instr, $1.num_ligne_codop,
               creation_inst1($1.val_code, creation_direct($2.num_regb))); }
  | branch ETIQ_lex
      { declare
          E : etiq;
        begin
          ins_util_etiq(acces_string($2.val_etiq), E);
          $$ := (nt_instr, $1.num_ligne_codop,
                 creation_inst1($1.val_code, creation_op_etiq(E))); 
        end; }
  | opzero
      { $$ := (nt_instr, $1.num_ligne_codop, creation_inst0($1.val_code)); }
  | WSTR_lex CONSTCHAINE_lex
      { $$ := (nt_instr, $1.num_ligne_autre,
               creation_inst1(code_WSTR, creation_op_chaine($2.val_chaine))); }
  | TSTO_lex CONSTENTPOS_lex
      { $$ := (nt_instr, $1.num_ligne_autre,
               creation_inst1(code_TSTO, creation_op_entier($2.val_ent))); }
  ;

oparithmbin 
  : ADD_lex  
      { $$ := (nt_codop, $1.num_ligne_autre, code_ADD); }
  | SUB_lex
      { $$ := (nt_codop, $1.num_ligne_autre, code_SUB); }
  | OPP_lex
      { $$ := (nt_codop, $1.num_ligne_autre, code_OPP); }
  | MUL_lex
      { $$ := (nt_codop, $1.num_ligne_autre, code_MUL); }
  | CMP_lex
      { $$ := (nt_codop, $1.num_ligne_autre, code_CMP); }
  | DIV_lex  
      { $$ := (nt_codop, $1.num_ligne_autre, code_DIV); }
  ;

oparithment
  : MOD_lex 
      { $$ := (nt_codop, $1.num_ligne_autre, code_MOD); }
  | FLOAT_lex
      { $$ := (nt_codop, $1.num_ligne_autre, code_FLOAT); }
  ;

oparithmreel
  : INT_lex
      { $$ := (nt_codop, $1.num_ligne_autre, code_INT); }
  ;

opunreg 
  : SEQ_lex
      { $$ := (nt_codop, $1.num_ligne_autre, code_SEQ); }
  | SNE_lex
      { $$ := (nt_codop, $1.num_ligne_autre, code_SNE); }
  | SGT_lex
      { $$ := (nt_codop, $1.num_ligne_autre, code_SGT); }
  | SLT_lex
      { $$ := (nt_codop, $1.num_ligne_autre, code_SLT); }
  | SGE_lex
      { $$ := (nt_codop, $1.num_ligne_autre, code_SGE); }
  | SLE_lex
      { $$ := (nt_codop, $1.num_ligne_autre, code_SLE); }
  | SOV_lex
      { $$ := (nt_codop, $1.num_ligne_autre, code_SOV); }
  | PUSH_lex 
      { $$ := (nt_codop, $1.num_ligne_autre, code_PUSH); }
  | POP_lex
      { $$ := (nt_codop, $1.num_ligne_autre, code_POP); }
  ;

opzero
  : RTS_lex
      { $$ := (nt_codop, $1.num_ligne_autre, code_RTS); }
  | RINT_lex
      { $$ := (nt_codop, $1.num_ligne_autre, code_RINT); }
  | RFLOAT_lex
      { $$ := (nt_codop, $1.num_ligne_autre, code_RFLOAT); }
  | WINT_lex 
      { $$ := (nt_codop, $1.num_ligne_autre, code_WINT); }
  | WFLOAT_lex 
      { $$ := (nt_codop, $1.num_ligne_autre, code_WFLOAT); }
  | WNL_lex
      { $$ := (nt_codop, $1.num_ligne_autre, code_WNL); }
  | HALT_lex
      { $$ := (nt_codop, $1.num_ligne_autre, code_HALT); }
  ;

branch
  : BRA_lex
      { $$ := (nt_codop, $1.num_ligne_autre, code_BRA); }
  | BEQ_lex
      { $$ := (nt_codop, $1.num_ligne_autre, code_BEQ); }
  | BNE_lex
      { $$ := (nt_codop, $1.num_ligne_autre, code_BNE); }
  | BGT_lex
      { $$ := (nt_codop, $1.num_ligne_autre, code_BGT); }
  | BLT_lex
      { $$ := (nt_codop, $1.num_ligne_autre, code_BLT); }
  | BGE_lex
      { $$ := (nt_codop, $1.num_ligne_autre, code_BGE); }
  | BLE_lex
      { $$ := (nt_codop, $1.num_ligne_autre, code_BLE); }
  | BOV_lex
      { $$ := (nt_codop, $1.num_ligne_autre, code_BOV); }
  | BSR_lex
      { $$ := (nt_codop, $1.num_ligne_autre, code_BSR); }
  ;

dval
  : dvalreel
      { $$ := $1; }
  | constantent
      { $$ := $1; }
  ;

dvalent
  : dadr
      { $$ := $1; }
  | REGB_lex
      { $$ := (nt_adressage,creation_direct($1.num_regb)); }
  | constantent
      { $$ := $1; }
  ;

dvalreel
  : dadr
      { $$ := $1; }
  | REGB_lex
      { $$ := (nt_adressage,creation_direct($1.num_regb)); }
  | CONSTREEL_lex
      { $$ := (nt_adressage, creation_op_reel($1.val_reel)); }
  ;

dadr
  : DEPL_lex '(' xx ')'            
      { $$ := (nt_adressage, 
               creation_op_indirect(deplacement($1.val_ent), $3.val_reg)); }
  | DEPL_lex '(' xx ',' REGB_lex ')' 
      { $$ := (nt_adressage,
               creation_op_indexe(deplacement($1.val_ent), $3.val_reg,
                                  R($5.num_regb))); }
  ;

constantent
  : CONSTENTPOS_lex  
      { $$ := (nt_adressage,creation_op_entier($1.val_ent)); }
  | CONSTENTNEG_lex   
      { $$ := (nt_adressage,creation_op_entier($1.val_ent)); }
  ;

xx
  : REGB_lex 
      { $$ := (nt_reg, R($1.num_regb)); }
  | LB_lex
      { $$ := (nt_reg, LB); }
  | GB_lex
      { $$ := (nt_reg, GB); }
  ;

%%

-- corps du paquetage d'analyse syntaxique

with Ada.TEXT_IO, MA_LEXICO, MA_SYNTAX_SHIFT_REDUCE, MA_SYNTAX_GOTO, TYPES_BASE,
     MA_DETIQ, PSEUDO_CODE; 
use  Ada.TEXT_IO, MA_LEXICO, MA_SYNTAX_SHIFT_REDUCE, MA_SYNTAX_GOTO, TYPES_BASE,
     MA_DETIQ, PSEUDO_CODE; 

package body MA_SYNTAX is

  function R(n : Num_reg_banalise) return banalise is
  begin
    return banalise'val(n + banalise'pos(banalise'first));
  end R;

  function creation_direct(n : Num_reg_banalise) return operande is
  begin
    return le_registre(R(n));
  end creation_direct;

  procedure yyerror(text : in string) is
  -- affiche un message d'erreur
  begin
    put_line("***** ligne" & positive'image(num_ligne) & " : " & text);
  end yyerror;

  
  -- yyparse
  ##

  procedure analyser_construire_liste_lignes(L : out ligne;
                                             nblignes : out natural) is
  begin
    yyparse;
    L := yyval.val_list_ligne;
    nblignes := num_ligne - 1;
  end analyser_construire_liste_lignes;

end MA_SYNTAX;
