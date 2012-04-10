/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - 2010 - Organization for Free and Open Source Software,  
 *                Athens, Greece.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package eu.sqooss.service.abstractmetric;

import eu.sqooss.service.db.Bug;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.EncapsulationUnit;
import eu.sqooss.service.db.ExecutionUnit;
import eu.sqooss.service.db.MailMessage;
import eu.sqooss.service.db.MailingList;
import eu.sqooss.service.db.MailingListThread;
import eu.sqooss.service.db.NameSpace;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectDirectory;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static eu.sqooss.service.abstractmetric.InvocationOrder.OLDFIRST;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
public @interface SchedulerHints {

	/**
	 * Set the plug-in's prefered activation order. Make sure you do not forget
	 * any activators when overriding the default order. The default ordering is
	 * the following:
	 * 
	 * <ul>
	 * <li>{@link eu.sqooss.service.db.ExecutionUnit}</li>
	 * <li>{@link eu.sqooss.service.db.EncapsulationUnit}</li>
	 * <li>{@link eu.sqooss.service.db.NameSpace}</li>
	 * <li>{@link eu.sqooss.service.db.ProjectDirectory}</li>
	 * <li>{@link eu.sqooss.service.db.ProjectFile}</li>
	 * <li>{@link eu.sqooss.service.db.ProjectVersion}</li>
	 * <li>{@link eu.sqooss.service.db.MailingListThread}</li>
	 * <li>{@link eu.sqooss.service.db.MailMessage}</li>
	 * <li>{@link eu.sqooss.service.db.MailingList}</li>
	 * <li>{@link eu.sqooss.service.db.Bug}</li>
	 * <li>{@link eu.sqooss.service.db.StoredProject}</li>
	 * </ul>
	 * 
	 */
	public Class<? extends DAObject>[] activationOrder() 
		default {
	        ExecutionUnit.class,
	        EncapsulationUnit.class,
	        NameSpace.class,
			ProjectDirectory.class, 
			ProjectFile.class,
			ProjectVersion.class,
			MailingListThread.class,
			MailMessage.class,
			MailingList.class,
			Bug.class,
			StoredProject.class
		};

    /**
     * The order of resources by which the metrics should be run.
     * This is a best effort approach
     */
    public InvocationOrder invocationOrder() default OLDFIRST;
}

