import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * This project will read two files, one reference file and another file 
 * that needs to correct the atoms' order. The atoms' order in the second file
 * will be corrected based on the order in the reference file so that it makes
 * easier in comparing the coordinates.
 * 
 * The output files are put in the same directory with filenames as
 * 2LWC_Model1_aa.pdb ... 2LWC_Model20_aa.pdb
 * 1PLW_Model1_aa.pdb  ... 1PLW_Model80_aa.pdb
 * 1PLX_Model1_aa.pdb   ... 1PLX_Model80_aa.pdb
 *
 * @author Wint Yee Hnin
 * @version 4th June, 2014
 * 
 */

//Testing: 	the to_correct file : C:\Users\Win\Desktop\PDB files\Merged_1PLW+1PLX+2LWC.pdb
//			the reference file: C:\Users\Win\Desktop\PDB files\2LWC_Model1.pdb 
public class AtomOrder {

	static BufferedReader read_ref;
	static BufferedReader read_correct;
	static BufferedReader read_fix;
	static PrintWriter output;
	static int printed = 0;
	static int already_printed;

	/**
	 * This method will get line by line from the reference file and compare it
	 * to the file to be corrected. It will, then write the line of the atom
	 * that matches to the line from reference in the output file.
	 * 
	 * As there are two GLY amino acids, there are two sets of same atoms. To solve this, the method will create
	 * a new int variable that will track the number of atoms from first GLY which has been
	 * printed. So, for GLY residue name, this method will check if all atoms with residue sequence
	 * number 2 are printed.If all has not printed, it will get the matched line in first GLY. Otherwise,
	 * instead of getting the line that has same atom and amino acid, it will find another matched line
	 * with the residue sequence number 3. 
	 * 
	 * @param ref
	 *            current line from the reference file to be compared
	 * @param correct_file
	 *            the file to be corrected
	 * 
	 */
	public static void compare(String ref, BufferedReader correct_file)
			throws IOException {
		int times = 0;
		
		while (correct_file.ready()) {
			String in_correct = correct_file.readLine();
			
				if (in_correct.contains("MODEL")) {
					times++;
				}
				
				if (times != printed) {
					continue;
				}
				else {
					in_correct = correct_file.readLine();
					while (!(in_correct.contains("TER"))) {
						int residue_sequence = Integer.parseInt(in_correct.substring(22, 26).replaceAll("\\s++", ""));
						
						if (in_correct.substring(17, 20).equals("GLY")) {
							if (((residue_sequence == 2) && (already_printed < 7)) || residue_sequence == 3){
								if (correct(ref, in_correct) == true) {
									if (residue_sequence == 2){
										already_printed++;
									}
									break;
								}
								else {
									in_correct = correct_file.readLine();
								}
							}
							
							else if (residue_sequence == 2 && already_printed == 7) {
								in_correct = correct_file.readLine();
							}
						}

						else if (correct(ref, in_correct) == true) {
							break;
						}
						else {
							in_correct = correct_file.readLine();
						}
				}
			}
				break;
//			}

		}
	}
	
	/**
	 * This method will correct the atom number.
	 * 
	 * @param ref
	 * @param to_correct
	 * @return
	 */
	public static boolean correct(String ref, String to_correct) {
		String acid_ref = ref.substring(17, 20);
		String atom_ref = ref.substring(12, 16);
		int number = Integer.parseInt(ref.substring(6, 11).replaceAll("\\s++", ""));
		boolean correct = false;
		
		if (to_correct.contains(acid_ref)) {
			if (to_correct.substring(12, 16).equals(atom_ref)) {
				String begin = to_correct.substring(0, 7);
				String end = to_correct.substring(12);
		
				output.printf("%s %3s %s %n", begin, number, end);
				correct = true;
			}
		}
		return correct;

	}

	/**
	 * main project
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		Scanner s = new Scanner(System.in);
		System.out.print("Enter the file name to correct: ");
		String cor_filename = s.nextLine();
		System.out.print("Enter the reference filename: ");
		String ref_filename = s.nextLine();
		
		String output_name;
//		= cor_filename.substring(0,	cor_filename.length() - 4) + "_corrected.pdb"
		try {
//			 output = new PrintWriter(output_name);

			read_correct = new BufferedReader(new FileReader(cor_filename));
			
			File input = new File(cor_filename);
			String directory = input.getParent();
			
			output = new PrintWriter(cor_filename.substring(0, cor_filename.length()-4) + "_Orderfixed.pdb");
			
			while (read_correct.ready()) {
				String in_correct = read_correct.readLine();

//				tracing the number of models in the file
//				if (in_correct.contains("MODEL")) {
//					System.out.println(in_correct);
//					already_printed=0;
//					printed++;
					
//					printing each model in a file
//					if (printed <81) {
//						output_name = directory + "\\1PLW\\1PLW_Model" + printed + "_aa.pdb";
//					}
//					else if (printed > 80 && printed < 161) {
//						output_name = directory + "\\1PLX\\1PLX_Model" + (printed-80) + "_aa.pdb";
//					}
//					else {
//						output_name = directory + "\\2LWC\\2LWC_Model" + (printed-160) + "_aa.pdb";
//					}
					
//					output = new PrintWriter(output_name);
//				}
				
				if (in_correct.startsWith("ATOM")) {
					read_ref = new BufferedReader(new FileReader(ref_filename));
					
					while (read_ref.ready()) {
						String in_ref = read_ref.readLine();
						read_fix = new BufferedReader(new FileReader(cor_filename));

						if (in_ref.startsWith("ATOM")) {
								compare(in_ref, read_fix);
						}
					}
					
					read_ref.close();
					while (!(Integer.parseInt(in_correct.substring(6, 11).replaceAll("\\s++",
							"")) == 75)) {
						in_correct = read_correct.readLine();
					}
				}
				else {
					System.out.println(in_correct);
					output.println(in_correct);
					
//					for producing each model in a file
//					if (in_correct.contains("END")) {
//						 if ((printed == 80) || (printed == 160) || (printed == 180)) {
//							 output.close();
//						 }
//					 }
				}
			}
			output.close();
			read_correct.close();
		} catch (FileNotFoundException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}
		s.close();
	}
}
