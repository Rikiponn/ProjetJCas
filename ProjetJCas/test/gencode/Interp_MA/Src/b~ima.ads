pragma Ada_95;
with System;
package ada_main is
   pragma Warnings (Off);

   gnat_argc : Integer;
   gnat_argv : System.Address;
   gnat_envp : System.Address;

   pragma Import (C, gnat_argc);
   pragma Import (C, gnat_argv);
   pragma Import (C, gnat_envp);

   gnat_exit_status : Integer;
   pragma Import (C, gnat_exit_status);

   GNAT_Version : constant String :=
                    "GNAT Version: 5.3.0" & ASCII.NUL;
   pragma Export (C, GNAT_Version, "__gnat_version");

   Ada_Main_Program_Name : constant String := "_ada_ima" & ASCII.NUL;
   pragma Export (C, Ada_Main_Program_Name, "__gnat_ada_main_program_name");

   procedure adainit;
   pragma Export (C, adainit, "adainit");

   procedure adafinal;
   pragma Export (C, adafinal, "adafinal");

   function main
     (argc : Integer;
      argv : System.Address;
      envp : System.Address)
      return Integer;
   pragma Export (C, main, "main");

   type Version_32 is mod 2 ** 32;
   u00001 : constant Version_32 := 16#d8ec1bbc#;
   pragma Export (C, u00001, "imaB");
   u00002 : constant Version_32 := 16#fbff4c67#;
   pragma Export (C, u00002, "system__standard_libraryB");
   u00003 : constant Version_32 := 16#f72f352b#;
   pragma Export (C, u00003, "system__standard_libraryS");
   u00004 : constant Version_32 := 16#3ffc8e18#;
   pragma Export (C, u00004, "adaS");
   u00005 : constant Version_32 := 16#72fd7b17#;
   pragma Export (C, u00005, "ada__command_lineB");
   u00006 : constant Version_32 := 16#d59e21a4#;
   pragma Export (C, u00006, "ada__command_lineS");
   u00007 : constant Version_32 := 16#f4ce8c3a#;
   pragma Export (C, u00007, "systemS");
   u00008 : constant Version_32 := 16#b19b6653#;
   pragma Export (C, u00008, "system__secondary_stackB");
   u00009 : constant Version_32 := 16#5faf4353#;
   pragma Export (C, u00009, "system__secondary_stackS");
   u00010 : constant Version_32 := 16#b01dad17#;
   pragma Export (C, u00010, "system__parametersB");
   u00011 : constant Version_32 := 16#8ae48145#;
   pragma Export (C, u00011, "system__parametersS");
   u00012 : constant Version_32 := 16#a207fefe#;
   pragma Export (C, u00012, "system__soft_linksB");
   u00013 : constant Version_32 := 16#af945ded#;
   pragma Export (C, u00013, "system__soft_linksS");
   u00014 : constant Version_32 := 16#b612ca65#;
   pragma Export (C, u00014, "ada__exceptionsB");
   u00015 : constant Version_32 := 16#1d190453#;
   pragma Export (C, u00015, "ada__exceptionsS");
   u00016 : constant Version_32 := 16#a46739c0#;
   pragma Export (C, u00016, "ada__exceptions__last_chance_handlerB");
   u00017 : constant Version_32 := 16#3aac8c92#;
   pragma Export (C, u00017, "ada__exceptions__last_chance_handlerS");
   u00018 : constant Version_32 := 16#393398c1#;
   pragma Export (C, u00018, "system__exception_tableB");
   u00019 : constant Version_32 := 16#5ad7ea2f#;
   pragma Export (C, u00019, "system__exception_tableS");
   u00020 : constant Version_32 := 16#ce4af020#;
   pragma Export (C, u00020, "system__exceptionsB");
   u00021 : constant Version_32 := 16#9cade1cc#;
   pragma Export (C, u00021, "system__exceptionsS");
   u00022 : constant Version_32 := 16#37d758f1#;
   pragma Export (C, u00022, "system__exceptions__machineS");
   u00023 : constant Version_32 := 16#b895431d#;
   pragma Export (C, u00023, "system__exceptions_debugB");
   u00024 : constant Version_32 := 16#472c9584#;
   pragma Export (C, u00024, "system__exceptions_debugS");
   u00025 : constant Version_32 := 16#570325c8#;
   pragma Export (C, u00025, "system__img_intB");
   u00026 : constant Version_32 := 16#f6156cf8#;
   pragma Export (C, u00026, "system__img_intS");
   u00027 : constant Version_32 := 16#39a03df9#;
   pragma Export (C, u00027, "system__storage_elementsB");
   u00028 : constant Version_32 := 16#d90dc63e#;
   pragma Export (C, u00028, "system__storage_elementsS");
   u00029 : constant Version_32 := 16#b98c3e16#;
   pragma Export (C, u00029, "system__tracebackB");
   u00030 : constant Version_32 := 16#6af355e1#;
   pragma Export (C, u00030, "system__tracebackS");
   u00031 : constant Version_32 := 16#9ed49525#;
   pragma Export (C, u00031, "system__traceback_entriesB");
   u00032 : constant Version_32 := 16#f4957a4a#;
   pragma Export (C, u00032, "system__traceback_entriesS");
   u00033 : constant Version_32 := 16#8c33a517#;
   pragma Export (C, u00033, "system__wch_conB");
   u00034 : constant Version_32 := 16#efb3aee8#;
   pragma Export (C, u00034, "system__wch_conS");
   u00035 : constant Version_32 := 16#9721e840#;
   pragma Export (C, u00035, "system__wch_stwB");
   u00036 : constant Version_32 := 16#c2a282e9#;
   pragma Export (C, u00036, "system__wch_stwS");
   u00037 : constant Version_32 := 16#92b797cb#;
   pragma Export (C, u00037, "system__wch_cnvB");
   u00038 : constant Version_32 := 16#e004141b#;
   pragma Export (C, u00038, "system__wch_cnvS");
   u00039 : constant Version_32 := 16#6033a23f#;
   pragma Export (C, u00039, "interfacesS");
   u00040 : constant Version_32 := 16#ece6fdb6#;
   pragma Export (C, u00040, "system__wch_jisB");
   u00041 : constant Version_32 := 16#60740d3a#;
   pragma Export (C, u00041, "system__wch_jisS");
   u00042 : constant Version_32 := 16#41837d1e#;
   pragma Export (C, u00042, "system__stack_checkingB");
   u00043 : constant Version_32 := 16#7a71e7d2#;
   pragma Export (C, u00043, "system__stack_checkingS");
   u00044 : constant Version_32 := 16#f64b89a4#;
   pragma Export (C, u00044, "ada__integer_text_ioB");
   u00045 : constant Version_32 := 16#f1daf268#;
   pragma Export (C, u00045, "ada__integer_text_ioS");
   u00046 : constant Version_32 := 16#28f088c2#;
   pragma Export (C, u00046, "ada__text_ioB");
   u00047 : constant Version_32 := 16#1a9b0017#;
   pragma Export (C, u00047, "ada__text_ioS");
   u00048 : constant Version_32 := 16#10558b11#;
   pragma Export (C, u00048, "ada__streamsB");
   u00049 : constant Version_32 := 16#2e6701ab#;
   pragma Export (C, u00049, "ada__streamsS");
   u00050 : constant Version_32 := 16#db5c917c#;
   pragma Export (C, u00050, "ada__io_exceptionsS");
   u00051 : constant Version_32 := 16#12c8cd7d#;
   pragma Export (C, u00051, "ada__tagsB");
   u00052 : constant Version_32 := 16#ce72c228#;
   pragma Export (C, u00052, "ada__tagsS");
   u00053 : constant Version_32 := 16#c3335bfd#;
   pragma Export (C, u00053, "system__htableB");
   u00054 : constant Version_32 := 16#700c3fd0#;
   pragma Export (C, u00054, "system__htableS");
   u00055 : constant Version_32 := 16#089f5cd0#;
   pragma Export (C, u00055, "system__string_hashB");
   u00056 : constant Version_32 := 16#d25254ae#;
   pragma Export (C, u00056, "system__string_hashS");
   u00057 : constant Version_32 := 16#699628fa#;
   pragma Export (C, u00057, "system__unsigned_typesS");
   u00058 : constant Version_32 := 16#b44f9ae7#;
   pragma Export (C, u00058, "system__val_unsB");
   u00059 : constant Version_32 := 16#793ec5c1#;
   pragma Export (C, u00059, "system__val_unsS");
   u00060 : constant Version_32 := 16#27b600b2#;
   pragma Export (C, u00060, "system__val_utilB");
   u00061 : constant Version_32 := 16#586e3ac4#;
   pragma Export (C, u00061, "system__val_utilS");
   u00062 : constant Version_32 := 16#d1060688#;
   pragma Export (C, u00062, "system__case_utilB");
   u00063 : constant Version_32 := 16#d0c7e5ed#;
   pragma Export (C, u00063, "system__case_utilS");
   u00064 : constant Version_32 := 16#84a27f0d#;
   pragma Export (C, u00064, "interfaces__c_streamsB");
   u00065 : constant Version_32 := 16#8bb5f2c0#;
   pragma Export (C, u00065, "interfaces__c_streamsS");
   u00066 : constant Version_32 := 16#845f5a34#;
   pragma Export (C, u00066, "system__crtlS");
   u00067 : constant Version_32 := 16#431faf3c#;
   pragma Export (C, u00067, "system__file_ioB");
   u00068 : constant Version_32 := 16#53bf6d5f#;
   pragma Export (C, u00068, "system__file_ioS");
   u00069 : constant Version_32 := 16#b7ab275c#;
   pragma Export (C, u00069, "ada__finalizationB");
   u00070 : constant Version_32 := 16#19f764ca#;
   pragma Export (C, u00070, "ada__finalizationS");
   u00071 : constant Version_32 := 16#95817ed8#;
   pragma Export (C, u00071, "system__finalization_rootB");
   u00072 : constant Version_32 := 16#bb3cffaa#;
   pragma Export (C, u00072, "system__finalization_rootS");
   u00073 : constant Version_32 := 16#769e25e6#;
   pragma Export (C, u00073, "interfaces__cB");
   u00074 : constant Version_32 := 16#4a38bedb#;
   pragma Export (C, u00074, "interfaces__cS");
   u00075 : constant Version_32 := 16#ee0f26dd#;
   pragma Export (C, u00075, "system__os_libB");
   u00076 : constant Version_32 := 16#d7b69782#;
   pragma Export (C, u00076, "system__os_libS");
   u00077 : constant Version_32 := 16#1a817b8e#;
   pragma Export (C, u00077, "system__stringsB");
   u00078 : constant Version_32 := 16#8a719d5c#;
   pragma Export (C, u00078, "system__stringsS");
   u00079 : constant Version_32 := 16#09511692#;
   pragma Export (C, u00079, "system__file_control_blockS");
   u00080 : constant Version_32 := 16#f6fdca1c#;
   pragma Export (C, u00080, "ada__text_io__integer_auxB");
   u00081 : constant Version_32 := 16#b9793d30#;
   pragma Export (C, u00081, "ada__text_io__integer_auxS");
   u00082 : constant Version_32 := 16#181dc502#;
   pragma Export (C, u00082, "ada__text_io__generic_auxB");
   u00083 : constant Version_32 := 16#a6c327d3#;
   pragma Export (C, u00083, "ada__text_io__generic_auxS");
   u00084 : constant Version_32 := 16#18d57884#;
   pragma Export (C, u00084, "system__img_biuB");
   u00085 : constant Version_32 := 16#afb4a0b7#;
   pragma Export (C, u00085, "system__img_biuS");
   u00086 : constant Version_32 := 16#e7d8734f#;
   pragma Export (C, u00086, "system__img_llbB");
   u00087 : constant Version_32 := 16#ee73b049#;
   pragma Export (C, u00087, "system__img_llbS");
   u00088 : constant Version_32 := 16#9777733a#;
   pragma Export (C, u00088, "system__img_lliB");
   u00089 : constant Version_32 := 16#e581d9eb#;
   pragma Export (C, u00089, "system__img_lliS");
   u00090 : constant Version_32 := 16#0e8808d4#;
   pragma Export (C, u00090, "system__img_llwB");
   u00091 : constant Version_32 := 16#471f93df#;
   pragma Export (C, u00091, "system__img_llwS");
   u00092 : constant Version_32 := 16#428b07f8#;
   pragma Export (C, u00092, "system__img_wiuB");
   u00093 : constant Version_32 := 16#c1f52725#;
   pragma Export (C, u00093, "system__img_wiuS");
   u00094 : constant Version_32 := 16#7ebd8839#;
   pragma Export (C, u00094, "system__val_intB");
   u00095 : constant Version_32 := 16#bc6ba605#;
   pragma Export (C, u00095, "system__val_intS");
   u00096 : constant Version_32 := 16#b3aa7b17#;
   pragma Export (C, u00096, "system__val_lliB");
   u00097 : constant Version_32 := 16#6eea6a9a#;
   pragma Export (C, u00097, "system__val_lliS");
   u00098 : constant Version_32 := 16#06052bd0#;
   pragma Export (C, u00098, "system__val_lluB");
   u00099 : constant Version_32 := 16#13647f88#;
   pragma Export (C, u00099, "system__val_lluS");
   u00100 : constant Version_32 := 16#b59b63c1#;
   pragma Export (C, u00100, "assembleurB");
   u00101 : constant Version_32 := 16#79c41007#;
   pragma Export (C, u00101, "assembleurS");
   u00102 : constant Version_32 := 16#bd463446#;
   pragma Export (C, u00102, "ma_detiqB");
   u00103 : constant Version_32 := 16#11d3f81e#;
   pragma Export (C, u00103, "ma_detiqS");
   u00104 : constant Version_32 := 16#c50dcf1a#;
   pragma Export (C, u00104, "mes_tablesB");
   u00105 : constant Version_32 := 16#e67d62e6#;
   pragma Export (C, u00105, "mes_tablesS");
   u00106 : constant Version_32 := 16#7dbbd31d#;
   pragma Export (C, u00106, "text_ioS");
   u00107 : constant Version_32 := 16#1c33b50d#;
   pragma Export (C, u00107, "types_baseB");
   u00108 : constant Version_32 := 16#447091ce#;
   pragma Export (C, u00108, "types_baseS");
   u00109 : constant Version_32 := 16#5b9edcc4#;
   pragma Export (C, u00109, "system__compare_array_unsigned_8B");
   u00110 : constant Version_32 := 16#5dcdfdb7#;
   pragma Export (C, u00110, "system__compare_array_unsigned_8S");
   u00111 : constant Version_32 := 16#5f72f755#;
   pragma Export (C, u00111, "system__address_operationsB");
   u00112 : constant Version_32 := 16#e7c23209#;
   pragma Export (C, u00112, "system__address_operationsS");
   u00113 : constant Version_32 := 16#fd83e873#;
   pragma Export (C, u00113, "system__concat_2B");
   u00114 : constant Version_32 := 16#f66e5bea#;
   pragma Export (C, u00114, "system__concat_2S");
   u00115 : constant Version_32 := 16#b5b2aca1#;
   pragma Export (C, u00115, "system__finalization_mastersB");
   u00116 : constant Version_32 := 16#80d8a57a#;
   pragma Export (C, u00116, "system__finalization_mastersS");
   u00117 : constant Version_32 := 16#57a37a42#;
   pragma Export (C, u00117, "system__address_imageB");
   u00118 : constant Version_32 := 16#55221100#;
   pragma Export (C, u00118, "system__address_imageS");
   u00119 : constant Version_32 := 16#7268f812#;
   pragma Export (C, u00119, "system__img_boolB");
   u00120 : constant Version_32 := 16#0117fdd1#;
   pragma Export (C, u00120, "system__img_boolS");
   u00121 : constant Version_32 := 16#d7aac20c#;
   pragma Export (C, u00121, "system__ioB");
   u00122 : constant Version_32 := 16#6a8c7b75#;
   pragma Export (C, u00122, "system__ioS");
   u00123 : constant Version_32 := 16#6d4d969a#;
   pragma Export (C, u00123, "system__storage_poolsB");
   u00124 : constant Version_32 := 16#01950bbe#;
   pragma Export (C, u00124, "system__storage_poolsS");
   u00125 : constant Version_32 := 16#e34550ca#;
   pragma Export (C, u00125, "system__pool_globalB");
   u00126 : constant Version_32 := 16#c88d2d16#;
   pragma Export (C, u00126, "system__pool_globalS");
   u00127 : constant Version_32 := 16#2bce1226#;
   pragma Export (C, u00127, "system__memoryB");
   u00128 : constant Version_32 := 16#adb3ea0e#;
   pragma Export (C, u00128, "system__memoryS");
   u00129 : constant Version_32 := 16#956644e6#;
   pragma Export (C, u00129, "pseudo_codeB");
   u00130 : constant Version_32 := 16#6837ff4f#;
   pragma Export (C, u00130, "pseudo_codeS");
   u00131 : constant Version_32 := 16#1384aef2#;
   pragma Export (C, u00131, "entier_esB");
   u00132 : constant Version_32 := 16#6f99cae8#;
   pragma Export (C, u00132, "entier_esS");
   u00133 : constant Version_32 := 16#5182b687#;
   pragma Export (C, u00133, "pseudo_code__tableB");
   u00134 : constant Version_32 := 16#6cd79f5c#;
   pragma Export (C, u00134, "pseudo_code__tableS");
   u00135 : constant Version_32 := 16#12c24a43#;
   pragma Export (C, u00135, "ada__charactersS");
   u00136 : constant Version_32 := 16#8f637df8#;
   pragma Export (C, u00136, "ada__characters__handlingB");
   u00137 : constant Version_32 := 16#3b3f6154#;
   pragma Export (C, u00137, "ada__characters__handlingS");
   u00138 : constant Version_32 := 16#4b7bb96a#;
   pragma Export (C, u00138, "ada__characters__latin_1S");
   u00139 : constant Version_32 := 16#af50e98f#;
   pragma Export (C, u00139, "ada__stringsS");
   u00140 : constant Version_32 := 16#e2ea8656#;
   pragma Export (C, u00140, "ada__strings__mapsB");
   u00141 : constant Version_32 := 16#1e526bec#;
   pragma Export (C, u00141, "ada__strings__mapsS");
   u00142 : constant Version_32 := 16#41937159#;
   pragma Export (C, u00142, "system__bit_opsB");
   u00143 : constant Version_32 := 16#0765e3a3#;
   pragma Export (C, u00143, "system__bit_opsS");
   u00144 : constant Version_32 := 16#92f05f13#;
   pragma Export (C, u00144, "ada__strings__maps__constantsS");
   u00145 : constant Version_32 := 16#d0432c8d#;
   pragma Export (C, u00145, "system__img_enum_newB");
   u00146 : constant Version_32 := 16#95828afa#;
   pragma Export (C, u00146, "system__img_enum_newS");
   u00147 : constant Version_32 := 16#c7fe82f1#;
   pragma Export (C, u00147, "reel_esB");
   u00148 : constant Version_32 := 16#bbe3e6eb#;
   pragma Export (C, u00148, "reel_esS");
   u00149 : constant Version_32 := 16#d5f9759f#;
   pragma Export (C, u00149, "ada__text_io__float_auxB");
   u00150 : constant Version_32 := 16#f854caf5#;
   pragma Export (C, u00150, "ada__text_io__float_auxS");
   u00151 : constant Version_32 := 16#f0df9003#;
   pragma Export (C, u00151, "system__img_realB");
   u00152 : constant Version_32 := 16#3366ddd8#;
   pragma Export (C, u00152, "system__img_realS");
   u00153 : constant Version_32 := 16#f05937c9#;
   pragma Export (C, u00153, "system__fat_llfS");
   u00154 : constant Version_32 := 16#1b28662b#;
   pragma Export (C, u00154, "system__float_controlB");
   u00155 : constant Version_32 := 16#1432cf06#;
   pragma Export (C, u00155, "system__float_controlS");
   u00156 : constant Version_32 := 16#f1f88835#;
   pragma Export (C, u00156, "system__img_lluB");
   u00157 : constant Version_32 := 16#205f2839#;
   pragma Export (C, u00157, "system__img_lluS");
   u00158 : constant Version_32 := 16#eef535cd#;
   pragma Export (C, u00158, "system__img_unsB");
   u00159 : constant Version_32 := 16#f662140d#;
   pragma Export (C, u00159, "system__img_unsS");
   u00160 : constant Version_32 := 16#a4beea4d#;
   pragma Export (C, u00160, "system__powten_tableS");
   u00161 : constant Version_32 := 16#faa9a7b2#;
   pragma Export (C, u00161, "system__val_realB");
   u00162 : constant Version_32 := 16#0ae7fb2b#;
   pragma Export (C, u00162, "system__val_realS");
   u00163 : constant Version_32 := 16#0be1b996#;
   pragma Export (C, u00163, "system__exn_llfB");
   u00164 : constant Version_32 := 16#754a92d5#;
   pragma Export (C, u00164, "system__exn_llfS");
   u00165 : constant Version_32 := 16#8a899923#;
   pragma Export (C, u00165, "system__fat_lfltS");
   u00166 : constant Version_32 := 16#48f9793a#;
   pragma Export (C, u00166, "ma_lexicoB");
   u00167 : constant Version_32 := 16#a39e02a9#;
   pragma Export (C, u00167, "ma_lexicoS");
   u00168 : constant Version_32 := 16#8ec51ba6#;
   pragma Export (C, u00168, "ma_dictB");
   u00169 : constant Version_32 := 16#b0f120d1#;
   pragma Export (C, u00169, "ma_dictS");
   u00170 : constant Version_32 := 16#6bc02dbb#;
   pragma Export (C, u00170, "ma_token_ioB");
   u00171 : constant Version_32 := 16#ad4475e8#;
   pragma Export (C, u00171, "ma_token_ioS");
   u00172 : constant Version_32 := 16#f08789ae#;
   pragma Export (C, u00172, "ada__text_io__enumeration_auxB");
   u00173 : constant Version_32 := 16#52f1e0af#;
   pragma Export (C, u00173, "ada__text_io__enumeration_auxS");
   u00174 : constant Version_32 := 16#eb6581d5#;
   pragma Export (C, u00174, "ma_syntax_tokensS");
   u00175 : constant Version_32 := 16#4b37b589#;
   pragma Export (C, u00175, "system__val_enumB");
   u00176 : constant Version_32 := 16#4fd4ceaf#;
   pragma Export (C, u00176, "system__val_enumS");
   u00177 : constant Version_32 := 16#afdd9afa#;
   pragma Export (C, u00177, "ma_lexico_dfaB");
   u00178 : constant Version_32 := 16#a47f2bc3#;
   pragma Export (C, u00178, "ma_lexico_dfaS");
   u00179 : constant Version_32 := 16#ce8c6475#;
   pragma Export (C, u00179, "ma_lexico_ioB");
   u00180 : constant Version_32 := 16#e7886c03#;
   pragma Export (C, u00180, "ma_lexico_ioS");
   u00181 : constant Version_32 := 16#2b70b149#;
   pragma Export (C, u00181, "system__concat_3B");
   u00182 : constant Version_32 := 16#ffbed09f#;
   pragma Export (C, u00182, "system__concat_3S");
   u00183 : constant Version_32 := 16#819541f5#;
   pragma Export (C, u00183, "ma_syntaxB");
   u00184 : constant Version_32 := 16#c2446f96#;
   pragma Export (C, u00184, "ma_syntaxS");
   u00185 : constant Version_32 := 16#aca433ca#;
   pragma Export (C, u00185, "ma_syntax_gotoS");
   u00186 : constant Version_32 := 16#7ffda9a3#;
   pragma Export (C, u00186, "ma_syntax_shift_reduceS");
   u00187 : constant Version_32 := 16#932a4690#;
   pragma Export (C, u00187, "system__concat_4B");
   u00188 : constant Version_32 := 16#8aaaa71a#;
   pragma Export (C, u00188, "system__concat_4S");
   u00189 : constant Version_32 := 16#608e2cd1#;
   pragma Export (C, u00189, "system__concat_5B");
   u00190 : constant Version_32 := 16#7390cf14#;
   pragma Export (C, u00190, "system__concat_5S");
   u00191 : constant Version_32 := 16#fceb5070#;
   pragma Export (C, u00191, "lecture_entiersB");
   u00192 : constant Version_32 := 16#b02104db#;
   pragma Export (C, u00192, "lecture_entiersS");
   u00193 : constant Version_32 := 16#a3b0e77f#;
   pragma Export (C, u00193, "optionsB");
   u00194 : constant Version_32 := 16#d5b38fdb#;
   pragma Export (C, u00194, "optionsS");
   u00195 : constant Version_32 := 16#56eac0fd#;
   pragma Export (C, u00195, "partie_opB");
   u00196 : constant Version_32 := 16#5829a7d7#;
   pragma Export (C, u00196, "partie_opS");
   u00197 : constant Version_32 := 16#c86e2b51#;
   pragma Export (C, u00197, "lecture_reelsB");
   u00198 : constant Version_32 := 16#fc31a9f6#;
   pragma Export (C, u00198, "lecture_reelsS");
   --  BEGIN ELABORATION ORDER
   --  ada%s
   --  ada.characters%s
   --  ada.characters.handling%s
   --  ada.characters.latin_1%s
   --  ada.command_line%s
   --  interfaces%s
   --  system%s
   --  system.address_operations%s
   --  system.address_operations%b
   --  system.case_util%s
   --  system.case_util%b
   --  system.exn_llf%s
   --  system.exn_llf%b
   --  system.float_control%s
   --  system.float_control%b
   --  system.htable%s
   --  system.img_bool%s
   --  system.img_bool%b
   --  system.img_enum_new%s
   --  system.img_enum_new%b
   --  system.img_int%s
   --  system.img_int%b
   --  system.img_lli%s
   --  system.img_lli%b
   --  system.img_real%s
   --  system.io%s
   --  system.io%b
   --  system.parameters%s
   --  system.parameters%b
   --  system.crtl%s
   --  interfaces.c_streams%s
   --  interfaces.c_streams%b
   --  system.powten_table%s
   --  system.standard_library%s
   --  system.exceptions_debug%s
   --  system.exceptions_debug%b
   --  system.storage_elements%s
   --  system.storage_elements%b
   --  system.stack_checking%s
   --  system.stack_checking%b
   --  system.string_hash%s
   --  system.string_hash%b
   --  system.htable%b
   --  system.strings%s
   --  system.strings%b
   --  system.os_lib%s
   --  system.traceback_entries%s
   --  system.traceback_entries%b
   --  ada.exceptions%s
   --  system.soft_links%s
   --  system.unsigned_types%s
   --  system.fat_lflt%s
   --  system.fat_llf%s
   --  system.img_biu%s
   --  system.img_biu%b
   --  system.img_llb%s
   --  system.img_llb%b
   --  system.img_llu%s
   --  system.img_llu%b
   --  system.img_llw%s
   --  system.img_llw%b
   --  system.img_uns%s
   --  system.img_uns%b
   --  system.img_real%b
   --  system.img_wiu%s
   --  system.img_wiu%b
   --  system.val_enum%s
   --  system.val_int%s
   --  system.val_lli%s
   --  system.val_llu%s
   --  system.val_real%s
   --  system.val_uns%s
   --  system.val_util%s
   --  system.val_util%b
   --  system.val_uns%b
   --  system.val_real%b
   --  system.val_llu%b
   --  system.val_lli%b
   --  system.val_int%b
   --  system.val_enum%b
   --  system.wch_con%s
   --  system.wch_con%b
   --  system.wch_cnv%s
   --  system.wch_jis%s
   --  system.wch_jis%b
   --  system.wch_cnv%b
   --  system.wch_stw%s
   --  system.wch_stw%b
   --  ada.exceptions.last_chance_handler%s
   --  ada.exceptions.last_chance_handler%b
   --  system.address_image%s
   --  system.bit_ops%s
   --  system.bit_ops%b
   --  system.compare_array_unsigned_8%s
   --  system.compare_array_unsigned_8%b
   --  system.concat_2%s
   --  system.concat_2%b
   --  system.concat_3%s
   --  system.concat_3%b
   --  system.concat_4%s
   --  system.concat_4%b
   --  system.concat_5%s
   --  system.concat_5%b
   --  system.exception_table%s
   --  system.exception_table%b
   --  ada.io_exceptions%s
   --  ada.strings%s
   --  ada.strings.maps%s
   --  ada.strings.maps.constants%s
   --  ada.tags%s
   --  ada.streams%s
   --  ada.streams%b
   --  interfaces.c%s
   --  system.exceptions%s
   --  system.exceptions%b
   --  system.exceptions.machine%s
   --  system.file_control_block%s
   --  system.file_io%s
   --  system.finalization_root%s
   --  system.finalization_root%b
   --  ada.finalization%s
   --  ada.finalization%b
   --  system.storage_pools%s
   --  system.storage_pools%b
   --  system.finalization_masters%s
   --  system.memory%s
   --  system.memory%b
   --  system.standard_library%b
   --  system.pool_global%s
   --  system.pool_global%b
   --  system.secondary_stack%s
   --  system.finalization_masters%b
   --  system.file_io%b
   --  interfaces.c%b
   --  ada.tags%b
   --  ada.strings.maps%b
   --  system.soft_links%b
   --  system.os_lib%b
   --  ada.command_line%b
   --  ada.characters.handling%b
   --  system.secondary_stack%b
   --  system.address_image%b
   --  system.traceback%s
   --  ada.exceptions%b
   --  system.traceback%b
   --  ada.text_io%s
   --  ada.text_io%b
   --  ada.text_io.enumeration_aux%s
   --  ada.text_io.float_aux%s
   --  ada.text_io.generic_aux%s
   --  ada.text_io.generic_aux%b
   --  ada.text_io.float_aux%b
   --  ada.text_io.enumeration_aux%b
   --  ada.text_io.integer_aux%s
   --  ada.text_io.integer_aux%b
   --  ada.integer_text_io%s
   --  ada.integer_text_io%b
   --  text_io%s
   --  ma_lexico_dfa%s
   --  ma_lexico_dfa%b
   --  ma_lexico_io%s
   --  ma_lexico_io%b
   --  ma_syntax_goto%s
   --  ma_syntax_shift_reduce%s
   --  options%s
   --  options%b
   --  types_base%s
   --  types_base%b
   --  entier_es%s
   --  entier_es%b
   --  lecture_entiers%s
   --  lecture_entiers%b
   --  lecture_reels%s
   --  mes_tables%s
   --  mes_tables%b
   --  pseudo_code%s
   --  assembleur%s
   --  ma_detiq%s
   --  ma_detiq%b
   --  ma_syntax_tokens%s
   --  ma_dict%s
   --  ma_lexico%s
   --  ma_syntax%s
   --  ma_syntax%b
   --  assembleur%b
   --  ma_token_io%s
   --  ma_token_io%b
   --  ma_dict%b
   --  partie_op%s
   --  ima%b
   --  reel_es%s
   --  reel_es%b
   --  partie_op%b
   --  lecture_reels%b
   --  pseudo_code.table%s
   --  pseudo_code.table%b
   --  pseudo_code%b
   --  ma_lexico%b
   --  END ELABORATION ORDER


end ada_main;
