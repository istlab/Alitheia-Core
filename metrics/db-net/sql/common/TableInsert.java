package paxosk.sql.common;

import java.util.*;
import java.io.*;
import org.apache.log4j.*;
import paxosk.sql.common.exceptions.*;

/**
 * Formulates and stores an SQL INSERT statement; the 
 * form of the table can be conceptually represented as:
 * columnName1,type1,value1
 * columnName2,type2,value2
 * ...
 */
public class TableInsert 
{
    //table storing the column names and the corresponding (values,type) tuples of the insert
    private Hashtable<String,TypeValueT> hColumnValue_=new Hashtable<String,TypeValueT>();
    //table storing all the sql data types and the corresponding type defined in the "Type" enum;
    //used as base for getting the corresponding internal data types of the class, given the 
    //SQL data types -class uses an internal data type representation
    private static Hashtable<String,Type> corTable_=new Hashtable<String,Type>();
    //an array storing the names of the columns; given this array the correspondingvalues will be 
    //returned, achieving in this way a 1-1 correspondence
    private String[] columnNames=null;
    //the name of the SQL table
    private String tabName_="";
    //the logger of the SourceParser
    private Logger logger_=null;
    
    
    /**
     * Constructor:
     * Just initializes the SQL to class internal representation data types table. See {@link #initCorrespondenceTable()}
     */
    public TableInsert(String tabName,Logger logger) 
    {
        tabName_=tabName;
        logger_=logger;        
        initCorrespondenceTable();
    }//SQLInsertTable

    
    /**
     * Default Constructor
     */
    public TableInsert()
    {
        logger_=Logger.getLogger("DefConstructor_Logger");
        initCorrespondenceTable();        
    }//SQLInsertTable
    
    
    /**
     * Declare a new column and type in the table; must be called before the updating the value of a field 
     * by using the {@link #updateField(String,String)} method. Used by in-package SourceParser class.
     * @parameter fieldName the name of the SQL column/field, as declared in the SQL source file
     * @type the type of this field
     * 
     * The caller must first call the {@link #getCorrespondingType(String) } method to ensure,
     * that he is getting the write {@link #Type}
     */
    protected void declareField(String fieldName,Type type)
    {
        hColumnValue_.put(fieldName,new TypeValueT(type));
    }//declareField
    
    
    
    /**
     * Removes the named field/column from the table; used in the case, where we do not want
     * to updated a given field, either  because its value will be later updated or because
     * it will be auto incremented.
     * The opposite of {@link #declareField(String,Type)}
     *
     * @param fname the name of the field/column to be excluded
     * @return true if the field name exists and has been excluded
     *      and false otherwise
     */
    public boolean excludeField(String fname)
    {
        Object o=hColumnValue_.remove(fname);
        //if the key didn't  exist
        if (o==null) {
            return false;
        }

        return true;
    }//excludeField             
    
    
    /**
     * Given the field name and the value, updates the insert table.
     * @parameter fieldName the name of the field/column of the SQL table
     * @value teh corresponding value for  the field above
     * @throws FieldNotDeclaredException in case the corresponding field has not been declared
     * in the table, by using the {@link #declareField(String,Type)} method.
     */
    public void updateField(String fieldName,String value) throws FieldNotDeclaredException
    {
        if (!hColumnValue_.containsKey(fieldName)) {
            throw new FieldNotDeclaredException("The value of column "+fieldName+" has not yet been" +
                    " declared! Cannot update");
        }
        
        hColumnValue_.get(fieldName).value_=value;
    }//updateField
    
    
    
