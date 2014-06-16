This program asks for the user input for the reference filename and the file to rename. It will then check whether the file is Amber compatible or Rosetta Compatible, and print that mode out.

If the file is Amber compatible, it will produce Rosetta compatible file, and vice versa. The output file will be put in the same directory as the file to rename, with the suffix "..._Amber.pdb" for Rosetta input file, and the suffix "..._Rosetta.pdb" for Amber input file.

The Amber file used for testing was 2LWC_Model1.pdb, and the Rosetta file was 2LWCA_aa_RelaxedFrombb.pdb.