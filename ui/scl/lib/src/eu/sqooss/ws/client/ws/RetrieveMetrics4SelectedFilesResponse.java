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

/**
 * RetrieveMetrics4SelectedFilesResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: #axisVersion# #today#
 */

package eu.sqooss.ws.client.ws;
/**
 *  RetrieveMetrics4SelectedFilesResponse bean class
 */

public  class RetrieveMetrics4SelectedFilesResponse
implements org.apache.axis2.databinding.ADBBean{

    public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
            "http://services.web.service.sqooss.eu/xsd",
            "retrieveMetrics4SelectedFilesResponse",
    "ns2");



    /**
     * field for _return
     * This was an Array!
     */

    protected eu.sqooss.ws.client.datatypes.WSMetric[] local_return ;


    /**
     * Auto generated getter method
     * @return eu.sqooss.ws.client.datatypes.WSMetric[]
     */
    public  eu.sqooss.ws.client.datatypes.WSMetric[] get_return(){
        return local_return;
    }






    /**
     * validate the array for _return
     */
    protected void validate_return(eu.sqooss.ws.client.datatypes.WSMetric[] param){

        if ((param != null) && (param.length < 1)){
            throw new java.lang.RuntimeException();
        }

    }


    /**
     * Auto generated setter method
     * @param param _return
     */
    public void set_return(eu.sqooss.ws.client.datatypes.WSMetric[] param){

        validate_return(param);


        this.local_return=param;
    }



    /**
     * Auto generated add method for the array for convenience
     * @param param eu.sqooss.ws.client.datatypes.WSMetric
     */
    public void add_return(eu.sqooss.ws.client.datatypes.WSMetric param){
        if (local_return == null){
            local_return = new eu.sqooss.ws.client.datatypes.WSMetric[]{};
        }



        java.util.List list =
            org.apache.axis2.databinding.utils.ConverterUtil.toList(local_return);
        list.add(param);
        this.local_return =
            (eu.sqooss.ws.client.datatypes.WSMetric[])list.toArray(
                    new eu.sqooss.ws.client.datatypes.WSMetric[list.size()]);

    }




    /**
     *
     * @param parentQName
     * @param factory
     * @return org.apache.axiom.om.OMElement
     */
    public org.apache.axiom.om.OMElement getOMElement(
            final javax.xml.namespace.QName parentQName,
            final org.apache.axiom.om.OMFactory factory){


        org.apache.axiom.om.OMDataSource dataSource =
            new org.apache.axis2.databinding.ADBDataSource(this,parentQName){

            public void serialize(
                    javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {



                java.lang.String prefix = parentQName.getPrefix();
                java.lang.String namespace = parentQName.getNamespaceURI();

                if (namespace != null) {
                    java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
                    if (writerPrefix != null) {
                        xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
                    } else {
                        if (prefix == null) {
                            prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                        }

                        xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);
                    }
                } else {
                    xmlWriter.writeStartElement(parentQName.getLocalPart());
                }



                if (local_return!=null){
                    for (int i = 0;i < local_return.length;i++){
                        if (local_return[i] != null){
                            local_return[i].getOMElement(
                                    new javax.xml.namespace.QName("http://services.web.service.sqooss.eu/xsd","return"),
                                    factory).serialize(xmlWriter);
                        } else {

                            // write null attribute
                            java.lang.String namespace2 = "http://services.web.service.sqooss.eu/xsd";
                            if (! namespace2.equals("")) {
                                java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

                                if (prefix2 == null) {
                                    prefix2 = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();

                                    xmlWriter.writeStartElement(prefix2,"return", namespace2);
                                    xmlWriter.writeNamespace(prefix2, namespace2);
                                    xmlWriter.setPrefix(prefix2, namespace2);

                                } else {
                                    xmlWriter.writeStartElement(namespace2,"return");
                                }

                            } else {
                                xmlWriter.writeStartElement("return");
                            }

                            // write the nil attribute
                            writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","true",xmlWriter);
                            xmlWriter.writeEndElement();

                        }

                    }
                } else {

                    // write null attribute
                    java.lang.String namespace2 = "http://services.web.service.sqooss.eu/xsd";
                    if (! namespace2.equals("")) {
                        java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

                        if (prefix2 == null) {
                            prefix2 = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();

                            xmlWriter.writeStartElement(prefix2,"return", namespace2);
                            xmlWriter.writeNamespace(prefix2, namespace2);
                            xmlWriter.setPrefix(prefix2, namespace2);

                        } else {
                            xmlWriter.writeStartElement(namespace2,"return");
                        }

                    } else {
                        xmlWriter.writeStartElement("return");
                    }

                    // write the nil attribute
                    writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","true",xmlWriter);
                    xmlWriter.writeEndElement();

                }


                xmlWriter.writeEndElement();



            }

            /**
             * Util method to write an attribute with the ns prefix
             */
            private void writeAttribute(java.lang.String prefix,java.lang.String namespace,java.lang.String attName,
                    java.lang.String attValue,javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException{
                if (xmlWriter.getPrefix(namespace) == null) {
                    xmlWriter.writeNamespace(prefix, namespace);
                    xmlWriter.setPrefix(prefix, namespace);

                }

                xmlWriter.writeAttribute(namespace,attName,attValue);

            }

            /**
             * Util method to write an attribute without the ns prefix
             */
            private void writeAttribute(java.lang.String namespace,java.lang.String attName,
                    java.lang.String attValue,javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException{
                if (namespace.equals(""))
                {
                    xmlWriter.writeAttribute(attName,attValue);
                }
                else
                {
                    registerPrefix(xmlWriter, namespace);
                    xmlWriter.writeAttribute(namespace,attName,attValue);
                }
            }

            /**
             * Register a namespace prefix
             */
            private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace) throws javax.xml.stream.XMLStreamException {
                java.lang.String prefix = xmlWriter.getPrefix(namespace);

                if (prefix == null) {
                    prefix = createPrefix();

                    while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
                        prefix = createPrefix();
                    }

                    xmlWriter.writeNamespace(prefix, namespace);
                    xmlWriter.setPrefix(prefix, namespace);
                }

                return prefix;
            }

            /**
             * Create a prefix
             */
            private java.lang.String createPrefix() {
                return "ns" + (int)Math.random();
            }
        };


        return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(
                MY_QNAME,factory,dataSource);

    }


    /**
     * databinding method to get an XML representation of this object
     *
     */
    public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName){



        java.util.ArrayList elementList = new java.util.ArrayList();
        java.util.ArrayList attribList = new java.util.ArrayList();


        if (local_return!=null) {
            for (int i = 0;i < local_return.length;i++){

                if (local_return[i] != null){
                    elementList.add(new javax.xml.namespace.QName("http://services.web.service.sqooss.eu/xsd",
                    "return"));
                    elementList.add(local_return[i]);
                } else {

                    elementList.add(new javax.xml.namespace.QName("http://services.web.service.sqooss.eu/xsd",
                    "return"));
                    elementList.add(null);

                }

            }
        } else {

            elementList.add(new javax.xml.namespace.QName("http://services.web.service.sqooss.eu/xsd",
            "return"));
            elementList.add(local_return);

        }



        return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(), attribList.toArray());



    }



    /**
     *  Factory class that keeps the parse method
     */
    public static class Factory{


        /**
         * static method to create the object
         * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
         *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
         * Postcondition: If this object is an element, the reader is positioned at its end element
         *                If this object is a complex type, the reader is positioned at the end element of its outer element
         */
        public static RetrieveMetrics4SelectedFilesResponse parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            RetrieveMetrics4SelectedFilesResponse object = new RetrieveMetrics4SelectedFilesResponse();
            int event;
            try {

                while (!reader.isStartElement() && !reader.isEndElement())
                    reader.next();


                if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","type")!=null){
                    java.lang.String fullTypeName = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                    "type");
                    if (fullTypeName!=null){
                        java.lang.String nsPrefix = fullTypeName.substring(0,fullTypeName.indexOf(":"));
                        nsPrefix = nsPrefix==null?"":nsPrefix;

                        java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":")+1);
                        if (!"retrieveMetrics4SelectedFilesResponse".equals(type)){
                            //find namespace for the prefix
                            java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (RetrieveMetrics4SelectedFilesResponse)eu.sqooss.ws.client.ws.ExtensionMapper.getTypeObject(
                                    nsUri,type,reader);
                        }

                    }

                }



                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.Vector handledAttributes = new java.util.Vector();


                boolean isReaderMTOMAware = false;

                try{
                    isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
                }catch(java.lang.IllegalArgumentException e){
                    isReaderMTOMAware = false;
                }




                reader.next();

                java.util.ArrayList list1 = new java.util.ArrayList();


                while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://services.web.service.sqooss.eu/xsd","return").equals(reader.getName())){



                    // Process the array and step past its final element's end.

                    if ("true".equals(reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil"))){
                        list1.add(null);
                        reader.next();
                    } else {
                        list1.add(eu.sqooss.ws.client.datatypes.WSMetric.Factory.parse(reader));
                    }
                    //loop until we find a start element that is not part of this array
                    boolean loopDone1 = false;
                    while(!loopDone1){
                        // We should be at the end element, but make sure
                        while (!reader.isEndElement())
                            reader.next();
                        // Step out of this element
                        reader.next();
                        // Step to next element event.
                        while (!reader.isStartElement() && !reader.isEndElement())
                            reader.next();
                        if (reader.isEndElement()){
                            //two continuous end elements means we are exiting the xml structure
                            loopDone1 = true;
                        } else {
                            if (new javax.xml.namespace.QName("http://services.web.service.sqooss.eu/xsd","return").equals(reader.getName())){

                                if ("true".equals(reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil"))){
                                    list1.add(null);
                                    reader.next();
                                } else {
                                    list1.add(eu.sqooss.ws.client.datatypes.WSMetric.Factory.parse(reader));
                                }
                            }else{
                                loopDone1 = true;
                            }
                        }
                    }
                    // call the converter utility  to convert and set the array
                    object.set_return((eu.sqooss.ws.client.datatypes.WSMetric[])
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                    eu.sqooss.ws.client.datatypes.WSMetric.class,
                                    list1));

                }  // End of if for expected property start element

                else{
                    // A start element we are not expecting indicates an invalid parameter was passed
                    throw new java.lang.RuntimeException("Unexpected subelement " + reader.getLocalName());
                }

                while (!reader.isStartElement() && !reader.isEndElement())
                    reader.next();
                if (reader.isStartElement())
                    // A start element we are not expecting indicates a trailing invalid property
                    throw new java.lang.RuntimeException("Unexpected subelement " + reader.getLocalName());



            } catch (javax.xml.stream.XMLStreamException e) {
                throw new java.lang.Exception(e);
            }

            return object;
        }

    }//end of factory class



}

//vi: ai nosi sw=4 ts=4 expandtab
