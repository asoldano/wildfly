/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2016, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.jaxrs.deployment;

import org.jboss.as.jaxrs.logging.JaxrsLogger;
import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.modules.Module;
import org.jboss.wsf.spi.deployment.AbstractExtensible;
import org.jboss.wsf.spi.deployment.Deployment;
import org.jboss.wsf.spi.deployment.Service;
import org.jboss.wsf.spi.metadata.JAXRSDeploymentMetadata;

/**
 * This deployer initializes JBossWS - JAXRS deployment meta data.
 *
 * @author <a href="mailto:alessio.soldano@jboss.com">Alessio Soldano</a>
 */
public final class ModelDeploymentProcessor implements DeploymentUnitProcessor {

	@Override
	public void deploy(DeploymentPhaseContext phaseContext)
			throws DeploymentUnitProcessingException {
		final DeploymentUnit unit = phaseContext.getDeploymentUnit();
		if (JaxrsDeploymentMarker.isJaxrsDeployment(unit)) {
			Deployment dep = newDeployment(unit);
			unit.putAttachment(JaxrsAttachments.JAXRS_DEPLOYMENT_KEY, dep);
			dep.addAttachment(JAXRSDeploymentMetadata.class, unit.getAttachment(JaxrsAttachments.JAXRS_DEPLOYMENT_DATA));
		}
	}

	@Override
	public void undeploy(DeploymentUnit context) {
		// TODO Auto-generated method stub
		
	}
	
	private JAXRSDeployment newDeployment(final DeploymentUnit unit) throws DeploymentUnitProcessingException {
        JaxrsLogger.JAXRS_LOGGER.tracef("Creating new unified JAXRS deployment model for %s", unit);
        final ClassLoader classLoader;
        final Module module = unit.getAttachment(Attachments.MODULE);
        if (module == null) {
            classLoader = unit.getAttachment(JaxrsAttachments.CLASSLOADER_KEY);
            if (classLoader == null) {
                throw new DeploymentUnitProcessingException("missing clasloader!"); //TODO i18n
            }
        } else {
            classLoader = module.getClassLoader();
        }

        final JAXRSDeployment dep = new JAXRSDeployment(unit.getName(), classLoader);

        return dep;
    }
	
	//TODO this will be removed later; we should likely be using the same Deployment that's used for jaxws
	private final class JAXRSDeployment extends AbstractExtensible implements Deployment {

		private String name;
		private ClassLoader classLoader;
		
		public JAXRSDeployment(String name, ClassLoader classLoader) {
			this.name = name;
			this.classLoader = classLoader;
		}
		
		@Override
		public String getSimpleName() {
			return name;
		}

		@Override
		public ClassLoader getClassLoader() {
			return classLoader;
		}

		@Override
		public Service getService() {
			return null;
		}
		
	}
}
