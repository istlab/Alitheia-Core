
            /**
            * WSMetricsResultRequest.java
            *
            * This file was auto-generated from WSDL
            * by the Apache Axis2 version: #axisVersion# #today#
            */

            package eu.sqooss.ws.client.datatypes;
            /**
            *  WSMetricsResultRequest bean class
            */
        
        public  class WSMetricsResultRequest
        implements org.apache.axis2.databinding.ADBBean{
        /* This type was generated from the piece of schema that had
                name = WSMetricsResultRequest
                Namespace URI = http://datatypes.services.web.service.impl.sqooss.eu/xsd
                Namespace Prefix = ns1
                */
            

                        /**
                        * field for DaObjectId
                        */

                        protected long localDaObjectId ;
                        

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getDaObjectId(){
                               return localDaObjectId;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param DaObjectId
                               */
                               public void setDaObjectId(long param){
                            
                                    this.localDaObjectId=param;
                            

                               }
                            

                        /**
                        * field for FileGroup
                        */

                        protected boolean localFileGroup ;
                        

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getFileGroup(){
                               return localFileGroup;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param FileGroup
                               */
                               public void setFileGroup(boolean param){
                            
                                    this.localFileGroup=param;
                            

                               }
                            

                        /**
                        * field for Mnemonics
                        * This was an Array!
                        */

                        protected java.lang.String[] localMnemonics ;
                        

                           /**
                           * Auto generated getter method
                           * @return java.lang.String[]
                           */
                           public  java.lang.String[] getMnemonics(){
                               return localMnemonics;
                           }

                           
                        


                               
                              /**
                               * validate the array for Mnemonics
                               */
                              protected void validateMnemonics(java.lang.String[] param){
                             
                              if ((param != null) && (param.length < 1)){
                                throw new java.lang.RuntimeException();
                              }
                              
                              }


                             /**
                              * Auto generated setter method
                              * @param param Mnemonics
                              */
                              public void setMnemonics(java.lang.String[] param){
                              
                                   validateMnemonics(param);

                               
                                      this.localMnemonics=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param java.lang.String
                             */
                             public void addMnemonics(java.lang.String param){
                                   if (localMnemonics == null){
                                   localMnemonics = new java.lang.String[]{};
                                   }

                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localMnemonics);
                               list.add(param);
                               this.localMnemonics =
                             (java.lang.String[])list.toArray(
                            new java.lang.String[list.size()]);

                             }
                             

                        /**
                        * field for ProjectFile
                        */

                        protected boolean localProjectFile ;
                        

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getProjectFile(){
                               return localProjectFile;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ProjectFile
                               */
                               public void setProjectFile(boolean param){
                            
                                    this.localProjectFile=param;
                            

                               }
                            

                        /**
                        * field for ProjectVersion
                        */

                        protected boolean localProjectVersion ;
                        

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getProjectVersion(){
                               return localProjectVersion;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ProjectVersion
                               */
                               public void setProjectVersion(boolean param){
                            
                                    this.localProjectVersion=param;
                            

                               }
                            

                        /**
                        * field for StoredProject
                        */

                        protected boolean localStoredProject ;
                        

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getStoredProject(){
                               return localStoredProject;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param StoredProject
                               */
                               public void setStoredProject(boolean param){
                            
                                    this.localStoredProject=param;
                            

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

                                            xmlWriter.writeStartElement(prefix,"daObjectId", namespace);
                                            xmlWriter.writeNamespace(prefix, namespace);
                                            xmlWriter.setPrefix(prefix, namespace);

                                        } else {
                                            xmlWriter.writeStartElement(namespace,"daObjectId");
                                        }

                                    } else {
                                        xmlWriter.writeStartElement("daObjectId");
                                    }
                                
                                       xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localDaObjectId));
                                    
                                   xmlWriter.writeEndElement();
                             
                                    namespace = "http://datatypes.services.web.service.impl.sqooss.eu/xsd";
                                    if (! namespace.equals("")) {
                                        prefix = xmlWriter.getPrefix(namespace);

                                        if (prefix == null) {
                                            prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();

                                            xmlWriter.writeStartElement(prefix,"fileGroup", namespace);
                                            xmlWriter.writeNamespace(prefix, namespace);
                                            xmlWriter.setPrefix(prefix, namespace);

                                        } else {
                                            xmlWriter.writeStartElement(namespace,"fileGroup");
                                        }

                                    } else {
                                        xmlWriter.writeStartElement("fileGroup");
                                    }
                                
                                       xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFileGroup));
                                    
                                   xmlWriter.writeEndElement();
                             
                             if (localMnemonics!=null) {
                                   for (int i = 0;i < localMnemonics.length;i++){
                                        
                                            if (localMnemonics[i] != null){
                                                namespace = "http://datatypes.services.web.service.impl.sqooss.eu/xsd";
                                                if (! namespace.equals("")) {
                                                    prefix = xmlWriter.getPrefix(namespace);

                                                    if (prefix == null) {
                                                        prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();

                                                        xmlWriter.writeStartElement(prefix,"mnemonics", namespace);
                                                        xmlWriter.writeNamespace(prefix, namespace);
                                                        xmlWriter.setPrefix(prefix, namespace);

                                                    } else {
                                                        xmlWriter.writeStartElement(namespace,"mnemonics");
                                                    }

                                                } else {
                                                    xmlWriter.writeStartElement("mnemonics");
                                                }
                                                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMnemonics[i]));
                                                xmlWriter.writeEndElement();

                                            } else {
                                               
                                                       // write null attribute
                                                        namespace = "http://datatypes.services.web.service.impl.sqooss.eu/xsd";
                                                        if (! namespace.equals("")) {
                                                            prefix = xmlWriter.getPrefix(namespace);

                                                            if (prefix == null) {
                                                                prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();

                                                                xmlWriter.writeStartElement(prefix,"mnemonics", namespace);
                                                                xmlWriter.writeNamespace(prefix, namespace);
                                                                xmlWriter.setPrefix(prefix, namespace);

                                                            } else {
                                                                xmlWriter.writeStartElement(namespace,"mnemonics");
                                                            }

                                                        } else {
                                                            xmlWriter.writeStartElement("mnemonics");
                                                        }
                                                        writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","true",xmlWriter);
                                                        xmlWriter.writeEndElement();
                                                   
                                            }
                                        
                                   }
                             } else {
                                 
                                         // write the null attribute
                                        // write null attribute
                                            java.lang.String namespace2 = "http://datatypes.services.web.service.impl.sqooss.eu/xsd";
                                            if (! namespace2.equals("")) {
                                                java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

                                                if (prefix2 == null) {
                                                    prefix2 = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();

                                                    xmlWriter.writeStartElement(prefix2,"mnemonics", namespace2);
                                                    xmlWriter.writeNamespace(prefix2, namespace2);
                                                    xmlWriter.setPrefix(prefix2, namespace2);

                                                } else {
                                                    xmlWriter.writeStartElement(namespace2,"mnemonics");
                                                }

                                            } else {
                                                xmlWriter.writeStartElement("mnemonics");
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

                                            xmlWriter.writeStartElement(prefix,"projectFile", namespace);
                                            xmlWriter.writeNamespace(prefix, namespace);
                                            xmlWriter.setPrefix(prefix, namespace);

                                        } else {
                                            xmlWriter.writeStartElement(namespace,"projectFile");
                                        }

                                    } else {
                                        xmlWriter.writeStartElement("projectFile");
                                    }
                                
                                       xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localProjectFile));
                                    
                                   xmlWriter.writeEndElement();
                             
                                    namespace = "http://datatypes.services.web.service.impl.sqooss.eu/xsd";
                                    if (! namespace.equals("")) {
                                        prefix = xmlWriter.getPrefix(namespace);

                                        if (prefix == null) {
                                            prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();

                                            xmlWriter.writeStartElement(prefix,"projectVersion", namespace);
                                            xmlWriter.writeNamespace(prefix, namespace);
                                            xmlWriter.setPrefix(prefix, namespace);

                                        } else {
                                            xmlWriter.writeStartElement(namespace,"projectVersion");
                                        }

                                    } else {
                                        xmlWriter.writeStartElement("projectVersion");
                                    }
                                
                                       xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localProjectVersion));
                                    
                                   xmlWriter.writeEndElement();
                             
                                    namespace = "http://datatypes.services.web.service.impl.sqooss.eu/xsd";
                                    if (! namespace.equals("")) {
                                        prefix = xmlWriter.getPrefix(namespace);

                                        if (prefix == null) {
                                            prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();

                                            xmlWriter.writeStartElement(prefix,"storedProject", namespace);
                                            xmlWriter.writeNamespace(prefix, namespace);
                                            xmlWriter.setPrefix(prefix, namespace);

                                        } else {
                                            xmlWriter.writeStartElement(namespace,"storedProject");
                                        }

                                    } else {
                                        xmlWriter.writeStartElement("storedProject");
                                    }
                                
                                       xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localStoredProject));
                                    
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
                                                                      "daObjectId"));
                            
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localDaObjectId));
                            
                             elementList.add(new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd",
                                                                      "fileGroup"));
                            
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFileGroup));
                            
                            if (localMnemonics!=null){
                                  for (int i = 0;i < localMnemonics.length;i++){
                                      
                                         if (localMnemonics[i] != null){
                                          elementList.add(new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd",
                                                                              "mnemonics"));
                                          elementList.add(
                                          org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMnemonics[i]));
                                          } else {
                                             
                                                    elementList.add(new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd",
                                                                              "mnemonics"));
                                                    elementList.add(null);
                                                
                                          }
                                      

                                  }
                            } else {
                              
                                    elementList.add(new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd",
                                                                              "mnemonics"));
                                    elementList.add(null);
                                
                            }

                        
                             elementList.add(new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd",
                                                                      "projectFile"));
                            
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localProjectFile));
                            
                             elementList.add(new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd",
                                                                      "projectVersion"));
                            
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localProjectVersion));
                            
                             elementList.add(new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd",
                                                                      "storedProject"));
                            
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localStoredProject));
                            

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
        public static WSMetricsResultRequest parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            WSMetricsResultRequest object = new WSMetricsResultRequest();
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
                    if (!"WSMetricsResultRequest".equals(type)){
                        //find namespace for the prefix
                        java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                        return (WSMetricsResultRequest)eu.sqooss.impl.service.web.services.datatypes.xsd.ExtensionMapper.getTypeObject(
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
                
                        java.util.ArrayList list3 = new java.util.ArrayList();
                    
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd","daObjectId").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setDaObjectId(
                                        org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                            
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new java.lang.RuntimeException("Unexpected subelement " + reader.getLocalName());
                                }
                            
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd","fileGroup").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setFileGroup(
                                        org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                            
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new java.lang.RuntimeException("Unexpected subelement " + reader.getLocalName());
                                }
                            
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd","mnemonics").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    
                                              if ("true".equals(reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil"))){
                                                  list3.add(null);
                                                  reader.next();
                                              } else {
                                            list3.add(reader.getElementText());
                                            }
                                            //loop until we find a start element that is not part of this array
                                            boolean loopDone3 = false;
                                            while(!loopDone3){
                                                // Ensure we are at the EndElement
                                                while (!reader.isEndElement()){
                                                    reader.next();
                                                }
                                                // Step out of this element
                                                reader.next();
                                                // Step to next element event.
                                                while (!reader.isStartElement() && !reader.isEndElement())
                                                    reader.next();
                                                if (reader.isEndElement()){
                                                    //two continuous end elements means we are exiting the xml structure
                                                    loopDone3 = true;
                                                } else {
                                                    if (new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd","mnemonics").equals(reader.getName())){
                                                         
                                                          if ("true".equals(reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil"))){
                                                              list3.add(null);
                                                              reader.next();
                                                          } else {
                                                        list3.add(reader.getElementText());
                                                        }
                                                    }else{
                                                        loopDone3 = true;
                                                    }
                                                }
                                            }
                                            // call the converter utility  to convert and set the array
                                            object.setMnemonics((java.lang.String[])
                                                org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                                    java.lang.String.class,
                                                    list3));
                                        
                              }  // End of if for expected property start element
                            
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new java.lang.RuntimeException("Unexpected subelement " + reader.getLocalName());
                                }
                            
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd","projectFile").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setProjectFile(
                                        org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                            
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new java.lang.RuntimeException("Unexpected subelement " + reader.getLocalName());
                                }
                            
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd","projectVersion").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setProjectVersion(
                                        org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                            
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new java.lang.RuntimeException("Unexpected subelement " + reader.getLocalName());
                                }
                            
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("http://datatypes.services.web.service.impl.sqooss.eu/xsd","storedProject").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setStoredProject(
                                        org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
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
           
          