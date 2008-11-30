/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - Organization for Free and Open Source Software,  
 *                  Athens, Greece.
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
package eu.sqooss.impl.metrics.clmt;

import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;

/**
 * Measurements calculated by CLMT plug-in
 * 
 * @author Vassilios Karakoidas (bkarak@aueb.gr)
 *
 */
public enum CLMTMeasurement {
    NOCH("NumberOfChildren", "NOCH", ProjectVersion.class, "Number of Chidren", MetricType.Type.SOURCE_CODE),
    DIT("DepthOfInheritanceTree", "DIT", ProjectFile.class, "Depth of Inheritance Tree", MetricType.Type.SOURCE_CODE),
    NMPV("NativeMethodsPerProject", "NMPV", ProjectVersion.class, "Number of Native Methods per Project Version", MetricType.Type.PROJECT_WIDE),
    NMPC("NativeMethodsPerCodeUnit", "NMPC", ProjectFile.class, "Number of Native Method per Code Unit", MetricType.Type.SOURCE_CODE),
    SCPV("StaticClassesPerProject", "SCPV", ProjectVersion.class, "Number of Static Classes per Project", MetricType.Type.PROJECT_WIDE),
    EFC("EfferentCouplings", "EFC", ProjectFile.class, "Efferent Couplings", MetricType.Type.SOURCE_FOLDER),
    AFC("AfferentCouplings", "AFC", ProjectFile.class, "Afferent Couplings", MetricType.Type.SOURCE_FOLDER),
    INST("Instability", "INST", ProjectFile.class, "Instability Metric", MetricType.Type.SOURCE_FOLDER),
    NUMMOD("ModuleCount", "NUMMOD", ProjectVersion.class, "Number of Modules/Namespaces", MetricType.Type.PROJECT_WIDE),
    AVGLMOD("AverageLOCperModule", "AVGLMOD", ProjectFile.class, "Average Lines of Code per Module", MetricType.Type.PROJECT_WIDE),
    AVGETCL("AverageMethodsPerClass", "AVGMETCL", ProjectFile.class, "Average Methods per Class", MetricType.Type.PROJECT_WIDE),
    NUMENUM("NumberOfEnumerations", "NUMENUM", ProjectVersion.class, "Number of Enumerations", MetricType.Type.PROJECT_WIDE),
    NUMIFACE("NumberOfInterfaces", "NUMIFACE", ProjectVersion.class, "Number of Interfaces", MetricType.Type.PROJECT_WIDE),
    NUMCL("NumberOfClasses", "NUMCL", ProjectVersion.class, "Number of Classes per Project", MetricType.Type.PROJECT_WIDE),
    WMC("WeigthedMethodsPerClass", "WMC", ProjectFile.class, "Weighted Methods per Class", MetricType.Type.SOURCE_CODE);
    
    private String mnemonic;
    private String clmtMeasurementName;
    private Class<? extends DAObject> clazz;
    private String description;
    private MetricType.Type type;    
    
    private CLMTMeasurement(String clmtName,
                            String mnemonic,
                            Class<? extends DAObject> clazz,
                            String description,
                            MetricType.Type type) {
        this.mnemonic = mnemonic;
        this.clmtMeasurementName = clmtName;
        this.clazz = clazz;
        this.description = description;
        this.type = type;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public String getClmtMeasurementName() {
        return clmtMeasurementName;
    }
    
    public Class<? extends DAObject> getClazz() {
        return clazz;
    }
    
    public String getDescription() {
        return description;
    }
    
    public MetricType.Type getType() {
        return type;
    }
}