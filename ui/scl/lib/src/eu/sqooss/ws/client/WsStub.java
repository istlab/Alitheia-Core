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



        _operations = new org.apache.axis2.description.AxisOperation[10];

        __operation = new org.apache.axis2.description.OutInAxisOperation();


        __operation.setName(new javax.xml.namespace.QName("", "displayUser"));
        _service.addOperation(__operation);



        _operations[0]=__operation;


        __operation = new org.apache.axis2.description.OutInAxisOperation();


        __operation.setName(new javax.xml.namespace.QName("", "evaluatedProjectsList"));
        _service.addOperation(__operation);



        _operations[1]=__operation;


        __operation = new org.apache.axis2.description.OutInAxisOperation();


        __operation.setName(new javax.xml.namespace.QName("", "retrieveMetrics4SelectedProject"));
        _service.addOperation(__operation);



        _operations[2]=__operation;


        __operation = new org.apache.axis2.description.OutInAxisOperation();


        __operation.setName(new javax.xml.namespace.QName("", "submitUser"));
        _service.addOperation(__operation);



        _operations[3]=__operation;


        __operation = new org.apache.axis2.description.OutInAxisOperation();


        __operation.setName(new javax.xml.namespace.QName("", "modifyUser"));
        _service.addOperation(__operation);



        _operations[4]=__operation;


        __operation = new org.apache.axis2.description.OutInAxisOperation();


        __operation.setName(new javax.xml.namespace.QName("", "requestEvaluation4Project"));
        _service.addOperation(__operation);



        _operations[5]=__operation;


        __operation = new org.apache.axis2.description.OutInAxisOperation();


        __operation.setName(new javax.xml.namespace.QName("", "deleteUser"));
        _service.addOperation(__operation);



        _operations[6]=__operation;


        __operation = new org.apache.axis2.description.OutInAxisOperation();


        __operation.setName(new javax.xml.namespace.QName("", "retrieveFileList"));
        _service.addOperation(__operation);



        _operations[7]=__operation;


        __operation = new org.apache.axis2.description.OutInAxisOperation();


        __operation.setName(new javax.xml.namespace.QName("", "retrieveSelectedMetric"));
        _service.addOperation(__operation);



        _operations[8]=__operation;


        __operation = new org.apache.axis2.description.OutInAxisOperation();


        __operation.setName(new javax.xml.namespace.QName("", "retrieveMetrics4SelectedFiles"));
        _service.addOperation(__operation);



        _operations[9]=__operation;


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
     * @see eu.sqooss.ws.client.Ws#displayUser
     * @param param20

     */
    public eu.sqooss.ws.client.ws.DisplayUserResponse displayUser(

            eu.sqooss.ws.client.ws.DisplayUser param20)
    throws java.rmi.RemoteException

    {
        try{
            org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[0].getName());
            _operationClient.getOptions().setAction("urn:displayUser");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;

            //Style is Doc.


            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                    param20,
                    optimizeContent(new javax.xml.namespace.QName("",
                            "displayUser")));

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
                    eu.sqooss.ws.client.ws.DisplayUserResponse.class,
                    getEnvelopeNamespaces(_returnEnv));
            _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            return (eu.sqooss.ws.client.ws.DisplayUserResponse)object;

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
     * @see eu.sqooss.ws.client.Ws#evaluatedProjectsList
     * @param param22

     */
    public eu.sqooss.ws.client.ws.EvaluatedProjectsListResponse evaluatedProjectsList(

            eu.sqooss.ws.client.ws.EvaluatedProjectsList param22)
    throws java.rmi.RemoteException

    {
        try{
            org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[1].getName());
            _operationClient.getOptions().setAction("urn:evaluatedProjectsList");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;

            //Style is Doc.


            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                    param22,
                    optimizeContent(new javax.xml.namespace.QName("",
                            "evaluatedProjectsList")));

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
                    eu.sqooss.ws.client.ws.EvaluatedProjectsListResponse.class,
                    getEnvelopeNamespaces(_returnEnv));
            _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            return (eu.sqooss.ws.client.ws.EvaluatedProjectsListResponse)object;

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
     * @see eu.sqooss.ws.client.Ws#retrieveMetrics4SelectedProject
     * @param param24

     */
    public eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedProjectResponse retrieveMetrics4SelectedProject(

            eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedProject param24)
    throws java.rmi.RemoteException

    {
        try{
            org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[2].getName());
            _operationClient.getOptions().setAction("urn:retrieveMetrics4SelectedProject");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;

            //Style is Doc.


            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                    param24,
                    optimizeContent(new javax.xml.namespace.QName("",
                            "retrieveMetrics4SelectedProject")));

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
                    eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedProjectResponse.class,
                    getEnvelopeNamespaces(_returnEnv));
            _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            return (eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedProjectResponse)object;

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
     * @see eu.sqooss.ws.client.Ws#submitUser
     * @param param26

     */
    public eu.sqooss.ws.client.ws.SubmitUserResponse submitUser(

            eu.sqooss.ws.client.ws.SubmitUser param26)
    throws java.rmi.RemoteException

    {
        try{
            org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[3].getName());
            _operationClient.getOptions().setAction("urn:submitUser");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;

            //Style is Doc.


            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                    param26,
                    optimizeContent(new javax.xml.namespace.QName("",
                            "submitUser")));

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
                    eu.sqooss.ws.client.ws.SubmitUserResponse.class,
                    getEnvelopeNamespaces(_returnEnv));
            _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            return (eu.sqooss.ws.client.ws.SubmitUserResponse)object;

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
     * @param param28

     */
    public void modifyUser(

            eu.sqooss.ws.client.ws.ModifyUser param28)
    throws java.rmi.RemoteException

    {
        try{
            org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[4].getName());
            _operationClient.getOptions().setAction("urn:modifyUser");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;

            //Style is Doc.


            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                    param28,
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


            return;

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
     * @param param30

     */
    public eu.sqooss.ws.client.ws.RequestEvaluation4ProjectResponse requestEvaluation4Project(

            eu.sqooss.ws.client.ws.RequestEvaluation4Project param30)
    throws java.rmi.RemoteException

    {
        try{
            org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[5].getName());
            _operationClient.getOptions().setAction("urn:requestEvaluation4Project");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;

            //Style is Doc.


            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                    param30,
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
     * @see eu.sqooss.ws.client.Ws#deleteUser
     * @param param32

     */
    public void deleteUser(

            eu.sqooss.ws.client.ws.DeleteUser param32)
    throws java.rmi.RemoteException

    {
        try{
            org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[6].getName());
            _operationClient.getOptions().setAction("urn:deleteUser");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;

            //Style is Doc.


            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                    param32,
                    optimizeContent(new javax.xml.namespace.QName("",
                            "deleteUser")));

            //adding SOAP headers
            _serviceClient.addHeadersToEnvelope(env);
            // create message context with that soap envelope
            org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext() ;
            _messageContext.setEnvelope(env);

            // add the message contxt to the operation client
            _operationClient.addMessageContext(_messageContext);

            //execute the operation client
            _operationClient.execute(true);


            return;

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
     * @see eu.sqooss.ws.client.Ws#retrieveFileList
     * @param param34

     */
    public eu.sqooss.ws.client.ws.RetrieveFileListResponse retrieveFileList(

            eu.sqooss.ws.client.ws.RetrieveFileList param34)
    throws java.rmi.RemoteException

    {
        try{
            org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[7].getName());
            _operationClient.getOptions().setAction("urn:retrieveFileList");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;

            //Style is Doc.


            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                    param34,
                    optimizeContent(new javax.xml.namespace.QName("",
                            "retrieveFileList")));

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
                    eu.sqooss.ws.client.ws.RetrieveFileListResponse.class,
                    getEnvelopeNamespaces(_returnEnv));
            _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            return (eu.sqooss.ws.client.ws.RetrieveFileListResponse)object;

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
     * @see eu.sqooss.ws.client.Ws#retrieveSelectedMetric
     * @param param36

     */
    public eu.sqooss.ws.client.ws.RetrieveSelectedMetricResponse retrieveSelectedMetric(

            eu.sqooss.ws.client.ws.RetrieveSelectedMetric param36)
    throws java.rmi.RemoteException

    {
        try{
            org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[8].getName());
            _operationClient.getOptions().setAction("urn:retrieveSelectedMetric");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;

            //Style is Doc.


            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                    param36,
                    optimizeContent(new javax.xml.namespace.QName("",
                            "retrieveSelectedMetric")));

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
                    eu.sqooss.ws.client.ws.RetrieveSelectedMetricResponse.class,
                    getEnvelopeNamespaces(_returnEnv));
            _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            return (eu.sqooss.ws.client.ws.RetrieveSelectedMetricResponse)object;

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
     * @see eu.sqooss.ws.client.Ws#retrieveMetrics4SelectedFiles
     * @param param38

     */
    public eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedFilesResponse retrieveMetrics4SelectedFiles(

            eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedFiles param38)
    throws java.rmi.RemoteException

    {
        try{
            org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[9].getName());
            _operationClient.getOptions().setAction("urn:retrieveMetrics4SelectedFiles");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);



            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;

            //Style is Doc.


            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                    param38,
                    optimizeContent(new javax.xml.namespace.QName("",
                            "retrieveMetrics4SelectedFiles")));

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
                    eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedFilesResponse.class,
                    getEnvelopeNamespaces(_returnEnv));
            _messageContext.getTransportOut().getSender().cleanup(_messageContext);
            return (eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedFilesResponse)object;

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
    private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.DisplayUser param, boolean optimizeContent){

        return param.getOMElement(eu.sqooss.ws.client.ws.DisplayUser.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());


    }

    private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.DisplayUserResponse param, boolean optimizeContent){

        return param.getOMElement(eu.sqooss.ws.client.ws.DisplayUserResponse.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());


    }

    private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.EvaluatedProjectsList param, boolean optimizeContent){

        return param.getOMElement(eu.sqooss.ws.client.ws.EvaluatedProjectsList.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());


    }

    private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.EvaluatedProjectsListResponse param, boolean optimizeContent){

        return param.getOMElement(eu.sqooss.ws.client.ws.EvaluatedProjectsListResponse.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());


    }

    private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedProject param, boolean optimizeContent){

        return param.getOMElement(eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedProject.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());


    }

    private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedProjectResponse param, boolean optimizeContent){

        return param.getOMElement(eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedProjectResponse.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());


    }

    private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.SubmitUser param, boolean optimizeContent){

        return param.getOMElement(eu.sqooss.ws.client.ws.SubmitUser.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());


    }

    private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.SubmitUserResponse param, boolean optimizeContent){

        return param.getOMElement(eu.sqooss.ws.client.ws.SubmitUserResponse.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());


    }

    private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.ModifyUser param, boolean optimizeContent){

        return param.getOMElement(eu.sqooss.ws.client.ws.ModifyUser.MY_QNAME,
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

    private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.DeleteUser param, boolean optimizeContent){

        return param.getOMElement(eu.sqooss.ws.client.ws.DeleteUser.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());


    }

    private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.RetrieveFileList param, boolean optimizeContent){

        return param.getOMElement(eu.sqooss.ws.client.ws.RetrieveFileList.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());


    }

    private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.RetrieveFileListResponse param, boolean optimizeContent){

        return param.getOMElement(eu.sqooss.ws.client.ws.RetrieveFileListResponse.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());


    }

    private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.RetrieveSelectedMetric param, boolean optimizeContent){

        return param.getOMElement(eu.sqooss.ws.client.ws.RetrieveSelectedMetric.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());


    }

    private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.RetrieveSelectedMetricResponse param, boolean optimizeContent){

        return param.getOMElement(eu.sqooss.ws.client.ws.RetrieveSelectedMetricResponse.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());


    }

    private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedFiles param, boolean optimizeContent){

        return param.getOMElement(eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedFiles.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());


    }

    private  org.apache.axiom.om.OMElement  toOM(eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedFilesResponse param, boolean optimizeContent){

        return param.getOMElement(eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedFilesResponse.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());


    }



    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, eu.sqooss.ws.client.ws.DisplayUser param, boolean optimizeContent){
        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

        emptyEnvelope.getBody().addChild(param.getOMElement(eu.sqooss.ws.client.ws.DisplayUser.MY_QNAME,factory));

        return emptyEnvelope;
    }




    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, eu.sqooss.ws.client.ws.EvaluatedProjectsList param, boolean optimizeContent){
        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

        emptyEnvelope.getBody().addChild(param.getOMElement(eu.sqooss.ws.client.ws.EvaluatedProjectsList.MY_QNAME,factory));

        return emptyEnvelope;
    }




    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedProject param, boolean optimizeContent){
        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

        emptyEnvelope.getBody().addChild(param.getOMElement(eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedProject.MY_QNAME,factory));

        return emptyEnvelope;
    }




    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, eu.sqooss.ws.client.ws.SubmitUser param, boolean optimizeContent){
        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

        emptyEnvelope.getBody().addChild(param.getOMElement(eu.sqooss.ws.client.ws.SubmitUser.MY_QNAME,factory));

        return emptyEnvelope;
    }




    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, eu.sqooss.ws.client.ws.ModifyUser param, boolean optimizeContent){
        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

        emptyEnvelope.getBody().addChild(param.getOMElement(eu.sqooss.ws.client.ws.ModifyUser.MY_QNAME,factory));

        return emptyEnvelope;
    }




    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, eu.sqooss.ws.client.ws.RequestEvaluation4Project param, boolean optimizeContent){
        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

        emptyEnvelope.getBody().addChild(param.getOMElement(eu.sqooss.ws.client.ws.RequestEvaluation4Project.MY_QNAME,factory));

        return emptyEnvelope;
    }




    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, eu.sqooss.ws.client.ws.DeleteUser param, boolean optimizeContent){
        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

        emptyEnvelope.getBody().addChild(param.getOMElement(eu.sqooss.ws.client.ws.DeleteUser.MY_QNAME,factory));

        return emptyEnvelope;
    }




    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, eu.sqooss.ws.client.ws.RetrieveFileList param, boolean optimizeContent){
        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

        emptyEnvelope.getBody().addChild(param.getOMElement(eu.sqooss.ws.client.ws.RetrieveFileList.MY_QNAME,factory));

        return emptyEnvelope;
    }




    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, eu.sqooss.ws.client.ws.RetrieveSelectedMetric param, boolean optimizeContent){
        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

        emptyEnvelope.getBody().addChild(param.getOMElement(eu.sqooss.ws.client.ws.RetrieveSelectedMetric.MY_QNAME,factory));

        return emptyEnvelope;
    }




    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedFiles param, boolean optimizeContent){
        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

        emptyEnvelope.getBody().addChild(param.getOMElement(eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedFiles.MY_QNAME,factory));

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

            if (eu.sqooss.ws.client.ws.DisplayUser.class.equals(type)){

                return eu.sqooss.ws.client.ws.DisplayUser.Factory.parse(param.getXMLStreamReaderWithoutCaching());


            }

            if (eu.sqooss.ws.client.ws.DisplayUserResponse.class.equals(type)){

                return eu.sqooss.ws.client.ws.DisplayUserResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());


            }

            if (eu.sqooss.ws.client.ws.EvaluatedProjectsList.class.equals(type)){

                return eu.sqooss.ws.client.ws.EvaluatedProjectsList.Factory.parse(param.getXMLStreamReaderWithoutCaching());


            }

            if (eu.sqooss.ws.client.ws.EvaluatedProjectsListResponse.class.equals(type)){

                return eu.sqooss.ws.client.ws.EvaluatedProjectsListResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());


            }

            if (eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedProject.class.equals(type)){

                return eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedProject.Factory.parse(param.getXMLStreamReaderWithoutCaching());


            }

            if (eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedProjectResponse.class.equals(type)){

                return eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedProjectResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());


            }

            if (eu.sqooss.ws.client.ws.SubmitUser.class.equals(type)){

                return eu.sqooss.ws.client.ws.SubmitUser.Factory.parse(param.getXMLStreamReaderWithoutCaching());


            }

            if (eu.sqooss.ws.client.ws.SubmitUserResponse.class.equals(type)){

                return eu.sqooss.ws.client.ws.SubmitUserResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());


            }

            if (eu.sqooss.ws.client.ws.ModifyUser.class.equals(type)){

                return eu.sqooss.ws.client.ws.ModifyUser.Factory.parse(param.getXMLStreamReaderWithoutCaching());


            }

            if (eu.sqooss.ws.client.ws.RequestEvaluation4Project.class.equals(type)){

                return eu.sqooss.ws.client.ws.RequestEvaluation4Project.Factory.parse(param.getXMLStreamReaderWithoutCaching());


            }

            if (eu.sqooss.ws.client.ws.RequestEvaluation4ProjectResponse.class.equals(type)){

                return eu.sqooss.ws.client.ws.RequestEvaluation4ProjectResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());


            }

            if (eu.sqooss.ws.client.ws.DeleteUser.class.equals(type)){

                return eu.sqooss.ws.client.ws.DeleteUser.Factory.parse(param.getXMLStreamReaderWithoutCaching());


            }

            if (eu.sqooss.ws.client.ws.RetrieveFileList.class.equals(type)){

                return eu.sqooss.ws.client.ws.RetrieveFileList.Factory.parse(param.getXMLStreamReaderWithoutCaching());


            }

            if (eu.sqooss.ws.client.ws.RetrieveFileListResponse.class.equals(type)){

                return eu.sqooss.ws.client.ws.RetrieveFileListResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());


            }

            if (eu.sqooss.ws.client.ws.RetrieveSelectedMetric.class.equals(type)){

                return eu.sqooss.ws.client.ws.RetrieveSelectedMetric.Factory.parse(param.getXMLStreamReaderWithoutCaching());


            }

            if (eu.sqooss.ws.client.ws.RetrieveSelectedMetricResponse.class.equals(type)){

                return eu.sqooss.ws.client.ws.RetrieveSelectedMetricResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());


            }

            if (eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedFiles.class.equals(type)){

                return eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedFiles.Factory.parse(param.getXMLStreamReaderWithoutCaching());


            }

            if (eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedFilesResponse.class.equals(type)){

                return eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedFilesResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());


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

//vi: ai nosi sw=4 ts=4 expandtab
