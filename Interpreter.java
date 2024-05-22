import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
// Edel Dominguez - 23656568

public class Interpreter {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        String[] lines = input.split(";");
        
        Map<String, Integer> variables = new HashMap<>();

        for (String line : lines) {
            String[] assignment = line.split("=");
            if (assignment.length != 2) {
                System.out.println("Error: Invalid input");
                return;
            }

            String variable = assignment[0].trim();
            String expression = assignment[1].trim();

            if (!evaluateVariable(variable)) {
                System.out.println("Error: Invalid variable name");
                return;
            }

            int value = evaluateExpression(expression, variables);
            if (value == Integer.MIN_VALUE) {
                System.out.println("Error: Invalid expression");
                return;
            }

            variables.put(variable, value);
        }

        for (Map.Entry<String, Integer> entry : variables.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
    }

    private static boolean evaluateVariable(String variable) {
        if (variable.length() == 0) {
            return false;
        }
        if (!Character.isLetter(variable.charAt(0)) && variable.charAt(0) != '_') {
            return false;
        }
        for (int i = 1; i < variable.length(); i++) {
            if (!Character.isLetterOrDigit(variable.charAt(i)) && variable.charAt(i) != '_') {
                return false;
            }
        }
        return true;
    }

    private static int evaluateExpression(String expression, Map<String, Integer> variables) {
        List<String> tokens = getTokens(expression);
        return evaluateExpression(tokens, variables);
    }

    private static List<String> getTokens(String expression) {
        List<String> result = new ArrayList<String>(); // List to store the extracted tokens
        StringBuffer buffer = new StringBuffer(); // Buffer to accumulate characters to form a token
            
        // Iterate through each character in the expression
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            // Method to check if the character is an operator or parenthesis
                if (c == '+' || c == '-' || c == '*' || c == '(' || c == ')') {
                    // If there are characters in the buffer, add them as a token to the result list
                    if (buffer.length() != 0) {
                    result.add(buffer.toString());
                    buffer = new StringBuffer(); // Reset the buffer to accumulate the next token
                }
                result.add(c + ""); // Add the operator or parenthesis as a separate token
            } else {
                buffer.append(c); // Append non-operator characters to the buffer
            }
        }
        // Add the remaining content in the buffer as a token
        if (buffer.length() != 0) {
            result.add(buffer.toString());
        }
        return result;
    }

    private static int evaluateExpression(List<String> tokens, Map<String, Integer> variables) {
    int result = evaluateTerm(tokens, variables);
    // If the evaluation fails, return the error value
    if (result == Integer.MIN_VALUE) {
        return result;
    }
    // Loop through the tokens to process the expression
    while (!tokens.isEmpty()) {
        String operator = tokens.remove(0);
        if (!isValidOperator(operator)) {
            tokens.add(0, operator);
            return result;
        }

        int value = evaluateTerm(tokens, variables);
        if (value == Integer.MIN_VALUE) {
            return value;
        }

        result = applyOperation(result, value, operator); // Apply the operator to the result and value
    }

    return result;
}

// Check if the string represents a valid operator
private static boolean isValidOperator(String operator) {
    return operator.equals("+") || operator.equals("-");
}

// Apply the operation on the result based on the operator
private static int applyOperation(int result, int value, String operator) {
    return operator.equals("+") ? result + value : result - value;
}

private static int evaluateTerm(List<String> tokens, Map<String, Integer> variables) {
    int result = evaluateFactor(tokens, variables); 
    // Return error if evaluation fails
    if (result == Integer.MIN_VALUE) { 
        return result;
    }

    // Loop through the tokens to process the factors
    while (!tokens.isEmpty()) {
        String operator = tokens.remove(0); 
        if (!isValidMultiplication(operator)) { // Check if the operator is for multiplication
            tokens.add(0, operator); 
            return result;
        }

        int value = evaluateFactor(tokens, variables); 
        if (value == Integer.MIN_VALUE) { // Return error if evaluation fails
            return value;
        }

        result *= value; // Multiply the result by the evaluated value
    }

    return result; 
}

// Check if the string represents a multiplication operator
private static boolean isValidMultiplication(String operator) {
    return operator.equals("*");
}

private static int evaluateFactor(List<String> tokens, Map<String, Integer> variables) {
    String nextToken = tokens.remove(0); 
    // Check if it's an opening parenthesis
    if (nextToken.equals("(")) { 
        int result = evaluateExpression(tokens, variables); // Evaluate the expression within the parentheses
        
        if (result == Integer.MIN_VALUE || tokens.isEmpty() || !tokens.remove(0).equals(")")) {
            return Integer.MIN_VALUE; // Return error if expression evaluation fails or no closing parenthesis found
        }
            return result; // Return the evaluated result within parentheses
      // Handle unary plus/minus
    } else if (nextToken.equals("+") || nextToken.equals("-")) { 
        int value = evaluateFactor(tokens, variables); 
        
        if (value == Integer.MIN_VALUE) {
            return value; // Return error if factor evaluation fails
        }
            return (nextToken.equals("+")) ? value : -value; 
      // Handle integers and variables        
    } else { 
        if (isValidInteger(nextToken)) { // Check if it's a valid integer
            return Integer.parseInt(nextToken); // Return the parsed integer value
        } else if (variables.containsKey(nextToken)) { // Check if it's a valid variable
            return variables.get(nextToken); 
        } else {
            return Integer.MIN_VALUE; // Return error if it's none of the above
        }
    }
}

// Method to check if the string represents a valid integer
private static boolean isValidInteger(String s) {
    if (s.charAt(0) == '0' && s.length() > 1) {
        return false;
    }
    for (int i = 0; i < s.length(); i++) {
        if (!Character.isDigit(s.charAt(i))) {
            return false;
        }
    }
    return true;
}

}