    /**
     * Returns the value for a given column name of the insert table
     * @parameter the name of the field/column
     * @return the value of the field/column
     */
    public String getValue(String fieldName)
    {
        return hColumnValue_.get(fieldName).value_;
    }//getValue
    
    
    /**
     * Returns the corresponding type for the given field/column name of the 
     * insert table
     * @parameter the name of the field
     * @returns the {@link #Type} of the field
     */
    public Type getType(String fieldName)
    {
        return hColumnValue_.get(fieldName).type_;
    }//getType           

    
    /**
     * @return the name of the TableInsert
     */
    public String getName()
    {
        return tabName_;
    }//getName
    
        
    /**
     * Performs various checks and tranformations on the insert table:
     * -checks if a NUMERIC field (SQL (BIG,MEDIUM,SMALL)int) contains only digits
     * -checks if the value of a field is empty and a warning is issued
     * -encapsulates string represented values (either SQL timestamps or strings)
     *  in double-quotes
     * @return true upon successful  validation and false  otherwise
     */
    public boolean validate()
    {
        logger_.debug("Will validate table: "+ tabName_);
        logger_.debug(tabName_+":  "+toString());
        
        for (String field: hColumnValue_.keySet())
        {
            String newvalue=SQLUtils.sanitizeInputString(hColumnValue_.get(field).value_);
            //insert the sanitized value into the table, in the place of the old value
            hColumnValue_.get(field).value_=newvalue; 
            //now get the new value
            String value=hColumnValue_.get(field).value_;
        
            //ckecks and warns for empty values
            if (value.equals("")) {
                logger_.warn(tabName_+" "+field+": empty string value found while validating insert table");
            }
            
            //encapulates the value in "", in all cases in general, except the
            //case of inserting a value of numerical type
            TypeValueT tvt=hColumnValue_.get(field);
            Type t=tvt.type_;
            if (t.equals(Type.DATE))
            {
                String date=getValue(field);
                date='\"'+date+'\"';
                
                try {
                    updateField(field,date);
                } catch (FieldNotDeclaredException ex) {
                    ex.printStackTrace();
                }
            }
            else if (t.equals(Type.NUMERIC))
            {
                String number=getValue(field);
                char[] carray=number.toCharArray();
                for (char c: carray)
                {
                    if (!Character.isDigit(c)) {
                        return false;
                    }
                }//for
            }            
            else if (t.equals(Type.STRING))
            {
                String s=getValue(field);
                s='\"'+s+'\"';
                
                try {
                    updateField(field,s);
                } catch (FieldNotDeclaredException ex) {
                    ex.printStackTrace();
                }                
            }       
            else if (t.equals(Type.ENUM))
            {
                String s=getValue(field);
                s='\"'+s+'\"';
                
                try {
                    updateField(field,s);
                } catch (FieldNotDeclaredException ex) {
                    ex.printStackTrace();
                }                      
            }
        }//for
        
        return true;
    }//validate
       
    
    /**
     * Resets all column values to "" strings; must be called before
     * starting inserting values for a new "INSERT" call
     */
    public void resetTableValues()
    {
        for (String key:hColumnValue_.keySet())
        {
            hColumnValue_.get(key).value_="";
        }
    }//resetValues
       
    
    
    /**
     * Get the string names of the column names in a suitable form, in order to append them
     * to  an INSERT statement
     * @return a string representation of the column names (col1,col2,col3,...)
     */
    public String getColumnsString()
    {
        String s="(";
        Object[] sarray=hColumnValue_.keySet().toArray();
        columnNames=new String[sarray.length];
        
        s+=(String)sarray[0];
        columnNames[0]=(String)sarray[0];
                
        for (int i=1; i<sarray.length; i++)
        {
            s+=",";
            s+=(String)sarray[i];
            columnNames[i]=(String)sarray[i];
        }
        
        s+=")";
        return s;
    }//getColumnsString
    
            
    
    /**
     * Get the string values of the columns in a suitable form, in order to append them
     * to  an INSERT statement
     * @return a string representation of the column names (val1,val2,val3,...)
     */
    public String getValuesString() throws ColumnsNotRetrievedException
    {
        //if the array containing the column names has not been populated yet;
        //we cannot retrieve the corresponding values
        if (columnNames==null) {
            throw new ColumnsNotRetrievedException();
        }
        
        String s="(";                
        s+=getValue(columnNames[0]);
        
        for (int i=1; i<columnNames.length; i++)
        {
            s+=",";
            s+=getValue(columnNames[i]);
        }
                
        s+=")";
        //reset the array containing the column names to null, in order for it
        //to be used for the next insert
        columnNames=null;
        
        return s;        
    }//getValuesString
    
    
    
