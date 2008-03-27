
            /**
            * ExtensionMapper.java
            *
            * This file was auto-generated from WSDL
            * by the Apache Axis2 version: #axisVersion# #today#
            */

            package eu.sqooss.impl.service.web.services.datatypes.xsd;
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
                   
                            return  eu.sqooss.ws.client.datatypes.WSMetric.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://datatypes.services.web.service.impl.sqooss.eu/xsd".equals(namespaceURI) &&
                  "WSProjectVersion".equals(typeName)){
                   
                            return  eu.sqooss.ws.client.datatypes.WSProjectVersion.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://datatypes.services.web.service.impl.sqooss.eu/xsd".equals(namespaceURI) &&
                  "WSProjectFile".equals(typeName)){
                   
                            return  eu.sqooss.ws.client.datatypes.WSProjectFile.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://datatypes.services.web.service.impl.sqooss.eu/xsd".equals(namespaceURI) &&
                  "WSMetricType".equals(typeName)){
                   
                            return  eu.sqooss.ws.client.datatypes.WSMetricType.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://datatypes.services.web.service.impl.sqooss.eu/xsd".equals(namespaceURI) &&
                  "WSStoredProject".equals(typeName)){
                   
                            return  eu.sqooss.ws.client.datatypes.WSStoredProject.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://datatypes.services.web.service.impl.sqooss.eu/xsd".equals(namespaceURI) &&
                  "WSUser".equals(typeName)){
                   
                            return  eu.sqooss.ws.client.datatypes.WSUser.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://datatypes.services.web.service.impl.sqooss.eu/xsd".equals(namespaceURI) &&
                  "WSUserGroup".equals(typeName)){
                   
                            return  eu.sqooss.ws.client.datatypes.WSUserGroup.Factory.parse(reader);
                        

                  }

              
             throw new java.lang.RuntimeException("Unsupported type " + namespaceURI + " " + typeName);
          }

        }
    