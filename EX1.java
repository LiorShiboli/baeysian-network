import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class EX1 {
    public static void main(String[] args) {
        
        BufferedReader reader;

		try {
			reader = new BufferedReader(new FileReader("input.txt"));
			String line = reader.readLine();
            Bayesian_Network network= new Bayesian_Network(line);
            line=reader.readLine();
			while (line != null) {
				// read next line
                int algorithm;
                algorithm = Character.getNumericValue( line.charAt(line.length()-1));
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
                funcOutput output;
                if (algorithm==1) {
                    
                
                output = network.naiveQuery(variableMap, Query, QueryOutcome);}
                else{
                    output= network.VECalculateProbabilty(variableMap, Query, QueryOutcome);
                }
                System.out.println(line);
                System.out.println(output.getOutput()+","+output.getAdditionOperations()+","+output.getMultOperations());
				line = reader.readLine();

			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