    /**
     * Given an SQL datatype, its corresponding of {@link #Type} is returned
     * @return the Type
     */
    public static Type getCorrespondingType(String type)
    {
        String genType=type.split("\\(")[0].trim(); //it might be something like VARCHAR(255); get the VARCHAR only
        genType=genType.split(",")[0]; //it might be something like "TEXT,"; get the "TEXT" only
        return corTable_.get(genType);
    }//getCorrespondingType
    
    
    /**
     * Gets the string representation of the TableInsert
     * @return the string
     */
    public String toString()
    {
        String s="<";
        for (String key:hColumnValue_.keySet())
        {
            s+="("+key+","+getValue(key)+","+getType(key)+")";
        }
        s+=">";
        
        return s;
    }//toString
    
    
    ///////////////////////////////////////////////HELPERS//////////////////////////////////////////////////
    /**
     * Initializes the table, which maps the  SQL datatypes to the class initial representation
     * of these data types
     */
    private void initCorrespondenceTable()
    {
        corTable_.put("VARCHAR",Type.STRING);
        corTable_.put("CHAR",Type.STRING);
        corTable_.put("TEXT",Type.STRING);
        corTable_.put("MEDIUMTEXT",Type.STRING);
        corTable_.put("INT",Type.NUMERIC);
        corTable_.put("BIGINT",Type.NUMERIC);
        corTable_.put("SMALLINT",Type.NUMERIC);
        corTable_.put("MEDIUMINT",Type.NUMERIC);
        corTable_.put("TIMESTAMP",Type.DATE);
        corTable_.put("TIME",Type.DATE);
        corTable_.put("DATE",Type.DATE);
        corTable_.put("INT",Type.NUMERIC);
        corTable_.put("ENUM",Type.ENUM);
    }//initCorrespondenceTable
    ///////////////////////////////////////////////HELPERS//////////////////////////////////////////////////
    
    
    /////////////////////////////////////////////TypeValueT/////////////////////////////////////////////////
    /** 
     * Class representing a (type,value) tuple, ie the type of the field/column of the SQL table, followed
     * by the corresponding value
     */
    private class TypeValueT
    {
        Type type_;
        String value_="";
        
        TypeValueT(Type type)
        {
            type_=type;
        }//TypeValueT
        
        TypeValueT(Type type,String value)
        {
            type_=type;
            value_=value;
        }//TypeValueT
        
        
        public int hashCode()
        {
            return new String(type_.toString()+value_).hashCode();
        }//hashCode
        
        
        public boolean equals(Object o)
        {
            if (o==this) {
                return true;
            }
            else if (!(o instanceof TableInsert)) {
                return false;
            }
            else {
                TypeValueT tvt=(TypeValueT)o;
                return ((this.type_.equals(tvt.type_)) && (this.value_.equals(tvt.value_)));
            }
        }//equals
        
    }//TypeValueT
        
    protected enum Type {NUMERIC, STRING, DATE,ENUM}    
    /////////////////////////////////////////////TypeValueT/////////////////////////////////////////////////  
    
    
//    public static void main(String[] args) throws Exception
//    {
//        TableInsert t=new TableInsert();
//        t.declareField("count",Type.NUMERIC);
//        t.declareField("name",Type.STRING);
//        t.declareField("xman",Type.NUMERIC);
//        t.excludeField("xman");
//        t.updateField("count","10");
//        t.updateField("name","kostas");
//        t.validate();
//        System.err.println(t.getColumnsString());
//        System.err.println(t.getValuesString());      
//    }//main
}
