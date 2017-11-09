-- A lexical scanner generated by aflex
with Ada.Text_IO; use Ada.Text_IO;
with ma_lexico_DFA; use ma_lexico_DFA; 
with ma_lexico_IO; use ma_lexico_IO; 
--# line 1 "ma_lexico.l"
------------------------------------------------------------------------
-- ma_lexico.l : source Aflex de l'analyseur lexical du langage       --
--               d'assemblage de la machine abstraite                 --
--                                                                    --
-- Auteur : X. Nicollin                                               --
--                                                                    --
-- Date de creation : 01/94                                           --
-- Date de derniere modification : 17/11/94                           --
------------------------------------------------------------------------
--# line 39 "ma_lexico.l"


with MA_DICT, TYPES_BASE, REEL_ES;
use  MA_DICT, TYPES_BASE, REEL_ES;

pragma Elaborate_All (MA_Dict);
package body MA_LEXICO is

  subtype Chiffre is Character range '0' .. '9';
  chiffres       : constant array(Chiffre) of natural := 
                                (0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    -- pour convertir un chiffre en sa valeur numerique

  ligne_courante :          positive := 1; -- : numero de ligne courante

  -----------------------------------------------------------------------------

  function num_ligne return positive is
  begin
     return ligne_courante;
  end num_ligne;

  -----------------------------------------------------------------------------

  function valeur_chaine (S : string) return chaine is
  -- retourne une copie protegee de la constante chaine de caracteres
  begin
    return creation(S(S'first+1..S'last-1));
  end valeur_chaine;

  -----------------------------------------------------------------------------

  function valeur_chaine2 (S : string) return chaine is
  -- retourne une copie protegee de la constante chaine de caracteres
    temp         : string(S'range);
    indice_copie : integer         := temp'first;
    indice       : integer         := S'first + 1;
  begin
    while indice <= S'last-1 loop
      temp(indice_copie) := S(indice);
      indice_copie := indice_copie+1;
      if S(indice) /= '"' then 
        indice := indice + 1;
      else 
        indice := indice + 2; 
      end if;
    end loop;
    return creation(temp(temp'first .. indice_copie-1));
  end valeur_chaine2;

  -----------------------------------------------------------------------------

  procedure erreur(S : in string) is
  -- affichage de message d'erreur 
  begin
    put_line ("LIGNE " & positive'image(ligne_courante) & " -- ");
    put_line ("   Erreur : " & S);
  end erreur;

  -----------------------------------------------------------------------------

  function horner (S : string) return natural is
  -- convertit une chaine de chiffres en sa valeur numerique
    -- val : natural := 0;
  begin
    return Natural'Value (S);
    -- for i in S'range loop
      -- val := val * 10 + chiffres(S(i));
    -- end loop;
    -- return val;
  end horner;

  -----------------------------------------------------------------------------

  procedure normaliser  (S : in out string) is
  -- convertit une chaine d'alphanumeriques en majuscules
    diff : constant := character'pos('A')-character'pos('a');
  begin
    for i in S'range loop
      if S(i) in 'a'..'z' then 
        S(i) := character'val(character'pos(S(i)) + diff); 
      end if;
    end loop;
  end normaliser;

  -----------------------------------------------------------------------------

  function sup_esp(S : string) return string is
  -- supprime l'espace en seconde position de S
    S1 :         string(1 .. S'length-1);
  begin
    S1(1) := S(S'first);
    S1(2 .. S1'last) := S(S'first + 2 .. S'last);
    return S1;
  end sup_esp;

  -----------------------------------------------------------------------------

  procedure init_dico is
  -- initialise le dictionnaire avecles ``identificateurs'' autorises :
  -- codes op, LB et GB 
  begin
    ins_tok ("LOAD",    LOAD_lex);
    ins_tok ("STORE",   STORE_lex);
    ins_tok ("LEA",     LEA_lex);
    ins_tok ("PEA",     PEA_lex);
    ins_tok ("PUSH",    PUSH_lex);
    ins_tok ("POP",     POP_lex);
    ins_tok ("ADDSP",   ADDSP_lex);
    ins_tok ("SUBSP",   SUBSP_lex);
    ins_tok ("ADD",     ADD_lex);
    ins_tok ("SUB",     SUB_lex);
    ins_tok ("OPP",     OPP_lex);
    ins_tok ("MUL",     MUL_lex);
    ins_tok ("DIV",     DIV_lex);
    ins_tok ("CMP",     CMP_lex);
    ins_tok ("MOD",     MOD_lex);
    ins_tok ("FLOAT",   FLOAT_lex);
    ins_tok ("SEQ",     SEQ_lex);
    ins_tok ("SNE",     SNE_lex);
    ins_tok ("SGT",     SGT_lex);
    ins_tok ("SLT",     SLT_lex);
    ins_tok ("SGE",     SGE_lex);
    ins_tok ("SLE",     SLE_lex);
    ins_tok ("SOV",     SOV_lex);
    ins_tok ("INT",     INT_lex);
    ins_tok ("BRA",     BRA_lex);
    ins_tok ("BEQ",     BEQ_lex);
    ins_tok ("BNE",     BNE_lex);
    ins_tok ("BGT",     BGT_lex);
    ins_tok ("BLT",     BLT_lex);
    ins_tok ("BGE",     BGE_lex);
    ins_tok ("BLE",     BLE_lex);
    ins_tok ("BOV",     BOV_lex);
    ins_tok ("BSR",     BSR_lex);
    ins_tok ("RTS",     RTS_lex);
    ins_tok ("RINT",    RINT_lex);
    ins_tok ("RFLOAT",  RFLOAT_lex);
    ins_tok ("WINT",    WINT_lex);
    ins_tok ("WFLOAT",  WFLOAT_lex);
    ins_tok ("WSTR",    WSTR_lex);
    ins_tok ("WNL",     WNL_lex);
    ins_tok ("TSTO",    TSTO_lex);
    ins_tok ("HALT",    HALT_lex);

    ins_tok ("LB",      LB_lex);
    ins_tok ("GB",      GB_lex);

    for i in Num_reg_banalise loop
      ins_tok(sup_esp("R" & Num_reg_banalise'image(i)), REGB_lex);
    end loop;
  end init_dico;

-- code de yylex
function YYLex return Token is
subtype short is integer range -32768..32767;
    yy_act : integer;
    yy_c : short;

-- returned upon end-of-file
YY_END_TOK : constant integer := 0;
YY_END_OF_BUFFER : constant := 19;
subtype yy_state_type is integer;
yy_current_state : yy_state_type;
INITIAL : constant := 0;
yy_accept : constant array(0..38) of short :=
    (   0,
        0,    0,   19,   17,    2,    1,   17,   17,   11,   12,
       17,   13,   17,    5,   16,    2,    3,    0,   14,    0,
        0,    8,    4,    6,    5,    2,    3,   14,    0,    7,
        9,    0,   15,   10,    0,    0,   10,    0
    ) ;

yy_ec : constant array(CHARACTER'FIRST..CHARACTER'LAST) of short :=
    (   0,
        1,    1,    1,    1,    1,    1,    1,    1,    2,    3,
        1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
        1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
        1,    4,    5,    6,    7,    5,    5,    5,    5,    8,
        9,    5,   10,   11,   12,   13,    5,   14,   14,   14,
       14,   14,   14,   14,   14,   14,   14,   15,   16,    5,
        5,    5,    5,    5,   17,   17,   17,   17,   18,   17,
       17,   17,   17,   17,   17,   17,   17,   17,   17,   17,
       17,   17,   17,   17,   17,   17,   17,   17,   17,   17,
        5,    5,    5,    5,   19,    5,   17,   17,   17,   17,

       17,   17,   17,   17,   17,   17,   17,   17,   17,   17,
       17,   17,   17,   17,   17,   17,   17,   17,   17,   17,
       17,   17,    5,    5,    5,    5,   20,   20,   20,   20,
       20,   20,   20,   20,   20,   20,   20,   20,   20,   20,
       20,   20,   20,   20,   20,   20,   20,   20,   20,   20,
       20,   20,   20,   20,   20,   20,   20,   20,   20,   20,
       20,   20,   20,   20,   20,   20,   20,   20,   20,   20,
       20,   20,   20,   20,   20,   20,   20,   20,   20,   20,
       20,   20,   20,   20,   20,   20,   20,   20,   20,   20,
       20,   20,   20,   20,   20,   20,   20,   20,   20,   20,

       20,   20,   20,   20,   20,   20,   20,   20,   20,   20,
       20,   20,   20,   20,   20,   20,   20,   20,   20,   20,
       20,   20,   20,   20,   20,   20,   20,   20,   20,   20,
       20,   20,   20,   20,   20,   20,   20,   20,   20,   20,
       20,   20,   20,   20,   20,   20,   20,   20,   20,   20,
       20,   20,   20,   20,   20
    ) ;

yy_meta : constant array(0..20) of short :=
    (   0,
        1,    2,    1,    3,    3,    3,    3,    3,    3,    4,
        3,    4,    5,    6,    3,    3,    5,    5,    5,    2
    ) ;

yy_base : constant array(0..43) of short :=
    (   0,
        0,    0,   67,   68,   68,   68,   60,   11,   68,   68,
       51,   68,   50,   49,   68,    0,    0,   56,   55,   46,
       45,   13,   44,   43,   42,    0,    0,   49,   47,   15,
       17,   21,   28,   18,   19,   10,    8,   68,   34,   39,
       41,   45,   48
    ) ;

yy_def : constant array(0..43) of short :=
    (   0,
       38,    1,   38,   38,   38,   38,   39,   38,   38,   38,
       38,   38,   38,   38,   38,   40,   41,   39,   38,   38,
       38,   38,   38,   38,   38,   40,   41,   38,   42,   38,
       38,   38,   38,   38,   43,   38,   38,    0,   38,   38,
       38,   38,   38
    ) ;

yy_nxt : constant array(0..88) of short :=
    (   0,
        4,    5,    6,    5,    4,    7,    8,    9,   10,   11,
       12,   13,    4,   14,   15,   16,   17,   17,    4,    4,
       20,   37,   21,   37,   22,   32,   22,   32,   30,   32,
       31,   34,   37,   29,   34,   35,   18,   18,   18,   18,
       26,   26,   26,   26,   26,   27,   27,   29,   29,   29,
       29,   36,   33,   36,   29,   25,   24,   23,   31,   30,
       29,   28,   25,   24,   23,   19,   38,    3,   38,   38,
       38,   38,   38,   38,   38,   38,   38,   38,   38,   38,
       38,   38,   38,   38,   38,   38,   38,   38
    ) ;

yy_chk : constant array(0..88) of short :=
    (   0,
        1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
        1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
        8,   37,    8,   36,    8,   22,   22,   30,   30,   31,
       31,   34,   35,   33,   32,   34,   39,   39,   39,   39,
       40,   40,   40,   40,   40,   41,   41,   42,   42,   42,
       42,   43,   29,   43,   28,   25,   24,   23,   21,   20,
       19,   18,   14,   13,   11,    7,    3,   38,   38,   38,
       38,   38,   38,   38,   38,   38,   38,   38,   38,   38,
       38,   38,   38,   38,   38,   38,   38,   38
    ) ;


-- copy whatever the last rule matched to the standard output

procedure ECHO is
begin
   if (Ada.Text_IO.is_open(user_output_file)) then
     Ada.Text_IO.put( user_output_file, yytext );
   else
     Ada.Text_IO.put( yytext );
   end if;
end ECHO;

-- enter a start condition.
-- Using procedure requires a () after the ENTER, but makes everything
-- much neater.

procedure ENTER( state : integer ) is
begin
     yy_start := 1 + 2 * state;
end ENTER;

-- action number for EOF rule of a given start state
function YY_STATE_EOF(state : integer) return integer is
begin
     return YY_END_OF_BUFFER + state + 1;
end YY_STATE_EOF;

-- return all but the first 'n' matched characters back to the input stream
procedure yyless(n : integer) is
begin
        yy_ch_buf(yy_cp) := yy_hold_char; -- undo effects of setting up yytext
        yy_cp := yy_bp + n;
        yy_c_buf_p := yy_cp;
        YY_DO_BEFORE_ACTION; -- set up yytext again
end yyless;

-- redefine this if you have something you want each time.
procedure YY_USER_ACTION is
begin
        null;
end;

-- yy_get_previous_state - get the state just before the EOB char was reached

function yy_get_previous_state return yy_state_type is
    yy_current_state : yy_state_type;
    yy_c : short;
begin
    yy_current_state := yy_start;

    for yy_cp in yytext_ptr..yy_c_buf_p - 1 loop
    yy_c := yy_ec(yy_ch_buf(yy_cp));
    if ( yy_accept(yy_current_state) /= 0 ) then
        yy_last_accepting_state := yy_current_state;
        yy_last_accepting_cpos := yy_cp;
    end if;
    while ( yy_chk(yy_base(yy_current_state) + yy_c) /= yy_current_state ) loop
        yy_current_state := yy_def(yy_current_state);
        if ( yy_current_state >= 39 ) then
        yy_c := yy_meta(yy_c);
        end if;
    end loop;
    yy_current_state := yy_nxt(yy_base(yy_current_state) + yy_c);
    end loop;

    return yy_current_state;
end yy_get_previous_state;

procedure yyrestart( input_file : file_type ) is
begin
   open_input(Ada.Text_IO.name(input_file));
end yyrestart;

begin -- of YYLex
<<new_file>>
        -- this is where we enter upon encountering an end-of-file and
        -- yywrap() indicating that we should continue processing

    if ( yy_init ) then
        if ( yy_start = 0 ) then
            yy_start := 1;      -- first start state
        end if;

        -- we put in the '\n' and start reading from [1] so that an
        -- initial match-at-newline will be true.

        yy_ch_buf(0) := ASCII.LF;
        yy_n_chars := 1;

        -- we always need two end-of-buffer characters.  The first causes
        -- a transition to the end-of-buffer state.  The second causes
        -- a jam in that state.

        yy_ch_buf(yy_n_chars) := YY_END_OF_BUFFER_CHAR;
        yy_ch_buf(yy_n_chars + 1) := YY_END_OF_BUFFER_CHAR;

        yy_eof_has_been_seen := false;

        yytext_ptr := 1;
        yy_c_buf_p := yytext_ptr;
        yy_hold_char := yy_ch_buf(yy_c_buf_p);
        yy_init := false;
    end if; -- yy_init

    loop                -- loops until end-of-file is reached


        yy_cp := yy_c_buf_p;

        -- support of yytext
        yy_ch_buf(yy_cp) := yy_hold_char;

        -- yy_bp points to the position in yy_ch_buf of the start of the
        -- current run.
    yy_bp := yy_cp;
    yy_current_state := yy_start;
    loop
        yy_c := yy_ec(yy_ch_buf(yy_cp));
        if ( yy_accept(yy_current_state) /= 0 ) then
            yy_last_accepting_state := yy_current_state;
            yy_last_accepting_cpos := yy_cp;
        end if;
        while ( yy_chk(yy_base(yy_current_state) + yy_c) /= yy_current_state ) loop
            yy_current_state := yy_def(yy_current_state);
            if ( yy_current_state >= 39 ) then
            yy_c := yy_meta(yy_c);
            end if;
        end loop;
        yy_current_state := yy_nxt(yy_base(yy_current_state) + yy_c);
        yy_cp := yy_cp + 1;
if ( yy_current_state = 38 ) then
    exit;
end if;
    end loop;
    yy_cp := yy_last_accepting_cpos;
    yy_current_state := yy_last_accepting_state;

<<next_action>>
        yy_act := yy_accept(yy_current_state);
            YY_DO_BEFORE_ACTION;
            YY_USER_ACTION;

        if aflex_debug then  -- output acceptance info. for (-d) debug mode
            Ada.Text_IO.put( Standard_Error, "--accepting rule #" );
            Ada.Text_IO.put( Standard_Error, INTEGER'IMAGE(yy_act) );
            Ada.Text_IO.put_line( Standard_Error, "(""" & yytext & """)");
        end if;


<<do_action>>   -- this label is used only to access EOF actions
            case yy_act is
        when 0 => -- must backtrack
        -- undo the effects of YY_DO_BEFORE_ACTION
        yy_ch_buf(yy_cp) := yy_hold_char;
        yy_cp := yy_last_accepting_cpos;
        yy_current_state := yy_last_accepting_state;
        goto next_action;




when 1 => 
--# line 42 "ma_lexico.l"
 
  yylval := (lex_autre, ligne_courante);
  ligne_courante := ligne_courante + 1; 
  return NL_lex;   

when 2 => 
--# line 48 "ma_lexico.l"
 
  null; 

when 3 => 
--# line 52 "ma_lexico.l"
 
  declare
    texte  : string(yytext'range) := yytext;
    att    : chaine;
    code   : token                := ETIQ_lex;
  begin
    normaliser(texte);
    mise_a_jour(texte, code, att);
    case code is
      when ETIQ_lex =>
        yylval := (lex_etiq, ligne_courante, att);
      when REGB_lex =>
        yylval := (lex_regb, ligne_courante, 
                   horner(texte( texte'first+1 ..  texte'last)));
      when others =>
        yylval := (lex_autre, ligne_courante);
    end case;
    return code;
  end; 

when 4 => 
--# line 73 "ma_lexico.l"
 
  begin
    yylval := (lex_entier, ligne_courante, 
               entier(horner(yytext(yytext'first+1 .. yytext'last))));
    return DEPL_lex;
  exception
    when others => 
      erreur("depassement de capacite d'entier") ;
      raise MA_erreur_conversion;
  end ; 

when 5 => 
--# line 85 "ma_lexico.l"
 
  begin
    yylval := (lex_entier, ligne_courante, entier(horner(yytext)));
    return DEPL_lex;
  exception
    when others => 
      erreur("depassement de capacite d'entier") ;
      raise MA_erreur_conversion;
  end ; 

when 6 => 
--# line 96 "ma_lexico.l"
 
  begin
    yylval := (lex_entier, ligne_courante, 
               entier(-horner(yytext(yytext'first+1 .. yytext'last))));
    return DEPL_lex; 
  exception
    when others => 
      erreur("depassement de capacite d'entier") ;
      raise MA_erreur_conversion;
  end; 

when 7 => 
--# line 108 "ma_lexico.l"
 
  begin
    yylval := (lex_entier, ligne_courante, 
               entier(horner(yytext(yytext'first+2 .. yytext'last))));
    return CONSTENTPOS_lex; 
  exception
    when others => 
      erreur("depassement de capacite d'entier") ;
      raise MA_erreur_conversion;
  end; 

when 8 => 
--# line 120 "ma_lexico.l"
 
  begin
    yylval := (lex_entier, ligne_courante, 
               entier(horner(yytext(yytext'first+1 .. yytext'last))));
    return CONSTENTPOS_lex; 
  exception
    when others => 
      erreur("depassement de capacite d'entier") ;
      raise MA_erreur_conversion;
  end; 

when 9 => 
--# line 132 "ma_lexico.l"
 
  begin
    yylval := (lex_entier, ligne_courante, 
               entier(-horner(yytext(yytext'first+2 .. yytext'last))));
    return CONSTENTNEG_lex; 
  exception
    when others => 
      erreur("depassement de capacite d'entier") ;
      raise MA_erreur_conversion;
  end; 

when 10 => 
--# line 144 "ma_lexico.l"

  declare 
    val_reel : reel; 
    long     : positive; 
    temp     : string(yytext'first+1 .. yytext'last);
  begin
    temp := yytext(yytext'first+1 .. yytext'last);
    get(temp, val_reel, long);
    yylval := (lex_reel, ligne_courante, val_reel);
    return (CONSTREEL_lex); 
  exception
    when others => 
      erreur("depassement de capacite de reel") ;
      raise MA_erreur_conversion;
  end; 

when 11 => 
--# line 161 "ma_lexico.l"
 
  yylval := (lex_autre, ligne_courante);
  return '('; 

when 12 => 
--# line 166 "ma_lexico.l"
 
  yylval := (lex_autre, ligne_courante);
  return ')'; 

when 13 => 
--# line 171 "ma_lexico.l"
 
  yylval := (lex_autre, ligne_courante);
  return ','; 

when 14 => 
--# line 176 "ma_lexico.l"
 --  Les chaines simples sont plus faciles a convertir.
  yylval := (lex_chaine, ligne_courante, valeur_chaine(yytext));
  return CONSTCHAINE_lex; 

when 15 => 
--# line 181 "ma_lexico.l"
 -- Il faut convertir les chaines dans le cas general 
  yylval := (lex_chaine, ligne_courante, valeur_chaine2(yytext));
  return CONSTCHAINE_lex; 

when 16 => 
--# line 186 "ma_lexico.l"
 
  yylval := (lex_autre, ligne_courante);
  return ':'; 

when 17 => 
--# line 191 "ma_lexico.l"
 
  erreur("caractere " & yytext(1) & " non permis"); 
  raise MA_erreur_lexicale ; 

when 18 => 
--# line 195 "ma_lexico.l"
ECHO;
when YY_END_OF_BUFFER + INITIAL + 1 => 
    return End_Of_Input;
                when YY_END_OF_BUFFER =>
                    -- undo the effects of YY_DO_BEFORE_ACTION
                    yy_ch_buf(yy_cp) := yy_hold_char;

                    yytext_ptr := yy_bp;

                    case yy_get_next_buffer is
                        when EOB_ACT_END_OF_FILE =>
                            begin
                            if ( yywrap ) then
                                -- note: because we've taken care in
                                -- yy_get_next_buffer() to have set up yytext,
                                -- we can now set up yy_c_buf_p so that if some
                                -- total hoser (like aflex itself) wants
                                -- to call the scanner after we return the
                                -- End_Of_Input, it'll still work - another
                                -- End_Of_Input will get returned.

                                yy_c_buf_p := yytext_ptr;

                                yy_act := YY_STATE_EOF((yy_start - 1) / 2);

                                goto do_action;
                            else
                                --  start processing a new file
                                yy_init := true;
                                goto new_file;
                            end if;
                            end;
                        when EOB_ACT_RESTART_SCAN =>
                            yy_c_buf_p := yytext_ptr;
                            yy_hold_char := yy_ch_buf(yy_c_buf_p);
                        when EOB_ACT_LAST_MATCH =>
                            yy_c_buf_p := yy_n_chars;
                            yy_current_state := yy_get_previous_state;

                            yy_cp := yy_c_buf_p;
                            yy_bp := yytext_ptr;
                            goto next_action;
                        when others => null;
                        end case; -- case yy_get_next_buffer()
                when others =>
                    Ada.Text_IO.put( "action # " );
                    Ada.Text_IO.put( INTEGER'IMAGE(yy_act) );
                    Ada.Text_IO.new_line;
                    raise AFLEX_INTERNAL_ERROR;
            end case; -- case (yy_act)
        end loop; -- end of loop waiting for end of file
end YYLex;
--# line 195 "ma_lexico.l"

begin
  init_dico;
end MA_LEXICO;

