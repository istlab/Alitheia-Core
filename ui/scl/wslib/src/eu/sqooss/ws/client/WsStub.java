
        /**
        * WsStub.java
        *
        * This file was auto-generated from WSDL
        * by the Apache Axis2 version: 1.1 Nov 13, 2006 (07:31:44 LKT)
        */
        package eu.sqooss.ws.client;

        

        /*
        *  WsStub java implementation
        */

        
        public class WsStub extends org.apache.axis2.client.Stub
        implements Ws{
        protected org.apache.axis2.description.AxisOperation[] _operations;

        //hashmaps to keep the fault mapping
        private java.util.HashMap faultExeptionNameMap = new java.util.HashMap();
        private java.util.HashMap faultExeptionClassNameMap = new java.util.HashMap();
        private java.util.HashMap faultMessageMap = new java.util.HashMap();

    
    private void populateAxisService() throws org.apache.axis2.AxisFault {

     //creating the Service with a unique name
     _service = new org.apache.axis2.description.AxisService("Ws" + this.hashCode());
     
    

        //creating the operations
        org.apache.axis2.description.AxisOperation __operation;
    


        _operations = new org.apache.axis2.description.AxisOperation[21];
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("", "getProjectVersionsByProjectId"));
	    _service.addOperation(__operation);
	    
	    
	    
            _operations[0]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("", "getEvaluatedProjects"));
	    _service.addOperation(__operation);
	    
	    
	    
            _operations[1]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("", "getMetrics"));
	    _service.addOperation(__operation);
	    
	    
	    
            _operations[2]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("", "getUserByName"));
	    _service.addOperation(__operation);
	    
	    
	    
            _operations[3]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("", "getFilesNumberByProjectVersionId"));
	    _service.addOperation(__operation);
	    
	    
	    
            _operations[4]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("", "createPendingUser"));
	    _service.addOperation(__operation);
	    
	    
	    
            _operations[5]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("", "getProjectIdByName"));
	    _service.addOperation(__operation);
	    
	    
	    
            _operations[6]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("", "deleteUserById"));
	    _service.addOperation(__operation);
	    
	    
	    
            _operations[7]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("", "modifyUser"));
	    _service.addOperation(__operation);
	    
	    
	    
            _operations[8]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("", "getStoredProjects"));
	    _service.addOperation(__operation);
	    
	    
	    
            _operations[9]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("", "getFilesNumberByProjectId"));
	    _service.addOperation(__operation);
	    
	    
	    
            _operations[10]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("", "getFilesByProjectId"));
	    _service.addOperation(__operation);
	    
	    
	    
            _operations[11]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("", "requestEvaluation4Project"));
	    _service.addOperation(__operation);
	    
	    
	    
            _operations[12]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("", "getMetricsByFileNames"));
	    _service.addOperation(__operation);
	    
	    
	    
            _operations[13]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("", "getMetricsResult"));
	    _service.addOperation(__operation);
	    
	    
	    
            _operations[14]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("", "getProjectById"));
	    _service.addOperation(__operation);
	    
	    
	    
            _operations[15]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("", "getFilesByProjectVersionId"));
	    _service.addOperation(__operation);
	    
	    
	    
            _operations[16]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("", "getUserById"));
	    _service.addOperation(__operation);
	    
	    
	    
            _operations[17]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("", "createUser"));
	    _service.addOperation(__operation);
	    
	    
	    
            _operations[18]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("", "getMetricsByProjectId"));
	    _service.addOperation(__operation);
	    
	    
	    
            _operations[19]=__operation;
            
        
                   __operation = new org.apache.axis2.description.OutInAxisOperation();
                

            __operation.setName(new javax.xml.namespace.QName("", "getUserMessageOfTheDay"));
	    _service.addOperation(__operation);
	    
	    
	    
            _operations[20]=__operation;
            
        
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
                    * @see eu.sqooss.ws.client.Ws#getProjectVersionsByProjectId
                        * @param param42
                    
                    */
                    public eu.sqooss.ws.client.ws.GetProjectVersionsByProjectIdResponse getProjectVersionsByProjectId(

                    eu.sqooss.ws.client.ws.GetProjectVersionsByProjectId param42)
                    throws java.rmi.RemoteException
                    
                    {
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[0].getName());
              _operationClient.getOptions().setAction("urn:getProjectVersionsByProjectId");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                    //Style is Doc.
                                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    param42,
                                                    optimizeContent(new javax.xml.namespace.QName("",
                                                    "getProjectVersionsByProjectId")));
                                                
        //adding SOAP headers
         _serviceClient.addHeadersToEnvelope(env);
        // create message context with that soap envelope
        org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext() ;
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
                                        eu.sqooss.ws.client.ws.GetProjectVersionsByProjectIdResponse.class,
                                         getEnvelopeNamespaces(_returnEnv));
                           _messageContext.getTransportOut().getSender().cleanup(_messageContext);
                           return (eu.sqooss.ws.client.ws.GetProjectVersionsByProjectIdResponse)object;
                    
         }catch(org.apache.axis2.AxisFault f){
            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt!=null){
                if (faultExeptionNameMap.containsKey(faultElt.getQName())){
                    //make the fault by reflection
                    try{
                        java.lang.String exceptionClassName = (java.lang.String)faultExeptionClassNameMap.get(faultElt.getQName());
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
                    * Auto generated method signature
                    * @see eu.sqooss.ws.client.Ws#getEvaluatedProjects
                        * @param param44
                    
                    */
                    public eu.sqooss.ws.client.ws.GetEvaluatedProjectsResponse getEvaluatedProjects(

                    eu.sqooss.ws.client.ws.GetEvaluatedProjects param44)
                    throws java.rmi.RemoteException
                    
                    {
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[1].getName());
              _operationClient.getOptions().setAction("urn:getEvaluatedProjects");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                    //Style is Doc.
                                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    param44,
                                                    optimizeContent(new javax.xml.namespace.QName("",
                                                    "getEvaluatedProjects")));
                                                
        //adding SOAP headers
         _serviceClient.addHeadersToEnvelope(env);
        // create message context with that soap envelope
        org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext() ;
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
                                        eu.sqooss.ws.client.ws.GetEvaluatedProjectsResponse.class,
                                         getEnvelopeNamespaces(_returnEnv));
                           _messageContext.getTransportOut().getSender().cleanup(_messageContext);
                           return (eu.sqooss.ws.client.ws.GetEvaluatedProjectsResponse)object;
                    
         }catch(org.apache.axis2.AxisFault f){
            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt!=null){
                if (faultExeptionNameMap.containsKey(faultElt.getQName())){
                    //make the fault by reflection
                    try{
                        java.lang.String exceptionClassName = (java.lang.String)faultExeptionClassNameMap.get(faultElt.getQName());
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
                    * Auto generated method signature
                    * @see eu.sqooss.ws.client.Ws#getMetrics
                        * @param param46
                    
                    */
                    public eu.sqooss.ws.client.ws.GetMetricsResponse getMetrics(

                    eu.sqooss.ws.client.ws.GetMetrics param46)
                    throws java.rmi.RemoteException
                    
                    {
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[2].getName());
              _operationClient.getOptions().setAction("urn:getMetrics");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                    //Style is Doc.
                                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    param46,
                                                    optimizeContent(new javax.xml.namespace.QName("",
                                                    "getMetrics")));
                                                
        //adding SOAP headers
         _serviceClient.addHeadersToEnvelope(env);
        // create message context with that soap envelope
        org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext() ;
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
                                        eu.sqooss.ws.client.ws.GetMetricsResponse.class,
                                         getEnvelopeNamespaces(_returnEnv));
                           _messageContext.getTransportOut().getSender().cleanup(_messageContext);
                           return (eu.sqooss.ws.client.ws.GetMetricsResponse)object;
                    
         }catch(org.apache.axis2.AxisFault f){
            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt!=null){
                if (faultExeptionNameMap.containsKey(faultElt.getQName())){
                    //make the fault by reflection
                    try{
                        java.lang.String exceptionClassName = (java.lang.String)faultExeptionClassNameMap.get(faultElt.getQName());
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
                    * Auto generated method signature
                    * @see eu.sqooss.ws.client.Ws#getUserByName
                        * @param param48
                    
                    */
                    public eu.sqooss.ws.client.ws.GetUserByNameResponse getUserByName(

                    eu.sqooss.ws.client.ws.GetUserByName param48)
                    throws java.rmi.RemoteException
                    
                    {
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[3].getName());
              _operationClient.getOptions().setAction("urn:getUserByName");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                    //Style is Doc.
                                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    param48,
                                                    optimizeContent(new javax.xml.namespace.QName("",
                                                    "getUserByName")));
                                                
        //adding SOAP headers
         _serviceClient.addHeadersToEnvelope(env);
        // create message context with that soap envelope
        org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext() ;
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
                                        eu.sqooss.ws.client.ws.GetUserByNameResponse.class,
                                         getEnvelopeNamespaces(_returnEnv));
                           _messageContext.getTransportOut().getSender().cleanup(_messageContext);
                           return (eu.sqooss.ws.client.ws.GetUserByNameResponse)object;
                    
         }catch(org.apache.axis2.AxisFault f){
            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt!=null){
                if (faultExeptionNameMap.containsKey(faultElt.getQName())){
                    //make the fault by reflection
                    try{
                        java.lang.String exceptionClassName = (java.lang.String)faultExeptionClassNameMap.get(faultElt.getQName());
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
                    * Auto generated method signature
                    * @see eu.sqooss.ws.client.Ws#getFilesNumberByProjectVersionId
                        * @param param50
                    
                    */
                    public eu.sqooss.ws.client.ws.GetFilesNumberByProjectVersionIdResponse getFilesNumberByProjectVersionId(

                    eu.sqooss.ws.client.ws.GetFilesNumberByProjectVersionId param50)
                    throws java.rmi.RemoteException
                    
                    {
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[4].getName());
              _operationClient.getOptions().setAction("urn:getFilesNumberByProjectVersionId");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                    //Style is Doc.
                                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    param50,
                                                    optimizeContent(new javax.xml.namespace.QName("",
                                                    "getFilesNumberByProjectVersionId")));
                                                
        //adding SOAP headers
         _serviceClient.addHeadersToEnvelope(env);
        // create message context with that soap envelope
        org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext() ;
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
                                        eu.sqooss.ws.client.ws.GetFilesNumberByProjectVersionIdResponse.class,
                                         getEnvelopeNamespaces(_returnEnv));
                           _messageContext.getTransportOut().getSender().cleanup(_messageContext);
                           return (eu.sqooss.ws.client.ws.GetFilesNumberByProjectVersionIdResponse)object;
                    
         }catch(org.apache.axis2.AxisFault f){
            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt!=null){
                if (faultExeptionNameMap.containsKey(faultElt.getQName())){
                    //make the fault by reflection
                    try{
                        java.lang.String exceptionClassName = (java.lang.String)faultExeptionClassNameMap.get(faultElt.getQName());
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
                    * Auto generated method signature
                    * @see eu.sqooss.ws.client.Ws#createPendingUser
                        * @param param52
                    
                    */
                    public eu.sqooss.ws.client.ws.CreatePendingUserResponse createPendingUser(

                    eu.sqooss.ws.client.ws.CreatePendingUser param52)
                    throws java.rmi.RemoteException
                    
                    {
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[5].getName());
              _operationClient.getOptions().setAction("urn:createPendingUser");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                    //Style is Doc.
                                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    param52,
                                                    optimizeContent(new javax.xml.namespace.QName("",
                                                    "createPendingUser")));
                                                
        //adding SOAP headers
         _serviceClient.addHeadersToEnvelope(env);
        // create message context with that soap envelope
        org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext() ;
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
                                        eu.sqooss.ws.client.ws.CreatePendingUserResponse.class,
                                         getEnvelopeNamespaces(_returnEnv));
                           _messageContext.getTransportOut().getSender().cleanup(_messageContext);
                           return (eu.sqooss.ws.client.ws.CreatePendingUserResponse)object;
                    
         }catch(org.apache.axis2.AxisFault f){
            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt!=null){
                if (faultExeptionNameMap.containsKey(faultElt.getQName())){
                    //make the fault by reflection
                    try{
                        java.lang.String exceptionClassName = (java.lang.String)faultExeptionClassNameMap.get(faultElt.getQName());
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
                    * Auto generated method signature
                    * @see eu.sqooss.ws.client.Ws#getProjectIdByName
                        * @param param54
                    
                    */
                    public eu.sqooss.ws.client.ws.GetProjectIdByNameResponse getProjectIdByName(

                    eu.sqooss.ws.client.ws.GetProjectIdByName param54)
                    throws java.rmi.RemoteException
                    
                    {
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[6].getName());
              _operationClient.getOptions().setAction("urn:getProjectIdByName");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                    //Style is Doc.
                                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    param54,
                                                    optimizeContent(new javax.xml.namespace.QName("",
                                                    "getProjectIdByName")));
                                                
        //adding SOAP headers
         _serviceClient.addHeadersToEnvelope(env);
        // create message context with that soap envelope
        org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext() ;
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
                                        eu.sqooss.ws.client.ws.GetProjectIdByNameResponse.class,
                                         getEnvelopeNamespaces(_returnEnv));
                           _messageContext.getTransportOut().getSender().cleanup(_messageContext);
                           return (eu.sqooss.ws.client.ws.GetProjectIdByNameResponse)object;
                    
         }catch(org.apache.axis2.AxisFault f){
            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt!=null){
                if (faultExeptionNameMap.containsKey(faultElt.getQName())){
                    //make the fault by reflection
                    try{
                        java.lang.String exceptionClassName = (java.lang.String)faultExeptionClassNameMap.get(faultElt.getQName());
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
                    * Auto generated method signature
                    * @see eu.sqooss.ws.client.Ws#deleteUserById
                        * @param param56
                    
                    */
                    public eu.sqooss.ws.client.ws.DeleteUserByIdResponse deleteUserById(

                    eu.sqooss.ws.client.ws.DeleteUserById param56)
                    throws java.rmi.RemoteException
                    
                    {
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[7].getName());
              _operationClient.getOptions().setAction("urn:deleteUserById");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                    //Style is Doc.
                                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    param56,
                                                    optimizeContent(new javax.xml.namespace.QName("",
                                                    "deleteUserById")));
                                                
        //adding SOAP headers
         _serviceClient.addHeadersToEnvelope(env);
        // create message context with that soap envelope
        org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext() ;
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
                                        eu.sqooss.ws.client.ws.DeleteUserByIdResponse.class,
                                         getEnvelopeNamespaces(_returnEnv));
                           _messageContext.getTransportOut().getSender().cleanup(_messageContext);
                           return (eu.sqooss.ws.client.ws.DeleteUserByIdResponse)object;
                    
         }catch(org.apache.axis2.AxisFault f){
            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt!=null){
                if (faultExeptionNameMap.containsKey(faultElt.getQName())){
                    //make the fault by reflection
                    try{
                        java.lang.String exceptionClassName = (java.lang.String)faultExeptionClassNameMap.get(faultElt.getQName());
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
                    * Auto generated method signature
                    * @see eu.sqooss.ws.client.Ws#modifyUser
                        * @param param58
                    
                    */
                    public eu.sqooss.ws.client.ws.ModifyUserResponse modifyUser(

                    eu.sqooss.ws.client.ws.ModifyUser param58)
                    throws java.rmi.RemoteException
                    
                    {
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[8].getName());
              _operationClient.getOptions().setAction("urn:modifyUser");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                    //Style is Doc.
                                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    param58,
                                                    optimizeContent(new javax.xml.namespace.QName("",
                                                    "modifyUser")));
                                                
        //adding SOAP headers
         _serviceClient.addHeadersToEnvelope(env);
        // create message context with that soap envelope
        org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext() ;
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
                                        eu.sqooss.ws.client.ws.ModifyUserResponse.class,
                                         getEnvelopeNamespaces(_returnEnv));
                           _messageContext.getTransportOut().getSender().cleanup(_messageContext);
                           return (eu.sqooss.ws.client.ws.ModifyUserResponse)object;
                    
         }catch(org.apache.axis2.AxisFault f){
            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt!=null){
                if (faultExeptionNameMap.containsKey(faultElt.getQName())){
                    //make the fault by reflection
                    try{
                        java.lang.String exceptionClassName = (java.lang.String)faultExeptionClassNameMap.get(faultElt.getQName());
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
                    * Auto generated method signature
                    * @see eu.sqooss.ws.client.Ws#getStoredProjects
                        * @param param60
                    
                    */
                    public eu.sqooss.ws.client.ws.GetStoredProjectsResponse getStoredProjects(

                    eu.sqooss.ws.client.ws.GetStoredProjects param60)
                    throws java.rmi.RemoteException
                    
                    {
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[9].getName());
              _operationClient.getOptions().setAction("urn:getStoredProjects");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                    //Style is Doc.
                                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    param60,
                                                    optimizeContent(new javax.xml.namespace.QName("",
                                                    "getStoredProjects")));
                                                
        //adding SOAP headers
         _serviceClient.addHeadersToEnvelope(env);
        // create message context with that soap envelope
        org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext() ;
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
                                        eu.sqooss.ws.client.ws.GetStoredProjectsResponse.class,
                                         getEnvelopeNamespaces(_returnEnv));
                           _messageContext.getTransportOut().getSender().cleanup(_messageContext);
                           return (eu.sqooss.ws.client.ws.GetStoredProjectsResponse)object;
                    
         }catch(org.apache.axis2.AxisFault f){
            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt!=null){
                if (faultExeptionNameMap.containsKey(faultElt.getQName())){
                    //make the fault by reflection
                    try{
                        java.lang.String exceptionClassName = (java.lang.String)faultExeptionClassNameMap.get(faultElt.getQName());
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
                    * Auto generated method signature
                    * @see eu.sqooss.ws.client.Ws#getFilesNumberByProjectId
                        * @param param62
                    
                    */
                    public eu.sqooss.ws.client.ws.GetFilesNumberByProjectIdResponse getFilesNumberByProjectId(

                    eu.sqooss.ws.client.ws.GetFilesNumberByProjectId param62)
                    throws java.rmi.RemoteException
                    
                    {
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[10].getName());
              _operationClient.getOptions().setAction("urn:getFilesNumberByProjectId");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                    //Style is Doc.
                                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    param62,
                                                    optimizeContent(new javax.xml.namespace.QName("",
                                                    "getFilesNumberByProjectId")));
                                                
        //adding SOAP headers
         _serviceClient.addHeadersToEnvelope(env);
        // create message context with that soap envelope
        org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext() ;
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
                                        eu.sqooss.ws.client.ws.GetFilesNumberByProjectIdResponse.class,
                                         getEnvelopeNamespaces(_returnEnv));
                           _messageContext.getTransportOut().getSender().cleanup(_messageContext);
                           return (eu.sqooss.ws.client.ws.GetFilesNumberByProjectIdResponse)object;
                    
         }catch(org.apache.axis2.AxisFault f){
            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt!=null){
                if (faultExeptionNameMap.containsKey(faultElt.getQName())){
                    //make the fault by reflection
                    try{
                        java.lang.String exceptionClassName = (java.lang.String)faultExeptionClassNameMap.get(faultElt.getQName());
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
                    * Auto generated method signature
                    * @see eu.sqooss.ws.client.Ws#getFilesByProjectId
                        * @param param64
                    
                    */
                    public eu.sqooss.ws.client.ws.GetFilesByProjectIdResponse getFilesByProjectId(

                    eu.sqooss.ws.client.ws.GetFilesByProjectId param64)
                    throws java.rmi.RemoteException
                    
                    {
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[11].getName());
              _operationClient.getOptions().setAction("urn:getFilesByProjectId");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                    //Style is Doc.
                                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    param64,
                                                    optimizeContent(new javax.xml.namespace.QName("",
                                                    "getFilesByProjectId")));
                                                
        //adding SOAP headers
         _serviceClient.addHeadersToEnvelope(env);
        // create message context with that soap envelope
        org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext() ;
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
                                        eu.sqooss.ws.client.ws.GetFilesByProjectIdResponse.class,
                                         getEnvelopeNamespaces(_returnEnv));
                           _messageContext.getTransportOut().getSender().cleanup(_messageContext);
                           return (eu.sqooss.ws.client.ws.GetFilesByProjectIdResponse)object;
                    
         }catch(org.apache.axis2.AxisFault f){
            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt!=null){
                if (faultExeptionNameMap.containsKey(faultElt.getQName())){
                    //make the fault by reflection
                    try{
                        java.lang.String exceptionClassName = (java.lang.String)faultExeptionClassNameMap.get(faultElt.getQName());
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
                    * Auto generated method signature
                    * @see eu.sqooss.ws.client.Ws#requestEvaluation4Project
                        * @param param66
                    
                    */
                    public eu.sqooss.ws.client.ws.RequestEvaluation4ProjectResponse requestEvaluation4Project(

                    eu.sqooss.ws.client.ws.RequestEvaluation4Project param66)
                    throws java.rmi.RemoteException
                    
                    {
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[12].getName());
              _operationClient.getOptions().setAction("urn:requestEvaluation4Project");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                    //Style is Doc.
                                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    param66,
                                                    optimizeContent(new javax.xml.namespace.QName("",
                                                    "requestEvaluation4Project")));
                                                
        //adding SOAP headers
         _serviceClient.addHeadersToEnvelope(env);
        // create message context with that soap envelope
        org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext() ;
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
                                        eu.sqooss.ws.client.ws.RequestEvaluation4ProjectResponse.class,
                                         getEnvelopeNamespaces(_returnEnv));
                           _messageContext.getTransportOut().getSender().cleanup(_messageContext);
                           return (eu.sqooss.ws.client.ws.RequestEvaluation4ProjectResponse)object;
                    
         }catch(org.apache.axis2.AxisFault f){
            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt!=null){
                if (faultExeptionNameMap.containsKey(faultElt.getQName())){
                    //make the fault by reflection
                    try{
                        java.lang.String exceptionClassName = (java.lang.String)faultExeptionClassNameMap.get(faultElt.getQName());
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
                    * Auto generated method signature
                    * @see eu.sqooss.ws.client.Ws#getMetricsByFileNames
                        * @param param68
                    
                    */
                    public eu.sqooss.ws.client.ws.GetMetricsByFileNamesResponse getMetricsByFileNames(

                    eu.sqooss.ws.client.ws.GetMetricsByFileNames param68)
                    throws java.rmi.RemoteException
                    
                    {
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[13].getName());
              _operationClient.getOptions().setAction("urn:getMetricsByFileNames");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                    //Style is Doc.
                                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    param68,
                                                    optimizeContent(new javax.xml.namespace.QName("",
                                                    "getMetricsByFileNames")));
                                                
        //adding SOAP headers
         _serviceClient.addHeadersToEnvelope(env);
        // create message context with that soap envelope
        org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext() ;
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
                                        eu.sqooss.ws.client.ws.GetMetricsByFileNamesResponse.class,
                                         getEnvelopeNamespaces(_returnEnv));
                           _messageContext.getTransportOut().getSender().cleanup(_messageContext);
                           return (eu.sqooss.ws.client.ws.GetMetricsByFileNamesResponse)object;
                    
         }catch(org.apache.axis2.AxisFault f){
            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt!=null){
                if (faultExeptionNameMap.containsKey(faultElt.getQName())){
                    //make the fault by reflection
                    try{
                        java.lang.String exceptionClassName = (java.lang.String)faultExeptionClassNameMap.get(faultElt.getQName());
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
                    * Auto generated method signature
                    * @see eu.sqooss.ws.client.Ws#getMetricsResult
                        * @param param70
                    
                    */
                    public eu.sqooss.ws.client.ws.GetMetricsResultResponse getMetricsResult(

                    eu.sqooss.ws.client.ws.GetMetricsResult param70)
                    throws java.rmi.RemoteException
                    
                    {
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[14].getName());
              _operationClient.getOptions().setAction("urn:getMetricsResult");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                    //Style is Doc.
                                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    param70,
                                                    optimizeContent(new javax.xml.namespace.QName("",
                                                    "getMetricsResult")));
                                                
        //adding SOAP headers
         _serviceClient.addHeadersToEnvelope(env);
        // create message context with that soap envelope
        org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext() ;
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
                                        eu.sqooss.ws.client.ws.GetMetricsResultResponse.class,
                                         getEnvelopeNamespaces(_returnEnv));
                           _messageContext.getTransportOut().getSender().cleanup(_messageContext);
                           return (eu.sqooss.ws.client.ws.GetMetricsResultResponse)object;
                    
         }catch(org.apache.axis2.AxisFault f){
            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt!=null){
                if (faultExeptionNameMap.containsKey(faultElt.getQName())){
                    //make the fault by reflection
                    try{
                        java.lang.String exceptionClassName = (java.lang.String)faultExeptionClassNameMap.get(faultElt.getQName());
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
                    * Auto generated method signature
                    * @see eu.sqooss.ws.client.Ws#getProjectById
                        * @param param72
                    
                    */
                    public eu.sqooss.ws.client.ws.GetProjectByIdResponse getProjectById(

                    eu.sqooss.ws.client.ws.GetProjectById param72)
                    throws java.rmi.RemoteException
                    
                    {
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[15].getName());
              _operationClient.getOptions().setAction("urn:getProjectById");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                    //Style is Doc.
                                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    param72,
                                                    optimizeContent(new javax.xml.namespace.QName("",
                                                    "getProjectById")));
                                                
        //adding SOAP headers
         _serviceClient.addHeadersToEnvelope(env);
        // create message context with that soap envelope
        org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext() ;
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
                                        eu.sqooss.ws.client.ws.GetProjectByIdResponse.class,
                                         getEnvelopeNamespaces(_returnEnv));
                           _messageContext.getTransportOut().getSender().cleanup(_messageContext);
                           return (eu.sqooss.ws.client.ws.GetProjectByIdResponse)object;
                    
         }catch(org.apache.axis2.AxisFault f){
            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt!=null){
                if (faultExeptionNameMap.containsKey(faultElt.getQName())){
                    //make the fault by reflection
                    try{
                        java.lang.String exceptionClassName = (java.lang.String)faultExeptionClassNameMap.get(faultElt.getQName());
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
                    * Auto generated method signature
                    * @see eu.sqooss.ws.client.Ws#getFilesByProjectVersionId
                        * @param param74
                    
                    */
                    public eu.sqooss.ws.client.ws.GetFilesByProjectVersionIdResponse getFilesByProjectVersionId(

                    eu.sqooss.ws.client.ws.GetFilesByProjectVersionId param74)
                    throws java.rmi.RemoteException
                    
                    {
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[16].getName());
              _operationClient.getOptions().setAction("urn:getFilesByProjectVersionId");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                    //Style is Doc.
                                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    param74,
                                                    optimizeContent(new javax.xml.namespace.QName("",
                                                    "getFilesByProjectVersionId")));
                                                
        //adding SOAP headers
         _serviceClient.addHeadersToEnvelope(env);
        // create message context with that soap envelope
        org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext() ;
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
                                        eu.sqooss.ws.client.ws.GetFilesByProjectVersionIdResponse.class,
                                         getEnvelopeNamespaces(_returnEnv));
                           _messageContext.getTransportOut().getSender().cleanup(_messageContext);
                           return (eu.sqooss.ws.client.ws.GetFilesByProjectVersionIdResponse)object;
                    
         }catch(org.apache.axis2.AxisFault f){
            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt!=null){
                if (faultExeptionNameMap.containsKey(faultElt.getQName())){
                    //make the fault by reflection
                    try{
                        java.lang.String exceptionClassName = (java.lang.String)faultExeptionClassNameMap.get(faultElt.getQName());
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
                    * Auto generated method signature
                    * @see eu.sqooss.ws.client.Ws#getUserById
                        * @param param76
                    
                    */
                    public eu.sqooss.ws.client.ws.GetUserByIdResponse getUserById(

                    eu.sqooss.ws.client.ws.GetUserById param76)
                    throws java.rmi.RemoteException
                    
                    {
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[17].getName());
              _operationClient.getOptions().setAction("urn:getUserById");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                    //Style is Doc.
                                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    param76,
                                                    optimizeContent(new javax.xml.namespace.QName("",
                                                    "getUserById")));
                                                
        //adding SOAP headers
         _serviceClient.addHeadersToEnvelope(env);
        // create message context with that soap envelope
        org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext() ;
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
                                        eu.sqooss.ws.client.ws.GetUserByIdResponse.class,
                                         getEnvelopeNamespaces(_returnEnv));
                           _messageContext.getTransportOut().getSender().cleanup(_messageContext);
                           return (eu.sqooss.ws.client.ws.GetUserByIdResponse)object;
                    
         }catch(org.apache.axis2.AxisFault f){
            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt!=null){
                if (faultExeptionNameMap.containsKey(faultElt.getQName())){
                    //make the fault by reflection
                    try{
                        java.lang.String exceptionClassName = (java.lang.String)faultExeptionClassNameMap.get(faultElt.getQName());
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
                    * Auto generated method signature
                    * @see eu.sqooss.ws.client.Ws#createUser
                        * @param param78
                    
                    */
                    public eu.sqooss.ws.client.ws.CreateUserResponse createUser(

                    eu.sqooss.ws.client.ws.CreateUser param78)
                    throws java.rmi.RemoteException
                    
                    {
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[18].getName());
              _operationClient.getOptions().setAction("urn:createUser");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                    //Style is Doc.
                                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    param78,
                                                    optimizeContent(new javax.xml.namespace.QName("",
                                                    "createUser")));
                                                
        //adding SOAP headers
         _serviceClient.addHeadersToEnvelope(env);
        // create message context with that soap envelope
        org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext() ;
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
                                        eu.sqooss.ws.client.ws.CreateUserResponse.class,
                                         getEnvelopeNamespaces(_returnEnv));
                           _messageContext.getTransportOut().getSender().cleanup(_messageContext);
                           return (eu.sqooss.ws.client.ws.CreateUserResponse)object;
                    
         }catch(org.apache.axis2.AxisFault f){
            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt!=null){
                if (faultExeptionNameMap.containsKey(faultElt.getQName())){
                    //make the fault by reflection
                    try{
                        java.lang.String exceptionClassName = (java.lang.String)faultExeptionClassNameMap.get(faultElt.getQName());
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
                    * Auto generated method signature
                    * @see eu.sqooss.ws.client.Ws#getMetricsByProjectId
                        * @param param80
                    
                    */
                    public eu.sqooss.ws.client.ws.GetMetricsByProjectIdResponse getMetricsByProjectId(

                    eu.sqooss.ws.client.ws.GetMetricsByProjectId param80)
                    throws java.rmi.RemoteException
                    
                    {
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[19].getName());
              _operationClient.getOptions().setAction("urn:getMetricsByProjectId");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                    //Style is Doc.
                                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    param80,
                                                    optimizeContent(new javax.xml.namespace.QName("",
                                                    "getMetricsByProjectId")));
                                                
        //adding SOAP headers
         _serviceClient.addHeadersToEnvelope(env);
        // create message context with that soap envelope
        org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext() ;
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
                                        eu.sqooss.ws.client.ws.GetMetricsByProjectIdResponse.class,
                                         getEnvelopeNamespaces(_returnEnv));
                           _messageContext.getTransportOut().getSender().cleanup(_messageContext);
                           return (eu.sqooss.ws.client.ws.GetMetricsByProjectIdResponse)object;
                    
         }catch(org.apache.axis2.AxisFault f){
            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt!=null){
                if (faultExeptionNameMap.containsKey(faultElt.getQName())){
                    //make the fault by reflection
                    try{
                        java.lang.String exceptionClassName = (java.lang.String)faultExeptionClassNameMap.get(faultElt.getQName());
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
                    * Auto generated method signature
                    * @see eu.sqooss.ws.client.Ws#getUserMessageOfTheDay
                        * @param param82
                    
                    */
                    public eu.sqooss.ws.client.ws.GetUserMessageOfTheDayResponse getUserMessageOfTheDay(

                    eu.sqooss.ws.client.ws.GetUserMessageOfTheDay param82)
                    throws java.rmi.RemoteException
                    
                    {
              try{
               org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[20].getName());
              _operationClient.getOptions().setAction("urn:getUserMessageOfTheDay");
              _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

              

              // create SOAP envelope with that payload
              org.apache.axiom.soap.SOAPEnvelope env = null;
                    
                                    //Style is Doc.
                                    
                                                    
                                                    env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                                                    param82,
                                                    optimizeContent(new javax.xml.namespace.QName("",
                                                    "getUserMessageOfTheDay")));
                                                
        //adding SOAP headers
         _serviceClient.addHeadersToEnvelope(env);
        // create message context with that soap envelope
        org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext() ;
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
                                        eu.sqooss.ws.client.ws.GetUserMessageOfTheDayResponse.class,
                                         getEnvelopeNamespaces(_returnEnv));
                           _messageContext.getTransportOut().getSender().cleanup(_messageContext);
                           return (eu.sqooss.ws.client.ws.GetUserMessageOfTheDayResponse)object;
                    
         }catch(org.apache.axis2.AxisFault f){
            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt!=null){
                if (faultExeptionNameMap.containsKey(faultElt.getQName())){
                    //make the fault by reflection
                    try{
                        java.lang.String exceptionClassName = (java.lang.String)faultExeptionClassNameMap.get(faultElt.getQName());
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
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.GetProjectVersionsByProjectId param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.GetProjectVersionsByProjectId.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.GetProjectVersionsByProjectIdResponse param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.GetProjectVersionsByProjectIdResponse.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.GetEvaluatedProjects param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.GetEvaluatedProjects.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.GetEvaluatedProjectsResponse param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.GetEvaluatedProjectsResponse.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.GetMetrics param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.GetMetrics.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.GetMetricsResponse param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.GetMetricsResponse.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.GetUserByName param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.GetUserByName.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.GetUserByNameResponse param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.GetUserByNameResponse.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.GetFilesNumberByProjectVersionId param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.GetFilesNumberByProjectVersionId.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.GetFilesNumberByProjectVersionIdResponse param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.GetFilesNumberByProjectVersionIdResponse.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.CreatePendingUser param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.CreatePendingUser.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.CreatePendingUserResponse param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.CreatePendingUserResponse.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.GetProjectIdByName param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.GetProjectIdByName.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.GetProjectIdByNameResponse param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.GetProjectIdByNameResponse.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.DeleteUserById param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.DeleteUserById.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.DeleteUserByIdResponse param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.DeleteUserByIdResponse.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.ModifyUser param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.ModifyUser.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.ModifyUserResponse param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.ModifyUserResponse.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.GetStoredProjects param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.GetStoredProjects.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.GetStoredProjectsResponse param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.GetStoredProjectsResponse.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.GetFilesNumberByProjectId param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.GetFilesNumberByProjectId.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.GetFilesNumberByProjectIdResponse param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.GetFilesNumberByProjectIdResponse.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.GetFilesByProjectId param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.GetFilesByProjectId.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.GetFilesByProjectIdResponse param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.GetFilesByProjectIdResponse.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.RequestEvaluation4Project param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.RequestEvaluation4Project.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.RequestEvaluation4ProjectResponse param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.RequestEvaluation4ProjectResponse.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.GetMetricsByFileNames param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.GetMetricsByFileNames.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.GetMetricsByFileNamesResponse param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.GetMetricsByFileNamesResponse.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.GetMetricsResult param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.GetMetricsResult.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.GetMetricsResultResponse param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.GetMetricsResultResponse.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.GetProjectById param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.GetProjectById.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.GetProjectByIdResponse param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.GetProjectByIdResponse.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.GetFilesByProjectVersionId param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.GetFilesByProjectVersionId.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.GetFilesByProjectVersionIdResponse param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.GetFilesByProjectVersionIdResponse.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.GetUserById param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.GetUserById.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.GetUserByIdResponse param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.GetUserByIdResponse.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.CreateUser param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.CreateUser.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.CreateUserResponse param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.CreateUserResponse.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.GetMetricsByProjectId param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.GetMetricsByProjectId.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.GetMetricsByProjectIdResponse param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.GetMetricsByProjectIdResponse.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.GetUserMessageOfTheDay param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.GetUserMessageOfTheDay.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.GetUserMessageOfTheDayResponse param, boolean optimizeContent){
            
                     return param.getOMElement(eu.sqooss.ws.client.ws.GetUserMessageOfTheDayResponse.MY_QNAME,
                                  org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                    

            }
        

                            
                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, eu.sqooss.ws.client.ws.GetProjectVersionsByProjectId param, boolean optimizeContent){
                        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                             
                                    emptyEnvelope.getBody().addChild(param.getOMElement(eu.sqooss.ws.client.ws.GetProjectVersionsByProjectId.MY_QNAME,factory));
                                
                         return emptyEnvelope;
                        }

                        

                            
                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, eu.sqooss.ws.client.ws.GetEvaluatedProjects param, boolean optimizeContent){
                        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                             
                                    emptyEnvelope.getBody().addChild(param.getOMElement(eu.sqooss.ws.client.ws.GetEvaluatedProjects.MY_QNAME,factory));
                                
                         return emptyEnvelope;
                        }

                        

                            
                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, eu.sqooss.ws.client.ws.GetMetrics param, boolean optimizeContent){
                        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                             
                                    emptyEnvelope.getBody().addChild(param.getOMElement(eu.sqooss.ws.client.ws.GetMetrics.MY_QNAME,factory));
                                
                         return emptyEnvelope;
                        }

                        

                            
                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, eu.sqooss.ws.client.ws.GetUserByName param, boolean optimizeContent){
                        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                             
                                    emptyEnvelope.getBody().addChild(param.getOMElement(eu.sqooss.ws.client.ws.GetUserByName.MY_QNAME,factory));
                                
                         return emptyEnvelope;
                        }

                        

                            
                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, eu.sqooss.ws.client.ws.GetFilesNumberByProjectVersionId param, boolean optimizeContent){
                        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                             
                                    emptyEnvelope.getBody().addChild(param.getOMElement(eu.sqooss.ws.client.ws.GetFilesNumberByProjectVersionId.MY_QNAME,factory));
                                
                         return emptyEnvelope;
                        }

                        

                            
                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, eu.sqooss.ws.client.ws.CreatePendingUser param, boolean optimizeContent){
                        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                             
                                    emptyEnvelope.getBody().addChild(param.getOMElement(eu.sqooss.ws.client.ws.CreatePendingUser.MY_QNAME,factory));
                                
                         return emptyEnvelope;
                        }

                        

                            
                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, eu.sqooss.ws.client.ws.GetProjectIdByName param, boolean optimizeContent){
                        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                             
                                    emptyEnvelope.getBody().addChild(param.getOMElement(eu.sqooss.ws.client.ws.GetProjectIdByName.MY_QNAME,factory));
                                
                         return emptyEnvelope;
                        }

                        

                            
                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, eu.sqooss.ws.client.ws.DeleteUserById param, boolean optimizeContent){
                        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                             
                                    emptyEnvelope.getBody().addChild(param.getOMElement(eu.sqooss.ws.client.ws.DeleteUserById.MY_QNAME,factory));
                                
                         return emptyEnvelope;
                        }

                        

                            
                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, eu.sqooss.ws.client.ws.ModifyUser param, boolean optimizeContent){
                        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                             
                                    emptyEnvelope.getBody().addChild(param.getOMElement(eu.sqooss.ws.client.ws.ModifyUser.MY_QNAME,factory));
                                
                         return emptyEnvelope;
                        }

                        

                            
                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, eu.sqooss.ws.client.ws.GetStoredProjects param, boolean optimizeContent){
                        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                             
                                    emptyEnvelope.getBody().addChild(param.getOMElement(eu.sqooss.ws.client.ws.GetStoredProjects.MY_QNAME,factory));
                                
                         return emptyEnvelope;
                        }

                        

                            
                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, eu.sqooss.ws.client.ws.GetFilesNumberByProjectId param, boolean optimizeContent){
                        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                             
                                    emptyEnvelope.getBody().addChild(param.getOMElement(eu.sqooss.ws.client.ws.GetFilesNumberByProjectId.MY_QNAME,factory));
                                
                         return emptyEnvelope;
                        }

                        

                            
                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, eu.sqooss.ws.client.ws.GetFilesByProjectId param, boolean optimizeContent){
                        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                             
                                    emptyEnvelope.getBody().addChild(param.getOMElement(eu.sqooss.ws.client.ws.GetFilesByProjectId.MY_QNAME,factory));
                                
                         return emptyEnvelope;
                        }

                        

                            
                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, eu.sqooss.ws.client.ws.RequestEvaluation4Project param, boolean optimizeContent){
                        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                             
                                    emptyEnvelope.getBody().addChild(param.getOMElement(eu.sqooss.ws.client.ws.RequestEvaluation4Project.MY_QNAME,factory));
                                
                         return emptyEnvelope;
                        }

                        

                            
                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, eu.sqooss.ws.client.ws.GetMetricsByFileNames param, boolean optimizeContent){
                        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                             
                                    emptyEnvelope.getBody().addChild(param.getOMElement(eu.sqooss.ws.client.ws.GetMetricsByFileNames.MY_QNAME,factory));
                                
                         return emptyEnvelope;
                        }

                        

                            
                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, eu.sqooss.ws.client.ws.GetMetricsResult param, boolean optimizeContent){
                        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                             
                                    emptyEnvelope.getBody().addChild(param.getOMElement(eu.sqooss.ws.client.ws.GetMetricsResult.MY_QNAME,factory));
                                
                         return emptyEnvelope;
                        }

                        

                            
                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, eu.sqooss.ws.client.ws.GetProjectById param, boolean optimizeContent){
                        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                             
                                    emptyEnvelope.getBody().addChild(param.getOMElement(eu.sqooss.ws.client.ws.GetProjectById.MY_QNAME,factory));
                                
                         return emptyEnvelope;
                        }

                        

                            
                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, eu.sqooss.ws.client.ws.GetFilesByProjectVersionId param, boolean optimizeContent){
                        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                             
                                    emptyEnvelope.getBody().addChild(param.getOMElement(eu.sqooss.ws.client.ws.GetFilesByProjectVersionId.MY_QNAME,factory));
                                
                         return emptyEnvelope;
                        }

                        

                            
                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, eu.sqooss.ws.client.ws.GetUserById param, boolean optimizeContent){
                        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                             
                                    emptyEnvelope.getBody().addChild(param.getOMElement(eu.sqooss.ws.client.ws.GetUserById.MY_QNAME,factory));
                                
                         return emptyEnvelope;
                        }

                        

                            
                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, eu.sqooss.ws.client.ws.CreateUser param, boolean optimizeContent){
                        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                             
                                    emptyEnvelope.getBody().addChild(param.getOMElement(eu.sqooss.ws.client.ws.CreateUser.MY_QNAME,factory));
                                
                         return emptyEnvelope;
                        }

                        

                            
                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, eu.sqooss.ws.client.ws.GetMetricsByProjectId param, boolean optimizeContent){
                        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                             
                                    emptyEnvelope.getBody().addChild(param.getOMElement(eu.sqooss.ws.client.ws.GetMetricsByProjectId.MY_QNAME,factory));
                                
                         return emptyEnvelope;
                        }

                        

                            
                        private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, eu.sqooss.ws.client.ws.GetUserMessageOfTheDay param, boolean optimizeContent){
                        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                             
                                    emptyEnvelope.getBody().addChild(param.getOMElement(eu.sqooss.ws.client.ws.GetUserMessageOfTheDay.MY_QNAME,factory));
                                
                         return emptyEnvelope;
                        }

                        


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
        
                if (eu.sqooss.ws.client.ws.GetProjectVersionsByProjectId.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.GetProjectVersionsByProjectId.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.GetProjectVersionsByProjectIdResponse.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.GetProjectVersionsByProjectIdResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.GetEvaluatedProjects.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.GetEvaluatedProjects.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.GetEvaluatedProjectsResponse.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.GetEvaluatedProjectsResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.GetMetrics.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.GetMetrics.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.GetMetricsResponse.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.GetMetricsResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.GetUserByName.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.GetUserByName.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.GetUserByNameResponse.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.GetUserByNameResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.GetFilesNumberByProjectVersionId.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.GetFilesNumberByProjectVersionId.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.GetFilesNumberByProjectVersionIdResponse.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.GetFilesNumberByProjectVersionIdResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.CreatePendingUser.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.CreatePendingUser.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.CreatePendingUserResponse.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.CreatePendingUserResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.GetProjectIdByName.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.GetProjectIdByName.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.GetProjectIdByNameResponse.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.GetProjectIdByNameResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.DeleteUserById.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.DeleteUserById.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.DeleteUserByIdResponse.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.DeleteUserByIdResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.ModifyUser.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.ModifyUser.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.ModifyUserResponse.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.ModifyUserResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.GetStoredProjects.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.GetStoredProjects.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.GetStoredProjectsResponse.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.GetStoredProjectsResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.GetFilesNumberByProjectId.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.GetFilesNumberByProjectId.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.GetFilesNumberByProjectIdResponse.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.GetFilesNumberByProjectIdResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.GetFilesByProjectId.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.GetFilesByProjectId.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.GetFilesByProjectIdResponse.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.GetFilesByProjectIdResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.RequestEvaluation4Project.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.RequestEvaluation4Project.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.RequestEvaluation4ProjectResponse.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.RequestEvaluation4ProjectResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.GetMetricsByFileNames.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.GetMetricsByFileNames.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.GetMetricsByFileNamesResponse.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.GetMetricsByFileNamesResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.GetMetricsResult.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.GetMetricsResult.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.GetMetricsResultResponse.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.GetMetricsResultResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.GetProjectById.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.GetProjectById.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.GetProjectByIdResponse.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.GetProjectByIdResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.GetFilesByProjectVersionId.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.GetFilesByProjectVersionId.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.GetFilesByProjectVersionIdResponse.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.GetFilesByProjectVersionIdResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.GetUserById.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.GetUserById.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.GetUserByIdResponse.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.GetUserByIdResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.CreateUser.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.CreateUser.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.CreateUserResponse.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.CreateUserResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.GetMetricsByProjectId.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.GetMetricsByProjectId.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.GetMetricsByProjectIdResponse.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.GetMetricsByProjectIdResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.GetUserMessageOfTheDay.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.GetUserMessageOfTheDay.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (eu.sqooss.ws.client.ws.GetUserMessageOfTheDayResponse.class.equals(type)){
                
                           return eu.sqooss.ws.client.ws.GetUserMessageOfTheDayResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
        } catch (Exception e) {
        throw new RuntimeException(e);
        }
           return null;
        }



    
            private void setOpNameArray(){
            opNameArray = new javax.xml.namespace.QName[] {
            
            };
           }
           
   }
   