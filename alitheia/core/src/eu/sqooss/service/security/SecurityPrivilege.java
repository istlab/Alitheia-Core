/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
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

package eu.sqooss.service.security;

public interface SecurityPrivilege {

    /**
     * @return the id of the privilege
     */
    public long getId();

    /**
     * @return the description of the group
     */
    public String getDescription();

    /**
     * Sets a new privilege description.
     * @param description the new description
     */
    public void setDescription(String description);

    /**
     * Sets a new privilege values. The old values are removed.
     * @param values the new privilege values
     */
    public void setValues(String[] values);

    /**
     * @return the privilege values
     */
    public String[] getValues();

    /**
     * Adds a new privilege value.
     * @param value the new privilege value
     * @return the id of the new privilege value  
     */
    public long addValue(String value);

    /**
     * Removes the privilege value.
     * @param value
     * @return <code>true</code> if the privilege exists and is removed successfully,
     * <code>false</code> otherwise
     */
    public boolean removeValue(String value);

    /**
     * Removes the privilege value with given id.
     * @param id
     * @return <code>true</code> if the privilege exists and is removed successfully,
     * <code>false</code> otherwise
     */
    public boolean removeValue(long id);

    /**
     * @param value
     * @return the id of the privilege value
     */
    public long getValueId(String value);

    /**
     * Removes the privilege.
     */
    public void remove();

}

//vi: ai nosi sw=4 ts=4 expandtab
