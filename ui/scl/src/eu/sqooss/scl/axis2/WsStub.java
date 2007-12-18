
/**
 * WsStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.2 Apr 27, 2007 (04:35:37 IST)
 */
package eu.sqooss.scl.axis2;



/*
 *  WswsSOAP12Port_httpStub java implementation
 */


public class WsStub extends org.apache.axis2.client.Stub
{
    protected org.apache.axis2.description.AxisOperation[] _operations;

    //hashmaps to keep the fault mapping
    private java.util.HashMap faultExceptionNameMap = new java.util.HashMap();
    private java.util.HashMap faultExceptionClassNameMap = new java.util.HashMap();
    private java.util.HashMap faultMessageMap = new java.util.HashMap();


    private void populateAxisService() throws org.apache.axis2.AxisFault {

        //creating the Service with a unique name
        _service = new org.apache.axis2.description.AxisService("Ws" + this.hashCode());



        //creating the operations
        org.apache.axis2.description.AxisOperation __operation;



        _operations = new org.apache.axis2.description.AxisOperation[1];

        __operation = new org.apache.axis2.description.OutInAxisOperation();


        __operation.setName(new javax.xml.namespace.QName("", "evaluatedProjectsList"));
        _service.addOperation(__operation);



        _operations[0]=__operation;


    }

    //populates the faults
    private void populateFaults(){



    }

    /**
    Constructor that takes in a configContext
     */
    public WsStub(org.apache.axis2.context.ConfigurationContext configurationContext,
            java.lang.String targetEndpoint)
    throws org.apache.axis2.AxisFault {
        //To populate AxisService
        populateAxisService();
        populateFaults();

        _serviceClient = new org.apache.axis2.client.ServiceClient(configurationContext,_service);


        configurationContext = _serviceClient.getServiceContext().getConfigurationContext();

        _serviceClient.getOptions().setTo(new org.apache.axis2.addressing.EndpointReference(
                targetEndpoint));

        //Set the soap version
        _serviceClient.getOptions().setSoapVersionURI(org.apache.axiom.soap.SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);


    }

    /**
     * Default Constructor
     */
    public WsStub() throws org.apache.axis2.AxisFault {

        this("http://localhost:8088//services/ws" );

    }

    /**
     * Constructor taking the target endpoint
     */
    public WsStub(java.lang.String targetEndpoint) throws org.apache.axis2.AxisFault {
        this(null,targetEndpoint);
    }




    /**
     * Auto generated method signature
     * @see eu.sqooss.scl.axis2.WswsSOAP12Port_http#evaluatedProjectsList
     * @param evaluatedProjectsList2

     */


    public  eu.sqooss.scl.axis2.WsStub.EvaluatedProjectsListResponse evaluatedProjectsList(

            eu.sqooss.scl.axis2.WsStub.EvaluatedProjectsList evaluatedProjectsList2)


    throws java.rmi.RemoteException

    {

        try{
            org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[0].getName());
            _operationClient.getOptions().setAction("urn:evaluatedProjectsList");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



            addPropertyToOperationClient(_operationClient,org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,"&");


            // create a message context
            org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();



            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;


            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                    evaluatedProjectsList2,
                    optimizeContent(new javax.xml.namespace.QName("",
                            "evaluatedProjectsList")));

            //adding SOAP soap_headers
            _serviceClient.addHeadersToEnvelope(env);
            // set the message context with that soap envelope
            _messageContext.setEnvelope(env);

            // add the message contxt to the operation client
            _operationClient.addMessageContext(_messageContext);

            //execute the operation client
            _operationClient.execute(true);


