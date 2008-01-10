/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
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

/**
 * ExtensionMapper.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: #axisVersion# #today#
 */

package eu.sqooss.scl.axis2.ws;
/**
 *  ExtensionMapper class
 */

public  class ExtensionMapper{

    public static java.lang.Object getTypeObject(java.lang.String namespaceURI,
            java.lang.String typeName,
            javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{


        if (
                "http://datatypes.services.web.service.impl.sqooss.eu/xsd".equals(namespaceURI) &&
                "WSMetric".equals(typeName)){

            return  eu.sqooss.scl.axis2.datatypes.WSMetric.Factory.parse(reader);


        }


        if (
                "http://datatypes.services.web.service.impl.sqooss.eu/xsd".equals(namespaceURI) &&
                "WSProjectVersion".equals(typeName)){

            return  eu.sqooss.scl.axis2.datatypes.WSProjectVersion.Factory.parse(reader);


        }


        if (
                "http://datatypes.services.web.service.impl.sqooss.eu/xsd".equals(namespaceURI) &&
                "WSProjectFile".equals(typeName)){

            return  eu.sqooss.scl.axis2.datatypes.WSProjectFile.Factory.parse(reader);


        }


        if (
                "http://datatypes.services.web.service.impl.sqooss.eu/xsd".equals(namespaceURI) &&
                "WSMetricType".equals(typeName)){

            return  eu.sqooss.scl.axis2.datatypes.WSMetricType.Factory.parse(reader);


        }


        if (
                "http://datatypes.services.web.service.impl.sqooss.eu/xsd".equals(namespaceURI) &&
                "WSStoredProject".equals(typeName)){

            return  eu.sqooss.scl.axis2.datatypes.WSStoredProject.Factory.parse(reader);


        }


        if (
                "http://datatypes.services.web.service.impl.sqooss.eu/xsd".equals(namespaceURI) &&
                "WSUser".equals(typeName)){

            return  eu.sqooss.scl.axis2.datatypes.WSUser.Factory.parse(reader);


        }


        if (
                "http://datatypes.services.web.service.impl.sqooss.eu/xsd".equals(namespaceURI) &&
                "WSUserGroup".equals(typeName)){

            return  eu.sqooss.scl.axis2.datatypes.WSUserGroup.Factory.parse(reader);


        }


        if (
                "http://datatypes.services.web.service.impl.sqooss.eu/xsd".equals(namespaceURI) &&
                "WSFileMetadata".equals(typeName)){

            return  eu.sqooss.scl.axis2.datatypes.WSFileMetadata.Factory.parse(reader);


        }


        throw new java.lang.RuntimeException("Unsupported type " + namespaceURI + " " + typeName);
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
