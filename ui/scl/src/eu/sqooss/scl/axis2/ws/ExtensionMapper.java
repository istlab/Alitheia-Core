
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
                  "WSMetricType".equals(typeName)){
                   
                            return  eu.sqooss.scl.axis2.datatypes.WSMetricType.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://datatypes.services.web.service.impl.sqooss.eu/xsd".equals(namespaceURI) &&
                  "WSStoredProject".equals(typeName)){
                   
                            return  eu.sqooss.scl.axis2.datatypes.WSStoredProject.Factory.parse(reader);
                        

                  }

              
             throw new java.lang.RuntimeException("Unsupported type " + namespaceURI + " " + typeName);
          }

        }
    