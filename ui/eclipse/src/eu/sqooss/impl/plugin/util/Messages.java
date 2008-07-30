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

package eu.sqooss.impl.plugin.util;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    
    private static final String MESSAGE_BUNDLE_NAME = Messages.class.getName();

    static {
        NLS.initializeMessages(MESSAGE_BUNDLE_NAME, Messages.class);
    }
    
    private Messages() {
        //do not instantiate
    }
    
    //ProjectPropertyPage's messages
    public static String ProjectPropertyPage_Label_Caption;
    public static String ProjectPropertyPage_Link_Configuration;
    public static String ProjectPropertyPage_Link_Profile;
    public static String ProjectPropertyPage_Link_Quality;
    
    //EnabledPropertyPage
    public static String EnabledPropertyPage_Connection_Init_Dialog_Message;
    public static String EnabledPropertyPage_Message_Error_Invalid_Connection;
    public static String EnabledPropertyPage_Message_Error_Invalid_Project;
    
    //ConfigurationPropertyPage's messages
    public static String ConfigurationPropertyPage_Text_Server_Address_Default_Value;
    public static String ConfigurationPropertyPage_Text_Server_Port_Default_Value;
    public static String ConfigurationPropertyPage_Text_User_Name_Default_Value;
    public static String ConfigurationPropertyPage_Text_Password_Default_Value;
    public static String ConfigurationPropertyPage_Text_Project_Name_Default_Value;
    public static String ConfigurationPropertyPage_Label_Project_Name;
    public static String ConfigurationPropertyPage_Label_Project_Version;
    public static String ConfigurationPropertyPage_Combo_First_Project_Version;
    public static String ConfigurationPropertyPage_Combo_Last_Project_Version;
    public static String ConfigurationPropertyPage_Combo_Other_Project_Version;
    public static String ConfigurationPropertyPage_Button_Validate;
    public static String ConfigurationPropertyPage_MessageBox_Validate_Title;
    public static String ConfigurationPropertyPage_MessageBox_Validate_Pass;
    public static String ConfigurationPropertyPage_MessageBox_Validate_Fail;
    public static String ConfigurationPropertyPage_MessageBox_Save_Title;
    public static String ConfigurationPropertyPage_MessageBox_Save_Fail;
    public static String ConfigurationPropertyPage_TabItem_Account;
    public static String ConfigurationPropertyPage_TabItem_Project;
    public static String ConfigurationPropertyPage_Message_Error_IOException;
    public static String ConfigurationPropertyPage_Message_Error_Unknown;
    public static String ConfigurationPropertyPage_Button_Project_Specific_Settings;
    public static String ConfigurationPropertyPage_Link_Configuration_Preference_Page;
    
    //QualityPropertyPage's messages
    public static String QualityPropertyPage_Label_Entity_Path;
    public static String QualityPropertyPage_Label_Metric;
    public static String QualityPropertyPage_Button_Compare;
    public static String QualityPropertyPage_Message_Error_Missing_Entity;
    public static String QualityPropertyPage_Message_Error_Missing_Metrics;
    public static String QualityPropertyPage_Combo_Compare_Version_Interval;
    public static String QualityPropertyPage_Combo_Compare_Version_Prefix;
    public static String QualityPropertyPage_Combo_Compare_Version_Postfix;
    public static String QualityPropertyPage_Combo_Compare_Version_First;
    public static String QualityPropertyPage_Combo_Compare_Version_Last;
    public static String QualityPropertyPage_Combo_Compare_Version_Other;
    
    //ProfilePropertyPage's messages
    public static String ProfilePropertyPage_Label_Profile_Name;
    public static String ProfilePropertyPage_Label_Profile_Path;
    public static String ProfilePropertyPage_Label_Profile_Files_Filter;
    public static String ProfilePropertyPage_Label_Recalc_Freq;
    public static String ProfilePropertyPage_Label_Project_Ver;
    public static String ProfilePropertyPage_Button_Remove_Profile;
    public static String ProfilePropertyPage_Button_Update_Profile;
    public static String ProfilePropertyPage_Button_Path_Browse;
    
    //VisualizerFactory's messages
    public static String VisualizerFactory_Title_Version;
    public static String VisualizerFactory_Title_Result;
    
    //TableVisualizer's messages
    public static String TableVisualizer_Title_Metric;
    
    //Configuration messages
    public static String Configuration_Label_Server_Address;
    public static String Configuration_Label_Server_Port;
    public static String Configuration_Label_User_Name;
    public static String Configuration_Label_Password;
    public static String Configuration_Group_User;
    public static String Configuration_Group_Server;
}

//vi: ai nosi sw=4 ts=4 expandtab