            org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient.getMessageContext(
                    org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();


            java.lang.Object object = fromOM(
                    _returnEnv.getBody().getFirstElement() ,
                    eu.sqooss.scl.axis2.WsStub.EvaluatedProjectsListResponse.class,
                    getEnvelopeNamespaces(_returnEnv));
            _messageContext.getTransportOut().getSender().cleanup(_messageContext);

            return (eu.sqooss.scl.axis2.WsStub.EvaluatedProjectsListResponse)object;

        }catch(org.apache.axis2.AxisFault f){

            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt!=null){
                if (faultExceptionNameMap.containsKey(faultElt.getQName())){
                    //make the fault by reflection
                    try{
                        java.lang.String exceptionClassName = (java.lang.String)faultExceptionClassNameMap.get(faultElt.getQName());
                        java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        java.lang.Exception ex=
                            (java.lang.Exception) exceptionClass.newInstance();
                        //message class
                        java.lang.String messageClassName = (java.lang.String)faultMessageMap.get(faultElt.getQName());
                        java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                        java.lang.Object messageObject = fromOM(faultElt,messageClass,null);
                        java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                                new java.lang.Class[]{messageClass});
                        m.invoke(ex,new java.lang.Object[]{messageObject});


                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    }catch(java.lang.ClassCastException e){
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }catch (java.lang.NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }  catch (java.lang.IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }   catch (java.lang.InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                }else{
                    throw f;
                }
            }else{
                throw f;
            }
        }
    }



    /**
     *  A utility method that copies the namepaces from the SOAPEnvelope
     */
    private java.util.Map getEnvelopeNamespaces(org.apache.axiom.soap.SOAPEnvelope env){
        java.util.Map returnMap = new java.util.HashMap();
        java.util.Iterator namespaceIterator = env.getAllDeclaredNamespaces();
        while (namespaceIterator.hasNext()) {
            org.apache.axiom.om.OMNamespace ns = (org.apache.axiom.om.OMNamespace) namespaceIterator.next();
            returnMap.put(ns.getPrefix(),ns.getNamespaceURI());
        }
        return returnMap;
    }



    private javax.xml.namespace.QName[] opNameArray = null;
    private boolean optimizeContent(javax.xml.namespace.QName opName) {


        if (opNameArray == null) {
            return false;
        }
        for (int i = 0; i < opNameArray.length; i++) {
            if (opName.equals(opNameArray[i])) {
                return true;   
            }
        }
        return false;
    }
    //http://localhost:8088//services/ws
    public static class ExtensionMapper{

        public static java.lang.Object getTypeObject(java.lang.String namespaceURI,
                java.lang.String typeName,
                javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{


            if (
                    "http://datatypes.services.web.service.impl.sqooss.eu/xsd".equals(namespaceURI) &&
                    "WSProjectVersion".equals(typeName)){

                return  WSProjectVersion.Factory.parse(reader);


            }


            if (
                    "http://datatypes.services.web.service.impl.sqooss.eu/xsd".equals(namespaceURI) &&
                    "WSStoredProject".equals(typeName)){

                return  WSStoredProject.Factory.parse(reader);


            }


            throw new java.lang.RuntimeException("Unsupported type " + namespaceURI + " " + typeName);
        }

    }


    public static class WSProjectVersion
    implements org.apache.axis2.databinding.ADBBean{
        /* This type was generated from the piece of schema that had
                name = WSProjectVersion
                Namespace URI = http://datatypes.services.web.service.impl.sqooss.eu/xsd
                Namespace Prefix = ns1
         */


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
         * field for Project
         */


        protected long localProject ;


        /**
         * Auto generated getter method
         * @return long
         */
        public  long getProject(){
            return localProject;
        }



        /**
         * Auto generated setter method
         * @param param Project
         */
        public void setProject(long param){

            this.localProject=param;


        }


        /**
         * field for Version
         */


        protected int localVersion ;


        /**
         * Auto generated getter method
         * @return int
         */
        public  int getVersion(){
            return localVersion;
        }



        /**
         * Auto generated setter method
         * @param param Version
         */
        public void setVersion(int param){

            this.localVersion=param;


        }


        /**
         * isReaderMTOMAware
         * @return true if the reader supports MTOM
         */
        public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
            boolean isReaderMTOMAware = false;

            try{
                isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
            }catch(java.lang.IllegalArgumentException e){
                isReaderMTOMAware = false;
            }
            return isReaderMTOMAware;
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

                public void serialize(javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
                    WSProjectVersion.this.serialize(parentQName,factory,xmlWriter);
                }
            };
            return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(
                    parentQName,factory,dataSource);

        }



        public void serialize(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory,
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

                    xmlWriter.writeStartElement(prefix,"id", namespace);
                    xmlWriter.writeNamespace(prefix, namespace);
                    xmlWriter.setPrefix(prefix, namespace);

                } else {
                    xmlWriter.writeStartElement(namespace,"id");
                }

            } else {
                xmlWriter.writeStartElement("id");
            }

            if (localId==java.lang.Long.MIN_VALUE) {

                throw new RuntimeException("id cannot be null!!");

            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localId));
            }

            xmlWriter.writeEndElement();

            namespace = "http://datatypes.services.web.service.impl.sqooss.eu/xsd";
            if (! namespace.equals("")) {
                prefix = xmlWriter.getPrefix(namespace);

                if (prefix == null) {
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();

                    xmlWriter.writeStartElement(prefix,"project", namespace);
                    xmlWriter.writeNamespace(prefix, namespace);
                    xmlWriter.setPrefix(prefix, namespace);

                } else {
                    xmlWriter.writeStartElement(namespace,"project");
                }

            } else {
                xmlWriter.writeStartElement("project");
            }

            if (localProject==java.lang.Long.MIN_VALUE) {

                throw new RuntimeException("project cannot be null!!");

            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localProject));
            }

            xmlWriter.writeEndElement();

            namespace = "http://datatypes.services.web.service.impl.sqooss.eu/xsd";
            if (! namespace.equals("")) {
                prefix = xmlWriter.getPrefix(namespace);

                if (prefix == null) {
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();

                    xmlWriter.writeStartElement(prefix,"version", namespace);
                    xmlWriter.writeNamespace(prefix, namespace);
                    xmlWriter.setPrefix(prefix, namespace);

                } else {
                    xmlWriter.writeStartElement(namespace,"version");
                }

            } else {
                xmlWriter.writeStartElement("version");
            }

            if (localVersion==java.lang.Integer.MIN_VALUE) {

                throw new RuntimeException("version cannot be null!!");

            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localVersion));
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
         *  method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            java.lang.String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix,namespaceURI);
                }
                xmlWriter.writeCharacters(prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
                java.lang.String namespaceURI = null;
                java.lang.String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix,namespaceURI);
                        }
                        stringToWrite.append(prefix).append(":").append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    } else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }

        }


        /**
         * Register a namespace prefix
         */
        private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace) throws javax.xml.stream.XMLStreamException {
            java.lang.String prefix = xmlWriter.getPrefix(namespace);

            if (prefix == null) {
                prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();

                while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }

                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }

            return prefix;
        }



        /**
         * databinding method to get an XML representation of this object
         *
         */
        public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName){



            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();


            elementList.add(new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd",
            "id"));

            elementList.add(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localId));

            elementList.add(new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd",
            "project"));

            elementList.add(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localProject));

            elementList.add(new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd",
            "version"));

            elementList.add(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localVersion));


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
            public static WSProjectVersion parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
                WSProjectVersion object = new WSProjectVersion();

                int event;
                java.lang.String nillableValue = null;
                java.lang.String prefix ="";
                java.lang.String namespaceuri ="";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();


                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","type")!=null){
                        java.lang.String fullTypeName = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                        "type");
                        if (fullTypeName!=null){
                            java.lang.String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1){
                                nsPrefix = fullTypeName.substring(0,fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix==null?"":nsPrefix;

                            java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":")+1);

                            if (!"WSProjectVersion".equals(type)){
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (WSProjectVersion)ExtensionMapper.getTypeObject(
                                        nsUri,type,reader);
                            }


                        }

                    }



                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();




                    reader.next();


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

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd","project").equals(reader.getName())){

                        java.lang.String content = reader.getElementText();

                        object.setProject(
                                org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));

                        reader.next();

                    }  // End of if for expected property start element

                    else{
                        // A start element we are not expecting indicates an invalid parameter was passed
                        throw new java.lang.RuntimeException("Unexpected subelement " + reader.getLocalName());
                    }


                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd","version").equals(reader.getName())){

                        java.lang.String content = reader.getElementText();

                        object.setVersion(
                                org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(content));

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



    public static class WSStoredProject1
    implements org.apache.axis2.databinding.ADBBean{

        public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
                "http://datatypes.services.web.service.impl.sqooss.eu/xsd",
                "WSStoredProject",
        "ns1");



        /**
         * field for WSStoredProject
         */


        protected WSStoredProject localWSStoredProject ;


        /**
         * Auto generated getter method
         * @return WSStoredProject
         */
        public  WSStoredProject getWSStoredProject(){
            return localWSStoredProject;
        }



        /**
         * Auto generated setter method
         * @param param WSStoredProject
         */
        public void setWSStoredProject(WSStoredProject param){

            this.localWSStoredProject=param;


        }


        /**
         * isReaderMTOMAware
         * @return true if the reader supports MTOM
         */
        public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
            boolean isReaderMTOMAware = false;

            try{
                isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
            }catch(java.lang.IllegalArgumentException e){
                isReaderMTOMAware = false;
            }
            return isReaderMTOMAware;
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
                new org.apache.axis2.databinding.ADBDataSource(this,MY_QNAME){

                public void serialize(javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
                    WSStoredProject1.this.serialize(MY_QNAME,factory,xmlWriter);
                }
            };
            return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(
                    MY_QNAME,factory,dataSource);

        }



        public void serialize(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {


            //We can safely assume an element has only one type associated with it

            if (localWSStoredProject==null){
                throw new RuntimeException("Property cannot be null!");
            }
            localWSStoredProject.serialize(MY_QNAME,factory,xmlWriter);


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
         *  method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            java.lang.String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix,namespaceURI);
                }
                xmlWriter.writeCharacters(prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
                java.lang.String namespaceURI = null;
                java.lang.String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix,namespaceURI);
                        }
                        stringToWrite.append(prefix).append(":").append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    } else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }

        }


        /**
         * Register a namespace prefix
         */
        private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace) throws javax.xml.stream.XMLStreamException {
            java.lang.String prefix = xmlWriter.getPrefix(namespace);

            if (prefix == null) {
                prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();

                while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }

                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }

            return prefix;
        }



        /**
         * databinding method to get an XML representation of this object
         *
         */
        public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName){




            //We can safely assume an element has only one type associated with it
            return localWSStoredProject.getPullParser(MY_QNAME);

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
            public static WSStoredProject1 parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
                WSStoredProject1 object = new WSStoredProject1();

                int event;
                java.lang.String nillableValue = null;
                java.lang.String prefix ="";
                java.lang.String namespaceuri ="";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();




                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();



                    while(!reader.isEndElement()) {
                        if (reader.isStartElement() ){

                            if (reader.isStartElement() && new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd","WSStoredProject").equals(reader.getName())){

                                object.setWSStoredProject(WSStoredProject.Factory.parse(reader));

                            }  // End of if for expected property start element

                            else{
                                // A start element we are not expecting indicates an invalid parameter was passed
                                throw new java.lang.RuntimeException("Unexpected subelement " + reader.getLocalName());
                            }

                        } else reader.next();  
                    }  // end of while loop




                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new java.lang.Exception(e);
                }

                return object;
            }

        }//end of factory class



    }



    public static class EvaluatedProjectsListResponse
    implements org.apache.axis2.databinding.ADBBean{

        public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
                "http://services.web.service.sqooss.eu/xsd",
                "evaluatedProjectsListResponse",
        "ns2");



        /**
         * field for _return
         * This was an Array!
         */


        protected WSStoredProject[] local_return ;


        /**
         * Auto generated getter method
         * @return WSStoredProject[]
         */
        public  WSStoredProject[] get_return(){
            return local_return;
        }






        /**
         * validate the array for _return
         */
        protected void validate_return(WSStoredProject[] param){

            if ((param != null) && (param.length < 1)){
                throw new java.lang.RuntimeException();
            }

        }


        /**
         * Auto generated setter method
         * @param param _return
         */
        public void set_return(WSStoredProject[] param){

            validate_return(param);


            this.local_return=param;
        }



        /**
         * Auto generated add method for the array for convenience
         * @param param WSStoredProject
         */
        public void add_return(WSStoredProject param){
            if (local_return == null){
                local_return = new WSStoredProject[]{};
            }



            java.util.List list =
                org.apache.axis2.databinding.utils.ConverterUtil.toList(local_return);
            list.add(param);
            this.local_return =
                (WSStoredProject[])list.toArray(
                        new WSStoredProject[list.size()]);

        }


        /**
         * isReaderMTOMAware
         * @return true if the reader supports MTOM
         */
        public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
            boolean isReaderMTOMAware = false;

            try{
                isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
            }catch(java.lang.IllegalArgumentException e){
                isReaderMTOMAware = false;
            }
            return isReaderMTOMAware;
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
                new org.apache.axis2.databinding.ADBDataSource(this,MY_QNAME){

                public void serialize(javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
                    EvaluatedProjectsListResponse.this.serialize(MY_QNAME,factory,xmlWriter);
                }
            };
            return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(
                    MY_QNAME,factory,dataSource);

        }



        public void serialize(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory,
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
                        local_return[i].serialize(new javax.xml.namespace.QName("http://services.web.service.sqooss.eu/xsd","return"),
                                factory,xmlWriter);
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
                        writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
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
                writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
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
         *  method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            java.lang.String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix,namespaceURI);
                }
                xmlWriter.writeCharacters(prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
                java.lang.String namespaceURI = null;
                java.lang.String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix,namespaceURI);
                        }
                        stringToWrite.append(prefix).append(":").append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    } else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }

        }


        /**
         * Register a namespace prefix
         */
        private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace) throws javax.xml.stream.XMLStreamException {
            java.lang.String prefix = xmlWriter.getPrefix(namespace);

            if (prefix == null) {
                prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();

                while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }

                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }

            return prefix;
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
            public static EvaluatedProjectsListResponse parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
                EvaluatedProjectsListResponse object = new EvaluatedProjectsListResponse();

                int event;
                java.lang.String nillableValue = null;
                java.lang.String prefix ="";
                java.lang.String namespaceuri ="";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();


                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","type")!=null){
                        java.lang.String fullTypeName = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                        "type");
                        if (fullTypeName!=null){
                            java.lang.String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1){
                                nsPrefix = fullTypeName.substring(0,fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix==null?"":nsPrefix;

                            java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":")+1);

                            if (!"evaluatedProjectsListResponse".equals(type)){
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (EvaluatedProjectsListResponse)ExtensionMapper.getTypeObject(
                                        nsUri,type,reader);
                            }


                        }

                    }



                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();




                    reader.next();

                    java.util.ArrayList list1 = new java.util.ArrayList();


                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://services.web.service.sqooss.eu/xsd","return").equals(reader.getName())){



                        // Process the array and step past its final element's end.

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                        if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                            list1.add(null);
                            reader.next();
                        } else {
                            list1.add(WSStoredProject.Factory.parse(reader));
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

                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        list1.add(null);
                                        reader.next();
                                    } else {
                                        list1.add(WSStoredProject.Factory.parse(reader));
                                    }
                                }else{
                                    loopDone1 = true;
                                }
                            }
                        }
                        // call the converter utility  to convert and set the array

                        object.set_return((WSStoredProject[])
                                org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                        WSStoredProject.class,
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



    public static class EvaluatedProjectsList
    implements org.apache.axis2.databinding.ADBBean{

        public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
                "http://services.web.service.sqooss.eu/xsd",
                "evaluatedProjectsList",
        "ns2");



        /**
         * field for UserName
         */


        protected java.lang.String localUserName ;


        /**
         * Auto generated getter method
         * @return java.lang.String
         */
        public  java.lang.String getUserName(){
            return localUserName;
        }



        /**
         * Auto generated setter method
         * @param param UserName
         */
        public void setUserName(java.lang.String param){

            this.localUserName=param;


        }


        /**
         * field for Password
         */


        protected java.lang.String localPassword ;


        /**
         * Auto generated getter method
         * @return java.lang.String
         */
        public  java.lang.String getPassword(){
            return localPassword;
        }



        /**
         * Auto generated setter method
         * @param param Password
         */
        public void setPassword(java.lang.String param){

            this.localPassword=param;


        }


        /**
         * isReaderMTOMAware
         * @return true if the reader supports MTOM
         */
        public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
            boolean isReaderMTOMAware = false;

            try{
                isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
            }catch(java.lang.IllegalArgumentException e){
                isReaderMTOMAware = false;
            }
            return isReaderMTOMAware;
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
                new org.apache.axis2.databinding.ADBDataSource(this,MY_QNAME){

                public void serialize(javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
                    EvaluatedProjectsList.this.serialize(MY_QNAME,factory,xmlWriter);
                }
            };
            return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(
                    MY_QNAME,factory,dataSource);

        }



        public void serialize(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory,
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

                    xmlWriter.writeStartElement(prefix,"userName", namespace);
                    xmlWriter.writeNamespace(prefix, namespace);
                    xmlWriter.setPrefix(prefix, namespace);

                } else {
                    xmlWriter.writeStartElement(namespace,"userName");
                }

            } else {
                xmlWriter.writeStartElement("userName");
            }


            if (localUserName==null){
                // write the nil attribute

                writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);

            }else{


                xmlWriter.writeCharacters(localUserName);

            }

            xmlWriter.writeEndElement();

            namespace = "http://services.web.service.sqooss.eu/xsd";
            if (! namespace.equals("")) {
                prefix = xmlWriter.getPrefix(namespace);

                if (prefix == null) {
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();

                    xmlWriter.writeStartElement(prefix,"password", namespace);
                    xmlWriter.writeNamespace(prefix, namespace);
                    xmlWriter.setPrefix(prefix, namespace);

                } else {
                    xmlWriter.writeStartElement(namespace,"password");
                }

            } else {
                xmlWriter.writeStartElement("password");
            }


            if (localPassword==null){
                // write the nil attribute

                writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);

            }else{


                xmlWriter.writeCharacters(localPassword);

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
         *  method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            java.lang.String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix,namespaceURI);
                }
                xmlWriter.writeCharacters(prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
                java.lang.String namespaceURI = null;
                java.lang.String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix,namespaceURI);
                        }
                        stringToWrite.append(prefix).append(":").append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    } else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }

        }


        /**
         * Register a namespace prefix
         */
        private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace) throws javax.xml.stream.XMLStreamException {
            java.lang.String prefix = xmlWriter.getPrefix(namespace);

            if (prefix == null) {
                prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();

                while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }

                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }

            return prefix;
        }



        /**
         * databinding method to get an XML representation of this object
         *
         */
        public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName){



            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();


            elementList.add(new javax.xml.namespace.QName("http://services.web.service.sqooss.eu/xsd",
            "userName"));

            elementList.add(localUserName==null?null:
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localUserName));

            elementList.add(new javax.xml.namespace.QName("http://services.web.service.sqooss.eu/xsd",
            "password"));

            elementList.add(localPassword==null?null:
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localPassword));


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
            public static EvaluatedProjectsList parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
                EvaluatedProjectsList object = new EvaluatedProjectsList();

                int event;
                java.lang.String nillableValue = null;
                java.lang.String prefix ="";
                java.lang.String namespaceuri ="";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();


                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","type")!=null){
                        java.lang.String fullTypeName = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                        "type");
                        if (fullTypeName!=null){
                            java.lang.String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1){
                                nsPrefix = fullTypeName.substring(0,fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix==null?"":nsPrefix;

                            java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":")+1);

                            if (!"evaluatedProjectsList".equals(type)){
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (EvaluatedProjectsList)ExtensionMapper.getTypeObject(
                                        nsUri,type,reader);
                            }


                        }

                    }



                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();




                    reader.next();


                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://services.web.service.sqooss.eu/xsd","userName").equals(reader.getName())){

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){

                            java.lang.String content = reader.getElementText();

                            object.setUserName(
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

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://services.web.service.sqooss.eu/xsd","password").equals(reader.getName())){

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){

                            java.lang.String content = reader.getElementText();

                            object.setPassword(
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



    public static class WSStoredProject
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


        protected WSProjectVersion[] localProjectVersions ;


        /**
         * Auto generated getter method
         * @return WSProjectVersion[]
         */
        public  WSProjectVersion[] getProjectVersions(){
            return localProjectVersions;
        }






        /**
         * validate the array for ProjectVersions
         */
        protected void validateProjectVersions(WSProjectVersion[] param){

            if ((param != null) && (param.length < 1)){
                throw new java.lang.RuntimeException();
            }

        }


        /**
         * Auto generated setter method
         * @param param ProjectVersions
         */
        public void setProjectVersions(WSProjectVersion[] param){

            validateProjectVersions(param);


            this.localProjectVersions=param;
        }



        /**
         * Auto generated add method for the array for convenience
         * @param param WSProjectVersion
         */
        public void addProjectVersions(WSProjectVersion param){
            if (localProjectVersions == null){
                localProjectVersions = new WSProjectVersion[]{};
            }



            java.util.List list =
                org.apache.axis2.databinding.utils.ConverterUtil.toList(localProjectVersions);
            list.add(param);
            this.localProjectVersions =
                (WSProjectVersion[])list.toArray(
                        new WSProjectVersion[list.size()]);

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
         * isReaderMTOMAware
         * @return true if the reader supports MTOM
         */
        public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
            boolean isReaderMTOMAware = false;
            try{
                isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
            }catch(java.lang.IllegalArgumentException e){
                isReaderMTOMAware = false;
            }
            return isReaderMTOMAware;
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

                public void serialize(javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
                    WSStoredProject.this.serialize(parentQName,factory,xmlWriter);
                }
            };
            return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(
                    parentQName,factory,dataSource);

        }



        public void serialize(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory,
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
                writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
            }else{
                xmlWriter.writeCharacters(localBugs);
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

                writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);

            }else{


                xmlWriter.writeCharacters(localContact);

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

            if (localId==java.lang.Long.MIN_VALUE) {

                throw new RuntimeException("id cannot be null!!");

            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localId));
            }

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

                writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);

            }else{


                xmlWriter.writeCharacters(localMail);

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

                writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);

            }else{


                xmlWriter.writeCharacters(localName);

            }

            xmlWriter.writeEndElement();

            if (localProjectVersions!=null){
                for (int i = 0;i < localProjectVersions.length;i++){
                    if (localProjectVersions[i] != null){
                        localProjectVersions[i].serialize(new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd","projectVersions"),
                                factory,xmlWriter);
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
                        writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
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
                writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
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

                writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);

            }else{


                xmlWriter.writeCharacters(localRepository);

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

                writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);

            }else{


                xmlWriter.writeCharacters(localWebsite);

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
         *  method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            java.lang.String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix,namespaceURI);
                }
                xmlWriter.writeCharacters(prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
                java.lang.String namespaceURI = null;
                java.lang.String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix,namespaceURI);
                        }
                        stringToWrite.append(prefix).append(":").append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    } else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }

        }


        /**
         * Register a namespace prefix
         */
        private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace) throws javax.xml.stream.XMLStreamException {
            java.lang.String prefix = xmlWriter.getPrefix(namespace);

            if (prefix == null) {
                prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();

                while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }

                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }

            return prefix;
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
                java.lang.String nillableValue = null;
                java.lang.String prefix ="";
                java.lang.String namespaceuri ="";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();


                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","type")!=null){
                        java.lang.String fullTypeName = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                        "type");
                        if (fullTypeName!=null){
                            java.lang.String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1){
                                nsPrefix = fullTypeName.substring(0,fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix==null?"":nsPrefix;

                            java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":")+1);

                            if (!"WSStoredProject".equals(type)){
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (WSStoredProject)ExtensionMapper.getTypeObject(
                                        nsUri,type,reader);
                            }


                        }

                    }



                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();




                    reader.next();

                    java.util.ArrayList list6 = new java.util.ArrayList();


                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd","bugs").equals(reader.getName())){

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){

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

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){

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

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){

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

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){

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

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                        if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                            list6.add(null);
                            reader.next();
                        } else {
                            list6.add(WSProjectVersion.Factory.parse(reader));
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

                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        list6.add(null);
                                        reader.next();
                                    } else {
                                        list6.add(WSProjectVersion.Factory.parse(reader));
                                    }
                                }else{
                                    loopDone6 = true;
                                }
                            }
                        }
                        // call the converter utility  to convert and set the array

                        object.setProjectVersions((WSProjectVersion[])
                                org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                        WSProjectVersion.class,
                                        list6));

                    }  // End of if for expected property start element

                    else{
                        // A start element we are not expecting indicates an invalid parameter was passed
                        throw new java.lang.RuntimeException("Unexpected subelement " + reader.getLocalName());
                    }


                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd","repository").equals(reader.getName())){

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){

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

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                        if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){

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



    public static class WSProjectVersion0
    implements org.apache.axis2.databinding.ADBBean{

        public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
                "http://datatypes.services.web.service.impl.sqooss.eu/xsd",
                "WSProjectVersion",
        "ns1");



        /**
         * field for WSProjectVersion
         */


        protected WSProjectVersion localWSProjectVersion ;


        /**
         * Auto generated getter method
         * @return WSProjectVersion
         */
        public  WSProjectVersion getWSProjectVersion(){
            return localWSProjectVersion;
        }



        /**
         * Auto generated setter method
         * @param param WSProjectVersion
         */
        public void setWSProjectVersion(WSProjectVersion param){

            this.localWSProjectVersion=param;


        }


        /**
         * isReaderMTOMAware
         * @return true if the reader supports MTOM
         */
        public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
            boolean isReaderMTOMAware = false;

            try{
                isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
            }catch(java.lang.IllegalArgumentException e){
                isReaderMTOMAware = false;
            }
            return isReaderMTOMAware;
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
                new org.apache.axis2.databinding.ADBDataSource(this,MY_QNAME){

                public void serialize(javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
                    WSProjectVersion0.this.serialize(MY_QNAME,factory,xmlWriter);
                }
            };
            return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(
                    MY_QNAME,factory,dataSource);

        }



        public void serialize(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {


            //We can safely assume an element has only one type associated with it

            if (localWSProjectVersion==null){
                throw new RuntimeException("Property cannot be null!");
            }
            localWSProjectVersion.serialize(MY_QNAME,factory,xmlWriter);


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
         *  method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            java.lang.String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix,namespaceURI);
                }
                xmlWriter.writeCharacters(prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
                java.lang.String namespaceURI = null;
                java.lang.String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix,namespaceURI);
                        }
                        stringToWrite.append(prefix).append(":").append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    } else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }

        }


        /**
         * Register a namespace prefix
         */
        private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace) throws javax.xml.stream.XMLStreamException {
            java.lang.String prefix = xmlWriter.getPrefix(namespace);

            if (prefix == null) {
                prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();

                while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }

                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }

            return prefix;
        }



        /**
         * databinding method to get an XML representation of this object
         *
         */
        public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName){




            //We can safely assume an element has only one type associated with it
            return localWSProjectVersion.getPullParser(MY_QNAME);

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
            public static WSProjectVersion0 parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
                WSProjectVersion0 object = new WSProjectVersion0();

                int event;
                java.lang.String nillableValue = null;
                java.lang.String prefix ="";
                java.lang.String namespaceuri ="";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();
                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();
                    while(!reader.isEndElement()) {
                        if (reader.isStartElement() ){
                            if (reader.isStartElement() && new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd","WSProjectVersion").equals(reader.getName())){
                                object.setWSProjectVersion(WSProjectVersion.Factory.parse(reader));
                            }  // End of if for expected property start element
                            else{
                                // A start element we are not expecting indicates an invalid parameter was passed
                                throw new java.lang.RuntimeException("Unexpected subelement " + reader.getLocalName());
                            }
                        } else reader.next();  
                    }  // end of while loop
                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new java.lang.Exception(e);
                }
                return object;
            }
        }//end of factory class
    }
    
    private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.scl.axis2.WsStub.EvaluatedProjectsList param, boolean optimizeContent){
        return param.getOMElement(eu.sqooss.scl.axis2.WsStub.EvaluatedProjectsList.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    
    private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.scl.axis2.WsStub.EvaluatedProjectsListResponse param, boolean optimizeContent){

        return param.getOMElement(eu.sqooss.scl.axis2.WsStub.EvaluatedProjectsListResponse.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    
    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, eu.sqooss.scl.axis2.WsStub.EvaluatedProjectsList param, boolean optimizeContent){
        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

        emptyEnvelope.getBody().addChild(param.getOMElement(eu.sqooss.scl.axis2.WsStub.EvaluatedProjectsList.MY_QNAME,factory));

        return emptyEnvelope;
    }
    /* methods to provide back word compatibility */

    /**
     *  get the default envelope
     */
     private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory){
        return factory.getDefaultEnvelope();
    }
     
    private  java.lang.Object fromOM(
            org.apache.axiom.om.OMElement param,
            java.lang.Class type,
            java.util.Map extraNamespaces){

        try {

            if (eu.sqooss.scl.axis2.WsStub.EvaluatedProjectsList.class.equals(type)){
                return eu.sqooss.scl.axis2.WsStub.EvaluatedProjectsList.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }

            if (eu.sqooss.scl.axis2.WsStub.EvaluatedProjectsListResponse.class.equals(type)){
                return eu.sqooss.scl.axis2.WsStub.EvaluatedProjectsListResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    private void setOpNameArray(){
        opNameArray = null;
    }
}
