pragma Ada_95;
pragma Source_File_Name (ada_main, Spec_File_Name => "b~test_pseudo_code.ads");
pragma Source_File_Name (ada_main, Body_File_Name => "b~test_pseudo_code.adb");
pragma Suppress (Overflow_Check);
with Ada.Exceptions;

package body ada_main is
   pragma Warnings (Off);

   E072 : Short_Integer; pragma Import (Ada, E072, "system__os_lib_E");
   E013 : Short_Integer; pragma Import (Ada, E013, "system__soft_links_E");
   E154 : Short_Integer; pragma Import (Ada, E154, "system__fat_lflt_E");
   E142 : Short_Integer; pragma Import (Ada, E142, "system__fat_llf_E");
   E023 : Short_Integer; pragma Import (Ada, E023, "system__exception_table_E");
   E046 : Short_Integer; pragma Import (Ada, E046, "ada__io_exceptions_E");
   E108 : Short_Integer; pragma Import (Ada, E108, "ada__strings_E");
   E110 : Short_Integer; pragma Import (Ada, E110, "ada__strings__maps_E");
   E113 : Short_Integer; pragma Import (Ada, E113, "ada__strings__maps__constants_E");
   E048 : Short_Integer; pragma Import (Ada, E048, "ada__tags_E");
   E045 : Short_Integer; pragma Import (Ada, E045, "ada__streams_E");
   E070 : Short_Integer; pragma Import (Ada, E070, "interfaces__c_E");
   E025 : Short_Integer; pragma Import (Ada, E025, "system__exceptions_E");
   E075 : Short_Integer; pragma Import (Ada, E075, "system__file_control_block_E");
   E064 : Short_Integer; pragma Import (Ada, E064, "system__file_io_E");
   E068 : Short_Integer; pragma Import (Ada, E068, "system__finalization_root_E");
   E066 : Short_Integer; pragma Import (Ada, E066, "ada__finalization_E");
   E131 : Short_Integer; pragma Import (Ada, E131, "system__storage_pools_E");
   E123 : Short_Integer; pragma Import (Ada, E123, "system__finalization_masters_E");
   E133 : Short_Integer; pragma Import (Ada, E133, "system__pool_global_E");
   E017 : Short_Integer; pragma Import (Ada, E017, "system__secondary_stack_E");
   E006 : Short_Integer; pragma Import (Ada, E006, "ada__text_io_E");
   E101 : Short_Integer; pragma Import (Ada, E101, "types_base_E");
   E077 : Short_Integer; pragma Import (Ada, E077, "pseudo_code_E");
   E103 : Short_Integer; pragma Import (Ada, E103, "pseudo_code__table_E");

   Local_Priority_Specific_Dispatching : constant String := "";
   Local_Interrupt_States : constant String := "";

   Is_Elaborated : Boolean := False;

   procedure finalize_library is
   begin
      E006 := E006 - 1;
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
         E064 := E064 - 1;
         F2;
      end;
      E123 := E123 - 1;
      E133 := E133 - 1;
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
      E154 := E154 + 1;
      System.Fat_Llf'Elab_Spec;
      E142 := E142 + 1;
      System.Exception_Table'Elab_Body;
      E023 := E023 + 1;
      Ada.Io_Exceptions'Elab_Spec;
      E046 := E046 + 1;
      Ada.Strings'Elab_Spec;
      E108 := E108 + 1;
      Ada.Strings.Maps'Elab_Spec;
      Ada.Strings.Maps.Constants'Elab_Spec;
      E113 := E113 + 1;
      Ada.Tags'Elab_Spec;
      Ada.Streams'Elab_Spec;
      E045 := E045 + 1;
      Interfaces.C'Elab_Spec;
      System.Exceptions'Elab_Spec;
      E025 := E025 + 1;
      System.File_Control_Block'Elab_Spec;
      E075 := E075 + 1;
      System.Finalization_Root'Elab_Spec;
      E068 := E068 + 1;
      Ada.Finalization'Elab_Spec;
      E066 := E066 + 1;
      System.Storage_Pools'Elab_Spec;
      E131 := E131 + 1;
      System.Finalization_Masters'Elab_Spec;
      System.Pool_Global'Elab_Spec;
      E133 := E133 + 1;
      System.Finalization_Masters'Elab_Body;
      E123 := E123 + 1;
      System.File_Io'Elab_Body;
      E064 := E064 + 1;
      E070 := E070 + 1;
      Ada.Tags'Elab_Body;
      E048 := E048 + 1;
      E110 := E110 + 1;
      System.Soft_Links'Elab_Body;
      E013 := E013 + 1;
      System.Os_Lib'Elab_Body;
      E072 := E072 + 1;
      System.Secondary_Stack'Elab_Body;
      E017 := E017 + 1;
      Ada.Text_Io'Elab_Spec;
      Ada.Text_Io'Elab_Body;
      E006 := E006 + 1;
      E101 := E101 + 1;
      Pseudo_Code'Elab_Spec;
      Pseudo_Code.Table'Elab_Spec;
      Pseudo_Code.Table'Elab_Body;
      E103 := E103 + 1;
      Pseudo_Code'Elab_Body;
      E077 := E077 + 1;
   end adainit;

   procedure Ada_Main_Program;
   pragma Import (Ada, Ada_Main_Program, "_ada_test_pseudo_code");

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
   --   D:\Projet\Interp_MA\Obj\types_base.o
   --   D:\Projet\Interp_MA\Obj\entier_es.o
   --   D:\Projet\Interp_MA\Obj\test_pseudo_code.o
   --   D:\Projet\Interp_MA\Obj\reel_es.o
   --   D:\Projet\Interp_MA\Obj\pseudo_code-table.o
   --   D:\Projet\Interp_MA\Obj\pseudo_code.o
   --   -LD:\Projet\Interp_MA\Obj\
   --   -L../Obj\
   --   -LC:/MinGW/lib/gcc/mingw32/5.3.0/adalib/
   --   -static
   --   -lgnat
   --   -Wl,--stack=0x2000000
--  END Object file/option list   

end ada_main;
