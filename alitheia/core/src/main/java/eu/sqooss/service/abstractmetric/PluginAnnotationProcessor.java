/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2010 - Organization for Free and Open Source Software,  
 *                 Athens, Greece.
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
package eu.sqooss.service.abstractmetric;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import eu.sqooss.service.db.DAObject;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("eu.sqooss.service.abstractmetric.*")
public class PluginAnnotationProcessor extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnvironment) {
		
		Set<Class<? extends DAObject>> declActivators = 
			new HashSet<Class<? extends DAObject>>();
		
		for (Element e : roundEnvironment.getRootElements()) {
			for (AnnotationMirror mirror : e.getAnnotationMirrors()) {
				String annotationType = mirror.getAnnotationType().toString();

				if (annotationType.equals(MetricDeclarations.class.getName())) {
					System.err.println("found annotation:" + annotationType);

					Map<? extends ExecutableElement, ? extends AnnotationValue> values = 
						mirror.getElementValues();

					for (ExecutableElement mirrorKey : values.keySet()) {
						AnnotationValue mirrorEntry = values.get(mirrorKey);
						List<? extends AnnotationValue> subAnnotations = 
							(List<? extends AnnotationValue>) mirrorEntry.getValue();
						for (AnnotationValue subAnnotation : subAnnotations) {
							System.err.println("found subannotation:" + subAnnotation);
							AnnotationMirror am = (AnnotationMirror) subAnnotation.getValue();
							for (ExecutableElement paramKey : am.getElementValues().keySet()) {
								//System.err.println("found param:" + paramKey.getAnnotation(annotationType));
							}
						}
					}
				}
			}
		}
		return true;
	}

	private void processMirror(AnnotationMirror mirror) {
		
	}
}
