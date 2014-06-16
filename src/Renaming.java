import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

/**
 * This program asks for the user input for the reference filename and the file to rename.
 * It will check whether the file is Amber compatible or Rosetta Compatible, and print that mode out.
 * If the file is Amber compatible, it will produce Rosetta compatible file, and vice versa.
 * The output file will be put in the same directory as the file to rename, 
 * with the suffix "..._Amber.pdb" for Rosetta input file, 
 * and the suffix "..._Rosetta.pdb" for Amber input file.
 * 
 * Rename the atoms.
 * Amber to Rosetta
 * TYR: H1, H2, H3 -> 1H, 2H, 3H 			move one character left 
 * 		HB2, HB3 -> 1HB, 2HB 				move one character left
 * 
 * GLY: HA2, HA3 -> 1HA, 2HA 				move one character left
 * 		If atoms are found second time, 	residue sequence number = 3
 * 
 * PHE: HB2, HB3 -> 1HB, 2HB 				move one character left
 * 
 * MET: HB2, HB3 -> 1HB, 2HB 				move one character left
 * 		HG2, HB3 -> 1HG, 2HG 				move one character left
 * 		HE1, HE2, HE3 -> 1HE, 2HE, 3HE 		move one character left
 * 
 * @author Wint Yee Hnin
 * @version 8th June, 2014
 * 
 */

//Testing: 	Rosetta file: C:\Users\Win\Desktop\PDB files\2LWCA_aa_RelaxedFrombb.pdb
// 			Amber file: C:\Users\Win\Desktop\PDB files\2LWC_Model1.pdb
public class Renaming {

	static PrintWriter output;
	static String residue_name, atom_name;
	static int residue_number;

	/**
	 * This method will first check if the atom names are the same.
	 * If not, it will find the same atom, and fix the name as the reference file.
	 * 
	 * @param reader read the input file to rename the atoms 
	 * @param ref_filename the reference filename
	 * @param mode Amber or Rosetta mode
	 * @throws IOException
	 */

	public static void rename(BufferedReader reader, String ref_filename,
			String mode) throws IOException {

		while (reader.ready()) {
			String input = reader.readLine();

			if (input.startsWith("ATOM")) {
				residue_name = input.substring(17, 20);
				atom_name = input.substring(12, 16);
				residue_number = Integer.parseInt(input.substring(22, 26).replaceAll("\\s", ""));
				char[] atom = atom_name.toCharArray();

				BufferedReader reader_ref = new BufferedReader(new FileReader(ref_filename));

				while (reader_ref.ready()) {
					String line_ref = reader_ref.readLine();

					if (line_ref.startsWith("ATOM")) {
						String atom_name_ref = line_ref.substring(12, 16);
						String residue_ref = line_ref.substring(17, 20);

						if (residue_name.equals(residue_ref)) {
							if (atom_name.equals(atom_name_ref)) {
								break;
							} else {
								char[] atom_ref = atom_name_ref.toCharArray();
								Arrays.sort(atom);
								Arrays.sort(atom_ref);

								if ((atom[2] == atom_ref[2]) && (atom[3] == atom_ref[3])) {
									if (atom[1] == ' ') {
										atom_name = atom_name_ref;
										break;
									} else {
										int number = Integer.parseInt(String.valueOf(atom[1]));
										if ((atom[2] == 'E')) {
											if (String.valueOf(atom_ref[1]).equals(String.valueOf(number))) {
												atom_name = atom_name_ref;
												break;
											}
										}

											else {
												if ((mode == "AMBER")
														&& (String.valueOf(atom_ref[1])
																.equals(String.valueOf(number - 1)))) {
													atom_name = atom_name_ref;
													break;
												} else if ((mode == "ROSETTA")
														&& (String.valueOf(atom_ref[1])
																.equals(String.valueOf(number + 1)))) {
													atom_name = atom_name_ref;
													break;
												}
											}
									}
								}
							}
						}
					}
				}
				String begin = input.substring(0, 12) + atom_name + input.substring(16, 22);
				output.printf("%s %3s %s %n", begin, residue_number, input.substring(27));
			}

			else {
				output.println(input.trim());
			}
		}
	}

	/**
	 * This method checks the file whether it is in Amber or Rosetta mode.
	 * If it finds H1, it is in Amber mode, and if it finds 1H, it is in Rosetta mode.
	 * 
	 * @param inputfile
	 * @return String mode
	 * @throws IOException
	 */
	public static String mode(BufferedReader inputfile) throws IOException {
		String mode = null;

		while (inputfile.ready()) {
			String line = inputfile.readLine();
			if (line.startsWith("ATOM")) {
				String atom_name = line.substring(12, 16)
						.replaceAll("\\s*", "");

				if (atom_name.equals("H1")) {
					mode = "AMBER";
					break;
				} else if (atom_name.equals("1H")) {
					mode = "ROSETTA";
					break;
				}
			}
		}
		inputfile.close();
		System.out.println("This file is in " + mode + " mode.");
		return mode;
	}

	/**
	 * main method
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Scanner s = new Scanner(System.in);
		System.out.print("Enter the name to rename atoms: ");
		String input_file = s.nextLine();
		System.out.print("Enter the reference file: ");
		String ref_file = s.nextLine();
		
//		for getting all files in the directory and converting them
//		File folder = new File(input_file);
//		File[] files =  folder.listFiles();
		
//		for (int i = 0; i < files.length; i++) {
//			String filename = files[i].getName();
			BufferedReader reader = new BufferedReader(new FileReader(input_file));
				
			String mode = mode(reader);

			if (mode.equals("AMBER")) {
				reader = new BufferedReader(new FileReader(input_file));
				output = new PrintWriter(input_file.substring(0, input_file.length() - 4) + "_RosettaNaming.pdb");
			}

			else if (mode.equals("ROSETTA")) {
				reader = new BufferedReader(new FileReader(input_file));
				output = new PrintWriter(input_file.substring(0, input_file.length() - 4) + "_AmberNaming.pdb");
			}
			rename(reader, ref_file, mode);

			reader.close();
			output.close();
			s.close();
	}
}
