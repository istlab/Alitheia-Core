/*
 * Copyright 2008 - Organization for Free and Open Source Software,  
 *                  Athens, Greece.
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

/*
** That copyright notice makes sense for code residing in the 
** main SQO-OSS repository. For the Skeleton plug-in only, the Copyright
** notice may be removed and replaced by a statement of your own
** with (compatible) license terms as you see fit; the Skeleton
** plug-in itself is insufficiently a creative work to be protected
** by Copyright.
*/

/* This is the package for this particular plug-in. Third-party
** applications will want a different package name, but it is
** *ESSENTIAL* that the package name contain the string '.metrics.'
** because this is how Alitheia Core discovers the metric plug-ins. 
*/
package foo.metrics.bar;

/*
 * These are standard OSGi imports which we need for an activator.
 */
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/*
 * The rest of the code is boilerplate; we use the
 * implementation of the Skeleton plug-in to instantiate
 * a service. 
 *
 * The Skeleton plug-in is simple because it has only a single
 * interface and we have put the implementation in the same package
 * as the activator. Some plug-ins are big and complicated and
 * will put interfaces in this package and the implementation
 * in impl.metrics; that's up to the implementor.
 */
public class SkeletonActivator implements BundleActivator {

    private ServiceRegistration registration;

    public void start(BundleContext bc) throws Exception {

        registration = bc.registerService(Skeleton.class.getName(),
                new Skeleton(bc), null);
    }

    public void stop(BundleContext context) throws Exception {
        registration.unregister();
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

