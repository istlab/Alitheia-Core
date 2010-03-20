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

import javax.tools.Diagnostic.Kind;
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

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("eu.sqooss.service.abstractmetric.*")
public class PluginAnnotationProcessor extends AbstractProcessor {

	Set<String> declActivators = new HashSet<String>();
	
	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnvironment) {
		
		for (Element e : roundEnvironment.getRootElements()) {
			for (AnnotationMirror mirror : e.getAnnotationMirrors()) {
				String annotationType = mirror.getAnnotationType().toString();

				if (annotationType.equals(MetricDeclarations.class.getName())) {
					processMetricDeclarations(mirror);
				} else if (annotationType.equals(MetricDecl.class.getName())) {
					processingEnv.getMessager().printMessage(Kind.ERROR, 
							"The @MetricDecl annotation is only allowed " +
							"as a the context of @MetricDeclarations");
				}
			}
		}
		
		
		
		return true;
	}

	private void processMetricDeclarations(AnnotationMirror aMirror) {
		try {
			Map<? extends ExecutableElement, ? extends AnnotationValue> mirrorMap = aMirror.getElementValues();

			for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> mirrorEntry : mirrorMap.entrySet()) {
				final String mirrorKey = mirrorEntry.getKey().toString();

				if (mirrorKey.equals("metrics()")) {
					List<? extends AnnotationValue> anns = extractMetricDecl(mirrorEntry.getValue());
					for (AnnotationValue annVal : anns) {
						extractMetricDeclParams(annVal);
					}
				}
			}
		} catch (Exception ex) {
			processingEnv.getMessager().printMessage(Kind.ERROR,
					"processMetricDeclarations: " + ex.getMessage());
		}
	}

	private List<? extends AnnotationValue> extractMetricDecl(
			AnnotationValue aAnnotationValue) {
		return (List<? extends AnnotationValue>) aAnnotationValue.getValue();
	}
	
	private void extractMetricDeclParams(AnnotationValue aAnnotationValue) {

		AnnotationMirror am = (AnnotationMirror) aAnnotationValue;
		Map<? extends ExecutableElement, ? extends AnnotationValue> amMap = am
				.getElementValues();
		String mnemonic = "", descr = "", act = "";
		for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> amMirrorEntry : amMap.entrySet()) {
			String amMirrorKey = amMirrorEntry.getKey().toString();
			
			
			if (amMirrorKey.equals("mnemonic()")) {
				mnemonic = amMirrorEntry.getValue().toString();
				mnemonic = mnemonic.substring(1, mnemonic.length() - 1);
			} else if (amMirrorKey.equals("descr()")) {
				descr = amMirrorEntry.getValue().toString();
			} else if (amMirrorKey.equals("activator()")) {
				act = amMirrorEntry.getValue().toString();
			}
		}
		
		if (mnemonic.length() > 10)
			processingEnv.getMessager().printMessage(Kind.ERROR, 
					"Mnemonic " + mnemonic + " is too long. " +
					"A metric mnemonic can only be < 10 chars long");
		
		if (mnemonic.equals(""))
			processingEnv.getMessager().printMessage(Kind.ERROR, 
					"Metric mnemonic is an empty String");
		
		if (descr == null || descr.length() <= 0)
			processingEnv.getMessager().printMessage(Kind.WARNING, 
					"A valid description should be specified with metric " 
					+ mnemonic);
		
		declActivators.add(act);
	}
}
