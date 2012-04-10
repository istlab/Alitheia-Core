/*
 * Copyright 2011 - Organization for Free and Open Source Software,
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

package gr.aueb.metrics.findbugsscala

import eu.sqooss.service.abstractmetric._
import org.osgi.framework.BundleContext
import eu.sqooss.service.db._

/**
 *
 * @author Georgios Gousios <gousiosg@gmail.com>
 */
@SchedulerHints(invocationOrder = InvocationOrder.NEWFIRST, activationOrder = Array(classOf[ProjectVersion]))
@MetricDeclarations(metrics = Array(
  new MetricDecl(mnemonic = "DCDP", activators = Array(classOf[ProjectFile]), descr = "Dm: Hardcoded constant database password"),
  new MetricDecl(mnemonic = "DEDP", activators = Array(classOf[ProjectFile]), descr = "Dm: Empty database password"),
  new MetricDecl(mnemonic = "HRPTC", activators = Array(classOf[ProjectFile]), descr = "HRS: HTTP cookie formed from untrusted input"),
  new MetricDecl(mnemonic = "HRPTHH", activators = Array(classOf[ProjectFile]), descr = "HRS: HTTP Response splitting vulnerability"),
  new MetricDecl(mnemonic = "SNSPTE", activators = Array(classOf[ProjectFile]), descr = "SQL: Nonconstant string passed to execute method on an SQL statement"),
  new MetricDecl(mnemonic = "SPSGFNS", activators = Array(classOf[ProjectFile]), descr = "SQL: A prepared statement is generated from a nonconstant String"),
  new MetricDecl(mnemonic = "XRPTJW", activators = Array(classOf[ProjectFile]), descr = "XSS: JSP reflected cross site scripting vulnerability"),
  new MetricDecl(mnemonic = "XRPTSE", activators = Array(classOf[ProjectFile]), descr = "XSS: Servlet reflected cross site scripting vulnerability in error page"),
  new MetricDecl(mnemonic = "XRPTSW", activators = Array(classOf[ProjectFile]), descr = "XSS: Servlet reflected cross site scripting vulnerability"),
  new MetricDecl(mnemonic = "DCCIDP", activators = Array(classOf[ProjectFile]), descr = "DP: Classloaders should only be created inside doPrivileged block"),
  new MetricDecl(mnemonic = "DDIDP", activators = Array(classOf[ProjectFile]), descr = "DP: Method invoked that should be only be invoked inside a doPrivileged block"),
  new MetricDecl(mnemonic = "EER", activators = Array(classOf[ProjectFile]), descr = "EI: May expose internal representation by returning reference to mutable object"),
  new MetricDecl(mnemonic = "EER2", activators = Array(classOf[ProjectFile]), descr = "EI2: May expose internal representation by incorporating reference to mutable object"),
  new MetricDecl(mnemonic = "FPSBP", activators = Array(classOf[ProjectFile]), descr = "FI: Finalizer should be protected, not public"),
  new MetricDecl(mnemonic = "EESR", activators = Array(classOf[ProjectFile]), descr = "MS: May expose internal static state by storing a mutable object into a static field"),
  new MetricDecl(mnemonic = "MCBF", activators = Array(classOf[ProjectFile]), descr = "MS: Field isn't final and can't be protected from malicious code"),
  new MetricDecl(mnemonic = "MER", activators = Array(classOf[ProjectFile]), descr = "MS: Public static method may expose internal representation by returning array"),
  new MetricDecl(mnemonic = "MFP", activators = Array(classOf[ProjectFile]), descr = "MS: Field should be both final and package protected"),
  new MetricDecl(mnemonic = "MMA", activators = Array(classOf[ProjectFile]), descr = "MS: Field is a mutable array"),
  new MetricDecl(mnemonic = "MMH", activators = Array(classOf[ProjectFile]), descr = "MS: Field is a mutable Hashtable"),
  new MetricDecl(mnemonic = "MOP", activators = Array(classOf[ProjectFile]), descr = "MS: Field should be moved out of an interface and made package protected"),
  new MetricDecl(mnemonic = "MP", activators = Array(classOf[ProjectFile]), descr = "MS: Field should be package protected"),
  new MetricDecl(mnemonic = "MSBF", activators = Array(classOf[ProjectFile]), descr = "MS: Field isn't final but should be"),
  new MetricDecl(mnemonic = "TDCDP", activators = Array(classOf[ProjectVersion]), descr = "Dm: Hardcoded constant database password (total)"),
  new MetricDecl(mnemonic = "TDEDP", activators = Array(classOf[ProjectVersion]), descr = "Dm: Empty database password (total)"),
  new MetricDecl(mnemonic = "THRPTC", activators = Array(classOf[ProjectVersion]), descr = "HRS: HTTP cookie formed from untrusted input (total)"),
  new MetricDecl(mnemonic = "THRPTHH", activators = Array(classOf[ProjectVersion]), descr = "HRS: HTTP Response splitting vulnerability (total)"),
  new MetricDecl(mnemonic = "TSNSPTE", activators = Array(classOf[ProjectVersion]), descr = "SQL: Nonconstant string passed to execute method on an SQL statement (total)"),
  new MetricDecl(mnemonic = "TSPSGFNS", activators = Array(classOf[ProjectVersion]), descr = "SQL: A prepared statement is generated from a nonconstant String (total)"),
  new MetricDecl(mnemonic = "TXRPTJW", activators = Array(classOf[ProjectVersion]), descr = "XSS: JSP reflected cross site scripting vulnerability (total)"),
  new MetricDecl(mnemonic = "TXRPTSE", activators = Array(classOf[ProjectVersion]), descr = "XSS: Servlet reflected cross site scripting vulnerability in error page (total)"),
  new MetricDecl(mnemonic = "TXRPTSW", activators = Array(classOf[ProjectVersion]), descr = "XSS: Servlet reflected cross site scripting vulnerability (total)"),
  new MetricDecl(mnemonic = "TDCCIDP", activators = Array(classOf[ProjectVersion]), descr = "DP: Classloaders should only be created inside doPrivileged block (total)"),
  new MetricDecl(mnemonic = "TDDIDP", activators = Array(classOf[ProjectVersion]), descr = "DP: Method invoked that should be only be invoked inside a doPrivileged block (total)"),
  new MetricDecl(mnemonic = "TEER", activators = Array(classOf[ProjectVersion]), descr = "EI: May expose internal representation by returning reference to mutable object (total)"),
  new MetricDecl(mnemonic = "TEER2", activators = Array(classOf[ProjectVersion]), descr = "EI2: May expose internal representation by incorporating reference to mutable object (total)"),
  new MetricDecl(mnemonic = "TFPSBP", activators = Array(classOf[ProjectVersion]), descr = "FI: Finalizer should be protected, not public (total)"),
  new MetricDecl(mnemonic = "TEESR", activators = Array(classOf[ProjectVersion]), descr = "MS: May expose internal static state by storing a mutable object into a static field (total)"),
  new MetricDecl(mnemonic = "TMCBF", activators = Array(classOf[ProjectVersion]), descr = "MS: Field isn't final and can't be protected from malicious code (total)"),
  new MetricDecl(mnemonic = "TMER", activators = Array(classOf[ProjectVersion]), descr = "MS: Public static method may expose internal representation by returning array (total)"),
  new MetricDecl(mnemonic = "TMFP", activators = Array(classOf[ProjectVersion]), descr = "MS: Field should be both final and package protected (total)"),
  new MetricDecl(mnemonic = "TMMA", activators = Array(classOf[ProjectVersion]), descr = "MS: Field is a mutable array (total)"),
  new MetricDecl(mnemonic = "TMMH", activators = Array(classOf[ProjectVersion]), descr = "MS: Field is a mutable Hashtable (total)"),
  new MetricDecl(mnemonic = "TMOP", activators = Array(classOf[ProjectVersion]), descr = "MS: Field should be moved out of an interface and made package protected (total)"),
  new MetricDecl(mnemonic = "TMP", activators = Array(classOf[ProjectVersion]), descr = "MS: Field should be package protected (total)"),
  new MetricDecl(mnemonic = "TMSBF", activators = Array(classOf[ProjectVersion]), descr = "MS: Field isn't final but should be (total)")
))
class FindBugsMetrics(bc: BundleContext) extends AbstractMetric(bc)  {

  def getResult(pf: ProjectFile, m: Metric) : java.util.List[Result] =
    getResult(pf, classOf[ProjectFileMeasurement], m, Result.ResultType.INTEGER);

  //Run per version only
  def run(pf: ProjectFile) = {}

  def getResult(pv: ProjectVersion, m: Metric) : java.util.List[Result] =
    return getResult(pv, classOf[ProjectVersionMeasurement], m, Result.ResultType.INTEGER);

  def run(pv: ProjectVersion) = {
    log.debug("Running for pv:%s ".format(pv.getRevisionId))
  }
}

object FindBugsMetrics {
  lazy val mvnPath =  {
    if (System.getProperty("mvn.path") != null)
      System.getProperty("mvn.path");
    else
      "mvn";
  }

  lazy val findbugsPath = {
    if (System.getProperty("findbugs.path") != null)
      System.getProperty("findbugs.path");
    else
      "findbugs";
  }
}