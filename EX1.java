import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Ex1 {
    public static void main(String[] args) {
        
        BufferedReader reader;

		try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true));
			reader = new BufferedReader(new FileReader("input.txt"));
			String line = reader.readLine();
            Bayesian_Network network= new Bayesian_Network(line);
            line=reader.readLine();
			while (line != null) {
				// read next line
                int algorithm;
                algorithm = Character.getNumericValue( line.charAt(line.length()-1));
                //looks like a lot,is a bunch of regex to figure out the variables outcomes and query,
                //assumes only one query that comes first and that every variable is comprised of only letters and numbers,
                //could be a problem with spaces,is easily fixable
                Pattern variablePattern = Pattern.compile("([a-z A-Z\\d]*)=");
                Pattern outcomePattern =  Pattern.compile("=([a-z A-Z\\d]*)");
                Matcher variableMatcher = variablePattern.matcher(line);
                Matcher outcomeMatcher = outcomePattern.matcher(line);
                variableMatcher.find();
                outcomeMatcher.find();
                String Query = variableMatcher.group(1);
                String QueryOutcome = outcomeMatcher.group(1);
                HashMap<String,String> variableMap = new HashMap<String,String>();
                while (variableMatcher.find()&&outcomeMatcher.find()) {
                    variableMap.put(variableMatcher.group(1), outcomeMatcher.group(1));
                }
                //calculate probability using defined algorithm
                funcOutput output;
                if (algorithm==1) {
                    
                
                output = network.naiveQuery(variableMap, Query, QueryOutcome);}
                else{
                    output= network.VECalculateProbabilty(variableMap, Query, QueryOutcome,algorithm);
                }
                
                String outputLine = output.getOutput()+","+output.getAdditionOperations()+","+output.getMultOperations()+"\n";
                writer.append(outputLine);
				line = reader.readLine();

			}
            writer.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
