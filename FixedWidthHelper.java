package com.spark.fixedlength;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@SuppressWarnings("rawtypes")
public class FixedWidthHelper {

    private static final Logger LOG = LoggerFactory
            .getLogger(FixedWidthHelper.class);
    static int counter = 0;
    static boolean isFixedWidthField = false;
    private FixedWidthHelper() {

    }

    public static Object[] getFields(
            StructType schema, String line,
            String[] lengthsAndDelimiters,
            boolean safe, String quote, List<FastDateFormat> dateFormats)  throws Exception{


        if (!line.equals("")) {

            try {
                String[] tokens = generateTokensFromRawData(line,
                        lengthsAndDelimiters, quote);
                return coerceParsedTokens(line, tokens, safe, schema, dateFormats);
            } catch (Exception e) {
                throw new RuntimeException("Exception while generating tokens.\nLine being parsed: "
                        + line + "\nFields: " + getFieldsFromSchema(schema,dateFormats.size())
                        + "\nLengths and delimiters in scheme: "
                        + Arrays.toString(lengthsAndDelimiters)
                        + "\nDatatypes in scheme: "
                        + getTypesFromSchema(schema,dateFormats.size())
                        + "\nSafe was set to: " + safe + "\n Error being -> " ,e);
            }
        } else {
            return new Object[lengthsAndDelimiters.length];
        }
    }

    private static String getTypesFromSchema(StructType schema, int length) {
        String fields = schema.apply(0).dataType().toString();
        for(int i=1;i< length ;i++){
            fields = fields + schema.apply(i).dataType().toString();
        }
        return fields;
    }

    private static String getFieldsFromSchema(StructType schema, int length) {
        String fields = schema.apply(0).name();
        for(int i=1;i< length ;i++){
            fields = fields + schema.apply(i).name();
        }
        return fields;
    }

    private static String[] generateTokensFromRawData(
            String line,String[] lengthsAndDelimiters,
            String quote) {
        String tokens[] = new String[lengthsAndDelimiters.length];
        String strings[];
        String identifier;
        quote = FixedWidthHelper.maskRegexChar(quote);
        for (int i = 0; i < lengthsAndDelimiters.length; i++) {
            identifier = FixedWidthHelper
                    .maskRegexChar(lengthsAndDelimiters[i]);

                tokens[i] = line.substring(0, Integer.parseInt(identifier));
                if (i != (lengthsAndDelimiters.length - 1))
                    line = line.substring(Integer.parseInt(identifier));

        }
        return tokens;
    }

    private static Object[] coerceParsedTokens(
            String line, Object[] tokens, boolean safe,
            StructType schema, List<FastDateFormat> dateFormats)  throws Exception {

        Object[] result = new Object[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            try {
                tokens[i] = !schema.apply(i).dataType().simpleString().equalsIgnoreCase("String") ? tokens[i].toString().trim() : tokens[i];
                result[i] = TypeCast.inputValue(tokens[i].toString(), schema.apply(i).dataType(),
                        schema.apply(i).nullable(), "null", true, null);
            } catch (Exception exception) {
                result[i] = null;
                if (!safe) {
                    throw new RuntimeException(getSafeMessage(tokens[i], i, schema) + "\n Line being parsed => " + line,exception);
                }
            }
        }
        tokens = result;
        return tokens;
    }

    private static String getSafeMessage(
            Object object, int i, StructType schema) {
        try {
            return "field " + schema.apply(i).name() + " cannot be coerced from : " + object + " to: " + schema.apply(i).dataType();
        } catch (Throwable throwable) {
            return "field pos " + i + " cannot be coerced from: " + object + ", pos has no corresponding field name or coercion type";
        }
    }


    public static boolean isLastFieldNewLine(String[] lengthsAndDelimiters) {
        return lengthsAndDelimiters[lengthsAndDelimiters.length - 1]
                .matches("\n")
                || lengthsAndDelimiters[lengthsAndDelimiters.length - 1]
                .contentEquals("\\n");
    }

    public static boolean hasaNewLineField(String[] lengthsAndDelimiters) {
        for (String string : lengthsAndDelimiters) {
            if (string.contains("\n") || string.contentEquals("\\n"))
                return true;
        }
        return false;
    }

    public static boolean containsNewLine(String outputLine) {
        return outputLine.contains("\n") || outputLine.contains("\\n")
                || outputLine.contains("\r\n") || outputLine.contains("\\r\\n");
    }

    public static String modifyIdentifier(String identifier) {
        String string = identifier;
        if (identifier.contains("\\r\\n")) {
            string = identifier.replace("\\r\\n", "\r\n");
        } else if (identifier.contains("\\n")) {
            string = identifier.replace("\\n", "\n");
        }
        if (identifier.contains("\\t")) {
            string = identifier.replace("\\t", "\t");
        }
        if (identifier.contains("\\x")) {
            string = parseHex(identifier);
        }
        return string;
    }

    public static String[] modifyIdentifier(String[] identifiers) {
        for (int i = 0; i < identifiers.length; i++) {
            identifiers[i] = modifyIdentifier(identifiers[i]);
        }
        return identifiers;
    }



    public static boolean isLastFixedWidthFieldNewLineField(
            String[] lengthsAndDelimiters) {
        try {
            return Integer
                    .parseInt(lengthsAndDelimiters[lengthsAndDelimiters.length - 1]) == 1;
        } catch (Exception e) {
            return false;
        }
    }

    public static String maskRegexChar(
            String singleChar) {
        if (singleChar.contains("|")) {
            singleChar = singleChar.replace("|", "\\|");
        }
        if (singleChar.contains(".")) {
            singleChar = singleChar.replace(".", "\\.");
        }
        if (singleChar.contains("+")) {
            singleChar = singleChar.replace("+", "\\+");
        }
        if (singleChar.contains("$")) {
            singleChar = singleChar.replace("$", "\\$");
        }
        if (singleChar.contains("*")) {
            singleChar = singleChar.replace("*", "\\*");
        }
        if (singleChar.contains("?")) {
            singleChar = singleChar.replace("?", "\\?");
        }
        if (singleChar.contains("^")) {
            singleChar = singleChar.replace("^", "\\^");
        }
        if (singleChar.contains("-")) {
            singleChar = singleChar.replace("-", "\\-");
        }
        if (singleChar.contains(")")) {
            singleChar = singleChar.replace(")", "\\)");
        }
        if (singleChar.contains("(")) {
            singleChar = singleChar.replace("(", "\\(");
        }
        if (singleChar.contains("\\x")) {
            singleChar = parseHex(singleChar);
        }
        return singleChar;
    }



    public static String parseHex(String input) {
        final int NO_OF_DIGITS = 2;

        if (input.contains("\\t")) {
            input = input.replace("\\t", "\\x09");
        }

        String[] tokens = input.split("\\\\x");
        String hex;
        String temp;
        boolean startsWithHex = input.startsWith("\\x");

        for (int counter = 0; counter < tokens.length; counter++) {

            if (counter == 0 && !startsWithHex)
                continue;

            if (tokens[counter].equals(""))
                continue;

            temp = tokens[counter];
            hex = temp.substring(0, NO_OF_DIGITS);
            temp = temp.substring(NO_OF_DIGITS, temp.length());
            tokens[counter] = hexToChar(hex) + temp;

        }

        String result = "";
        for (String token : tokens) {
            result = result + token;
        }
        return result;
    }

    private static char hexToChar(String hex) {
        return (char) Short.parseShort(hex, 16);
    }
}