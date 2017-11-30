pragma Ada_95;
pragma Source_File_Name (ada_main, Spec_File_Name => "b~ima.ads");
pragma Source_File_Name (ada_main, Body_File_Name => "b~ima.adb");
pragma Suppress (Overflow_Check);
with Ada.Exceptions;

package body ada_main is
   pragma Warnings (Off);

   E076 : Short_Integer; pragma Import (Ada, E076, "system__os_lib_E");
   E013 : Short_Integer; pragma Import (Ada, E013, "system__soft_links_E");
   E165 : Short_Integer; pragma Import (Ada, E165, "system__fat_lflt_E");
   E153 : Short_Integer; pragma Import (Ada, E153, "system__fat_llf_E");
   E019 : Short_Integer; pragma Import (Ada, E019, "system__exception_table_E");
   E050 : Short_Integer; pragma Import (Ada, E050, "ada__io_exceptions_E");
   E139 : Short_Integer; pragma Import (Ada, E139, "ada__strings_E");
   E141 : Short_Integer; pragma Import (Ada, E141, "ada__strings__maps_E");
   E144 : Short_Integer; pragma Import (Ada, E144, "ada__strings__maps__constants_E");
   E052 : Short_Integer; pragma Import (Ada, E052, "ada__tags_E");
   E049 : Short_Integer; pragma Import (Ada, E049, "ada__streams_E");
   E074 : Short_Integer; pragma Import (Ada, E074, "interfaces__c_E");
   E021 : Short_Integer; pragma Import (Ada, E021, "system__exceptions_E");
   E079 : Short_Integer; pragma Import (Ada, E079, "system__file_control_block_E");
   E068 : Short_Integer; pragma Import (Ada, E068, "system__file_io_E");
   E072 : Short_Integer; pragma Import (Ada, E072, "system__finalization_root_E");
   E070 : Short_Integer; pragma Import (Ada, E070, "ada__finalization_E");
   E124 : Short_Integer; pragma Import (Ada, E124, "system__storage_pools_E");
   E116 : Short_Integer; pragma Import (Ada, E116, "system__finalization_masters_E");
   E126 : Short_Integer; pragma Import (Ada, E126, "system__pool_global_E");
   E009 : Short_Integer; pragma Import (Ada, E009, "system__secondary_stack_E");
   E047 : Short_Integer; pragma Import (Ada, E047, "ada__text_io_E");
   E178 : Short_Integer; pragma Import (Ada, E178, "ma_lexico_dfa_E");
   E180 : Short_Integer; pragma Import (Ada, E180, "ma_lexico_io_E");
   E194 : Short_Integer; pragma Import (Ada, E194, "options_E");
   E108 : Short_Integer; pragma Import (Ada, E108, "types_base_E");
   E192 : Short_Integer; pragma Import (Ada, E192, "lecture_entiers_E");
   E198 : Short_Integer; pragma Import (Ada, E198, "lecture_reels_E");
   E105 : Short_Integer; pragma Import (Ada, E105, "mes_tables_E");
   E130 : Short_Integer; pragma Import (Ada, E130, "pseudo_code_E");
   E101 : Short_Integer; pragma Import (Ada, E101, "assembleur_E");
   E103 : Short_Integer; pragma Import (Ada, E103, "ma_detiq_E");
   E174 : Short_Integer; pragma Import (Ada, E174, "ma_syntax_tokens_E");
   E169 : Short_Integer; pragma Import (Ada, E169, "ma_dict_E");
   E167 : Short_Integer; pragma Import (Ada, E167, "ma_lexico_E");
   E184 : Short_Integer; pragma Import (Ada, E184, "ma_syntax_E");
   E196 : Short_Integer; pragma Import (Ada, E196, "partie_op_E");
   E134 : Short_Integer; pragma Import (Ada, E134, "pseudo_code__table_E");

   Local_Priority_Specific_Dispatching : constant String := "";
   Local_Interrupt_States : constant String := "";

   Is_Elaborated : Boolean := False;

   procedure finalize_library is
   begin
      E047 := E047 - 1;
      declare
         procedure F1;
         pragma Import (Ada, F1, "ada__text_io__finalize_spec");
      begin
         F1;
      end;
      declare
         procedure F2;
         pragma Import (Ada, F2, "system__file_io__finalize_body");
      begin
         E068 := E068 - 1;
         F2;
      end;
      E116 := E116 - 1;
      E126 := E126 - 1;
      declare
         procedure F3;
         pragma Import (Ada, F3, "system__pool_global__finalize_spec");
      begin
         F3;
      end;
      declare
         procedure F4;
         pragma Import (Ada, F4, "system__finalization_masters__finalize_spec");
      begin
         F4;
      end;
      declare
         procedure Reraise_Library_Exception_If_Any;
            pragma Import (Ada, Reraise_Library_Exception_If_Any, "__gnat_reraise_library_exception_if_any");
      begin
         Reraise_Library_Exception_If_Any;
      end;
   end finalize_library;

   procedure adafinal is
      procedure s_stalib_adafinal;
      pragma Import (C, s_stalib_adafinal, "system__standard_library__adafinal");

      procedure Runtime_Finalize;
      pragma Import (C, Runtime_Finalize, "__gnat_runtime_finalize");

   begin
      if not Is_Elaborated then
         return;
      end if;
      Is_Elaborated := False;
      Runtime_Finalize;
      s_stalib_adafinal;
   end adafinal;

   type No_Param_Proc is access procedure;

   procedure adainit is
      Main_Priority : Integer;
      pragma Import (C, Main_Priority, "__gl_main_priority");
      Time_Slice_Value : Integer;
      pragma Import (C, Time_Slice_Value, "__gl_time_slice_val");
      WC_Encoding : Character;
      pragma Import (C, WC_Encoding, "__gl_wc_encoding");
      Locking_Policy : Character;
      pragma Import (C, Locking_Policy, "__gl_locking_policy");
      Queuing_Policy : Character;
      pragma Import (C, Queuing_Policy, "__gl_queuing_policy");
      Task_Dispatching_Policy : Character;
      pragma Import (C, Task_Dispatching_Policy, "__gl_task_dispatching_policy");
      Priority_Specific_Dispatching : System.Address;
      pragma Import (C, Priority_Specific_Dispatching, "__gl_priority_specific_dispatching");
      Num_Specific_Dispatching : Integer;
      pragma Import (C, Num_Specific_Dispatching, "__gl_num_specific_dispatching");
      Main_CPU : Integer;
      pragma Import (C, Main_CPU, "__gl_main_cpu");
      Interrupt_States : System.Address;
      pragma Import (C, Interrupt_States, "__gl_interrupt_states");
      Num_Interrupt_States : Integer;
      pragma Import (C, Num_Interrupt_States, "__gl_num_interrupt_states");
      Unreserve_All_Interrupts : Integer;
      pragma Import (C, Unreserve_All_Interrupts, "__gl_unreserve_all_interrupts");
      Detect_Blocking : Integer;
      pragma Import (C, Detect_Blocking, "__gl_detect_blocking");
      Default_Stack_Size : Integer;
      pragma Import (C, Default_Stack_Size, "__gl_default_stack_size");
      Leap_Seconds_Support : Integer;
      pragma Import (C, Leap_Seconds_Support, "__gl_leap_seconds_support");

      procedure Runtime_Initialize (Install_Handler : Integer);
      pragma Import (C, Runtime_Initialize, "__gnat_runtime_initialize");

      Finalize_Library_Objects : No_Param_Proc;
      pragma Import (C, Finalize_Library_Objects, "__gnat_finalize_library_objects");
   begin
      if Is_Elaborated then
         return;
      end if;
      Is_Elaborated := True;
      Main_Priority := -1;
      Time_Slice_Value := -1;
      WC_Encoding := 'b';
      Locking_Policy := ' ';
      Queuing_Policy := ' ';
      Task_Dispatching_Policy := ' ';
      Priority_Specific_Dispatching :=
        Local_Priority_Specific_Dispatching'Address;
      Num_Specific_Dispatching := 0;
      Main_CPU := -1;
      Interrupt_States := Local_Interrupt_States'Address;
      Num_Interrupt_States := 0;
      Unreserve_All_Interrupts := 0;
      Detect_Blocking := 0;
      Default_Stack_Size := -1;
      Leap_Seconds_Support := 0;

      Runtime_Initialize (1);

      Finalize_Library_Objects := finalize_library'access;

      System.Soft_Links'Elab_Spec;
      System.Fat_Lflt'Elab_Spec;
      E165 := E165 + 1;
      System.Fat_Llf'Elab_Spec;
      E153 := E153 + 1;
      System.Exception_Table'Elab_Body;
      E019 := E019 + 1;
      Ada.Io_Exceptions'Elab_Spec;
      E050 := E050 + 1;
      Ada.Strings'Elab_Spec;
      E139 := E139 + 1;
      Ada.Strings.Maps'Elab_Spec;
      Ada.Strings.Maps.Constants'Elab_Spec;
      E144 := E144 + 1;
      Ada.Tags'Elab_Spec;
      Ada.Streams'Elab_Spec;
      E049 := E049 + 1;
      Interfaces.C'Elab_Spec;
      System.Exceptions'Elab_Spec;
      E021 := E021 + 1;
      System.File_Control_Block'Elab_Spec;
      E079 := E079 + 1;
      System.Finalization_Root'Elab_Spec;
      E072 := E072 + 1;
      Ada.Finalization'Elab_Spec;
      E070 := E070 + 1;
      System.Storage_Pools'Elab_Spec;
      E124 := E124 + 1;
      System.Finalization_Masters'Elab_Spec;
      System.Pool_Global'Elab_Spec;
      E126 := E126 + 1;
      System.Finalization_Masters'Elab_Body;
      E116 := E116 + 1;
      System.File_Io'Elab_Body;
      E068 := E068 + 1;
      E074 := E074 + 1;
      Ada.Tags'Elab_Body;
      E052 := E052 + 1;
      E141 := E141 + 1;
      System.Soft_Links'Elab_Body;
      E013 := E013 + 1;
      System.Os_Lib'Elab_Body;
      E076 := E076 + 1;
      System.Secondary_Stack'Elab_Body;
      E009 := E009 + 1;
      Ada.Text_Io'Elab_Spec;
      Ada.Text_Io'Elab_Body;
      E047 := E047 + 1;
      E178 := E178 + 1;
      MA_LEXICO_IO'ELAB_SPEC;
      E180 := E180 + 1;
      OPTIONS'ELAB_SPEC;
      E194 := E194 + 1;
      E108 := E108 + 1;
      E192 := E192 + 1;
      E105 := E105 + 1;
      Pseudo_Code'Elab_Spec;
      ASSEMBLEUR'ELAB_SPEC;
      MA_DETIQ'ELAB_SPEC;
      MA_DETIQ'ELAB_BODY;
      E103 := E103 + 1;
      Ma_Syntax_Tokens'Elab_Spec;
      E174 := E174 + 1;
      MA_LEXICO'ELAB_SPEC;
      E184 := E184 + 1;
      ASSEMBLEUR'ELAB_BODY;
      E101 := E101 + 1;
      MA_DICT'ELAB_BODY;
      E169 := E169 + 1;
      PARTIE_OP'ELAB_SPEC;
      PARTIE_OP'ELAB_BODY;
      E196 := E196 + 1;
      E198 := E198 + 1;
      Pseudo_Code.Table'Elab_Spec;
      Pseudo_Code.Table'Elab_Body;
      E134 := E134 + 1;
      Pseudo_Code'Elab_Body;
      E130 := E130 + 1;
      MA_LEXICO'ELAB_BODY;
      E167 := E167 + 1;
   end adainit;

   procedure Ada_Main_Program;
   pragma Import (Ada, Ada_Main_Program, "_ada_ima");

   function main
     (argc : Integer;
      argv : System.Address;
      envp : System.Address)
      return Integer
   is
      procedure Initialize (Addr : System.Address);
      pragma Import (C, Initialize, "__gnat_initialize");

      procedure Finalize;
      pragma Import (C, Finalize, "__gnat_finalize");
      SEH : aliased array (1 .. 2) of Integer;

      Ensure_Reference : aliased System.Address := Ada_Main_Program_Name'Address;
      pragma Volatile (Ensure_Reference);

   begin
      gnat_argc := argc;
      gnat_argv := argv;
      gnat_envp := envp;

      Initialize (SEH'Address);
      adainit;
      Ada_Main_Program;
      adafinal;
      Finalize;
      return (gnat_exit_status);
   end;

--  BEGIN Object file/option list
   --   D:\Projet\Interp_MA\Obj\ma_lexico_dfa.o
   --   D:\Projet\Interp_MA\Obj\ma_lexico_io.o
   --   D:\Projet\Interp_MA\Obj\ma_syntax_goto.o
   --   D:\Projet\Interp_MA\Obj\ma_syntax_shift_reduce.o
   --   D:\Projet\Interp_MA\Obj\options.o
   --   D:\Projet\Interp_MA\Obj\types_base.o
   --   D:\Projet\Interp_MA\Obj\entier_es.o
   --   D:\Projet\Interp_MA\Obj\lecture_entiers.o
   --   D:\Projet\Interp_MA\Obj\mes_tables.o
   --   D:\Projet\Interp_MA\Obj\ma_detiq.o
   --   D:\Projet\Interp_MA\Obj\ma_syntax_tokens.o
   --   D:\Projet\Interp_MA\Obj\ma_syntax.o
   --   D:\Projet\Interp_MA\Obj\assembleur.o
   --   D:\Projet\Interp_MA\Obj\ma_token_io.o
   --   D:\Projet\Interp_MA\Obj\ma_dict.o
   --   D:\Projet\Interp_MA\Obj\ima.o
   --   D:\Projet\Interp_MA\Obj\reel_es.o
   --   D:\Projet\Interp_MA\Obj\partie_op.o
   --   D:\Projet\Interp_MA\Obj\lecture_reels.o
   --   D:\Projet\Interp_MA\Obj\pseudo_code-table.o
   --   D:\Projet\Interp_MA\Obj\pseudo_code.o
   --   D:\Projet\Interp_MA\Obj\ma_lexico.o
   --   -LD:\Projet\Interp_MA\Obj\
   --   -L../Obj\
   --   -LC:/MinGW/lib/gcc/mingw32/5.3.0/adalib/
   --   -static
   --   -lgnat
   --   -Wl,--stack=0x2000000
--  END Object file/option list   

end ada_main;
