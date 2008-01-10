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
 * ModifyUser.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: #axisVersion# #today#
 */

package eu.sqooss.scl.axis2.ws;
/**
 *  ModifyUser bean class
 */

public  class ModifyUser
implements org.apache.axis2.databinding.ADBBean{

    public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
            "http://services.web.service.sqooss.eu/xsd",
            "modifyUser",
    "ns2");



    /**
     * field for UserNameForAccess
     */

    protected java.lang.String localUserNameForAccess ;


    /**
     * Auto generated getter method
     * @return java.lang.String
     */
    public  java.lang.String getUserNameForAccess(){
        return localUserNameForAccess;
    }



    /**
     * Auto generated setter method
     * @param param UserNameForAccess
     */
    public void setUserNameForAccess(java.lang.String param){

        this.localUserNameForAccess=param;


    }


    /**
     * field for PasswordForAccess
     */

    protected java.lang.String localPasswordForAccess ;


    /**
     * Auto generated getter method
     * @return java.lang.String
     */
    public  java.lang.String getPasswordForAccess(){
        return localPasswordForAccess;
    }



    /**
     * Auto generated setter method
     * @param param PasswordForAccess
     */
    public void setPasswordForAccess(java.lang.String param){

        this.localPasswordForAccess=param;


    }


    /**
     * field for NewUserName
     */

    protected java.lang.String localNewUserName ;


    /**
     * Auto generated getter method
     * @return java.lang.String
     */
    public  java.lang.String getNewUserName(){
        return localNewUserName;
    }



    /**
     * Auto generated setter method
     * @param param NewUserName
     */
    public void setNewUserName(java.lang.String param){

        this.localNewUserName=param;


    }


    /**
     * field for NewNames
     */

    protected java.lang.String localNewNames ;


    /**
     * Auto generated getter method
     * @return java.lang.String
     */
    public  java.lang.String getNewNames(){
        return localNewNames;
    }



    /**
     * Auto generated setter method
     * @param param NewNames
     */
    public void setNewNames(java.lang.String param){

        this.localNewNames=param;


    }


    /**
     * field for NewPassword
     */

    protected java.lang.String localNewPassword ;


    /**
     * Auto generated getter method
     * @return java.lang.String
     */
    public  java.lang.String getNewPassword(){
        return localNewPassword;
    }



    /**
     * Auto generated setter method
     * @param param NewPassword
     */
    public void setNewPassword(java.lang.String param){

        this.localNewPassword=param;


    }


    /**
     * field for NewUserClass
     */

    protected java.lang.String localNewUserClass ;


    /**
     * Auto generated getter method
     * @return java.lang.String
     */
    public  java.lang.String getNewUserClass(){
        return localNewUserClass;
    }



    /**
     * Auto generated setter method
     * @param param NewUserClass
     */
    public void setNewUserClass(java.lang.String param){

        this.localNewUserClass=param;


    }


    /**
     * field for NewOtherInfo
     */

    protected java.lang.String localNewOtherInfo ;


    /**
     * Auto generated getter method
     * @return java.lang.String
     */
    public  java.lang.String getNewOtherInfo(){
        return localNewOtherInfo;
    }



    /**
     * Auto generated setter method
     * @param param NewOtherInfo
     */
    public void setNewOtherInfo(java.lang.String param){

        this.localNewOtherInfo=param;


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



                namespace = "http://services.web.service.sqooss.eu/xsd";
                if (! namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();

                        xmlWriter.writeStartElement(prefix,"userNameForAccess", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    } else {
                        xmlWriter.writeStartElement(namespace,"userNameForAccess");
                    }

                } else {
                    xmlWriter.writeStartElement("userNameForAccess");
                }


                if (localUserNameForAccess==null){
                    // write the nil attribute

                    writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","true",xmlWriter);

                }else{


                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localUserNameForAccess));

                }

                xmlWriter.writeEndElement();

                namespace = "http://services.web.service.sqooss.eu/xsd";
                if (! namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();

                        xmlWriter.writeStartElement(prefix,"passwordForAccess", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    } else {
                        xmlWriter.writeStartElement(namespace,"passwordForAccess");
                    }

                } else {
                    xmlWriter.writeStartElement("passwordForAccess");
                }


                if (localPasswordForAccess==null){
                    // write the nil attribute

                    writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","true",xmlWriter);

                }else{


                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localPasswordForAccess));

                }

                xmlWriter.writeEndElement();

                namespace = "http://services.web.service.sqooss.eu/xsd";
                if (! namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();

                        xmlWriter.writeStartElement(prefix,"newUserName", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    } else {
                        xmlWriter.writeStartElement(namespace,"newUserName");
                    }

                } else {
                    xmlWriter.writeStartElement("newUserName");
                }


                if (localNewUserName==null){
                    // write the nil attribute

                    writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","true",xmlWriter);

                }else{


                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewUserName));

                }

                xmlWriter.writeEndElement();

                namespace = "http://services.web.service.sqooss.eu/xsd";
                if (! namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();

                        xmlWriter.writeStartElement(prefix,"newNames", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    } else {
                        xmlWriter.writeStartElement(namespace,"newNames");
                    }

                } else {
                    xmlWriter.writeStartElement("newNames");
                }


                if (localNewNames==null){
                    // write the nil attribute

                    writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","true",xmlWriter);

                }else{


                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewNames));

                }

                xmlWriter.writeEndElement();

                namespace = "http://services.web.service.sqooss.eu/xsd";
                if (! namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();

                        xmlWriter.writeStartElement(prefix,"newPassword", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    } else {
                        xmlWriter.writeStartElement(namespace,"newPassword");
                    }

                } else {
                    xmlWriter.writeStartElement("newPassword");
                }


                if (localNewPassword==null){
                    // write the nil attribute

                    writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","true",xmlWriter);

                }else{


                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewPassword));

                }

                xmlWriter.writeEndElement();

                namespace = "http://services.web.service.sqooss.eu/xsd";
                if (! namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();

                        xmlWriter.writeStartElement(prefix,"newUserClass", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    } else {
                        xmlWriter.writeStartElement(namespace,"newUserClass");
                    }

                } else {
                    xmlWriter.writeStartElement("newUserClass");
                }


                if (localNewUserClass==null){
                    // write the nil attribute

                    writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","true",xmlWriter);

                }else{


                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewUserClass));

                }

                xmlWriter.writeEndElement();

                namespace = "http://services.web.service.sqooss.eu/xsd";
                if (! namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();

                        xmlWriter.writeStartElement(prefix,"newOtherInfo", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    } else {
                        xmlWriter.writeStartElement(namespace,"newOtherInfo");
                    }

                } else {
                    xmlWriter.writeStartElement("newOtherInfo");
                }


                if (localNewOtherInfo==null){
                    // write the nil attribute

                    writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","true",xmlWriter);

                }else{


                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewOtherInfo));

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
                MY_QNAME,factory,dataSource);

    }


    /**
     * databinding method to get an XML representation of this object
     *
     */
    public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName){



        java.util.ArrayList elementList = new java.util.ArrayList();
        java.util.ArrayList attribList = new java.util.ArrayList();


        elementList.add(new javax.xml.namespace.QName("http://services.web.service.sqooss.eu/xsd",
        "userNameForAccess"));

        elementList.add(localUserNameForAccess==null?null:
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localUserNameForAccess));

        elementList.add(new javax.xml.namespace.QName("http://services.web.service.sqooss.eu/xsd",
        "passwordForAccess"));

        elementList.add(localPasswordForAccess==null?null:
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localPasswordForAccess));

        elementList.add(new javax.xml.namespace.QName("http://services.web.service.sqooss.eu/xsd",
        "newUserName"));

        elementList.add(localNewUserName==null?null:
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewUserName));

        elementList.add(new javax.xml.namespace.QName("http://services.web.service.sqooss.eu/xsd",
        "newNames"));

        elementList.add(localNewNames==null?null:
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewNames));

        elementList.add(new javax.xml.namespace.QName("http://services.web.service.sqooss.eu/xsd",
        "newPassword"));

        elementList.add(localNewPassword==null?null:
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewPassword));

        elementList.add(new javax.xml.namespace.QName("http://services.web.service.sqooss.eu/xsd",
        "newUserClass"));

        elementList.add(localNewUserClass==null?null:
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewUserClass));

        elementList.add(new javax.xml.namespace.QName("http://services.web.service.sqooss.eu/xsd",
        "newOtherInfo"));

        elementList.add(localNewOtherInfo==null?null:
            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewOtherInfo));


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
        public static ModifyUser parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            ModifyUser object = new ModifyUser();
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
                        if (!"modifyUser".equals(type)){
                            //find namespace for the prefix
                            java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (ModifyUser)eu.sqooss.scl.axis2.ws.ExtensionMapper.getTypeObject(
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


                while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://services.web.service.sqooss.eu/xsd","userNameForAccess").equals(reader.getName())){

                    if (!"true".equals(reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil"))){

                        java.lang.String content = reader.getElementText();

                        object.setUserNameForAccess(
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

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://services.web.service.sqooss.eu/xsd","passwordForAccess").equals(reader.getName())){

                    if (!"true".equals(reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil"))){

                        java.lang.String content = reader.getElementText();

                        object.setPasswordForAccess(
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

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://services.web.service.sqooss.eu/xsd","newUserName").equals(reader.getName())){

                    if (!"true".equals(reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil"))){

                        java.lang.String content = reader.getElementText();

                        object.setNewUserName(
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

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://services.web.service.sqooss.eu/xsd","newNames").equals(reader.getName())){

                    if (!"true".equals(reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil"))){

                        java.lang.String content = reader.getElementText();

                        object.setNewNames(
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

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://services.web.service.sqooss.eu/xsd","newPassword").equals(reader.getName())){

                    if (!"true".equals(reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil"))){

                        java.lang.String content = reader.getElementText();

                        object.setNewPassword(
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

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://services.web.service.sqooss.eu/xsd","newUserClass").equals(reader.getName())){

                    if (!"true".equals(reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil"))){

                        java.lang.String content = reader.getElementText();

                        object.setNewUserClass(
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

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://services.web.service.sqooss.eu/xsd","newOtherInfo").equals(reader.getName())){

                    if (!"true".equals(reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil"))){

                        java.lang.String content = reader.getElementText();

                        object.setNewOtherInfo(
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
