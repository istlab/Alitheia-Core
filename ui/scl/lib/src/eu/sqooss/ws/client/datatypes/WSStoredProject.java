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
 * WSStoredProject.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: #axisVersion# #today#
 */

package eu.sqooss.ws.client.datatypes;
/**
 *  WSStoredProject bean class
 */

public  class WSStoredProject
implements org.apache.axis2.databinding.ADBBean{
    /* This type was generated from the piece of schema that had
                name = WSStoredProject
                Namespace URI = http://datatypes.services.web.service.impl.sqooss.eu/xsd
                Namespace Prefix = ns1
     */


    /**
     * field for Bugs
     */

    protected java.lang.String localBugs ;


    /**
     * Auto generated getter method
     * @return java.lang.String
     */
    public  java.lang.String getBugs(){
        return localBugs;
    }



    /**
     * Auto generated setter method
     * @param param Bugs
     */
    public void setBugs(java.lang.String param){

        this.localBugs=param;


    }


    /**
     * field for Contact
     */

    protected java.lang.String localContact ;


    /**
     * Auto generated getter method
     * @return java.lang.String
     */
    public  java.lang.String getContact(){
        return localContact;
    }



    /**
     * Auto generated setter method
     * @param param Contact
     */
    public void setContact(java.lang.String param){

        this.localContact=param;


    }


    /**
     * field for Id
     */

    protected long localId ;


    /**
     * Auto generated getter method
     * @return long
     */
    public  long getId(){
        return localId;
    }



    /**
     * Auto generated setter method
     * @param param Id
     */
    public void setId(long param){

        this.localId=param;


    }


    /**
     * field for Mail
     */

    protected java.lang.String localMail ;


    /**
     * Auto generated getter method
     * @return java.lang.String
     */
    public  java.lang.String getMail(){
        return localMail;
    }



    /**
     * Auto generated setter method
     * @param param Mail
     */
    public void setMail(java.lang.String param){

        this.localMail=param;


    }


    /**
     * field for Name
     */

    protected java.lang.String localName ;


    /**
     * Auto generated getter method
     * @return java.lang.String
     */
    public  java.lang.String getName(){
        return localName;
    }



    /**
     * Auto generated setter method
     * @param param Name
     */
    public void setName(java.lang.String param){

        this.localName=param;


    }


    /**
     * field for ProjectVersions
     * This was an Array!
     */

    protected eu.sqooss.ws.client.datatypes.WSProjectVersion[] localProjectVersions ;


    /**
     * Auto generated getter method
     * @return eu.sqooss.ws.client.datatypes.WSProjectVersion[]
     */
    public  eu.sqooss.ws.client.datatypes.WSProjectVersion[] getProjectVersions(){
        return localProjectVersions;
    }






    /**
     * validate the array for ProjectVersions
     */
    protected void validateProjectVersions(eu.sqooss.ws.client.datatypes.WSProjectVersion[] param){

        if ((param != null) && (param.length < 1)){
            throw new java.lang.RuntimeException();
        }

    }


    /**
     * Auto generated setter method
     * @param param ProjectVersions
     */
    public void setProjectVersions(eu.sqooss.ws.client.datatypes.WSProjectVersion[] param){

        validateProjectVersions(param);


        this.localProjectVersions=param;
    }



    /**
     * Auto generated add method for the array for convenience
     * @param param eu.sqooss.ws.client.datatypes.WSProjectVersion
     */
    public void addProjectVersions(eu.sqooss.ws.client.datatypes.WSProjectVersion param){
        if (localProjectVersions == null){
            localProjectVersions = new eu.sqooss.ws.client.datatypes.WSProjectVersion[]{};
        }



        java.util.List list =
            org.apache.axis2.databinding.utils.ConverterUtil.toList(localProjectVersions);
        list.add(param);
        this.localProjectVersions =
            (eu.sqooss.ws.client.datatypes.WSProjectVersion[])list.toArray(
                    new eu.sqooss.ws.client.datatypes.WSProjectVersion[list.size()]);

    }


    /**
     * field for Repository
     */

    protected java.lang.String localRepository ;


    /**
     * Auto generated getter method
     * @return java.lang.String
     */
    public  java.lang.String getRepository(){
        return localRepository;
    }



    /**
     * Auto generated setter method
     * @param param Repository
     */
    public void setRepository(java.lang.String param){

        this.localRepository=param;


    }


    /**
     * field for Website
     */

    protected java.lang.String localWebsite ;


    /**
     * Auto generated getter method
     * @return java.lang.String
     */
    public  java.lang.String getWebsite(){
        return localWebsite;
    }



    /**
     * Auto generated setter method
     * @param param Website
     */
    public void setWebsite(java.lang.String param){

        this.localWebsite=param;


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



                namespace = "http://datatypes.services.web.service.impl.sqooss.eu/xsd";
                if (! namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();

                        xmlWriter.writeStartElement(prefix,"bugs", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    } else {
                        xmlWriter.writeStartElement(namespace,"bugs");
                    }

                } else {
                    xmlWriter.writeStartElement("bugs");
                }


                if (localBugs==null){
                    // write the nil attribute

                    writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","true",xmlWriter);

                }else{


                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localBugs));

                }

                xmlWriter.writeEndElement();

                namespace = "http://datatypes.services.web.service.impl.sqooss.eu/xsd";
                if (! namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();

                        xmlWriter.writeStartElement(prefix,"contact", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    } else {
                        xmlWriter.writeStartElement(namespace,"contact");
                    }

                } else {
                    xmlWriter.writeStartElement("contact");
                }


                if (localContact==null){
                    // write the nil attribute

                    writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","true",xmlWriter);

                }else{


                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localContact));

                }

                xmlWriter.writeEndElement();

                namespace = "http://datatypes.services.web.service.impl.sqooss.eu/xsd";
                if (! namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();

                        xmlWriter.writeStartElement(prefix,"id", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    } else {
                        xmlWriter.writeStartElement(namespace,"id");
                    }

                } else {
                    xmlWriter.writeStartElement("id");
                }

                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localId));

                xmlWriter.writeEndElement();

                namespace = "http://datatypes.services.web.service.impl.sqooss.eu/xsd";
                if (! namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();

                        xmlWriter.writeStartElement(prefix,"mail", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    } else {
                        xmlWriter.writeStartElement(namespace,"mail");
                    }

                } else {
                    xmlWriter.writeStartElement("mail");
                }


                if (localMail==null){
                    // write the nil attribute

                    writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","true",xmlWriter);

                }else{


                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMail));

                }

                xmlWriter.writeEndElement();

                namespace = "http://datatypes.services.web.service.impl.sqooss.eu/xsd";
                if (! namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();

                        xmlWriter.writeStartElement(prefix,"name", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    } else {
                        xmlWriter.writeStartElement(namespace,"name");
                    }

                } else {
                    xmlWriter.writeStartElement("name");
                }


                if (localName==null){
                    // write the nil attribute

                    writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","true",xmlWriter);

                }else{


                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localName));

                }

                xmlWriter.writeEndElement();

                if (localProjectVersions!=null){
                    for (int i = 0;i < localProjectVersions.length;i++){
                        if (localProjectVersions[i] != null){
                            localProjectVersions[i].getOMElement(
                                    new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd","projectVersions"),
                                    factory).serialize(xmlWriter);
                        } else {

                            // write null attribute
                            java.lang.String namespace2 = "http://datatypes.services.web.service.impl.sqooss.eu/xsd";
                            if (! namespace2.equals("")) {
                                java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

                                if (prefix2 == null) {
                                    prefix2 = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();

                                    xmlWriter.writeStartElement(prefix2,"projectVersions", namespace2);
                                    xmlWriter.writeNamespace(prefix2, namespace2);
                                    xmlWriter.setPrefix(prefix2, namespace2);

                                } else {
                                    xmlWriter.writeStartElement(namespace2,"projectVersions");
                                }

                            } else {
                                xmlWriter.writeStartElement("projectVersions");
                            }

                            // write the nil attribute
                            writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","true",xmlWriter);
                            xmlWriter.writeEndElement();

                        }

                    }
                } else {

                    // write null attribute
                    java.lang.String namespace2 = "http://datatypes.services.web.service.impl.sqooss.eu/xsd";
                    if (! namespace2.equals("")) {
                        java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

                        if (prefix2 == null) {
                            prefix2 = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();

                            xmlWriter.writeStartElement(prefix2,"projectVersions", namespace2);
                            xmlWriter.writeNamespace(prefix2, namespace2);
                            xmlWriter.setPrefix(prefix2, namespace2);

                        } else {
                            xmlWriter.writeStartElement(namespace2,"projectVersions");
                        }

                    } else {
                        xmlWriter.writeStartElement("projectVersions");
                    }

                    // write the nil attribute
                    writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","true",xmlWriter);
                    xmlWriter.writeEndElement();

                }

                namespace = "http://datatypes.services.web.service.impl.sqooss.eu/xsd";
                if (! namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();

                        xmlWriter.writeStartElement(prefix,"repository", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    } else {
                        xmlWriter.writeStartElement(namespace,"repository");
                    }

                } else {
                    xmlWriter.writeStartElement("repository");
                }


                if (localRepository==null){
                    // write the nil attribute

                    writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","true",xmlWriter);

                }else{


                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localRepository));

                }

                xmlWriter.writeEndElement();

                namespace = "http://datatypes.services.web.service.impl.sqooss.eu/xsd";
                if (! namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();

                        xmlWriter.writeStartElement(prefix,"website", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    } else {
                        xmlWriter.writeStartElement(namespace,"website");
                    }

                } else {
                    xmlWriter.writeStartElement("website");
                }


                if (localWebsite==null){
                    // write the nil attribute

                    writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","true",xmlWriter);

                }else{


                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localWebsite));

                }

                xmlWriter.writeEndElement();


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
                parentQName,factory,dataSource);

    }


    /**
     * databinding method to get an XML representation of this object
     *
     */
    public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName){



        java.util.ArrayList elementList = new java.util.ArrayList();
        java.util.ArrayList attribList = new java.util.ArrayList();


        elementList.add(new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd",
        "bugs"));

        elementList.add(localBugs==null?null:
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localBugs));

        elementList.add(new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd",
        "contact"));

        elementList.add(localContact==null?null:
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localContact));

        elementList.add(new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd",
        "id"));

        elementList.add(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localId));

        elementList.add(new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd",
        "mail"));

        elementList.add(localMail==null?null:
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMail));

        elementList.add(new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd",
        "name"));

        elementList.add(localName==null?null:
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localName));

        if (localProjectVersions!=null) {
            for (int i = 0;i < localProjectVersions.length;i++){

                if (localProjectVersions[i] != null){
                    elementList.add(new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd",
                            "projectVersions"));
                    elementList.add(localProjectVersions[i]);
                } else {

                    elementList.add(new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd",
                    "projectVersions"));
                    elementList.add(null);

                }

            }
        } else {

            elementList.add(new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd",
            "projectVersions"));
            elementList.add(localProjectVersions);

        }


        elementList.add(new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd",
        "repository"));

        elementList.add(localRepository==null?null:
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localRepository));

        elementList.add(new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd",
        "website"));

        elementList.add(localWebsite==null?null:
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localWebsite));


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
        public static WSStoredProject parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            WSStoredProject object = new WSStoredProject();
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
                        if (!"WSStoredProject".equals(type)){
                            //find namespace for the prefix
                            java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (WSStoredProject)eu.sqooss.ws.client.ws.ExtensionMapper.getTypeObject(
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

                java.util.ArrayList list6 = new java.util.ArrayList();


                while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd","bugs").equals(reader.getName())){

                    if (!"true".equals(reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil"))){

                        java.lang.String content = reader.getElementText();

                        object.setBugs(
                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                    } else {
                        reader.getElementText(); // throw away text nodes if any.
                    }

                    reader.next();

                }  // End of if for expected property start element

                else{
                    // A start element we are not expecting indicates an invalid parameter was passed
                    throw new java.lang.RuntimeException("Unexpected subelement " + reader.getLocalName());
                }


                while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd","contact").equals(reader.getName())){

                    if (!"true".equals(reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil"))){

                        java.lang.String content = reader.getElementText();

                        object.setContact(
                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                    } else {
                        reader.getElementText(); // throw away text nodes if any.
                    }

                    reader.next();

                }  // End of if for expected property start element

                else{
                    // A start element we are not expecting indicates an invalid parameter was passed
                    throw new java.lang.RuntimeException("Unexpected subelement " + reader.getLocalName());
                }


                while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd","id").equals(reader.getName())){

                    java.lang.String content = reader.getElementText();

                    object.setId(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));

                    reader.next();

                }  // End of if for expected property start element

                else{
                    // A start element we are not expecting indicates an invalid parameter was passed
                    throw new java.lang.RuntimeException("Unexpected subelement " + reader.getLocalName());
                }


                while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd","mail").equals(reader.getName())){

                    if (!"true".equals(reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil"))){

                        java.lang.String content = reader.getElementText();

                        object.setMail(
                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                    } else {
                        reader.getElementText(); // throw away text nodes if any.
                    }

                    reader.next();

                }  // End of if for expected property start element

                else{
                    // A start element we are not expecting indicates an invalid parameter was passed
                    throw new java.lang.RuntimeException("Unexpected subelement " + reader.getLocalName());
                }


                while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd","name").equals(reader.getName())){

                    if (!"true".equals(reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil"))){

                        java.lang.String content = reader.getElementText();

                        object.setName(
                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                    } else {
                        reader.getElementText(); // throw away text nodes if any.
                    }

                    reader.next();

                }  // End of if for expected property start element

                else{
                    // A start element we are not expecting indicates an invalid parameter was passed
                    throw new java.lang.RuntimeException("Unexpected subelement " + reader.getLocalName());
                }


                while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd","projectVersions").equals(reader.getName())){



                    // Process the array and step past its final element's end.

                    if ("true".equals(reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil"))){
                        list6.add(null);
                        reader.next();
                    } else {
                        list6.add(eu.sqooss.ws.client.datatypes.WSProjectVersion.Factory.parse(reader));
                    }
                    //loop until we find a start element that is not part of this array
                    boolean loopDone6 = false;
                    while(!loopDone6){
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
                            loopDone6 = true;
                        } else {
                            if (new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd","projectVersions").equals(reader.getName())){

                                if ("true".equals(reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil"))){
                                    list6.add(null);
                                    reader.next();
                                } else {
                                    list6.add(eu.sqooss.ws.client.datatypes.WSProjectVersion.Factory.parse(reader));
                                }
                            }else{
                                loopDone6 = true;
                            }
                        }
                    }
                    // call the converter utility  to convert and set the array
                    object.setProjectVersions((eu.sqooss.ws.client.datatypes.WSProjectVersion[])
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                    eu.sqooss.ws.client.datatypes.WSProjectVersion.class,
                                    list6));

                }  // End of if for expected property start element

                else{
                    // A start element we are not expecting indicates an invalid parameter was passed
                    throw new java.lang.RuntimeException("Unexpected subelement " + reader.getLocalName());
                }


                while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd","repository").equals(reader.getName())){

                    if (!"true".equals(reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil"))){

                        java.lang.String content = reader.getElementText();

                        object.setRepository(
                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                    } else {
                        reader.getElementText(); // throw away text nodes if any.
                    }

                    reader.next();

                }  // End of if for expected property start element

                else{
                    // A start element we are not expecting indicates an invalid parameter was passed
                    throw new java.lang.RuntimeException("Unexpected subelement " + reader.getLocalName());
                }


                while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd","website").equals(reader.getName())){

                    if (!"true".equals(reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil"))){

                        java.lang.String content = reader.getElementText();

                        object.setWebsite(
                                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                    } else {
                        reader.getElementText(); // throw away text nodes if any.
                    }

                    reader.next();

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
