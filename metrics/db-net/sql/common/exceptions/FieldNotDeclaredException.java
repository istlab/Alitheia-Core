package paxosk.sql.common.exceptions;


/* thrown, when the name of a column has not been declared in the SQLInsertTable and an attempt
 * to update the value of this non-existing column is made
 */
public class FieldNotDeclaredException extends Exception
{       
    public FieldNotDeclaredException() 
    {
        super();
    }
    
    public FieldNotDeclaredException(String msg) 
    {
        super(msg);
    }    
}
