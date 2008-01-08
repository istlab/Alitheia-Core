package paxosk.sql.common.exceptions;

/* thrown when the creation of the string with the insert values is requested, by the 
 * string with the column names has not been created in the first place
 */
public class ColumnsNotRetrievedException extends Exception
{
    
    public ColumnsNotRetrievedException() 
    {
        super();
    }
    
    public ColumnsNotRetrievedException(String msg) 
    {
        super(msg);
    }    
}
