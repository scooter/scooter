/*
 *   This software is distributed under the terms of the FSF 
 *   Gnu Lesser General Public License (see lgpl.txt). 
 *
 *   This program is distributed WITHOUT ANY WARRANTY. See the
 *   GNU General Public License for more details.
 */
package com.scooterframework.orm.activerecord;

/**
 * class AlreadyAttachedException
 * 
 * @author (Fei) John Chen
 */
public class AlreadyAttachedException extends RelationException 
{
    /**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -4895770666702413885L;

	public AlreadyAttachedException() {
        super();
    }
}
