/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

/**
 * class WrongRecordTypeException
 * 
 * @author (Fei) John Chen
 */
public class WrongRecordTypeException extends RelationException 
{
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 959449904089119115L;
	
	public WrongRecordTypeException( String message ) {
        super(message);
    }
    
    public String getCorrectType() 
    {
        return correct;
    }
    
    public void setCorrectType(String correct) 
    {
        this.correct = correct;
    }
    
    public String getWrongType() 
    {
        return wrong;
    }
    
    public void setWrongType(String wrong) 
    {
        this.wrong = wrong;
    }
    
    private String correct;
    private String wrong;
}
